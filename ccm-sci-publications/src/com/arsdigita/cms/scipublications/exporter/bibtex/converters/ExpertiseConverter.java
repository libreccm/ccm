package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class ExpertiseConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(ExpertiseConverter.class);

    @Override
    protected String getBibTeXType() {
        return "misc";
    }

    public String convert(Publication publication) {
        BibTeXBuilder builder;
        Expertise expertise;

         if (!(publication instanceof Expertise)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The ExpertiseConverter only "
                                  + "supports publication types which are of the"
                                  + "type Expertise or which are "
                                  + "extending "
                                  + "Expertise. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "Expertise and does not "
                                  + "extends Expertise.",
                                  publication.getClass().getName()));
        }

        expertise = (Expertise) publication;

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
        return Expertise.class.getName();
    }

}
