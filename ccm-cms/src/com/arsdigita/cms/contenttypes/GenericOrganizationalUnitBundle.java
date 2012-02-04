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
}
