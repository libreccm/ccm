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
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.contenttypes.DecisionTree;
import com.arsdigita.cms.contenttypes.DecisionTreeSection;
import com.arsdigita.cms.contenttypes.DecisionTreeSectionCollection;
import com.arsdigita.cms.contenttypes.util.DecisionTreeGlobalizationUtil;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.util.LockableImpl;

import org.apache.log4j.Logger;


/**
 * A table that displays the sections for the currently selected DecisionTree.
 * 
 * If no sections are created / exists, it prints an empty section statement
 * and a link to add new sections. Otherwise
 *
 * @author Carsten Clasohm
 * @version $Id$
 */
public class DecisionTreeSectionTable extends Table {

    // match columns by (symbolic) index, makes for easier reordering
    public static final int COL_IDX_TITLE  = 0;  // title section
    public static final int COL_IDX_EDIT   = 1;  // edit section link
    public static final int COL_IDX_DEL    = 2;  // delete section link
    public static final int COL_IDX_FIRST  = 3;  // first section link

    private ItemSelectionModel m_selTree;

    private static final Logger s_log = Logger.getLogger(
                                        DecisionTreeSectionTable.class);

    /**
     * Constructor.
     *
     * @param selArticle a selection model that returns the MultiPartArticle
     * which holds the sections to display.
     */
    public DecisionTreeSectionTable (ItemSelectionModel selArticle) {
        super();
        m_selTree = selArticle;

        TableColumnModel model = getColumnModel();
        model.add( new TableColumn(
              COL_IDX_TITLE, 
              new Label(DecisionTreeGlobalizationUtil.globalize(
              "cms.contenttypes.ui.decisiontree.sections.table.header_section")
              ) ));
        model.add( new TableColumn(
              COL_IDX_EDIT, 
              new Label(DecisionTreeGlobalizationUtil.globalize(
              "cms.contenttypes.ui.decisiontree.sections.table.header_edit")
              ) ));
        model.add( new TableColumn(
              COL_IDX_DEL, 
              new Label(DecisionTreeGlobalizationUtil.globalize(
              "cms.contenttypes.ui.decisiontree.sections.table.header_delete")
              ) ));
        model.add( new TableColumn(
              COL_IDX_FIRST, 
              new Label(DecisionTreeGlobalizationUtil.globalize(
              "cms.contenttypes.ui.decisiontree.sections.table.header_first_section")
              ) ));

        model.get(COL_IDX_EDIT).setCellRenderer(new SectionTableCellRenderer(true));
        model.get(COL_IDX_DEL).setCellRenderer(new SectionTableCellRenderer(true));
        model.get(COL_IDX_FIRST).setCellRenderer(new SectionTableCellRenderer(true));

        setModelBuilder(new SectionTableModelBuilder(m_selTree));
    }

    /**
     * 
     * @param selSection 
     */
    public void setSectionModel ( ItemSelectionModel selSection ) {
        if ( selSection == null ) {
            s_log.warn("null item model");
        }
    }

    /**
     * The model builder to generate a suitable model for the SectionTable
     */
    protected class SectionTableModelBuilder extends LockableImpl
                                             implements TableModelBuilder {

        protected ItemSelectionModel m_selTree;

        /**
         * Internal class constructor.
         */
        public SectionTableModelBuilder (ItemSelectionModel selTree) {
            m_selTree = selTree;
        }

        /**
         * Internal class worker method. 
         * 
         * @param table
         * @param state
         * @return 
         */
        public TableModel makeModel(Table table, PageState state) {

            table.getRowSelectionModel().clearSelection(state);
            DecisionTree tree = (DecisionTree)m_selTree.getSelectedObject(state);

            return new SectionTableModel(table, state, tree);
        }
    }

    /**
     * Internal protected class.
     */
    protected class SectionTableModel implements TableModel {

        private TableColumnModel m_colModel;
        private DecisionTreeSectionCollection m_sections;
        private DecisionTreeSection m_section;

        /** Internal class' Constructor. */
        public SectionTableModel (Table table, PageState state, DecisionTree tree) {
            m_colModel = table.getColumnModel();
            m_sections = tree.getSections();
        }

        /** Return the number of columns this TableModel has. */
        public int getColumnCount () {
            return m_colModel.size();
        }

        /** Move to the next row and return true if the model is now positioned on
         *  a valid row.
         */
        public boolean nextRow () {
            if ( m_sections.next() ) {
                m_section = (DecisionTreeSection)m_sections.getSection();
                return true;
            }
            return false;
        }

        /** 
         * Return the data element for the given column and the current row. 
         */
        public Object getElementAt(int columnIndex) {

            if (m_colModel == null) { return null; }

            if ( columnIndex == COL_IDX_TITLE ) {
                return m_section.getTitle();
            } else if ( columnIndex == COL_IDX_EDIT ) {
                return new Label(DecisionTreeGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.decisiontree.sections.table.link_edit"));
            } else if ( columnIndex == COL_IDX_DEL ) {
                return new Label(DecisionTreeGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.decisiontree.sections.table.link_delete"));
            } else if ( columnIndex == COL_IDX_FIRST ) {
            	DecisionTree tree = m_section.getTree();
            	DecisionTreeSection firstSection = tree.getFirstSection();
            	if (firstSection != null && firstSection.getID() == m_section.getID()) {
                    // return anything different from Label to prevent the
                    // construction of a link
                    return null;
            	} else {
                    return new Label(DecisionTreeGlobalizationUtil.globalize(
                        "cms.contenttypes.ui.decisiontree.sections.table.link_set_first"));
            	}
            }

            return null;
        }

        /** Return the key for the given column and the current row. */
        public Object getKeyAt(int columnIndex) {
            return m_section.getID();
        }
    }

    /**
     * 
     */
    public class SectionTableCellRenderer extends LockableImpl
                                          implements TableCellRenderer {
        private boolean m_active;

        /**
         * Internal class constructor.
         */
        public SectionTableCellRenderer () {
            this(false);
        }

        /**
         * Internal class constructor.
         * @param active 
         */
        public SectionTableCellRenderer ( boolean active ) {
            m_active = active;
        }

        /**
         * 
         * @param table
         * @param state
         * @param value
         * @param isSelected
         * @param key
         * @param row
         * @param column
         * @return 
         */
        public Component getComponent ( Table table, PageState state,
                                        Object value, boolean isSelected,
                                        Object key, int row, int column ) {
            Component ret = null;
            SecurityManager sm = CMS.getSecurityManager(state);
            ContentItem item = (ContentItem)m_selTree.getSelectedObject(state);
            
            boolean active = m_active && sm.canAccess(state.getRequest(), 
                                                      SecurityManager.EDIT_ITEM,
                                                      item);

            if (value == null) {
                ret = new Label("", false);
            } else  if (value instanceof Label) {
                if (active) {
                    ret = new ControlLink( (Component)value );
                } else {
                    ret = (Component)value;
                }
            } else {
                // last resort, should never happen
                ret = (Component)value;
            }

            return ret;
        }
    }


}
