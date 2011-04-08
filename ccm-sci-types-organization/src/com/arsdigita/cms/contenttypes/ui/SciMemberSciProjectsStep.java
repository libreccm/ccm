package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciMemberSciProjectsStep extends SimpleEditStep {

    private String MEMBER_ADD_project_SHEET_NAME = "memberAddproject";
    private SciProject selectedProject;
    private String selectedProjectRole;
    private String selectedProjectStatus;

    public SciMemberSciProjectsStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciMemberSciProjectsStep(ItemSelectionModel itemModel,
            AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addprojectForm = new SciMemberSciProjectAddForm(itemModel, this);
        add(MEMBER_ADD_project_SHEET_NAME,
                (String) SciOrganizationGlobalizationUtil.globalize("scimember.ui.project.add").localize(),
                new WorkflowLockedComponentAccess(addprojectForm, itemModel),
                addprojectForm.getSaveCancelSection().getCancelButton());

        setDisplayComponent(new SciMemberSciProjectsTable(itemModel, this));
    }

    protected SciProject getSelectedProject() {
        return selectedProject;
    }

    protected void setSelectedProject(SciProject selectedProject) {
        this.selectedProject = selectedProject;
    }

    protected String getSelectedProjectRole() {
        return selectedProjectRole;
    }

    protected void setSelectedProjectRole(String selectedProjectRole) {
        this.selectedProjectRole = selectedProjectRole;
    }

    protected String getSelectedProjectStatus() {
        return selectedProjectStatus;
    }

    protected void setSelectedProjectStatus(String selectedProjectStatus) {
        this.selectedProjectStatus = selectedProjectStatus;
    }

    protected void showEditComponent(PageState state) {
        showComponent(state, MEMBER_ADD_project_SHEET_NAME);
    }

}
