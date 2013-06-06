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

package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.contentassets.ui.DublinCoreEdit;
import com.arsdigita.cms.contentassets.util.DublinCoreGlobalizationUtil;
import com.arsdigita.cms.contenttypes.ContentAssetInitializer;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Initializes the dublin core content asset.
 * 
 * The class just implements all abstract methods of the super class 
 */
public class DublinCoreInitializer extends ContentAssetInitializer {

    /**
     * Constructor, sets its specific manifest file and delegates to super class.
     */
    public DublinCoreInitializer() {
        super("ccm-cms-assets-dublincore.pdl.mf");
    }

    /**
     * The base type against which the asset is defined,
     * typically com.arsdigita.cms.ContentPage
     */
    public String getBaseType() {
        return ContentPage.BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Returns the path to the XML file defintions for the asset, eg:
     * /WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/DublinCore.xml
     */
    public String getTraversalXML() {
        return TRAVERSAL_ADAPTER_BASE_DIR + "DublinCore.xml";
    }

    /**
     * The name of the association between the item
     * and the asset, eg 'fileAttachments'.
     */
    public String getProperty() {
        return "dublinCore";
    }

    /**
     * The class of the authoring kit step
     */
    public Class getAuthoringStep() {
        return DublinCoreEdit.class;
    }

    /**
     * The label for the authoring step
     */
    public GlobalizedMessage getAuthoringStepLabel() {
        return DublinCoreGlobalizationUtil.AuthoringStepLabel();
    }

    /**
     * The description for the authoring step
     */
    public GlobalizedMessage getAuthoringStepDescription() {
        return DublinCoreGlobalizationUtil.AuthoringStepDescription();
    }

    /**
     * The sort key for the authoring step
     */
    public int getAuthoringStepSortKey() {
        return DublinCoreInstance.instanceOf().getAssetStepSortKey();
    }
}
