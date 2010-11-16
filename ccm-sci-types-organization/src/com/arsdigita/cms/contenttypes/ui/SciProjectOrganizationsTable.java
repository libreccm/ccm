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
import com.arsdigita.cms.contenttypes.SciOrganization;
import com.arsdigita.cms.contenttypes.SciProject;
import com.arsdigita.cms.contenttypes.SciProjectOrganizationsCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import org.apache.log4j.Logger;

/**
 * Table for showing the links between a {@link SciProject} and 
 * {@link SciOrganization}s.
 *
 * @author Jens Pelzetter
 */
public class SciProjectOrganizationsTable
        extends Table
        implements TableActionListener {

    private static final Logger s_log = Logger.getLogger(
            SciProjectOrganizationsTable.class);
    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private final String TABLE_COL_UP = "table_col_up";
    private final String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;

    public SciProjectOrganizationsTable(ItemSelectionModel itemModel) {
        super();
        m_itemModel = itemModel;

        setEmptyView(
                new Label(SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.organization.none")));
        TableColumnModel colModel = getColumnModel();
        colModel.add(new TableColumn(
                0,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.organization").localize(),
                TABLE_COL_EDIT));
        colModel.add(new TableColumn(
                1,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.organization.remove").localize(),
                TABLE_COL_DEL));
        colModel.add(new TableColumn(
                2,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.organization.up").localize(),
                TABLE_COL_UP));
        colModel.add(new TableColumn(
                3,
                SciOrganizationGlobalizationUtil.globalize(
                "sciorganization.ui.project.organization.down").localize(),
                TABLE_COL_DOWN));


        setModelBuilder(
                new SciProjectOrganizationsTableModelBuilder(itemModel));

        colModel.get(0).setCellRenderer(new EditCellRenderer());
        colModel.get(1).setCellRenderer(new DeleteCellRenderer());
        colModel.get(2).setCellRenderer(new UpCellRenderer());
        colModel.get(3).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);
    }

    private class SciProjectOrganizationsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public SciProjectOrganizationsTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        @Override
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            SciProject project = (SciProject) m_itemModel.getSelectedObject(
                    state);
            return new SciProjectOrganizationsTableModel(table,
                                                         state,
                                                         project);
        }
    }

    private class SciProjectOrganizationsTableModel
            implements TableModel {

        private Table m_table;
        private SciProjectOrganizationsCollection m_organizations;
        private SciOrganization m_organization;

        public SciProjectOrganizationsTableModel(Table table,
                                                 PageState state,
                                                 SciProject project) {
            m_table = table;
            m_organizations = project.getOrganizations();
        }

        @Override
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            boolean ret;

            if ((m_organizations != null) && m_organizations.next()) {
                m_organization = m_organizations.getOrganization();
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
                    return m_organization.getTitle();
                case 1:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "sciorganization.ui.project.organization.remove").
                            localize();
                case 2:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "sciorganization.ui.project.organization.up").
                            localize();
                case 3:
                    return SciOrganizationGlobalizationUtil.globalize(
                            "sciorganization.ui.project.organization.down").
                            localize();
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(int columnIndex) {
            return m_organization.getID();
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
            SciProject project = (SciProject) m_itemModel.getSelectedObject(
                    state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.EDIT_ITEM,
                    project);

            if (canEdit) {
                SciOrganization orga;
                try {
                    orga = new SciOrganization((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    s_log.warn(String.format("No object with key '%s' found.",
                                             key),
                               ex);
                    return new Label(value.toString());
                }

                ContentSection section = CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link =
                     new Link(value.toString(),
                              resolver.generateItemURL(state,
                                                       orga,
                                                       section,
                                                       orga.getVersion()));
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
                        "sciorganization.ui.project.organization."
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
                        "sciorganization.ui.project.organization.up").
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

            SciProject project = (SciProject) m_itemModel.getSelectedObject(
                    state);
            SciProjectOrganizationsCollection orgas =
                                              project.getOrganizations();

            if ((orgas.size() - 1) == row) {
                Label label = new Label("");
                return label;
            } else {
                ControlLink link = new ControlLink(
                        (String) SciOrganizationGlobalizationUtil.globalize(
                        "sciorganization.ui.project.organization.down").
                        localize());
                return link;
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        SciOrganization orga = new SciOrganization(
                new BigDecimal(event.getRowKey().toString()));

        SciProject project =
                   (SciProject) m_itemModel.getSelectedObject(state);

        SciProjectOrganizationsCollection subprojects =
                                          project.getOrganizations();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            project.removeOrganization(orga);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            subprojects.swapWithPrevious(orga);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            subprojects.swapWithNext(orga);
        }
    }

    @Override
    public void headSelected(TableActionEvent event) {
        //Nothing to do.
    }
}
