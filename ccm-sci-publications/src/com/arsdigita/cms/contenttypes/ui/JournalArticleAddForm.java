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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.ArticleInJournalCollection;
import com.arsdigita.cms.contenttypes.Journal;
import com.arsdigita.cms.contenttypes.PublicationsConfig;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;

import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class JournalArticleAddForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private static final Logger s_log = Logger.getLogger(
            JournalArticleAddForm.class);
    private JournalPropertiesStep m_step;
    private ItemSearchWidget m_itemSearch;
    private final String ITEM_SEARCH = "articles";
    private ItemSelectionModel m_itemModel;
    private final static PublicationsConfig config = new PublicationsConfig();

    static {
        config.load();
    }

    public JournalArticleAddForm(ItemSelectionModel itemModel) {
        super("ArticlesAddForm", itemModel);
        m_itemModel = itemModel;
    }

    @Override
    public void addWidgets() {
        m_itemSearch = new ItemSearchWidget(
                ITEM_SEARCH,
                ContentType.findByAssociatedObjectType(
                ArticleInJournal.class.getName()));
        m_itemSearch.setDefaultCreationFolder(config.getDefaultArticlesInJournalFolder());
        m_itemSearch.setLabel(PublicationGlobalizationUtil.globalize(
                              "publications.ui.journal.articles.select_article"));
        add(m_itemSearch);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        Journal journal = (Journal) getItemSelectionModel().getSelectedObject(
                state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            ArticleInJournal article = (ArticleInJournal) data.get(ITEM_SEARCH);
            article = (ArticleInJournal) article.getContentBundle().getInstance(journal.
                    getLanguage());

            journal.addArticle(article);
            m_itemSearch.publishCreatedItem(data, article);
        }

        init(fse);
    }

    @Override
    public void validate(FormSectionEvent fse) throws FormProcessException {
        final PageState state = fse.getPageState();
        final FormData data = fse.getFormData();

        if (data.get(ITEM_SEARCH) == null) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.journal.articles.select_article.no_article_selected"));
            return;
        }

        Journal journal = (Journal) getItemSelectionModel().getSelectedObject(
                state);
        ArticleInJournal article = (ArticleInJournal) data.get(ITEM_SEARCH);
        if (!(article.getContentBundle().hasInstance(journal.getLanguage(),
                                                     Kernel.getConfig().
              languageIndependentItems()))) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.journal.articles.select_article.no_suitable_language_variant"));
            return;
        }

        article = (ArticleInJournal) article.getContentBundle().getInstance(journal.
                getLanguage());
        ArticleInJournalCollection articles = journal.getArticles();
        articles.addFilter(String.format("id = %s", article.getID().toString()));
        if (articles.size() > 0) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.journal.articles.select_article.already_added"));
        }

        articles.close();
    }
}
