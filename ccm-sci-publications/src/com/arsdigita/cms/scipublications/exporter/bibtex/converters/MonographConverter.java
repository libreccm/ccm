package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class MonographConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            MonographConverter.class);

    @Override
    protected String getBibTeXType() {
        return "book";
    }

    public String convert(final Publication publication) {
        Monograph monograph;

        if (!(publication instanceof Monograph)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The MonographConverter only "
                                  + "supports publication types which are of the"
                                  + "type Monograph or which are extending "
                                  + "Monograh. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type Monograph and does not "
                                  + "extends Monograph.",
                                  publication.getClass().getName()));
        }

        monograph = (Monograph) publication;

        convertAuthors(publication);
        try {
            convertTitle(publication);
            convertYear(publication);

            convertPublisher(monograph);
            convertISBN(monograph);
            convertEdition(monograph);
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return getBibTeXBuilder().toBibTeX();
    }

    public String getCcmType() {
        return Monograph.class.getName();
    }
}
