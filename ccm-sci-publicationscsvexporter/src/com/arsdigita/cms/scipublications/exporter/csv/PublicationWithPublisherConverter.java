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

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import java.util.Map;

import static com.arsdigita.cms.scipublications.exporter.csv.CsvExporterConstants.*;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class PublicationWithPublisherConverter extends PublicationConverter {

    @Override
    public Map<CsvExporterConstants, String> convert(final Publication publication) {

        if (!(publication instanceof PublicationWithPublisher)) {
            throw new UnsupportedCcmTypeException(
                String.format("The PublicationWithPublisherConverter only "
                              + "supports publication types which are of the"
                              + "type PublicationWithPublisher or which are extending "
                              + "PublicationWithPublisher. The "
                              + "provided publication is of type '%s' which "
                              + "is not of type PublicationWithPublisher and does not "
                              + "extends PubliccationWithPublisher.",
                              publication.getClass().getName()));
        }

        final PublicationWithPublisher publicationWithPublisher
                                       = (PublicationWithPublisher) publication;

        final Map<CsvExporterConstants, String> values = super.convert(publication);

        values.put(ISBN, publicationWithPublisher.getISBN());
        values.put(VOLUME, convertIntegerValue(publicationWithPublisher.getVolume()));
        values.put(NUMBER_OF_VOLUMES, convertIntegerValue(publicationWithPublisher.
            getNumberOfVolumes()));
        values.put(NUMBER_OF_PAGES, convertIntegerValue(
            publicationWithPublisher.getNumberOfPages()));
        values.put(EDITION, publicationWithPublisher.getEdition());
        if (publicationWithPublisher.getPublisher() != null) {
            values.put(PLACE, publicationWithPublisher.getPublisher().getPlace());
            values.put(PUBLISHER, publicationWithPublisher.getPublisher().getPublisherName());
        }

        return values;
    }

    @Override
    public String getCCMType() {
        return PublicationWithPublisher.class.getName();
    }

}
