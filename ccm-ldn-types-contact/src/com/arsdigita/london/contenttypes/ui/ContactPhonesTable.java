/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.london.contenttypes.Contact;
import com.arsdigita.london.contenttypes.ContactPhone;
import com.arsdigita.london.contenttypes.ContactPhonesCollection;
import com.arsdigita.london.contenttypes.util.ContactGlobalizationUtil;
import com.arsdigita.util.LockableImpl;

import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * A Table to display the Phones associated with the Contact object. Provides
 * links to delete the corresponding phone in each row.
 *
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 * @version $Id: ContactPhonesTable.java 287 2005-02-22 00:29:02Z sskracic $
 */
class ContactPhonesTable extends Table implements TableActionListener {

    private static final Logger log =
        Logger.getLogger(ContactPhonesTable.class.getName());

    // match columns by (symbolic) index, makes for easier reordering
    private static final int COL_INDEX_PHONE_TYPE = 0;  //"Phone Type";
    private static final int COL_INDEX_PHONE_NUM  = 1;  //"Phone Number";
    private static final int COL_INDEX_DELETE     = 2;  //"Delete";

    private ItemSelectionModel m_selContact;

    /**
     * Constructor. Create an instance of this class.
     *
     * @param selContact ItemSelectionModel which provides the
     *                   <code>Contact</code> object whose Phones are to be 
     *                   manipulated.
     */
    public ContactPhonesTable(ItemSelectionModel selContact) {

        super();
        m_selContact = selContact;

        setEmptyView(new Label(ContactGlobalizationUtil.globalize(
                "london.contenttypes.ui.contact.phonetable.no_entries_msg")));

        TableColumnModel model = getColumnModel();
        model.add(new TableColumn(
              COL_INDEX_PHONE_TYPE, 
              new Label(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.phonetable.header_type")
              ) ));
        model.add(new TableColumn(
              COL_INDEX_PHONE_NUM, 
              new Label(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.phonetable.header_num")
              ) ));
        model.add(new TableColumn(
              COL_INDEX_DELETE, 
              new Label(ContactGlobalizationUtil.globalize(
                  "london.contenttypes.ui.contact.phonetable.header_delete")
              ) ));

        model.get(2).setCellRenderer(new DeleteCellRenderer());

        setModelBuilder(new PhonesTableModelBuilder(selContact));

        addTableActionListener(this);

    }

    /**
     * 
     */
    private class PhonesTableModelBuilder extends LockableImpl
                                          implements TableModelBuilder {

        private ItemSelectionModel m_sel;

        /**
         * Private class constructor
         * @param sel 
         */
        private PhonesTableModelBuilder(ItemSelectionModel sel) {
            m_sel = sel;
        }

        /**
         * 
         * @param table
         * @param state
         * @return 
         */
        public TableModel makeModel(Table table, PageState state) {
            table.getRowSelectionModel().clearSelection(state);

            Contact contact = (Contact) m_sel.getSelectedObject(state);

            return new PhonesTableModel(table, state, contact);
        }
    }

    /**
     * Internal private class.
     */
    private class PhonesTableModel implements TableModel {

        private Table m_table;
        private ContactPhonesCollection m_phones;
        private ContactPhone m_phone;

       /**
        * Private class constructor.
        * @param t
        * @param ps
        * @param c 
        */
        private PhonesTableModel(Table t, PageState ps, Contact c) {
            m_table = t;
            m_phones = c.getPhones();
        }

        /** Return the number of columsn this TableModel has. */
        public int getColumnCount() {
            return m_table.getColumnModel().size();
        }

        /**
         * check collection for the existence of another row. If it has fetch
         * the value of Current Phone object into m_phone class variable.
         */
        public boolean nextRow() {
            if (m_phones != null && m_phones.next()) {
                m_phone = m_phones.getPhone();
                return true;
            } else {
                return false;
            }
        }

        /**
         * Return the phoneType and phoneNumber attributes for the Type and
         * Number columns respectively.
         *
         * @see com.arsdigita.bebop.table.TableModel#getElementAt(int)
         */
        public Object getElementAt(int columnIndex) {

            switch (columnIndex) {
                case 0:
                    return m_phone.getPhoneType();
                case 1:
                    return m_phone.getPhoneNumber();
                case 2:
                //  return "Delete";
                    return  new Label(
                            ContactGlobalizationUtil.globalize(
                            "london.contenttypes.ui.contact.phonetable.link_delete")
                            );
                default:
                    return null;
            }
        }

        /**
         * Always return the ID of
         * <code>Phone</code> represented by current row.
         *
         * @see com.arsdigita.bebop.table.TableModel#getKeyAt(int)
         */
        public Object getKeyAt(int columnIndex) {
            return m_phone.getID();
        }
    }

    /**
     * Private class to check for the permissions to delete item and put either 
     * a Lable or a ControlLink accordingly.
     */
    private class DeleteCellRenderer extends LockableImpl 
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
         * @return 
         */
        public Component getComponent(Table table, PageState state, 
                                      Object value, boolean isSelected, 
                                      Object key, int row, int column) {

            Component ret = null;
            SecurityManager sm = CMS.getSecurityManager(state);
            Contact item = (Contact) m_selContact.getSelectedObject(state);

            /* Check for permission to delete item */
            boolean canDelete = sm.canAccess(state.getRequest(),
                                             SecurityManager.DELETE_ITEM,
                                             item);

            if ( value instanceof Label ) {  // just the delete Controllink

                if (canDelete) {
                        ret = new ControlLink((Component)value);                
               //   ret.setConfirmation(ContactGlobalizationUtil.globalize(
               //       "london.contenttypes.ui.contact.phonetable.confirm_delete"));
                    } else {
                        ret = (Component)value;
                    }

            } else if ( value instanceof String ) { // phone type and value

           //   if (canDelete) {
                   //   ret = new ControlLink(value.toString());                    
           //           ret = new ControlLink((String)value);                    
                   //   link.setConfirmation("Delete this Phone ?");
                   //  return link;
           //       } else {
                        ret = new Label((String)value);
           //       }

            } else {
             // return new Label(value.toString());
                /* Just returns the label without underlying link */
                ret = new Label(value.toString());
            }
            
            return ret;
        }
    }

    /**
     * Provide implementation to TableActionListener method. Code that comes
     * into picture when a link on the table is clicked. Handles delete event.
     */
    public void cellSelected(TableActionEvent evt) {
        PageState state = evt.getPageState();

        TableColumn col = getColumnModel().get(evt.getColumn().intValue());
        int columnIndex = col.getModelIndex();
 
     // if (COL_DELETE.equals(colName)) {
        if ( columnIndex == COL_INDEX_DELETE ) {
            BigDecimal phoneID = new BigDecimal(evt.getRowKey().toString());
            Contact contact = (Contact) m_selContact.getSelectedObject(state);
            ContactPhone ph = new ContactPhone(phoneID);
            contact.removePhone(ph);
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
