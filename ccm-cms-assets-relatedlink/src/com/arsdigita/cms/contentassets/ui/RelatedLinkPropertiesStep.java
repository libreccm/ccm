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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.contenttypes.ui.LinkPropertiesStep;
import com.arsdigita.cms.contenttypes.ui.LinkTable;

/**
 * Authoring step to create a RelatedLink and change ordering. 
 */
public class RelatedLinkPropertiesStep extends LinkPropertiesStep {
    
    /**
     * Constructor. Creates a <code>RelatedLinkPropertiesStep</code> given an
     * <code>ItemSelectionModel</code>  and an
     * <code>AuthoringKitWizard</code>.
     *
     * @param itemModel The <code>ItemSelectionModel</code> for the current page.
     * @param parent The <code>AuthoringKitWizard</code> to track the
     * current link
     */
    public RelatedLinkPropertiesStep( ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent ) {
        super(itemModel, parent);
    }

    /**
     * Sets a RelatedLinkSelectionModel as the 
     * LinkSelectionModel for this authoring step. 
     */
    protected void setLinkSelectionModel() {
        setLinkSelectionModel(
            new RelatedLinkSelectionModel(getLinkParam()));
    }

    /**
     * Returns a RelatedLinkTable as the 
     * display component for this authoring step.
     *
     * @return The display component to use for the authoring step
     */
    public Component getDisplayComponent() {
        SimpleContainer container = new SimpleContainer();
        LinkTable table;
        if (ContentSection.getConfig().isHideAdditionalResourceFields()) {
        	table = new LinkTable(getItemSelectionModel(), getLinkSelectionModel());
        	table.setModelBuilder(new RelatedLinkTableModelBuilder(getItemSelectionModel()));
        } else {
        	table = new RelatedLinkTable(getItemSelectionModel(), 
                    getLinkSelectionModel());
        }
      
        container.add(table);
        return container;
    }

    /**
     * Gets the edit form (a RelatedLinkPropertyForm)
     *
     * @return The edit form
     */
    protected FormSection getEditSheet() {
        return new RelatedLinkPropertyForm(getItemSelectionModel(), 
                                           getLinkSelectionModel());
    }
}

