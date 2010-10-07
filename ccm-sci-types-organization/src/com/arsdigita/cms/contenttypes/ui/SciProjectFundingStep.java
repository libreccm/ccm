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
 */
public class SciProjectFundingStep extends SimpleEditStep {

    private String EDIT_FUNDING_SHEET_NAME = "editFunding";

    public SciProjectFundingStep(ItemSelectionModel itemModel,
                                 AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectFundingStep(ItemSelectionModel itemModel,
                                 AuthoringKitWizard parent,
                                 String prefix) {
        super(itemModel, parent, null);

        BasicItemForm editFundingForm = new SciProjectFundingForm(itemModel);
        add(EDIT_FUNDING_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.edit_funding").localize(),
            new WorkflowLockedComponentAccess(editFundingForm, itemModel),
            editFundingForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(
                getSciProjectFundingSheet(itemModel));
    }

    public static Component getSciProjectFundingSheet(
            ItemSelectionModel itemModel) {
        DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
                itemModel);

        sheet.add(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.funding"),
                  SciProject.FUNDING);

        return sheet;
    }
}
