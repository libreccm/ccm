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
public class PublicPersonalProfileNavigationStep extends SimpleEditStep {
    
    public static final String EDIT_NAV_SHEET_NAME = "editNav";
    
    public PublicPersonalProfileNavigationStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent) {
       this(itemModel, parent, null);                
    }
    
     public PublicPersonalProfileNavigationStep(
            final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent,
            final String prefix) {
         super(itemModel, parent, prefix);
         
         BasicItemForm editNavSheet = new PublicPersonalProfileNavigationAddForm(itemModel, this);
         add(EDIT_NAV_SHEET_NAME,
             (String) PublicPersonalProfileGlobalizationUtil.globalize("publicpersonalprofile.ui.profile.nav_add").localize(),
             new WorkflowLockedComponentAccess(editNavSheet, itemModel),
             editNavSheet.getSaveCancelSection().getCancelButton());
         
         PublicPersonalProfileNavigationTable navTable = new PublicPersonalProfileNavigationTable(itemModel, this);
         setDisplayComponent(navTable);
     }
     
     
    
    
}
