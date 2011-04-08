package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciMemberSciDepartmentsStep extends SimpleEditStep {

    private String MEMBER_ADD_DEPARTMENT_SHEET_NAME = "memberAddDepartment";
    private SciDepartment selectedDepartment;
    private String selectedDepartmentRole;
    private String selectedDepartmentStatus;

    public SciMemberSciDepartmentsStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciMemberSciDepartmentsStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addDepartmentForm = new SciMemberSciDepartmentAddForm(itemModel, this);
        add(MEMBER_ADD_DEPARTMENT_SHEET_NAME,
                (String) SciOrganizationGlobalizationUtil.globalize("scimember.ui.department.add").localize(),
                new WorkflowLockedComponentAccess(addDepartmentForm, itemModel),
                addDepartmentForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(new SciMemberSciDepartmentsTable(itemModel, this));
    }

    protected SciDepartment getSelectedDepartment() {
        return selectedDepartment;
    }

    protected void setSelectedDepartment(SciDepartment selectedDepartment) {
        this.selectedDepartment = selectedDepartment;
    }

    protected String getSelectedDepartmentRole() {
        return selectedDepartmentRole;
    }

    protected void setSelectedDepartmentRole(String selectedDepartmentRole) {
        this.selectedDepartmentRole = selectedDepartmentRole;
    }

    protected String getSelectedDepartmentStatus() {
        return selectedDepartmentStatus;
    }

    protected void setSelectedDepartmentStatus(String selectedDepartmentStatus) {
        this.selectedDepartmentStatus = selectedDepartmentStatus;
    }

    protected void showEditComponent(PageState state) {
        showComponent(state, MEMBER_ADD_DEPARTMENT_SHEET_NAME);
    }

}
