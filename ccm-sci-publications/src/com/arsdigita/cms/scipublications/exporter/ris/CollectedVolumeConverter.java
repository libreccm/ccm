package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author Jens Pelzetter
 */
public class CollectedVolumeConverter extends AbstractRisConverter {

    @Override
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

        getRisBuilder().setType(RisTypes.BOOK);
        convertAuthors(publication);

        convertTitle(publication);
        convertYear(publication);

        convertPublisher(collectedVolume);
        convertISBN(collectedVolume);
        convertEdition(collectedVolume);

        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return CollectedVolume.class.getName();
    }
}
