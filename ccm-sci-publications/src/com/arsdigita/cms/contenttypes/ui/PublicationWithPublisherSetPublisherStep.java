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
public class PublicationWithPublisherSetPublisherStep extends SimpleEditStep {

    private String SET_PUBLICATION_PUBLISHER_STEP =
                   "setPublicationPublisherStep";

    public PublicationWithPublisherSetPublisherStep(final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

     public PublicationWithPublisherSetPublisherStep(final ItemSelectionModel itemModel,
            final AuthoringKitWizard parent,
            final String prefix) {
         super(itemModel, parent, prefix);

         BasicItemForm setPublisherForm = new PublicationWithPublisherSetPublisherForm(
                 itemModel);
         add(SET_PUBLICATION_PUBLISHER_STEP,
                 (String) PublicationGlobalizationUtil.globalize("publications.ui.with_publisher.setPublisher").localize(),
                 new WorkflowLockedComponentAccess(setPublisherForm, itemModel),
                 setPublisherForm.getSaveCancelSection().getCancelButton());

         PublicationWithPublisherSetPublisherSheet sheet = new PublicationWithPublisherSetPublisherSheet(
                 itemModel);
         setDisplayComponent(sheet);
     }
}
