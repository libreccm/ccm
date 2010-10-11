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
public class SciDepartmentSuperDepartmentStep extends SimpleEditStep {

    private String SET_SUPER_DEPARTMENT_STEP = "setSuperDepartment";

    public SciDepartmentSuperDepartmentStep(ItemSelectionModel itemModel,
                                            AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentSuperDepartmentStep(ItemSelectionModel itemModel,
                                            AuthoringKitWizard parent,
                                            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm setSuperDepartmentForm =
                      new SciDepartmentSuperDepartmentForm(itemModel);
        add(SET_SUPER_DEPARTMENT_STEP,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.setSuperDepartment").localize(),
            new WorkflowLockedComponentAccess(setSuperDepartmentForm,
                                              itemModel),
            setSuperDepartmentForm.getSaveCancelSection().
                getCancelButton());

        SciDepartmentSuperDepartmentSheet sheet =
                                          new SciDepartmentSuperDepartmentSheet(
                itemModel);
        setDisplayComponent(sheet);

    }
}
