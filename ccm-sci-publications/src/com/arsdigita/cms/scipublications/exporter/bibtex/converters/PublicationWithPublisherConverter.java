package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class PublicationWithPublisherConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            PublicationWithPublisherConverter.class);

    @Override
    protected String getBibTeXType() {
        return "misc";
    }

    public String convert(final Publication publication) {
        PublicationWithPublisher _publication;

        if (!(publication instanceof PublicationWithPublisher)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The PublicationWithPublicationConverter only "
                                  + "supports publication types which are "
                                  + "extending PublicationWithPublisher. The "
                                  + "provided publication is of type '%s' which "
                                  + "does not extends "
                                  + "PublicationWithPublisher.",
                                  publication.getClass().getName()));
        }

        _publication = (PublicationWithPublisher) publication;

        convertAuthors(publication);
        try {
            convertTitle(publication);
            convertYear(publication);

            convertPublisher(_publication);
            convertISBN(_publication);
            convertEdition(_publication);
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return getBibTeXBuilder().toBibTeX();
    }

    public String getCcmType() {
        return PublicationWithPublisher.class.getName();
    }
}
