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
import com.arsdigita.cms.contenttypes.CollectedVolume;
import com.arsdigita.cms.contenttypes.Publication;

/**
 * Converts a {@link CollectedVolume} to a RIS reference.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class CollectedVolumeConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
        CollectedVolume collectedVolume;

        if (!(publication instanceof CollectedVolume)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The PublicationWithPublicationConverter only "
                                  + "supports publication types which are "
                                  + "extending CollectedVolume. The "
                                  + "provided publication is of type '%s' which "
                                  + "does not extends "
                                  + "CollectedVolume.",
                                  publication.getClass().getName()));
        }

        collectedVolume = (CollectedVolume) publication;

        getRisBuilder().setType(RisType.EDBOOK);
        convertAuthors(publication);

        convertTitle(publication);
        convertYear(publication);

        convertPublisher(collectedVolume);
        convertISBN(collectedVolume);
        convertEdition(collectedVolume);

        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return CollectedVolume.class.getName();
    }
}
