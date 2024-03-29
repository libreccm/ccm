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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.ProceedingsOrganizerCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;

import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 */
public class ProceedingsOrganizerTable
    extends Table
    implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel itemModel;

    public ProceedingsOrganizerTable(final ItemSelectionModel itemModel) {
        super();

        this.itemModel = itemModel;

        setEmptyView(new Label(PublicationGlobalizationUtil.globalize(
            "publications.ui.proceedings.organizer.none")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            0,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.proceedings.organizer")),
            TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
            1,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.proceedings.organizer.remove")),
            TABLE_COL_DEL));
        columnModel.add(new TableColumn(
            2,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.proceedings.organizer.up")),
            TABLE_COL_UP));
        columnModel.add(new TableColumn(
            3,
            new Label(PublicationGlobalizationUtil.globalize(
                    "publications.ui.proceedings.organizer.down")),
            TABLE_COL_DOWN));

        setModelBuilder(new ProceedingsOrganizerTableModelBuilder(itemModel));
        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());
        columnModel.get(2).setCellRenderer(new UpCellRenderer());
        columnModel.get(3).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);
    }

    private class ProceedingsOrganizerTableModelBuilder
        extends LockableImpl
        implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public ProceedingsOrganizerTableModelBuilder(
            final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            Proceedings proceedings = (Proceedings) itemModel.getSelectedObject(
                state);
            return new ProceedingsOrganizerTableModel(table, state, proceedings);
        }

    }

    private class ProceedingsOrganizerTableModel implements TableModel {

        private Table table;
        private ProceedingsOrganizerCollection organizers;
        private GenericOrganizationalUnit organizer;

        public ProceedingsOrganizerTableModel(final Table table,
                                              final PageState state,
                                              final Proceedings proceedings) {
            this.table = table;
            organizers = proceedings.getOrganizers();
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((organizers != null) && organizers.next()) {
                organizer = organizers.getOrganizer();
                ret = true;
            } else {
                ret = false;
            }
            return ret;
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return organizer.getTitle();
                case 1:
                    return new Label(PublicationGlobalizationUtil.globalize(
                        "publications.ui.proceedings.organizer.remove"));
                case 2:
                    return new Label(PublicationGlobalizationUtil.globalize(
                        "publications.ui.proceedings.organizer.up"));
                case 3:
                    return new Label(PublicationGlobalizationUtil.globalize(
                        "publications.ui.proceedings.organizer.down"));
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return organizer.getID();
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
            com.arsdigita.cms.SecurityManager securityManager = CMS
                .getSecurityManager(state);
            Proceedings proceedings = (Proceedings) itemModel.getSelectedObject(
                state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                                                        proceedings);
            if (canEdit) {
                GenericOrganizationalUnit organizer;
                try {
                    organizer = new GenericOrganizationalUnit(
                        (BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                ContentSection section = organizer.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link = new Link(value.toString(),
                                     resolver.generateItemURL(state,
                                                              organizer,
                                                              section,
                                                              organizer
                                                              .getVersion()));

                return link;
            } else {
                GenericOrganizationalUnit organizer;
                try {
                    organizer = new GenericOrganizationalUnit(
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
            com.arsdigita.cms.SecurityManager securityManager = Utilities
                .getSecurityManager(state);
            Proceedings proceedings = (Proceedings) itemModel.getSelectedObject(
                state);

            boolean canEdit = securityManager.canAccess(
                state.getRequest(),
                com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                proceedings);

            if (canEdit) {
                ControlLink link = new ControlLink((Label) value);
                link.setConfirmation(PublicationGlobalizationUtil.globalize(
                    "publications.ui.proceedings.organizer.remove.confirm"));
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
        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int col) {
            if (0 == row) {
                Label label = new Label();
                return label;
            } else {
                ControlLink link = new ControlLink((Label) value);
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

            Proceedings proceedings = (Proceedings) itemModel
                .getSelectedObject(state);
            ProceedingsOrganizerCollection organizers = proceedings
                .getOrganizers();

            if ((organizers.size() - 1) == row) {
                Label label = new Label();
                return label;
            } else {
                ControlLink link = new ControlLink((Label) value);
                return link;
            }
        }

    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        PageState state = event.getPageState();

        final GenericOrganizationalUnit organizer
                                            = new GenericOrganizationalUnit(
                new BigDecimal(event.getRowKey().toString()));

        final Proceedings proceedings = (Proceedings) itemModel
            .getSelectedObject(
                state);

        final ProceedingsOrganizerCollection organizers = proceedings
            .getOrganizers();

        final TableColumn column = getColumnModel().get(event.getColumn()
            .intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            proceedings.removeOrganizer(organizer);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            organizers.swapWithPrevious(organizer);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            organizers.swapWithNext(organizer);
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing to do.
    }

}
