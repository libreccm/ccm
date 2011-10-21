package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericOrgaUnitContactsTab implements GenericOrgaUnitTab {

    @Override
    public boolean hasData(final GenericOrganizationalUnit orgaunit) {
        return !getData(orgaunit).isEmpty();
    }

    @Override
    public void generateXml(final GenericOrganizationalUnit orgaunit,
                            final Element parent,
                            final PageState state) {
        final GenericOrganizationalUnitContactCollection contacts = getData(orgaunit,
                                                                            state);

        Element contactsElem = parent.newChildElement("contacts");
        while (contacts.next()) {
            GenericContact contact;
            contact = contacts.getContact();

            generateGenericContactXML(contact,
                                      contactsElem,
                                      state,
                                      Integer.toString(
                    contacts.getContactOrder()),
                                      true);
        }
    }

    protected void generateGenericContactXML(final GenericContact contact,
                                             final Element parent,
                                             final PageState state,
                                             final String order,
                                             final boolean withPerson) {
        ContactXmlLGenerator generator = new ContactXmlLGenerator(contact);

        generator.generateXML(state, parent, order);
    }

    protected GenericOrganizationalUnitContactCollection getData(
            final GenericOrganizationalUnit orgaunit,
            final PageState state) {
       return getData(orgaunit);
    }
    
    protected GenericOrganizationalUnitContactCollection getData(
            final GenericOrganizationalUnit orgaunit) {
         return orgaunit.getContacts();
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
