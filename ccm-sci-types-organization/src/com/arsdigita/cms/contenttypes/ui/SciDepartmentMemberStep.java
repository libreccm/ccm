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

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Edit step for adding members to a SciDepartment.
 *
 * @author Jens Pelzetter
 */
public class SciDepartmentMemberStep
        extends SimpleEditStep
        implements GenericOrganizationalUnitPersonSelector {

    private static final String ADD_MEMBER_SHEET_NAME = "addMember";
    private GenericPerson selectedPerson;
    private String selectedPersonRole;
    private String selectedPersonStatus;

    public SciDepartmentMemberStep(ItemSelectionModel itemModel,
                                   AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentMemberStep(ItemSelectionModel itemModel,
                                   AuthoringKitWizard parent,
                                   String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addMemberSheet =
                      new SciDepartmentMemberAddForm(itemModel,
                      this);
        add(ADD_MEMBER_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.add_member").localize(),
            new WorkflowLockedComponentAccess(addMemberSheet, itemModel),
            addMemberSheet.getSaveCancelSection().getCancelButton());

        SciDepartmentMemberTable memberTable = new SciDepartmentMemberTable(
                itemModel,
                this);
        setDisplayComponent(memberTable);
    }

    public GenericPerson getSelectedPerson() {
        return selectedPerson;
    }

    public void setSelectedPerson(final GenericPerson selectedPerson) {
 this.selectedPerson = selectedPerson;
    }

    public String getSelectedPersonRole() {
       return selectedPersonRole;
    }

    public void setSelectedPersonRole(final String selectedPersonRole) {
       this.selectedPersonRole = selectedPersonRole;
    }

    public String getSelectedPersonStatus() {
        return selectedPersonStatus;
    }

    public void setSelectedPersonStatus(final String selectedPersonStatus) {
        this.selectedPersonStatus = selectedPersonStatus;
    }

    public void showEditComponent(PageState state) {
        showComponent(state, ADD_MEMBER_SHEET_NAME);
    }
}
