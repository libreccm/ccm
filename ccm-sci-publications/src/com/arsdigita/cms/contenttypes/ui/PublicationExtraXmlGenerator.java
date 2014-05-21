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
import com.arsdigita.cms.contenttypes.AuthorshipCollection;
import com.arsdigita.cms.contenttypes.GenericOrganizationalUnit;
import com.arsdigita.cms.contenttypes.GenericPerson;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationGenericOrganizationalsUnitCollection;
import com.arsdigita.cms.contenttypes.Series;
import com.arsdigita.cms.contenttypes.SeriesCollection;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporters;
import com.arsdigita.globalization.GlobalizationHelper;
import com.arsdigita.xml.Element;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jens Pelzetter 
 * @version $Id$
 */
public class PublicationExtraXmlGenerator implements ExtraXMLGenerator {

    private final static List<ExtraXMLGenerator> EXTENDING_GENERATORS = new ArrayList<ExtraXMLGenerator>();
    private boolean listMode;

    public static void addExtentingGenerator(final ExtraXMLGenerator generator) {
        EXTENDING_GENERATORS.add(generator);
    }
    
    @Override
    public void generateXML(final ContentItem item,
                            final Element element,
                            final PageState state) {
//        final long start = System.nanoTime();
        if (!(item instanceof Publication)) {
            throw new IllegalArgumentException(String.format(
                    "ExtraXMLGenerator '%s' only supports items of type '%s'.",
                    getClass().getName(),
                    Publication.class.getName()));
        }

        final Publication publication = (Publication) item;
        createAuthorsXml(publication, element, state);
//        System.out.printf("[%s] Created authors XML in %d ms\n",
//                          PublicationExtraXmlGenerator.class.getName(),
//                          (System.nanoTime() - start) / 1000000);
        createSeriesXml(publication, element, state);
//        System.out.printf("[%s] Created series XML in %d ms\n",
//                          PublicationExtraXmlGenerator.class.getName(),
//                          (System.nanoTime() - start) / 1000000);
        if (!listMode) {
            createOrgaUnitsXml(publication, element, state);

            final List<PublicationFormat> formats = SciPublicationsExporters.getInstance().getSupportedFormats();

            for (PublicationFormat format : formats) {
                createExportLink(format, element, (Publication) item, state);
            }
        }
        
        for (ExtraXMLGenerator extending : EXTENDING_GENERATORS) {
            extending.setListMode(listMode);
            extending.generateXML(item, element, state);
        }
//        System.out.printf("[%s] Created extra XML in %d ms\n",
//                          PublicationExtraXmlGenerator.class.getName(),
//                          (System.nanoTime() - start) / 1000000);
    }

    private void createAuthorsXml(final Publication publication,
                                  final Element parent,
                                  final PageState state) {
//        final long start = System.nanoTime();
        final AuthorshipCollection authors = publication.getAuthors();
        if ((authors == null) || authors.isEmpty()) {
            return;
        }
//        System.out.printf("[%s#createAuthorsXML] Got authors in %d ms\n",
//                          PublicationExtraXmlGenerator.class.getName(),
//                          (System.nanoTime() - start) / 1000000);

        final Element authorsElem = parent.newChildElement("authors");
        while (authors.next()) {
            createAuthorXml(authors.getAuthor(),
                            authors.isEditor(),
                            authors.getAuthorshipOrder(),
                            authorsElem,
                            state);
        }
//        System.out.printf("[%s#createAuthorsXML] Created XML for authors in %d ms\n",
//                          PublicationExtraXmlGenerator.class.getName(),
//                          (System.nanoTime() - start) / 1000000);
    }

    private void createAuthorXml(final GenericPerson author,
                                 final Boolean isAuthor,
                                 final Integer order,
                                 final Element authorsElem,
                                 final PageState state) {
//        final long start = System.nanoTime();
        final XmlGenerator generator = new XmlGenerator(author);
        generator.setItemElemName("author", "");
        generator.addItemAttribute("isEditor", isAuthor.toString());
        generator.addItemAttribute("order", order.toString());
        generator.setListMode(listMode);
        generator.generateXML(state, authorsElem, "");
//        System.out.printf("[%s] Created XML for author %s in %d ms\n",
//                          PublicationExtraXmlGenerator.class.getName(),
//                          author.getTitle(),
//                          (System.nanoTime() - start) / 1000000);
    }

    private void createOrgaUnitsXml(final Publication publication,
                                    final Element parent,
                                    final PageState state) {
        final PublicationGenericOrganizationalsUnitCollection orgaunits =
                                                              publication.getOrganizationalUnits();
        if ((orgaunits == null) || orgaunits.isEmpty()) {
            return;
        }

        final Element orgaunitsElem = parent.newChildElement(
                "organizationalunits");
        while (orgaunits.next()) {
            createOrgaUnitXml(orgaunits.getOrganizationalUnit(GlobalizationHelper.getNegotiatedLocale().getLanguage()),
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
            createSeriesElemXml(series.getSeries(GlobalizationHelper.getNegotiatedLocale().getLanguage()),
                                series.getVolumeOfSeries(),
                                seriesElem,
                                state);
        }
    }

    private void createSeriesElemXml(final Series series,
                                     final String volumeOfSeries,
                                     final Element seriesElem,
                                     final PageState state) {
        final XmlGenerator generator = new XmlGenerator(series);
        generator.setItemElemName("series", "");
        if (volumeOfSeries != null) {
            generator.addItemAttribute("volume", volumeOfSeries);
        }
        generator.setListMode(listMode);
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

    @Override
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
