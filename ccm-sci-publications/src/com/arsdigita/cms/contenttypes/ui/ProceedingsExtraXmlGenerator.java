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
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InProceedingsCollection;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class ProceedingsExtraXmlGenerator implements ExtraXMLGenerator {

    private boolean listMode = false;

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof Proceedings)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    Proceedings.class.getName()));
        }

        final Proceedings proceedings = (Proceedings) item;
        createOrganizerXml(proceedings, element, state);
        if (!listMode) {
            createPapersXml(proceedings, element, state);
        }
    }

    private void createOrganizerXml(final Proceedings proceedings,
                                    final Element parent,
                                    final PageState state) {
//        final GenericOrganizationalUnit organizer =
//                                        proceedings.getOrganizerOfConference(GlobalizationHelper.
//                getNegotiatedLocale().getLanguage());
//        if (organizer != null) {
//            final XmlGenerator generator = new XmlGenerator(organizer);
//            generator.setItemElemName("organizer", "");
//            generator.setListMode(true);
//            generator.generateXML(state, parent, "");
//        }
    }

    private void createPapersXml(final Proceedings proceedings,
                                 final Element parent,
                                 final PageState state) {
        final InProceedingsCollection papers = proceedings.getPapers();
        if ((papers == null) || papers.isEmpty()) {
            return;
        }

        final Element papersElem = parent.newChildElement("papers");
        while (papers.next()) {
            createPaperXml(papers.getPaper(GlobalizationHelper.
                    getNegotiatedLocale().getLanguage()),
                           papers.getPaperOrder(),
                           papersElem,
                           state);
        }
    }

    private void createPaperXml(final InProceedings paper,
                                final Integer order,
                                final Element papersElem,
                                final PageState state) {
        final XmlGenerator generator = new XmlGenerator(paper);
        generator.setItemElemName("paper", "");
        generator.addItemAttribute("order", order.toString());
        generator.setListMode(true);
        generator.generateXML(state, papersElem, "");
    }

    public void addGlobalStateParams(final Page page) {
        //nothing
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
