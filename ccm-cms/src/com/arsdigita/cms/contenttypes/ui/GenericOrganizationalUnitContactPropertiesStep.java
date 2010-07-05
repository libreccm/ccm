/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitContactPropertiesStep
        extends SimpleEditStep {

    private static String ADD_CONTACT_SHEET_NAME = "addContact";

    public GenericOrganizationalUnitContactPropertiesStep(
            ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public GenericOrganizationalUnitContactPropertiesStep(
            ItemSelectionModel itemModel, AuthoringKitWizard parent,
                                                          String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addContactSheet = 
                new GenericOrganizationalUnitContactAddForm(
                itemModel);
        add(ADD_CONTACT_SHEET_NAME,
            (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.add_contact").localize(),
            new WorkflowLockedComponentAccess(addContactSheet, itemModel),
            addContactSheet.getSaveCancelSection().getCancelButton());

        GenericOrganizationalUnitContactTable contactsTable = new GenericOrganizationalUnitContactTable(
                itemModel);
        setDisplayComponent(contactsTable);
    }
}
