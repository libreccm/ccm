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
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactTypeCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationContactTypeCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.globalization.GlobalizationHelper;
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
    private final static String TABLE_COL_EDIT_ASSOC = "table_col_edit_assoc";
    private final static String TABLE_COL_DEL = "table_col_del";
    private final static String TABLE_COL_UP = "table_col_up";
    private final static String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;
    private GenericOrganizationalUnitContactPropertiesStep editStep;

    public GenericOrganizationalUnitContactTable(
            final ItemSelectionModel itemModel,
            final GenericOrganizationalUnitContactPropertiesStep editStep) {
        super();
        this.m_itemModel = itemModel;
        this.editStep = editStep;

        setEmptyView(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contacts.none")));
        TableColumnModel tabModel = getColumnModel();

        tabModel.add(new TableColumn(
                0,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.type")));
        tabModel.add(new TableColumn(
                1,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.title"),
                TABLE_COL_EDIT));
        tabModel.add(new TableColumn(
                2,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.edit").localize(),
                TABLE_COL_EDIT_ASSOC));
        tabModel.add(new TableColumn(
                3,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.action").localize(),
                TABLE_COL_DEL));
        tabModel.add(new TableColumn(
                4,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.up").localize(),
                TABLE_COL_UP));
        tabModel.add(new TableColumn(
                5,
                ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.genericorgaunit.contact.down").localize(),
                TABLE_COL_DOWN));

        setModelBuilder(
                new GenericOrganizationalUnitTableModelBuilder(itemModel));

        tabModel.get(1).setCellRenderer(new EditCellRenderer());
        tabModel.get(2).setCellRenderer(new EditAssocCellRenderer());
        tabModel.get(3).setCellRenderer(new DeleteCellRenderer());
        tabModel.get(4).setCellRenderer(new UpCellRenderer());
        tabModel.get(5).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);
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

        private Table m_table;
        private GenericOrganizationalUnitContactCollection m_contactCollection;
        private GenericContact m_contact;
        private GenericOrganizationContactTypeCollection m_contacttypes =
                                             new GenericOrganizationContactTypeCollection();

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
            s_log.debug(String.format("contacttypes.size() = %d",
                                      m_contacttypes.size()));
            switch (columnIndex) {
                case 0:
                    s_log.debug(String.format(
                            "Getting human readable contact type for contact type \"%s\"...",
                            m_contactCollection.getContactType()));
                    String lang =
                           GlobalizationHelper.getNegotiatedLocale().
                            getLanguage();
                    if (m_contacttypes.size() <= 0) {
                        s_log.warn(String.format("No matching relation "
                                                 + "attributes for contact type '%s' found. "
                                                 + "Using key as fallback.",
                                                 m_contactCollection.
                                getContactType()));
                        return m_contactCollection.getContactType();
                    }
                    if (m_contacttypes.getRelationAttribute(m_contactCollection.
                            getContactType(), lang) == null) {
                        s_log.debug(String.format(
                                "No human readable name "
                                + "found for '%s' for language '%s' Using key.",
                                m_contactCollection.getContactType(),
                                lang));
                        String contactType =
                               m_contactCollection.getContactType();
                        m_contacttypes.close();
                        return contactType;
                    } else {
                        s_log.debug(String.format(
                                "Human readable contact type is: \"%s\"...",
                                m_contacttypes.getRelationAttribute(
                                m_contactCollection.getContactType(),
                                GlobalizationHelper.getNegotiatedLocale().
                                getLanguage()).getName()));
                        return m_contacttypes.getRelationAttribute(
                                m_contactCollection.getContactType(),
                                GlobalizationHelper.getNegotiatedLocale().
                                getLanguage()).getName();
                    }
                case 1:
                    return m_contact.getTitle();
                case 2:
                    return GlobalizationUtil.globalize("cms.ui.edit_assoc").
                            localize();
                case 3:
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
                GenericContact contact;
                try {
                    contact = new GenericContact((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    return new Label(value.toString());
                }
                ContentSection section = contact.getContentSection();//CMS.getContext().getContentSection();
                ItemResolver resolver = section.getItemResolver();
                Link link =
                     new Link(String.format("%s (%s)",
                                            value.toString(),
                                            contact.getLanguage()),
                              resolver.generateItemURL(state,
                                                       contact,
                                                       section,
                                                       contact.getVersion()));
                return link;
            } else {
                GenericContact contact;
                try {
                    contact = new GenericContact((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    return new Label(value.toString());
                }
                Label label = new Label(String.format("%s (%s)", value.toString(),
                                                                 contact.getLanguage()));
                
                return label;
            }
        }
    }

    private class EditAssocCellRenderer
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

            GenericOrganizationalUnit orgaunit =
                                      (GenericOrganizationalUnit) m_itemModel.
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
        } else if (column.getHeaderKey().toString().equals(
                TABLE_COL_EDIT_ASSOC)) {
            while (contacts.next()) {
                if (contacts.getContact().equals(contact)) {
                    break;
                }
            }

            editStep.setSelectedContact(contact);
            editStep.setSelectedContactType(contacts.getContactType());

            contacts.close();

            editStep.showComponent(state,
                                   GenericOrganizationalUnitContactPropertiesStep.ADD_CONTACT_SHEET_NAME);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            orgaunit.removeContact(contact);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            contacts.swapWithPrevious(contact);
        } else if (column.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            contacts.swapWithNext(contact);
        }

    }

    @Override
    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
