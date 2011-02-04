package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.WorkingPaper;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class WorkingPaperConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            WorkingPaperConverter.class);

    @Override
    protected String getBibTeXType() {
        return "misc";
    }

    public String convert(Publication publication) {
        BibTeXBuilder builder;
        WorkingPaper workingPaper;

        if (!(publication instanceof WorkingPaper)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The WorkingPaperConverter only "
                                  + "supports publication types which are of the"
                                  + "type WorkingPaper or which are "
                                  + "extending "
                                  + "WorkingPaper. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "WorkingPaper and does not "
                                  + "extends WorkingPaper.",
                                  publication.getClass().getName()));
        }

        workingPaper = (WorkingPaper) publication;

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
        return WorkingPaper.class.getName();
    }
}
