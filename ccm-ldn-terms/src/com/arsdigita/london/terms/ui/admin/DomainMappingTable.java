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
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainService;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.london.terms.Domain;
import com.arsdigita.london.terms.util.TermsGlobalizationUtil;
import com.arsdigita.london.util.ui.parameters.DomainObjectParameter;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.LockableImpl;
import com.arsdigita.web.Application;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class DomainMappingTable extends Table implements TableActionListener {

    private static final String TABLE_COL_DEL = "table_col_del";
    private final DomainObjectParameter selected;

    public DomainMappingTable(final DomainObjectParameter selected) {
        super();

        this.selected = selected;

        setEmptyView(new Label(TermsGlobalizationUtil.globalize(
                                    "terms.domain.ui.no_mappings")));

        final TableColumnModel columnModel = getColumnModel();

        columnModel.add(new TableColumn(
                0, 
                TermsGlobalizationUtil.globalize("terms.domain.mapping.ui.application")));
        columnModel.add(new TableColumn(
                1, 
                TermsGlobalizationUtil.globalize("terms.domain.mapping.ui.context")));
        columnModel.add(new TableColumn(
                2, 
                TermsGlobalizationUtil.globalize("terms.domain.mapping.ui.remove"), 
                TABLE_COL_DEL));

        setModelBuilder(new DomainMappingTableModelBuilder());

        columnModel.get(2).setCellRenderer(new DeleteCellRenderer());

        addTableActionListener(this);
    }

    private class DomainMappingTableModelBuilder extends LockableImpl implements TableModelBuilder {

        @Override
        public TableModel makeModel(final Table table,
                                    final PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            return new DomainMappingTableModel(table, state);
        }

    }

    private class DomainMappingTableModel extends DomainService implements TableModel {

        private final Table table;
        private final DomainCollection useContexts;

        public DomainMappingTableModel(final Table table, final PageState state) {
            this.table = table;

            final Domain domain = (Domain) state.getValue(selected);

            if (domain == null) {
                useContexts = null;
            } else {
                useContexts = domain.getUseContexts();
            }
        }

        @Override
        public int getColumnCount() {
            return table.getColumnModel().size();
        }

        @Override
        public boolean nextRow() {
            if (useContexts == null) {
                return false;
            } else {
                return useContexts.next();
            }
        }

        @Override
        public Object getElementAt(final int columnIndex) {

            switch (columnIndex) {
                case 0:
                    final DomainObject obj = DomainObjectFactory
                            .newInstance((DataObject) get(useContexts
                                    .getDomainObject(), "categoryOwner"));
                    if (obj instanceof Application) {
                        return ((Application) obj).getPath();
                    } else {
                        return obj.getOID().toString();
                    }
                case 1:
                    return get(useContexts.getDomainObject(), "useContext");
                case 2:
                    return TermsGlobalizationUtil.globalize("terms.domain.mapping.ui.delete");
                default:
                    return null;
            }
        }

        @Override
        public Object getKeyAt(final int columnIndex) {
            return useContexts.getDomainObject().getOID().toString();
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
                    "terms.domain.mapping.ui.delete_confirm"));
            return link;
        }

    }

    @Override
    public void cellSelected(final TableActionEvent event) {
        final PageState state = event.getPageState();
        final DomainObject obj = DomainObjectFactory.newInstance(OID.valueOf(event.getRowKey().
                toString()));

        final TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (TABLE_COL_DEL.equals(column.getHeaderKey().toString())) {
            obj.delete();
        }
    }

    @Override
    public void headSelected(final TableActionEvent event) {
        //Nothing
    }

}
