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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericAddress;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericContactEntry;
import com.arsdigita.cms.contenttypes.GenericContactEntryCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitPersonCollection;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter
 */
public class GenericOrganizationalUnitPanel extends CompoundContentItemPanel {

    public static final String SHOW_CONTACTS = "contacts";
    public static final String SHOW_MEMBERS = "members";
    private boolean displayContacts = true;
    private boolean displayMembers = true;

    @Override
    protected String getDefaultForShowParam() {
        return SHOW_CONTACTS;
    }

    @Override
    protected Class<? extends ContentItem> getAllowedClass() {
        return GenericOrganizationalUnit.class;
    }

    public boolean isDisplayContacts() {
        return displayContacts;
    }

    public void setDisplayContacts(boolean displayContacts) {
        this.displayContacts = displayContacts;
    }

    public boolean isDisplayMembers() {
        return displayMembers;
    }

    public void setDisplayMembers(boolean displayMembers) {
        this.displayMembers = displayMembers;
    }

    protected void generateContactsXML(GenericOrganizationalUnit orga,
                                       Element parent, PageState state) {
        GenericOrganizationalUnitContactCollection contacts;
        contacts = orga.getContacts();

        long pageNumber = getPageNumber(state);

        long pageCount = getPageCount(contacts.size());
        long begin = getPaginatorBegin(pageNumber);
        long count = getPaginatorCount(begin, contacts.size());
        long end = getPaginatorEnd(begin, count);
        pageNumber = normalizePageNumber(pageCount, pageNumber);

        createPaginatorElement(parent, pageNumber, pageCount, begin, end,
                               count, contacts.size());
        contacts.setRange((int) begin + 1, (int) end + 1);

        Element contactsElem = parent.newChildElement("contacts");
        while (contacts.next()) {
            GenericContact contact;
            contact = contacts.getContact();

            generateContactXML(contact,
                               contactsElem,
                               state,
                               Integer.toString(contacts.getContactOrder()),
                               true);
        }
    }

    protected void generateMembersXML(GenericOrganizationalUnit orga,
                                      Element parent, PageState state) {
        GenericOrganizationalUnitPersonCollection persons;
        persons = orga.getPersons();
        long pageNumber = getPageNumber(state);

        Element personsElem = parent.newChildElement("members");

        long pageCount = getPageCount(persons.size());
        long begin = getPaginatorBegin(pageNumber);
        long count = getPaginatorCount(begin, persons.size());
        long end = getPaginatorEnd(begin, persons.size());
        pageNumber = normalizePageNumber(pageCount, pageNumber);

        createPaginatorElement(parent, pageNumber, pageCount, begin, end,
                               count, persons.size());
        persons.setRange((int) begin + 1, (int) end + 1);

        while (persons.next()) {
            GenericPerson person;
            person = persons.getPerson();

            Element personElem = personsElem.newChildElement("member");
            Element title = personElem.newChildElement("title");
            title.setText(person.getTitle());

            if ((person.getTitlePre() != null)
                && !person.getTitlePre().isEmpty()) {
                Element titlePre = personElem.newChildElement("titlePre");
                titlePre.setText(person.getTitlePre());
            }

            Element surname = personElem.newChildElement("surname");
            surname.setText(person.getSurname());

            Element givenName = personElem.newChildElement("givenname");
            givenName.setText(person.getGivenName());

            if ((person.getTitlePost() != null)
                && !person.getTitlePost().isEmpty()) {
                Element titlePost = personElem.newChildElement("titlePost");
                titlePost.setText(person.getTitlePost());
            }

            GenericPersonContactCollection contacts = person.getContacts();
            if ((contacts != null) && (contacts.size() > 0)) {
                Element contactsElem =
                        personElem.newChildElement("contacts");
                while (contacts.next()) {
                    GenericContact contact = contacts.getContact();

                    generateContactXML(contact, contactsElem, state, contacts.
                            getContactOrder(), false);
                }
            }
        }
    }

    protected void generateContactXML(final GenericContact contact,
                                      final Element parent,
                                      final PageState state,
                                      final String order,
                                      final boolean withPerson) {
        ContactXmlLGenerator generator = new ContactXmlLGenerator(contact);
        
        generator.generateXML(state, parent, order);
        
        /*Element contactElem = parent.newChildElement("contact");
        contactElem.addAttribute("order", order);

        Element title = contactElem.newChildElement("title");
        title.setText(contact.getTitle());

        Element typeElem = contactElem.newChildElement("type");
        typeElem.setText(contact.getContactType());

        if (withPerson) {
            GenericPerson person = contact.getPerson();
            if (person != null) {
                Element personElem = contactElem.newChildElement("person");
                if ((person.getTitlePre() != null) && !person.getTitlePre().
                        isEmpty()) {
                    Element titlePre =
                            personElem.newChildElement("titlePre");
                    titlePre.setText(person.getTitlePre());
                }

                Element givenName = contactElem.newChildElement("givenname");
                givenName.setText(person.getGivenName());

                Element surname = contactElem.newChildElement("surname");
                surname.setText(person.getSurname());

                if ((person.getTitlePost() != null)
                    && !person.getTitlePost().isEmpty()) {
                    Element titlePost = contactElem.newChildElement(
                            "titlePost");
                    titlePost.setText(person.getTitlePost());
                }
            }
        }

        GenericContactEntryCollection contactEntries =
                                      contact.getContactEntries();
        if ((contactEntries != null)
            && (contactEntries.size() > 0)) {
            Element contactEntriesElem =
                    contactElem.newChildElement("contactEntries");
            while (contactEntries.next()) {
                GenericContactEntry contactEntry =
                                    contactEntries.getContactEntry();
                Element contactEntryElem =
                        contactEntriesElem.newChildElement(
                        "contactEntry");
                contactEntryElem.addAttribute("key",
                                              contactEntry.getKey());
                Element valueElem = contactEntryElem.newChildElement(
                        "value");
                valueElem.setText(contactEntry.getValue());

                if ((contactEntry.getDescription() != null)
                    && !contactEntry.getDescription().isEmpty()) {
                    Element descElem = contactEntryElem.newChildElement(
                            "description");
                    descElem.setText(contactEntry.getDescription());
                }
            }
        }

        GenericAddress address = contact.getAddress();
        if (address != null) {
            Element addressElem = contactElem.newChildElement(
                    "address");
            Element postalCode = addressElem.newChildElement(
                    "postalCode");
            postalCode.setText(address.getPostalCode());
            Element city = addressElem.newChildElement("city");
            city.setText(address.getCity());
            Element data = addressElem.newChildElement("address");
            data.setText(address.getAddress());
            Element country = addressElem.newChildElement("country");
            country.setText(address.getIsoCountryCode());
            Element theState = addressElem.newChildElement("state");
            theState.setText(address.getState());
        }*/
    }

    @Override
    public void generateXML(ContentItem item, Element element,
                            PageState state) {
        Element content = generateBaseXML(item, element, state);

        Element availableData = content.newChildElement("availableData");

        GenericOrganizationalUnit orga = (GenericOrganizationalUnit) item;

        if ((orga.getContacts() != null)
            && (orga.getContacts().size() > 0)
            && displayMembers) {
            availableData.newChildElement("contacts");
        }
        if ((orga.getPersons() != null)
            && (orga.getPersons().size() > 0)
            && displayMembers) {
            availableData.newChildElement("members");
        }

        String show = getShowParam(state);
        if (SHOW_CONTACTS.equals(show)) {
            generateContactsXML(orga, content, state);
        } else if (SHOW_MEMBERS.equals(show)) {
            generateMembersXML(orga, content, state);
        }
    }

    private class ContactXmlLGenerator extends SimpleXMLGenerator {

        private GenericContact contact;

        public ContactXmlLGenerator(final GenericContact contact) {
            super();
            this.contact = contact;
        }

        @Override
        protected ContentItem getContentItem(PageState state) {
            return contact;
        }
    }
}
