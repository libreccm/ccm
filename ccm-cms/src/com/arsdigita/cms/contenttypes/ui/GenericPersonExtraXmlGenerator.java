package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.GenericContact;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.GenericPersonContactCollection;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class GenericPersonExtraXmlGenerator implements ExtraXMLGenerator {

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof GenericPerson)) {
            throw new IllegalArgumentException(
                    "The GenericPersonExtraXmlGenerator can only process "
                    + "instances of GenericPerson");
        }

        final GenericPerson person = (GenericPerson) item;
        final GenericPersonContactCollection contacts = person.getContacts();

        final Element contactsElem = element.newChildElement("contacts");
        while (contacts.next()) {
            generateContactXml(
                    contactsElem,
                    contacts.getContact(GlobalizationHelper.getNegotiatedLocale().
                    getLanguage()),
                    state);
        }
    }

    private void generateContactXml(final Element contactsElem,
                                    final GenericContact contact,
                                    final PageState state) {
        final XmlGenerator generator = new XmlGenerator(contact);
        generator.setItemElemName("contact", "");
        generator.addItemAttribute("contactType", contact.getContactType());
        generator.generateXML(state, contactsElem, "");
    }

    public void addGlobalStateParams(final Page page) {
        //Nothing
    }
    
    @Override
    public void setListMode(final boolean listMode) {
        //nothing
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
