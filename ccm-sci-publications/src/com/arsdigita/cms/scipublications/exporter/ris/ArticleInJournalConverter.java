package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author Jens Pelzetter
 */
public class ArticleInJournalConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
        ArticleInJournal article;

        if (!(publication instanceof ArticleInJournal)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The ArticleInJournalConverter only "
                                  + "supports publication types which are of the"
                                  + "type ArticleInJournal or which are "
                                  + "extending "
                                  + "ArticleInJournal. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "ArticleInJournal and does not "
                                  + "extends ArticleInJournal.",
                                  publication.getClass().getName()));
        }

        article = (ArticleInJournal) publication;

        getRisBuilder().setType(RisTypes.MGZN);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        if (article.getJournal() != null) {
            getRisBuilder().addField(RisFields.JF,
                                     article.getJournal().getTitle());
        }

        if (article.getVolume() != null) {
            getRisBuilder().addField(RisFields.VL,
                                     article.getVolume().toString());
        }

        if (article.getPagesFrom() != null) {
            getRisBuilder().addField(RisFields.SP,
                                     article.getPagesFrom().toString());
            getRisBuilder().addField(RisFields.EP,
                                     article.getPagesTo().toString());
        }

        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return ArticleInJournalConverter.class.getName();
    }
}
