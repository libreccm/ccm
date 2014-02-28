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

import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;
import static com.arsdigita.cms.scipublications.exporter.csv.CsvExporterConstants.*;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ProceedingsConverter extends PublicationWithPublisherConverter {

    @Override
    public Map<CsvExporterConstants, String> convert(final Publication publication) {

        if (!(publication instanceof Proceedings)) {
            throw new UnsupportedCcmTypeException(
                String.format("The ProceedingsConverter only "
                              + "supports publication types which are of the"
                              + "type Proceedings or which are extending "
                              + "Proceedings. The "
                              + "provided publication is of type '%s' which "
                              + "is not of type Proceedings and does not "
                              + "extends PubliccationWithPublisher.",
                              publication.getClass().getName()));
        }

        final Map<CsvExporterConstants, String> values = super.convert(publication);

        final Proceedings proceedings = (Proceedings) publication;

        values.put(CONFERENCE, proceedings.getNameOfConference());
        values.put(CONFERENCE_PLACE, proceedings.getPlaceOfConference());
        values.put(CONFERENCE_DATE_FROM, convertDateValue(proceedings.getDateFromOfConference()));
        values.put(CONFERENCE_DATE_TO, convertDateValue(proceedings.getDateToOfConference()));

        return values;
    }

    @Override
    public String getCCMType() {
        return Proceedings.class.getName();
    }

}
