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
package com.arsdigita.london.contenttypes;

import java.math.BigDecimal;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

/**
 * DomainObject class to represent objects of type <code>ContactPhone</code>.
 * This object represents type of phone and it's number.
 * They are associated with <code>Contact</code> objects.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 *
 * @version $Id: ContactPhone.java 287 2005-02-22 00:29:02Z sskracic $
 * 
 */
public class ContactPhone extends ContentItem {

  /** data object type for this domain object */
  public static final String BASE_DATA_OBJECT_TYPE =
      "com.arsdigita.london.contenttypes.ContactPhone";
  
  /** PDL property names */
  public static final String PHONE_TYPE = "phoneType";
  public static final String PHONE_NUMBER = "phoneNumber";
  
  /** Default constructor. */
  public ContactPhone() {
      super(BASE_DATA_OBJECT_TYPE);
  }

  /**
   * Constructor. Retrieves an object instance with the given OID.
   *
   * @param oid the object id of the object to retrieve
   */
  public ContactPhone(OID oid) throws DataObjectNotFoundException {
    super(oid);
  }

  /**
   * Constructor. Retrieves an object instance with the given id.
   * @param id the id of the object to retrieve
   */
  public ContactPhone( BigDecimal id ) throws DataObjectNotFoundException {
      this(new OID(BASE_DATA_OBJECT_TYPE, id));
  }

  /**
   * Constructor. Create a Contact domain object using the given data object.
   * @param obj the object data to use
   */
  public ContactPhone( DataObject obj ) {
      super(obj);
  }

  /** Constructor. */
  public ContactPhone( String type ) {
    super(type);
  }
  
    @Override
  public String getBaseDataObjectType() {
      return BASE_DATA_OBJECT_TYPE;
  }

    //Accessors   
  public String getPhoneType(){
    return (String) get(PHONE_TYPE);
  }
  
  public String getPhoneNumber(){
    return (String) get(PHONE_NUMBER);
  }
  
  public void setPhoneNumber(String num){
    set(PHONE_NUMBER,num);
  }

  public void setPhoneType(String type){
    set(PHONE_TYPE, type);
  }
  
}
