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

import java.math.BigDecimal;

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
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentProjectsCollection;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.util.LockableImpl;

/**
 * Table for showing all {@link SciProject}s linked with a {@link SciDepartment}
 *
 * @author Jens Pelzetter
 * @see SciDepartment
 * @see SciProject
 */
public class SciDepartmentProjectsTable
        extends Table
        implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;

    public SciDepartmentProjectsTable(ItemSelectionModel itemModel) {
        super();
        m_itemModel = itemModel;

        setEmptyView(
                new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.projects.none")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.project").localize(),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.project.remove").localize(),
                TABLE_COL_DEL));
        colModel.add(new TableColumn(
                2,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.project.up").localize(),
                TABLE_COL_UP));
        colModel.add(new TableColumn(
                3,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.project.down").localize(),
                TABLE_COL_DOWN));

        setModelBuilder(
                new SciDepartmentProjectsTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());
        colModel.get(2).setCellRenderer(new UpCellRenderer());
        colModel.get(3).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);
    }

    private class SciDepartmentProjectsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public SciDepartmentProjectsTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            SciDepartment department = (SciDepartment) m_itemModel.
                    getSelectedObject(state);
            return new SciDepartmentProjectsTableModel(table,
                                                       state,
                                                       department);
        }
    }

    private class SciDepartmentProjectsTableModel
            implements TableModel {

        private Table m_table;
        private SciDepartmentProjectsCollection m_projects;
        private SciProject m_project;

        public SciDepartmentProjectsTableModel(Table table,
                                               PageState state,
                                               SciDepartment department) {
            m_table = table;
            m_projects = department.getProjects();
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((m_projects != null) && m_projects.next()) {
                m_project = m_projects.getProject();
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return m_project.getTitle();
                case 1:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "sciorganization.ui.department.project.remove").
                            localize();
                case 2:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "sciorganization.ui.department.project.up").
                            localize();
                case 3:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "sciorganization.ui.department.project.down").
                            localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return m_project.getID();
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
                                      int col) {
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            SciDepartment department = (SciDepartment) m_itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.EDIT_ITEM,
                    department);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
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
            SciDepartment department = (SciDepartment) m_itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    department);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) SciOrganizationGlobalizationUtil.
                        globalize(
                        "sciorganization.ui.department.project."
                        + ".confirm_remove").
                        localize());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    private class UpCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {

            if (0 == row) {
                Label label = new Label("");
                return label;
            } else {
                ControlLink link = new ControlLink(
                        (String) SciOrganizationGlobalizationUtil.globalize(
                        "sciorganization.ui.department.project.up").
                        localize());
                return link;
            }
        }
    }

    private class DownCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {

            SciDepartment department = (SciDepartment) m_itemModel.getSelectedObject(
                    state);
            SciDepartmentProjectsCollection projects =
                                            department.getProjects();

            if ((projects.size() - 1) == row) {
                Label label = new Label("");
                return label;
            } else {
                ControlLink link = new ControlLink(
                        (String) SciOrganizationGlobalizationUtil.globalize(
                        "sciorganization.ui.department.project.down").
                        localize());
                return link;
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        SciProject project = new SciProject(
                new BigDecimal(event.getRowKey().toString()));

        SciDepartment department =
                      (SciDepartment) m_itemModel.getSelectedObject(state);

        SciDepartmentProjectsCollection projects =
                                        department.getProjects();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            department.removeProject(project);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            projects.swapWithPrevious(project);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            projects.swapWithNext(project);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do.
    }
}
