package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ItemSelectionModel;
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

    private String SCIPROJECT_SPONSOR_STEP = "SciProjectSponsorStep";

    public SciProjectSponsorStep(final ItemSelectionModel itemModel,
                                 final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public SciProjectSponsorStep(final ItemSelectionModel itemModel,
                                 final AuthoringKitWizard parent,
                                 final String prefix) {
        super(itemModel, parent, prefix);

        final BasicItemForm sponsorForm = new SciProjectSponsorForm(itemModel);
        add(SCIPROJECT_SPONSOR_STEP,
            (String) SciProjectGlobalizationUtil.globalize("sciproject.ui.sponsor.add").localize(),
            new WorkflowLockedComponentAccess(sponsorForm, itemModel),
            sponsorForm.getSaveCancelSection().getCancelButton());
        
        final SciProjectSponsorSheet sheet = new SciProjectSponsorSheet(itemModel);
        setDisplayComponent(sheet);
    }

}
