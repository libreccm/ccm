/*
 * Copyright (c) 2014 Jens Pelzetter
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
package com.arsdigita.cms.scipublications.exporter.csv;

import com.arsdigita.cms.contenttypes.Publication;
import java.util.Map;

/**
 * Interface for the converter classes which are transforming publication objects into a CSV line.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public interface CsvConverter {
    
    /**
     * Converts a publication to CSV.
     * 
     * @param publication The publication to convert.
     * @return The values for the CSV line. They are returned as a map, where each entry is
     * a column in the CSV line. For the key the values from {@link CsvExporterConstants} are used.
     */
    Map<CsvExporterConstants, String> convert(Publication publication);
    
    /**
     * 
     * 
     * @return The CCM publication type supported by this converter.
     */
    String getCCMType();
    
}
