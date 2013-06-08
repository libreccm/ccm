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
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.GenericContactTypeCollection;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 * 
 *
 * @author Sören Bernstein (quasimodo) quasi@barkhof.uni-bremen.de
 */
public class GenericContactTypeTable extends Table implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel m_itemModel;

    /**
     * Creates a new instance of GenericContactTypeTable
     */
    public GenericContactTypeTable(final ItemSelectionModel itemModel) {

        super();
        this.m_itemModel = itemModel;

        // if table is empty:
        setEmptyView(new Label(ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.contacttypes.none")));
        TableColumnModel tab_model = getColumnModel();

        // define columns
        tab_model.add(new TableColumn(
                0, 
                ContenttypesGlobalizationUtil
                .globalize("cms.contenttypes.ui.contacttypes.key")
             // .localize()
                , 
                TABLE_COL_EDIT));
        tab_model.add(new TableColumn(
                1, 
                ContenttypesGlobalizationUtil
                .globalize("cms.contenttypes.ui.contacttypes.title")
              //.localize()
                ));
        tab_model.add(new TableColumn(
                2, 
                ContenttypesGlobalizationUtil
                .globalize("cms.contenttypes.ui.contacttypes.action")
                .localize()
                , 
                TABLE_COL_DEL));

        setModelBuilder(new GenericContactTypeTableModelBuilder(itemModel));

        tab_model.get(0).setCellRenderer(new EditCellRenderer());
        tab_model.get(2).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);

    }

    /**
     * XXXX
     *
     */
    private class GenericContactTypeTableModelBuilder extends LockableImpl 
                                                      implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public GenericContactTypeTableModelBuilder(ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            RelationAttribute contacttype = (RelationAttribute) m_itemModel.getSelectedObject(state);
            return new GenericContactTypeTableModel(table, state, contacttype);
        }
    }

    /**
     * XXX
     *
     */
    private class GenericContactTypeTableModel implements TableModel {

        final private int MAX_DESC_LENGTH = 25;
        private Table m_table;
        private RelationAttribute m_contacttype;
        private GenericContactTypeCollection m_contacttypeCollection = new 
                GenericContactTypeCollection();

        private GenericContactTypeTableModel(Table t, 
                                             PageState ps, 
                                             RelationAttribute contacttype) {
            m_table = t;
            m_contacttype = contacttype;
//            m_contacttypeCollection.addLanguageFilter(
//                   DispatcherHelper.getNegotiatedLocale().getLanguage());
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

            if (m_contacttypeCollection != null && m_contacttypeCollection.next()) {
                m_contacttype = m_contacttypeCollection.getRelationAttribute();
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
                    return m_contacttypeCollection.getKey();
                case 1:
                    return m_contacttypeCollection.getName();
                case 2:
                    return GlobalizationUtil.globalize("cms.ui.delete")
                          //.localize()
                            ;
                default:
                    return null;
            }
        }

        /**
         *
         * @see com.arsdigita.bebop.table.TableModel#getKeyAt(int)
         */
        public Object getKeyAt(int columnIndex) {
            return m_contacttype.getKey();
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
            RelationAttribute contacttype = (RelationAttribute) 
                                             m_itemModel.getSelectedObject(state);

//            boolean canEdit = sm.canAccess(state.getRequest(),
//                    SecurityManager.EDIT_ITEM,
//                    contacttype);
//            if (canEdit) {
            ControlLink link = new ControlLink(value.toString());
            return link;
//            } else {
//                return new Label(value.toString());
//            }
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
            RelationAttribute contacttype = (RelationAttribute) 
                                            m_itemModel.getSelectedObject(state);

//            boolean canDelete = sm.canAccess(state.getRequest(),
//                    SecurityManager.DELETE_ITEM,
//                    contacttype);
//            if (canDelete) {
            ControlLink link = new ControlLink(value.toString());
            link.setConfirmation(ContenttypesGlobalizationUtil
                                 .globalize(
                                 "cms.contenttypes.ui.contacttype.confirm_delete")
                                  );
            return link;
//            } else {
//                return new Label(value.toString());
//            }
        }
    }

    /**
     * Provide implementation to TableActionListener method.
     * Code that comes into picture when a link on the table is clicked.
     * Handles edit and delete event.
     */
    public void cellSelected(TableActionEvent evt) {

        PageState state = evt.getPageState();

        // Get selected GenericContactType
        RelationAttribute contacttype = new RelationAttribute(new 
                                        BigDecimal(evt.getRowKey().toString()));

        // Get selected column
        TableColumn col = getColumnModel().get(evt.getColumn().intValue());

        // Edit
        if (col.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        }

        // Delete
        if (col.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            contacttype.delete();
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
