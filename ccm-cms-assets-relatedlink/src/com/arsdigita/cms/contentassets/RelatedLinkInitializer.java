/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.contenttypes.ContentAssetInitializer;
import com.arsdigita.cms.contentassets.ui.RelatedLinkPropertiesStep;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Initializer
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/03/30 $
 * @version $Id: RelatedLinkInitializer.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public class RelatedLinkInitializer extends ContentAssetInitializer {

    /**
     * 
     */
    public RelatedLinkInitializer() {
        super("ccm-cms-assets-relatedlink.pdl.mf");
    }

    /**
     * 
     * @return
     */
    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/" + 
            "cms/contentassets/RelatedLink.xml";
    }

    /**
     * 
     * @return
     */
    public String getProperty() {
        return "links";
    }

    public String getBaseType() {
        return ContentPage.BASE_DATA_OBJECT_TYPE;
    }

    public Class getAuthoringStep() {
        return RelatedLinkPropertiesStep.class;
    }

    public GlobalizedMessage getAuthoringStepLabel() {
        return new GlobalizedMessage("com.arsdigita.cms.contentassets.related_link_label",
                                     "com.arsdigita.cms.contentassets.RelatedLinkResources");
    }

    public GlobalizedMessage getAuthoringStepDescription() {
        return new GlobalizedMessage("com.arsdigita.cms.contentassets.related_link_description",
                                     "com.arsdigita.cms.contentassets.RelatedLinkResources");
    }

    public int getAuthoringStepSortKey() {
        return 1; // XXX config param please
    }
}
