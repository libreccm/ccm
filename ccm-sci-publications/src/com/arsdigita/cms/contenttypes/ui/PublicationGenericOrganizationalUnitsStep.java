package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationGenericOrganizationalUnitsStep extends SimpleEditStep {
    
    private String ADD_ORGAUNIT_SHEET_NAME = "PublicationGenericOrganizationalUnitAddForm";
    
    public PublicationGenericOrganizationalUnitsStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }
    
    public PublicationGenericOrganizationalUnitsStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent,
            final String prefix) {
        super(itemModel, parent, prefix);
        
        final BasicItemForm addOrgaUnitSheet = new PublicationGenericOrganizationalUnitAddForm(
                itemModel);
        add(ADD_ORGAUNIT_SHEET_NAME,
            (String) PublicationGlobalizationUtil.globalize("publications.ui.orgaunit.add").localize(),
            new WorkflowLockedComponentAccess(addOrgaUnitSheet, itemModel),
            addOrgaUnitSheet.getSaveCancelSection().getCancelButton());
        
        final PublicationGenericOrganizationalUnitsTable orgaunitsTable = new PublicationGenericOrganizationalUnitsTable(
                itemModel);
        setDisplayComponent(orgaunitsTable);        
    }             
}
