/*
 * Copyright (C) 2009 Sören Bernstein
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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * This represents an organization. It is designed to be suitable
 * for most organizations. Currently, it offers the following properties:
 * - name of the organization
 * - an addendum for the name
 * - a short description of the organization.
 *
 * It is also possible to add roles to the organization, e.g. CEO, mayor or others.
 * The following features are planned to implement in one of the next commits:
 * - Adding OrganizationUnits
 *
 * The current version of this contenttype is modeled on base of the MultipartArticle
 * contenttype.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class HealthCareFacility extends GenericOrganizationalUnit {
    
    //private static final HealthCareFacilityConfig s_config = new HealthCareFacilityConfig();
    private static final Logger s_log = Logger.getLogger(HealthCareFacility.class);

    private static final HealthCareFacilityConfig s_config = new HealthCareFacilityConfig();
    
    public static final String ADDRESS = "address";
    public static final String CONTACTS = "contacts";
    public static final String CONTACT_TYPE = "contact_type";
    public static final String CONTACT_ORDER = "contact_order";
    
    public static String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.HealthCareFacility";
    
    /**
     * Called when the class is loaded by the Java class loader.
     */
    static {
        s_log.debug("Static initalizer starting...");
        s_config.load();
        s_log.debug("Static initalizer finished.");
    }
    
    /**
     * Returns a possibly existing configuration object for the class.
     *
     * @return config object
     */
    public static final HealthCareFacilityConfig getConfig() {
        return s_config;
    }
    
    /**
     * Default constructor. This creates a new (empty) health care facility
     */
    public HealthCareFacility() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    /**
     * Trys to find an health care facility in the database by its id.
     *
     * @param id ID of the object to (re-)create
     * @throws DataObjectNotFoundException if no object with the given id is found in the database.
     */
    public HealthCareFacility(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    
    /**
     * Trys to find an health care facility in the database by its OID.
     *
     * @param id ID of the object to (re-)create
     * @throws DataObjectNotFoundException if no object with the given OID is found in the database.
     */
    public HealthCareFacility(OID id) throws DataObjectNotFoundException {
        super(id);
    }
    
    /**
     * Create an new HealthCareFacility object from a DataObject
     *
     * @param obj The data object
     */
    public HealthCareFacility(DataObject obj) {
        super(obj);
    }
    
    /**
     * Not sure for what this constructor is.
     *
     * @param type The type of the object to create (?)
     */
    public HealthCareFacility(String type) {
        super(type);
    }
    
    
//    /* accessors *************************************************/
//    
//    // Get the address for this contact
//    public com.arsdigita.cms.contenttypes.GenericAddress getAddress() {
//        return (com.arsdigita.cms.contenttypes.GenericAddress)DomainObjectFactory.newInstance((DataObject)get(ADDRESS));
//    }
//    
//    // Set the address for this contact
//    public void setAddress(com.arsdigita.cms.contenttypes.GenericAddress address) {
//        set(ADDRESS, address);
//    }
//    
//    // Unset the address for this contact
//    public void unsetAddress() {
//        set(ADDRESS, null);
//    }
//        
//    // Get all contacts for this health care facility
//    public HealthCareFacilityContactCollection getContacts() {
//        return new HealthCareFacilityContactCollection((DataCollection) get(CONTACTS));
//    }
//    
//    // Add a contact for this health care facility
//    public void addContact(com.arsdigita.cms.contenttypes.GenericContact contact, String contactType) {
//        Assert.exists(contact, com.arsdigita.cms.contenttypes.GenericContact.class);
//        
//        DataObject link = add(CONTACTS, contact);
//        
//        link.set(CONTACT_TYPE, contactType);
//        link.set(CONTACT_ORDER, BigDecimal.valueOf(getContacts().size()));
////        this.save();
//        
////        add(CONTACTS, contact);
//    }
//    
//    // Remove a contect for this health care facility
//    public void removeContactEntry(com.arsdigita.cms.contenttypes.GenericContact contact) {
//        Assert.exists(contact, com.arsdigita.cms.contenttypes.GenericContact.class);
//        remove(CONTACTS, contact);
//    }
//    
//    public boolean hasContacts() {
//        return !this.getContacts().isEmpty();
//    }
}