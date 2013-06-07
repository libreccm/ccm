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
import com.arsdigita.cms.scipublications.imexporter.ris.RisField;
import com.arsdigita.cms.contenttypes.GreyLiterature;
import com.arsdigita.cms.contenttypes.Publication;

/**
 *
 * @author Jens Pelzetter
 */
public class GreyLiteratureConverter extends AbstractRisConverter {

    @Override
    public String convert(final Publication publication) {
        GreyLiterature greyLiterature;

        if (!(publication instanceof GreyLiterature)) {
            throw new UnsupportedCcmTypeException(
                    String.format("The GreyLiteratureConverter only "
                                  + "supports publication types which are of the"
                                  + "type GreyLiterature or which are "
                                  + "extending "
                                  + "GreyLiterature. The "
                                  + "provided publication is of type '%s' which "
                                  + "is not of type "
                                  + "GreyLiterature and does not "
                                  + "extends GreyLiterature.",
                                  publication.getClass().getName()));
        }

        greyLiterature = (GreyLiterature) publication;

        getRisBuilder().setType(RisType.UNPD);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);
        
        if (greyLiterature.getPlace() != null) {
            getRisBuilder().addField(RisField.CY, greyLiterature.getPlace());
        }
        
        if (greyLiterature.getNumber() != null) {
            getRisBuilder().addField(RisField.M1, greyLiterature.getNumber());
        }
        
        if (greyLiterature.getOrganization() != null) {
            getRisBuilder().addField(RisField.PB, greyLiterature.getOrganization().getTitle());
        }

        return getRisBuilder().toRis();
    }

    @Override
    public String getCcmType() {
        return GreyLiterature.class.getName();
    }
}
