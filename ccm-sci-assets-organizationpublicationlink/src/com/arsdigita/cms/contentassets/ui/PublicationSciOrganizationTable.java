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
package com.arsdigita.cms.contentassets.ui;

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
import com.arsdigita.cms.contentassets.PublicationSciOrganizationCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithOrganization;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.ObjectNotFoundException;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter 
 */
public class PublicationSciOrganizationTable
        extends Table
        implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel itemModel;

    public PublicationSciOrganizationTable(ItemSelectionModel itemModel) {
        super();
        this.itemModel = itemModel;

        setEmptyView(new Label(SciOrganizationPublicationGlobalizationUtil.
                globalize("sciorganizationpublication.ui.organizations.none")));

        TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                SciOrganizationPublicationGlobalizationUtil.globalize(
                "sciorganizationpublication.ui.organization").localize(),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                SciOrganizationPublicationGlobalizationUtil.globalize(
                "sciorganizationpublication.ui.organization.remove").
                localize(), TABLE_COL_DEL));

        setModelBuilder(new PublicationSciOrganizationTableModelBuilder(
                itemModel));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
        columnModel.get(1).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class PublicationSciOrganizationTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public PublicationSciOrganizationTableModelBuilder(
                ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            Publication publication = (Publication) itemModel.getSelectedObject(
                    state);
            return new PublicationSciOrganizationTableModel(table,
                                                            state,
                                                            publication);
        }
    }

    private class PublicationSciOrganizationTableModel
            implements TableModel {

        private Table table;
        private PublicationSciOrganizationCollection organizations;
        private GenericOrganizationalUnit orga;

        public PublicationSciOrganizationTableModel(Table table,
                                                    PageState state,
                                                    Publication pub) {
            PublicationWithOrganization publication;
            this.table = table;
            publication = new PublicationWithOrganization(pub.getOID());
            organizations = publication.getOrganizations();
        }

        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if ((organizations != null) && organizations.next()) {
                orga = organizations.getOrganization();
                ret = true;
            } else {
                ret = false;
            }
            
            return ret;
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return orga.getTitle();
                case 1:
                    return SciOrganizationPublicationGlobalizationUtil.globalize(
                            "sciorganizationpublication.ui.organization.remove").
                            localize();
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return orga.getID();
        }
    }

    private class EditCellRenderer
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
            Publication publication = (Publication) itemModel.getSelectedObject(
                    state);

            boolean canEdit = securityManager.canAccess(
                    state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    publication);

            if (canEdit) {
                GenericOrganizationalUnit orga;
                try {
                    orga = new GenericOrganizationalUnit((BigDecimal) key);
                } catch (ObjectNotFoundException ex) {
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

        public Component getComponent(Table table,
                                      PageState state,
                                      Object value,
                                      boolean isSelected,
                                      Object key,
                                      int row,
                                      int column) {
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
                link.setConfirmation((String) SciOrganizationPublicationGlobalizationUtil.
                        globalize(
                        "sciorganizationpublication.ui.organization.confirm.remove").
                        localize());
                return link;
            } else {
                Label label = new Label(value.toString());
                return label;
            }
        }
    }

    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();
        
        GenericOrganizationalUnit orga = new GenericOrganizationalUnit(new BigDecimal(event.getRowKey().toString()));
        
        Publication pub = (Publication) itemModel.getSelectedObject(
                state);
        PublicationWithOrganization publication = new PublicationWithOrganization(
                pub.getOID());
        
        PublicationSciOrganizationCollection organizations = publication.getOrganizations();
                
        TableColumn column = getColumnModel().get(event.getColumn().intValue());
        
        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
            
        } else if(column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            Assert.exists(orga, GenericOrganizationalUnit.class);
            
            publication.removeOrganization(orga);
        }
    }

    public void headSelected(TableActionEvent event) {
       //Nothing to do.
    }
}
