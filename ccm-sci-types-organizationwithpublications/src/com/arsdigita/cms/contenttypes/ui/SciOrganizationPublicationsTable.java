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
import com.arsdigita.cms.contenttypes.SciOrganizationPublicationsCollection;
import com.arsdigita.cms.contenttypes.SciOrganizationWithPublications;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 */
public class SciOrganizationPublicationsTable
        extends Table
        implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel itemModel;

    public SciOrganizationPublicationsTable(final ItemSelectionModel itemModel) {
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

        setModelBuilder(new SciOrganizationPublicationsTableModelBuilder(
                itemModel));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class SciOrganizationPublicationsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public SciOrganizationPublicationsTableModelBuilder(
                final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            SciOrganizationWithPublications organization =
                                            (SciOrganizationWithPublications) itemModel.
                    getSelectedObject(state);

            return new SciOrganizationPublicationsTableModel(table,
                                                             state,
                                                             organization);
        }
    }

    private class SciOrganizationPublicationsTableModel implements TableModel {

        private Table table;
        private SciOrganizationPublicationsCollection publications;
        private Publication publication;

        public SciOrganizationPublicationsTableModel(
                final Table table,
                final PageState state,
                final SciOrganizationWithPublications organization) {
            this.table = table;
            publications = organization.getPublications();
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

        public Object getElementAt(final int columnIndex) {
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

        public Object getKeyAt(final int columnIndex) {
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
            SciOrganizationWithPublications organization =
                                            (SciOrganizationWithPublications) itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    organization);

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
            SciOrganizationWithPublications organization =
                                            (SciOrganizationWithPublications) itemModel.
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

    @Override
    public void cellSelected(final TableActionEvent event) {
        PageState state = event.getPageState();

        Publication publication =
                    new Publication(new BigDecimal(event.getRowKey().toString()));

        SciOrganizationWithPublications organization =
                                        (SciOrganizationWithPublications) itemModel.
                getSelectedObject(state);

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
            //Nothing to do here  
        } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            organization.removePublication(publication);
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing to do.
    }
}
