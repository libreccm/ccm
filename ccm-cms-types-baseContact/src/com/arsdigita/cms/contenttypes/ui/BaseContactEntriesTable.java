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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryLocalization;
import com.arsdigita.categorization.CategoryLocalizationCollection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import java.util.Locale;

/**
 * Lists all existing contact entries for a selected contact.
 *
 * @author Sören Bernstein (quasimodo) quasi@barkhof.uni-bremen.de
 */
public class BaseContactEntriesTable extends Table {
    
    
    private final String TABLE_COL_EDIT = "table_col_lang";
    private final String TABLE_COL_DEL  = "table_col_del";
    
    /**
     * Creates a new instance of BaseContactEntriesTable
     */
    public BaseContactEntriesTable(final ItemSelectionModel itemModel, AuthoringKitWizard parent) {
        
        super();
        this.m_itemModel = itemModel;
        
        // if table is empty:
        setEmptyView(new Label(GlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.contactentries.none")));
        TableColumnModel tab_model = getColumnModel();
        
        // define columns
        // XXX globalize
        tab_model.add(new TableColumn(0, GlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.contactentries.key").localize(), TABLE_COL_EDIT));
        tab_model.add(new TableColumn(1, GlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.contactentries.value").localize()));
        tab_model.add(new TableColumn(2, GlobalizationUtil.globalize("cms.contenttypes.ui.baseContact.contactentries.delete").localize(), TABLE_COL_DEL));
        
        setModelBuilder(new BaseContactTableModelBuilder());
        
        tab_model.get(0).setCellRenderer(new EditCellRenderer());
        tab_model.get(2).setCellRenderer(new DeleteCellRenderer());
        
        addTableActionListener(this);
        
    }

    /**
     * XXXX
     *
     */
    private class BaseContactTableModelBuilder extends LockableImpl implements TableModelBuilder {
        
        public TableModel makeModel(Table table, PageState state) {
//XXX
            final BaseContact baseContact = m_category.getCategory(state);
            
            if (baseContact != null && baseContact.hasContactEntries()) {
                return new BaseContactTableModel(table, state, category);
            } else {
                return Table.EMPTY_MODEL;
            }
        }
    }

    /**
     * XXX
     *
     */
    private class BaseContactTableModel implements TableModel {
        
        final private int MAX_DESC_LENGTH = 25;
        
        private Table m_table;
        private BaseContactEntryCollection m_baseContactEntryCollection;
        private BaseContactEntry m_baseContactEntry;
        
        private BaseContactTableModel(Table t, PageState ps, BaseContact baseContact) {
            m_table = t;
            m_baseContactEntryColletion = new BaseContactEntryCollection(baseContact);
        }
        
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }
        
        /**
         * Check collection for the existence of another row.
         * 
         * If exists, fetch the value of current BaseContactEntryCollection object
         * into m_baseContactEntry class variable.
         */
        public boolean nextRow() {
            
            if(m_baseContactEntryCollection != null && m_baseContactEntryCollection.next()){
                m_baseContactEntry = m_baseContactEntryCollection.getBaseContactEntry();
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
                    return m_baseContactEntry.getKey();
                case 1:
                    return m_baseContactEntry.getValue();
                case 2:
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
            return m_baseContactEntry.getID();
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
//            CategoryLocalization cl =
//                (CategoryLocalization) m_clSel.getSelectedObject(state);
            
//            boolean canEdit = sm.canAccess(state.getRequest(),
//                    SecurityManager.DELETE_ITEM,
//                    cl);
//            if(canEdit) {
            if(true) {
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
            
//            SecurityManager sm = Utilities.getSecurityManager(state);
//            CategoryLocalization categoryLocalization =
//                new CategoryLocalization(new BigDecimal(evt.getRowKey().toString()));
            
//            boolean canDelete = sm.canAccess(state.getRequest(),
//                    SecurityManager.DELETE_ITEM,
//                    categoryLocalization);
//            if(canDelete) {
            if(true) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) GlobalizationUtil.globalize(
                        "cms.ui.category.localization.confirm_delete").localize());
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
        
        // Get selected BaseContactEntry
        BaseContactEntry baseContactEntry =
            new BaseContactEntry(new BigDecimal(evt.getRowKey().toString()));
        
        // Get BaseContact
// XXX
        BaseContact baseContact = m_baseContact.getCategory(state);
        
        // Get selected column
        TableColumn col = getColumnModel().get(evt.getColumn().intValue());
        
        // Edit
        if(col.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
            
        }
        
        // Delete
        if(col.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            baseContact.delContactEntry(baseContactEntry.getID());
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
