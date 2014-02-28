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

import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.Publication;
import java.util.Map;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class CollectedVolumeConverter extends PublicationWithPublisherConverter {

    @Override
    public Map<CsvExporterConstants, String> convert(final Publication publication) {

        if (!(publication instanceof CollectedVolume)) {
            throw new UnsupportedCcmTypeException(
                String.format("The CollectedVolumeConverter only "
                              + "supports publication types which are of the"
                              + "type CollectedVolume or which are extending "
                              + "CollectedVolume. The "
                              + "provided publication is of type '%s' which "
                              + "is not of type CollectedVolume and does not "
                              + "extends PubliccationWithPublisher.",
                              publication.getClass().getName()));
        }

        final CollectedVolume collectedVolume = (CollectedVolume) publication;

        return super.convert(publication);

    }

    @Override
    public String getCCMType() {
        return CollectedVolume.class.getName();
    }

}
