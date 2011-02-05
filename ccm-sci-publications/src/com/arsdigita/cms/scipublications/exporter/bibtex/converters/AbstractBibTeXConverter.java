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
 *
 * @author jensp
 */
public abstract class AbstractBibTeXConverter implements BibTeXConverter {

    private BibTeXBuilder bibTeXBuilder;

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

    protected void convertTitle(final Publication publication)
            throws UnsupportedFieldException {
        getBibTeXBuilder().setField(BibTeXField.TITLE, publication.getTitle());
    }

    protected void convertYear(final Publication publication)
            throws UnsupportedFieldException {
        if (publication.getYearOfPublication() != null) {
            getBibTeXBuilder().setField(BibTeXField.YEAR,
                                        publication.getYearOfPublication().
                    toString());
        }
    }

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

    protected void convertISBN(final PublicationWithPublisher publication)
            throws UnsupportedFieldException {
        if (publication.getISBN() != null) {
            getBibTeXBuilder().setField(BibTeXField.ISBN,
                                        publication.getISBN());
        }
    }

    protected void convertVolume(final PublicationWithPublisher publication)
            throws UnsupportedFieldException {
        if (publication.getVolume() != null) {
            getBibTeXBuilder().setField(BibTeXField.VOLUME,
                                        publication.getVolume().toString());
        }
    }

    protected void convertEdition(final PublicationWithPublisher publication)
            throws UnsupportedFieldException {
        if (publication.getEdition() != null) {
            getBibTeXBuilder().setField(BibTeXField.EDITION,
                                        publication.getEdition());
        }
    }

    protected void convertSeries(final Publication publication)
            throws UnsupportedFieldException {
        SeriesCollection seriesColl =
                         publication.getSeries();
        if ((seriesColl != null) && (seriesColl.size() > 0)) {

            seriesColl.next();

            getBibTeXBuilder().setField(BibTeXField.SERIES,
                             seriesColl.getSeries().getTitle());

            seriesColl.close();
        }
    }

    protected BibTeXBuilder getBibTeXBuilder() {
        if (bibTeXBuilder == null) {
            bibTeXBuilder = BibTeXBuilders.getInstance().
                    getBibTeXBuilderForType(getBibTeXType());
        }

        return bibTeXBuilder;
    }

    protected abstract String getBibTeXType();
}
