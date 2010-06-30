/*
 * Copyright (C) 2010 Sören Bernstein All Rights Reserved.
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
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactTypeCollection;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 * Lists all existing contact entries for a selected contact.
 *
 * @author Sören Bernstein (quasimodo) quasi@barkhof.uni-bremen.de
 */
public class GenericPersonContactTable extends Table implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel m_itemModel;

    /**
     * Creates a new instance of GenericPersonGenericPersonTable
     */
    public GenericPersonContactTable(final ItemSelectionModel itemModel) {

        super();
        this.m_itemModel = itemModel;

        // if table is empty:
        setEmptyView(new Label(ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.contacts.none")));
        TableColumnModel tab_model = getColumnModel();

        // define columns
        tab_model.add(new TableColumn(0, ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.contact.order").localize(), TABLE_COL_EDIT));
        tab_model.add(new TableColumn(1, ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.contact.type").localize()));
        tab_model.add(new TableColumn(2, ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.contact.title").localize()));
        tab_model.add(new TableColumn(3, ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.contact.action").localize(), TABLE_COL_DEL));

        setModelBuilder(new GenericPersonTableModelBuilder(itemModel));

        tab_model.get(0).setCellRenderer(new EditCellRenderer());
        tab_model.get(3).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);

    }

    /**
     * XXXX
     *
     */
    private class GenericPersonTableModelBuilder extends LockableImpl implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public GenericPersonTableModelBuilder(ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            GenericPerson person = (GenericPerson) m_itemModel.getSelectedObject(state);
            return new GenericPersonTableModel(table, state, person);
        }
    }

    /**
     * XXX
     *
     */
    private class GenericPersonTableModel implements TableModel {

        final private int MAX_DESC_LENGTH = 25;
        private Table m_table;
        private GenericPersonContactCollection m_contactCollection;
        private GenericContact m_contact;
        private GenericContactTypeCollection contacttypes = new GenericContactTypeCollection();


        private GenericPersonTableModel(Table t, PageState ps, GenericPerson person) {
            m_table = t;
            m_contactCollection = person.getContacts();
        }

        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        /**
         * Check collection for the existence of another row.
         *
         * If exists, fetch the value of current GenericPersonEntryCollection object
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
                    return contacttypes.getContactType(m_contactCollection.getContactType(),
                                                       DispatcherHelper.getNegotiatedLocale().getLanguage());
                case 2:
                    return m_contact.getTitle();
//                case 2:
//                    return (m_personEntry.getDescription() != null && m_personEntry.getDescription().length() > MAX_DESC_LENGTH)
//                                ? m_personEntry.getDescription().substring(0, MAX_DESC_LENGTH)
//                                : m_personEntry.getDescription();
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
            GenericPerson person = (GenericPerson) m_itemModel.getSelectedObject(state);

            boolean canEdit = sm.canAccess(state.getRequest(),
                    SecurityManager.EDIT_ITEM,
                    person);
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
            GenericPerson person = (GenericPerson) m_itemModel.getSelectedObject(state);

            boolean canDelete = sm.canAccess(state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    person);
            if (canDelete) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.person.confirm_delete").localize());
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

        // Get selected GenericContact
        GenericContact contact =  new GenericContact(new BigDecimal(evt.getRowKey().toString()));

        // Get GenericPerson
        GenericPerson person = (GenericPerson) m_itemModel.getSelectedObject(state);

        // Get selected column
        TableColumn col = getColumnModel().get(evt.getColumn().intValue());

        // Edit
        if (col.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        }

        // Delete
        if (col.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            person.removeContact(contact);
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
