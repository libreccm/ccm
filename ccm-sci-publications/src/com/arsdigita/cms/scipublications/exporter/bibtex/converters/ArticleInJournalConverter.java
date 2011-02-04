package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXField;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class ArticleInJournalConverter extends AbstractBibTeXConverter {

    private static final Logger logger = Logger.getLogger(
            ArticleInJournalConverter.class);

    @Override
    protected String getBibTeXType() {
        return "article";
    }

    public String convert(final Publication publication) {
        ArticleInJournal article;
        BibTeXBuilder builder;

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

        convertAuthors(publication);
        builder = getBibTeXBuilder();
        try {
            convertTitle(publication);
            convertYear(publication);

            if (article.getJournal() == null) {
                builder.setField(BibTeXField.JOURNAL, "");
            } else {
                builder.setField(BibTeXField.JOURNAL,
                                 article.getJournal().getTitle());
            }

            if (article.getVolume() != null) {
                builder.setField(BibTeXField.VOLUME,
                                 article.getVolume().toString());
            }

            if (article.getIssue() != null) {
                builder.setField(BibTeXField.NUMBER,
                                 article.getIssue());
            }

             if (article.getPagesFrom() != null) {
                builder.setField(BibTeXField.PAGES,
                                 String.format("%s - %s",
                                               article.getPagesFrom(),
                                               article.getPagesTo()));
            }
        } catch (UnsupportedFieldException ex) {
            logger.warn("Tried to set unsupported BibTeX field while "
                        + "converting a publication");
        }

        return builder.toBibTeX();
    }

    public String getCcmType() {
        return ArticleInJournal.class.getName();
    }
}
