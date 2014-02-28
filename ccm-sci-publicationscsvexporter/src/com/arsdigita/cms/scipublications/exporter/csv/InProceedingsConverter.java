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

import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.Publication;
import java.util.Map;

import static com.arsdigita.cms.scipublications.exporter.csv.CsvExporterConstants.*;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class InProceedingsConverter extends PublicationConverter {

    @Override
    public Map<CsvExporterConstants, String> convert(final Publication publication) {

        if (!(publication instanceof InProceedings)) {
            throw new UnsupportedCcmTypeException(
                String.format("The InProceedingsConverter only "
                              + "supports publication types which are of the"
                              + "type InProceedings or which are extending "
                              + "InProceedings. The "
                              + "provided publication is of type '%s' which "
                              + "is not of type InProceedings and does not "
                              + "extends PubliccationWithPublisher.",
                              publication.getClass().getName()));
        }

        final InProceedings inProceedings = (InProceedings) publication;

        final Map<CsvExporterConstants, String> values = super.convert(publication);

        values.put(PAGES_FROM, convertIntegerValue(inProceedings.getPagesFrom()));
        values.put(PAGES_TO, convertIntegerValue(inProceedings.getPagesTo()));

        if (inProceedings.getProceedings() != null) {
            values.put(COLLECTED_VOLUME, inProceedings.getProceedings().getTitle());
            values.put(COLLECTED_VOLUME_EDITORS, convertAuthors(inProceedings.getProceedings().
                getAuthors()));
            values.put(CONFERENCE, inProceedings.getProceedings().getNameOfConference());
            values.put(CONFERENCE_PLACE, inProceedings.getProceedings().getPlaceOfConference());
            values.put(CONFERENCE_DATE_FROM, convertDateValue(inProceedings.getProceedings().
                getDateFromOfConference()));
            values.put(CONFERENCE_DATE_TO, convertDateValue(inProceedings.getProceedings().
                getDateToOfConference()));
        }

        return values;
    }

    @Override
    public String getCCMType() {
        return InProceedings.class.getName();
    }

}
