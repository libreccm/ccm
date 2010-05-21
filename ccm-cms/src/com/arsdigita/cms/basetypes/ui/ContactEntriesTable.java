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
package com.arsdigita.cms.basetypes.ui;

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
import com.arsdigita.cms.basetypes.Contact;
import com.arsdigita.cms.basetypes.ContactEntry;
import com.arsdigita.cms.basetypes.ContactEntryCollection;
import com.arsdigita.cms.basetypes.util.BasetypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 * Lists all existing contact entries for a selected contact.
 *
 * @author Sören Bernstein (quasimodo) quasi@barkhof.uni-bremen.de
 */
public class ContactEntriesTable extends Table implements TableActionListener{
    
    
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL  = "table_col_del";
    
    private ItemSelectionModel m_itemModel;
    
    /**
     * Creates a new instance of ContactEntriesTable
     */
    public ContactEntriesTable(final ItemSelectionModel itemModel) {
        
        super();
        this.m_itemModel = itemModel;
        
        // if table is empty:
        setEmptyView(new Label(BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.contactEntry.none")));
        TableColumnModel tab_model = getColumnModel();
        
        // define columns
        tab_model.add(new TableColumn(0, BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.contactEntry.key").localize(), TABLE_COL_EDIT));
        tab_model.add(new TableColumn(1, BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.contactEntry.value").localize()));
        tab_model.add(new TableColumn(2, BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.contactEntry.description").localize()));
        tab_model.add(new TableColumn(3, BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.contactEntry.action").localize(), TABLE_COL_DEL));
        
        setModelBuilder(new ContactTableModelBuilder(itemModel));
        
        tab_model.get(0).setCellRenderer(new EditCellRenderer());
        tab_model.get(3).setCellRenderer(new DeleteCellRenderer());
        
        addTableActionListener(this);
        
    }

    /**
     * XXXX
     *
     */
    private class ContactTableModelBuilder extends LockableImpl implements TableModelBuilder {
        
        private ItemSelectionModel m_itemModel;
        
        public ContactTableModelBuilder(ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }
        
        public TableModel makeModel(Table table, PageState state) {

            table.getRowSelectionModel().clearSelection(state);
            
            Contact contact = (Contact) m_itemModel.getSelectedObject(state);
            
//            if (contact != null && contact.hasContactEntries()) {
                return new ContactTableModel(table, state, contact);
//            } else {
//                return Table.EMPTY_MODEL;
//            }
        }
    }

    /**
     * XXX
     *
     */
    private class ContactTableModel implements TableModel {
        
        final private int MAX_DESC_LENGTH = 25;
        
        private Table m_table;
        private ContactEntryCollection m_contactEntryCollection;
        private ContactEntry m_contactEntry;
        
        private ContactTableModel(Table t, PageState ps, Contact contact) {
            m_table = t;
            m_contactEntryCollection = contact.getContactEntries();
        }
        
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }
        
        /**
         * Check collection for the existence of another row.
         * 
         * If exists, fetch the value of current ContactEntryCollection object
         * into m_contactEntry class variable.
         */
        public boolean nextRow() {
            
            if(m_contactEntryCollection != null && m_contactEntryCollection.next()){
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
        public Object getElementAt(int columnIndex) {
            switch (columnIndex){
                case 0:
                    return (String)BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.contactEntry.key." + m_contactEntry.getKey()).localize();
                case 1:
                    return m_contactEntry.getValue();
                case 2:
                    return (m_contactEntry.getDescription() != null && m_contactEntry.getDescription().length() > MAX_DESC_LENGTH)
                                ? m_contactEntry.getDescription().substring(0, MAX_DESC_LENGTH)
                                : m_contactEntry.getDescription();
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
            Contact contact = (Contact) m_itemModel.getSelectedObject(state);
            
            boolean canEdit = sm.canAccess(state.getRequest(),
                                           SecurityManager.EDIT_ITEM,
                                           contact);
            if(canEdit) {
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
            Contact contact = (Contact) m_itemModel.getSelectedObject(state);
            
            boolean canDelete = sm.canAccess(state.getRequest(),
                                             SecurityManager.DELETE_ITEM,
                                             contact);
            if(canDelete) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) BasetypesGlobalizationUtil.globalize("cms.basetypes.ui.contact.confirm_delete").localize());
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
        
        // Get selected ContactEntry
        ContactEntry contactEntry =
            new ContactEntry(new BigDecimal(evt.getRowKey().toString()));
        
        // Get Contact
        Contact contact = (Contact) m_itemModel.getSelectedObject(state);
        
        // Get selected column
        TableColumn col = getColumnModel().get(evt.getColumn().intValue());
        
        // Edit
        if(col.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
            
        }
        
        // Delete
        if(col.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            contact.removeContactEntry(contactEntry);
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
