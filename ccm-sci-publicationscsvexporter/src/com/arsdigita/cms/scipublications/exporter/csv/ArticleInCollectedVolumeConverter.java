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

import com.arsdigita.cms.contenttypes.ArticleInCollectedVolume;
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.Publication;
import java.util.Map;

import static com.arsdigita.cms.scipublications.exporter.csv.CsvExporterConstants.*;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ArticleInCollectedVolumeConverter extends PublicationConverter {

    @Override
    public Map<CsvExporterConstants, String> convert(final Publication publication) {

        if (!(publication instanceof ArticleInCollectedVolume)) {
            throw new UnsupportedCcmTypeException(
                String.format("The ArticleInCollectedVolumeConverter only "
                              + "supports publication types which are of the"
                              + "type ArticleInCollectedVolume or which are extending "
                              + "ArticleInCollectedVolume. The "
                              + "provided publication is of type '%s' which "
                              + "is not of type ArticleInCollectedVolume and does not "
                              + "extends PubliccationWithPublisher.",
                              publication.getClass().getName()));
        }

        final ArticleInCollectedVolume article = (ArticleInCollectedVolume) publication;

        final Map<CsvExporterConstants, String> values = super.convert(publication);
        
        values.put(PAGES_FROM, convertIntegerValue(article.getPagesFrom()));
        values.put(PAGES_TO, convertIntegerValue(article.getPagesTo()));
        values.put(CHAPTER, article.getChapter());
        
        if (article.getCollectedVolume() != null) {
            final CollectedVolume collectedVolume = article.getCollectedVolume();
            values.put(COLLECTED_VOLUME, collectedVolume.getTitle());
            values.put(COLLECTED_VOLUME_EDITORS, convertAuthors(collectedVolume.getAuthors()));
        }
        
        return values;
    }
    
    @Override
    public String getCCMType() {
        return ArticleInCollectedVolume.class.getName();
    }

}
