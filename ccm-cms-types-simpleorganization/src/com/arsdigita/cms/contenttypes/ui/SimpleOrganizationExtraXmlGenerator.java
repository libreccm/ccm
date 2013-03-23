package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnitContactCollection;
import com.arsdigita.cms.contenttypes.SimpleOrganization;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SimpleOrganizationExtraXmlGenerator implements ExtraXMLGenerator {

    private boolean listMode;

    public void generateXML(final ContentItem item, final Element element, final PageState state) {
        if (!(item instanceof SimpleOrganization)) {
            throw new IllegalArgumentException("The SimpleOrganizationExtraXMLGenerator can only process instances"
                                               + "of SimpleOrganization.");
        }

        final SimpleOrganization organization = (SimpleOrganization) item;
        final GenericOrganizationalUnitContactCollection contacts = organization.getContacts();

        final Element contactsElem = element.newChildElement("contacts");
        while (contacts.next()) {
            final GenericContact contact = contacts.getContact(GlobalizationHelper.getNegotiatedLocale().getLanguage());            
            generateContactXml(contactsElem, contact, contacts.getContactType(), state);
        }
    }

    private void generateContactXml(final Element contactsElem,
                                    final GenericContact contact,
                                    final String contactType,
                                    final PageState state) {
        //final long start = System.nanoTime();
        final XmlGenerator generator = new XmlGenerator(contact);
        generator.setItemElemName("contact", "");
        generator.addItemAttribute("contactType", contactType);
        generator.generateXML(state, contactsElem, "");
        //System.err.printf("Generated XML for a contact in %d ms\n", (System.nanoTime() - start)  / 1000000);
    }

    public void addGlobalStateParams(final Page page) {
        //Nothing for now
    }

    public void setListMode(final boolean listMode) {
        this.listMode = listMode;
    }

    private class XmlGenerator extends SimpleXMLGenerator {

        private ContentItem item;

        public XmlGenerator(final ContentItem item) {
            this.item = item;
        }

        @Override
        public ContentItem getContentItem(final PageState state) {
            return item;
        }

    }
}
