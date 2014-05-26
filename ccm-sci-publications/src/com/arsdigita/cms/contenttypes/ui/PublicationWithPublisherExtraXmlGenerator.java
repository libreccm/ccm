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
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.Publisher;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationWithPublisherExtraXmlGenerator
        implements ExtraXMLGenerator {

    @Override
    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof PublicationWithPublisher)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    PublicationWithPublisher.class.getName()));
        }

        final PublicationWithPublisher publication =
                                       (PublicationWithPublisher) item;
        createPublisherXml(publication, element, state);
    }

    private void createPublisherXml(final PublicationWithPublisher publication,
                                    final Element parent,
                                    final PageState state) {
        final Publisher publisher =
                        publication.getPublisher(GlobalizationHelper.
                getNegotiatedLocale().getLanguage());
        if (publisher == null) {
            return;
        }

        final XmlGenerator generator = new XmlGenerator(publisher);
        generator.setItemElemName("publisher", "");
        generator.generateXML(state, parent, "");                
    }

    public void addGlobalStateParams(final Page p) {
        //nothing                
    }
    
    @Override
    public void setListMode(final boolean listMode) {
        //nothing
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
