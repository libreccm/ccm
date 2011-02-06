package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author jensp
 */
public class ArticleInCollectedVolumeConverter extends AbstractRisConverter {

    public String convert(final Publication publication) {
        ArticleInCollectedVolume article;

        if (!(publication instanceof ArticleInCollectedVolume)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The ArticleInCollectedVolumeConverter only "
                                  + "supports publication types which are of the"
                                  + "type ArticleInCollectedVolume or which are "
                                  + "extending "
                                  + "ArticleInCollectedVolume. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "ArticleInCollectedVolume and does not "
                                  + "extends ArticleInCollectedVolume.",
                                  publication.getClass().getName()));
        }

        article = (ArticleInCollectedVolume) publication;

        getRisBuilder().setType(RisTypes.CHAP);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        if (article.getCollectedVolume() != null) {
            getRisBuilder().addField(RisFields.BT,
                                     article.getCollectedVolume().getTitle());

        }

        if (article.getPagesFrom() != null) {
            getRisBuilder().addField(RisFields.SP,
                                     article.getPagesFrom().toString());
            getRisBuilder().addField(RisFields.EP,
                                     article.getPagesTo().toString());
        }

        return getRisBuilder().toRis();
    }

    public String getCcmType() {
        return ArticleInCollectedVolume.class.getName();
    }
}
