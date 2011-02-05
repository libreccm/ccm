package com.arsdigita.cms.scipublications.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public final class SciPublicationsExporters {

    private static final Logger logger = Logger.getLogger(
            SciPublicationsExporters.class);
    private Map<String, SciPublicationsExporter> exporters =
                                                 new HashMap<String, SciPublicationsExporter>();

    private static class Instance {

        private static final SciPublicationsExporters INSTANCE =
                                                      new SciPublicationsExporters();
    }

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

    public static SciPublicationsExporters getInstance() {
        return Instance.INSTANCE;
    }

    public SciPublicationsExporter getExporterForFormat(final String format) {
        return exporters.get(format);
    }

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
