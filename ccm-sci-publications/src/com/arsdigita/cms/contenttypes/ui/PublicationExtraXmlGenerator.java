package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ExtraXMLGenerator;
import com.arsdigita.cms.contenttypes.AuthorshipCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationGenericOrganizationalsUnitCollection;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.contenttypes.SeriesCollection;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.cms.scipublications.exporter.PublicationFormat;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporters;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;
import java.util.List;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationExtraXmlGenerator implements ExtraXMLGenerator {

    private boolean listMode;

    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
        if (!(item instanceof Publication)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    Publication.class.getName()));
        }

        final Publication publication = (Publication) item;
        createAuthorsXml(publication, element, state);
        createOrgaUnitsXml(publication, element, state);
        createSeriesXml(publication, element, state);

        final List<PublicationFormat> formats = SciPublicationsExporters.
                getInstance().getSupportedFormats();

        if (!listMode) {
            for (PublicationFormat format : formats) {
                createExportLink(format, element, (Publication) item, state);
            }
        }
    }

    private void createAuthorsXml(final Publication publication,
                                  final Element parent,
                                  final PageState state) {
        final AuthorshipCollection authors = publication.getAuthors();
        if ((authors == null) || authors.isEmpty()) {
            return;
        }

        final Element authorsElem = parent.newChildElement("authors");
        while (authors.next()) {
            createAuthorXml(authors.getAuthor(),
                            authors.isEditor(),
                            authors.getAuthorshipOrder(),
                            authorsElem,
                            state);
        }
    }

    private void createAuthorXml(final GenericPerson author,
                                 final Boolean isAuthor,
                                 final Integer order,
                                 final Element authorsElem,
                                 final PageState state) {
        final XmlGenerator generator = new XmlGenerator(author);
        generator.setItemElemName("author", "");
        generator.addItemAttribute("isEditor", isAuthor.toString());
        generator.addItemAttribute("order", order.toString());
        generator.setListMode(listMode);
        generator.generateXML(state, authorsElem, "");
    }

    private void createOrgaUnitsXml(final Publication publication,
                                    final Element parent,
                                    final PageState state) {
        final PublicationGenericOrganizationalsUnitCollection orgaunits =
                                                              publication.
                getOrganizationalUnits();
        if ((orgaunits == null) || orgaunits.isEmpty()) {
            return;
        }

        final Element orgaunitsElem = parent.newChildElement(
                "organizationalunits");
        while (orgaunits.next()) {
            createOrgaUnitXml(orgaunits.getOrganizationalUnit(GlobalizationHelper.
                    getNegotiatedLocale().getLanguage()),
                              orgaunitsElem,
                              state);
        }
    }

    private void createOrgaUnitXml(final GenericOrganizationalUnit orgaunit,
                                   final Element orgaunitsElem,
                                   final PageState state) {
        final XmlGenerator generator = new XmlGenerator(orgaunit);
        generator.setItemElemName("organizationalunit", "");
        generator.generateXML(state, orgaunitsElem, "");
    }

    private void createSeriesXml(final Publication publication,
                                 final Element parent,
                                 final PageState state) {
        final SeriesCollection series = publication.getSeries();
        if ((series == null) || series.isEmpty()) {
            return;
        }

        final Element seriesElem = parent.newChildElement("series");
        while (series.next()) {
            createSeriesElemXml(series.getSeries(GlobalizationHelper.
                    getNegotiatedLocale().getLanguage()),
                                seriesElem,
                                state);
        }
    }

    private void createSeriesElemXml(final Series series,
                                     final Element seriesElem,
                                     final PageState state) {
        final XmlGenerator generator = new XmlGenerator(series);
        generator.setItemElemName("series", "");
        generator.generateXML(state, seriesElem, "");
    }

    private void createExportLink(final PublicationFormat format,
                                  final Element parent,
                                  final Publication publication,
                                  final PageState state) {
        final Element exportLinkElem = parent.newChildElement(
                "publicationExportLink");
        final Element formatKeyElem =
                      exportLinkElem.newChildElement("formatKey");
        formatKeyElem.setText(format.getName().toLowerCase());
        final Element formatNameElem = exportLinkElem.newChildElement(
                "formatName");
        formatNameElem.setText(format.getName());
        final Element publicationIdElem = exportLinkElem.newChildElement(
                "publicationId");
        publicationIdElem.setText(publication.getID().toString());
    }

    public void addGlobalStateParams(final Page page) {
        //Nothing for now
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
