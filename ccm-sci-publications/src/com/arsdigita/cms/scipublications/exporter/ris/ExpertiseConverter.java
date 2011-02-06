package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author Jens Pelzetter
 */
public class ExpertiseConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
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

        getRisBuilder().setType(RisTypes.GEN);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        return getRisBuilder().toRis();        
    }

    @Override
    public String getCcmType() {
        return Expertise.class.getName();
    }
}
