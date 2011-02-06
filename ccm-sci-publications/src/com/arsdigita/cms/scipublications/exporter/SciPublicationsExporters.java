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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;

/**
 * This class provides access to implementations of the 
 * {@link SciPublicationsExporter} interface. It is implemented as a Singleton.
 *
 * @author Jens Pelzetter
 */
public final class SciPublicationsExporters {

    private static final Logger logger = Logger.getLogger(
            SciPublicationsExporters.class);
    /**
     * Association of the format and the responsible exporters.
     */
    private Map<String, SciPublicationsExporter> exporters =
                                                 new HashMap<String, SciPublicationsExporter>();

    /**
     * The one and only instance of this class. The pattern here ensures that
     * the instance is created at the first access, but not earlier.
     */
    private static class Instance {

        private static final SciPublicationsExporters INSTANCE =
                                                      new SciPublicationsExporters();
    }

    /**
     * Creates the instance. Uses the {@link ServiceLoader} to find all available
     * implementations of {@link SciPublicationsExporter} and puts them into
     * the {@link #exporters} map.
     */
    private SciPublicationsExporters() {
        logger.debug("Creating SciPublicationsExporter instance...");
        ServiceLoader<SciPublicationsExporter> exporterServices;

        logger.debug("Loading all available implementations of the "
                     + "SciPublicationsExporter interface...");
        exporterServices = ServiceLoader.load(SciPublicationsExporter.class);

        for (SciPublicationsExporter exporter : exporterServices) {
            logger.debug(String.format("Found exporter for format '%s'...",
                                       exporter.getSupportedFormat().getName().
                    toLowerCase()));
            exporters.put(exporter.getSupportedFormat().getName().toLowerCase(),
                          exporter);
        }
        logger.debug(String.format("Found %d exporters.", exporters.size()));
    }

    /**
     * @return The instance of this class.
     */
    public static SciPublicationsExporters getInstance() {
        return Instance.INSTANCE;
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
     * @return A list of all supported export formats.
     */
    public List<PublicationFormat> getSupportedFormats() {
        List<PublicationFormat> supportedFormats;

        supportedFormats = new ArrayList<PublicationFormat>();

        for (Map.Entry<String, SciPublicationsExporter> entry : exporters.
                entrySet()) {
            supportedFormats.add(entry.getValue().getSupportedFormat());
        }

        return supportedFormats;
    }
}
