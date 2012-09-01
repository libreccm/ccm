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

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.RelationAttributeInterface;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;
import java.math.BigDecimal;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 * This content type represents an basic contact
 *
 */
public class GenericContact extends ContentPage implements
        RelationAttributeInterface, ExtraXMLGenerator {

    private static final Logger logger = Logger.getLogger(GenericContact.class);
    /**
     * PDL property names
     */
    public static final String PERSON = "person";
//    public static final String CONTACT_TYPE = "";
    public static final String ADDRESS = "address";
    public static final String CONTACT_ENTRIES = "contactentries";
    public static final String CONTACTS_KEY =
                               GenericPersonContactCollection.CONTACTS_KEY;
    private static final String RELATION_ATTRIBUTES =
                                "person.link_key:GenericContactTypes;contactentries.key:GenericContactEntryKeys";
    // Config
    private static final GenericContactConfig s_config =
                                              new GenericContactConfig();

    static {
        logger.debug("Static initializer is starting...");
        s_config.load();
        logger.debug("Static initializer finished");
    }

    /**
     * Data object type for this domain object
     */
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.GenericContact";

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
        //unsetPerson();        
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

    public GenericContactBundle getGenericContactBundle() {
        return (GenericContactBundle) getContentBundle();
    }

    ///////////////////////////////////////////////////////////////
    // accessors
    // Get the person for this contact
    public GenericPerson getPerson() {
        /*
         * DataCollection collection;
         *
         * collection = (DataCollection) get(PERSON);
         *
         * if (collection.size() == 0) { return null; } else { DataObject dobj;
         *
         * collection.next(); dobj = collection.getDataObject();
         *
         * // Close Collection to prevent an open ResultSet collection.close();
         *
         * return (GenericPerson) DomainObjectFactory.newInstance(dobj);
         }
         */

        return getGenericContactBundle().getPerson();
    }

    // Set the person for this contact
    public void setPerson(GenericPerson person, String contactType) {
        //set(PERSON, person);
        /*
         * if (getPerson() != null) { unsetPerson(); }
         *
         * if (person != null) { Assert.exists(person, GenericPerson.class);
         * DataObject link = add(PERSON, person);
         * link.set(GenericPerson.CONTACTS_KEY, contactType);
         * link.set(GenericPerson.CONTACTS_ORDER, new
         * BigDecimal(person.getContacts().size())); link.save();
         }
         */

        getGenericContactBundle().setPerson(person, contactType);
    }

//    // Get the type for this contact
//    public String getContactType() {
//        return get(CONTACT_TYPE));
//    }
//
//    // Set the type for this contact
//    public void setContactType(String type) {
//        set(CONTACT_TYPE, type);
//    }
    // Unset the address for this contact
    public void unsetPerson() {
        //set(PERSON, null);
        /*
         * GenericPerson oldPerson; oldPerson = getPerson(); if (oldPerson !=
         * null) { remove(PERSON, oldPerson);
         }
         */

        getGenericContactBundle().unsetPerson();
    }

    // Get the address for this contact
    public GenericAddress getAddress() {
        return (GenericAddress) DomainObjectFactory.newInstance((DataObject) get(
                ADDRESS));
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
        return new GenericContactEntryCollection((DataCollection) get(
                CONTACT_ENTRIES));
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

    public String getContactType() {

        GenericPerson person = getPerson();

        if (person != null) {
            GenericPersonContactCollection collection = person.getContacts();
            collection.next();
            String contactType = (String) collection.getContactType();

            // Close Collection to prevent open ResultSet
            collection.close();

            return contactType;
        } else {
            return null;
        }
    }

    public void setContactType(String contactType) {

        GenericPerson person = getPerson();
        if (person != null) {
            GenericPersonContactCollection collection = person.getContacts();
            collection.next();
            DataObject link = (DataObject) collection.get("link");
            link.set(CONTACTS_KEY, contactType);
        }
    }

    public boolean hasPerson() {
        return !(this.getPerson() == null);
    }

    public boolean hasAddress() {
        return !(this.getAddress() == null);
    }

    public boolean hasContactEntries() {
        return !this.getContactEntries().isEmpty();
    }

    @Override
    public boolean hasRelationAttributes() {
        return !RELATION_ATTRIBUTES.isEmpty();
    }

    @Override
    public boolean hasRelationAttributeProperty(String propertyName) {
        StringTokenizer strTok = new StringTokenizer(RELATION_ATTRIBUTES, ";");
        while (strTok.hasMoreTokens()) {
            String token = strTok.nextToken();
            if (token.startsWith(propertyName + ".")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public StringTokenizer getRelationAttributes() {
        return new StringTokenizer(RELATION_ATTRIBUTES, ";");
    }

    @Override
    public String getRelationAttributeKeyName(String propertyName) {
        StringTokenizer strTok = new StringTokenizer(RELATION_ATTRIBUTES, ";");
        while (strTok.hasMoreTokens()) {
            String token = strTok.nextToken();
            if (token.startsWith(propertyName + ".")) {
                return token.substring(token.indexOf(".") + 1,
                                       token.indexOf(":"));
            }
        }
        return null;
    }

    @Override
    public String getRelationAttributeName(String propertyName) {
        StringTokenizer strTok = new StringTokenizer(RELATION_ATTRIBUTES, ";");
        while (strTok.hasMoreTokens()) {
            String token = strTok.nextToken();
            if (token.startsWith(propertyName + ".")) {
                return token.substring(token.indexOf(":") + 1);
            }
        }
        return null;
    }

    @Override
    public String getRelationAttributeKey(String propertyName) {
        return null;
    }

    @Override
    public void generateXML(ContentItem item, Element element, PageState state) {
        if (getPerson() != null) {
            Element personElem = element.newChildElement("person");
            GenericPerson person = getPerson();

            if ((person.getSurname() != null) && !person.getSurname().isEmpty()) {
                Element surnameElem = personElem.newChildElement("surname");
                surnameElem.setText(person.getSurname());
            }

            if ((person.getGivenName() != null) && !person.getGivenName().isEmpty()) {
                Element givenNameElem = personElem.newChildElement("givenname");
                givenNameElem.setText(person.getGivenName());
            }

            if ((person.getTitlePre() != null) && !person.getTitlePre().isEmpty()) {
                Element titlePreElem = personElem.newChildElement("titlepre");
                titlePreElem.setText(person.getTitlePre());
            }

            if ((person.getTitlePost() != null) && !person.getTitlePost().isEmpty()) {
                Element titlePostElem = personElem.newChildElement("titlepost");
                titlePostElem.setText(person.getTitlePost());
            }
        }

        StringTokenizer keys = s_config.getContactEntryKeys();
        Element contactKeysElem = element.newChildElement("contactEntryKeys");
        while (keys.hasMoreElements()) {
            contactKeysElem.newChildElement("entryKey").setText(keys.nextToken());
        }
    }

    @Override
    public void addGlobalStateParams(Page p) {
        //Nothing
    }

    @Override
    public void setListMode(final boolean listMode) {
        //Nothing
    }

    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(this);
        return generators;
    }

}
