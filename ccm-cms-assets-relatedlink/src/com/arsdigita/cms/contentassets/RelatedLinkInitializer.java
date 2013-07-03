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
import com.arsdigita.cms.contentassets.util.RelatedLinkGlobalizationUtil;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Initializes the RelatedLink content item asset at each system startup.
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/03/30 $
 * @version $Id: RelatedLinkInitializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class RelatedLinkInitializer extends ContentAssetInitializer {

    /**
     * Default constructor, sets its specific manifest file and delegates to 
     * super class.
     */
    public RelatedLinkInitializer() {
        super("ccm-cms-assets-relatedlink.pdl.mf");
    }
    
    /**
     * Constructor.
     */
    protected RelatedLinkInitializer(final String manifestFile) {
        super(manifestFile);
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
     * /WEB-INF/traversal-adapters/com/arsdigita/cms/contentassets/FileAttachments.xml
     */
    public String getTraversalXML() {
        return TRAVERSAL_ADAPTER_BASE_DIR + "RelatedLink.xml";
    }

    /**
     * The name of the association between the item
     * and the asset, eg 'fileAttachments'.
     */
    public String getProperty() {
        return "links";
    }

    /**
     * The class of the authoring kit step
     */
    public Class getAuthoringStep() {
        return RelatedLinkPropertiesStep.class;
    }

    /**
     * The label for the authoring step
     */
    public GlobalizedMessage getAuthoringStepLabel() {
        return RelatedLinkGlobalizationUtil
               .globalize("cms.contentassets.related_link.label");
    }

    /**
     * The description for the authoring step
     */
    public GlobalizedMessage getAuthoringStepDescription() {
        return RelatedLinkGlobalizationUtil
               .globalize("cms.contentassets.related_link.description");
    }

    /**
     * The sort key for the authoring step
     */
    public int getAuthoringStepSortKey() {
        return RelatedLinkConfig.getInstance().getAssetStepSortKey();
    }
}
