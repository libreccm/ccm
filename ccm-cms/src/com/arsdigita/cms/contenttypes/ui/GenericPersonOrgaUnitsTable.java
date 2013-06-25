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
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitBundleCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
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
public class GenericPersonOrgaUnitsTable extends Table {

    private final static String TABLE_COL_EDIT = "table_col_edit";
    private final static String TABLE_COL_YEAR = "table_col_year";
    private final static String TABLE_COL_TYPE = "table_col_type";
    private ItemSelectionModel itemModel;

    /**
     * 
     * @param itemModel 
     */
    public GenericPersonOrgaUnitsTable(final ItemSelectionModel itemModel) {
        super();

        this.itemModel = itemModel;

        setEmptyView(new Label(ContenttypesGlobalizationUtil
                               .globalize("person.ui.orgaunits.none")));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                0,
                ContenttypesGlobalizationUtil
                .globalize("person.ui.orgaunits.columns.name"),
                TABLE_COL_EDIT));
        columnModel.add(new TableColumn(
                2,
                ContenttypesGlobalizationUtil
                .globalize("person.ui.orgaunits.columns.type"),
                TABLE_COL_TYPE));

        setModelBuilder(new ModelBuilder(itemModel));

        columnModel.get(0).setCellRenderer(new EditCellRenderer());
    }

    /**
     * 
     */
    private class ModelBuilder extends LockableImpl implements TableModelBuilder {

        private final ItemSelectionModel itemModel;

        public ModelBuilder(final ItemSelectionModel itemModel) {
            this.itemModel = itemModel;
        }

        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            final GenericPerson person = (GenericPerson) itemModel
                                                         .getSelectedItem(state);

            return new Model(table, state, person);
        }

    }

    /**
     * 
     */
    private class Model implements TableModel {

        private final Table table;
        private final GenericOrganizationalUnitBundleCollection orgaUnits;

        /**
         * 
         * @param table
         * @param state
         * @param person 
         */
        public Model(final Table table, 
                     final PageState state, 
                     final GenericPerson person) {
            this.table = table;
            orgaUnits =
            new GenericOrganizationalUnitBundleCollection((DataCollection) 
                    person.getGenericPersonBundle().get("organizationalunits"));
        }

        /**
         * 
         * @return 
         */
        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        /**
         * 
         * @return 
         */
        @Override
        public boolean nextRow() {
            boolean ret;

            if ((orgaUnits != null) && orgaUnits.next()) {
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        /**
         * 
         * @param columnIndex
         * @return 
         */
        @Override
        public Object getElementAt(final int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return orgaUnits.getGenericOrganizationalUnit().getTitle();
                case 1:
                    return ((DataObject) orgaUnits.getGenericOrganizationalUnit()
                                                  .get("type")).get("label");
                default:
                    return null;
            }
        }

        /**
         * 
         * @param columnIndex
         * @return 
         */
        @Override
        public Object getKeyAt(final int columnIndex) {
            return orgaUnits.getGenericOrganizationalUnit().getID();
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
            //final com.arsdigita.cms.SecurityManager securityManager = CMS.getSecurityManager(state);
            final GenericOrganizationalUnit orgaUnit = new GenericOrganizationalUnit((BigDecimal) key);

            final ContentSection section = orgaUnit.getContentSection();//CMS.getContext().getContentSection();
            final ItemResolver resolver = section.getItemResolver();
            final Link link = new Link(value.toString(),
                                       resolver.generateItemURL(state,
                                                                orgaUnit,
                                                                section,
                                                                orgaUnit.getVersion()));
            return link;

        }

    }
}
