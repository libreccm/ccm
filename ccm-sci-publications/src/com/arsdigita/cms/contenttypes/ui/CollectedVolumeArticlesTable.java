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
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolumeCollection;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class CollectedVolumeArticlesTable
    extends Table
    implements TableActionListener {

    private static final Logger s_log = Logger.getLogger(
        CollectedVolumeArticlesTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;

    public CollectedVolumeArticlesTable(ItemSelectionModel itemModel) {
        super();
        m_itemModel = itemModel;

        setEmptyView(
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.collected_volume.no_articles")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
            0,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.collected_volume.article")),
            TABLE_COL_EDIT));
        colModel.add(new TableColumn(
            1,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.collected_volume.article.remove")),
            TABLE_COL_DEL));
        colModel.add(new TableColumn(
            2,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.collected_volume.article.up")),
            TABLE_COL_UP));
        colModel.add(new TableColumn(
            3,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.collected_volume.article.down")),
            TABLE_COL_DOWN));

        setModelBuilder(
            new CollectedVolumeArticlesTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());
        colModel.get(2).setCellRenderer(new UpCellRenderer());
        colModel.get(3).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);
    }

    private class CollectedVolumeArticlesTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public CollectedVolumeArticlesTableModelBuilder(
            ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            CollectedVolume collectedVolume = (CollectedVolume) m_itemModel.getSelectedObject(
                state);
            return new CollectedVolumeArticlesTableModel(table,
                                                         state,
                                                         collectedVolume);
        }

    }

    private class CollectedVolumeArticlesTableModel implements TableModel {

        private Table m_table;
        private ArticleInCollectedVolumeCollection m_articles;
        private ArticleInCollectedVolume m_article;

        private CollectedVolumeArticlesTableModel(
            Table table,
            PageState state,
            CollectedVolume collectedVolume) {
            m_table = table;
            m_articles = collectedVolume.getArticles();
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
                    return new Label(PublicationGlobalizationUtil.globalize(
                        "publications.ui.collected_volume.article.remove"));
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
            SecurityManager securityManager = Utilities.getSecurityManager(state);
            CollectedVolume collectedVolume = (CollectedVolume) m_itemModel.
                getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                SecurityManager.EDIT_ITEM,
                collectedVolume);

            if (canEdit) {
                ArticleInCollectedVolume article;
                try {
                    article = new ArticleInCollectedVolume((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }

                ContentSection section = article.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link = new Link(value.toString(),
                                     resolver.generateItemURL(state,
                                                              article,
                                                              section,
                                                              article.getVersion()));

                return link;
            } else {
                ArticleInCollectedVolume article;
                try {
                    article = new ArticleInCollectedVolume((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }

                Label label = new Label(value.toString());
                return label;
            }
        }

    }

    private class DeleteCellRenderer
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
            SecurityManager securityManager = Utilities.getSecurityManager(state);
            CollectedVolume collectedVolume = (CollectedVolume) m_itemModel.getSelectedObject(
                state);

            boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                SecurityManager.DELETE_ITEM,
                collectedVolume);

            if (canEdit) {
                ControlLink link = new ControlLink((Label) value);
                link.setConfirmation(PublicationGlobalizationUtil.globalize(
                    "publications.ui.collected_volume.articles.confirm_remove"));
                return link;
            } else {
                return new Label("");
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
                s_log.debug("Row is first row in table, don't show up link");
                Label label = new Label();
                return label;
            } else {
                ControlLink link = new ControlLink("up");
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

            CollectedVolume collectedVolume = (CollectedVolume) m_itemModel.getSelectedObject(
                state);
            ArticleInCollectedVolumeCollection articles = collectedVolume.getArticles();

            if ((articles.size() - 1) == row) {
                s_log.debug("Row is last row in table, don't show down link");
                Label label = new Label();
                return label;
            } else {
                ControlLink link = new ControlLink("down");
                return link;
            }
        }

    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        ArticleInCollectedVolume article = new ArticleInCollectedVolume(
            new BigDecimal(event.getRowKey().toString()));

        CollectedVolume collectedVolume = (CollectedVolume) m_itemModel.getSelectedObject(state);

        ArticleInCollectedVolumeCollection articles = collectedVolume.getArticles();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            collectedVolume.removeArticle(article);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            articles.swapWithPrevious(article);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            articles.swapWithNext(article);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do.
    }

}
