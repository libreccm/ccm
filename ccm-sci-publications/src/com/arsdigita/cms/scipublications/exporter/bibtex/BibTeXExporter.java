package com.arsdigita.cms.scipublications.exporter.bibtex;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.PublicationFormat;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporter;
import com.arsdigita.cms.scipublications.exporter.bibtex.converters.BibTeXConverters;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class BibTeXExporter implements SciPublicationsExporter {

    private final static Logger logger = Logger.getLogger(BibTeXExporter.class);

    public PublicationFormat getSupportedFormat() {
        try {
            return new PublicationFormat("BibTeX",
                                         new MimeType("text", "x-bibtex"),
                                         "bib");
        } catch (MimeTypeParseException ex) {
            logger.warn("Failed to create MimeType for PublicationFormat."
                        + "Using null mimetype instead. Cause: ", ex);
            return new PublicationFormat("BibTeX",
                                         null,
                                         "bib");

        }
    }

    public String exportPublication(final Publication publication) {       
        return BibTeXConverters.getInstance().convert(publication);     
    }
}
