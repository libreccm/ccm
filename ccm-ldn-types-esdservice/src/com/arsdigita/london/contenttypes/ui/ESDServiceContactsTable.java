/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.london.contenttypes.ui;

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
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.london.contenttypes.Contact;
import com.arsdigita.london.contenttypes.ESDService;
import com.arsdigita.london.contenttypes.util.ContactGlobalizationUtil;
import com.arsdigita.london.contenttypes.util.ESDServiceGlobalizationUtil;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;

import java.math.BigDecimal;

/**
 * A table which displays a list of
 * <code>Contact</code> objects to choose from for the
 * <code>ESDService</code> content type.
 *
 * @author Shashin Shinde <a
 * href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ESDServiceContactsTable.java 451 2005-03-20 23:11:22Z mbooth $
 */
public class ESDServiceContactsTable extends Table implements TableActionListener {

    // match columns by (symbolic) index, makes for easier reordering
    private static final int GIVENNAME_COL_IDX = 0;
    private static final int FAMILYNAME_COL_IDX = 1;
    private static final int TYPE_COL_IDX = 2;
    private static final int CHOOSE_COL_IDX = 3;
    
    private static final String COL_CHOOSE_CONTACT = "Select";
    private ItemSelectionModel m_selService;
    private ESDServiceChooseContactStep m_parent;

    /**
     * Constructor.
     *
     * @param selService, selection model which will give
     * the <code>ESDService</code> object that we are dealing with.
     * @param parent, component whose display pane will be shown when the
     * selection of desired contact is done.
     */
    public ESDServiceContactsTable(ItemSelectionModel selService,
                                   ESDServiceChooseContactStep parent) {
        super();

        m_parent = parent;
        m_selService = selService;

        TableColumnModel model = getColumnModel();
        model.add(new TableColumn(
                GIVENNAME_COL_IDX,
                new Label(ContactGlobalizationUtil.globalize(
                          "london.contenttypes.ui.contact.givenname")
                ) )); 
        model.add(new TableColumn(
                FAMILYNAME_COL_IDX, 
                new Label(ContactGlobalizationUtil.globalize(
                         "london.contenttypes.ui.contact.familyname")
                ) )); 
        model.add(new TableColumn(
                TYPE_COL_IDX, 
                new Label(ContactGlobalizationUtil.globalize(
                          "london.contenttypes.ui.contact.type")
                ) )); 
        model.add(new TableColumn(
                CHOOSE_COL_IDX, 
                new Label(ESDServiceGlobalizationUtil.globalize(
                          "london.contenttypes.ui.esdservice.select_contact")
                ) )); 

        setEmptyView(new Label(ESDServiceGlobalizationUtil.globalize(
                               "london.contenttypes.ui.esdservice.no_contacts")
                               ));
        setModelBuilder(new ContactsTableModelBuilder());

        model.get(3).setCellRenderer(new SelectCellRenderer());

        addTableActionListener(this);

    }

    /**
     * Private class Model Builder to build the required data for the table.
     */
    private class ContactsTableModelBuilder extends LockableImpl 
                                            implements TableModelBuilder {

        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);
            DataCollection m_contacts = SessionManager.getSession()
                                        .retrieve(Contact.BASE_DATA_OBJECT_TYPE);
            m_contacts.addEqualsFilter(ContentItem.VERSION, ContentItem.DRAFT);
            return new ContactsTableModel(table, m_contacts);
        }
    }

    /**
     * Internal private class
     */
    private class ContactsTableModel implements TableModel {

        private Table m_table;
        private DataCollection m_contacts;
        private Contact m_contact;

        private ContactsTableModel(Table t, DataCollection cts) {
            m_table = t;
            m_contacts = cts;
        }

        /**
         * Return the no. of columns in column model we have constructed.
         */
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        /**
         * check collection for the existence of another row.If it has fetch the
         * value of Contact object into m_contact class variable.
         */
        public boolean nextRow() {
            if (m_contacts.next()) {
                m_contact = (Contact) DomainObjectFactory
                            .newInstance(m_contacts.getDataObject());
                return true;
            } else {
                return false;
            }
        }

        /**
         * Return the attributes of Contact object in the current row.
         *
         * @see com.arsdigita.bebop.table.TableModel#getElementAt(int)
         */
        public Object getElementAt(int columnIndex) {

            switch (columnIndex) {
                case GIVENNAME_COL_IDX:
                    return m_contact.getGivenName();
                case FAMILYNAME_COL_IDX:
                    return m_contact.getFamilyName();
                case TYPE_COL_IDX:
                    return m_contact.getContactTypeName();
                case CHOOSE_COL_IDX:
                    return  new Label(ESDServiceGlobalizationUtil.globalize(
                          "london.contenttypes.ui.esdservice.select")
                            );
                default:
                    return null;
            }
        }

        /**
         * Always return the ID of
         * <code>Contact</code> represented by current row.
         *
         * @see com.arsdigita.bebop.table.TableModel#getKeyAt(int)
         */
        public Object getKeyAt(int columnIndex) {
            return m_contact.getID();
        }
    }

    /**
     * Internal private class to handle the click on the select link in the
     * table of available contacts.
     */
    private class SelectCellRenderer extends LockableImpl 
                                     implements TableCellRenderer {

        /**
         * 
         * @param table
         * @param state
         * @param value        
         * @param isSelected
         * @param key
         * @param row
         * @param column
         * @return             the link to assoziate the selected
         *                     contact entry with the service editet.
         */
        public Component getComponent(Table table, PageState state, Object value,
                                      boolean isSelected, Object key,
                                      int row, int column) {

            ControlLink link = new ControlLink( (Component)value );
            link.setConfirmation(ESDServiceGlobalizationUtil.globalize(
                    "london.contenttypes.ui.esdservice.select_this_contact"));
            return link;
        }
    }

    /**
     * Provide implementation to TableActionListener method. Code that comes
     * into picture when select link in the table is clicked. Handles selection
     * event.
     */
    public void cellSelected(TableActionEvent evt) {

        PageState state = evt.getPageState();
        TableColumn col = getColumnModel().get(evt.getColumn().intValue());

        if ( CHOOSE_COL_IDX == evt.getColumn() ) {
            m_parent.showDisplayPane(state);
            BigDecimal contactID = new BigDecimal(evt.getRowKey().toString());
            ESDService service = (ESDService) m_selService.getSelectedObject(state);
            Contact ct = (Contact) DomainObjectFactory.newInstance(new OID(Contact.BASE_DATA_OBJECT_TYPE, contactID));
            service.setContact(ct);
        }
    }

    /**
     * provide Implementation to TableActionListener method. Does nothing in our
     * case.
     */
    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}
