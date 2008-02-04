/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
 *
 */
package com.arsdigita.cms.contenttypes;

import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.LegacyInitEvent;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.xml.XML;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import org.apache.log4j.Logger;


public abstract class ContentAssetInitializer extends CompoundInitializer {
    private static Logger s_log = Logger.getLogger(ContentAssetInitializer.class);

    protected ContentAssetInitializer(final String manifestFile) {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             (manifestFile,
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }

    public void init(LegacyInitEvent evt) {
        super.init(evt);

        final String traversal = getTraversalXML();
        XML.parseResource
            (traversal,
             new ContentAssetTraversalHandler(getProperty()));
        
        AuthoringKitWizard.registerAssetStep(
            getBaseType(),
            getAuthoringStep(),
            getAuthoringStepLabel(),
            getAuthoringStepDescription(),
            getAuthoringStepSortKey()
        );
    }
    
    /**
     * The base type against which the asset is defined,
     * typically com.arsdigita.cms.ContentPage
     */
    public abstract String getBaseType();

    
    /**
     * Returns the path to the XML file defintions for the
     * asset, eg /WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/FileAttachments.xml
     */
    public abstract String getTraversalXML();
    /**
     * The name of the association between the item
     * and the asset, eg 'fileAttachments'.
     */
    public abstract String getProperty();

    
    /**
     * The class of the authoring kit step
     */
    public abstract Class getAuthoringStep();

    /**
     * The label for the authoring step
     */
    public abstract GlobalizedMessage getAuthoringStepLabel();
    /**
     * The description for the authoring step
     */
    public abstract GlobalizedMessage getAuthoringStepDescription();
    /**
     * The sort key for the authoring step
     */
    public abstract int getAuthoringStepSortKey();
}
