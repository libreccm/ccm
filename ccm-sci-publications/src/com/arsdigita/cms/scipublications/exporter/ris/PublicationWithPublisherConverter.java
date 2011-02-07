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

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;

/**
 * Converts a {@link PublicationWithPublisher} to a RIS reference. This converter
 * is used as a fallback if no other suitable converter can be found and the 
 * publication to convert is a subclass of {@link PublicationWithPublisher}.
 *
 * @author Jens Pelzetter
 */
public class PublicationWithPublisherConverter extends AbstractRisConverter {

    public String convert(final Publication publication) {
        PublicationWithPublisher _publication;

        if (!(publication instanceof PublicationWithPublisher)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The PublicationWithPublicationConverter only "
                                  + "supports publication types which are "
                                  + "extending PublicationWithPublisher. The "
                                  + "provided publication is of type '%s' which "
                                  + "does not extends "
                                  + "PublicationWithPublisher.",
                                  publication.getClass().getName()));
        }

        _publication = (PublicationWithPublisher) publication;

        getRisBuilder().setType(RisTypes.GEN);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        convertPublisher(_publication);
        convertISBN(_publication);
        convertEdition(_publication);

        return getRisBuilder().toRis();
    }

    public String getCcmType() {
        return PublicationWithPublisher.class.getName();
    }
}
