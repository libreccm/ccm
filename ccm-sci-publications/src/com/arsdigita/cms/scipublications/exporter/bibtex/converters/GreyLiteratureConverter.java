package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class GreyLiteratureConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(GreyLiteratureConverter.class);

    @Override
    protected String getBibTeXType() {
        return "misc";
    }

    public String convert(Publication publication) {
        BibTeXBuilder builder;
        GreyLiterature greyLiterature;

         if (!(publication instanceof GreyLiterature)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The GreyLiteratureConverter only "
                                  + "supports publication types which are of the"
                                  + "type GreyLiterature or which are "
                                  + "extending "
                                  + "GreyLiterature. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "GreyLiterature and does not "
                                  + "extends GreyLiterature.",
                                  publication.getClass().getName()));
        }

        greyLiterature = (GreyLiterature) publication;

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
        return GreyLiterature.class.getName();
    }

}
