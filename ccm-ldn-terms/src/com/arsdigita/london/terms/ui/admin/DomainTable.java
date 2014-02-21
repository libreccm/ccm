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
package com.arsdigita.london.terms.ui.admin;

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
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.util.TermsGlobalizationUtil;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class DomainTable extends Table implements TableActionListener {

    private static final String TABLE_COL_EDIT = "table_col_edit";
    private static final String TABLE_COL_DEL = "table_col_del";
    private final DomainObjectParameter selected;

    public DomainTable(final DomainObjectParameter selected) {
        super();
        
        this.selected = selected;

        setEmptyView(new Label(TermsGlobalizationUtil.globalize(
                               "terms.domain.ui.no_domains")));

        final TableColumnModel columnModel = getColumnModel();

        columnModel.add(new TableColumn(
                0, 
                TermsGlobalizationUtil.globalize("terms.domain.ui.key_label")));

        columnModel.add(new TableColumn(
                1, 
                TermsGlobalizationUtil.globalize("terms.domain.ui.title_label")));

        columnModel.add(new TableColumn(
                2, 
                TermsGlobalizationUtil.globalize("terms.domain.ui.url_label")));

        columnModel.add(new TableColumn(
                3, 
                TermsGlobalizationUtil.globalize("terms.domain.ui.version_label")));

        columnModel.add(new TableColumn(
                4, 
                TermsGlobalizationUtil.globalize("terms.domain.ui.released_label")));

        columnModel.add(new TableColumn(
                5,
                TermsGlobalizationUtil.globalize("terms.domain.ui.action_edit"),
                TABLE_COL_EDIT));

        columnModel.add(new TableColumn(
                6,
                TermsGlobalizationUtil.globalize("terms.domain.ui.action_delete"),
                TABLE_COL_DEL));

        setModelBuilder(new DomainTableModelBuilder());

        columnModel.get(5).setCellRenderer(new EditCellRenderer());
        columnModel.get(6).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);

    }

    private class DomainTableModelBuilder extends LockableImpl 
                                          implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table, final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new DomainTableModel(table, state);
        }

    }

    private class DomainTableModel implements TableModel {

        private final Table table;
        private final DomainCollection domains;

        public DomainTableModel(final Table table, final PageState state) {
            this.table = table;
            domains = new DomainCollection(SessionManager.getSession().retrieve(
                    Domain.BASE_DATA_OBJECT_TYPE));
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            return domains.next();
        }

        @Override
        public Object getElementAt(final int columnIndex) {
            final Domain domain = (Domain) domains.getDomainObject();

            switch (columnIndex) {
                case 0:
                    return domain.getKey();
                case 1:
                    return domain.getTitle();
                case 2:
                    return domain.getURL().toString();
                case 3:
                    return domain.getVersion();
                case 4:
                    return domain.getReleased().toString();
                case 5:
                    return TermsGlobalizationUtil.globalize(
                            "terms.domain.ui.action_edit");
                case 6:
                    return TermsGlobalizationUtil.globalize(
                            "terms.domain.ui.action_delete");
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return domains.getDomainObject().getOID().toString();
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
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            final ControlLink link = new ControlLink(new Label((GlobalizedMessage) value));
            link.setConfirmation(TermsGlobalizationUtil.globalize(
                                 "terms.domain.ui.delete.confirm"));
            return link;
        }

    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        final PageState state = event.getPageState();

        final Domain domain = (Domain) DomainObjectFactory.newInstance(OID.valueOf(
                event.getRowKey().toString()));
        
        final TableColumn column = getColumnModel().get(event.getColumn().intValue());
        
        if (TABLE_COL_EDIT.equals(column.getHeaderKey().toString())) {
            state.setValue(selected, domain);
        } else if(TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            domain.delete();
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing
    }

}
