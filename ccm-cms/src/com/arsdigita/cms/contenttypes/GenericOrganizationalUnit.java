/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.contenttypes.ui.GenericOrganizationalUnitSubordinateOrgaUnitAddForm;
import com.arsdigita.cms.contenttypes.ui.GenericOrganizationalUnitSubordinateOrgaUnitsTable;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * This class provides a base type for building content types which represent 
 * organizations, departments, projects etc.
 *
 * An item of this content type can be linked with several other content types:
 * - Contact (0..n)
 * - Person (0..n)
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class GenericOrganizationalUnit extends ContentPage {

    private static final Logger logger = Logger.getLogger(
            GenericOrganizationalUnit.class);
    //public final static String ORGAUNIT_NAME = "ORGAUNIT_NAME";
    public final static String ADDENDUM = "addendum";
    public final static String CONTACTS = "contacts";
    public final static String PERSONS = "persons";
    public final static String SUPERIOR_ORGAUNITS = "superiorOrgaunits";
    public final static String SUBORDINATE_ORGAUNITS = "subordinateOrgaunits";
    public final static String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.GenericOrganizationalUnit";

    public GenericOrganizationalUnit() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public GenericOrganizationalUnit(final BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public GenericOrganizationalUnit(final OID oid)
            throws DataObjectNotFoundException {
        super(oid);
    }

    public GenericOrganizationalUnit(final DataObject obj) {
        super(obj);
    }

    public GenericOrganizationalUnit(final String type) {
        super(type);
    }

    public String getAddendum() {
        return (String) get(ADDENDUM);
    }

    public void setAddendum(final String addendum) {
        set(ADDENDUM, addendum);
    }

    public GenericOrganizationalUnitContactCollection getContacts() {
        return new GenericOrganizationalUnitContactCollection((DataCollection) get(
                CONTACTS));
    }

    public void addContact(final GenericContact contact,
                           final String contactType) {
        Assert.exists(contact, GenericContact.class);

        logger.debug(String.format("Adding contact of type \"%s\"...",
                                   contactType));
        final DataObject link = add(CONTACTS, contact);

        link.set(GenericOrganizationalUnitContactCollection.CONTACT_TYPE,
                 contactType);
        link.set(GenericOrganizationalUnitContactCollection.CONTACT_ORDER,
                 Integer.valueOf((int) getContacts().size()));
        link.save();
    }

    public void removeContact(final GenericContact contact) {
        Assert.exists(contact, GenericContact.class);
        remove(CONTACTS, contact);
    }

    public boolean hasContacts() {
        return !this.getContacts().isEmpty();
    }

    public GenericOrganizationalUnitPersonCollection getPersons() {
        DataCollection dataColl = (DataCollection) get(PERSONS);
        logger.debug(String.format(
                "GenericOrganizationalUnitPersonCollection size = %d", dataColl.
                size()));
        return new GenericOrganizationalUnitPersonCollection(dataColl);
    }

    public void addPerson(final GenericPerson person,
                          final String role,
                          final String status) {
        Assert.exists(person, GenericPerson.class);

        GenericPerson personToLink = person;

        final ContentBundle bundle = person.getContentBundle();
        if ((bundle != null) && (bundle.hasInstance(this.getLanguage()))) {
            personToLink =
            (GenericPerson) bundle.getInstance(this.getLanguage());
        }

        Assert.exists(personToLink, GenericPerson.class);

        final DataObject link = add(PERSONS, personToLink);

        link.set(GenericOrganizationalUnitPersonCollection.PERSON_ROLE, role);
        link.set(GenericOrganizationalUnitPersonCollection.STATUS, status);
        link.save();
    }

    public void removePerson(final GenericPerson person) {
        logger.debug("Removing person association...");
        Assert.exists(person, GenericPerson.class);
        remove(PERSONS, person);
    }

    public boolean hasPersons() {
        return !this.getPersons().isEmpty();
    }

    public GenericOrganizationalUnitSuperiorCollection getSuperiorOrgaUnits() {
        final DataCollection dataCollection = (DataCollection) get(
                SUPERIOR_ORGAUNITS);
        return new GenericOrganizationalUnitSuperiorCollection(dataCollection);
    }

    public void addSuperiorOrgaUnit(final GenericOrganizationalUnit orgaunit,
                                    final String assocType) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);

        final DataObject link = add(SUPERIOR_ORGAUNITS, orgaunit);
        link.set(GenericOrganizationalUnitSuperiorCollection.ASSOCTYPE,
                 assocType);
        link.set(
                GenericOrganizationalUnitSuperiorCollection.SUPERIOR_ORGAUNIT_ORDER,
                getSuperiorOrgaUnits().size());
        link.save();
    }

    public void addSuperiorOrgaUnit(final GenericOrganizationalUnit orgaunit) {
        addSuperiorOrgaUnit(orgaunit, "");
    }
    
    public void removeSuperiorOrgaUnit(
            final GenericOrganizationalUnit orgaunit) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);
        remove(SUPERIOR_ORGAUNITS, orgaunit);
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

        final DataObject link = add(SUBORDINATE_ORGAUNITS, orgaunit);
        link.set(GenericOrganizationalUnitSubordinateCollection.ASSOCTYPE,
                 assocType);
        link.set(
                GenericOrganizationalUnitSubordinateCollection.SUBORDINATE_ORGAUNIT_ORDER,
                 getSubordinateOrgaUnits().size());
        link.save();
    }
    
     public void addSubordinateOrgaUnit(final GenericOrganizationalUnit orgaunit) {
         addSubordinateOrgaUnit(orgaunit, "");
     }
     
      public void removeSubordinateOrgaUnit(
            final GenericOrganizationalUnit orgaunit) {
        Assert.exists(orgaunit, GenericOrganizationalUnit.class);
        remove(SUBORDINATE_ORGAUNITS, orgaunit);
    }
    
    public boolean hasSubordinateOrgaUnits() {
        return !getSubordinateOrgaUnits().isEmpty();
    }
}
