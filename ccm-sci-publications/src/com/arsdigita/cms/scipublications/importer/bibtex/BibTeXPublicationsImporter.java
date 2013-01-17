package com.arsdigita.cms.scipublications.importer.bibtex;

import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImportException;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImporter;
import com.arsdigita.cms.scipublications.importer.report.ImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Map;
import java.util.logging.Level;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.log4j.Logger;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.Key;
import org.jbibtex.ParseException;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class BibTeXPublicationsImporter implements SciPublicationsImporter {

    private static final Logger LOGGER = Logger.getLogger(BibTeXPublicationsImporter.class);

    public PublicationFormat getSupportedFormat() {
        try {
            return new PublicationFormat("BibTeX", new MimeType("application/x-bibtex"), "bib");
        } catch (MimeTypeParseException ex) {
            LOGGER.warn("Failed to create MimeType for PublicationFormat."
                        + "Using null mimetype instead. Cause: ", ex);
            return new PublicationFormat("BibTeX", null, "bib");
        }
    }

    public ImportReport importPublications(final String publications,
                                           final Map<String, String> importerParams,
                                           final boolean pretend,
                                           final boolean publishNewItems)
            throws SciPublicationsImportException {

        final ImportReport report = new ImportReport();
        final ImporterUtil importerUtil = new ImporterUtil(publishNewItems);
        final BibTeXConverters converters = BibTeXConverters.getInstance();

        report.setImporter(BibTeXPublicationsImporter.class.getName());
        report.setPretend(pretend);
        
        final StringReader reader = new StringReader(publications);
        final BibTeXParser parser = new BibTeXParser();
        final BibTeXDatabase database;
        try {
            database = parser.parse(reader);
        } catch (IOException ex) {
            final PrintWriter writer = new PrintWriter(System.err);
            writer.print("Failed to parse BibTeX file.");
            ex.printStackTrace(writer);

            return report;
        } catch (ParseException ex) {
            final PrintWriter writer = new PrintWriter(System.err);
            writer.print("Failed to parse BibTeX file.");
            ex.printStackTrace(writer);

            return report;
        }
        final Map<Key, BibTeXEntry> entries = database.getEntries();

        for (BibTeXEntry entry : entries.values()) {
            report.addPublication(converters.convert(entry, importerUtil, pretend, publishNewItems));
        }

        return report;
    }

}
