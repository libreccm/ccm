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

import org.apache.log4j.Logger;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;


/**
 * <code>DomainObject</code> class to represent Contact <code>ContentType</code>
 * objects.
 * 
 * @author Shashin Shinde <a href="mailto:sshinde@redhat.com">sshinde@redhat.com</a>
 * @version $Id: Contact.java 1689 2007-10-26 11:06:23Z chrisg23 $
 */
public class Contact extends ContentPage {

  /** PDL property names */
	/*
	 * Flattened out Person object properties.Next 3 properties can be moved out
	 * to seperate object if needed.
	 */
  public static final String GIVEN_NAME = "givenName";
  public static final String FAMILY_NAME = "familyName";
  public static final String SUFFIX = "suffix";
  public static final String EMAILS = "emails";
  
  public static final String DESCRIPTION = "description";
  public static final String ORG_NAME = "orgName";
  public static final String DEPT_NAME = "deptName";
  public static final String ROLE = "role";
  
  /* Composite objects used by this class */
  public static final String CONTACT_TYPE = "contactType";
  public static final String CONTACT_ADDRESS = "contactAddress";
  public static final String PHONES = "phones";

	public static final String ITEMS = "associatedContentItemsForContact";

	private static final Logger s_log = Logger.getLogger(Contact.class);

  /** data object type for this domain object */
	public static final String BASE_DATA_OBJECT_TYPE =
                                   "com.arsdigita.london.contenttypes.Contact";

  /** Default constructor. */
  public Contact() {
      super(BASE_DATA_OBJECT_TYPE);
  }

  /**
   * Adds an association between this contact and the given content item.
   * @param item
   */
    public void addContentItem(ContentItem item) {
	s_log.debug("item is " + item);
	item.addToAssociation(getItemsForContact());
    }
    
    /**
     * Deletes the association between this contact and the given content item.
     * @param item
     */
    public void removeContentItem(ContentItem item) {
	s_log.debug("item is " + item);
	DataOperation operation = SessionManager
	    .getSession()
	    .retrieveDataOperation(
            "com.arsdigita.london.contenttypes.removeContactFromContentItemAssociation");
	operation.setParameter("itemID", new Integer(item.getID().intValue()));
	operation.execute();
    }
    
    /**
     * Removes all mappings between this contact and any other content item.
     *
     */
    private void removeItemMappings() {
	DataOperation operation = SessionManager
	    .getSession()
	    .retrieveDataOperation(
                "com.arsdigita.london.contenttypes.removeContactFromAllAssociations");
	operation.setParameter("contactID", new Integer(this.getID().intValue()));
	operation.execute();
    }
    
    
    /**
     * Gets the DataAssociation that holds the mapping between this contact
     * and any content items.
     * @return
     */
    public DataAssociation getItemsForContact() {
	return (DataAssociation) get(ITEMS);
    }
    
    
    /**
     * Returns the Contact for a given content item.
     * @param item
     * @return
     */
    public static Contact getContactForItem(ContentItem item) {
	s_log.debug("getting contact for item " + item);
	DataQuery query = SessionManager.getSession().retrieveQuery(
                          "com.arsdigita.london.contenttypes.getContactForItem");
	query.setParameter("itemID", item.getID());
	BigDecimal contactID;
	Contact contact = null;
	while (query.next()) {
	    contactID = (BigDecimal) query.get("contactID");
	    contact = new Contact(contactID);
	}
	s_log.debug("returning contact " + contact);
	return contact;
    }
    
    
	
    
    /**
     * Constructor. Retrieves an object instance with the given id.
     * @param id the id of the object to retrieve
     */
    public Contact( BigDecimal id ) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
  /**
   * Constructor. Retrieves an object instance with the given OID.
   *
   * @param oid the object id of the object to retrieve
   */
  public Contact(OID oid) throws DataObjectNotFoundException {
    super(oid);
  }
  
  /**
   * Constructor. Create a Contact domain object using the given data object.
   * @param obj the object data to use
   */
  public Contact( DataObject obj ) {
      super(obj);
  }

  /** Constructor. */
  public Contact( String type ) {
    super(type);
  }

    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }
  
  public String getBaseDataObjectType() {
      return BASE_DATA_OBJECT_TYPE;
  }

  /** Accessor. Get the GIVEN_NAME for this Contact. */
  public String getGivenName(){
    return (String) get(GIVEN_NAME);
  }

  /** Mutator. Set the GIVEN_NAME for this Contact. */
  public void setGivenName(String gn){
    set(GIVEN_NAME , gn);
  }
  
  /** Accessor. Get the FAMILY_NAME for this Contact. */
  public String getFamilyName(){
    return (String) get(FAMILY_NAME);
  }

  /** Mutator. Set the FAMILY_NAME for this Contact. */
  public void setFamilyName(String fn){
    set(FAMILY_NAME , fn);
  }
  
  /** Accessor. Get the SUFFIX for this Contact. */
  public String getSuffix(){
    return (String) get(SUFFIX);
  }
  
  /** Mutator. Set the DESCRIPTION for this Contact. */
  public void setSuffix(String suf){
    set(SUFFIX , suf);
  }
  
  /** Accessor. Get the EMAILS for this Contact. */
  public String getEmails(){
    return (String) get(EMAILS);
  }
  
  /** Mutator. Set the EMAILS for this Contact. */
  public void setEmails(String ems){
    set(EMAILS , ems);
  }
  
  /** Accessor. Get the CONTACT_TYPE for this Contact. */
  public String getContactTypeName() {
    String ctTypeName = "";
    if(getContactType() != null){
      ctTypeName = getContactType().getTypeName(); 
    }
    return ctTypeName;
  }
  
  /** Accessor. Get the DESCRIPTION for this Contact. */
    @Override
  public String getDescription() {
      return (String) get(DESCRIPTION);
  }

  /** Mutator. Set the DESCRIPTION for this Contact. */
    @Override
  public void setDescription( String desc ) {
      set(DESCRIPTION , desc);
  }

  /** Accessor. Get the ORG_NAME for this Contact. */
  public String getOrganisationName() {
      return (String) get(ORG_NAME);
  }

  /** Mutator. Set the ORG_NAME for this Contact. */
  public void setOrganisationName( String orgName ) {
      set(ORG_NAME , orgName);
  }

  /** Accessor. Get the DEPT_NAME for this Contact. */
  public String getDeptName() {
      return (String) get(DEPT_NAME);
  }

  /** Mutator. Set the DEPT_NAME for this Contact. */
  public void setDeptName( String deptName ) {
      set(DEPT_NAME , deptName);
  }

  /** Accessor. Get the ROLE for this Contact. */
  public String getRole() {
      return (String) get(ROLE);
  }

  /** Mutator. Set the ROLE for this Contact. */
  public void setRole( String role ) {
      set(ROLE , role);
  }

  /** 
   * return type of Contact associated with this Contact object.
   * 
   * @return null if there is no associated <code><ContactType/code> object.
   */
  public ContactType getContactType(){
    DataObject obj = retrieveDataobject( CONTACT_TYPE );
    if(obj != null){
        return new ContactType(obj);
    }
    return null;
  }
  
  /** Mutator. Set the CONTACT_TYPE for this Contact. */
  public void setContactType( ContactType ct ) {
      setAssociation(CONTACT_TYPE , ct);
  }

/******* ContactAddress object manipulation methods *********************/
  /**
   * set the ContactAddress object association.
   */
  public void setContactAddress(ContactAddress caddr){
    setAssociation(CONTACT_ADDRESS , caddr);
  }

  /** 
   * Get the associated ContactAddress object for this Contact.
   * @return null if no Address is present.
   */
  public ContactAddress getContactAddress(){
    DataObject obj = retrieveDataobject( CONTACT_ADDRESS);
    if(obj != null){
        return new ContactAddress(obj);
    }
    return null;
  }

  /******* ContactPhone object manipulation methods *********************/
  
  /**
   * Add a Phone object to the collection of the phone objects this contact
   * has.
   */
  public void addPhone(ContactPhone ph){
    add(PHONES , ph);
    save();
  }
  
  /**
   * Remove the passed in Phone object from the list of phones this contact
   * object has.
   */
  public void removePhone(ContactPhone ph){
    ph.delete();
    save();
  }

  /**
   * Return collection of Phone objects associated with this Contact object.
   */
  public PhonesCollection getPhones(){
    DataAssociationCursor dac = ((DataAssociation) get(PHONES)).cursor();
    return new PhonesCollection(dac);
  }

  private DataObject retrieveDataobject(String attr){
    return ( DataObject ) get( attr );
  }
  
} //End of class.
