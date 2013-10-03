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
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciProjectDescriptionTextStep extends SimpleEditStep {

    public static final String EDIT_DESCRIPTION_TEXT_SHEET_NAME = "editDescriptionText";
    private String UPLOAD_PROJECT_DESC_SHEET_NAME = "uploadProjectDesc";

    public SciProjectDescriptionTextStep(final ItemSelectionModel itemModel,
                                         final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectDescriptionTextStep(final ItemSelectionModel itemModel,
                                         final AuthoringKitWizard parent,
                                         final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm descTextEditSheet = new SciProjectDescriptionTextEditForm(itemModel);
        add(EDIT_DESCRIPTION_TEXT_SHEET_NAME,
            SciProjectGlobalizationUtil.globalize("sciproject.ui.desc.text.edit"),
            new WorkflowLockedComponentAccess(descTextEditSheet, itemModel),
            descTextEditSheet.getSaveCancelSection().getCancelButton());

        final SciProjectDescriptionUploadForm uploadDescForm =
                                              new SciProjectDescriptionUploadForm(
                itemModel);
        add(UPLOAD_PROJECT_DESC_SHEET_NAME,
            SciProjectGlobalizationUtil.globalize("sciproject.ui.desc.upload"),
            new WorkflowLockedComponentAccess(uploadDescForm, itemModel),
            uploadDescForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(
                getSciProjectEditDescTextSheet(itemModel));
    }

    public static Component getSciProjectEditDescTextSheet(final ItemSelectionModel itemModel) {
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(SciProjectGlobalizationUtil.globalize("sciproject.ui.desc"),
                  SciProject.PROJECT_DESCRIPTION);

        return sheet;
    }

}
