/*
 * Copyright (c) 2010 Jens Pelzetter, ScientificCMS.org team
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

import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides access to implementations of the 
 * {@link SciPublicationsExporter} interface. It is implemented as a Singleton.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public final class SciPublicationsExporters {
   
    /**
     * Association of the format and the responsible exporters.
     */
    private Map<String, SciPublicationsExporter> exporters =
                                                 new HashMap<String, SciPublicationsExporter>();

    /**
     * The one and only instance of this class. The pattern used here ensures that
     * the instance is created at the first access, but not earlier.
     */
    private static class Instance {

        private static final SciPublicationsExporters INSTANCE =
                                                      new SciPublicationsExporters();
    }

    /**
     * Private constructor to ensure that no instances of this class can be created.
     */
    private SciPublicationsExporters() {
        //Nothing for now
    }

    /**
     * @return The instance of this class.
     */
    public static SciPublicationsExporters getInstance() {
        return Instance.INSTANCE;
    }
    
    /**
     * Convenient static wrapper method for {@code SciPublicationsExporter.getInstance().registerExporter(SciPublicationsExporter)}.
     * 
     * @param exporter The exporter to register.
     */
    public static void register(final SciPublicationsExporter exporter) {
        getInstance().registerExporter(exporter);
    }
    
    /**
     * Register an {@link SciPublicationsExporter} implementation.
     * 
     * @param exporter The exporter to register.
     */
    public void registerExporter(final SciPublicationsExporter exporter) {
        exporters.put(exporter.getSupportedFormat().getName().toLowerCase(), exporter);
    }

    /**
     * Retrieves the exporter for the specified format.
     *
     * @param format The format which should be supported by the exporter.
     * @return The exporter for the specified format, or <code>null</code>, if
     * there is no exporter which supports the specified format.
     */
    public SciPublicationsExporter getExporterForFormat(final String format) {
        return exporters.get(format);
    }

    /**
     *
     * @return A list of all supported exportUsers formats.
     */
    public List<PublicationFormat> getSupportedFormats() {
        List<PublicationFormat> supportedFormats;

        supportedFormats = new ArrayList<PublicationFormat>();

        for (Map.Entry<String, SciPublicationsExporter> entry : exporters.entrySet()) {
            supportedFormats.add(entry.getValue().getSupportedFormat());
        }

        return supportedFormats;
    }

}
