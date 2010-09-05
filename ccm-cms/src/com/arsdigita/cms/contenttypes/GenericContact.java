/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.RelationAttributeInterface;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.StringTokenizer;


/**
 * This content type represents an basic contact
 *
 */
public class GenericContact extends ContentPage implements RelationAttributeInterface {

    /** PDL property names */
    public static final String PERSON = "person";
    public static final String ADDRESS = "address";
    public static final String CONTACT_ENTRIES = "contactentries";

    private static final String RELATION_ATTRIBUTES = "GenericContactType;GenericContactEntryType";
    
    // Config
    private static final GenericContactConfig s_config = new GenericContactConfig();
    static {
	    s_config.load();
    }
    
    /** Data object type for tihs domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contenttypes.GenericContact";

    public GenericContact() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public GenericContact(BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public GenericContact(OID id)
            throws DataObjectNotFoundException {
        super(id);
    }

    public GenericContact(DataObject obj) {
        super(obj);
    }

    public GenericContact(String type) {
        super(type);
    }

    @Override
    public void beforeSave() {
        super.beforeSave();
        
        Assert.exists(getContentType(), ContentType.class);
    }

    /**
     * Retrieves the current configuration
     */
    public static final GenericContactConfig getConfig() {
        return s_config;
    }
    

    ///////////////////////////////////////////////////////////////
    // accessors

    // Get the person for this contact
    public GenericPerson getPerson() {
        return (GenericPerson) DomainObjectFactory.newInstance((DataObject)get(PERSON));
    }
    
    // Set the person for this contact
    public void setPerson(GenericPerson person) {
        set(PERSON, person);
    }
    
    // Unset the address for this contact
    public void unsetPerson() {
        set(PERSON, null);
    }
    
    // Get the address for this contact
    public GenericAddress getAddress() {
        return (GenericAddress)DomainObjectFactory.newInstance((DataObject)get(ADDRESS));
    }
    
    // Set the address for this contact
    public void setAddress(GenericAddress address) {
        set(ADDRESS, address);
    }
    
    // Unset the address for this contact
    public void unsetAddress() {
        set(ADDRESS, null);
    }
    
    // Get all contact entries for this contact, p. ex. phone number, type of contact etc.
    public GenericContactEntryCollection getContactEntries() {
        return new GenericContactEntryCollection ((DataCollection) get(CONTACT_ENTRIES));
    }
    
    // Add a contact entry for this contact
    public void addContactEntry(GenericContactEntry contactEntry) {
        Assert.exists(contactEntry, GenericContactEntry.class);
        add(CONTACT_ENTRIES, contactEntry);
    }
    
    // Remove a contect entry for this contact
    public void removeContactEntry(GenericContactEntry contactEntry) {
        Assert.exists(contactEntry, GenericContactEntry.class);
        remove(CONTACT_ENTRIES, contactEntry);
    }
    
    public boolean hasContactEntries() {
        return !this.getContactEntries().isEmpty();
    }

    @Override
    public boolean hasRelationAttributes() {
        return !RELATION_ATTRIBUTES.isEmpty();
    }

    @Override
    public StringTokenizer getRelationAttributes() {
        return new StringTokenizer(RELATION_ATTRIBUTES, ";");
    }
}
