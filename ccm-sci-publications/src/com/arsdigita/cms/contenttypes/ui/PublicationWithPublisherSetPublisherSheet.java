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
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationWithPublisherSetPublisherSheet
        extends Table
        implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel itemModel;

    public PublicationWithPublisherSetPublisherSheet(
            final ItemSelectionModel itemModel) {
        super();
        this.itemModel = itemModel;

        setEmptyView(new Label(PublicationGlobalizationUtil.globalize(
                "publications.ui.with_publisher.publisher.none")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                new Label(PublicationGlobalizationUtil.globalize(
                          "publications.ui.with_publisher.publisher")),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                new Label(PublicationGlobalizationUtil.globalize(
                          "publications.ui.with_publisher.publisher.remove")),
                TABLE_COL_DEL));

        setModelBuilder(new PublicationWithPublisherSetPublisherSheetModelBuilder(
                itemModel));
        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer((new DeleteCellRenderer()));

        addTableActionListener(this);
    }

    private class PublicationWithPublisherSetPublisherSheetModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public PublicationWithPublisherSetPublisherSheetModelBuilder(
                final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            PublicationWithPublisher publication =
                                     (PublicationWithPublisher) itemModel.
                    getSelectedObject(state);
            return new PublicationWithPublisherSetPublisherSheetModel(table,
                                                                      state,
                                                                      publication);
        }
    }

    private class PublicationWithPublisherSetPublisherSheetModel
            implements TableModel {

        private final Table table;
        private final Publisher publisher;
        private boolean m_done;

        public PublicationWithPublisherSetPublisherSheetModel(final Table table,
                                                              final PageState state,
                                                              final PublicationWithPublisher publication) {
            this.table = table;
            publisher = publication.getPublisher();
            if (publisher == null) {
                m_done = false;
            } else {
                m_done = true;
            }
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if (m_done) {
                ret = true;
                m_done = false;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return publisher.getTitle();
                case 1:
                    return new Label(PublicationGlobalizationUtil.globalize(
                            "publications.ui.with_publisher.publisher.remove"));
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return publisher.getID();
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
            com.arsdigita.cms.SecurityManager securityManager =
                                              CMS.getSecurityManager(state);
            PublicationWithPublisher publication =
                                     (PublicationWithPublisher) itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                                                        publication);
            if (canEdit) {
                Publisher publisher;
                try {
                    publisher = new Publisher(
                            (BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                ContentSection section = publisher.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link =
                     new Link(value.toString(),
                              resolver.generateItemURL(state,
                                                       publisher,
                                                       section,
                                                       publisher.getVersion()));

                return link;
            } else {
                Publisher publisher;
                try {
                    publisher = new Publisher(
                            (BigDecimal) key);
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
                                      int col) {
            com.arsdigita.cms.SecurityManager securityManager =
                                              CMS.getSecurityManager(state);
            PublicationWithPublisher publication =
                                     (PublicationWithPublisher) itemModel.
                    getSelectedObject(
                    state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                    publication);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation(PublicationGlobalizationUtil.globalize(
                        "publications.ui.with_publisher.publisher.remove.confirm"));
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        PageState state = event.getPageState();

        PublicationWithPublisher publication =
                                 (PublicationWithPublisher) itemModel.
                getSelectedObject(state);

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            publication.setPublisher(null);
        }

    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing to do
    }
}
