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
import com.arsdigita.cms.contentassets.SciPublicationsPersonsPersonCollection;
import com.arsdigita.cms.contentassets.SciPublicationsPersonsService;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsPersonsExtraXMLGenerator implements ExtraXMLGenerator {

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
        final SciPublicationsPersonsService service = new SciPublicationsPersonsService();
        final SciPublicationsPersonsPersonCollection persons = service.getPersons(publication);
        if ((persons != null) && !persons.isEmpty()) {
            final Element personsElem = element.newChildElement("persons");

            while (persons.next()) {
                addPerson(personsElem, persons.getPerson(), persons.getRelation(), state);
            }
        }

    }

    private void addPerson(final Element parent,
                           final GenericPerson person,
                           final String relation,
                           final PageState state) {

        final XmlGenerator generator = new XmlGenerator(person);
        generator.setItemElemName("person", "");
        generator.addItemAttribute("relation", relation);
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
