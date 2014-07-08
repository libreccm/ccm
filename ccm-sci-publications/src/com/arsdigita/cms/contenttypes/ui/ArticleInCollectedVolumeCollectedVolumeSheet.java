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
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 * Sheet which displays the collected volume to which an article in a collected
 * volume is associated to.
 *
 * @author Jens Pelzetter
 */
public class ArticleInCollectedVolumeCollectedVolumeSheet
        extends Table
        implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel itemModel;

    public ArticleInCollectedVolumeCollectedVolumeSheet(
            ItemSelectionModel itemModel) {
        super();
        this.itemModel = itemModel;

        setEmptyView(
                new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleInCollectedVolume.collectedVolume.none")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleInCollectedVolume.collectedVolume")),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.articleInCollectedVolume.collectedVolume.remove")),
                TABLE_COL_DEL));

        setModelBuilder(
                new ArticleInCollectedVolumeCollectedVolumeSheetModelBuilder(
                itemModel));
        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class ArticleInCollectedVolumeCollectedVolumeSheetModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public ArticleInCollectedVolumeCollectedVolumeSheetModelBuilder(
                ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            ArticleInCollectedVolume article =
                                     (ArticleInCollectedVolume) itemModel.
                    getSelectedObject(state);
            return new ArticleInCollectedVolumeCollectedVolumeSheetModel(table,
                                                                         state,
                                                                         article);

        }

    }

    private class ArticleInCollectedVolumeCollectedVolumeSheetModel
            implements TableModel {

        private Table table;
        private CollectedVolume collectedVolume;
        private boolean done;

        public ArticleInCollectedVolumeCollectedVolumeSheetModel(Table table,
                                                                 PageState state,
                                                                 ArticleInCollectedVolume article) {
            this.table = table;
            this.collectedVolume = article.getCollectedVolume();
            if (collectedVolume == null) {
                done = false;
            } else {
                done = true;
            }
        }

        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if (done) {
                ret = true;
                done = false;
            } else {
                ret = false;
            }

            return ret;
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return collectedVolume.getTitle();
                case 1:
                    return PublicationGlobalizationUtil.globalize(
                            "publications.ui.articleInCollectedVolume.collectedVolume.remove").
                            localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return collectedVolume.getID();
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
                                      int column) {
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            ArticleInCollectedVolume article =
                                     (ArticleInCollectedVolume) itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        SecurityManager.EDIT_ITEM,
                                                        article);

            if (canEdit) {
                CollectedVolume collectedVolume;
                try {
                    collectedVolume = new CollectedVolume((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    return new Label(value.toString());
                }
                ContentSection section = collectedVolume.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link = new Link(value.toString(),
                                     resolver.generateItemURL(state,
                                                              collectedVolume,
                                                              section,
                                                              collectedVolume.
                        getVersion()));
                return link;
            } else {
                CollectedVolume collectedVolume;
                try {
                    collectedVolume = new CollectedVolume((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
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
                                      int column) {
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            ArticleInCollectedVolume article =
                                     (ArticleInCollectedVolume) itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    article);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation(
                        PublicationGlobalizationUtil.globalize(
                        "publications.ui.articleInCollectedVolume.collectedVolume."
                        + "confirm_remove"));
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }

    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        ArticleInCollectedVolume article = (ArticleInCollectedVolume) itemModel.
                getSelectedObject(state);

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            article.setCollectedVolume(null);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do
    }

}
