package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
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

        getRisBuilder().setType(RisType.UNPD);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);
        
        if (greyLiterature.getPlace() != null) {
            getRisBuilder().addField(RisField.CY, greyLiterature.getPlace());
        }
        
        if (greyLiterature.getNumber() != null) {
            getRisBuilder().addField(RisField.M1, greyLiterature.getNumber());
        }
        
        if (greyLiterature.getOrganization() != null) {
            getRisBuilder().addField(RisField.PB, greyLiterature.getOrganization().getTitle());
        }

        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return GreyLiterature.class.getName();
    }
}
