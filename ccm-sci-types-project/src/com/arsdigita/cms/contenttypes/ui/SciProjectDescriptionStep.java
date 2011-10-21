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
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciProjectDescriptionStep extends SimpleEditStep {
    
    private String EDIT_PROJECT_DESC_SHEET_NAME = "editProjectDesc";
    private String UPLOAD_PROJECT_DESC_SHEET_NAME = "uploadProjectDesc";
    
     public SciProjectDescriptionStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectDescriptionStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent,
                                     final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm editDescForm =
                      new SciProjectDescriptionEditForm(itemModel);
        add(EDIT_PROJECT_DESC_SHEET_NAME,
            (String) SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.desc.edit").localize(),
            new WorkflowLockedComponentAccess(editDescForm, itemModel),
            editDescForm.getSaveCancelSection().getCancelButton());

        final SciProjectDescriptionUploadForm uploadDescForm =
                                        new SciProjectDescriptionUploadForm(
                itemModel);
        add(UPLOAD_PROJECT_DESC_SHEET_NAME,
            (String) SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.desc.upload").localize(),
            new WorkflowLockedComponentAccess(uploadDescForm, itemModel),
            uploadDescForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(
                getSciProjectEditDescSheet(itemModel));

    }
    
    public static Component getSciProjectEditDescSheet(
            final ItemSelectionModel itemModel) {
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(SciProjectGlobalizationUtil.globalize(
                "sciproject.ui.desc"),
                  SciProject.PROJECT_DESCRIPTION);
        if (SciProject.getConfig().getEnableFunding()) {
            sheet.add(SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.funding"),
                      SciProject.FUNDING);
        }
        if (SciProject.getConfig().getEnableFundingVolume()) {
            sheet.add(SciProjectGlobalizationUtil.globalize(
                    "sciproject.ui.funding.volume"),
                      SciProject.FUNDING_VOLUME);
        }

        return sheet;
    }
}
