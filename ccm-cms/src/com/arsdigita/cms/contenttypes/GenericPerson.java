/*
 * Copyright (C) 2010 Sören Bernstein
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
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.LanguageInvariantContentItem;
import com.arsdigita.cms.RelationAttributeInterface;
import com.arsdigita.cms.contenttypes.ui.GenericPersonExtraXmlGenerator;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Basic GenericPerson Contenttype for OpenCCM.
 *
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @author Jens Pelzetter
 */
public class GenericPerson extends ContentPage implements
        RelationAttributeInterface,
        LanguageInvariantContentItem {

    public static final String PERSON = "person";
    public static final String SURNAME = "surname";
    public static final String GIVENNAME = "givenname";
    public static final String TITLEPRE = "titlepre";
    public static final String TITLEPOST = "titlepost";
    public static final String BIRTHDATE = "birthdate";
    public static final String GENDER = "gender";
    public static final String CONTACTS = "contacts";
    public static final String CONTACTS_KEY = "linkKey";
    public static final String CONTACTS_ORDER = "linkOrder";
    public static final String ALIAS = "alias";    
    private static final String RELATION_ATTRIBUTES =
                                "contacts.link_key:GenericContactType";
    /**
     * Data object type for this domain object
     */
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.contenttypes.GenericPerson";

    /**
     * Default constructor. This creates a new (empty) GenericPerson.
     *
     */
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

    public GenericPersonBundle getGenericPersonBundle() {
        return (GenericPersonBundle) getContentBundle();
    }

    @Override
    public void beforeSave() {
        super.beforeSave();

        Assert.exists(getContentType(), ContentType.class);
    }

    /*
     * accessors ****************************************************
     */
    public String getSurname() {
        return (String) get(SURNAME);
    }

    public void setSurname(String surname) {
        set(SURNAME, surname);
        updateNameAndTitle();
    }

    public String getGivenName() {
        return (String) get(GIVENNAME);
    }

    public void setGivenName(String givenName) {
        set(GIVENNAME, givenName);
        updateNameAndTitle();
    }

    public String getTitlePre() {
        return (String) get(TITLEPRE);
    }

    public void setTitlePre(String titlePre) {
        set(TITLEPRE, titlePre);
        updateNameAndTitle();
    }

    public String getTitlePost() {
        return (String) get(TITLEPOST);
    }

    public void setTitlePost(String titlePost) {
        set(TITLEPOST, titlePost);
        updateNameAndTitle();
    }

    public Date getBirthdate() {
        return (Date) get(BIRTHDATE);
    }

    public void setBirthdate(Date birthdate) {
        set(BIRTHDATE, birthdate);
    }

    public String getGender() {
        return (String) get(GENDER);
    }

    public void setGender(String gender) {
        set(GENDER, gender);
    }

    public GenericPerson getAlias() {
        return (GenericPerson) DomainObjectFactory.newInstance((DataObject) get(
                ALIAS));
    }

    public void setAlias(final GenericPerson alias) {
        set(ALIAS, alias);
    }

    public void unsetAlias() {
        set(ALIAS, null);
    }
   
    /**
     * A convenient method which combines {@code titlePre}, {@code givenName),
     * {@code surname} and {@code titlePost}.
     *
     * @return {@code titlePre} {@code givenName) {@code surnameName} {@code titlePost}
     */
    public String getFullName() {
        String titlePre = getTitlePre();
        String givenName = getGivenName();
        String surname = getSurname();
        String titlePost = getTitlePost();

        if (titlePre == null) {
            titlePre = "";
        }
        if (titlePost == null) {
            titlePost = "";
        }
        if (givenName == null) {
            givenName = "";
        }
        if (surname == null) {
            surname = "";
        }

        if (titlePost.trim().isEmpty()) {
            return String.format("%s %s %s", titlePre, givenName, surname).trim();
        } else {
            return String.format("%s %s %s, %s", titlePre, givenName, surname,
                                 titlePost).trim();
        }
    }

    private void updateNameAndTitle() {
        // Sync title and name with CI data
        String fullname = getFullName();
        if (fullname != null && !fullname.isEmpty()) {
            setTitle(fullname);
            setName(GenericPerson.urlSave(String.format("%s %s", getSurname(),
                                                        getGivenName())));
        }
    }

    // Get all contacts for this person
    public GenericPersonContactCollection getContacts() {
        //return new GenericPersonContactCollection(
        //      (DataCollection) get(CONTACTS));
        return getGenericPersonBundle().getContacts();
    }

    // Add a contact for this person
    public void addContact(final GenericContact contact,
                           final String contactType) {
        /*
         * Assert.exists(contact, GenericContact.class);
         *
         * DataObject link = add(CONTACTS, contact);
         *
         * link.set(CONTACTS_KEY, contactType); link.set(CONTACTS_ORDER,
         * BigDecimal.valueOf(getContacts().size()));
         */
        getGenericPersonBundle().addContact(contact, contactType);
    }

    // Remove a contact for this person
    public void removeContact(final GenericContact contact) {
        //Assert.exists(contact, GenericContact.class);
        //remove(CONTACTS, contact);
        getGenericPersonBundle().removeContact(contact);
    }

    public boolean hasContacts() {
        return !this.getContacts().isEmpty();
    }

    // Create a ulr save version of the full name
    public static String urlSave(String in) {

        if (in != null && !in.isEmpty()) {

            // Replacement map
            String[][] replacements = {{"ä", "ae"}, {"Ä", "Ae"}, {"ö", "oe"}, {
                    "Ö", "Oe"}, {"ü", "ue"}, {"Ü", "Ue"}, {"ß", "ss"}, {".", ""}};

            // Replace all spaces with dash
            String out = in.replace(" ", "-");

            // Replace all special chars defined in replacement map
            for (int i = 0; i < replacements.length; i++) {
                out = out.replace(replacements[i][0], replacements[i][1]);
            }

            // Replace all special chars that are not yet replaced with a dash
            return out.replaceAll("[^A-Za-z0-9-]", "_").toLowerCase();
        }

        return in;

    }

    @Override
    public boolean hasRelationAttributes() {
        return !RELATION_ATTRIBUTES.isEmpty();
    }

    @Override
    public boolean hasRelationAttributeProperty(String propertyName) {
        StringTokenizer strTok = new StringTokenizer(RELATION_ATTRIBUTES, ";");
        while (strTok.hasMoreTokens()) {
            String token = strTok.nextToken();
            if (token.startsWith(propertyName + ".")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public StringTokenizer getRelationAttributes() {
        return new StringTokenizer(RELATION_ATTRIBUTES, ";");
    }

    @Override
    public String getRelationAttributeKeyName(String propertyName) {
        StringTokenizer strTok = new StringTokenizer(RELATION_ATTRIBUTES, ";");
        while (strTok.hasMoreTokens()) {
            String token = strTok.nextToken();
            if (token.startsWith(propertyName + ".")) {
                return token.substring(token.indexOf(".") + 1,
                                       token.indexOf(":"));
            }
        }
        return null;
    }

    @Override
    public String getRelationAttributeName(String propertyName) {
        StringTokenizer strTok = new StringTokenizer(RELATION_ATTRIBUTES, ";");
        while (strTok.hasMoreTokens()) {
            String token = strTok.nextToken();
            if (token.startsWith(propertyName + ".")) {
                return token.substring(token.indexOf(":") + 1);
            }
        }
        return null;
    }

    @Override
    public String getRelationAttributeKey(String propertyName) {
        return null;
    }

    @Override
    public String getSearchSummary() {
        return getFullName();
    }

    @Override
    public boolean isLanguageInvariant() {
        return true;
    }

    @Override
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.
                getExtraListXMLGenerators();
        generators.add(new GenericPersonExtraXmlGenerator());
        return generators;
    }

    @Override
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        final List<ExtraXMLGenerator> generators = super.
                getExtraListXMLGenerators();
        generators.add(new GenericPersonExtraXmlGenerator());
        return generators;
    }
}
