/*
 * Copyright (c) 2014 Jens Pelzetter
 *
 * This library is free software, you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library, if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.scipublications.exporter.csv;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.scipublications.exporter.SciPublicationsExporter;
import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import java.util.Map;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class CsvExporter implements SciPublicationsExporter {

    private final static Logger LOGGER = Logger.getLogger(CsvExporter.class);
    private final static CsvExporterConfig CONFIG = new CsvExporterConfig();

    @Override
    public PublicationFormat getSupportedFormat() {
        try {
            return new PublicationFormat("CSV",
                                         new MimeType("text", "csv"),
                                         "csv");
        } catch (MimeTypeParseException ex) {
            LOGGER.warn("Failed to create MimeType for PublicationFormat."
                        + "Using null mimetype instead. Cause: ", ex);
            return new PublicationFormat("CSV",
                                         null,
                                         "csv");

        }
    }

    @Override
    public String exportPublication(final Publication publication) {
        final Map<CsvExporterConstants, String> line = CsvConverters.getInstance().convert(
            publication);

        final String[] columns = CONFIG.getColumns().split(",");
        final String separator = CONFIG.getSeparator();

        final StringBuilder builder = new StringBuilder();

        String value;
        for (String token : columns) {
            value = line.get(CsvExporterConstants.valueOf(token));
            if ((value == null) || value.isEmpty()) {
                builder.append(" ");
            } else {
                builder.append(value);
            }
            builder.append(separator);
        }

        builder.append("\n");
        
        return builder.toString();
    }
    
      @Override
    public String getPreamble() {
        
        final String[] columns = CONFIG.getColumns().split(",");
        final String separator = CONFIG.getSeparator();
        
        final StringBuilder builder = new StringBuilder();
        
        for(String column : columns) {
            builder.append(column);
            builder.append(separator);
        }
        builder.append("\n");
        
        return builder.toString();
    }

}
