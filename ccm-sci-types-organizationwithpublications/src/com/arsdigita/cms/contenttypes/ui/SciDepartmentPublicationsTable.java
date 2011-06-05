/*
 * Copyright (c) 2011 Jens Pelzetter,
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
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.SciDepartmentPublicationsCollection;
import com.arsdigita.cms.contenttypes.SciDepartmentWithPublications;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciDepartmentPublicationsTable
        extends Table
        implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel itemModel;

    public SciDepartmentPublicationsTable(final ItemSelectionModel itemModel) {
        super();
        this.itemModel = itemModel;

        setEmptyView(new Label(SciOrganizationWithPublicationsGlobalizationUtil.
                globalize("sciorganizationpublication.ui.publications.none")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                SciOrganizationWithPublicationsGlobalizationUtil.globalize(
                "sciorganizationpublication.ui.publication").localize(),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                SciOrganizationWithPublicationsGlobalizationUtil.globalize(
                "sciorganizationpublication.ui.publication.remove").
                localize(), TABLE_COL_DEL));

        setModelBuilder(
                new SciDepartmentPublicationsTableModelBuilder(itemModel));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class SciDepartmentPublicationsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public SciDepartmentPublicationsTableModelBuilder(
                final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            SciDepartmentWithPublications department =
                                          (SciDepartmentWithPublications) itemModel.
                    getSelectedObject(state);

            return new SciDepartmentPublicationsTableModel(table,
                                                           state,
                                                           department);
        }
    }

    private class SciDepartmentPublicationsTableModel
            implements TableModel {

        private Table table;
        private SciDepartmentPublicationsCollection publications;
        private Publication publication;

        public SciDepartmentPublicationsTableModel(
                final Table table,
                final PageState state,
                final SciDepartmentWithPublications department) {
            this.table = table;
            publications = department.getPublications();
        }

        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if ((publications != null) && publications.next()) {
                publication = publications.getPublication();
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return publication.getTitle();
                case 1:
                    return SciOrganizationWithPublicationsGlobalizationUtil.
                            globalize(
                            "sciorganizationpublication.ui.publiction.remove").
                            localize();
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return publication.getID();
        }
    }

    private class EditCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);

            SciDepartmentWithPublications department =
                                          (SciDepartmentWithPublications) itemModel.
                    getSelectedObject(state);


            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    department);

            if (canEdit) {
                Publication publication;
                try {
                    publication = new Publication((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                ContentSection section = CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link = new Link(value.toString(),
                                     resolver.generateItemURL(state,
                                                              publication,
                                                              section,
                                                              publication.
                        getVersion()));

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

        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
            com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);
            SciDepartmentWithPublications organization =
                                          (SciDepartmentWithPublications) itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                    organization);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) SciOrganizationWithPublicationsGlobalizationUtil.
                        globalize(
                        "sciorganizationpublication.ui.publication.confirm.remove").
                        localize());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    public void cellSelected(final TableActionEvent event) {
        PageState state = event.getPageState();

        Publication publication =
                    new Publication(new BigDecimal(event.getRowKey().toString()));

        SciDepartmentWithPublications department =
                                      (SciDepartmentWithPublications) itemModel.
                getSelectedObject(state);

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
            //Nothing to do here  
        } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            department.removePublication(publication);
        }
    }

    public void headSelected(final TableActionEvent event) {
        //Nothing to do her
    }
}
