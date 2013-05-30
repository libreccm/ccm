package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCollection;
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
    public final static String SUPERIOR_ORGAUNITS = "superiorOrgaunits";
    public final static String SUBORDINATE_ORGAUNITS = "subordinateOrgaunits";

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
        
        updatePersonsStr();
    }

    public void removePerson(final GenericPerson person) {
        Assert.exists(person, GenericPerson.class);

        remove(PERSONS, person.getContentBundle());
        
        updatePersonsStr();
    }

    public boolean hasPersons() {
        return !getPersons().isEmpty();
    }

    protected void updatePersonsStr() {
        final GenericOrganizationalUnitPersonCollection persons = getPersons();
        final StringBuilder builder = new StringBuilder();
        while(persons.next()) {
            if (builder.length() > 0) {
                builder.append("; ");
            }
            builder.append(persons.getSurname());
            builder.append(", ");
            builder.append(persons.getGivenName());
        }
        
        final String personsStr = builder.toString();
        
        final ItemCollection instances = getInstances();
        
        GenericOrganizationalUnit orgaunit;
        while(instances.next()) {
            orgaunit = (GenericOrganizationalUnit) instances.getDomainObject();
            orgaunit.set(GenericOrganizationalUnit.PERSONS_STR, personsStr);
        }
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

    public GenericOrganizationalUnitSuperiorCollection getSuperiorOrgaUnits() {
        return new GenericOrganizationalUnitSuperiorCollection((DataCollection) get(
                SUPERIOR_ORGAUNITS));
    }

    public void addSuperiorOrgaUnit(final GenericOrganizationalUnit orgaunit,
                                    final String assocType) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);

        final DataObject link =
                         add(SUPERIOR_ORGAUNITS,
                             orgaunit.getGenericOrganizationalUnitBundle());
        link.set(GenericOrganizationalUnitSuperiorCollection.ASSOCTYPE,
                 assocType);
        link.set(
                GenericOrganizationalUnitSuperiorCollection.SUPERIOR_ORGAUNIT_ORDER,
                (int) getSuperiorOrgaUnits().size());
        link.set(
                GenericOrganizationalUnitSubordinateCollection.SUBORDINATE_ORGAUNIT_ORDER,
                (int) getSubordinateOrgaUnits().size() + 1);

        link.save();
    }

    public void addSuperiorOrgaUnit(final GenericOrganizationalUnit orgaunit) {
        addSuperiorOrgaUnit(orgaunit, "");
    }

    public void removeSuperiorOrgaUnit(
            final GenericOrganizationalUnit orgaunit) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);
        remove(SUPERIOR_ORGAUNITS, orgaunit.getGenericOrganizationalUnitBundle());
    }

    public boolean hasSuperiorOrgaUnits() {
        return !getSuperiorOrgaUnits().isEmpty();
    }

    /**
     * Gets a collection of subordinate organizational units. Note that their
     * is no authoring step registered for this property. The {@code ccm-cms} 
     * module provides only a form for adding subordinate organizational units
     * and a table for showing them. Subtypes of 
     * {@code GenericOrganizationalUnit}  may add these components to their 
     * authoring steps via a new authoring step which contains the form
     * and the table. These authoring steps should be registered by using 
     * {@link AuthoringKitWizard#registerAssetStep(java.lang.String, java.lang.Class, com.arsdigita.globalization.GlobalizedMessage, com.arsdigita.globalization.GlobalizedMessage, int) }
     * in the initalizer of the content type. Some aspects of the form and
     * table, for example the labels, can be configured using implementations
     * of two interfaces. Please refer to the documentation of 
     * {@link GenericOrganizationalUnitSubordinateOrgaUnitsTable} and 
     * {@link GenericOrganizationalUnitSubordinateOrgaUnitAddForm} 
     * for more information about customizing the table and the form.
     * 
     * @return A collection of subordinate organizational units.
     */
    public GenericOrganizationalUnitSubordinateCollection getSubordinateOrgaUnits() {
        final DataCollection dataCollection = (DataCollection) get(
                SUBORDINATE_ORGAUNITS);
        return new GenericOrganizationalUnitSubordinateCollection(dataCollection);
    }

    public void addSubordinateOrgaUnit(final GenericOrganizationalUnit orgaunit,
                                       final String assocType) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);

        final DataObject link =
                         add(SUBORDINATE_ORGAUNITS,
                             orgaunit.getGenericOrganizationalUnitBundle());
        link.set(GenericOrganizationalUnitSubordinateCollection.ASSOCTYPE,
                 assocType);
        link.set(
                GenericOrganizationalUnitSubordinateCollection.SUBORDINATE_ORGAUNIT_ORDER,
                (int) getSubordinateOrgaUnits().size());
        link.set(
                GenericOrganizationalUnitSuperiorCollection.SUPERIOR_ORGAUNIT_ORDER,
                ((int) getSuperiorOrgaUnits().size()) + 1);
        link.save();
    }

    public void addSubordinateOrgaUnit(final GenericOrganizationalUnit orgaunit) {
        addSubordinateOrgaUnit(orgaunit, "");
    }

    public void removeSubordinateOrgaUnit(
            final GenericOrganizationalUnit orgaunit) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);
        remove(SUBORDINATE_ORGAUNITS,
               orgaunit.getGenericOrganizationalUnitBundle());
    }

    public boolean hasSubordinateOrgaUnits() {
        return !getSubordinateOrgaUnits().isEmpty();
    }

    @Override
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        final String attribute = property.getName();
        if (copier.getCopyType() == ItemCopier.VERSION_COPY) {
            final GenericOrganizationalUnitBundle orgaBundle =
                                                  (GenericOrganizationalUnitBundle) source;

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
            } else if (SUPERIOR_ORGAUNITS.equals(attribute)) {
                final DataCollection superOrgaUnits =
                                     (DataCollection) orgaBundle.get(
                        SUPERIOR_ORGAUNITS);

                while (superOrgaUnits.next()) {
                    createSuperiorAssoc(superOrgaUnits);
                }

                return true;
            } else if (SUBORDINATE_ORGAUNITS.equals(attribute)) {
                final DataCollection subOrgaUnits = (DataCollection) orgaBundle.
                        get(SUBORDINATE_ORGAUNITS);

                while (subOrgaUnits.next()) {
                    createSubordinateAssoc(subOrgaUnits);
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
        final GenericPersonBundle draftPerson =
                                  (GenericPersonBundle) DomainObjectFactory.
                newInstance(persons.getDataObject());
        final GenericPersonBundle livePerson =
                                  (GenericPersonBundle) draftPerson.
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

    private void createSuperiorAssoc(final DataCollection superOrgaUnits) {
        final GenericOrganizationalUnitBundle draftOrga =
                                              (GenericOrganizationalUnitBundle) DomainObjectFactory.
                newInstance(superOrgaUnits.getDataObject());
        final GenericOrganizationalUnitBundle liveOrga =
                                              (GenericOrganizationalUnitBundle) draftOrga.
                getLiveVersion();

        if (liveOrga != null) {
            final DataObject link = add(SUPERIOR_ORGAUNITS, liveOrga);

            link.set(GenericOrganizationalUnitSuperiorCollection.ASSOCTYPE,
                     superOrgaUnits.get(
                    GenericOrganizationalUnitSuperiorCollection.LINK_ASSOCTYPE));
            link.set(
                    GenericOrganizationalUnitSuperiorCollection.SUPERIOR_ORGAUNIT_ORDER,
                    superOrgaUnits.get(
                    GenericOrganizationalUnitSuperiorCollection.LINK_SUPERIOR_ORGAUNIT_ORDER));
            link.set(
                    GenericOrganizationalUnitSubordinateCollection.SUBORDINATE_ORGAUNIT_ORDER,
                    superOrgaUnits.get(
                    GenericOrganizationalUnitSubordinateCollection.LINK_SUBORDINATE_ORGAUNIT_ORDER));

            link.save();
        }

    }

    private void createSubordinateAssoc(final DataCollection subOrgaUnits) {
        final GenericOrganizationalUnitBundle draftOrga =
                                              (GenericOrganizationalUnitBundle) DomainObjectFactory.
                newInstance(subOrgaUnits.getDataObject());
        final GenericOrganizationalUnitBundle liveOrga =
                                              (GenericOrganizationalUnitBundle) draftOrga.
                getLiveVersion();

        if (liveOrga != null) {
            final DataObject link = add(SUBORDINATE_ORGAUNITS, liveOrga);

            link.set(GenericOrganizationalUnitSubordinateCollection.ASSOCTYPE,
                     subOrgaUnits.get(
                    GenericOrganizationalUnitSubordinateCollection.LINK_ASSOCTYPE));
            link.set(
                    GenericOrganizationalUnitSubordinateCollection.SUBORDINATE_ORGAUNIT_ORDER,
                    subOrgaUnits.get(
                    GenericOrganizationalUnitSubordinateCollection.LINK_SUBORDINATE_ORGAUNIT_ORDER));
            link.set(
                    GenericOrganizationalUnitSuperiorCollection.SUPERIOR_ORGAUNIT_ORDER,
                    subOrgaUnits.get(
                    GenericOrganizationalUnitSuperiorCollection.LINK_SUPERIOR_ORGAUNIT_ORDER));

            link.save();
        }
    }
}
