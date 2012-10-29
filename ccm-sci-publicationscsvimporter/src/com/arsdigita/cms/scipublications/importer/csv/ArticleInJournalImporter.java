package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.ArticleInJournalBundle;
import com.arsdigita.cms.contenttypes.PublicationBundle;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
class ArticleInJournalImporter extends AbstractPublicationImporter<ArticleInJournal> {

    protected ArticleInJournalImporter(final CsvLine data, final PublicationImportReport report) {
        super(data, report);
    }

    @Override
    public ArticleInJournal importPublication() {
        final ArticleInJournal article = super.importPublication();
        final CsvLine data = getData();
        final PublicationImportReport report = getReport();
        final ImporterUtil importerUtil = getImporterUtil();

        processVolume(article);
        if ((data.getIssue() != null) && !data.getIssue().isEmpty()) {
            article.setIssue(data.getIssue());
            report.addField(new FieldImportReport("Issue", data.getIssue()));
        }
        processPagesFrom(article);
        processPagesTo(article);
        processPublicationDate(article);

        if ((data.getJournal() != null) && !data.getJournal().isEmpty()) {
            importerUtil.processJournal(article, data.getJournal());
        }


        return article;
    }

    private void processVolume(final ArticleInJournal article) {
        if ((getData().getVolume() != null) && !getData().getVolume().isEmpty()) {
            try {
                final int volume = Integer.parseInt(getData().getVolume());
                article.setVolume(volume);
                getReport().addField(new FieldImportReport("Volume", getData().getVolume()));
            } catch (NumberFormatException ex) {
                getReport().addMessage(String.format("Failed to parse volume data in line '%d'.",
                                                     getData().getLineNumber()));
            }
        }
    }

    private void processPagesFrom(final ArticleInJournal publication) {
        if ((getData().getPageFrom() != null) && !getData().getPageFrom().isEmpty()) {
            try {
                final int pagesFrom = Integer.parseInt(getData().getPageFrom());
                publication.setPagesFrom(pagesFrom);
                getReport().addField(new FieldImportReport("Pages from", getData().getPageFrom()));
            } catch (NumberFormatException ex) {
                getReport().addMessage(String.format("Failed to parse pageFrom data in line '%d'.",
                                                     getData().getLineNumber()));
            }
        }
    }

    private void processPagesTo(final ArticleInJournal publication) {
        try {
            final int pagesTo = Integer.parseInt(getData().getPageTo());
            publication.setPagesFrom(pagesTo);
            getReport().addField(new FieldImportReport("Pages to", getData().getPageFrom()));
        } catch (NumberFormatException ex) {
            getReport().addMessage(String.format("Failed to parse pageTo data in line '%d'.",
                                                 getData().getLineNumber()));
        }
    }

    private void processPublicationDate(final ArticleInJournal article) {
        if ((getData().getPublicationDate() != null) && !getData().getPublicationDate().isEmpty()) {
            final DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
            try {
                final Date date = dateFormat.parse(getData().getPublicationDate());
                article.setPublicationDate(date);
                getReport().addField(new FieldImportReport("Publication date", getData().getPublicationDate()));
            } catch (java.text.ParseException ex) {
                getReport().addMessage(String.format("Failed to parse publication date in line %d.",
                                                     getData().getLineNumber()));
            }
        }
    }

    @Override
    protected ArticleInJournal createPublication() {
        return new ArticleInJournal();
    }

    @Override
    protected PublicationBundle createBundle(final ArticleInJournal article) {
        return new ArticleInJournalBundle(article);
    }

}
