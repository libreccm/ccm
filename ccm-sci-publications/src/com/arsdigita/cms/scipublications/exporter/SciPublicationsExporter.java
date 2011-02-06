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
package com.arsdigita.cms.scipublications.exporter;

import com.arsdigita.cms.contenttypes.Publication;

/**
 * This interface describes the methods provided by all publication exporters.
 *
 * @author Jens Pelzetter
 */
public interface SciPublicationsExporter {

    /**
     *
     * @return A description of the export format provided by this exporter.
     */
    PublicationFormat getSupportedFormat();

    /**
     * Exports an publication to the format provided by this exporter.
     *
     * @param publication The publication to export.
     * @return The data of the publication in the provided export format.
     */
    String exportPublication(Publication publication);

}
