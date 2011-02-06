package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author Jens Pelzetter
 */
public class InternetArticleConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
        InternetArticle article;

        if (!(publication instanceof InternetArticle)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The InternetArticleConverter only "
                                  + "supports publication types which are of the"
                                  + "type InternetArticle or which are "
                                  + "extending "
                                  + "InternetArticle. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "InternetArticle and does not "
                                  + "extends InternetArticle.",
                                  publication.getClass().getName()));
        }

        article = (InternetArticle) publication;

        getRisBuilder().setType(RisTypes.GEN);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return InternetArticle.class.getName();
    }
}
