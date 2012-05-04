/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.coventry.cms.contenttypes.ui;


import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.ui.ItemPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.coventry.cms.contenttypes.Person;


/**
 * Authoring step to edit the simple attributes of the Person content type.
 **/
public class PersonPropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static final String EDIT_SHEET_NAME = "edit";

    public PersonPropertiesStep(ItemSelectionModel itemModel,
                               AuthoringKitWizard parent) {
        super(itemModel, parent);

        BasicPageForm editSheet = new PersonEditForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit",
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getPersonPropertySheet(itemModel));
        setDefaultEditKey( EDIT_SHEET_NAME );
    }

    /**
     * Returns a component that displays the properties of the Person specified
     * by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     **/
    public static Component getPersonPropertySheet(ItemSelectionModel
                                                  itemModel) {
        ItemPropertySheet sheet = new ItemPropertySheet(itemModel);

        sheet.add("Name: ", Person.TITLE);
        sheet.add("URL: ", Person.NAME);
        sheet.add("Description: ",  Person.DESCRIPTION);
        sheet.add("Body Text: ",  Person.TEXT_ASSET + "." + TextAsset.CONTENT);
        sheet.add("Contact Details: ", Person.CONTACT_DETAILS);
        return sheet;
    }
}
