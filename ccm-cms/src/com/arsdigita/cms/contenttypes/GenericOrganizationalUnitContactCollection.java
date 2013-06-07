/*
 * Copyright (c) 2010 Jens Pelzetter
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

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 * Collection class for the GenericOrganizationalUnit -> Contact relation.
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitContactCollection extends DomainCollection {

    private final static Logger s_log =
                                Logger.getLogger(
            GenericOrganizationalUnitContactCollection.class);
    public static final String LINK_CONTACT_ORDER = "link.contact_order";
    public static final String LINK_CONTACT_TYPE = "link.contact_type";
    public static final String CONTACT_ORDER = "contact_order";
    public static final String CONTACT_TYPE = "contact_type";

    public GenericOrganizationalUnitContactCollection(
            DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(LINK_CONTACT_ORDER);
    }

    // Get the contact type of the link
    public String getContactType() {
        return (String) m_dataCollection.get(LINK_CONTACT_TYPE);
    }

    public void setContactType(final String contactType) {
        DataObject link = (DataObject) this.get("link");

        link.set(CONTACT_TYPE, contactType);
    }

    // Get the contact order of the link
    public Integer getContactOrder() {
        return (Integer) m_dataCollection.get(LINK_CONTACT_ORDER);
    }

    public void setContactOrder(Integer order) {
        DataObject link = (DataObject) this.get("link");

        link.set(CONTACT_ORDER, order);
    }

    /**
     * Swaps the item {@code contact} with the next one in the collection.
     *
     * @param contact The item to swap with the next one.
     * @throws IllegalArgumentException Thrown if the item provided is not part
     * of this collection, or if the item is the last one in the collection.
     */
    public void swapWithNext(GenericContact contact) {
        int currentPos = 0;
        int currentIndex = 0;
        int nextIndex = 0;

        s_log.debug("Searching contact...");
        this.rewind();
        while (this.next()) {
            currentPos = this.getPosition();
            currentIndex = this.getContactOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPos,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getContactOrder(): %d",
                                      getContactOrder()));
            if (this.getContact().equals(contact)) {
                break;
            }
        }

        if (currentPos == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided contact is not "
                    + "part of this collection."));
        }

        if (this.next()) {
            nextIndex = this.getContactOrder();
        } else {
            throw new IllegalArgumentException(
                    "The provided contact is the last "
                    + "in the collection, so there is no next object "
                    + "to swap with.");
        }

        this.rewind();

        while (this.getPosition() != currentPos) {
            this.next();
        }

        this.setContactOrder(nextIndex);
        this.next();
        this.setContactOrder(currentIndex);
        this.rewind();
    }

    /**
     * Swaps the item {@code contact} with the previous one in the collection.
     *
     * @param contact The item to swap with the previous one.
     * @throws IllegalArgumentException Thrown if the item provided is not part
     * of this collection, or if the item is the first one in the collection.
     */
    public void swapWithPrevious(GenericContact contact) {
        int previousPos = 0;
        int previousIndex = 0;
        int currentPos = 0;
        int currentIndex = 0;

        s_log.debug("Searching child...");
        this.rewind();
        while (this.next()) {
            currentPos = this.getPosition();
            currentIndex = this.getContactOrder();
            s_log.debug(String.format("Position: %d(%d)/%d", currentPos,
                                      currentIndex, this.size()));
            s_log.debug(String.format("getContactOrder(): %d",
                                      getContactOrder()));

            if (this.getContact().equals(contact)) {
                break;
            }

            previousPos = currentPos;
            previousIndex = currentIndex;
        }

        if (currentPos == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided contact is not "
                    + "part of this collection."));
        }

        if (previousPos == 0) {
            throw new IllegalArgumentException(
                    String.format(
                    "The provided contact is the first one in this "
                    + "collection, so there is no previous one to switch "
                    + "with."));
        }

        this.rewind();
        while (this.getPosition() != previousPos) {
            this.next();
        }

        this.setContactOrder(currentIndex);
        this.next();
        this.setContactOrder(previousIndex);
        this.rewind();
    }

    /**
     *
     * @return The current contact.
     */
    public GenericContact getContact() {
        /*
         * return new GenericContact(m_dataCollection.getDataObject());
         */
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());
        return (GenericContact) bundle.getInstance(GlobalizationHelper.
                getNegotiatedLocale().getLanguage());
    }

    public GenericContact getContact(final String language) {
        final ContentBundle bundle = (ContentBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());
        return (GenericContact) bundle.getInstance(language);
    }

    public GenericPerson getPerson() {
        DataCollection collection;

        collection = (DataCollection) m_dataCollection.getDataObject().get(
                GenericContact.PERSON);        

        if (collection.size() == 0) {
            return null;
        } else {
            DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();

            // Close Collection to prevent an open ResultSet
            collection.close();

            //return (GenericPerson) DomainObjectFactory.newInstance(dobj);
            final GenericPersonBundle bundle = (GenericPersonBundle) DomainObjectFactory.newInstance(dobj);
            return (GenericPerson) bundle.getPrimaryInstance();
        }
    }

    public GenericAddress getAddress() {
        /*return (GenericAddress) DomainObjectFactory.newInstance((DataObject) m_dataCollection.
                getDataObject().get(
                GenericContact.ADDRESS));*/
        return getContact().getAddress();
    }

    public GenericContactEntryCollection getContactEntries() {
        /*return new GenericContactEntryCollection((DataCollection) m_dataCollection.
                getDataObject().get(
                GenericContact.CONTACT_ENTRIES));*/
        return getContact().getContactEntries();
    }
}
