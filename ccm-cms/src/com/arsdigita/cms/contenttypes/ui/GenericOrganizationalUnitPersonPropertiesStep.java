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

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Properties step for editing the persons related to an organization.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitPersonPropertiesStep
        extends SimpleEditStep
        implements GenericOrganizationalUnitPersonSelector {

    public static final String ADD_PERSON_SHEET_NAME = "addPerson";
    private GenericPerson selectedPerson;
    private String selectedPersonRole;
    private String selectedPersonStatus;

    public GenericOrganizationalUnitPersonPropertiesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public GenericOrganizationalUnitPersonPropertiesStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addPersonSheet =
                      new GenericOrganizationalUnitPersonAddForm(itemModel,
                                                                 this);
        add(ADD_PERSON_SHEET_NAME,
            (String) ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.add_person").localize(),
            new WorkflowLockedComponentAccess(addPersonSheet, itemModel),
            addPersonSheet.getSaveCancelSection().getCancelButton());

        GenericOrganizationalUnitPersonsTable personsTable = new GenericOrganizationalUnitPersonsTable(
                itemModel,
                this);
        setDisplayComponent(personsTable);
    }

    @Override
    public GenericPerson getSelectedPerson() {
        return selectedPerson;
    }

    @Override
    public void setSelectedPerson(GenericPerson selectedPerson) {
        this.selectedPerson = selectedPerson;
    }

    @Override
    public String getSelectedPersonRole() {
        return selectedPersonRole;
    }

    @Override
    public void setSelectedPersonRole(String selectedPersonRole) {
        this.selectedPersonRole = selectedPersonRole;
    }

    @Override
    public String getSelectedPersonStatus() {
        return selectedPersonStatus;
    }

    @Override
    public void setSelectedPersonStatus(String selectedPersonStatus) {
        this.selectedPersonStatus = selectedPersonStatus;
    }

    @Override
    public void showEditComponent(final PageState state) {
        this.showComponent(state, ADD_PERSON_SHEET_NAME);
    }
}
