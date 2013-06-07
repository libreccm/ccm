/*
 * Copyright (c) 2010 Jens Pelzetter
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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationBundleCollection;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class OrganizationPublicationsTable extends Table {

    private final static String TABLE_COL_EDIT = "table_col_edit";
    private final static String TABLE_COL_YEAR = "table_col_year";
    private final static String TABLE_COL_TYPE = "table_col_type";
    private ItemSelectionModel itemModel;

    public OrganizationPublicationsTable(final ItemSelectionModel itemModel) {
        super();

        this.itemModel = itemModel;

        setEmptyView(new Label(PublicationGlobalizationUtil.globalize("organization.ui.publications.none")));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                PublicationGlobalizationUtil.globalize("organization.ui.publications.columns.name").localize(),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                1,
                PublicationGlobalizationUtil.globalize("organization.ui.publications.columns.year").localize(),
                TABLE_COL_YEAR));
        columnModel.add(new TableColumn(
                2,
                PublicationGlobalizationUtil.globalize("organization.ui.publications.columns.type").localize(),
                TABLE_COL_TYPE));

        setModelBuilder(new ModelBuilder(itemModel));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
    }

    private class ModelBuilder extends LockableImpl implements TableModelBuilder {

        private ItemSelectionModel itemModel;

        public ModelBuilder(final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            final GenericOrganizationalUnit orga = (GenericOrganizationalUnit) itemModel.getSelectedItem(state);

            return new Model(table, state, orga);
        }

    }

    private class Model implements TableModel {

        private Table table;
        private PublicationBundleCollection publications;

        public Model(final Table table, final PageState state, final GenericOrganizationalUnit orga) {
            this.table = table;
            publications = new PublicationBundleCollection((DataCollection) orga.getGenericOrganizationalUnitBundle().
                    get("unPublished"));
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if ((publications != null) && publications.next()) {
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return publications.getPublication().getTitle();
                case 1:
                    return publications.getPublication().getYearOfPublication();
                case 2:
                    return ((DataObject) publications.getPublication().get("type")).get("label");
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return publications.getPublication().getID();
        }

    }

    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final Publication publication = new Publication((BigDecimal) key);

//            final boolean canEdit = securityManager.canAccess(state.getRequest(),
//                                                              com.arsdigita.cms.SecurityManager.EDIT_ITEM,
//                                                              publication);

            //if (canEdit) {
            final ContentSection section = publication.getContentSection();//CMS.getContext().getContentSection();
            final ItemResolver resolver = section.getItemResolver();
            final Link link = new Link(value.toString(),
                                       resolver.generateItemURL(state,
                                                                publication,
                                                                section,
                                                                publication.getVersion()));
            return link;
//            } else {
//                final Label label = new Label(value.toString());
//                return label;
//            }

        }

    }
}
