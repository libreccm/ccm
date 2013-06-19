/*
 * Copyright (c) 2011 Jens Pelzetter
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
package com.arsdigita.cms.publicpersonalprofile.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItem;
import com.arsdigita.cms.contenttypes.PublicPersonalProfileNavItemCollection;
import com.arsdigita.cms.contenttypes.ui.PublicPersonalProfileGlobalizationUtil;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicPersonalProfileNavItemsTable
        extends Table
        implements TableActionListener {

    private final static String TABLE_COL_UP = "table_col_up";
    private final static String TABLE_COL_DOWN = "table_col_down";
    private final static String TABLE_COL_DELETE = "table_col_delete";
    private final static String TABLE_COL_EDIT = "table_col_edit";    
    private final ParameterSingleSelectionModel navItemSelect;

    public PublicPersonalProfileNavItemsTable(
            final ParameterSingleSelectionModel navItemSelect) {
 
        this.navItemSelect = navItemSelect;

        setEmptyView(new Label(PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.no_nav_items")));

        TableColumnModel columnModel = getColumnModel();

        columnModel.add(new TableColumn(
                0,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.key").localize()));

        columnModel.add(new TableColumn(
                1,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.lang").localize()));

        columnModel.add(new TableColumn(
                2,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.label").localize()));

        columnModel.add(new TableColumn(
                3,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.generatorclass").localize()));

        columnModel.add(new TableColumn(
                4,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.edit").localize(),
                TABLE_COL_EDIT));

        columnModel.add(new TableColumn(
                5,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.delete").localize(),
                TABLE_COL_DELETE));

        columnModel.add(new TableColumn(
                6,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.up").localize(),
                TABLE_COL_UP));
        columnModel.add(new TableColumn(
                7,
                PublicPersonalProfileGlobalizationUtil.globalize(
                "publicpersonalprofile.ui.navitem.down").localize(),
                TABLE_COL_DOWN));

        setModelBuilder(new PublicPersonalProfileNavItemsTableModelBuilder());

        columnModel.get(4).setCellRenderer(new EditCellRenderer());
        columnModel.get(5).setCellRenderer(new DeleteCellRenderer());
        columnModel.get(6).setCellRenderer(new UpCellRenderer());
        columnModel.get(7).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);

    }

    private class PublicPersonalProfileNavItemsTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            return new PublicPersonalProfileNavItemsTableModel(table, state);
        }
    }

    private class PublicPersonalProfileNavItemsTableModel
            implements TableModel {

        private final Table table;
        private final PageState state;
        private final PublicPersonalProfileNavItemCollection navItems =
                                                             new PublicPersonalProfileNavItemCollection();
        private int lastOrder = 0;
        private int numberOfKeys;

        public PublicPersonalProfileNavItemsTableModel(final Table table,
                                                       final PageState state) {
            this.table = table;
            this.state = state;
            final PublicPersonalProfileNavItemCollection items =
                                                         new PublicPersonalProfileNavItemCollection();
            final Set<String> keys = new HashSet<String>();
            while (items.next()) {
                keys.add(items.getNavItem().getKey());
            }
            numberOfKeys = keys.size();
        }

        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        public boolean nextRow() {
            if (!navItems.isBeforeFirst()) {
                lastOrder = navItems.getNavItem().getOrder();
            }
            return navItems.next();
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return navItems.getNavItem().getKey();
                case 1:
                    return navItems.getNavItem().getLang();
                case 2:
                    return navItems.getNavItem().getLabel();
                case 3:
                    return navItems.getNavItem().getGeneratorClass();
                case 4:
                    return PublicPersonalProfileGlobalizationUtil.globalize(
                            "publicpersonalprofile.ui.navitem.edit").localize();
                case 5:
                    return PublicPersonalProfileGlobalizationUtil.globalize(
                            "publicpersonalprofile.ui.navitem.delete").localize();
                case 6:
                    if (navItems.getNavItem().getOrder() == lastOrder) {
                        return null;
                    } else {
                        return PublicPersonalProfileGlobalizationUtil.globalize(
                                "publicpersonalprofile.ui.navitem.up").localize();
                    }
                case 7:
                    if ((navItems.getNavItem().getOrder() == lastOrder)
                        || (navItems.getNavItem().getOrder() == numberOfKeys)) {
                        return null;
                    } else {
                        return PublicPersonalProfileGlobalizationUtil.globalize(
                                "publicpersonalprofile.ui.navitem.down").
                                localize();
                    }
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return navItems.getNavItem().getId();
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

            final ControlLink link = new ControlLink(value.toString());
            return link;
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

            final ControlLink link = new ControlLink(value.toString());
            link.setConfirmation((String) PublicPersonalProfileGlobalizationUtil.
                    globalize("publicpersonalprofile.ui.navitems.delete.confirm").
                    localize());
            return link;
        }
    }

    private class UpCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            if (row == 0) {
                final Label label = new Label("");
                return label;
            } else if (value == null) {
                final Label label = new Label("");
                return label;
            } else {
                final ControlLink link =
                                  new ControlLink((String) PublicPersonalProfileGlobalizationUtil.
                        globalize("publicpersonalprofile.ui.navitems.up").
                        localize());
                return link;
            }
        }
    }

    private class DownCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        private final PublicPersonalProfileNavItemCollection navItems =
                                                             new PublicPersonalProfileNavItemCollection();

        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            if ((navItems.size() - 1) == row) {
                final Label label = new Label("");
                return label;
            } else if (value == null) {
                final Label label = new Label("");
                return label;
            } else {
                final ControlLink link =
                                  new ControlLink((String) PublicPersonalProfileGlobalizationUtil.
                        globalize("publicpersonalprofile.ui.navitems.down").
                        localize());
                return link;
            }
        }
    }

    public void cellSelected(final TableActionEvent event) {
        final PageState state = event.getPageState();

        final PublicPersonalProfileNavItem navItem =
                                           new PublicPersonalProfileNavItem(
                new BigDecimal(event.getRowKey().toString()));

        final PublicPersonalProfileNavItemCollection navItems =
                                                     new PublicPersonalProfileNavItemCollection();

        final TableColumn column = getColumnModel().get(event.getColumn().
                intValue());

        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
            navItemSelect.setSelectedKey(state, navItem.getId());
        } else if (TABLE_COL_DELETE.equals(column.getHeaderKey().toString())) {
            navItem.delete();
        } else if (TABLE_COL_UP.equals(column.getHeaderKey().toString())) {
            navItems.swapWithPrevious(navItem);
        } else if (TABLE_COL_DOWN.equals(column.getHeaderKey().toString())) {
            navItems.swapWithNext(navItem);
        }
    }

    public void headSelected(final TableActionEvent event) {
        //Nothing to do
    }
}
