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

import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;

/**
 * Converts a {@link Publication} to a RIS reference. This converter is used
 * as a fallback if no other suitable converter can be found and the publication
 * to convert is <em>no</em> subclass of {@link PublicationWithPublisher}.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class PublicationConverter extends AbstractRisConverter {

    public String convert(final Publication publication) {
        getRisBuilder().setType(RisType.GEN);
        convertAuthors(publication);
        convertTitle(publication);
        convertYear(publication);

        return getRisBuilder().toRis();
    }

    public String getCcmType() {
        return Publication.class.getName();
    }
}
