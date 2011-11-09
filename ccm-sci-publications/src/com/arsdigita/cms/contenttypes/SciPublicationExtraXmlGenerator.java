package com.arsdigita.cms.contenttypes;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.scipublications.exporter.PublicationFormat;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporters;
import com.arsdigita.xml.Element;
import java.util.List;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class SciPublicationExtraXmlGenerator implements ExtraXMLGenerator {

    public void generateXML(final ContentItem item, 
                            final Element element, 
                            final PageState state) {
        if (!(item instanceof Publication)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(), 
                    Publication.class.getName()));
        }
        
        List<PublicationFormat> formats = SciPublicationsExporters.getInstance().getSupportedFormats();
        
        for(PublicationFormat format : formats) {
            createExportLink(format, element, (Publication) item, state);
        }
    }

    private void createExportLink(final PublicationFormat format,
                                  final Element parent,
                                  final Publication publication,
                                  final PageState state) {
        final Element exportLinkElem = parent.newChildElement("publicationExportLink");
        final Element formatKeyElem = exportLinkElem.newChildElement("formatKey");
        formatKeyElem.setText(format.getName().toLowerCase());
        final Element formatNameElem = exportLinkElem.newChildElement("formatName");
        formatNameElem.setText(format.getName());
        final Element publicationIdElem = exportLinkElem.newChildElement("publicationId");
        publicationIdElem.setText(publication.getID().toString());
    }
    
    public void addGlobalStateParams(final Page page) {
        //Nothing for now
    }
    
    
    
}
