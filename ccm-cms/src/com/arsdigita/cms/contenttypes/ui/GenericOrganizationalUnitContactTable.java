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
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactTypeCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Table for showing the contacts associated with an organization.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitContactTable extends Table implements
        TableActionListener {

    private final static Logger s_log = Logger.getLogger(
            GenericOrganizationalUnitContactTable.class);
    private final static String TABLE_COL_EDIT = "table_col_edit";
    private final static String TABLE_COL_DEL = "table_col_del";
    private final static String TABLE_COL_UP = "table_col_up";
    private final static String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;

    public GenericOrganizationalUnitContactTable(
            final ItemSelectionModel itemModel) {
        super();
        this.m_itemModel = itemModel;

        setEmptyView(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contacts.none")));
        TableColumnModel tabModel = getColumnModel();

        tabModel.add(new TableColumn(
                0,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.type").localize()));
        tabModel.add(new TableColumn(
                1,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.title").localize()));
        tabModel.add(new TableColumn(
                2,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.action").localize(),
                TABLE_COL_DEL));
        tabModel.add(new TableColumn(
                3,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.up").localize(),
                TABLE_COL_UP));
        tabModel.add(new TableColumn(
                4,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.down").localize(),
                TABLE_COL_DOWN));

        setModelBuilder(
                new GenericOrganizationalUnitTableModelBuilder(itemModel));

        tabModel.get(1).setCellRenderer(new EditCellRenderer());
        tabModel.get(2).setCellRenderer(new DeleteCellRenderer());
        tabModel.get(3).setCellRenderer(new UpCellRenderer());
        tabModel.get(3).setCellRenderer(new DownCellRenderer());
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
                                      (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(state);
            return new GenericOrganizationalUnitTableModel(table, state,
                                                           orgaunit);
        }
    }

    private class GenericOrganizationalUnitTableModel implements TableModel {

        private final int MAX_DESC_LENGTH = 25;
        private Table m_table;
        private GenericOrganizationalUnitContactCollection m_contactCollection;
        private GenericContact m_contact;
        private GenericContactTypeCollection contacttypes =
                                             new GenericContactTypeCollection();

        private GenericOrganizationalUnitTableModel(
                Table table,
                PageState state,
                GenericOrganizationalUnit orgaunit) {
            m_table = table;
            m_contactCollection = orgaunit.getContacts();
        }

        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        public boolean nextRow() {
            boolean ret;

            if ((m_contactCollection != null)
                && m_contactCollection.next()) {
                m_contact = m_contactCollection.getContact();
                ret = true;
            } else {
                ret = false;
            }

            return ret;
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return contacttypes.getRelationAttribute(m_contactCollection.
                            getContactType(),
                                                       DispatcherHelper.
                            getNegotiatedLocale().getLanguage());
                case 1:
                    return m_contact.getTitle();
                case 2:
                    return GlobalizationUtil.globalize("cms.ui.delete").localize();
                default:
                    return null;
            }
        }

        public Object getKeyAt(int columnIndex) {
            return m_contact.getID();
        }
    }

    private class EditCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            GenericOrganizationalUnit orgaunit =
                                      (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(state);

            boolean canEdit = securityManager.canAccess(state.getRequest(),
                                                        SecurityManager.EDIT_ITEM,
                                                        orgaunit);
            if (canEdit) {
                ControlLink link = new ControlLink(value.toString());
                return link;
            } else {
                return new Label(value.toString());
            }
        }
    }

    private class DeleteCellRenderer
            extends LockableImpl
            implements TableCellRenderer {

        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {
            SecurityManager securityManager =
                            Utilities.getSecurityManager(state);
            GenericOrganizationalUnit orgaunit =
                                      (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(state);

            boolean canDelete = securityManager.canAccess(
                    state.getRequest(),
                    SecurityManager.DELETE_ITEM,
                    orgaunit);
            if (canDelete) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) ContenttypesGlobalizationUtil.
                        globalize(
                        "cms.contenttypes.ui.genericorgaunit.confirm_delete").
                        localize());
                return link;
            } else {
                return new Label(value.toString());
            }
        }
    }

    private class UpCellRenderer extends LockableImpl implements
            TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {

            if (0 == row) {
                s_log.debug("Row is first row in table, don't show up-link");
                return new Label("");
            } else {
                ControlLink link = new ControlLink("up");
                return link;
            }

        }
    }

    private class DownCellRenderer extends LockableImpl implements
            TableCellRenderer {

        @Override
        public Component getComponent(
                Table table,
                PageState state,
                Object value,
                boolean isSelected,
                Object key,
                int row,
                int col) {

            GenericOrganizationalUnit orgaunit = (GenericOrganizationalUnit) m_itemModel.
                    getSelectedObject(state);
            GenericOrganizationalUnitContactCollection contacts =
                                                       orgaunit.getContacts();

            if ((contacts.size() - 1) == row) {
                s_log.debug("Row is last row in table, don't show down-link");
                return new Label("");
            } else {
                ControlLink link = new ControlLink("down");
                return link;
            }

        }
    }

    @Override
    public void cellSelected(TableActionEvent event) {
        PageState state = event.getPageState();

        GenericContact contact =
                       new GenericContact(new BigDecimal(event.getRowKey().
                toString()));

        GenericOrganizationalUnit orgaunit =
                                  (GenericOrganizationalUnit) m_itemModel.
                getSelectedObject(state);

        GenericOrganizationalUnitContactCollection contacts =
                orgaunit.getContacts();

        TableColumn column = getColumnModel().get(event.getColumn().intValue());

        if (column.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        }
        else if(column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            orgaunit.removeContact(contact);
        } else if(column.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            contacts.swapWithPrevious(contact);
        } else if(column.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            contacts.swapWithNext(contact);
        }

    }

    @Override
    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
