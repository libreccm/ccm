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

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.Publication;

/**
 * Converts a {@link ArticleInCollectedVolume} to a RIS reference.
 *
 * @author Jens Pelzetter
 * @version $Id$
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
