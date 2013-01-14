package com.arsdigita.cms.scipublications.importer.bibtex;

import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImportException;
import com.arsdigita.cms.scipublications.importer.SciPublicationsImporter;
import com.arsdigita.cms.scipublications.importer.report.ImportReport;
import java.util.Map;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.log4j.Logger;

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
        
        
        throw new UnsupportedOperationException();
    }

}
