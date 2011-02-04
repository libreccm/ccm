package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXField;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class InternetArticleConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            InternetArticleConverter.class);

    @Override
    protected String getBibTeXType() {
        return "misc";
    }

    public String convert(final Publication publication) {
        BibTeXBuilder builder;
        InternetArticle article;

        if (!(publication instanceof InternetArticle)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The InternetArticleConverter only "
                                  + "supports publication types which are of the"
                                  + "type InternetArticle or which are "
                                  + "extending "
                                  + "InternetArticle. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "InternetArticle and does not "
                                  + "extends InternetArticle.",
                                  publication.getClass().getName()));
        }

        article = (InternetArticle) publication;

        convertAuthors(publication);
        builder = getBibTeXBuilder();
        try {
            convertTitle(publication);
            convertYear(publication);            
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return builder.toBibTeX();
    }

    public String getCcmType() {
        return InternetArticle.class.getName();
    }
}
