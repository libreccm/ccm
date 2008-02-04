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

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * DomainObject class to represent objects of <code>ContactType</code>.
 * These objects are associated with <code>Contact</code> objects.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ContactType.java 287 2005-02-22 00:29:02Z sskracic $
 * 
 */
public class ContactType extends ContentItem {

  /** data object type for this domain object */
  public static final String BASE_DATA_OBJECT_TYPE =
      "com.arsdigita.cms.contenttypes.ContactType";
      
  public static final String TYPE_NAME = "typeName";

  /** Default constructor. */
  public ContactType() {
      super(BASE_DATA_OBJECT_TYPE);
  }

  /**
   * Constructor. Retrieves an object instance with the given id.
   * @param id the id of the object to retrieve
   */
  public ContactType( BigDecimal id ) throws DataObjectNotFoundException {
      this(new OID(BASE_DATA_OBJECT_TYPE, id));
  }

  /**
   * Constructor. Retrieves an object instance with the given OID.
   *
   * @param oid the object id of the object to retrieve
   */
  public ContactType(OID oid) throws DataObjectNotFoundException {
    super(oid);
  }
  
  /**
   * Constructor. Create a Contact domain object using the given data object.
   * @param obj the object data to use
   */
  public ContactType( DataObject obj ) {
      super(obj);
  }

  /** Constructor. */
  public ContactType( String type ) {
    super(type);
  }

  public String getBaseDataObjectType() {
      return BASE_DATA_OBJECT_TYPE;
  }

  public String getTypeName(){
    return (String) get(TYPE_NAME);
  }
  
  public void setTypeName(String tname){
    set(TYPE_NAME,tname);
  }

}
