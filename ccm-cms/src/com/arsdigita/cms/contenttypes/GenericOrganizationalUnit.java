/*
 * Copyright (c) 2010 Jens Pelzetter, for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.cms.ContentPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 * This class provides a base type for building content types which represent organizations,
 * departments, projects etc.
 *
 * An item of this content type can be linked with several other content types:
 * - Contact (0..n)
 * - Person (0..n)
 * - GenericOrganizationalUnit (0..n) to build organizations structures.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class GenericOrganizationalUnit extends ContentPage {

    //public final static String ORGAUNIT_NAME = "ORGAUNIT_NAME";
    public final static String ADDENDUM = "addendum";
    public final static String CONTACTS = "contacts";
    public final static String CONTACT_TYPE = "contact_type";
    public final static String CONTACT_ORDER = "contact_order";
    public final static String ORGAUNIT_CHILDREN = "orgaunit_children";
    public final static String ORGAUNIT_CHILDREN_ORDER =
                               "orgaunit_children_order";
    public final static String PERSONS = "persons";
    public final static String ROLE = "role_name";
    public final static String PERSON_ORDER = "person_order";
    public final static String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.GenericOrganizationalUnit";

    public GenericOrganizationalUnit() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public GenericOrganizationalUnit(BigDecimal id) throws
            DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public GenericOrganizationalUnit(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public GenericOrganizationalUnit(DataObject obj) {
        super(obj);
    }

    public GenericOrganizationalUnit(String type) {
        super(type);
    }

    public String getAddendum() {
        return (String) get(ADDENDUM);
    }

    public void setAddendum(String addendum) {
        set(ADDENDUM, addendum);
    }

    public GenericOrganizationalUnitContactCollection getContacts() {
        return new GenericOrganizationalUnitContactCollection((DataCollection) get(
                CONTACTS));
    }

    public void addContact(GenericContact contact, String contactType) {
        Assert.exists(contact, GenericContact.class);

        DataObject link = add(CONTACTS, contact);

        link.set(CONTACT_TYPE, contactType);
        link.set(CONTACT_ORDER, BigDecimal.valueOf(getContacts().size()));
    }

    public void removeContact(GenericContact contact) {
        Assert.exists(contact, GenericContact.class);
        remove(CONTACTS, contact);
    }

    public boolean hasContacts() {
        return !this.getContacts().isEmpty();
    }

    public GenericOrganizationalUnitChildrenCollection getOrgaUnitChildren() {
        return new GenericOrganizationalUnitChildrenCollection((DataCollection) get(
                ORGAUNIT_CHILDREN));
    }

    public void addOrgaUnitChildren(GenericOrganizationalUnit child) {
        Assert.exists(child, GenericOrganizationalUnit.class);

        DataObject link = add(ORGAUNIT_CHILDREN, child);

        link.set(ORGAUNIT_CHILDREN_ORDER, Integer.valueOf((int) getOrgaUnitChildren().
                size()));
    }

    public void removeOrgaUnitChildren(GenericOrganizationalUnit child) {
        Assert.exists(child, GenericOrganizationalUnit.class);
        remove(ORGAUNIT_CHILDREN, child);
    }

    public boolean hasOrgaUnitChildren() {
        return !this.getOrgaUnitChildren().isEmpty();
    }

    public GenericOrganizationalUnitPersonCollection getPersons() {
        return new GenericOrganizationalUnitPersonCollection((DataCollection) get(
                PERSONS));
    }

    public void addPerson(GenericPerson person, String role) {
        Assert.exists(person, GenericPerson.class);

        DataObject link = add(PERSONS, person);

        link.set(ROLE, role);
        link.set(PERSON_ORDER, BigDecimal.valueOf(getPersons().size()));
    }

    public void removePerson(GenericPerson person) {
        Assert.exists(person, GenericPerson.class);
        remove(PERSONS, person);
    }

    public boolean hasPersons() {
        return !this.getPersons().isEmpty();
    }
}
