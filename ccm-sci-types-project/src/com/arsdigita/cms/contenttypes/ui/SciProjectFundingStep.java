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
public class SciProjectFundingStep extends SimpleEditStep {

    private String EDIT_PROJECT_FUNDING_SHEET_NAME = "editProjectFunding";
    private String UPLOAD_PROJECT_FUNDING_SHEET_NAME = "uploadProjectFunding";

    public SciProjectFundingStep(final ItemSelectionModel itemModel,
                                 final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectFundingStep(final ItemSelectionModel itemModel,
                                 final AuthoringKitWizard parent,
                                 final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm editFundingForm = new SciProjectFundingEditForm(itemModel);
        add(EDIT_PROJECT_FUNDING_SHEET_NAME,
            SciProjectGlobalizationUtil.globalize("sciproject.ui.funding.edit"),
            new WorkflowLockedComponentAccess(editFundingForm, itemModel),
            editFundingForm.getSaveCancelSection().getCancelButton());

        final SciProjectFundingUploadForm uploadFundingForm = new SciProjectFundingUploadForm(
                itemModel);
        add(UPLOAD_PROJECT_FUNDING_SHEET_NAME,
            SciProjectGlobalizationUtil.globalize("sciproject.ui.funding.upload"),
            new WorkflowLockedComponentAccess(uploadFundingForm, itemModel),
            uploadFundingForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(getSciProjectEditFundingSheet(itemModel));
    }

    public static Component getSciProjectEditFundingSheet(final ItemSelectionModel itemModel) {
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(itemModel);

        if (SciProject.getConfig().getEnableFunding()) {
            sheet.add(SciProjectGlobalizationUtil.globalize("sciproject.ui.funding"),
                      SciProject.FUNDING);
        }
        if (SciProject.getConfig().getEnableFundingVolume()) {
            sheet.add(SciProjectGlobalizationUtil.globalize("sciproject.ui.funding.volume"),
                      SciProject.FUNDING_VOLUME);
        }

        return sheet;
    }

}
