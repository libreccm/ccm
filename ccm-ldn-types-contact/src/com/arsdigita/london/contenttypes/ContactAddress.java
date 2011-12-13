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
 * DomainObject class to represent objects of type <code>ContactAddress</code>
 * These objects are associated with Contact objects in this package.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 * @version $Id: ContactAddress.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ContactAddress extends ContentItem {

	/** data object type for this domain object */
	public static final String BASE_DATA_OBJECT_TYPE =
		"com.arsdigita.london.contenttypes.ContactAddress";

	public static final String SAON = "saon";
	public static final String PAON = "paon";
	public static final String STREET_DESC = "streetDesc";
	public static final String STREET_REF_NO = "streetRefNo";
	public static final String LOCALITY = "locality";
	public static final String TOWN = "town";
	public static final String ADMINISTRATIVE_AREA = "administrativeArea";
	public static final String POST_TOWN = "postTown";
	public static final String POST_CODE = "postCode";
	public static final String PROP_REF_NO = "referenceNo";

	/** Default constructor. */
	public ContactAddress() {
		super(BASE_DATA_OBJECT_TYPE);
	}

	/**
	 * Constructor. Retrieves an object instance with the given id.
	 * @param id the id of the object to retrieve
	 */
	public ContactAddress(BigDecimal id) throws DataObjectNotFoundException {
		this(new OID(BASE_DATA_OBJECT_TYPE, id));
	}

	/**
	 * Constructor. Create a Contact domain object using the given data object.
	 * @param obj the object data to use
	 */
	public ContactAddress(DataObject obj) {
		super(obj);
	}

  /**
   * Constructor. Retrieves an object instance with the given OID.
   *
   * @param oid the object id of the object to retrieve
   */
  public ContactAddress(OID oid) throws DataObjectNotFoundException {
    super(oid);
  }

  /** Constructor. */
  public ContactAddress( String type ) {
    super(type);
  }
  
  public String getBaseDataObjectType() {
		return BASE_DATA_OBJECT_TYPE;
	}

	public String getSaon() {
		return (String) get(SAON);
	}

  public void setSaon(String saon) {
    set(SAON,saon);
  }

  public String getPaon() {
    return (String) get(PAON);
  }

  public void setPaon(String paon) {
    set(PAON , paon);
  }

  public String getStreetDesc() {
    return (String) get(STREET_DESC);
  }

  public void setStreetDesc(String desc) {
    set(STREET_DESC,desc);
  }

  public String getStreetRefNo() {
    return (String) get(STREET_REF_NO);
  }

  public void setStreetRefNo(String refno) {
    set(STREET_REF_NO,refno);
  }

  public String getLocality() {
    return (String) get(LOCALITY);
  }

  public void setLocality(String locality) {
    set(LOCALITY,locality);
  }

  public String getTown() {
    return (String) get(TOWN);
  }

  public void setTown(String town) {
    set(TOWN,town);
  }

  public String getAdministrativeArea() {
    return (String) get(ADMINISTRATIVE_AREA);
  }

  public void setAdministrativeArea(String adArea) {
    set(ADMINISTRATIVE_AREA,adArea);
  }

  public String getPostTown() {
    return (String) get(POST_TOWN);
  }

  public void setPostTown(String ptown) {
    set(POST_TOWN, ptown);
  }

  public String getPostCode() {
    return (String) get(POST_CODE);
  }

  public void setPostCode(String pcode) {
    set(POST_CODE, pcode);
  }

  public String getReferenceNo() {
    return (String) get(PROP_REF_NO);
  }

  public void setReferenceNo(String refno) {
    set(PROP_REF_NO, refno);
  }

}
