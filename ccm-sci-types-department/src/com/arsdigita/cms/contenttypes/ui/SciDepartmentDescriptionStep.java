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

import com.arsdigita.bebop.Component;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Authoring step for editing the description of a SciDepartment.
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciDepartmentDescriptionStep extends SimpleEditStep {

    private String EDIT_DEPARTMENT_DESC_SHEET_NAME = "editDepartmentDesc";
    private String UPDATE_DEPARTMENT_DESC_SHEET_NAME = "updateDepartmentDesc";

    public SciDepartmentDescriptionStep(final ItemSelectionModel itemModel,
                                        final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentDescriptionStep(final ItemSelectionModel itemModel,
                                        final AuthoringKitWizard parent,
                                        final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm editDescFrom = new SciDepartmentDescriptionEditForm(
                itemModel);
        add(EDIT_DEPARTMENT_DESC_SHEET_NAME,
            (String) SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.desc.edit").localize(),
            new WorkflowLockedComponentAccess(editDescFrom, itemModel),
            editDescFrom.getSaveCancelSection().getCancelButton());

        final SciDepartmentDescriptionUploadForm updateDescForm =
                                                 new SciDepartmentDescriptionUploadForm(
                itemModel);
        add(UPDATE_DEPARTMENT_DESC_SHEET_NAME,
            (String) SciDepartmentGlobalizationUtil.globalize(
                "scidepartment.ui.desc.upload").localize(),
            new WorkflowLockedComponentAccess(updateDescForm, itemModel),
            updateDescForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getSciDepartmentEditDescSheet(itemModel));
    }

    public static Component getSciDepartmentEditDescSheet(
            final ItemSelectionModel itemModel) {
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);
        
        sheet.add(SciDepartmentGlobalizationUtil.globalize("scidepartment.ui.desc"),
                  SciDepartment.DEPARTMENT_DESCRIPTION);
        
        return sheet;
    }
}
