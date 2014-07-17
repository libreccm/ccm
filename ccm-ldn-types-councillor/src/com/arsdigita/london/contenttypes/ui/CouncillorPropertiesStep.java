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

package com.arsdigita.london.contenttypes.ui;


import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.cms.ui.ItemPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.london.contenttypes.Councillor;

/**
 * Authoring step to edit the simple attributes of the Councillor content type (and
 * its subclasses). The attributes edited are 'name', 'title', 'councillor date',
 * 'location', 'lead', 'main contributor', 'councillor type', 'map link', and
 * 'cost'. This authoring step replaces the
 * <code>com.arsdigita.ui.authoring.PageEdit</code> step for this type.
 **/
public class CouncillorPropertiesStep extends SimpleEditStep {

    /** The name of the editing sheet added to this step */
    public static final String EDIT_SHEET_NAME = "edit";

    public CouncillorPropertiesStep(ItemSelectionModel itemModel,
                               AuthoringKitWizard parent) {
        super(itemModel, parent);

        BasicPageForm editSheet = new CouncillorEditForm(itemModel, this);
        add(EDIT_SHEET_NAME, "Edit",
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getCouncillorPropertySheet(itemModel));
        setDefaultEditKey( EDIT_SHEET_NAME );
    }

    /**
     * Returns a component that displays the properties of the Councillor specified
     * by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     * @pre itemModel != null
     * @return A component to display the state of the basic properties
     *  of the release
     **/
    public static Component getCouncillorPropertySheet(ItemSelectionModel
                                                  itemModel) {
        ItemPropertySheet sheet = new ItemPropertySheet(itemModel);

        sheet.add("Name: ", Councillor.TITLE);
        sheet.add("URL: ", Councillor.NAME);
        sheet.add("Description: ",  Councillor.DESCRIPTION);
        sheet.add("Body Text: ",  Councillor.TEXT_ASSET + "." + TextAsset.CONTENT);
        sheet.add("Contact Details: ", Councillor.CONTACT_DETAILS);
        sheet.add("Surgery Details: ",  Councillor.SURGERY_DETAILS);
        sheet.add("Position: ",  Councillor.POSITION);
        sheet.add("Political Party: ",  Councillor.POLITICAL_PARTY);
        sheet.add("Ward: ",  Councillor.WARD);
        sheet.add("Area of Responsibility: ",  Councillor.AREA_OF_RESPONSIBILITY);
        sheet.add("Term of Office: ",  Councillor.TERM_OF_OFFICE);
        sheet.add("Surgery Details: ",  Councillor.SURGERY_DETAILS);
        return sheet;
    }
}
