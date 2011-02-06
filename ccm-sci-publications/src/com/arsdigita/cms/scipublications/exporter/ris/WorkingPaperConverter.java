package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.WorkingPaper;

/**
 *
 * @author Jens Pelzetter
 */
public class WorkingPaperConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
        WorkingPaper workingPaper;

        if (!(publication instanceof WorkingPaper)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The WorkingPaperConverter only "
                                  + "supports publication types which are of the"
                                  + "type WorkingPaper or which are "
                                  + "extending "
                                  + "WorkingPaper. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "WorkingPaper and does not "
                                  + "extends WorkingPaper.",
                                  publication.getClass().getName()));
        }

        workingPaper = (WorkingPaper) publication;

        getRisBuilder().setType(RisTypes.UNPB);

        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return WorkingPaper.class.getName();
    }
}
