package com.arsdigita.cms.scipublications.importer.bibtex;

import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImportException;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImporter;
import com.arsdigita.cms.scipublications.importer.report.ImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.io.StringReader;
import java.util.Map;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.log4j.Logger;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.Key;

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
        
        final StringReader reader = new StringReader(publications);        
        final BibTeXParser parser = new BibTeXParser(reader);
        final BibTeXDatabase database = parser.getDatabase();
        final Map<Key, BibTeXEntry> entries = database.getEntries();
        
        for(BibTeXEntry entry : entries.values()) {
            report.addPublication(converters.convert(entry, importerUtil, pretend, publishNewItems));
        }
                
        return report;
    }

}
