/*
 * Copyright (c) 2010 Jens Pelzetter, ScientificCMS.org Team
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
import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;

/**
 * This interface describes the methods provided by all publication exporters.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public interface SciPublicationsExporter {

    /**
     *
     * @return A description of the exportUsers format provided by this exporter.
     */
    PublicationFormat getSupportedFormat();

    /**
     * Exports an publication to the format provided by this exporter.
     *
     * @param publication The publication to exportUsers.
     * @return The data of the publication in the provided exportUsers format.
     */
    String exportPublication(Publication publication);
    
    /**
     * @return If the format requires some sort of preamble this method should return it.
     * If the format needs no preamble the method must return null.
     */
    String getPreamble();

}
