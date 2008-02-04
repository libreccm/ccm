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
package com.arsdigita.cms.contenttypes;

import java.math.BigDecimal;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;

/**
 * 
 * Class which represents a collection of ContactTypes.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ContactTypesCollection.java 287 2005-02-22 00:29:02Z sskracic $
 * 
 */
public class ContactTypesCollection extends DomainCollection {

  /** Retrieve the collection of ContactTypes. */
  public static ContactTypesCollection getContactTypesCollection(){
    DataCollection typesColl = SessionManager.getSession().retrieve(ContactType.BASE_DATA_OBJECT_TYPE);
    return new ContactTypesCollection(typesColl);
  }

  /**
   * private Constructor.Single ton type of class.
   *
   **/
  private ContactTypesCollection(DataCollection dataCollection) {
      super(dataCollection);
  }

  /**
   * Returns a <code>DomainObject</code> for the current position in
   * the collection.
   *
   **/
  public DomainObject getDomainObject() {
      return new ContactType(m_dataCollection.getDataObject());
  }

  /**
   * Returns a <code>ContactType</code> for the current position in
   * the collection.
   *
   **/
  public ContactType getContactType() {
      return (ContactType) getDomainObject();
  }
  
  /**
   * Return the name of ContactType from current row.
   */
  public String getContactTypeName(){
    return getContactType().getTypeName();
  }
  
  /**
   * Return the ID of ContactType from current row.
   */
  public BigDecimal getContactTypeID(){
    return getContactType().getID();
  }

}
