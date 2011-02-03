package com.arsdigita.cms.scipublications.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 *
 * @author jensp
 */
public final class SciPublicationsExporters {

    private Map<String, SciPublicationsExporter> exporters =
                                                 new HashMap<String, SciPublicationsExporter>();

    private static class Instance {

        private static final SciPublicationsExporters INSTANCE =
                                                      new SciPublicationsExporters();
    }

    private SciPublicationsExporters() {
        ServiceLoader<SciPublicationsExporter> exporterServices ;

        exporterServices = ServiceLoader.load(SciPublicationsExporter.class);

        for(SciPublicationsExporter exporter : exporterServices) {
            exporters.put(exporter.getSupportedFormat().getName(), exporter);
        }
    }

    public SciPublicationsExporters getInstance() {
        return Instance.INSTANCE;
    }

    public SciPublicationsExporter getExporterForFormat(final String format) {
        return exporters.get(format);
    }

    public List<PublicationFormat> getSupportedFormats() {
        List<PublicationFormat> supportedFormats;

        supportedFormats = new ArrayList<PublicationFormat>();

        for(Map.Entry<String, SciPublicationsExporter> entry : exporters.entrySet()) {
            supportedFormats.add(entry.getValue().getSupportedFormat());
        }

        return supportedFormats;
    }
}
