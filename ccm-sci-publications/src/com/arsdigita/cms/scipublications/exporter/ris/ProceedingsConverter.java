package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author jensp
 */
public class ProceedingsConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
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

        getRisBuilder().setType(RisTypes.CONF);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        convertVolume(proceedings);
        convertSeries(publication);
        convertPublisher(proceedings);


        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return Proceedings.class.getName();
    }
}
