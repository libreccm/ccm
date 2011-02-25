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
import com.arsdigita.cms.contenttypes.SciMember;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Step for adding and removing member form a {@link SciOrganization}.
 *
 * @author Jens Pelzetter
 * @see SciOrganizationMemberAddForm
 * @see SciMember
 * @see SciOrganization
 */
public class SciOrganizationMemberStep
        extends SimpleEditStep
        implements GenericOrganizationalUnitPersonSelector {

    public static final String ADD_MEMBER_SHEET_NAME = "addMember";
    private GenericPerson selectedPerson;
    private String selectedPersonRole;
    private String selectedPersonStatus;

    public SciOrganizationMemberStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciOrganizationMemberStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent,
                                     String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addMemberSheet =
                      new SciOrganizationMemberAddForm(itemModel, this);
        add(ADD_MEMBER_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.orgnization.add_member").localize(),
            new WorkflowLockedComponentAccess(addMemberSheet, itemModel),
            addMemberSheet.getSaveCancelSection().getCancelButton());

        SciOrganizationMemberTable memberTable = new SciOrganizationMemberTable(
                itemModel, this);
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

    public void showEditComponent(final PageState state) {
        showComponent(state, ADD_MEMBER_SHEET_NAME);
    }
}
