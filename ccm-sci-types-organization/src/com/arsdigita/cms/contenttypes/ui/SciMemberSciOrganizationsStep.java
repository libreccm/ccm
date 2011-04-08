package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 */
public class SciMemberSciOrganizationsStep extends SimpleEditStep {

    private String MEMBER_ADD_ORGANIZATION_SHEET_NAME = "memberAddOrganization";
    private SciOrganization selectedOrganization;
    private String selectedOrganizationRole;
    private String selectedOrganizationStatus;

    public SciMemberSciOrganizationsStep(ItemSelectionModel itemModel,
                                         AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciMemberSciOrganizationsStep(ItemSelectionModel itemModel,
                                         AuthoringKitWizard parent,
                                         String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addOrganizationForm = new SciMemberSciOrganizationAddForm(
                itemModel,
                this);
        add(MEMBER_ADD_ORGANIZATION_SHEET_NAME,
            (String) SciOrganizationGlobalizationUtil.globalize(
                "scimember.ui.organization.add").localize(),
            new WorkflowLockedComponentAccess(addOrganizationForm, itemModel),
            addOrganizationForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(new SciMemberSciOrganizationsTable(itemModel, this));
    }

    protected  SciOrganization getSelectedOrganization() {
        return selectedOrganization;
    }

    protected void setSelectedOrganization(SciOrganization organization) {
        this.selectedOrganization = organization;
    }

     protected String getSelectedOrganizationRole() {
        return selectedOrganizationRole;
    }

    protected void setSelectedOrganizationRole(String role) {
        this.selectedOrganizationRole = role;
    }

    protected String getSelectedOrganizationStatus() {
        return selectedOrganizationStatus;
    }

    protected void setSelectedOrganizationStatus(String status) {
        this.selectedOrganizationStatus = status;
    }

    protected void showEditComponent(PageState state) {
        showComponent(state, MEMBER_ADD_ORGANIZATION_SHEET_NAME);
    }
}
