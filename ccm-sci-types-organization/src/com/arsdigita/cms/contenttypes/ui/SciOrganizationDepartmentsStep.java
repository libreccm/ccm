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
public class SciOrganizationDepartmentsStep extends SimpleEditStep {

    private String ADD_DEPARTMENT_SHEET_NAME = "addDepartment";

    public SciOrganizationDepartmentsStep(ItemSelectionModel itemModel,
                                          AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciOrganizationDepartmentsStep(ItemSelectionModel itemModel,
                                          AuthoringKitWizard parent,
                                          String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addDepartmentSheet =
                      new SciOrganizationDepartmentAddForm(itemModel);
        add(ADD_DEPARTMENT_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.add_department").localize(),
            new WorkflowLockedComponentAccess(addDepartmentSheet, itemModel),
            addDepartmentSheet.getSaveCancelSection().getCancelButton());

        SciOrganizationDepartmentsTable departmentTable =
                                        new SciOrganizationDepartmentsTable(
                itemModel);
        setDisplayComponent(departmentTable);
    }
}
