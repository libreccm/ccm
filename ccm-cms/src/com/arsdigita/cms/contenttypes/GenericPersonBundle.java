package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class GenericPersonBundle extends ContentBundle {

    public final static String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.GenericPersonBundle";
    public static final String CONTACTS = "contacts";
    public static final String CONTACTS_KEY = "linkKey";
    public static final String CONTACTS_ORDER = "linkOrder";

    public GenericPersonBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        super.setName(primary.getName());
    }

    public GenericPersonBundle(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public GenericPersonBundle(final BigDecimal id) throws
            DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public GenericPersonBundle(final DataObject dobj) {
        super(dobj);
    }

    public GenericPersonBundle(final String type) {
        super(type);
    }

    public GenericPersonContactCollection getContacts() {
        return new GenericPersonContactCollection(
                (DataCollection) get(CONTACTS));
    }

    // Add a contact for this person
    public void addContact(final GenericContact contact, 
                           final String contactType) {
        Assert.exists(contact, GenericContact.class);

        DataObject link = add(CONTACTS, contact.getContentBundle());

        link.set(CONTACTS_KEY, contactType);
        link.set(CONTACTS_ORDER, BigDecimal.valueOf(getContacts().size()));
    }

    // Remove a contact for this person
    public void removeContact(GenericContact contact) {
        Assert.exists(contact, GenericContact.class);
        remove(CONTACTS, contact.getContentBundle());
    }

    public boolean hasContacts() {
        return !this.getContacts().isEmpty();
    }
}
