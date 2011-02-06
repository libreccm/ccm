package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.Monograph;
import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author Jens Pelzetter
 */
public class MonographConverter extends AbstractRisConverter {

    @Override
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

        getRisBuilder().setType(RisTypes.BOOK);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        convertPublisher(monograph);
        convertISBN(monograph);
        convertEdition(monograph);

        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return Monograph.class.getName();
    }
}
