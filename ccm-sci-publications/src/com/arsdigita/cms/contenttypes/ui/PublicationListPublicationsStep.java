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
public class PublicationListPublicationsStep extends SimpleEditStep {

    private static final String ADD_PUBLICATION_SHEET_NAME = "addPublication";

    public PublicationListPublicationsStep(ItemSelectionModel itemModel,
                                           AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public PublicationListPublicationsStep(ItemSelectionModel itemModel,
                                           AuthoringKitWizard parent,
                                           String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addPublicationSheet =
                      new PublicationListPublicationAddForm(itemModel);
        add(ADD_PUBLICATION_SHEET_NAME,
            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publicationlist.add_publication").localize(),
            new WorkflowLockedComponentAccess(addPublicationSheet,
                                              itemModel),
            addPublicationSheet.getSaveCancelSection().getCancelButton());

        PublicationListPublicationsTable publicationsTable =
                new PublicationListPublicationsTable(itemModel);
        setDisplayComponent(publicationsTable);
    }
}
