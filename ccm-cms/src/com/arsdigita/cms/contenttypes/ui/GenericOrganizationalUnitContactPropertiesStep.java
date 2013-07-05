/*
 * Copyright (c) 2010 Jens Pelzetter
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

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Step for editing the associated contacts.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitContactPropertiesStep
        extends SimpleEditStep {

    public static final String ADD_CONTACT_SHEET_NAME = "addContact";
    private GenericContact selectedContact;
    private String selectedContactType;

    public GenericOrganizationalUnitContactPropertiesStep(
            ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public GenericOrganizationalUnitContactPropertiesStep(
            ItemSelectionModel itemModel, AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addContactSheet =
                      new GenericOrganizationalUnitContactAddForm(itemModel,
                                                                  this);
        add(ADD_CONTACT_SHEET_NAME,
            ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.add_contact"),
            new WorkflowLockedComponentAccess(addContactSheet, itemModel),
            addContactSheet.getSaveCancelSection().getCancelButton());

        GenericOrganizationalUnitContactTable contactsTable = new 
                GenericOrganizationalUnitContactTable(itemModel, this);
        setDisplayComponent(contactsTable);
    }

    public GenericContact getSelectedContact() {
        return selectedContact;
    }

    public void setSelectedContact(final GenericContact selectedContact) {
        this.selectedContact = selectedContact;
    }

    public String getSelectedContactType() {
        return selectedContactType;
    }

    public void setSelectedContactType(final String selectedContactType) {
        this.selectedContactType = selectedContactType;
    }
}
