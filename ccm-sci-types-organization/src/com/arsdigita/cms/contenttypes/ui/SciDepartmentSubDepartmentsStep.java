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
public class SciDepartmentSubDepartmentsStep extends SimpleEditStep {

    private String ADD_SUBDEPARTMENT_SHEET_NAME = "addSubDepartment";

    public SciDepartmentSubDepartmentsStep(ItemSelectionModel itemModel,
                                           AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentSubDepartmentsStep(ItemSelectionModel itemModel,
                                           AuthoringKitWizard parent,
                                           String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addSubDepartmentSheet =
                      new SciDepartmentSubDepartmentAddForm(itemModel);
        add(ADD_SUBDEPARTMENT_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.organization.add_subdepartment").localize(),
            new WorkflowLockedComponentAccess(addSubDepartmentSheet, itemModel),
            addSubDepartmentSheet.getSaveCancelSection().getCancelButton());

        SciDepartmentSubDepartmentsTable subdepartmentTable =
                                           new SciDepartmentSubDepartmentsTable(
                itemModel);
        setDisplayComponent(subdepartmentTable);
    }
}
