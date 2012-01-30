package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import java.math.BigDecimal;

/**
 *
 * @author quasi
 */
public class GenericPersonContactCollection extends DomainCollection {

    public static final String ORDER = "link." + GenericPerson.CONTACTS_ORDER
                                       + " asc";
    public static final String CONTACTS_KEY = "link."
                                              + GenericPerson.CONTACTS_KEY;
    public static final String CONTACTS_ORDER = "link."
                                                + GenericPerson.CONTACTS_ORDER;

    /**
     * Creates a new instance of GenericPersonContactCollection
     */
    public GenericPersonContactCollection(DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addOrder(ORDER);
    }

    // Get the contact type of the link
    public String getContactType() {
        return (String) m_dataCollection.get(CONTACTS_KEY);
    }

    // Get the contact order of the link
    public String getContactOrder() {
        String retVal = ((BigDecimal) m_dataCollection.get(CONTACTS_ORDER)).
                toString();

        if (retVal == null || retVal.isEmpty()) {
            retVal = String.valueOf(this.getPosition());
        }

        return retVal;
    }

    public void setContactOrder(BigDecimal order) {
        DataObject link = (DataObject) this.get("link");

        link.set("linkOrder", order);
    }

    public void swapWithNext(final GenericContact contact) {
        int currentPos = 0;
        int currentIndex = 0;
        int nextIndex = 0;

        this.rewind();
        while (this.next()) {
            currentPos = this.getPosition();
            currentIndex = Integer.parseInt(this.getContactOrder());
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
            nextIndex = Integer.parseInt(this.getContactOrder());
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

        this.setContactOrder(new BigDecimal(nextIndex));
        this.next();
        this.setContactOrder(new BigDecimal(currentIndex));
        this.rewind();
    }

    public void swapWithPrevious(final GenericContact contact) {
        int previousPos = 0;
        int previousIndex = 0;
        int currentPos = 0;
        int currentIndex = 0;

        this.rewind();
        while (this.next()) {
            currentPos = this.getPosition();
            currentIndex = Integer.parseInt(this.getContactOrder());

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

        this.setContactOrder(new BigDecimal(currentIndex));
        this.next();
        this.setContactOrder(new BigDecimal(previousIndex));
        this.rewind();
    }

    public GenericContact getContact() {
        final GenericContactBundle bundle =
                                   (GenericContactBundle) DomainObjectFactory.
                newInstance(m_dataCollection.getDataObject());
        return (GenericContact) bundle.getInstance(GlobalizationHelper.
                getNegotiatedLocale().getLanguage());
        //return new GenericContact(m_dataCollection.getDataObject());
    }

    public GenericContact getContact(final String language) {
        final GenericContactBundle bundle =
                                   (GenericContactBundle) DomainObjectFactory.
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

            GenericContactBundle bundle =
                                 (GenericContactBundle) DomainObjectFactory.
                    newInstance(dobj);
            return (GenericPerson) bundle.getPrimaryInstance();
        }
    }

    public GenericAddress getAddress() {
        /*
         * return (GenericAddress) DomainObjectFactory.newInstance((DataObject)
         * m_dataCollection. getDataObject().get(
                GenericContact.ADDRESS));
         */
        return getContact().getAddress();
    }

    public GenericContactEntryCollection getContactEntries() {
        /*
         * return new GenericContactEntryCollection((DataCollection)
         * m_dataCollection. getDataObject().get(
                GenericContact.CONTACT_ENTRIES));
         */
        return getContact().getContactEntries();
    }
}
