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
package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.ArticleInJournal;
import com.arsdigita.cms.contenttypes.Publication;

/**
 * Converts a {@link ArticleInJournal} to a RIS reference.
 *
 * @author Jens Pelzetter
 * @version $Id$
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

        getRisBuilder().setType(RisTypes.JOUR);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        if (article.getJournal() != null) {
            getRisBuilder().addField(RisFields.T2,
                                     article.getJournal().getTitle());
        }

        if (article.getIssue() != null) {
            getRisBuilder().addField(RisFields.M1, article.getIssue());
        }

        if (article.getVolume() != null) {
            getRisBuilder().addField(RisFields.VL,
                                     article.getVolume().toString());
        }

        if (article.getPagesFrom() != null) {
            getRisBuilder().addField(RisFields.SP,
                                     String.format("%d - %d", article.getPagesFrom(), article.getPagesTo()));
            /*
             * getRisBuilder().addField(RisFields.EP,
                                     article.getPagesTo().toString());
             */
        }
        
        if (article.getReviewed()) {
            getRisBuilder().addField(RisFields.RI, "");
        }        
        
        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return ArticleInJournalConverter.class.getName();
    }
}
