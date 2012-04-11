package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InProceedingsCollection;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class ProceedingsExtraXmlGenerator implements ExtraXMLGenerator {

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
        createPapersXml(proceedings, element, state);
    }

    private void createOrganizerXml(final Proceedings proceedings,
                                    final Element parent,
                                    final PageState state) {
        final GenericOrganizationalUnit organizer =
                                        proceedings.getOrganizerOfConference(GlobalizationHelper.
                getNegotiatedLocale().getLanguage());
        if (organizer != null) {
            final Element organizerElem = parent.newChildElement("organizer");
            final XmlGenerator generator = new XmlGenerator(organizer);
            generator.generateXML(state, organizerElem, "");
        }
    }

    private void createPapersXml(final Proceedings proceedings,
                                 final Element parent,
                                 final PageState state) {
        final InProceedingsCollection papers = proceedings.getPapers();
        if ((papers == null) || papers.isEmpty()) {
            return;
        }
        
        final Element papersElem = parent.newChildElement("papers");
        while(papers.next()) {
            createPaperXml(papers.getPaper(GlobalizationHelper.getNegotiatedLocale().getLanguage()),
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
        generator.addItemAttribute("order", order.toString());
        generator.generateXML(state, papersElem, "");
    }
    
    public void addGlobalStateParams(final Page page) {
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
