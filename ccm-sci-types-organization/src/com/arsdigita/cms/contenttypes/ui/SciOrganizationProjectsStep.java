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
public class SciOrganizationProjectsStep extends SimpleEditStep {

    private String ADD_PROJECT_SHEET_NAME = "addProject";

    public SciOrganizationProjectsStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciOrganizationProjectsStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent,
                                       String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addProjectSheet =
                      new SciOrganizationProjectAddForm(itemModel);
        add(ADD_PROJECT_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.add_project").localize(),
            new WorkflowLockedComponentAccess(addProjectSheet, itemModel),
            addProjectSheet.getSaveCancelSection().getCancelButton());

        SciOrganizationProjectsTable projectsTable =
                                        new SciOrganizationProjectsTable(
                itemModel);
        setDisplayComponent(projectsTable);
    }
}
