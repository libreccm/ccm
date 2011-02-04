package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.SeriesCollection;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXField;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class ArticleInCollectedVolumeConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            ArticleInCollectedVolumeConverter.class);

    @Override
    protected String getBibTeXType() {
        return "incollection";
    }

    public String convert(final Publication publication) {
        ArticleInCollectedVolume article;
        BibTeXBuilder builder;

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

        convertAuthors(publication);
        builder = getBibTeXBuilder();
        try {
            convertTitle(publication);
            convertYear(publication);

            builder.setField(BibTeXField.BOOKTITLE,
                             article.getCollectedVolume().getTitle());
            if (article.getCollectedVolume().getPublisher() == null) {
                builder.setField(BibTeXField.PUBLISHER, "");
            } else {
                builder.setField(BibTeXField.PUBLISHER,
                                 article.getCollectedVolume().getPublisher().
                        getTitle());
            }

            if (article.getCollectedVolume().getVolume() != null) {
                builder.setField(BibTeXField.VOLUME,
                                 article.getCollectedVolume().getVolume().
                        toString());
            }
            SeriesCollection seriesColl =
                             article.getCollectedVolume().getSeries();
            if ((seriesColl != null) && (seriesColl.size() > 0)) {

                seriesColl.next();

                builder.setField(BibTeXField.SERIES,
                                 seriesColl.getSeries().getTitle());

                seriesColl.close();
            }

            if (article.getChapter() != null) {
                builder.setField(BibTeXField.CHAPTER, article.getChapter());
            }

            if (article.getPagesFrom() != null) {
                builder.setField(BibTeXField.PAGES,
                                 String.format("%s - %s",
                                               article.getPagesFrom(),
                                               article.getPagesTo()));
            }

            if (article.getCollectedVolume().getEdition() != null) {
                builder.setField(BibTeXField.EDITION,
                                 article.getCollectedVolume().getEdition());
            }
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return builder.toBibTeX();
    }

    public String getCcmType() {
        return ArticleInCollectedVolume.class.getName();
    }
}
