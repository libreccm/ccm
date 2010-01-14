/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.arsdigita.london.theme;

import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.london.subsite.Subsite;
import com.arsdigita.london.subsite.SubsiteConfig;
import com.arsdigita.london.theme.ui.ThemeXSLParameterGenerator;
import com.arsdigita.london.theme.util.ThemeDevelopmentFileManager;
import com.arsdigita.london.theme.util.ThemePublishedFileManager;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.ContextCloseEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.LegacyInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.templating.PatternStylesheetResolver;
import org.apache.log4j.Logger;

/**
 * The Theme Directory initializer.
 *
 * @author Randy Graebner &lt;randyg@redhat.com&gt;
 * @version $Id: Initializer.java 758 2005-09-02 14:26:56Z sskracic $
 */
public class Initializer extends CompoundInitializer {

    private static Logger s_log =
            Logger.getLogger(Initializer.class);

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-ldn-theme.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    public void init(DomainInitEvent evt) {
        PatternStylesheetResolver.registerPatternGenerator(
            "theme",
            new ThemePatternGenerator()
        );

        PatternStylesheetResolver.registerPatternGenerator(
            "themedir",
            new ThemeDirectoryPatternGenerator()
        );

        PageTransformer.registerXSLParameterGenerator
            ("theme-prefix", new ThemeXSLParameterGenerator());

        // here we add instantiators for our DomainObjects that do
        // not extend ACSObject
        DomainObjectInstantiator instantiator =
            new DomainObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new ThemeFile(dataObject);
                }
            };
        evt.getFactory().registerInstantiator(ThemeFile.BASE_DATA_OBJECT_TYPE,
                                                 instantiator);

        evt.getFactory().registerInstantiator(
            ThemeApplication.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new ThemeApplication(dataObject);
                }
            });
    }

    public void init(LegacyInitEvent evt) {
        // This sets up the subsite for so that the form will include
        // all of the themes already in the database
        ThemeCollection collection = ThemeCollection.getAllThemes();
        collection.addNotEqualsFilter(Theme.LAST_PUBLISHED_USER, null);
        SubsiteConfig config = Subsite.getConfig();
        while (collection.next()) {
            config.addTheme(collection.getURL(), collection.getTitle());
        }

        // start thread for monitoring queue
        int devStartupDelay = ThemeApplication.getConfig()
            .getThemeDevFileWatchStartupDelay().intValue();
        int devPollDelay = ThemeApplication.getConfig()
            .getThemeDevFileWatchPollDelay().intValue();
        int pubStartupDelay = ThemeApplication.getConfig()
            .getThemePubFileWatchStartupDelay().intValue();
        int pubPollDelay = ThemeApplication.getConfig()
            .getThemePubFileWatchPollDelay().intValue();
        ThemePublishedFileManager.startWatchingFiles
            (pubStartupDelay, pubPollDelay, null);
        ThemeDevelopmentFileManager.startWatchingFiles
            (devStartupDelay, devPollDelay, null);
    }

    /**
     * 
     */
    public void close(ContextCloseEvent evt) {
        ThemePublishedFileManager.getInstance().stopWatchingFiles();
        ThemeDevelopmentFileManager.getInstance().stopWatchingFiles();
    }
}
