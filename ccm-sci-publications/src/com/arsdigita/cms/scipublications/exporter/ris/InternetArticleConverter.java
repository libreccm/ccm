/*
 * Copyright (c) 2010 Jens Pelzetter
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

import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.Publication;

import java.util.Objects;

/**
 * Converts a {@link InternetArticle} to a RIS reference.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class InternetArticleConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
        InternetArticle article;

        System.err.printf("Converting publication %s as InternetArticle to RIS...%n",
                          Objects.toString(publication));
        
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

        System.err.printf("Casting to InternetArticle%n");
        article = (InternetArticle) publication;

        System.err.printf("Setting RIS type to EJOUR...%n");
        getRisBuilder().setType(RisType.EJOUR);
        System.err.printf("Converting authors...%n");
        convertAuthors(publication);
        System.err.printf("Converting title...%n");
        convertTitle(publication);
        System.err.printf("Converting year...%n");
        convertYear(publication);
        
        System.err.printf("Converting reviewed...%n");
        if (article.getReviewed() != null && article.getReviewed()) {
            getRisBuilder().addField(RisField.RI, "");
        }
        
        System.err.printf("Converting  URL...%n");
        if (article.getUrl() != null) {
            getRisBuilder().addField(RisField.UR, article.getUrl());
        }

        System.err.printf("Building String%n");
        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return InternetArticle.class.getName();
    }
}
