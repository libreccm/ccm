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
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 * Step for editing the description of a {@link SciProject}.
 *
 * @author Jens Pelzetter
 */
public class SciProjectDescriptionStep extends SimpleEditStep {

    private String EDIT_PROJECT_DESC_SHEET_NAME = "editProjectDesc";
    private String UPLOAD_PROJECT_DESC_SHEET_NAME = "uploadProjectDesc";

    public SciProjectDescriptionStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectDescriptionStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent,
                                     String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm editDescForm =
                      new SciProjectDescriptionEditForm(itemModel);
        add(EDIT_PROJECT_DESC_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.edit_desc").localize(),
            new WorkflowLockedComponentAccess(editDescForm, itemModel),
            editDescForm.getSaveCancelSection().getCancelButton());

        SciProjectDescriptionUploadForm uploadDescForm =
                                        new SciProjectDescriptionUploadForm(
                itemModel);
        add(UPLOAD_PROJECT_DESC_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.upload_desc").localize(),
            new WorkflowLockedComponentAccess(uploadDescForm, itemModel),
            uploadDescForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(
                getSciProjectEditDescSheet(itemModel));

    }

    public static Component getSciProjectEditDescSheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.desc"),
                  SciProject.PROJECT_DESCRIPTION);
        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.funding"),
                  SciProject.FUNDING);

        return sheet;
    }
}
