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

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * Step for setting the superior organization of a SciDepartment.
 *
 * @author Jens Pelzetter
 * @see SciDepartment
 * @see SciOrganization
 */
public class SciDepartmentOrganizationStep extends SimpleEditStep {

    private String SET_DEPARTMENT_ORGANIZATION_STEP =
                   "setDepartmentOrganization";

    public SciDepartmentOrganizationStep(ItemSelectionModel itemModel,
                                         AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentOrganizationStep(ItemSelectionModel itemModel,
                                         AuthoringKitWizard parent,
                                         String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm setOrgaForm =
                      new SciDepartmentOrganizationForm(itemModel);
        add(SET_DEPARTMENT_ORGANIZATION_STEP,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.setOrganization").localize(),
            new WorkflowLockedComponentAccess(setOrgaForm, itemModel),
            setOrgaForm.getSaveCancelSection().getCancelButton());

        SciDepartmentOrganizationSheet sheet =
                                       new SciDepartmentOrganizationSheet(
                itemModel);
        setDisplayComponent(sheet);
    }
}
