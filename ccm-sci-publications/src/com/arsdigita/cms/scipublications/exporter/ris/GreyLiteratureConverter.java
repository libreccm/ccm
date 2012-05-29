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

        getRisBuilder().setType(RisTypes.UNPB);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);
        
        if (greyLiterature.getPlace() != null) {
            getRisBuilder().addField(RisFields.CY, greyLiterature.getPlace());
        }
        
        if (greyLiterature.getNumber() != null) {
            getRisBuilder().addField(RisFields.M1, greyLiterature.getNumber());
        }
        
        if (greyLiterature.getOrganization() != null) {
            getRisBuilder().addField(RisFields.PB, greyLiterature.getOrganization().getTitle());
        }

        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return GreyLiterature.class.getName();
    }
}
