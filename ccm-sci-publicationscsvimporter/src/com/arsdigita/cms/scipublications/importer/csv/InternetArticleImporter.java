package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.InternetArticleBundle;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class InternetArticleImporter extends AbstractPublicationImporter<InternetArticle> {

    public InternetArticleImporter(final CsvLine data, final PublicationImportReport report, final boolean pretend) {
        super(data, report, pretend);
    }

    @Override
    public InternetArticle importPublication() {
        final InternetArticle article = super.importPublication();
        final CsvLine data = getData();
        final PublicationImportReport report = getReport();

        if ((data.getPlace() != null) && !data.getPlace().isEmpty()) {
            if (!isPretend()) {
                article.setPlace(data.getPlace());
            }
            report.addField(new FieldImportReport("Place", data.getPlace()));
        }

        processNumberOfPages(article);

        if ((data.getEdition() != null) && !data.getEdition().isEmpty()) {
            if (!isPretend()) {
                article.setEdition(data.getEdition());
            }
            report.addField(new FieldImportReport("Edition", data.getEdition()));
        }

        if ((data.getIssn() != null) && !data.getIssn().isEmpty()) {
            if (!isPretend()) {
                article.setISSN(data.getIssn());
            }
            report.addField(new FieldImportReport("ISSN", data.getIssn()));
        }

        processLastAccessed(article);
        processPublicationDate(article);

        if ((data.getUrl() != null) && !data.getUrl().isEmpty()) {
            if (!isPretend()) {
                article.setUrl(data.getUrl());
            }
            report.addField(new FieldImportReport("URL", data.getUrl()));
        }

        if ((data.getUrn() != null) && !data.getUrn().isEmpty()) {
            if (!isPretend()) {
                article.setUrn(data.getUrn());
            }
            report.addField(new FieldImportReport("URN", data.getUrn()));
        }

        if ((data.getDoi() != null) && !data.getDoi().isEmpty()) {
            if (!isPretend()) {
                article.setDoi(data.getDoi());
            }
            report.addField(new FieldImportReport("DOI", data.getDoi()));
        }

        return article;
    }

    private void processNumberOfPages(final InternetArticle publication) {
        if ((getData().getNumberOfPages() != null) && !getData().getNumberOfPages().isEmpty()) {
            try {
                final int volume = Integer.parseInt(getData().getNumberOfPages());
                if (!isPretend()) {
                    publication.setNumberOfPages(volume);
                }
                getReport().addField(new FieldImportReport("Number of pages", getData().getNumberOfPages()));
            } catch (NumberFormatException ex) {
                getReport().addMessage(String.format("Failed to parse numberOfPages data in line %d.",
                                                     getData().getLineNumber()));
            }
        }
    }

    private void processLastAccessed(final InternetArticle article) {
        if ((getData().getLastAccess() != null) && !getData().getLastAccess().isEmpty()) {
            final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            try {
                final Date date = dateFormat.parse(getData().getLastAccess());
                if (!isPretend()) {
                    article.setLastAccessed(date);
                }
                getReport().addField(new FieldImportReport("Last access", getData().getLastAccess()));
            } catch (java.text.ParseException ex) {
                getReport().addMessage(String.format("Failed to parse date of last access in line %d.",
                                                     getData().getLineNumber()));
            }
        }
    }

    private void processPublicationDate(final InternetArticle article) {
        if ((getData().getPublicationDate() != null) && !getData().getPublicationDate().isEmpty()) {
            final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            try {
                final Date date = dateFormat.parse(getData().getPublicationDate());
                if (!isPretend()) {
                    article.setPublicationDate(date);
                }
                getReport().addField(new FieldImportReport("Publication date", getData().getPublicationDate()));
            } catch (java.text.ParseException ex) {
                getReport().addMessage(String.format("Failed to parse publication date in line %d.",
                                                     getData().getLineNumber()));
            }
        }
    }

    @Override
    protected InternetArticle createPublication() {
        if (isPretend()) {
            return null;
        } else {
            return new InternetArticle();
        }
    }

    @Override
    protected PublicationBundle createBundle(final InternetArticle article) {
        if (isPretend()) {
            return null;
        } else {
            return new InternetArticleBundle(article);
        }
    }

}
