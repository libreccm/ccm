/*
 * Copyright (c) 2010 Jens Pelzetter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
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
            PublicationGlobalizationUtil.globalize(
                                         "publications.ui.journal.add_article"),
            new WorkflowLockedComponentAccess(addArticleSheet, itemModel),
            addArticleSheet.getSaveCancelSection().getCancelButton());

        JournalArticlesTable articlesTable =
                new JournalArticlesTable(itemModel);
        setDisplayComponent(articlesTable);
    }
}
