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

import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.AuthoringKitStepAssociation;
import com.arsdigita.cms.AuthoringStep;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.basetypes.Article;
import com.arsdigita.cms.contenttypes.ContentAssetInitializer;
// import com.arsdigita.cms.contentassets.ItemImageAttachment;
import com.arsdigita.cms.contentassets.ui.ImageStep;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.runtime.DomainInitEvent;

/**
 * @version $Id: ItemImageAttachmentInitializer.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ItemImageAttachmentInitializer extends ContentAssetInitializer {

    public ItemImageAttachmentInitializer() {
        super("ccm-cms-assets-imagestep.pdl.mf");
    }

    @Override
    public void init( DomainInitEvent ev ) {

        super.init(ev);

        DomainObjectFactory.registerInstantiator(
            ItemImageAttachment.BASE_DATA_OBJECT_TYPE,
            new DomainObjectInstantiator() {
                protected DomainObject doNewInstance( DataObject obj ) {
                    return new ItemImageAttachment( obj );
                }

                @Override
                public DomainObjectInstantiator resolveInstantiator( DataObject obj ) {
                    return this;
                }
            }
        );

        removeDeprecatedImageSteps();
    }

    /**
     * 
     */
    private void removeDeprecatedImageSteps() {
        DataCollection steps = SessionManager.getSession().retrieve
            ( AuthoringStep.BASE_DATA_OBJECT_TYPE );

        // Don't use defined constant to reduce dependency on Article
        steps.addEqualsFilter( "component",
                               "com.arsdigita.cms.ui.authoring.ArticleImage" );

        while( steps.next() ) {
            DataObject step = steps.getDataObject();

            DataCollection kits = SessionManager.getSession().retrieve
                ( AuthoringKitStepAssociation.BASE_DATA_OBJECT_TYPE );
            kits.addEqualsFilter( "stepId", step.get( "id" ) );

            while( kits.next() ) {
                DataObject kitStep = kits.getDataObject();
                AuthoringKitStepAssociation kitStepAsso = new AuthoringKitStepAssociation(kitStep);
                // Check whether the content type is (persistence-wise) subtype
                // of com.ad.cms.Article.  This is lame, but I couldn't find a better API to do this:
                AuthoringKit kit = new AuthoringKit (kitStepAsso.getKitID());
                try {
                    ObjectType.verifySubtype(Article.BASE_DATA_OBJECT_TYPE,
                                             kit.getContentType().getAssociatedObjectType());
                    kitStepAsso.delete();
                } catch (PersistenceException pe) {
                    // Do nothing, the content type is not subtyping com.arsdigita.cms.Article
                }
            }

            // DomainObjectFactory.newInstance( step ).delete();
        }
    }


    public String getTraversalXML() {
        return "/WEB-INF/traversal-adapters/com/arsdigita/" +
            "cms/contentassets/ItemImageAttachment.xml";
    }

    public String getProperty() {
        return "imageAttachments";
    }

    public String getBaseType() {
        return ContentPage.BASE_DATA_OBJECT_TYPE;
    }

    public Class getAuthoringStep() {
        return ImageStep.class;
    }

    public GlobalizedMessage getAuthoringStepLabel() {
        return new GlobalizedMessage("com.arsdigita.cms.contentassets.image_step_label",
                                     "com.arsdigita.cms.contentassets.ImageStepResources");
    }

    public GlobalizedMessage getAuthoringStepDescription() {
        return new GlobalizedMessage("com.arsdigita.cms.contentassets.image_step_description",
                                     "com.arsdigita.cms.contentassets.ImageStepResources");
    }

    public int getAuthoringStepSortKey() {
        return 1; // XXX config param please
    }
}
