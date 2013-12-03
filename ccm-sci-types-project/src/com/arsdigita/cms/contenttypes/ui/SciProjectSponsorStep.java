package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciProjectSponsorStep extends SimpleEditStep {

    protected final static String SCIPROJECT_SPONSOR_STEP = "SciProjectSponsorStep";
    
    private GenericOrganizationalUnit selectedSponsor;
    private String selectedSponsorFundingCode;

    public SciProjectSponsorStep(final ItemSelectionModel itemModel,
                                 final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectSponsorStep(final ItemSelectionModel itemModel,
                                 final AuthoringKitWizard parent,
                                 final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm sponsorForm = new SciProjectSponsorForm(itemModel, this);
        add(SCIPROJECT_SPONSOR_STEP,
            (String) SciProjectGlobalizationUtil.globalize("sciproject.ui.sponsor.add").localize(),
            new WorkflowLockedComponentAccess(sponsorForm, itemModel),
            sponsorForm.getSaveCancelSection().getCancelButton());
        
        final SciProjectSponsorSheet sheet = new SciProjectSponsorSheet(itemModel, this);
        setDisplayComponent(sheet);
    }

    protected GenericOrganizationalUnit getSelectedSponsor() {
        return selectedSponsor;
    }
    
    protected void setSelectedSponsor(final GenericOrganizationalUnit selectedSponsor) {
        this.selectedSponsor = selectedSponsor;
    }
    
    protected String getSelectedSponsorFundingCode() {
        return selectedSponsorFundingCode;
    }
    
    protected  void setSelectedSponsorFundingCode(final String selectedSponsorFundingCode) {
        this.selectedSponsorFundingCode = selectedSponsorFundingCode;
    }
    
}
