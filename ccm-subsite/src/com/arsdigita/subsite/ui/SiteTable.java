/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.subsite.ui;

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
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.subsite.Site;
import com.arsdigita.util.LockableImpl;

/**
 * A table showing all subsites in the system.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SiteTable extends Table implements TableActionListener {

    private static final String TABLE_COL_EDIT = "table_col_edit";
    private static final String TABLE_COL_DEL = "table_col_del";    
    private final SiteSelectionModel siteSelect;

    public SiteTable(final SiteSelectionModel siteSelect) {
        super();
        
        this.siteSelect = siteSelect;

        setEmptyView(new Label(SubsiteGlobalizationUtil.globalize("subsite.ui.no_subsites")));

        final TableColumnModel columnModel = getColumnModel();

        columnModel.add(new TableColumn(
                0, SubsiteGlobalizationUtil.globalize("subsite.ui.title.label")));

        columnModel.add(new TableColumn(
                1, SubsiteGlobalizationUtil.globalize("subsite.ui.hostname.label")));

        columnModel.add(new TableColumn(
                2, SubsiteGlobalizationUtil.globalize("subsite.ui.customfrontpage.label")));

        columnModel.add(new TableColumn(
                3, SubsiteGlobalizationUtil.globalize("subsite.ui.theme.label")));

        columnModel.add(new TableColumn(
                4, SubsiteGlobalizationUtil.globalize("subsite.ui.root_category.label")));

        columnModel.add(new TableColumn(
                5, SubsiteGlobalizationUtil.globalize("subsite.ui.edit"),
                TABLE_COL_EDIT));

        columnModel.add(new TableColumn(
                6, SubsiteGlobalizationUtil.globalize("subsite.ui.delete"),
                TABLE_COL_DEL));

        setModelBuilder(new SiteTableModelBuilder());

        columnModel.get(5).setCellRenderer(new EditCellRenderer());
        columnModel.get(6).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class SiteTableModelBuilder extends LockableImpl implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new SiteTableModel(table, state);
        }

    }

    private class SiteTableModel implements TableModel {

        private final Table table;
        private final DomainCollection sites;

        public SiteTableModel(final Table table, final PageState state) {
            this.table = table;
            sites = new DomainCollection(SessionManager.getSession().retrieve(
                    Site.BASE_DATA_OBJECT_TYPE));

        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            return sites.next();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final Site site = (Site) sites.getDomainObject();
            switch (columnIndex) {
                case 0:
                    return site.getTitle();
                case 1:
                    return site.getHostname();
                case 2:
                    return site.getFrontPage().getTitle();
                case 3:
                    return site.getStyleDirectory();
                case 4:
                    return site.getRootCategory().getDisplayName();
                case 5:
                    return SubsiteGlobalizationUtil.globalize("subsite.ui.edit");
                case 6:
                    return SubsiteGlobalizationUtil.globalize("subsite.ui.delete");
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return sites.getDomainObject().getOID().toString();
        }

    }

    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final ControlLink link = new ControlLink(new Label((GlobalizedMessage) value));
            return link;
        }

    }

    private class DeleteCellRenderer extends LockableImpl implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state, final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final ControlLink link = new ControlLink(new Label((GlobalizedMessage) value));
            link.setConfirmation(SubsiteGlobalizationUtil.globalize("subsite.ui.delete.confirm"));
            return link;
        }

    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        final PageState state = event.getPageState();

        final Site site = (Site) DomainObjectFactory.newInstance(OID.valueOf(event.getRowKey().
                toString()));

        final TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
            siteSelect.setSelectedObject(state, site);
        } else if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            site.delete();
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing
    }

}
