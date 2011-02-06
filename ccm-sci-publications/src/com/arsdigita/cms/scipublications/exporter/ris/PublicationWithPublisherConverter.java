package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationWithPublisherConverter extends AbstractRisConverter {

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

        getRisBuilder().setType(RisTypes.GEN);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        convertPublisher(_publication);
        convertISBN(_publication);
        convertEdition(_publication);

        return getRisBuilder().toRis();
    }

    public String getCcmType() {
        return PublicationWithPublisher.class.getName();
    }
}
