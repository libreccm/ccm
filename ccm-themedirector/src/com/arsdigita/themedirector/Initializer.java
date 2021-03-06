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

package com.arsdigita.themedirector;

import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.db.DbHelper;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.subsite.Subsite;
import com.arsdigita.subsite.SubsiteConfig;
import com.arsdigita.themedirector.ui.ThemeXSLParameterGenerator;
import com.arsdigita.themedirector.util.ThemeDevelopmentFileManager;
import com.arsdigita.themedirector.util.ThemePublishedFileManager;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.runtime.CCMResourceManager;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.ContextCloseEvent;
import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.templating.PatternStylesheetResolver;
import com.arsdigita.ui.admin.ApplicationManagers;

import org.apache.log4j.Logger;


/**
 * The Theme Directory initializer.
 *
 * @author Randy Graebner &lt;randyg@redhat.com&gt;
 * @version $Id: Initializer.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class Initializer extends CompoundInitializer {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int the runtime environment
     *  and set com.arsdigita.themedirector.Initializer=DEBUG 
     *  by uncommenting or adding the line.                                   */
    private static final Logger s_log = Logger.getLogger(Initializer.class);

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-themedirector.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    @Override
    public void init(DomainInitEvent evt) {

        PatternStylesheetResolver.registerPatternGenerator(
            "theme",
            new ThemePatternGenerator()
        );

        PatternStylesheetResolver.registerPatternGenerator(
            "themedir",
            new ThemeDirectoryPatternGenerator()
        );

        PageTransformer.registerXSLParameterGenerator(
            "theme-prefix", 
            new ThemeXSLParameterGenerator()
        );

        // here we add instantiators for our DomainObjects that do
        // not extend ACSObject
        DomainObjectInstantiator instantiator =
            new DomainObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new ThemeFile(dataObject);
                }
            };
        evt.getFactory().registerInstantiator(ThemeFile.BASE_DATA_OBJECT_TYPE,
                                              instantiator);

        evt.getFactory().registerInstantiator(
            ThemeDirector.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new ThemeDirector(dataObject);
                }
            });
        
        //Register the ApplicationManager implementation for this application
        ApplicationManagers.register(new ThemeDirectorAppManager());
    }

    @Override
    public void init(ContextInitEvent evt) {
        // This sets up the subsite for so that the form will include
        // all of the themes already in the database
        ThemeCollection collection = ThemeCollection.getAllThemes();
        collection.addNotEqualsFilter(Theme.LAST_PUBLISHED_USER, null);
        SubsiteConfig config = Subsite.getConfig();
        while (collection.next()) {
            config.addTheme(collection.getURL(), collection.getTitle());
        }
        String baseDir=CCMResourceManager.getBaseDirectory().getPath();
        s_log.info("ThemeDirector's application context directory: " + baseDir);
 
        // start thread for monitoring queue
        int devStartupDelay = ThemeDirector.getConfig()
            .getThemeDevFileWatchStartupDelay().intValue();
        int devPollDelay = ThemeDirector.getConfig()
            .getThemeDevFileWatchPollDelay().intValue();
        int pubStartupDelay = ThemeDirector.getConfig()
            .getThemePubFileWatchStartupDelay().intValue();
        int pubPollDelay = ThemeDirector.getConfig()
            .getThemePubFileWatchPollDelay().intValue();
        ThemePublishedFileManager.startWatchingFiles
            (pubStartupDelay, pubPollDelay, baseDir);
        ThemeDevelopmentFileManager.startWatchingFiles
            (devStartupDelay, devPollDelay, baseDir);
    }

    /**
     * 
     */
    @Override
    public void close(ContextCloseEvent evt) {
        ThemePublishedFileManager.getInstance().stopWatchingFiles();
        ThemeDevelopmentFileManager.getInstance().stopWatchingFiles();
    }
}
