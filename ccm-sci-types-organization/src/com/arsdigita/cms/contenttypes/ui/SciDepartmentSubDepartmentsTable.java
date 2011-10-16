/*
 * Copyright (c) 2010 Jens Pelzetter,
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
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.SciDepartment;
import com.arsdigita.cms.contenttypes.SciDepartmentSubDepartmentsCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Table for showing the subdepartments of a {@link SciDepartment}.
 *
 * @author Jens Pelzetter
 * @see SciDepartment
 */
public class SciDepartmentSubDepartmentsTable
        extends Table
        implements TableActionListener {

    private final Logger s_log = Logger.getLogger(
            SciDepartmentSubDepartmentsTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;

    public SciDepartmentSubDepartmentsTable(ItemSelectionModel itemModel) {
        super();
        m_itemModel = itemModel;

        setEmptyView(
                new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.subdepartments.none")));

        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.subdepartment").localize(),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.subdepartment.remove").localize(),
                TABLE_COL_DEL));
        colModel.add(new TableColumn(
                2,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.subdepartment.up").localize(),
                TABLE_COL_UP));
        colModel.add(new TableColumn(
                3,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.department.subdepartment.down").localize(),
                TABLE_COL_DOWN));

        setModelBuilder(
                new SciDepartmentSubDepartmentsTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());
        colModel.get(2).setCellRenderer(new UpCellRenderer());
        colModel.get(3).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);
    }

    private class SciDepartmentSubDepartmentsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public SciDepartmentSubDepartmentsTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            SciDepartment department = (SciDepartment) m_itemModel.
                    getSelectedObject(state);
            return new SciDepartmentSubDepartmentsTableModel(table,
                                                             state,
                                                             department);
        }
    }

    private class SciDepartmentSubDepartmentsTableModel
            implements TableModel {

        private Table m_table;
        private SciDepartmentSubDepartmentsCollection m_subdepartments;
        private SciDepartment m_subdepartment;

        private SciDepartmentSubDepartmentsTableModel(Table table,
                                                      PageState state,
                                                      SciDepartment department) {
            m_table = table;
            m_subdepartments = department.getSubDepartments();
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((m_subdepartments != null) && m_subdepartments.next()) {
                m_subdepartment = m_subdepartments.getSubDepartment();
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
                    return m_subdepartment.getTitle();
                case 1:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "sciorganization.ui.department.subdepartment.remove").
                            localize();
                case 2:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "sciorganization.ui.department.subdepartment.up").
                            localize();
                case 3:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "sciorganization.ui.department.subdepartment.down").
                            localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return m_subdepartment.getID();
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
                SciDepartment subDepartment;
                try {
                    subDepartment = new SciDepartment(
                            (BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }

                ContentSection section = CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link = new Link(String.format("%s (%s)",
                                                   value.toString(),
                                                   subDepartment.getLanguage()),
                                     resolver.generateItemURL(state,
                                                              subDepartment,
                                                              section,
                                                              subDepartment.
                        getVersion()));

                return link;


            } else {
                  SciDepartment subDepartment;
                try {
                    subDepartment = new SciDepartment(
                            (BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }
                
                Label label = new Label(String.format("%s (%s)",
                                                      value.toString(),
                                                      subDepartment.getLanguage()));
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
                        "sciorganization.ui.department.subdepartment."
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
                        "sciorganization.ui.department.subdepartment.up").
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

            SciDepartment department = (SciDepartment) m_itemModel.
                    getSelectedObject(state);
            SciDepartmentSubDepartmentsCollection subDepartments =
                                                  department.getSubDepartments();

            if ((subDepartments.size() - 1) == row) {
                Label label = new Label("");
                return label;
            } else {
                ControlLink link = new ControlLink(
                        (String) SciOrganizationGlobalizationUtil.globalize(
                        "sciorganization.ui.department.subdepartment.down").
                        localize());
                return link;
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        SciDepartment subdepartment = new SciDepartment(
                new BigDecimal(event.getRowKey().toString()));

        SciDepartment department =
                      (SciDepartment) m_itemModel.getSelectedObject(state);

        SciDepartmentSubDepartmentsCollection subdepartments =
                                              department.getSubDepartments();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            department.removeSubDepartment(subdepartment);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            subdepartments.swapWithPrevious(subdepartment);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            subdepartments.swapWithNext(subdepartment);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do.
    }
}
