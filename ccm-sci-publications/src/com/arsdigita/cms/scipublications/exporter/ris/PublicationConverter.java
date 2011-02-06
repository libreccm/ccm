package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author Jens Pelzetter
 */
public class PublicationConverter extends AbstractRisConverter {

    public String convert(final Publication publication) {
        getRisBuilder().setType(RisTypes.GEN);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        return getRisBuilder().toRis();
    }

    public String getCcmType() {
        return Publication.class.getName();
    }
}
