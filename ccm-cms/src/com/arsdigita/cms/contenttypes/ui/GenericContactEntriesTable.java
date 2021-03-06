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
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactEntry;
import com.arsdigita.cms.contenttypes.GenericContactEntryCollection;
import com.arsdigita.cms.contenttypes.GenericContactEntryKeys;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import com.arsdigita.bebop.ParameterSingleSelectionModel;

/**
 * Lists all existing contact entries for a selected contact.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class GenericContactEntriesTable extends Table implements TableActionListener {

    private final int COL_KEY = 0;
    private final int COL_VALUE = 1;
    private final int COL_DESC = 2;
    private final int COL_EDIT = 3;
    private final int COL_DEL = 4;

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel m_itemModel;
    private GenericContactEntriesEditor m_editor;
    private ParameterSingleSelectionModel m_selectedEntry;

    /**
     * Creates a new instance of GenericContactEntriesTable.
     *
     * @param itemModel
     */
    public GenericContactEntriesTable(final ItemSelectionModel itemModel) {

        super();
        this.m_itemModel = itemModel;

        // if table is empty:
        setEmptyView(new Label(ContenttypesGlobalizationUtil
                .globalize("cms.contenttypes.ui.contact.contactEntry.none")));
        TableColumnModel tab_model = getColumnModel();

        // define columns
        tab_model.add(new TableColumn(
                COL_KEY,
                new Label(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.contact.contactEntry.key"))));
        tab_model.add(new TableColumn(
                COL_VALUE,
                new Label(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.contact.contactEntry.value"))));
        tab_model.add(new TableColumn(
                COL_DESC,
                new Label(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.contact.contactEntry.description"))));
        tab_model.add(new TableColumn(
                            COL_EDIT,
                            new Label(ContenttypesGlobalizationUtil.globalize(
                                "cms.contenttypes.ui.contact.contactEntry.edit"))));
        tab_model.add(new TableColumn(
                COL_DEL,
                new Label(ContenttypesGlobalizationUtil.globalize(
                    "cms.contenttypes.ui.contact.contactEntry.action")),
                TABLE_COL_DEL));

        setModelBuilder(new ContactTableModelBuilder(itemModel));

        tab_model.get(COL_EDIT).setCellRenderer(new EditCellRenderer());
        tab_model.get(COL_DEL).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);

    }

    public GenericContactEntriesTable(final ItemSelectionModel itemModel,
                                      final GenericContactEntriesEditor editor,
                                      final ParameterSingleSelectionModel selectedEntry) {
        this(itemModel);

        m_editor = editor;
        m_selectedEntry = selectedEntry;
    }

    /**
     * XXXX
     *
     */
    private class ContactTableModelBuilder extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public ContactTableModelBuilder(ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {

            table.getRowSelectionModel().clearSelection(state);

            GenericContact contact = (GenericContact) m_itemModel.getSelectedObject(state);
            return new ContactTableModel(table, state, contact);
        }

    }

    /**
     * XXX
     *
     */
    private class ContactTableModel implements TableModel {

        final private int MAX_DESC_LENGTH = 25;
        private Table m_table;
        private GenericContactEntryCollection m_contactEntryCollection;
        private GenericContactEntry m_contactEntry;

        private ContactTableModel(Table t, PageState ps, GenericContact contact) {
            m_table = t;
            m_contactEntryCollection = contact.getContactEntries();
        }

        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        /**
         * Check collection for the existence of another row.
         *
         * If exists, fetch the value of current GenericContactEntryCollection object
         * into m_contactEntry class variable.
         */
        public boolean nextRow() {

            if (m_contactEntryCollection != null && m_contactEntryCollection.next()) {
                m_contactEntry = m_contactEntryCollection.getContactEntry();
                return true;

            } else {

                return false;

            }
        }

        /**
         * Return the
         * @see com.arsdigita.bebop.table.TableModel#getElementAt(int)
         */
        @Override
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case COL_KEY:
                    GenericContactEntryKeys keys = new GenericContactEntryKeys(m_contactEntry.getKey());
                    keys.addLanguageFilter(
                            GlobalizationHelper.getNegotiatedLocale()
                            .getLanguage());
                    if (keys.next()) {
                        Object key = keys.getName();

                        // Close Collection to prevent open ResultSet
                        keys.close();

                        return key;
                    }
                    return m_contactEntry.getKey();
                case COL_VALUE:
                    return m_contactEntry.getValue();
                case COL_DESC:
                    return (m_contactEntry.getDescription() != null
                            && m_contactEntry.getDescription().length() > MAX_DESC_LENGTH)
                           ? m_contactEntry.getDescription().substring(
                            0, MAX_DESC_LENGTH)
                           : m_contactEntry.getDescription();
                case COL_EDIT:
                    return new Label(GlobalizationUtil.globalize("cms.ui.edit"));
                case COL_DEL:
                    return new Label(GlobalizationUtil.globalize("cms.ui.delete"));
                default:
                    return null;
            }
        }

        /**
         *
         * @see com.arsdigita.bebop.table.TableModel#getKeyAt(int)
         */
        public Object getKeyAt(int columnIndex) {
            return m_contactEntry.getID();
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
            GenericContact contact = (GenericContact) m_itemModel.getSelectedObject(state);

            boolean canEdit = sm.canAccess(state.getRequest(),
                                           SecurityManager.EDIT_ITEM,
                                           contact);
            if (canEdit) {
                ControlLink link = new ControlLink((Label)value);
                return link;
            } else {
                return (Label) value;
            }
        }

    }

    /**
     * Check for the permissions to delete item and put either a Label or
     * a ControlLink accordingly.
     */
    private class DeleteCellRenderer extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {

            SecurityManager sm = Utilities.getSecurityManager(state);
            GenericContact contact = (GenericContact) m_itemModel.getSelectedObject(state);

            boolean canDelete = sm.canAccess(state.getRequest(),
                                             SecurityManager.DELETE_ITEM,
                                             contact);
            if (canDelete) {
                ControlLink link = new ControlLink((Label)value);
                link.setConfirmation(
                        ContenttypesGlobalizationUtil.globalize(
                            "cms.contenttypes.ui.contact.confirm_delete"));
                return link;
            } else {
                return (Label) value;
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

        // Get selected GenericContactEntry
        GenericContactEntry contactEntry =
                            new GenericContactEntry(new BigDecimal(evt.getRowKey().toString()));

        // Get GenericContact
        GenericContact contact = (GenericContact) m_itemModel.getSelectedObject(state);

        // Get selected column
        TableColumn col = getColumnModel().get(evt.getColumn().intValue());

        switch(evt.getColumn()) {
            case COL_EDIT:
                m_selectedEntry.setSelectedKey(state, evt.getRowKey().toString());
                m_editor.showContactEntryForm(state);
                break;
            case COL_DEL:
                contact.removeContactEntry(contactEntry);
                break;
        }
    }

    /**
     * provide Implementation to TableActionListener method.
     * Does nothing in our case.
     */
    @Override
    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException("Not Implemented");
    }

}
