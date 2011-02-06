package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author Jens Pelzetter
 */
public class GreyLiteratureConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
        GreyLiterature greyLiterature;

        if (!(publication instanceof GreyLiterature)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The GreyLiteratureConverter only "
                                  + "supports publication types which are of the"
                                  + "type GreyLiterature or which are "
                                  + "extending "
                                  + "GreyLiterature. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "GreyLiterature and does not "
                                  + "extends GreyLiterature.",
                                  publication.getClass().getName()));
        }

        greyLiterature = (GreyLiterature) publication;

        getRisBuilder().setType(RisTypes.GEN);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return GreyLiterature.class.getName();
    }
}
