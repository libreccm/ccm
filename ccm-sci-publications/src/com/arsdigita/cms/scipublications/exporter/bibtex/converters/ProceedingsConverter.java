package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXField;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class ProceedingsConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            ProceedingsConverter.class);

    @Override
    protected String getBibTeXType() {
        return "proceedings";
    }

    public String convert(final Publication publication) {
        BibTeXBuilder builder;
        Proceedings proceedings;

        if (!(publication instanceof Proceedings)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The ProceedingsConverter only "
                                  + "supports publication types which are of the"
                                  + "type Proceedings or which are "
                                  + "extending "
                                  + "Proceedings. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "Proceedings and does not "
                                  + "extends Proceedings.",
                                  publication.getClass().getName()));
        }

        proceedings = (Proceedings) publication;

        convertAuthors(publication);
        builder = getBibTeXBuilder();
        try {
            convertTitle(publication);
            convertYear(publication);

            convertVolume(proceedings);
            convertSeries(publication);

            if (proceedings.getOrganizerOfConference() != null) {
                builder.setField(BibTeXField.ORGANIZATION,
                                 proceedings.getOrganizerOfConference().
                        getTitle());
            }

            convertPublisher(proceedings);

        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return builder.toBibTeX();
    }

    public String getCcmType() {
        return Proceedings.class.getName();
    }
}
