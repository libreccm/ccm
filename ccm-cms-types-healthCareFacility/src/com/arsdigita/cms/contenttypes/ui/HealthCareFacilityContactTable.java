/*
 * Copyright (C) 2008 Sören Bernstein All Rights Reserved.
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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.HealthCareFacilityContactCollection;
import com.arsdigita.cms.contenttypes.HealthCareFacility;
import com.arsdigita.cms.contenttypes.util.HealthCareFacilityGlobalizationUtil;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 * Lists all existing contact entries for a selected contact.
 *
 * @author Sören Bernstein (quasimodo) quasi@barkhof.uni-bremen.de
 */
public class HealthCareFacilityContactTable extends Table implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel m_itemModel;

    /**
     * Creates a new instance of HealthCareFacilityHealthCareFacilityTable
     */
    public HealthCareFacilityContactTable(final ItemSelectionModel itemModel) {

        super();
        this.m_itemModel = itemModel;

        // if table is empty:
        setEmptyView(new Label(HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.contacts.none")));
        TableColumnModel tab_model = getColumnModel();

        // define columns
        tab_model.add(new TableColumn(0, HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.contact.order").localize(), TABLE_COL_EDIT));
        tab_model.add(new TableColumn(1, HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.contact.type").localize()));
        tab_model.add(new TableColumn(2, HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.contact.title").localize()));
        tab_model.add(new TableColumn(3, HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.contact.action").localize(), TABLE_COL_DEL));

        setModelBuilder(new HealthCareFacilityTableModelBuilder(itemModel));

        tab_model.get(0).setCellRenderer(new EditCellRenderer());
        tab_model.get(3).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);

    }

    /**
     * XXXX
     *
     */
    private class HealthCareFacilityTableModelBuilder extends LockableImpl implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public HealthCareFacilityTableModelBuilder(ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            HealthCareFacility healthCareFacility = (HealthCareFacility) m_itemModel.getSelectedObject(state);
            return new HealthCareFacilityTableModel(table, state, healthCareFacility);
        }
    }

    /**
     * XXX
     *
     */
    private class HealthCareFacilityTableModel implements TableModel {

        final private int MAX_DESC_LENGTH = 25;
        private Table m_table;
        private HealthCareFacilityContactCollection m_contactCollection;
        private com.arsdigita.cms.basetypes.Contact m_contact;

        private HealthCareFacilityTableModel(Table t, PageState ps, HealthCareFacility healthCareFacility) {
            m_table = t;
            m_contactCollection = healthCareFacility.getContacts();
        }

        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        /**
         * Check collection for the existence of another row.
         *
         * If exists, fetch the value of current HealthCareFacilityEntryCollection object
         * into m_comntact class variable.
         */
        public boolean nextRow() {

            if (m_contactCollection != null && m_contactCollection.next()) {
                m_contact = m_contactCollection.getContact();
                return true;

            } else {

                return false;

            }
        }

        /**
         * Return the
         * @see com.arsdigita.bebop.table.TableModel#getElementAt(int)
         */
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return m_contactCollection.getContactOrder();
                case 1:
                    return (String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.contactType.key." + m_contactCollection.getContactType()).localize();
                case 2:
                    return m_contact.getTitle();
//                case 2:
//                    return (m_healthCareFacilityEntry.getDescription() != null && m_healthCareFacilityEntry.getDescription().length() > MAX_DESC_LENGTH)
//                                ? m_healthCareFacilityEntry.getDescription().substring(0, MAX_DESC_LENGTH)
//                                : m_healthCareFacilityEntry.getDescription();
                case 3:
                    return GlobalizationUtil.globalize("cms.ui.delete").localize();
                default:
                    return null;
            }
        }

        /**
         *
         * @see com.arsdigita.bebop.table.TableModel#getKeyAt(int)
         */
        public Object getKeyAt(int columnIndex) {
            return m_contact.getID();
        }
    }

    /**
     * Check for the permissions to edit item and put either a Label or
     * a ControlLink accordingly.
     */
    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                boolean isSelected, Object key,
                int row, int column) {

            SecurityManager sm = Utilities.getSecurityManager(state);
            HealthCareFacility healthCareFacility = (HealthCareFacility) m_itemModel.getSelectedObject(state);

            boolean canEdit = sm.canAccess(state.getRequest(),
                    SecurityManager.EDIT_ITEM,
                    healthCareFacility);
            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                return link;
            } else {
                return new Label(value.toString());
            }
        }
    }

    /**
     * Check for the permissions to delete item and put either a Label or
     * a ControlLink accordingly.
     */
    private class DeleteCellRenderer extends LockableImpl implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                boolean isSelected, Object key,
                int row, int column) {

            SecurityManager sm = Utilities.getSecurityManager(state);
            HealthCareFacility healthCareFacility = (HealthCareFacility) m_itemModel.getSelectedObject(state);

            boolean canDelete = sm.canAccess(state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    healthCareFacility);
            if (canDelete) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) HealthCareFacilityGlobalizationUtil.globalize("cms.contenttypes.ui.healthCareFacility.confirm_delete").localize());
                return link;
            } else {
                return new Label(value.toString());
            }
        }
    }

    /**
     * Provide implementation to TableActionListener method.
     * Code that comes into picture when a link on the table is clicked.
     * Handles edit and delete event.
     */
    public void cellSelected(TableActionEvent evt) {

        PageState state = evt.getPageState();

        // Get selected Contact
        com.arsdigita.cms.basetypes.Contact contact =
                new com.arsdigita.cms.basetypes.Contact(new BigDecimal(evt.getRowKey().toString()));

        // Get HealthCareFacility
        HealthCareFacility healthCareFacility = (HealthCareFacility) m_itemModel.getSelectedObject(state);

        // Get selected column
        TableColumn col = getColumnModel().get(evt.getColumn().intValue());

        // Edit
        if (col.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        }

        // Delete
        if (col.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            healthCareFacility.removeContactEntry(contact);
        }

    }

    /**
     * provide Implementation to TableActionListener method.
     * Does nothing in our case.
     */
    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
