package com.arsdigita.cms.scipublications.exporter.bibtex;

import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilders;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.Journal;
import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.contenttypes.Review;
import com.arsdigita.cms.contenttypes.WorkingPaper;
import com.arsdigita.cms.scipublications.exporter.PublicationFormat;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporter;
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
        BibTeXBuilder builder;
        

        builder = BibTeXBuilders.getInstance().
                getBibTeXBuilderForCcmPublicationtType(publication.getClass().
                getName());

        if ((builder == null) && publication instanceof PublicationWithPublisher) {
            builder = BibTeXBuilders.getInstance().
                    getBibTeXBuilderForCcmPublicationtType(
                    PublicationWithPublisher.class.getName());
        } else {
            builder = BibTeXBuilders.getInstance().
                    getBibTeXBuilderForCcmPublicationtType(Publication.class.
                    getName());
        }

        

        return builder.toBibTeX();
    }
}
