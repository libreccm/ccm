/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.dispatcher.Utilities;

import com.arsdigita.util.LockableImpl;

/**
 * Sheet for showing the superior project of a {@link SciProject}.
 *
 * @author Jens Pelzetter
 */
public class SciProjectSuperProjectSheet
        extends Table
        implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel m_itemModel;

    public SciProjectSuperProjectSheet(ItemSelectionModel itemModel) {
        super();
        m_itemModel = itemModel;

        setEmptyView(
                new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.superproject_none")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.superproject").localize(),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.superproject.remove").localize(),
                TABLE_COL_DEL));

        setModelBuilder(
                new SciProjectSuperProjectSheetModelBuilder(itemModel));
        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class SciProjectSuperProjectSheetModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public SciProjectSuperProjectSheetModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            SciProject project = (SciProject) m_itemModel.getSelectedObject(
                    state);
            return new SciProjectSuperProjectSheetModel(table,
                                                        state,
                                                        project);
        }
    }

    private class SciProjectSuperProjectSheetModel
            implements TableModel {

        private Table m_table;
        private SciProject m_superProject;

        public SciProjectSuperProjectSheetModel(Table table,
                                                PageState state,
                                                SciProject project) {
            m_table = table;
            m_superProject = project.getSuperProject();
        }

        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if (null == m_superProject) {
                ret = false;
            } else {
                ret = true;
                m_superProject = null;
            }

            return ret;
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return m_superProject.getTitle();
                case 1:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "sciorganization.ui.project.superproject.remove");
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return m_superProject.getID();
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
            Label label = new Label(value.toString());
            return label;
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
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            SciProject project = (SciProject) m_itemModel.getSelectedObject(
                    state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    project);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) SciOrganizationGlobalizationUtil.
                        globalize(
                        "sciorganization.ui.project.superproject."
                        + ".confirm_remove").
                        localize());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        /*SciProject superProject = new SciProject(
                new BigDecimal(event.getRowKey().toString()));*/

        SciProject project = (SciProject) m_itemModel.getSelectedObject(state);

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            project.setSuperProject(null);
        }

    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do.
    }
}
