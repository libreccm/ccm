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

import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;

/**
 * Converts a {@link Proceedings} to a RIS reference.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ProceedingsConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
        Proceedings proceedings;

        if (!(publication instanceof Proceedings)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The ProceedingsConverter only "
                                  + "supports publication types which are of the"
                                  + "type Proceedings or which are "
                                  + "extending "
                                  + "Proceedings. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "Proceedings and does not "
                                  + "extends Proceedings.",
                                  publication.getClass().getName()));
        }

        proceedings = (Proceedings) publication;

        getRisBuilder().setType(RisTypes.CONF);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        convertVolume(proceedings);
        convertSeries(publication);
        convertPublisher(proceedings);


        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return Proceedings.class.getName();
    }
}
