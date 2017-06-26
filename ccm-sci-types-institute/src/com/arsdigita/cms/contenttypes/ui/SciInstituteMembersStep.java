package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 * 
 * 
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciInstituteMembersStep
        extends SimpleEditStep
        implements GenericOrganizationalUnitPersonSelector {

    private String ADD_INSTITUTE_MEMBER_STEP = "SciInstituteAddMember";
    public static final String SELECTED_PERSON = "selected-person";
    private final ItemSelectionModel selectedPerson;
    private String selectedPersonRole;
    private String selectedPersonStatus;

    public SciInstituteMembersStep(final ItemSelectionModel itemModel,
                                   final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciInstituteMembersStep(final ItemSelectionModel itemModel,
                                   final AuthoringKitWizard parent,
                                   final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm addMemberSheet = new SciInstituteMemberAddForm(
                itemModel, this);
        add(ADD_INSTITUTE_MEMBER_STEP,
            SciInstituteGlobalizationUtil.globalize(
                "sciinstitute.ui.members.add"),
            new WorkflowLockedComponentAccess(addMemberSheet,itemModel),
            addMemberSheet.getSaveCancelSection().getCancelButton());

        final SciInstituteMembersTable memberTable =
                                       new SciInstituteMembersTable(itemModel,
                                                                    this);
        setDisplayComponent(memberTable);
        
        selectedPerson = new ItemSelectionModel(SELECTED_PERSON);
    }

    @Override
    public void register(final Page page) {
        super.register(page);
        
        page.addGlobalStateParam(selectedPerson.getStateParameter());
    }
    
    @Override
    public GenericPerson getSelectedPerson(final PageState state) {
        return (GenericPerson) selectedPerson.getSelectedItem(state);
    }

    @Override
    public void setSelectedPerson(final PageState state,
                                  final GenericPerson selectedPerson) {
        this.selectedPerson.setSelectedObject(state, selectedPerson);
    }

    @Override
    public String getSelectedPersonRole(final PageState state) {
        return selectedPersonRole;
    }

    @Override
    public void setSelectedPersonRole(final PageState state,
                                      final String selectedPersonRole) {
        this.selectedPersonRole = selectedPersonRole;
    }

    @Override
    public String getSelectedPersonStatus(final PageState state) {
        return selectedPersonStatus;
    }

    @Override
    public void setSelectedPersonStatus(final PageState state,
                                        final String selectedPersonStatus) {
        this.selectedPersonStatus = selectedPersonStatus;
    }

    @Override
    public void showEditComponent(final PageState state) {
        showComponent(state, ADD_INSTITUTE_MEMBER_STEP);
    }
}
