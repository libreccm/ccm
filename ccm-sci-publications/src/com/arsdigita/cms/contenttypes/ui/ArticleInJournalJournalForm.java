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
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.Journal;
import com.arsdigita.cms.contenttypes.PublicationsConfig;
import com.arsdigita.cms.ui.ItemSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.kernel.Kernel;
import java.math.BigDecimal;

/**
 * Form for adding an association between an article in a journal and a journal.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ArticleInJournalJournalForm
        extends BasicItemForm
        implements FormProcessListener,
                   FormInitListener {

    private ItemSearchWidget itemSearch;
    private final String ITEM_SEARCH = "journal";
    private final static PublicationsConfig config = new PublicationsConfig();

    static {
        config.load();
    }

    public ArticleInJournalJournalForm(ItemSelectionModel itemModel) {
        super("ArticleInJournalJournal", itemModel);
    }

    @Override
    protected void addWidgets() {
        itemSearch = new ItemSearchWidget(ITEM_SEARCH,
                                          ContentType.findByAssociatedObjectType(
                Journal.class.getName()));
        itemSearch.setDefaultCreationFolder(config.getDefaultJournalsFolder());
        itemSearch.setEditAfterCreate(false);
        itemSearch.setQueryField("symbol");
        itemSearch.setLabel(PublicationGlobalizationUtil.globalize(
                            "publications.ui.articleInJournal.selectJournal"));
        add(itemSearch);
    }

    @Override
    public void init(FormSectionEvent fse) throws FormProcessException {
        PageState state = fse.getPageState();

        setVisible(state, true);
    }

    @Override
    public void process(FormSectionEvent fse) throws FormProcessException {
        FormData data = fse.getFormData();
        PageState state = fse.getPageState();
        ArticleInJournal article = (ArticleInJournal) getItemSelectionModel().
                getSelectedObject(state);

        if (this.getSaveCancelSection().getSaveButton().isSelected(state)) {
            Journal journal = (Journal) data.get(ITEM_SEARCH);
            journal = (Journal) journal.getContentBundle().getInstance(article.getLanguage());

            article.setJournal(journal);
            itemSearch.publishCreatedItem(data, journal);
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
                    "publications.ui.articleInJournal.selectJournal.no_journal_selected"));
            return;
        }

        ArticleInJournal article = (ArticleInJournal) getItemSelectionModel().
                getSelectedObject(
                state);
        Journal journal = (Journal) data.get(ITEM_SEARCH);

        if (!(journal.getContentBundle().hasInstance(article.getLanguage(),
                                                     Kernel.getConfig().
              languageIndependentItems()))) {
            data.addError(
                    PublicationGlobalizationUtil.globalize(
                    "publications.ui.articleInJournal.selectJournal.no_suitable_language_variant"));
            return;
        }
    }

}
