package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 */
public class UnPublishedOrganizationStep extends SimpleEditStep {

    private String SET_UNPUBLISHED_ORGANIZATION_STEP =
                   "setUnPublishedOrganizationStep";

    public UnPublishedOrganizationStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public UnPublishedOrganizationStep(final ItemSelectionModel itemModel,
                                       final AuthoringKitWizard parent,
                                       String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm setOrgaForm = new UnPublishedOrganizationForm(itemModel);
        add(SET_UNPUBLISHED_ORGANIZATION_STEP,
            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.unpublished.setOrganization").localize(),
            new WorkflowLockedComponentAccess(setOrgaForm, itemModel),
            setOrgaForm.getSaveCancelSection().getCancelButton());

        UnPublishedOrganizationSheet sheet = new UnPublishedOrganizationSheet(
                itemModel);
        setDisplayComponent(sheet);
    }
}
