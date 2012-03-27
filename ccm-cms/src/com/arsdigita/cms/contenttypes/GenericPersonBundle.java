package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class GenericPersonBundle
        extends ContentBundle {

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

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();
        final GenericPersonBundle personBundle =
                                  (GenericPersonBundle) source;
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            if (CONTACTS.equals(attribute)) {
                final DataCollection contacts = (DataCollection) personBundle.
                        get(CONTACTS);

                while (contacts.next()) {
                    createContactAssoc(contacts);
                }

                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }

    private void createContactAssoc(final DataCollection contacts) {
        final GenericContactBundle draftContact =
                                   (GenericContactBundle) DomainObjectFactory.
                newInstance(contacts.getDataObject());
        final GenericContactBundle liveContact =
                                   (GenericContactBundle) draftContact.
                getLiveVersion();

        if (liveContact != null) {
            final DataObject link = add(CONTACTS, liveContact);

            link.set(CONTACTS_KEY,
                     contacts.get(GenericPersonContactCollection.CONTACTS_KEY));
            link.set(CONTACTS_ORDER,
                     contacts.get(GenericPersonContactCollection.CONTACTS_ORDER));

            link.save();
        }
    }
}
