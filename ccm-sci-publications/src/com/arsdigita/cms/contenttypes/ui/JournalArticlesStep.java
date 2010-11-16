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
public class JournalArticlesStep extends SimpleEditStep {

    private static final String ADD_ARTICLE_SHEET_NAME = "addArticle";

    public JournalArticlesStep(ItemSelectionModel itemModel,
                               AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public JournalArticlesStep(ItemSelectionModel itemModel,
                               AuthoringKitWizard parent,
                               String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addArticleSheet =
                new JournalArticleAddForm(itemModel);
        add(ADD_ARTICLE_SHEET_NAME,
                (String) PublicationGlobalizationUtil.globalize("publications.ui.journal.add_article").localize(),
                new WorkflowLockedComponentAccess(addArticleSheet, itemModel),
                addArticleSheet.getSaveCancelSection().getCancelButton());

        JournalArticlesTable articlesTable =
                new JournalArticlesTable(itemModel);
        setDisplayComponent(articlesTable);
    }
}
