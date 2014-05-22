/*
 * Copyright (c) 2014 Jens Pelzetter
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
package com.arsdigita.cms.contentassets.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contentassets.PublicationCollection;
import com.arsdigita.cms.contentassets.SciPublicationsAboutService;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciPublicationsAboutExtraXMLGenerator implements ExtraXMLGenerator {

    private boolean listMode;
    
    @Override
    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (listMode) {
            //In list mode we don't need th information provided by this ExtraXMLGenerator
            return;
        }
        
        if (!(item instanceof Publication)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    Publication.class.getName()));
        }

        final Publication publication = (Publication) item;
        final SciPublicationsAboutService service = new SciPublicationsAboutService();
        final PublicationCollection discussedPublications = service.getDiscussedPublications(
                publication);
        final PublicationCollection discussingPublications = service.
                getDiscussingPublications(publication);
         if ((discussedPublications != null) && !discussedPublications.isEmpty()) {
            final Element discussedElem = element.newChildElement("discussedPublications");
            
            while(discussedPublications.next()) {
                addDiscussedPublication(discussedElem, 
                                        discussedPublications.getPublication(), 
                                        state);
            }
        }
        
        if ((discussingPublications != null) && !discussingPublications.isEmpty()) {
            final Element discussingElem = element.newChildElement("discussingPublications");
            
            while(discussingPublications.next()) {
                addDiscussingPublication(discussingElem, 
                                         discussingPublications.getPublication(), 
                                         state);
            }
        }
    }
    
    private void addDiscussedPublication(final Element parent, 
                                         final Publication discussed, 
                                         final PageState state) {
        final XmlGenerator generator = new XmlGenerator(discussed);
        generator.setItemElemName("publication", "");
        generator.setListMode(true);
        generator.generateXML(state, parent, "");
    }
    
    private void addDiscussingPublication(final Element parent, 
                                          final Publication discussing,
                                          final PageState state) {
        final XmlGenerator generator = new XmlGenerator(discussing);
        generator.setItemElemName("publication", "");
        generator.setListMode(true);
        generator.generateXML(state, parent, "");
    }

    @Override
    public void addGlobalStateParams(final Page page) {
        //Nothing
    }

    @Override
    public void setListMode(final boolean listMode) {
        this.listMode = listMode;
    }
    
    private class XmlGenerator extends SimpleXMLGenerator {

        private final ContentItem item;

        public XmlGenerator(final ContentItem item) {
            super();
            this.item = item;
        }

        @Override
        protected ContentItem getContentItem(final PageState state) {
            return item;
        }

    }

}
