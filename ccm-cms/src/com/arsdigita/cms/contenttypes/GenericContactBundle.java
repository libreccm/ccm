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
 * @version $Id$
 */
public class GenericContactBundle
        extends ContentBundle {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.GenericContactBundle";
    public static final String PERSON = "person";

    public GenericContactBundle(final ContentItem primary) {
        super(BASE_DATA_OBJECT_TYPE);

        Assert.exists(primary, ContentItem.class);

        setDefaultLanguage(primary.getLanguage());
        setContentType(primary.getContentType());
        addInstance(primary);

        super.setName(primary.getName());
    }

    public GenericContactBundle(final OID oid) throws
            DataObjectNotFoundException {
        super(oid);
    }

    public GenericContactBundle(final BigDecimal id) throws
            DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public GenericContactBundle(final DataObject dobj) {
        super(dobj);
    }

    public GenericContactBundle(final String type) {
        super(type);
    }

    /**
     * <p> Copy association properties. These are for example the associations
     * between GenericPerson and GenericContact, or between
     * GenericOrganizationalUnit and GenericPerson. </p> 
     *
     * @param source param property param copier
     * @param property 
     * @param copier 
     *
     * @return
     */
    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final GenericContactBundle contactBundle =
                                       (GenericContactBundle) source;

            if (PERSON.equals(attribute)) {
                final DataCollection persons = (DataCollection) contactBundle.
                        get(PERSON);

                while (persons.next()) {
                    createPersonAssoc(persons);
                }

                return true;
            } else if ("organizationalunit".equals(attribute)) {
                final DataCollection orgaunits = (DataCollection) contactBundle.
                        get("organizationalunit");

                while (orgaunits.next()) {
                    createOrgaUnitAssoc(orgaunits);
                }

                return true;
            } else {
                return super.copyProperty(source, property, copier);
            }
        } else {
            return super.copyProperty(source, property, copier);
        }
    }

    private void createPersonAssoc(final DataCollection persons) {
        final GenericPersonBundle draftPerson =
                                  (GenericPersonBundle) DomainObjectFactory.
                newInstance(
                persons.getDataObject());
        final GenericPersonBundle livePerson =
                                  (GenericPersonBundle) draftPerson.
                getLiveVersion();

        if (livePerson != null) {
            final DataObject link = add(PERSON, livePerson);

            link.set(GenericPerson.CONTACTS_KEY,
                     persons.get(GenericPersonContactCollection.CONTACTS_KEY));
            link.set(GenericPerson.CONTACTS_ORDER,
                     persons.get(GenericPersonContactCollection.CONTACTS_ORDER));

            link.save();
        }
    }

    private void createOrgaUnitAssoc(final DataCollection orgaunits) {
        final GenericOrganizationalUnitBundle draftOrga =
                                              (GenericOrganizationalUnitBundle) DomainObjectFactory.
                newInstance(orgaunits.getDataObject());
        final GenericOrganizationalUnitBundle liveOrga =
                                              (GenericOrganizationalUnitBundle) draftOrga.
                getLiveVersion();

        if (liveOrga != null) {
            final DataObject link = add("organizationalunit", liveOrga);

            link.set(GenericOrganizationalUnitContactCollection.CONTACT_TYPE,
                     orgaunits.get(
                    GenericOrganizationalUnitContactCollection.LINK_CONTACT_TYPE));
            link.set(GenericOrganizationalUnitContactCollection.CONTACT_ORDER,
                     orgaunits.get(
                    GenericOrganizationalUnitContactCollection.LINK_CONTACT_ORDER));

            link.save();
        }
    }

    public GenericPerson getPerson() {
        DataCollection collection;

        collection = (DataCollection) get(PERSON);

        if (collection.size() == 0) {
            return null;
        } else {
            DataObject dobj;

            collection.next();
            dobj = collection.getDataObject();

            // Close Collection to prevent an open ResultSet
            collection.close();

            final GenericPersonBundle bundle =
                                      (GenericPersonBundle) DomainObjectFactory.
                    newInstance(dobj);

            return (GenericPerson) bundle.getPrimaryInstance();
        }
    }

    public void setPerson(GenericPerson person, String contactType) {
        if (getPerson() != null) {
            unsetPerson();
        }

        if (person != null) {
            Assert.exists(person, GenericPerson.class);
            DataObject link = add(PERSON, person.getGenericPersonBundle());
            link.set(GenericPerson.CONTACTS_KEY, contactType);
            link.set(GenericPerson.CONTACTS_ORDER, new BigDecimal(person.
                    getContacts().size()));
            link.save();
        }
    }

    public void unsetPerson() {
        GenericPerson oldPerson;
        oldPerson = getPerson();
        if (oldPerson != null) {
            remove(PERSON, oldPerson.getGenericPersonBundle());
        }
    }
}
