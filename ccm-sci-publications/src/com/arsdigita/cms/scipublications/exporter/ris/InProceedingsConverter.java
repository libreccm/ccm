package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author Jens Pelzetter
 */
public class InProceedingsConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
        InProceedings inProceedings;

        if (!(publication instanceof InProceedings)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The InProceedingsConverter only "
                                  + "supports publication types which are of the"
                                  + "type InProceedings or which are "
                                  + "extending "
                                  + "InProceedings. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "InProceedings and does not "
                                  + "extends InProceedings.",
                                  publication.getClass().getName()));
        }

        inProceedings = (InProceedings) publication;

        getRisBuilder().setType(RisTypes.GEN);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        if (inProceedings.getPagesFrom() != null) {
            getRisBuilder().addField(RisFields.SP,
                    inProceedings.getPagesFrom().toString());
             getRisBuilder().addField(RisFields.EP,
                    inProceedings.getPagesTo().toString());
        }

        if(inProceedings.getProceedings() != null) {
            Proceedings proceedings;

            proceedings = inProceedings.getProceedings();

            getRisBuilder().addField(RisFields.BT,
                    proceedings.getTitle());

            convertVolume(proceedings);
            convertSeries(proceedings);
            convertPublisher(proceedings);
        }

        return getRisBuilder().toString();
    }

    @Override
    public String getCcmType() {
        return InProceedings.class.getName();
    }
}
