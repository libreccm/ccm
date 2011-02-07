/*
 * Copyright (c) 2010 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXBuilder;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.BibTeXField;
import com.arsdigita.cms.scipublications.exporter.bibtex.builders.UnsupportedFieldException;
import org.apache.log4j.Logger;

/**
 * Converts an {@link ArticleInJournal} to a <code>article</code> BibTeX 
 * reference.
 *
 * @author Jens Pelzetter
 * @version $Id$
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
