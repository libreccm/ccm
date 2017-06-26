/*
 * Copyright (c) 2013 Jens Pelzetter
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

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Authoring step for editing the memberships of the SciDepartment.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciDepartmentMembersStep
    extends SimpleEditStep
    implements GenericOrganizationalUnitPersonSelector {

    private static final String ADD_DEPARTMENT_MEMBER_SHEET_NAME
                                = "SciDepartmentAddMember";
    private static final String SELECTED_PERSON = "selected-person";
    private final ItemSelectionModel selectedPerson;
    private String selectedPersonRole;
    private String selectedPersonStatus;

    public SciDepartmentMembersStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentMembersStep(final ItemSelectionModel itemModel,
                                    final AuthoringKitWizard parent,
                                    final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addMemberSheet = new SciDepartmentMemberAddForm(
            itemModel, this);
        add(ADD_DEPARTMENT_MEMBER_SHEET_NAME,
            SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.members.add"),
            new WorkflowLockedComponentAccess(addMemberSheet, itemModel),
            addMemberSheet.getSaveCancelSection().getCancelButton());

        final SciDepartmentMemberTable memberTable
                                       = new SciDepartmentMemberTable(
                itemModel, this);
        setDisplayComponent(memberTable);

        selectedPerson = new ItemSelectionModel(SELECTED_PERSON);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(selectedPerson.getStateParameter());
    }

    @Override
    public GenericPerson getSelectedPerson(final PageState state) {
        return (GenericPerson) selectedPerson.getSelectedItem(state);
    }

    @Override
    public void setSelectedPerson(final PageState state,
                                  final GenericPerson selectedPerson) {
        this.selectedPerson.setSelectedObject(state, selectedPerson);
    }

    @Override
    public String getSelectedPersonRole(final PageState state) {
        return selectedPersonRole;
    }

    @Override
    public void setSelectedPersonRole(final PageState state,
                                      final String selectedPersonRole) {
        this.selectedPersonRole = selectedPersonRole;
    }

    @Override
    public String getSelectedPersonStatus(final PageState state) {
        return selectedPersonStatus;
    }

    @Override
    public void setSelectedPersonStatus(final PageState state,
                                        final String selectedPersonStatus) {
        this.selectedPersonStatus = selectedPersonStatus;
    }

    @Override
    public void showEditComponent(final PageState state) {
        showComponent(state, ADD_DEPARTMENT_MEMBER_SHEET_NAME);
    }

}
