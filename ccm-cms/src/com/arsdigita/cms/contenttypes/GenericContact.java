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

    private static final Logger LOGGER = Logger.getLogger(GenericContact.class);
    /**
     * PDL property names
     */
    public static final String PERSON = "person";
//    public static final String CONTACT_TYPE = "";
    public static final String ADDRESS = "address";
    public static final String CONTACT_ENTRIES = "contactentries";
    public static final String CONTACTS_KEY = GenericPersonContactCollection.CONTACTS_KEY;
    private static final String RELATION_ATTRIBUTES
                                = "person.link_key:GenericContactTypes;contactentries.key:GenericContactEntryKeys";
    // Config
    private static final GenericContactConfig s_config = new GenericContactConfig();

    static {
        LOGGER.debug("Static initializer is starting...");
        s_config.load();
        LOGGER.debug("Static initializer finished");
    }

    /**
     * Data object type for this domain object
     */
    public static final String BASE_DATA_OBJECT_TYPE
                               = "com.arsdigita.cms.contenttypes.GenericContact";

    public GenericContact() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public GenericContact(final BigDecimal id)
        throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public GenericContact(final OID id)
        throws DataObjectNotFoundException {
        super(id);
    }

    public GenericContact(final DataObject obj) {
        super(obj);
    }

    public GenericContact(final String type) {
        super(type);
    }

    @Override
    public void beforeSave() {
        super.beforeSave();

        Assert.exists(getContentType(), ContentType.class);
    }

    /**
     * Retrieves the current configuration
     *
     * @return The Config for GenericContact
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
        return getGenericContactBundle().getPerson();
    }

    // Set the person for this contact
    public void setPerson(GenericPerson person, String contactType) {
        getGenericContactBundle().setPerson(person, contactType);
    }

    // Unset the address for this contact
    public void unsetPerson() {
        getGenericContactBundle().unsetPerson();
    }

    // Get the address for this contact
    public GenericAddress getAddress() {
//        return (GenericAddress) DomainObjectFactory.newInstance((DataObject) get(
//                ADDRESS));
        return getGenericContactBundle().getAddress();
    }

    // Set the address for this contact
    public void setAddress(final GenericAddress address) {
        //set(ADDRESS, address);
        getGenericContactBundle().setAddress(address);
    }

    // Unset the address for this contact
    public void unsetAddress() {
        //set(ADDRESS, null);
        getGenericContactBundle().unsetAddress();
    }

    // Get all contact entries for this contact, p. ex. phone number, type of contact etc.
    public GenericContactEntryCollection getContactEntries() {
        return new GenericContactEntryCollection((DataCollection) get(
            CONTACT_ENTRIES));
    }

    // Add a contact entry for this contact
    public void addContactEntry(final GenericContactEntry contactEntry) {
        Assert.exists(contactEntry, GenericContactEntry.class);
        add(CONTACT_ENTRIES, contactEntry);
    }

    // Remove a contect entry for this contact
    public void removeContactEntry(final GenericContactEntry contactEntry) {
        Assert.exists(contactEntry, GenericContactEntry.class);
        remove(CONTACT_ENTRIES, contactEntry);
    }

    public String getContactType() {
        final GenericPerson person = getPerson();

        if (person == null) {
            return null;
        } else {
            final GenericPersonContactCollection collection = person.getContacts();
            collection.next();
            final String contactType = collection.getContactType();

            // Close Collection to prevent open ResultSet
            collection.close();

            return contactType;
        }
    }

    public void setContactType(final String contactType) {

        final GenericPerson person = getPerson();
        if (person != null) {
            final GenericPersonContactCollection collection = person.getContacts();
            collection.next();
            final DataObject link = (DataObject) collection.get("link");
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
    public boolean hasRelationAttributeProperty(final String propertyName) {
        final StringTokenizer strTok = new StringTokenizer(RELATION_ATTRIBUTES, ";");
        while (strTok.hasMoreTokens()) {
            final String token = strTok.nextToken();
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
    public String getRelationAttributeKeyName(final String propertyName) {
        final StringTokenizer strTok = new StringTokenizer(RELATION_ATTRIBUTES, ";");
        while (strTok.hasMoreTokens()) {
            final String token = strTok.nextToken();
            if (token.startsWith(propertyName + ".")) {
                return token.substring(token.indexOf('.') + 1,
                                       token.indexOf(':'));
            }
        }
        return null;
    }

    @Override
    public String getRelationAttributeName(final String propertyName) {
        final StringTokenizer strTok = new StringTokenizer(RELATION_ATTRIBUTES, ";");
        while (strTok.hasMoreTokens()) {
            final String token = strTok.nextToken();
            if (token.startsWith(propertyName + ".")) {
                return token.substring(token.indexOf(':') + 1);
            }
        }
        return null;
    }

    @Override
    public String getRelationAttributeKey(final String propertyName) {
        return null;
    }

    @Override
    public void generateXML(final ContentItem item, final Element element, final PageState state) {
        if (getPerson() != null) {
            final Element personElem = element.newChildElement("person");
            GenericPerson person = getPerson();

            if ((person.getSurname() != null) && !person.getSurname().isEmpty()) {
                final Element surnameElem = personElem.newChildElement("surname");
                surnameElem.setText(person.getSurname());
            }

            if ((person.getGivenName() != null) && !person.getGivenName().isEmpty()) {
                final Element givenNameElem = personElem.newChildElement("givenname");
                givenNameElem.setText(person.getGivenName());
            }

            if ((person.getTitlePre() != null) && !person.getTitlePre().isEmpty()) {
                final Element titlePreElem = personElem.newChildElement("titlepre");
                titlePreElem.setText(person.getTitlePre());
            }

            if ((person.getTitlePost() != null) && !person.getTitlePost().isEmpty()) {
                final Element titlePostElem = personElem.newChildElement("titlepost");
                titlePostElem.setText(person.getTitlePost());
            }
        }

        final StringTokenizer keys = s_config.getContactEntryKeys();
        final Element contactKeysElem = element.newChildElement("contactEntryKeys");
        while (keys.hasMoreElements()) {
            contactKeysElem.newChildElement("entryKey").setText(keys.nextToken());
        }

        if (getAddress() != null) {
            final Element addressElem = element.newChildElement("address");
            final GenericAddress address = getAddress();

            if ((address.getAddress() != null) && !address.getAddress().isEmpty()) {
                final Element addressAdrElem = addressElem.newChildElement("address");
                addressAdrElem.setText(address.getAddress());
            }

            if ((address.getPostalCode() != null) && !address.getPostalCode().isEmpty()) {
                final Element postalCodeElem = addressElem.newChildElement("postalCode");
                postalCodeElem.setText(address.getPostalCode());
            }

            if ((address.getCity() != null) && !address.getCity().isEmpty()) {
                final Element cityElem = addressElem.newChildElement("city");
                cityElem.setText(address.getCity());
            }

            if ((address.getState() != null) && !address.getState().isEmpty()) {
                final Element stateElem = addressElem.newChildElement("state");
                stateElem.setText(address.getState());
            }

            if ((address.getIsoCountryCode() != null) && !address.getIsoCountryCode().isEmpty()) {
                final Element isoCodeElem = addressElem.newChildElement("isoCountryCode");
                isoCodeElem.setText(address.getIsoCountryCode());

                final Element countryElem = addressElem.newChildElement("country");
                countryElem.setText(GenericAddress.getCountryNameFromIsoCode(address.
                    getIsoCountryCode()));
            }

            final Element titleElem = element.newChildElement("title");
            titleElem.setText(address.getTitle());
        }
    }

    @Override
    public void addGlobalStateParams(final Page page) {
        //Nothing
    }

    @Override
    public void setListMode(final boolean listMode) {
        //Nothing
    }

    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.getExtraXMLGenerators();
        generators.add(this);
        return generators;
    }

}
