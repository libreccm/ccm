/*
 * Copyright (c) 2012 Jens Pelzetter, ScientificCMS.org team
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
package com.arsdigita.cms.scipublications.importer.csv;

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.Review;
import com.arsdigita.cms.contenttypes.WorkingPaper;
import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImportException;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImporter;
import com.arsdigita.cms.scipublications.importer.report.FieldImportReport;
import com.arsdigita.cms.scipublications.importer.report.ImportReport;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import java.util.Arrays;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.log4j.Logger;

public class PublicationsImporter implements SciPublicationsImporter {

    private static final Logger LOGGER = Logger.getLogger(PublicationsImporter.class);
    private static final String LINE_SEP = "\n";
    private static final String COL_SEP = "\t";
    private static final CsvImporterConfig CONFIG = new CsvImporterConfig();
//    private static final String AUTHORS_SEP = ";";
//    private static final String AUTHOR_NAME_SEP = ",";
//    private static final String DR_TITLE = "Dr.";
//    private static final String PROF_DR = "Prof. Dr.";

    static {
        CONFIG.load();
    }

    public static CsvImporterConfig getConfig() {
        return CONFIG;
    }

    public PublicationFormat getSupportedFormat() {
        try {
            return new PublicationFormat("CSV",
                                         new MimeType("text", "csv"),
                                         "csv");
        } catch (MimeTypeParseException ex) {
            LOGGER.warn("Failed to create MimeType for PublicationFormat."
                        + "Using null mimetype instead. Cause: ", ex);
            return new PublicationFormat("CSV",
                                         null,
                                         "csv");

        }
    }

    @Override
    public ImportReport importPublications(final String publications,
                                           final boolean pretend,
                                           final boolean publishNewItems)
            throws SciPublicationsImportException {
        final String[] linesWithHeader = publications.split(LINE_SEP);
        final String[] lines = Arrays.copyOfRange(linesWithHeader, 1, linesWithHeader.length);
        final ImportReport report = new ImportReport();

        report.setImporter("CSV Importer");
        report.setPretend(pretend);

        final Session session = SessionManager.getSession();
        final TransactionContext tctx = session.getTransactionContext();
        tctx.beginTxn();

        try {
            int lineNumber = 2; //Because we are starting at line 2 of the CSV file (line 1 contains the column headers)
            for (String line : lines) {
                final PublicationImportReport result = importPublication(line, lineNumber, publishNewItems);
                report.addPublication(result);
                lineNumber++;
            }
        } catch (Exception ex) {
            tctx.abortTxn();             
            throw new SciPublicationsImportException(ex);
        }

        if (pretend) {
            tctx.abortTxn();
        } else {
            tctx.commitTxn();
        }

        return report;
    }

    private PublicationImportReport importPublication(final String line,
                                                      final int lineNumber,
                                                      final boolean publishNewItems) {
        final PublicationImportReport report = new PublicationImportReport();

        final String[] cols = line.split(COL_SEP, -30);
        //Check number of cols    
        System.err.println("Checking number of cols...");
        if (cols.length != 30) {
            report.setSuccessful(false);
            report.addMessage(String.format("!!! Wrong number of columns. Exepcted 30 columns but found %d columns. "
                                            + "Skiping line %d!\n", cols.length, lineNumber));
            return report;
        }
        System.err.println("Checked number of cols...");

        System.err.println("Creating csv object...");
        final CsvLine data = new CsvLine(cols, lineNumber);

        System.err.println("Calling importer...");
        if (ArticleInCollectedVolume.class.getSimpleName().equals(data.getType())) {
            processArticleInCollectedVolume(publishNewItems, data, report);
        } else if (ArticleInCollectedVolume.class.getSimpleName().equals(data.getType())) {
            processArticleInJournal(publishNewItems, data, report);
        } else if (CollectedVolume.class.getSimpleName().equals(data.getType())) {
            processCollectedVolume(publishNewItems, data, report);
        } else if (Expertise.class.getSimpleName().equals(data.getType())) {
            processExpertise(publishNewItems, data, report);
        } else if (GreyLiterature.class.getSimpleName().equals(data.getType())) {
            processGreyLiterature(publishNewItems, data, report);
        } else if (InProceedings.class.getSimpleName().equals(data.getType())) {
            processInProceedings(publishNewItems, data, report);
        } else if (InternetArticle.class.getSimpleName().equals(data.getType())) {
            processInternetArticle(publishNewItems, data, report);
        } else if (Monograph.class.getSimpleName().equals(data.getType())) {
            System.err.println("CAlling monograph importer...");
            processMonograph(publishNewItems, data, report);
        } else if (Proceedings.class.getSimpleName().equals(data.getType())) {
            processProceedings(publishNewItems, data, report);
        } else if (Review.class.getSimpleName().equals(data.getType())) {
            processReview(publishNewItems, data, report);
        } else if (WorkingPaper.class.getSimpleName().equals(data.getType())) {
            processWorkingPaper(publishNewItems, data, report);
        }

        return report;
    }

    private void processArticleInCollectedVolume(final boolean publishNewItems,
                                                 final CsvLine data,
                                                 final PublicationImportReport report) {
        if (isPublicationAlreadyInDatabase(data, ArticleInCollectedVolume.class.getSimpleName(), report)) {
            return;
        }

        final ArticleInCollectedVolumeImporter importer = new ArticleInCollectedVolumeImporter(data, report);
        importer.doImport(publishNewItems);
    }

    private void processArticleInJournal(final boolean publishNewItems,
                                         final CsvLine data,
                                         final PublicationImportReport report) {
        if (isPublicationAlreadyInDatabase(data, ArticleInJournal.class.getSimpleName(), report)) {
            return;
        }

        final ArticleInJournalImporter importer = new ArticleInJournalImporter(data, report);
        importer.doImport(publishNewItems);
    }

    private void processCollectedVolume(final boolean publishNewItems,
                                        final CsvLine data,
                                        final PublicationImportReport report) {
        if (isPublicationAlreadyInDatabase(data, CollectedVolume.class.getSimpleName(), report)) {
            return;
        }

        final CollectedVolumeImporter importer = new CollectedVolumeImporter(data, report);
        importer.doImport(publishNewItems);
    }

    private void processExpertise(final boolean publishNewItems, final CsvLine data,
                                  final PublicationImportReport report) {
        if (isPublicationAlreadyInDatabase(data, Expertise.class.getSimpleName(), report)) {
            return;
        }

        final ExpertiseImporter importer = new ExpertiseImporter(data, report);
        importer.doImport(publishNewItems);
    }

    private void processGreyLiterature(final boolean publishNewItems,
                                       final CsvLine data,
                                       final PublicationImportReport report) {
        if (isPublicationAlreadyInDatabase(data, GreyLiterature.class.getSimpleName(), report)) {
            return;
        }

        final GreyLiteratureImporter importer = new GreyLiteratureImporter(data, report);
        importer.doImport(publishNewItems);
    }

    private void processInProceedings(final boolean publishNewItems,
                                      final CsvLine data,
                                      final PublicationImportReport report) {
        if (isPublicationAlreadyInDatabase(data, InProceedings.class.getSimpleName(), report)) {
            return;
        }

        final InProceedingsImporter importer = new InProceedingsImporter(data, report);
        importer.doImport(publishNewItems);
    }

    private void processInternetArticle(final boolean publishNewItems,
                                        final CsvLine data,
                                        final PublicationImportReport report) {
        if (isPublicationAlreadyInDatabase(data, InternetArticle.class.getSimpleName(), report)) {
            return;
        }

        final InternetArticleImporter importer = new InternetArticleImporter(data, report);
        importer.doImport(publishNewItems);
    }

    private void processMonograph(final boolean publishNewItems,
                                  final CsvLine data,
                                  final PublicationImportReport report) {
        if (isPublicationAlreadyInDatabase(data, Monograph.class.getSimpleName(), report)) {
            return;
        }

        final MonographImporter importer = new MonographImporter(data, report);
        importer.doImport(publishNewItems);
    }

    private void processProceedings(final boolean publishNewItems,
                                    final CsvLine data,
                                    final PublicationImportReport report) {
        if (isPublicationAlreadyInDatabase(data, Proceedings.class.getSimpleName(), report)) {
            return;
        }
        System.err.println("Publication is not in database");

        final ProceedingsImporter importer = new ProceedingsImporter(data, report);
        importer.doImport(publishNewItems);
    }

    private void processReview(final boolean publishNewItems,
                               final CsvLine data,
                               final PublicationImportReport report) {
        if (isPublicationAlreadyInDatabase(data, Review.class.getSimpleName(), report)) {
            return;
        }

        final ReviewImporter importer = new ReviewImporter(data, report);
        importer.doImport(publishNewItems);

    }

    private void processWorkingPaper(final boolean publishNewItems,
                                     final CsvLine data,
                                     final PublicationImportReport report) {
        if (isPublicationAlreadyInDatabase(data, WorkingPaper.class.getSimpleName(), report)) {
            return;
        }

        final WorkingPaperImporter importer = new WorkingPaperImporter(data, report);
        importer.doImport(publishNewItems);
    }

//    private List<AuthorData> parseAuthors(final boolean publishNewItems, final String authorsStr, final PublicationImportReport report) {
//        final List<AuthorData> authors = new ArrayList<AuthorData>();
//
//        final String[] tokens = authorsStr.split(AUTHORS_SEP);
//        reportWriter.printf("Found %d authors...\n", tokens.length);
//
//        for (String token : tokens) {
//            parseAuthor(token, authors, reportWriter);
//        }
//
//        return authors;
//    }
    private boolean isPublicationAlreadyInDatabase(final CsvLine data,
                                                   final String type,
                                                   final PublicationImportReport report) {
        final String title = data.getTitle();
        final String year = data.getYear();

        int yearOfPublication;
        try {
            yearOfPublication = Integer.parseInt(year);
        } catch (NumberFormatException ex) {
            yearOfPublication = 0;
        }

        final Session session = SessionManager.getSession();
        final DataCollection collection = session.retrieve(Publication.BASE_DATA_OBJECT_TYPE);
        final FilterFactory filterFactory = collection.getFilterFactory();
        final Filter titleFilter = filterFactory.equals("title", title);
        final Filter yearFilter = filterFactory.equals("yearOfPublication", yearOfPublication);
        collection.addFilter(titleFilter);
        collection.addFilter(yearFilter);

        final boolean result = !collection.isEmpty();
        collection.close();

        report.setTitle(title);
        report.setType(type);
        report.addField(new FieldImportReport("Year of publication", year));
        report.setAlreadyInDatabase(result);

        return result;
    }

//    private int parseYear(final CsvLine data, final PublicationImportReport report) {
//        int year = 0;
//        try {
//            year = Integer.parseInt(data.getYear());
//        } catch (NumberFormatException ex) {
//            report.addMessage(String.format("Can't parse year of publication into an integer in CSV line %d\n",
//                                            data.getLineNumber()));
//        }
//
//        return year;
//    }
//    private void parseAuthor(final String authorToken, final List<AuthorData> authors,
//                             final PublicationImportReport report) {
//        final String[] nameTokens = authorToken.split(AUTHOR_NAME_SEP);
//
//        final AuthorImportReport authorImportReport = new AuthorImportReport();
//
//        if (nameTokens.length == 1) {
//            final AuthorData author = new AuthorData();
//            author.setSurname(nameTokens[0]);
//            authorImportReport.setSurname(nameTokens[0]);
//            authors.add(author);
//        } else if (nameTokens.length == 2) {
//            final AuthorData author = new AuthorData();
//            author.setSurname(nameTokens[0]);
//            author.setGivenName(nameTokens[1]);
//            authorImportReport.setSurname(nameTokens[0]);
//            authorImportReport.setGivenName(nameTokens[1]);
//
//            authors.add(author);
//        } else {
//            final AuthorData author = new AuthorData();
//            author.setSurname(nameTokens[0]);
//            author.setGivenName(nameTokens[1]);
//            authorImportReport.setSurname(nameTokens[0]);
//            authorImportReport.setGivenName(nameTokens[1]);
//            authors.add(author);
//        }
//    }
//    private class AuthorData {
//
//        private String surname;
//        private String givenName;
//        private boolean editor;
//
//        public AuthorData() {
//            //Nothing
//        }
//
//        public String getSurname() {
//            if (surname == null) {
//                return "";
//            } else {
//                return surname.trim();
//            }
//        }
//
//        public void setSurname(final String surname) {
//            if (surname.startsWith(DR_TITLE)) {
//                this.surname = surname.substring(DR_TITLE.length());
//            } else if (surname.startsWith(PROF_DR)) {
//                this.surname = surname.substring(PROF_DR.length());
//            } else {
//                this.surname = surname;
//            }
//        }
//
//        public String getGivenName() {
//            if (givenName == null) {
//                return "";
//            } else {
//                return givenName.trim();
//            }
//        }
//
//        public void setGivenName(final String givenName) {
//            this.givenName = givenName;
//        }
//
//        public boolean isEditor() {
//            return editor;
//        }
//
//        public void setEditor(final boolean editor) {
//            this.editor = editor;
//        }
//
//    }
//
//    private String normalizeString(final String str) {
//        if (str == null) {
//            return "null";
//        }
//        return str.replace("ä", "ae").replace("ö", "oe").replace("ü", "ue").
//                replace(
//                "Ä", "Ae").replace("Ü", "Ue").replace("Ö", "Oe").replace("ß",
//                                                                         "ss").
//                replace(" ", "-").
//                replaceAll("[^a-zA-Z0-9\\-]", "").toLowerCase().trim();
//    }
}