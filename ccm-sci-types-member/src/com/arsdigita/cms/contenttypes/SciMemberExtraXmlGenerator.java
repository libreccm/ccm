package com.arsdigita.cms.contenttypes;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.cms.dispatcher.XMLGenerator;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciMemberExtraXmlGenerator implements ExtraXMLGenerator {

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof SciMember)) {
            throw new IllegalArgumentException(
                    "The SciMemberExtraXmlGenerator can only process "
                    + "instances of SciMember");
        }

        final SciMember member = (SciMember) item;
        final GenericPersonContactCollection contacts = member.getContacts();

        final Element contactsElem = element.newChildElement("contacts");
        while (contacts.next()) {
            generateContactXml(contactsElem, contacts.getContact(), state);
        }
    }

    public void addGlobalStateParams(final Page page) {
        //Nothing for now
    }

    private void generateContactXml(final Element contactsElem,
                                    final GenericContact contact,
                                    final PageState state) {
        final XmlGenerator generator = new XmlGenerator(contact);
        generator.setItemElemName("contact", "");
        generator.addItemAttribute("contactType", contact.getContactType());
        generator.generateXML(state, contactsElem, "");
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
