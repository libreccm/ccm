/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.ui.RelatedLinkPropertiesStep;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;

/**
 *
 * @author jensp
 */
public class SciProjectPublicationsStep extends RelatedLinkPropertiesStep {
   
    public SciProjectPublicationsStep(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        super(itemModel, parent);
    }

    @Override
    protected void setLinkSelectionModel() {
        super.setLinkSelectionModel();
        linkListName = "SciProjectPublications";
        contentType = ContentType.findByAssociatedObjectType(
                "com.arsdigita.cms.contenttypes.Publication");
    }
    
    @Override
    protected FormSection getEditSheet() {
        return new SciProjectPublicationLinkPropertyForm(
                getItemSelectionModel(),
                                                            getLinkSelectionModel(),
                                                            linkListName,
                                                            contentType);
    }
}
