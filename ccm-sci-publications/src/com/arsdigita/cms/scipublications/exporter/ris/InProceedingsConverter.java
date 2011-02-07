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

import com.arsdigita.cms.contenttypes.InProceedings;
import com.arsdigita.cms.contenttypes.Proceedings;
import com.arsdigita.cms.contenttypes.Publication;

/**
 * Converts a {@link InProceedings} to a RIS reference.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class InProceedingsConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
        InProceedings inProceedings;

        if (!(publication instanceof InProceedings)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The InProceedingsConverter only "
                                  + "supports publication types which are of the"
                                  + "type InProceedings or which are "
                                  + "extending "
                                  + "InProceedings. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "InProceedings and does not "
                                  + "extends InProceedings.",
                                  publication.getClass().getName()));
        }

        inProceedings = (InProceedings) publication;

        getRisBuilder().setType(RisTypes.GEN);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        if (inProceedings.getPagesFrom() != null) {
            getRisBuilder().addField(RisFields.SP,
                    inProceedings.getPagesFrom().toString());
             getRisBuilder().addField(RisFields.EP,
                    inProceedings.getPagesTo().toString());
        }

        if(inProceedings.getProceedings() != null) {
            Proceedings proceedings;

            proceedings = inProceedings.getProceedings();

            getRisBuilder().addField(RisFields.BT,
                    proceedings.getTitle());

            convertVolume(proceedings);
            convertSeries(proceedings);
            convertPublisher(proceedings);
        }

        return getRisBuilder().toString();
    }

    @Override
    public String getCcmType() {
        return InProceedings.class.getName();
    }
}
