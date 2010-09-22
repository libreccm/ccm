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
public class PublicationPublicationListPropertyStep extends SimpleEditStep {

    private static final String ADD_PUBLICATIONLIST_SHEET_NAME =
                                "addPublicationList";

    public PublicationPublicationListPropertyStep(ItemSelectionModel itemModel,
                                                  AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public PublicationPublicationListPropertyStep(ItemSelectionModel itemModel,
                                                  AuthoringKitWizard parent,
                                                  String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addPublicationListSheet =
                      new PublicationPublicationListAddForm(itemModel);
        add(ADD_PUBLICATIONLIST_SHEET_NAME,
            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.publicationlists.add_list").localize(),
            new WorkflowLockedComponentAccess(addPublicationListSheet,
                                              itemModel),
            addPublicationListSheet.getSaveCancelSection().getCancelButton());

        PublicationPublicationListsTable listsTable =
                new PublicationPublicationListsTable(itemModel);
        setDisplayComponent(listsTable);

    }
}
