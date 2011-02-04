package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class CollectedVolumeConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            CollectedVolumeConverter.class);

    @Override
    protected String getBibTeXType() {
        return "book";
    }

    public String convert(final Publication publication) {
        CollectedVolume collectedVolume;

        if (!(publication instanceof CollectedVolume)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The PublicationWithPublicationConverter only "
                                  + "supports publication types which are "
                                  + "extending CollectedVolume. The "
                                  + "provided publication is of type '%s' which "
                                  + "does not extends "
                                  + "CollectedVolume.",
                                  publication.getClass().getName()));
        }

        collectedVolume = (CollectedVolume) publication;

        convertAuthors(publication);
        try {
            convertTitle(publication);
            convertYear(publication);

            convertPublisher(collectedVolume);
            convertISBN(collectedVolume);
            convertEdition(collectedVolume);
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return getBibTeXBuilder().toBibTeX();
    }

    public String getCcmType() {
        return CollectedVolume.class.getName();
    }
}
