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

import com.arsdigita.cms.contenttypes.Expertise;
import com.arsdigita.cms.contenttypes.Publication;

/**
 * Converts a {@link Expertise} to a RIS reference.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class ExpertiseConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
        Expertise expertise;

        if (!(publication instanceof Expertise)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The ExpertiseConverter only "
                                  + "supports publication types which are of the"
                                  + "type Expertise or which are "
                                  + "extending "
                                  + "Expertise. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "Expertise and does not "
                                  + "extends Expertise.",
                                  publication.getClass().getName()));
        }

        expertise = (Expertise) publication;

        getRisBuilder().setType(RisTypes.GEN);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        return getRisBuilder().toRis();        
    }

    @Override
    public String getCcmType() {
        return Expertise.class.getName();
    }
}
