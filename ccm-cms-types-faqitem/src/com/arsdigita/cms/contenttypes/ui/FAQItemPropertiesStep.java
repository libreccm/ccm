/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.cms.contenttypes.FAQItem;

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.cms.contenttypes.util.FAQGlobalizationUtil;
import com.arsdigita.cms.util.GlobalizationUtil;


/**
 * Authoring step displays the simple attributes of the FAQItem content 
 * type (and its subclasses) and provides a link to the edit form. 
 */
public class FAQItemPropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static final String EDIT_SHEET_NAME = "edit";

    /**
     * Constructor 
     * @param itemModel
     * @param parent 
     */
    public FAQItemPropertiesStep( ItemSelectionModel itemModel,
                                  AuthoringKitWizard parent ) {
        super( itemModel, parent );

        setDefaultEditKey(EDIT_SHEET_NAME);
        BasicPageForm editSheet;

        editSheet = new FAQItemPropertyForm( itemModel, this);
        add( EDIT_SHEET_NAME, "Edit", 
             new WorkflowLockedComponentAccess(editSheet, itemModel),
             editSheet.getSaveCancelSection().getCancelButton() );

        setDisplayComponent( getFAQDomainObjectPropertySheet( itemModel ) );
    }

    /**
     * Returns a component that displays the properties of the 
     * FAQItem specified by the ItemSelectionModel passed in.
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     */
    public static Component getFAQDomainObjectPropertySheet( ItemSelectionModel
                                                     itemModel ) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet( itemModel );

        sheet.add( GlobalizationUtil
                   .globalize("cms.contenttypes.ui.title"), 
                   FAQItem.TITLE );
        sheet.add( GlobalizationUtil
                   .globalize("cms.contenttypes.ui.name"), 
                   FAQItem.NAME );
        sheet.add( FAQGlobalizationUtil
                   .globalize("cms.contenttypes.ui.faq.question"), 
                   FAQItem.QUESTION );
        sheet.add( FAQGlobalizationUtil
                   .globalize("cms.contenttypes.ui.faq.answer"), 
                   FAQItem.ANSWER );
        sheet.add( FAQGlobalizationUtil
                   .globalize("cms.contenttypes.ui.faq.sectionName"), 
                   FAQItem.SECTION_NAME );

        return sheet;
    }
}





