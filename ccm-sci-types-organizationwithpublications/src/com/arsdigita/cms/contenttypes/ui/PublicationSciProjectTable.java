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
import com.arsdigita.cms.contenttypes.PublicationSciProjectCollection;
import com.arsdigita.cms.contenttypes.SciProjectWithPublications;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 */
public class PublicationSciProjectTable
        extends Table
        implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel itemModel;

    public PublicationSciProjectTable(final ItemSelectionModel itemModel) {
        super();
        this.itemModel = itemModel;

        setEmptyView(new Label(SciOrganizationWithPublicationsGlobalizationUtil.
                globalize("sciorganizationpublication.ui.projects.none")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                SciOrganizationWithPublicationsGlobalizationUtil.globalize(
                "sciorganizationpublication.ui.projects").localize(),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                SciOrganizationWithPublicationsGlobalizationUtil.globalize(
                "sciorganizationpublication.ui.projects.remove").
                localize(), TABLE_COL_DEL));

        setModelBuilder(new PublicationSciProjectTableModelBuilder(
                itemModel));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class PublicationSciProjectTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public PublicationSciProjectTableModelBuilder(
                final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            Publication publication = (Publication) itemModel.getSelectedObject(
                    state);
            return new PublicationSciProjectTableModel(table,
                                                       state,
                                                       publication);
        }
    }

    private class PublicationSciProjectTableModel implements TableModel {

        private Table table;
        private PublicationSciProjectCollection projects;
        private SciProjectWithPublications project;

        public PublicationSciProjectTableModel(final Table table,
                                               final PageState state,
                                               final Publication publication) {

            this.table = table;

            projects =
            new PublicationSciProjectCollection((DataCollection) publication.get(
                    "projects"));
        }

        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if ((projects != null) && projects.next()) {
                project = projects.getOrganization();
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return project.getTitle();
                case 1:
                    return SciOrganizationWithPublicationsGlobalizationUtil.
                            globalize(
                            "sciorganizationpublication.ui.project.remove").
                            localize();
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return project.getID();
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
            Publication publication = (Publication) itemModel.getSelectedObject(
                    state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    publication);

            if (canEdit) {
                SciProjectWithPublications project;
                try {
                    project =
                    new SciProjectWithPublications((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                ContentSection section = CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link =
                     new Link(String.format("%s (%s)",
                                            value.toString(),
                                            project.getLanguage()),
                              resolver.generateItemURL(state,
                                                       project,
                                                       section,
                                                       project.getVersion()));
                return link;
            } else {
                SciProjectWithPublications project;
                try {
                    project =
                    new SciProjectWithPublications((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
                    return new Label(value.toString());
                }

                Label label = new Label(String.format("%s (%s)",
                                                      value.toString(),
                                                      project.getLanguage()));
                return label;
            }
        }
    }

    private class DeleteCellRenderer
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
            Publication publication = (Publication) itemModel.getSelectedObject(
                    state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                    publication);

            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) SciOrganizationWithPublicationsGlobalizationUtil.
                        globalize(
                        "sciorganizationpublication.ui.project.confirm.remove").
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

        SciProjectWithPublications project = new SciProjectWithPublications(
                new BigDecimal(event.getRowKey().toString()));

        Publication publication = (Publication) itemModel.getSelectedObject(
                state);

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
            //Nothing to do
        } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            Assert.exists(project, SciProjectWithPublications.class);

            publication.remove("projects", project);
        }
    }

    public void headSelected(final TableActionEvent event) {
        //Nothing to do
    }
}
