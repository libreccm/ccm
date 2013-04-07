/*
 * Copyright (C) 2007 Red Hat Inc. All Rights Reserved.
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


import org.apache.log4j.Logger;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.contenttypes.DecisionTree;
import com.arsdigita.cms.contenttypes.DecisionTreeOptionTarget;
import com.arsdigita.cms.contenttypes.DecisionTreeOptionTargetCollection;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.util.LockableImpl;


/**
 * A table that displays the targets for the currently
 * selected DecisionTree.
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeTargetTable extends Table
{
    private static final Logger s_log = Logger.getLogger(
                                        DecisionTreeTargetTable.class);

    // column headings
    public static final String COL_SECTION = "Section";
    public static final String COL_MATCH   = "Match";
    public static final String COL_EDIT    = "Edit";
    public static final String COL_DEL     = "Delete";

    private ItemSelectionModel m_selTree;

    /**
     * Constructor.
     *
     * @param selArticle a selection model that returns the MultiPartArticle
     * which holds the sections to display.
     */
    public DecisionTreeTargetTable(ItemSelectionModel selArticle) {
        super();
        m_selTree = selArticle;

        TableColumnModel model = getColumnModel();
        model.add(new TableColumn(0, COL_SECTION));
        model.add(new TableColumn(1, COL_MATCH));
        model.add(new TableColumn(2, COL_EDIT));
        model.add(new TableColumn(3, COL_DEL));

        model.get(2).setCellRenderer(new SectionTableCellRenderer(true));
        model.get(3).setCellRenderer(new SectionTableCellRenderer(true));

        setModelBuilder(new OptionTableModelBuilder(m_selTree));
    }

    /**
     * 
     * @param selSection 
     */
    public void setSectionModel(ItemSelectionModel selSection) {
        if (selSection == null) {
            s_log.warn("null item model");
        }
    }

    /**
     * The model builder to generate a suitable model for the OptionTable
     */
    protected class OptionTableModelBuilder extends LockableImpl
        implements TableModelBuilder
    {
        protected ItemSelectionModel m_selTree;

        public OptionTableModelBuilder(ItemSelectionModel selTree) {
            m_selTree = selTree;
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            DecisionTree tree = (DecisionTree)m_selTree.getSelectedObject(state);
            return new TargetTableModel(table, state, tree);
        }
    }

    /**
     * 
     */
    protected class TargetTableModel implements TableModel
    {
        private TableColumnModel m_colModel;
        private DecisionTreeOptionTargetCollection m_targets;
        private DecisionTreeOptionTarget m_target;

        /** Constructor. */
        public TargetTableModel(Table table, PageState state, DecisionTree tree) {
            m_colModel = table.getColumnModel();
            m_targets = tree.getTargets();
        }

        /** Return the number of columsn this TableModel has. */
        public int getColumnCount() {
            return m_colModel.size();
        }

        /** Move to the next row and return true if the model is now positioned on
         *  a valid row.
         */
        public boolean nextRow() {
            if (m_targets.next()) {
                m_target = (DecisionTreeOptionTarget) m_targets.getTarget();
                return true;
            }
            return false;
        }

        /** Return the data element for the given column and the current row. */
        public Object getElementAt(int columnIndex) {
            if (m_colModel == null) { return null; }

            // match columns by name... makes for easier reordering
            TableColumn col = m_colModel.get(columnIndex);
            String colName = (String) col.getHeaderValue();

            if (COL_SECTION.equals(colName)) {
                return m_target.getMatchOption().getSection().getTitle();
            } else if (COL_MATCH.equals(colName)) {
                return m_target.getMatchOption().getLabel();
            } else if (COL_EDIT.equals(colName)) {
                return "edit";
            } else if (COL_DEL.equals(colName)) {
                return "delete";
            }

            return null;
        }

        /** Return the key for the given column and the current row. */
        public Object getKeyAt(int columnIndex) {
            return m_target.getID();
        }
    }

    /**
     * 
     */
    public class SectionTableCellRenderer extends LockableImpl
        implements TableCellRenderer
    {
        /** */
        private boolean m_active;

        /**  */
        public SectionTableCellRenderer () {
            this(false);
        }

        /**  */
        public SectionTableCellRenderer(boolean active) {
            m_active = active;
        }

        /** */
        public Component getComponent(Table table, PageState state,
        		Object value, boolean isSelected,
        		Object key, int row, int column) {
        	Component ret = null;
            SecurityManager sm = Utilities.getSecurityManager(state);
            ContentItem item = (ContentItem)m_selTree.getSelectedObject(state);
            
            boolean active = m_active &&
                sm.canAccess(state.getRequest(), SecurityManager.EDIT_ITEM,
                                     item);

            if (value instanceof Component) {
                ret = (Component)value;
            } else {
                if (value == null) {
                    ret = new Label("", false);
                } else {
                    if (active) {
                        ret = new ControlLink(value.toString());
                    } else {
                        ret = new Label(value.toString());
                    }
                }
            }

            return ret;
        }
    }
}
