/*
 * Copyright (c) 2010 Jens Pelzetter
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

    @Override
    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {        
        if (!(item instanceof GenericPerson)) {
            throw new IllegalArgumentException(
                    "The GenericPersonExtraXmlGenerator can only process "
                    + "instances of GenericPerson");
        }       

        final GenericPerson person = (GenericPerson) item;
        //final long start = System.nanoTime();
        final GenericPersonContactCollection contacts = person.getContacts();        
        //System.out.printf("Got contacts in %d ms\n", (System.nanoTime() - start) / 1000000);

        final Element contactsElem = element.newChildElement("contacts");
        while (contacts.next()) {            
            //final long start2 = System.nanoTime();
            final GenericContact contact = contacts.getContact(GlobalizationHelper.getNegotiatedLocale().getLanguage());
            //System.err.printf("Got contact in %d ms from collection\n", (System.nanoTime() - start2) / 1000000);
            generateContactXml(
                    contactsElem,                    
                    contact,
                    state);            
        }        
    }

    private void generateContactXml(final Element contactsElem,
                                    final GenericContact contact,
                                    final PageState state) {
        if (contact == null) {
            return;
        }

        //final long start = System.nanoTime();
        final XmlGenerator generator = new XmlGenerator(contact);
        generator.setItemElemName("contact", "");
        generator.addItemAttribute("contactType", contact.getContactType());
        generator.generateXML(state, contactsElem, "");
        //System.err.printf("Generated XML for a contact in %d ms\n", (System.nanoTime() - start)  / 1000000);
    }

    @Override
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
