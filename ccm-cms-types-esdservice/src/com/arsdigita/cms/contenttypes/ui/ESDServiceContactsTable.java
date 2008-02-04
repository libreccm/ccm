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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.contenttypes.Contact;
import com.arsdigita.cms.contenttypes.ESDService;

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
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.LockableImpl;

import java.math.BigDecimal;

/**
 * 
 * A table which displays a list of <code>Contact</code> objects to choose
 * from for the <code>ESDService</code> content type.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ESDServiceContactsTable.java 451 2005-03-20 23:11:22Z mbooth $
 * 
 */
public class ESDServiceContactsTable extends Table implements TableActionListener{

  private static final String COL_CONTACT_GIVENNAME = "Given Name";
  private static final String COL_CONTACT_FAMILYNAME = "Family Name";
  private static final String COL_CONTACT_TYPE = "Contact Type";
  private static final String COL_CHOOSE_CONTACT = "Select";
  
  private ItemSelectionModel m_selService;
  private ESDServiceChooseContactStep m_parent;
  
  /**
   * Constructor.
   * @param selService, selection model which will give the <code>ESDService</code>
   *        object that we are dealing with.
   * @param parent, component whose display pane will be shown when the selection
   *        of desired contact is done.
   */
  public ESDServiceContactsTable(ItemSelectionModel selService,
                                 ESDServiceChooseContactStep parent) {
    super();
    
    m_parent = parent;
    m_selService = selService;
    
    TableColumnModel model = getColumnModel();
    model.add( new TableColumn( 0, COL_CONTACT_GIVENNAME));
    model.add( new TableColumn( 1, COL_CONTACT_FAMILYNAME ));
    model.add( new TableColumn( 2, COL_CONTACT_TYPE));
    model.add( new TableColumn( 3, COL_CHOOSE_CONTACT));
    
    setEmptyView(new Label("No Contacts available."));
    setModelBuilder(new ContactsTableModelBuilder());
    
    model.get(3).setCellRenderer(new SelectCellRenderer());
    
    addTableActionListener(this);
    
  }

  /**
   * Model Builder to build the required data for the table.
   */
  private class ContactsTableModelBuilder extends LockableImpl implements TableModelBuilder {

    public TableModel makeModel ( Table table, PageState state ) {
      table.getRowSelectionModel().clearSelection(state);
      DataCollection m_contacts = SessionManager.getSession().retrieve(Contact.BASE_DATA_OBJECT_TYPE);
      m_contacts.addEqualsFilter(ContentItem.VERSION, ContentItem.DRAFT);
      return new ContactsTableModel(table, m_contacts);
    }
  }
  
  private class ContactsTableModel implements TableModel{
    
    private Table m_table;
    private DataCollection m_contacts;
    private Contact m_contact;    

    private ContactsTableModel(Table t , DataCollection cts){
      m_table = t;
      m_contacts = cts;
    }
    
    /** Return the no. of columns in column model we have constructed. */
    public int getColumnCount() {
      return m_table.getColumnModel().size();
    }

    /**
     * check collection for the existence of another row.If it has fetch the
     * value of Contact object into m_contact class variable.
     */
    public boolean nextRow() {
      if(m_contacts.next()){
        m_contact = (Contact) DomainObjectFactory.newInstance(m_contacts.getDataObject());
        return true;
      }else{
        return false;
      }
    }
    
    /**
     * Return the attributes of Contact object in the current row.
     * @see com.arsdigita.bebop.table.TableModel#getElementAt(int)
     */
    public Object getElementAt(int columnIndex) {

      switch (columnIndex){
      case 0:
        return m_contact.getGivenName();
      case 1:
        return m_contact.getFamilyName();
      case 2:
        return m_contact.getContactTypeName();
      case 3:
          return COL_CHOOSE_CONTACT;
      default:
        return null;
      }
    }

    /**
     * Always return the ID of <code>Contact</code> represented by current row. 
     * @see com.arsdigita.bebop.table.TableModel#getKeyAt(int)
     */
    public Object getKeyAt(int columnIndex) {
      return m_contact.getID();
    }
  }
  
  /**
   * TODO:Check for the permissions to EDIT item and put either a Lable or
   * a ControlLink accordingly.
   */
  private class SelectCellRenderer extends LockableImpl implements TableCellRenderer{

    public Component getComponent(Table table,PageState state,Object value,
                                  boolean isSelected,Object key,
                                  int row,int column) {

      
      ControlLink link = new ControlLink(value.toString());  
      link.setConfirmation("Select this Contact ?");
      return link;
    }
  }
  
  /**
   * Provide implementation to TableActionListener method.
   * Code that comes into picture when select link in the table is clicked.
   * Handles selection event.
   */
  public void cellSelected(TableActionEvent evt) {
    PageState state = evt.getPageState();
    TableColumn col = getColumnModel().get(evt.getColumn().intValue());
    String colName = (String) col.getHeaderValue();
    
    if ( COL_CHOOSE_CONTACT.equals(colName) ) {
      m_parent.showDisplayPane(state);
      BigDecimal contactID = new BigDecimal(evt.getRowKey().toString());
      ESDService service = (ESDService) m_selService.getSelectedObject(state);
      Contact ct = (Contact) DomainObjectFactory.newInstance(new OID(Contact.BASE_DATA_OBJECT_TYPE,contactID));
      service.setContact(ct);
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

