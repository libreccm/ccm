/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contentassets.PublicationTypeAsset;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.LockableImpl;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublicationTypeAssetTable extends Table {

    private final static String TABLE_COL_TYPE = "table_col_type";
    private final static String TABLE_COL_ISBN = "table_col_isbn";
    private final static String TABLE_COL_MISC = "table_col_misc";
    private final static String TABLE_COL_EDIT = "table_col_edit";
    private final static String TABLE_COL_DEL = "table_col_del";
    private final PublicationTypeAssetStep parent;
    private final ItemSelectionModel itemModel;
    private final ACSObjectSelectionModel typeModel;

    public PublicationTypeAssetTable(final ItemSelectionModel itemModel,
                                     final ACSObjectSelectionModel typeModel,
                                     final PublicationTypeAssetStep parent) {
        super();

        this.parent = parent;
        this.itemModel = itemModel;
        this.typeModel = typeModel;

        setEmptyView(new Label(PublicationTypeAssetGlobalizationUtil.globalize(
                "scipublications.publication_type_asset.none")));

        final TableColumnModel columnModel = getColumnModel();

        columnModel.add(new TableColumn(
                0,
                PublicationTypeAssetGlobalizationUtil.globalize("scipublications.publication_type_asset.type"),
                TABLE_COL_TYPE));
        columnModel.add(new TableColumn(
                1,
                PublicationTypeAssetGlobalizationUtil.globalize("scipublications.publication_type_asset.isbn"),
                TABLE_COL_ISBN));
        columnModel.add(new TableColumn(
                2,
                PublicationTypeAssetGlobalizationUtil.globalize("scipublications.publication_type_asset.misc"),
                TABLE_COL_MISC));
        columnModel.add(new TableColumn(
                3,
                PublicationTypeAssetGlobalizationUtil.globalize("scipublications.publication_type_asset.edit"),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                4,
                PublicationTypeAssetGlobalizationUtil.globalize("scipublications.publication_type_asset.delete"),
                TABLE_COL_DEL));

        setModelBuilder(new ModelBuilder(itemModel));

        columnModel.get(3).setCellRenderer(new EditCellRenderer());
        columnModel.get(4).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(new ActionListener());
    }

    private class ModelBuilder extends LockableImpl implements TableModelBuilder {

        private final ItemSelectionModel itemModel;

        public ModelBuilder(final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            final Publication publication = (Publication) itemModel.getSelectedItem(state);

            return new Model(table, state, publication);
        }

    }

    private class Model implements TableModel {

        private final Table table;
        private final DataCollection typeAssets;

        public Model(final Table table, final PageState state, final Publication publication) {
            this.table = table;

            typeAssets = PublicationTypeAsset.getPublicationTypeAssets(publication);
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            return (typeAssets != null) && typeAssets.next();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return typeAssets.get(PublicationTypeAsset.PUBLICATION_TYPE);
                case 1:
                    return typeAssets.get(PublicationTypeAsset.ISBN);
                case 2:
                    return typeAssets.get(PublicationTypeAsset.MISC);
                case 3:
                    return PublicationTypeAssetGlobalizationUtil.globalize(
                            "scipublications.publication_type_asset.edit");
                case 4:
                    return PublicationTypeAssetGlobalizationUtil.globalize(
                            "scipublications.publication_type_asset.delete");
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return typeAssets.get(ContentItem.ID);
        }

    }

    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        public EditCellRenderer() {
            //Nothing
        }

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final Publication publication = (Publication) itemModel.getSelectedItem(state);

            final boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                              com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                                                              publication);

            if (canEdit) {
                final ControlLink link;
                if (value instanceof GlobalizedMessage) {
                    link = new ControlLink(new Label((GlobalizedMessage) value));
                } else if (value == null) {
                    return new Label("???");
                } else {
                    link = new ControlLink(value.toString());
                }
                return link;
            } else {
                final Label label;
                if (value instanceof GlobalizedMessage) {
                    label = new Label((GlobalizedMessage) value);
                } else if (value == null) {
                    return new Label("???");
                } else {
                    label = new Label(value.toString());
                }
                return label;
            }
        }

    }

    private class DeleteCellRenderer extends LockableImpl implements TableCellRenderer {

        public DeleteCellRenderer() {
            //Nothing
        }

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final Publication publication = (Publication) itemModel.getSelectedItem(state);

            final boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                              com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                                                              publication);

            if (canEdit) {
                final ControlLink link;
                if (value instanceof GlobalizedMessage) {
                    link = new ControlLink(new Label((GlobalizedMessage) value));
                } else {
                    link = new ControlLink(value.toString());
                }
                link.setConfirmation(PublicationTypeAssetGlobalizationUtil.globalize(
                        "scipublications.publication_type_asset.delete.confirm"));
                return link;
            } else {
                final Label label;
                if (value instanceof GlobalizedMessage) {
                    label = new Label((GlobalizedMessage) value);
                } else {
                    label = new Label(value.toString());
                }
                return label;
            }

        }

    }

    private class ActionListener implements TableActionListener {

        public ActionListener() {
            //Nothing for now
        }

        @Override
        public void cellSelected(final TableActionEvent event) {
            final PageState state = event.getPageState();

            final PublicationTypeAsset asset = new PublicationTypeAsset(new BigDecimal(event.getRowKey().toString()));

            final TableColumn column = getColumnModel().get(event.getColumn().intValue());

            if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
                typeModel.setSelectedObject(state, asset);
                parent.setAddVisible(state);
            } else if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
                asset.delete();
            }
        }

        @Override
        public void headSelected(final TableActionEvent event) {
            //Nothing
        }

    }
}
