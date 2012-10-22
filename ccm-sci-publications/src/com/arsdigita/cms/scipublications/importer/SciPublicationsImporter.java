/*
 * Copyright (c) 2012 Jens Pelzetter, ScientificCMS.org team
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
package com.arsdigita.cms.scipublications.importer;

import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import com.arsdigita.cms.scipublications.importer.report.ImportReport;

/**
 * Interface for publication importers
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public interface SciPublicationsImporter {
    
    /**    
     * @return Description of the format supported by the importer implementation. 
     */
    PublicationFormat getSupportedFormat();
           
    /**
     * Parses the provided string and creates publications from the string. 
     * 
     * @param publications The string conaining the publications in the format supported by this importer
     * @param pretend If set to {@code true} no publications will be created. This can be used for debugging purposes 
     * or to check an file containing publications.
     * @param publishNewItems If set to {@code true} the items created by the importer will also be published. 
     * @return A report describing what the importer has done.
     */
    ImportReport importPublications(String publications, boolean pretend, boolean publishNewItems);
    
}
