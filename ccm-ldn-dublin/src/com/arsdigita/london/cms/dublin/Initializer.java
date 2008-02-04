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

package com.arsdigita.london.cms.dublin;

import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.contenttypes.ContentAssetInitializer;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.london.cms.dublin.ui.DublinCoreEdit;

/**
 * Initializes the dublin core content asset
 */
public class Initializer extends ContentAssetInitializer {

    public Initializer() {
        super("ccm-ldn-dublin.pdl.mf");
    }

    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/" + 
            "cms/contentassets/DublinCore.xml";
    }

    public String getProperty() {
        return "dublinCore";
    }

    public String getBaseType() {
        return ContentPage.BASE_DATA_OBJECT_TYPE;
    }

    public Class getAuthoringStep() {
        return DublinCoreEdit.class;
    }

    public GlobalizedMessage getAuthoringStepLabel() {
        return new GlobalizedMessage(
            "com.arsdigita.london.cms.dublin.dublin_core_label",
            "com.arsdigita.london.cms.dublin.DublinCoreResources");
    }

    public GlobalizedMessage getAuthoringStepDescription() {
        return new GlobalizedMessage(
            "com.arsdigita.london.cms.dublin.dublin_core_description",
            "com.arsdigita.london.cms.dublin.DublinCoreResources");
    }

    public int getAuthoringStepSortKey() {
        return 3; // XXX config param please
    }
}
