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

/**
 * Interface for the RIS converters
 *
 * @author Jens Pelzetter
 */
public interface RisConverter {

    /**
     * Converts a publication.
     *
     * @param publication The publication to convert.
     * @return The data of the publication converted to RIS.
     * @throws UnsupportedCcmTypeException If the provided publication is of
     * a type which is not supported by this converter.
     */
    String convert(Publication publication);

    /**
     * @return The CCM publication type supported by this converter.
     */
    String getCcmType();

}
