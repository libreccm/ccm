/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciProjectMembersStep
        extends SimpleEditStep 
implements GenericOrganizationalUnitPersonSelector{
    
    private static final String ADD_PROJECT_MEMBER_SHEET_NAME = "SciProjectAddMember";
    private GenericPerson selectedPerson;
    private String selectedPersonRole;
    private String selectedPersonStatus;
    
    public SciProjectMembersStep(final ItemSelectionModel itemModel,
                                 final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }
    
    public SciProjectMembersStep(final ItemSelectionModel itemModel,
                                 final AuthoringKitWizard parent,
                                 final String prefix) {
        super(itemModel, parent, prefix);
        
        final BasicItemForm addMemberSheet = new SciProjectMemberAddForm(
                itemModel, this);
        add(ADD_PROJECT_MEMBER_SHEET_NAME, 
            (String) SciProjectGlobalizationUtil.globalize("sciproject.ui.members.add").localize(),
            new WorkflowLockedComponentAccess(addMemberSheet, itemModel),
            addMemberSheet.getSaveCancelSection().getCancelButton());
        
        final SciProjectMemberTable memberTable = new SciProjectMemberTable(
                itemModel, this);
        setDisplayComponent(memberTable);                
    }
    
    public GenericPerson getSelectedPerson() {
        return selectedPerson;
    }
    
      public void setSelectedPerson(final GenericPerson selectedPerson) {
        this.selectedPerson = selectedPerson;
    }
    
    public String getSelectedPersonRole() {
        return selectedPersonRole;
    }
    
     public void setSelectedPersonRole(final String selectedPersonRole) {
        this.selectedPersonRole = selectedPersonRole;
    }
    
    public String getSelectedPersonStatus() {
        return selectedPersonStatus;
    }
          
    public void setSelectedPersonStatus(final String selectedPersonStatus) {
        this.selectedPersonStatus = selectedPersonStatus;
    }

    public void showEditComponent(final PageState state) {
        showComponent(state, ADD_PROJECT_MEMBER_SHEET_NAME);
    }
}
