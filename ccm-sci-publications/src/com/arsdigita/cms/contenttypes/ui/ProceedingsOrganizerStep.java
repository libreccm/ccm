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
public class ProceedingsOrganizerStep extends SimpleEditStep {

    private String SET_PROCEEDINGS_ORGANIZER_STEP = "setProceedingsOrganizerStep";

    public ProceedingsOrganizerStep(final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public ProceedingsOrganizerStep(final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent,
            final String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm setOrganizerForm = new ProceedingsOrganizerForm(itemModel);
        add(SET_PROCEEDINGS_ORGANIZER_STEP,
                (String) PublicationGlobalizationUtil.globalize("publications.ui.proceedings.setOrganizer").localize(),
                new WorkflowLockedComponentAccess(setOrganizerForm, itemModel),
                setOrganizerForm.getSaveCancelSection().getCancelButton());

        ProceedingsOrganizerSheet sheet = new ProceedingsOrganizerSheet(
                itemModel);
        setDisplayComponent(sheet);
    }

}
