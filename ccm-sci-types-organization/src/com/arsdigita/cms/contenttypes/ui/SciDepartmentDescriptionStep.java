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

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * EditStep for the description of a SciDepartment.
 *
 * @author Jens Pelzetter
 * @see SciDepartment
 */
public class SciDepartmentDescriptionStep extends SimpleEditStep {

    private String EDIT_DEPARTMENT_DESC_SHEET_NAME = "editDepartmentDesc";
    private String UPLOAD_DEPARTMENT_DESC_SHEET_NAME = "uploadDepartmentDesc";

    public SciDepartmentDescriptionStep(ItemSelectionModel itemModel,
                                        AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentDescriptionStep(ItemSelectionModel itemModel,
                                        AuthoringKitWizard parent,
                                        String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm editDescForm =
                      new SciDepartmentDescriptionEditForm(itemModel);
        add(EDIT_DEPARTMENT_DESC_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.edit_desc").localize(),
            new WorkflowLockedComponentAccess(editDescForm, itemModel),
            editDescForm.getSaveCancelSection().getCancelButton());

        SciDepartmentDescriptionUploadForm uploadDescForm =
                                           new SciDepartmentDescriptionUploadForm(
                itemModel);
        add(UPLOAD_DEPARTMENT_DESC_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.upload_desc").localize(),
            new WorkflowLockedComponentAccess(uploadDescForm, itemModel),
            uploadDescForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(
                getSciDepartmentDescSheet(itemModel));
    }

    public static Component getSciDepartmentDescSheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.desc"),
                SciDepartment.DEPARTMENT_DESCRIPTION);

        return sheet;
    }
}
