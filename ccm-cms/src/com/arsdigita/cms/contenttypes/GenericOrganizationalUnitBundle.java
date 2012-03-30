package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.domain.DataObjectNotFoundException;
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
 * @version $Id: GenericOrganizationalUnitBundle.java 1480 2012-01-30 13:52:00Z
 * jensp $
 */
public class GenericOrganizationalUnitBundle extends ContentBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.GenericOrganizationalUnitBundle";
    public static final String PERSONS = "persons";
    public static final String ORGAUNITS = "organizationalunits";
    public final static String CONTACTS = "contacts";

    public GenericOrganizationalUnitBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        super.setName(primary.getName());
    }

    public GenericOrganizationalUnitBundle(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public GenericOrganizationalUnitBundle(final BigDecimal id)
            throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public GenericOrganizationalUnitBundle(final DataObject dobj) {
        super(dobj);
    }

    public GenericOrganizationalUnitBundle(final String type) {
        super(type);
    }

    public GenericOrganizationalUnitPersonCollection getPersons() {
        return new GenericOrganizationalUnitPersonCollection((DataCollection) get(
                PERSONS));
    }

    public void addPerson(final GenericPerson person,
                          final String role,
                          final String status) {
        Assert.exists(person, GenericPerson.class);

        final DataObject link = add(PERSONS, person.getGenericPersonBundle());

        link.set(GenericOrganizationalUnitPersonCollection.PERSON_ROLE, role);
        link.set(GenericOrganizationalUnitPersonCollection.STATUS, status);
        link.save();
    }

    public void removePerson(final GenericPerson person) {
        Assert.exists(person, GenericPerson.class);

        remove(PERSONS, person.getContentBundle());
    }

    public boolean hasPersons() {
        return !getPersons().isEmpty();
    }

    public GenericOrganizationalUnitContactCollection getContacts() {
        return new GenericOrganizationalUnitContactCollection((DataCollection) get(
                CONTACTS));
    }

    public void addContact(final GenericContact contact,
                           final String contactType) {
        Assert.exists(contact, GenericContact.class);

        final DataObject link = add(CONTACTS, contact.getContentBundle());

        link.set(GenericOrganizationalUnitContactCollection.CONTACT_TYPE,
                 contactType);
        link.set(GenericOrganizationalUnitContactCollection.CONTACT_ORDER,
                 Integer.valueOf((int) getContacts().size()));
        link.save();
    }

    public void removeContact(final GenericContact contact) {
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
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final GenericOrganizationalUnitBundle orgaBundle = (GenericOrganizationalUnitBundle) source;

            if (CONTACTS.equals(attribute)) {
                final DataCollection contacts = (DataCollection) orgaBundle.get(
                        CONTACTS);

                while (contacts.next()) {
                    createContactAssoc(contacts);
                }

                return true;
            } else if (PERSONS.equals(attribute)) {
                final DataCollection persons = (DataCollection) orgaBundle.get(
                        PERSONS);

                while (persons.next()) {
                    createPersonAssoc(persons);
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
        final GenericContactBundle draftContact = (GenericContactBundle) DomainObjectFactory.
                newInstance(contacts.getDataObject());
        final GenericContactBundle liveContact = (GenericContactBundle) draftContact.
                getLiveVersion();

        if (liveContact != null) {
            final DataObject link = add(CONTACTS, liveContact);

            link.set(GenericOrganizationalUnitContactCollection.CONTACT_TYPE,
                     contacts.get(
                    GenericOrganizationalUnitContactCollection.LINK_CONTACT_TYPE));
            link.set(GenericOrganizationalUnitContactCollection.CONTACT_ORDER,
                     contacts.get(
                    GenericOrganizationalUnitContactCollection.LINK_CONTACT_ORDER));

            link.save();
        }
    }

    private void createPersonAssoc(final DataCollection persons) {
        final GenericPersonBundle draftPerson = (GenericPersonBundle) DomainObjectFactory.
                newInstance(persons.getDataObject());
        final GenericPersonBundle livePerson = (GenericPersonBundle) draftPerson.
                getLiveVersion();

        if (livePerson != null) {
            final DataObject link = add(PERSONS, livePerson);

            link.set(GenericOrganizationalUnitPersonCollection.PERSON_ROLE,
                     persons.get(
                    GenericOrganizationalUnitPersonCollection.LINK_PERSON_ROLE));
            link.set(GenericOrganizationalUnitPersonCollection.STATUS,
                     persons.get(
                    GenericOrganizationalUnitPersonCollection.LINK_STATUS));

            link.save();
        }

    }
}
