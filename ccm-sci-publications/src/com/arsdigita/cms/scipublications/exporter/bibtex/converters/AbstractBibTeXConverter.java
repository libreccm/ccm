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
package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.AuthorshipCollection;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.SeriesCollection;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilders;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXField;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;

/**
 * An abstract implementation of the {@link BibTeXConverter} providing common
 * functionality for all converters. To create the BibTeX data, implementations
 * of the {@link BibTeXBuilder} interface are used.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public abstract class AbstractBibTeXConverter implements BibTeXConverter {

    private BibTeXBuilder bibTeXBuilder;

    /**
     * Convert the authors of a publication to BibTeX.
     *
     * @param publication The publication to use.
     */
    protected void convertAuthors(final Publication publication) {
        AuthorshipCollection authors;

        authors = publication.getAuthors();
        if (authors != null) {
            while (authors.next()) {
                if (authors.isEditor()) {
                    getBibTeXBuilder().addEditor(authors.getAuthor());
                } else {
                    getBibTeXBuilder().addAuthor(authors.getAuthor());
                }
            }
        }
    }

    /**
     * Convert the title of a publication to BibTeX.
     *
     * @param publication The publication to use.
     * @throws UnsupportedFieldException If the title field is not supported by
     * the BibTeX type.
     */
    protected void convertTitle(final Publication publication)
            throws UnsupportedFieldException {
        getBibTeXBuilder().setField(BibTeXField.TITLE, publication.getTitle());
    }

    /**
     * Converts the year of publication to BibTeX.
     *
     * @param publication
     * @throws UnsupportedFieldException
     */
    protected void convertYear(final Publication publication)
            throws UnsupportedFieldException {
        if (publication.getYearOfPublication() != null) {
            getBibTeXBuilder().setField(BibTeXField.YEAR,
                                        publication.getYearOfPublication().
                    toString());
        }
    }

    /**
     * Converts the publisher to BibTeX.
     *
     * @param publication
     * @throws UnsupportedFieldException
     */
    protected void convertPublisher(final PublicationWithPublisher publication)
            throws UnsupportedFieldException {
        if (publication.getPublisher() != null) {
            if ((publication.getPublisher().getPlace() == null)
                || publication.getPublisher().getPlace().isEmpty()) {
                getBibTeXBuilder().setField(BibTeXField.PUBLISHER,
                                            publication.getPublisher().getTitle());
            } else {
                getBibTeXBuilder().setField(BibTeXField.PUBLISHER,
                                            String.format("%s, %s",
                                                          publication.
                        getPublisher().
                        getTitle(),
                                                          publication.
                        getPublisher().
                        getPlace()));
            }
        }
    }

    /**
     * Convert the ISBN to BibTeX.
     *
     * @param publication
     * @throws UnsupportedFieldException
     */
    protected void convertISBN(final PublicationWithPublisher publication)
            throws UnsupportedFieldException {
        if (publication.getISBN() != null) {
            getBibTeXBuilder().setField(BibTeXField.ISBN,
                                        publication.getISBN());
        }
    }

    /**
     * Convert the volume to BibTeX.
     *
     * @param publication
     * @throws UnsupportedFieldException
     */
    protected void convertVolume(final PublicationWithPublisher publication)
            throws UnsupportedFieldException {
        if (publication.getVolume() != null) {
            getBibTeXBuilder().setField(BibTeXField.VOLUME,
                                        publication.getVolume().toString());
        }
    }

    /**
     * Convert the edition to BibTeX.
     *
     * @param publication
     * @throws UnsupportedFieldException
     */
    protected void convertEdition(final PublicationWithPublisher publication)
            throws UnsupportedFieldException {
        if (publication.getEdition() != null) {
            getBibTeXBuilder().setField(BibTeXField.EDITION,
                                        publication.getEdition());
        }
    }

    /**
     * Convert the series to BibTeX.
     *
     * @param publication
     * @throws UnsupportedFieldException
     */
    protected void convertSeries(final Publication publication)
            throws UnsupportedFieldException {
        SeriesCollection seriesColl = publication.getSeries();
        if ((seriesColl != null) && (seriesColl.size() > 0)) {

            seriesColl.next();

            getBibTeXBuilder().setField(BibTeXField.SERIES,
                             seriesColl.getSeries().getTitle());

            seriesColl.close();
        }
    }

    /**
     * Retrieves to BibTeX builder for the converter. Uses the implementation
     * of {@link #getBibTeXType()} for get the BibTeX type.
     *
     * @return The BibTeX builder for the converter.
     */
    protected BibTeXBuilder getBibTeXBuilder() {
        if (bibTeXBuilder == null) {
            bibTeXBuilder = BibTeXBuilders.getInstance().
                    getBibTeXBuilderForType(getBibTeXType());
        }

        return bibTeXBuilder;
    }

    /**
     *
     * @return The BibTeX type to use for building the BibTeX data.
     */
    protected abstract String getBibTeXType();
}
