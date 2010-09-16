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
public class PublicationAuthorsPropertyStep extends SimpleEditStep {

    private static final String ADD_AUTHOR_SHEET_NAME = "addAuthor";

    public PublicationAuthorsPropertyStep(ItemSelectionModel itemModel,
                                          AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public PublicationAuthorsPropertyStep(
            ItemSelectionModel itemModel,
            AuthoringKitWizard parent,
            String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addAuthorSheet =
                      new PublicationAuthorAddForm(itemModel);
        add(ADD_AUTHOR_SHEET_NAME,
            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.authors.add_author").localize(),
            new WorkflowLockedComponentAccess(addAuthorSheet, itemModel),
            addAuthorSheet.getSaveCancelSection().getCancelButton());

        PublicationAuthorsTable authorsTable = new PublicationAuthorsTable(
                itemModel);
        setDisplayComponent(authorsTable);
    }
}
