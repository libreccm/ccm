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
public class SciProjectSuperProjectStep extends SimpleEditStep {

    private String SET_SUPER_PROJECT_STEP = "setSuperProject";

    public SciProjectSuperProjectStep(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectSuperProjectStep(ItemSelectionModel itemModel,
                                      AuthoringKitWizard parent,
                                      String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm setSuperProjectForm =
                      new SciProjectSuperProjectSetForm(itemModel);
        add(SET_SUPER_PROJECT_STEP,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.setSuperProject").localize(),
            new WorkflowLockedComponentAccess(setSuperProjectForm,
                                              itemModel),
            setSuperProjectForm.getSaveCancelSection().
                getCancelButton());

        SciProjectSuperProjectSheet superProjectSheet =
                                    new SciProjectSuperProjectSheet(itemModel);
        setDisplayComponent(superProjectSheet);
    }
}
