/*
 * Copyright (c) 2010 Jens Pelzetter,
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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.bebop.util.GlobalizationUtil;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;

/**
 * Table for showing the persons associated with an organization.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitPersonsTable extends Table implements TableActionListener {

    private final String TABLE_COL_EDIT = "table_col_edit";
    private final String TABLE_COL_DEL = "table_col_del";
    private ItemSelectionModel m_itemModel;

    public  GenericOrganizationalUnitPersonsTable(final ItemSelectionModel itemModel) {
        super();
        m_itemModel = itemModel;

        setEmptyView(new Label(ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.genericorgaunit.persons.none")));
        TableColumnModel tabModel = getColumnModel();

        tabModel.add(new TableColumn(
                0,
                ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.genericorgaunit.persons.order").localize(),
                TABLE_COL_EDIT
                ));
        tabModel.add(new TableColumn(
                1,
                ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.genericorgaunit.persons.name").localize()
                ));
        tabModel.add(new TableColumn(
                2,
               ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.genericorgaunit.persons.role").localize()
                ));
        tabModel.add(new TableColumn(
                3,
               ContenttypesGlobalizationUtil.globalize("cms.contenttypes.ui.genericorgaunit.persons.delete").localize()
                ));

        setModelBuilder(new GenericOrganizationalUnitTableModelBuilder(itemModel));

        tabModel.get(0).setCellRenderer(new EditCellRenderer());
        tabModel.get(3).setCellRenderer(new DeleteCellRenderer());
    }

    private class GenericOrganizationalUnitTableModelBuilder
            extends LockableImpl
            implements TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public GenericOrganizationalUnitTableModelBuilder(
                ItemSelectionModel itemModel) {
            m_itemModel = itemModel;
        }

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            GenericOrganizationalUnit orgaunit =
                    (GenericOrganizationalUnit) m_itemModel.getSelectedObject(state);
            return new GenericOrganizationalUnitTableModel(table, state, orgaunit);
        }
    }

    private class GenericOrganizationalUnitTableModel implements TableModel {
        private final int MAX_DESC_LENGTH = 25;
        private Table m_table;
        private GenericOrganizationalUnitPersonCollection m_personsCollection;
        private GenericPerson m_person;        

        private GenericOrganizationalUnitTableModel(
                Table table,
                PageState state,
                GenericOrganizationalUnit orgaunit
                ) {
            m_table = table;
            m_personsCollection = orgaunit.getPersons();
        }

        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if ((m_personsCollection != null)
                    && m_personsCollection.next()) {
                m_person = m_personsCollection.getPerson();
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        public Object getElementAt(int columnIndex) {
            switch(columnIndex) {
                case 0:
                    return m_personsCollection.getPersonOrder();
                case 1:
                    return String.format("%s %s %s %s", 
                            m_person.getTitlePre(),
                            m_person.getGivenName(),
                            m_person.getSurname(),
                            m_person.getTitlePost());
                case 2:
                    return m_personsCollection.getRoleName();
                case 3:
                    return GlobalizationUtil.globalize("cms.ui.delete").localize();
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return m_person.getID();
        }
    }

    private class EditCellRenderer extends LockableImpl implements TableCellRenderer {

        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);
            GenericPerson person = (GenericPerson) m_itemModel.getSelectedObject(
                    state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                    com.arsdigita.cms.SecurityManager.EDIT_ITEM,
                    person);
            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                return link;
            } else {
                return new Label(value.toString());
            }
        }
    }

    private class DeleteCellRenderer extends LockableImpl implements TableCellRenderer {

        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            com.arsdigita.cms.SecurityManager securityManager = Utilities.
                    getSecurityManager(state);
            GenericPerson person = (GenericPerson) m_itemModel.getSelectedObject(
                    state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                    com.arsdigita.cms.SecurityManager.DELETE_ITEM,
                    person);
            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) ContenttypesGlobalizationUtil.
                        globalize(
                        "cms.contenttypes.ui.genericorgaunit.persons.confirm_delete").
                        localize());
                return link;
            } else {
                return new Label(value.toString());
            }
        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        GenericPerson person = new GenericPerson(new BigDecimal(event.getRowKey().toString()));

        GenericOrganizationalUnit orga = (GenericOrganizationalUnit) m_itemModel.getSelectedObject(state);

        TableColumn col = getColumnModel().get(event.getColumn().intValue());

        if (col.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        }

        if (col.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            orga.removePerson(person);
        }
    }


    @Override
    public void headSelected(TableActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
