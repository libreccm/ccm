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
public class SciDepartmentOrganizationStep extends SimpleEditStep {

    private String SET_DEPARTMENT_ORGANIZATION_STEP =
                   "setDepartmentOrganization";

    public SciDepartmentOrganizationStep(ItemSelectionModel itemModel,
                                         AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciDepartmentOrganizationStep(ItemSelectionModel itemModel,
                                         AuthoringKitWizard parent,
                                         String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm setOrgaForm =
                      new SciDepartmentOrganizationForm(itemModel);
        add(SET_DEPARTMENT_ORGANIZATION_STEP,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.setOrganization").localize(),
            new WorkflowLockedComponentAccess(setOrgaForm, itemModel),
            setOrgaForm.getSaveCancelSection().getCancelButton());

        SciDepartmentOrganizationSheet sheet =
                                       new SciDepartmentOrganizationSheet(
                itemModel);
        setDisplayComponent(sheet);
    }
}
