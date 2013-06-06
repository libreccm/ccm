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
import com.arsdigita.cms.contenttypes.ContentAssetInitializer;
import com.arsdigita.cms.contentassets.ui.ImageStep;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.runtime.DomainInitEvent;

/**
 * Initializes the imagestep content item asset.
 * 
 * @version $Id: ItemImageAttachmentInitializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ItemImageAttachmentInitializer extends ContentAssetInitializer {

    /**
     * Constructor, sets its specific manifest file and delegates to super class.
     */
    public ItemImageAttachmentInitializer() {
        super("ccm-cms-assets-imagestep.pdl.mf");
    }

    /**
     * 
     * @param ev 
     */
    @Override
    public void init(DomainInitEvent ev) {

        super.init(ev);

        DomainObjectFactory.registerInstantiator(
                ItemImageAttachment.BASE_DATA_OBJECT_TYPE,
                new DomainObjectInstantiator() {
            protected DomainObject doNewInstance(DataObject obj) {
                return new ItemImageAttachment(obj);
            }

            @Override
            public DomainObjectInstantiator resolveInstantiator(DataObject obj) {
                return this;
            }

        });

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
        return "/WEB-INF/traversal-adapters/com/arsdigita/" + "cms/contentassets/ItemImageAttachment.xml";
    }

    /**
     * The name of the association between the item
     * and the asset, eg 'fileAttachments'.
     */
    public String getProperty() {
        return "imageAttachments";
    }

    /**
     * The class of the authoring kit step
     */
    public Class getAuthoringStep() {
        return ImageStep.class;
    }

    /**
     * The label for the authoring step
     */
    public GlobalizedMessage getAuthoringStepLabel() {
        return new GlobalizedMessage("com.arsdigita.cms.contentassets.image_step_label",
                                     "com.arsdigita.cms.contentassets.ImageStepResources");
    }

    /**
     * The description for the authoring step
     */
    public GlobalizedMessage getAuthoringStepDescription() {
        return new GlobalizedMessage("com.arsdigita.cms.contentassets.image_step_description",
                                     "com.arsdigita.cms.contentassets.ImageStepResources");
    }

    /**
     * The sort key for the authoring step
     */
    public int getAuthoringStepSortKey() {
        return ItemImageAttachmentConfig.instanceOf().getImageStepSortKey();
    }

}
