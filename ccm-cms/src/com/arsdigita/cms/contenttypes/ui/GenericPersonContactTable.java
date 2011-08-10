/*
 * Copyright (C) 2010 Sören Bernstein All Rights Reserved.
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
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactTypeCollection;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.util.ContenttypesGlobalizationUtil;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.LockableImpl;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Lists all existing contact entries for a selected contact.
 *
 * @author Sören Bernstein (quasimodo) quasi@barkhof.uni-bremen.de
 */
public class GenericPersonContactTable extends Table implements
        TableActionListener {

    private final static Logger s_log = Logger.getLogger(
            GenericPersonContactTable.class);
    private final static String TABLE_COL_EDIT = "table_col_edit";
    private final static String TABLE_COL_DEL = "table_col_del";
    private final static String TABLE_COL_UP = "table_col_up";
    private final static String TABLE_COL_DOWN = "table_col_down";
    private ItemSelectionModel m_itemModel;

    /**
     * Creates a new instance of GenericPersonGenericPersonTable
     */
    public GenericPersonContactTable(final ItemSelectionModel itemModel) {
        super();
        s_log.debug("Constructor begin...");
        this.m_itemModel = itemModel;

        // if table is empty:
        setEmptyView(new Label(ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.contacts.none")));
        TableColumnModel tab_model = getColumnModel();

        tab_model.add(new TableColumn(0, ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.contact.type").localize()));
        tab_model.add(new TableColumn(1, ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.contact.title").localize(),
                                      TABLE_COL_EDIT));
        tab_model.add(new TableColumn(2, ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person.contact.del").localize(),
                                      TABLE_COL_DEL));
        tab_model.add(new TableColumn(3, ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person_contact.up").localize(),
                                      TABLE_COL_UP));
        tab_model.add(new TableColumn(4, ContenttypesGlobalizationUtil.globalize(
                "cms.contenttypes.ui.person_contact.down").localize(),
                                      TABLE_COL_DOWN));

        setModelBuilder(new GenericPersonTableModelBuilder(itemModel));

        tab_model.get(1).setCellRenderer(new EditCellRenderer());
        tab_model.get(2).setCellRenderer(new DeleteCellRenderer());
        tab_model.get(3).setCellRenderer(new UpCellRenderer());
        tab_model.get(4).setCellRenderer(new DownCellRenderer());

        addTableActionListener(this);
        s_log.debug("Constructor finished.");
    }

    /**
     * XXXX
     *
     */
    private class GenericPersonTableModelBuilder extends LockableImpl implements
            TableModelBuilder {

        private ItemSelectionModel m_itemModel;

        public GenericPersonTableModelBuilder(ItemSelectionModel itemModel) {
            s_log.debug("Creating table model builder...");
            m_itemModel = itemModel;
            s_log.debug("Created table model builder.");
        }

        public TableModel makeModel(Table table, PageState state) {
            s_log.debug("Making model...");
            table.getRowSelectionModel().clearSelection(state);
            GenericPerson person = (GenericPerson) m_itemModel.getSelectedObject(
                    state);
            s_log.debug("Made model...");
            return new GenericPersonTableModel(table, state, person);
        }
    }

    /**
     * XXX
     *
     */
    private class GenericPersonTableModel implements TableModel {

        final private int MAX_DESC_LENGTH = 25;
        private Table m_table;
        private GenericPersonContactCollection m_contactCollection;
        private GenericContact m_contact;
        private GenericContactTypeCollection contacttypes =
                                             new GenericContactTypeCollection();

        private GenericPersonTableModel(Table t, PageState ps,
                                        GenericPerson person) {
            s_log.debug("Creating table model...");
            m_table = t;
            m_contactCollection = person.getContacts();
            s_log.debug("Created table model...");
        }

        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        /**
         * Check collection for the existence of another row.
         *
         * If exists, fetch the value of current GenericPersonEntryCollection object
         * into m_contact class variable.
         */
        public boolean nextRow() {
            s_log.debug("Next row?");
            if (m_contactCollection != null && m_contactCollection.next()) {
                m_contact = m_contactCollection.getContact();
                s_log.debug("Yes.");
                return true;
            } else {
                s_log.debug("No.");
                return false;
            }
        }

        /**
         * Return the
         * @see com.arsdigita.bebop.table.TableModel#getElementAt(int)
         */
        public Object getElementAt(int columnIndex) {
            s_log.debug(String.format("Getting element at %d...",
                                      columnIndex));
            switch (columnIndex) {
                case 0:
                    s_log.debug(String.format(
                            "Getting human readable contact type for contact type \"%s\"...",
                            m_contactCollection.getContactType()));
                    String lang = DispatcherHelper.getNegotiatedLocale().
                            getLanguage();
                    if (contacttypes.size() <= 0) {
                        s_log.warn("No contact entry types found. Using key as "
                                   + "fallback.");
                        return m_contactCollection.getContactType();
                    }
                    if ((contacttypes.getRelationAttribute(m_contactCollection.
                         getContactType(), lang) == null)) {
                        s_log.debug(String.format(
                                "No human readable name "
                                + "found for '%s' for language '%s' Using key.",
                                m_contactCollection.getContactType(),
                                lang));
                        return m_contactCollection.getContactType();
                    }
                    s_log.debug(String.format(
                            "Human readable contact type is: \"%s\"...",
                            contacttypes.getRelationAttribute(
                            m_contactCollection.getContactType(),
                            lang)));

                    Object name = contacttypes.getRelationAttribute(
                            m_contactCollection.getContactType(),
                            lang).getName();

                    // Close collection to prevent open ResultSet
                    //contacttypes.close();

                    return name;
                case 1:
                    return m_contact.getTitle();
                case 2:
                    return GlobalizationUtil.globalize("cms.ui.delete").localize();
                default:
                    return null;
            }
        }

        /**
         *
         * @see com.arsdigita.bebop.table.TableModel#getKeyAt(int)
         */
        public Object getKeyAt(int columnIndex) {
            return m_contact.getID();
        }
    }

    /**
     * Check for the permissions to edit item and put either a Label or
     * a ControlLink accordingly.
     */
    private class EditCellRenderer extends LockableImpl implements
            TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {

            SecurityManager sm = Utilities.getSecurityManager(state);
            GenericPerson person = (GenericPerson) m_itemModel.getSelectedObject(
                    state);

            boolean canEdit = sm.canAccess(state.getRequest(),
                                           SecurityManager.EDIT_ITEM,
                                           person);
            if (canEdit) {
                GenericContact contact;
                try {
                    contact = new GenericContact((BigDecimal) key);
                } catch (DataObjectNotFoundException ex) {
                    return new Label(value.toString());
                }
                ContentSection section = CMS.getContext().getContentSection();
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
                
                Label label = new Label(String.format("%s (%s)",
                                                      value.toString(),
                                                      contact.getLanguage()));
                
                return label;
            }
        }
    }

    /**
     * Check for the permissions to delete item and put either a Label or
     * a ControlLink accordingly.
     */
    private class DeleteCellRenderer extends LockableImpl implements
            TableCellRenderer {

        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {

            SecurityManager sm = Utilities.getSecurityManager(state);
            GenericPerson person = (GenericPerson) m_itemModel.getSelectedObject(
                    state);

            boolean canDelete = sm.canAccess(state.getRequest(),
                                             SecurityManager.DELETE_ITEM,
                                             person);
            if (canDelete) {
                ControlLink link = new ControlLink(value.toString());
                link.setConfirmation((String) ContenttypesGlobalizationUtil.
                        globalize("cms.contenttypes.ui.person.confirm_delete").
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

            GenericPerson person = (GenericPerson) m_itemModel.getSelectedObject(
                    state);
            GenericPersonContactCollection contacts = person.getContacts();
            if ((contacts.size() - 1) == row) {
                s_log.debug("Row is last row in table, don't show down-link");
                return new Label("");
            } else {
                ControlLink link = new ControlLink("down");
                return link;
            }
        }
    }

    /**
     * Provide implementation to TableActionListener method.
     * Code that comes into picture when a link on the table is clicked.
     * Handles edit and delete event.
     */
    public void cellSelected(TableActionEvent evt) {

        PageState state = evt.getPageState();

        // Get selected GenericContact
        GenericContact contact = new GenericContact(new BigDecimal(evt.getRowKey().
                toString()));

        // Get GenericPerson
        GenericPerson person = (GenericPerson) m_itemModel.getSelectedObject(
                state);

        GenericPersonContactCollection contacts = person.getContacts();

        // Get selected column
        TableColumn col = getColumnModel().get(evt.getColumn().intValue());

        // Edit
        if (col.getHeaderKey().toString().equals(TABLE_COL_EDIT)) {
        } // Delete
        else if (col.getHeaderKey().toString().equals(TABLE_COL_DEL)) {
            person.removeContact(contact);
        } else if (col.getHeaderKey().toString().equals(TABLE_COL_UP)) {
            contacts.swapWithPrevious(contact);
        } else if (col.getHeaderKey().toString().equals(TABLE_COL_DOWN)) {
            contacts.swapWithNext(contact);
        }
    }

    /**
     * provide Implementation to TableActionListener method.
     * Does nothing in our case.
     */
    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
