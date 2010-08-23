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
public class CollectedVolumeArticlesStep extends SimpleEditStep {

    private static final String ADD_ARTICLE_SHEET_NAME = "addArticle";

    public CollectedVolumeArticlesStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent) {
        this(itemModel, parent, null);
    }

    public CollectedVolumeArticlesStep(ItemSelectionModel itemModel,
                                       AuthoringKitWizard parent,
                                       String prefix) {
        super(itemModel, parent, prefix);

        BasicItemForm addArticleSheet =
                      new CollectedVolumeArticleAddForm(itemModel);
        add(ADD_ARTICLE_SHEET_NAME,
            (String) PublicationGlobalizationUtil.globalize(
                "publications.ui.collected_volume.add_article").localize(),
            new WorkflowLockedComponentAccess(addArticleSheet, itemModel),
            addArticleSheet.getSaveCancelSection().getCancelButton());

        CollectedVolumeArticlesTable articlesTable =
                                     new CollectedVolumeArticlesTable(
                itemModel);
        setDisplayComponent(articlesTable);
    }
}
