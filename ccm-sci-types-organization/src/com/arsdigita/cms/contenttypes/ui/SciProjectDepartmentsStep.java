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
public class SciProjectDepartmentsStep extends SimpleEditStep {

    private String PROJECT_ADD_DEPARTMENT_SHEET_NAME = "projectAddDepartment";

    public SciProjectDepartmentsStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectDepartmentsStep(ItemSelectionModel itemModel,
                                     AuthoringKitWizard parent,
                                     String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addDepartmentForm =
                new SciProjectDepartmentAddForm(itemModel);
        add(PROJECT_ADD_DEPARTMENT_SHEET_NAME,
                (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.addDepartment").localize(),
                new WorkflowLockedComponentAccess(addDepartmentForm, itemModel),
                addDepartmentForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(new SciProjectDepartmentsTable(itemModel));
    }
}
