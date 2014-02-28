/*
 * Copyright (c) 2014 Jens Pelzetter
 *
 * This library is free software, you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library, if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.scipublications.exporter.csv;

import com.arsdigita.cms.contenttypes.InternetArticle;
import com.arsdigita.cms.contenttypes.Publication;
import java.util.Map;

import static com.arsdigita.cms.scipublications.exporter.csv.CsvExporterConstants.*;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class InternetArticleConverter extends PublicationConverter {

    @Override
    public Map<CsvExporterConstants, String> convert(final Publication publication) {

        if (!(publication instanceof InternetArticle)) {
            throw new UnsupportedCcmTypeException(
                String.format("The InternetArticleConverter only "
                              + "supports publication types which are of the"
                              + "type InternetArticle or which are extending "
                              + "InternetArticle. The "
                              + "provided publication is of type '%s' which "
                              + "is not of type InternetArticle and does not "
                              + "extends PubliccationWithPublisher.",
                              publication.getClass().getName()));
        }

        final InternetArticle article = (InternetArticle) publication;

        final Map<CsvExporterConstants, String> values = super.convert(publication);

        values.put(PLACE, article.getPlace());
        values.put(NUMBER, article.getNumber());
        values.put(NUMBER_OF_PAGES, convertIntegerValue(article.getNumberOfPages()));
        values.put(EDITION, article.getEdition());
        values.put(ISSN, article.getISSN());
        values.put(LAST_ACCESS, convertDateValue(article.getLastAccessed()));
        values.put(PUBLICATION_DATE, convertDateValue(article.getPublicationDate()));
        values.put(URL, article.getUrl());
        values.put(URN, article.getUrn());
        values.put(DOI, article.getDoi());

        return values;
    }

    @Override
    public String getCCMType() {
        return InternetArticle.class.getName();
    }

}
