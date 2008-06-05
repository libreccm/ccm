/*
 * CategortyLocalizationTable.java
 *
 * Created on 18. April 2008, 12:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
 * Listet alle vorhandenen Lokalisierungen zu der aktuellen Kategorie auf. Diese Klasse ist Teil
 * der Admin-Oberfläche von APLAWS+ und erweitert die Standardformulare um die Formulare für die
 * Bearbeitung der neuen, mehrsprachigen Kategorien.
 *
 * @author quasi
 */
public class CategoryLocalizationTable extends Table implements TableActionListener {
    
    private final CategoryRequestLocal m_category;
    private final SingleSelectionModel m_model;
    
    private final String TABLE_COL_LANG = "table_col_lang";
    private final String TABLE_COL_DEL  = "table_col_del";
    
    /**
     * Creates a new instance of CategoryLocalizationTable
     */
    public CategoryLocalizationTable(final CategoryRequestLocal category,
            final SingleSelectionModel model) {
        
        super();
        
        m_category = category;
        m_model = model;
        
        // Falls die Tabelle leer ist
        setEmptyView(new Label(GlobalizationUtil.globalize("cms.ui.category.localization.none")));
        TableColumnModel tab_model = getColumnModel();
        
        // Spalten definieren
        // XXX Globalisieren
        tab_model.add(new TableColumn(0, GlobalizationUtil.globalize("cms.ui.category.localization.locale").localize(), TABLE_COL_LANG));
        tab_model.add(new TableColumn(1, GlobalizationUtil.globalize("cms.ui.category.localization.name").localize()));
        tab_model.add(new TableColumn(2, GlobalizationUtil.globalize("cms.ui.category.localization.description").localize()));
        tab_model.add(new TableColumn(3, GlobalizationUtil.globalize("cms.ui.category.localization.url").localize()));
        tab_model.add(new TableColumn(4, GlobalizationUtil.globalize("cms.ui.category.localization.action").localize(), TABLE_COL_DEL));
        
        setModelBuilder(new CategoryLocalizationTableModelBuilder());
        
        tab_model.get(0).setCellRenderer(new EditCellRenderer());
        tab_model.get(4).setCellRenderer(new DeleteCellRenderer());
        
        addTableActionListener(this);
        
    }
    
    private class CategoryLocalizationTableModelBuilder extends LockableImpl implements TableModelBuilder {
        
        public TableModel makeModel(Table table, PageState state) {
            final Category category = m_category.getCategory(state);
            
            if (category != null && category.hasLocalizations()) {
                return new CategoryLocalizationTableModel(table, state, category);
            } else {
                return Table.EMPTY_MODEL;
            }
        }
    }
    
    private class CategoryLocalizationTableModel implements TableModel {
        
        final private int MAX_DESC_LENGTH = 25;
        
        private Table m_table;
        private CategoryLocalizationCollection m_categoryLocalizations;
        private CategoryLocalization m_categoryLocalization;
        
        private CategoryLocalizationTableModel(Table t, PageState ps, Category category) {
            m_table = t;
            m_categoryLocalizations = new CategoryLocalizationCollection(category);
        }
        
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }
        
        /**
         * check collection for the existence of another row.If it has fetch the
         * value of current CategoryLocalization object into m_categoryLocalization class variable.
         */
        public boolean nextRow() {
            
            if(m_categoryLocalizations != null && m_categoryLocalizations.next()){
                m_categoryLocalization = m_categoryLocalizations.getCategoryLocalization();
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
                    Locale clLocale = new Locale(m_categoryLocalization.getLocale());
                    return clLocale.getDisplayLanguage(/*Locale*/);
                case 1:
                    return m_categoryLocalization.getName();
                case 2:
                    String desc = m_categoryLocalization.getDescription();
                    if(desc != null && desc.length() > MAX_DESC_LENGTH) desc = desc.substring(MAX_DESC_LENGTH - 3).concat("...");
                    return desc;
                case 3:
                    return m_categoryLocalization.getURL();
                case 4:
                    return GlobalizationUtil.globalize("cms.ui.delete").localize();
//                    return "Delete";
                default:
                    return null;
            }
        }
        
        /**
         *
         * @see com.arsdigita.bebop.table.TableModel#getKeyAt(int)
         */
        public Object getKeyAt(int columnIndex) {
            return m_categoryLocalization.getID();
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
//            CategoryLocalization cl = (CategoryLocalization) m_clSel.getSelectedObject(state);
            
//            boolean canEdit = sm.canAccess(state.getRequest(),
//                    SecurityManager.DELETE_ITEM,
//                    cl);
//            if (canEdit) {
            if (true) {
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
//            CategoryLocalization categoryLocalization = new CategoryLocalization(new BigDecimal(evt.getRowKey().toString()));
            
//            boolean canDelete = sm.canAccess(state.getRequest(),
//                    SecurityManager.DELETE_ITEM,
//                    categoryLocalization);
//            if (canDelete) {
              if (true) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) GlobalizationUtil.globalize("cms.ui.category.localization.confirm_delete").localize());
//                link.setConfirmation("Delete this localization?");
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
        
        // Get selected CategoryLocalization
        CategoryLocalization categoryLocalization = new CategoryLocalization(new BigDecimal(evt.getRowKey().toString()));
        
        // Get Category
        Category category = m_category.getCategory(state);
        
        // Get selected column
        TableColumn col = getColumnModel().get(evt.getColumn().intValue());
        
        // Edit
        if(col.getHeaderKey().toString().equals(TABLE_COL_LANG)) {
            
        }
        
        // Delete
        if(col.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            category.delLanguage(categoryLocalization.getLocale());
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
