/*
 * Copyright (C) 2010 Sören Bernstein, for the Center of Social Politics of the University of Bremen
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

import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentPage;
import com.arsdigita.cms.RelationAttribute;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.StringTokenizer;
import java.util.Date;

/**
 * Basic GenericPerson Contenttype for OpenCCM.
 *
 * @author Sören Bernstein
 * @author Jens Pelzetter
 */
public class GenericPerson extends ContentPage implements RelationAttribute {

    public static final String PERSON = "person";
    public static final String SURNAME = "surname";
    public static final String GIVENNAME = "givenname";
    public static final String TITLEPRE = "titlepre";
    public static final String TITLEPOST = "titlepost";
    public static final String BIRTHDATE = "birthdate";
    public static final String CONTACTS = "contacts";
    public static final String CONTACT_TYPE = "contact_type";
    public static final String CONTACT_ORDER = "contact_order";
    private static final String RELATION_ATTRIBUTES = "GenericContactType";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contenttypes.GenericPerson";

    /**
     * Default constructor. This creates a new (empty) GenericPerson.
     **/
    public GenericPerson() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public GenericPerson(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public GenericPerson(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    public GenericPerson(DataObject obj) {
        super(obj);
    }

    public GenericPerson(String type) {
        super(type);
    }

    @Override
    public void beforeSave() {
        super.beforeSave();

        Assert.exists(getContentType(), ContentType.class);
    }

    /* accessors *****************************************************/
    public String getSurname() {
        return (String) get(SURNAME);
    }

    public void setSurname(String surname) {
        set(SURNAME, surname);
    }

    public String getGivenName() {
        return (String) get(GIVENNAME);
    }

    public void setGivenName(String givenName) {
        set(GIVENNAME, givenName);
    }

    public String getTitlePre() {
        return (String) get(TITLEPRE);
    }
     public Date getBirthdate() {
         return (Date)get(BIRTHDATE);
     }
     public void setBirthdate(Date birthdate) {
         set(BIRTHDATE, birthdate);
     }

    public void setTitlePre(String titlePre) {
        set(TITLEPRE, titlePre);
    }

    public String getTitlePost() {
        return (String) get(TITLEPOST);
    }

    public void setTitlePost(String titlePost) {
        set(TITLEPOST, titlePost);
    }

    // Get all contacts for this person
    public GenericPersonContactCollection getContacts() {
        return new GenericPersonContactCollection((DataCollection) get(CONTACTS));
    }

    // Add a contact for this person
    public void addContact(GenericContact contact, String contactType) {
        Assert.exists(contact, GenericContact.class);

        DataObject link = add(CONTACTS, contact);

        link.set(CONTACT_TYPE, contactType);
        link.set(CONTACT_ORDER, BigDecimal.valueOf(getContacts().size()));
    }

    // Remove a contact for this person
    public void removeContact(GenericContact contact) {
        Assert.exists(contact, GenericContact.class);
        remove(CONTACTS, contact);
    }

    public boolean hasContacts() {
        return !this.getContacts().isEmpty();
    }

    @Override
    public boolean hasRelationAttributes() {
        return !RELATION_ATTRIBUTES.isEmpty();
    }

    @Override
    public StringTokenizer getRelationAttributes() {
        return new StringTokenizer(RELATION_ATTRIBUTES, ";");
    }
}
