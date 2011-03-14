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
public class ExpertiseOrganizationStep extends SimpleEditStep {

    private String SET_EXPERTISE_ORGANIZATION_STEP =
                   "setExpertiseOrganizationStep";

    public ExpertiseOrganizationStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public ExpertiseOrganizationStep(final ItemSelectionModel itemModel,
                                     final AuthoringKitWizard parent,
                                     String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm setOrgaForm = new ExpertiseOrganizationForm(itemModel);
        add(SET_EXPERTISE_ORGANIZATION_STEP,
            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.expertise.setOrganization").localize(),
            new WorkflowLockedComponentAccess(setOrgaForm, itemModel),
            setOrgaForm.getSaveCancelSection().getCancelButton());

        ExpertiseOrganizationSheet sheet = new ExpertiseOrganizationSheet(
                itemModel);
        setDisplayComponent(sheet);
    }
}
