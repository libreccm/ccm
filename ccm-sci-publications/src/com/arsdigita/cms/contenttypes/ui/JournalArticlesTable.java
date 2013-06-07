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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.ArticleInJournalCollection;
import com.arsdigita.cms.contenttypes.Journal;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class JournalArticlesTable
        extends Table
        implements TableActionListener {

    private static final Logger s_log = Logger.getLogger(
            JournalArticlesTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;

    public JournalArticlesTable(ItemSelectionModel itemModel) {
        super();
        m_itemModel = itemModel;

        setEmptyView(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.journal.no_articles")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.journal.article").localize(),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.journal.article.remove").localize(),
                TABLE_COL_DEL));
        columnModel.add(new TableColumn(
                2,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.journal.article.up").localize(),
                TABLE_COL_UP));
        columnModel.add(new TableColumn(
                3,
                PublicationGlobalizationUtil.globalize(
                "publications.ui.journal.article.down").localize(),
                TABLE_COL_DOWN));

        setModelBuilder(new JournalArticlesTableModelBuilder(itemModel));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());
        columnModel.get(2).setCellRenderer(new UpCellRenderer());
        columnModel.get(3).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);
    }

    private class JournalArticlesTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public JournalArticlesTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            Journal collectedVolume =
                    (Journal) m_itemModel.getSelectedObject(
                    state);
            return new JournalArticlesTableModel(table,
                                                 state,
                                                 collectedVolume);
        }
    }

    private class JournalArticlesTableModel implements TableModel {

        private Table m_table;
        private ArticleInJournalCollection m_articles;
        private ArticleInJournal m_article;

        private JournalArticlesTableModel(Table table,
                                          PageState state,
                                          Journal journal) {
            m_table = table;
            m_articles = journal.getArticles();
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((m_articles != null) && m_articles.next()) {
                m_article = m_articles.getArticle();
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return m_article.getTitle();
                case 1:
                    return PublicationGlobalizationUtil.globalize(
                            "publications.ui.journal.article.remove").
                            localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return m_article.getID();
        }
    }

    private class EditCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int col) {
            com.arsdigita.cms.SecurityManager securityManager =
                                              Utilities.getSecurityManager(state);
            Journal journal = (Journal) m_itemModel.getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    journal);

            if (canEdit) {
                ArticleInJournal article;
                try {
                    article = new ArticleInJournal((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }
                ContentSection section = article.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link =
                     new Link(String.format("%s (%s)",
                                            value.toString(),
                                            article.getLanguage()),
                              resolver.generateItemURL(state,
                                                       article,
                                                       section,
                                                       article.getVersion()));
                return link;
            } else {
                ArticleInJournal article;
                try {
                    article = new ArticleInJournal((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }

                Label label = new Label(String.format("%s (%s)", 
                                                      value.toString(),
                                                      article.getLanguage()));
                return label;
            }
        }
    }

    private class DeleteCellRenderer extends LockableImpl implements
            TableCellRenderer {

        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);
            Journal journal = (Journal) m_itemModel.getSelectedObject(
                    state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                                                        journal);
            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) PublicationGlobalizationUtil.
                        globalize(
                        "cms.contenttypes.ui.journal.articles.confirm_delete").
                        localize());
                return link;
            } else {
                return new Label(value.toString());
            }
        }
    }

    private class UpCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {

            if (0 == row) {
                Label label = new Label("");
                return label;
            } else {
                ControlLink link = new ControlLink(
                        (String) PublicationGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.journal.articles.up").
                        localize());
                return link;
            }
        }
    }

    private class DownCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {

            Journal journal = (Journal) m_itemModel.getSelectedObject(state);
            ArticleInJournalCollection articles = journal.getArticles();

            if ((articles.size() - 1) == row) {
                Label label = new Label("");
                return label;
            } else {
                ControlLink link = new ControlLink(
                        (String) PublicationGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.journal.articles.down").
                        localize());
                return link;
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        s_log.debug("Cell selected.");
        PageState state = event.getPageState();
        s_log.debug(String.format("RowKey = %s", event.getRowKey().toString()));
        s_log.debug(String.format("Selected column: %d", event.getColumn().
                intValue()));

        ArticleInJournal article = new ArticleInJournal(new BigDecimal(event.
                getRowKey().
                toString()));

        Journal journal = (Journal) m_itemModel.getSelectedObject(state);

        ArticleInJournalCollection articles = journal.getArticles();

        TableColumn col = getColumnModel().get(event.getColumn().intValue());

        if (col.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (col.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            s_log.debug("Removing article assoc...");
            journal.removeArticle(article);
        } else if (col.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            articles.swapWithPrevious(article);
        } else if (col.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            articles.swapWithNext(article);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do
    }
}
