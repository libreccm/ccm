package com.arsdigita.cms.scipublications.importer;

import com.arsdigita.cms.scipublications.imexporter.PublicationFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;

/**
 * This class provides access to the available implementations of the {@link SciPublicationsImporter} interface.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsImporters {

    private static final Logger LOGGER = Logger.getLogger(SciPublicationsImporters.class);
    /**
     * Asscociation of the format the responsible importer.
     */
    private Map<String, SciPublicationsImporter> importers = new HashMap<String, SciPublicationsImporter>();

    private static class Instance {

        private static final SciPublicationsImporters INSTANCE = new SciPublicationsImporters();
    }

    /**
     * Create the instance. Uses the {@link ServiceLoader} to find all avaiable implementations of 
     * {@link SciPublicationsImporters} and puts them into the {@link #importers} map.
     */
    private SciPublicationsImporters() {
        LOGGER.debug("Creating SciPublicationsImporter instance...");
        final ServiceLoader<SciPublicationsImporter> importerServices;

        LOGGER.debug("Loading all available implementations of the SciPublicationsImporter interface...");
        importerServices = ServiceLoader.load(SciPublicationsImporter.class);

        for (SciPublicationsImporter importer : importerServices) {
            LOGGER.debug(String.format("Found importer for format '%s'...",
                                       importer.getSupportedFormat().getName().toLowerCase()));
            importers.put(importer.getSupportedFormat().getName().toLowerCase(), importer);
        }
        LOGGER.debug(String.format("Found %d importers.", importers.size()));
    }

    public static SciPublicationsImporters getInstance() {
        return Instance.INSTANCE;
    }

    /**
     * Retrieves the importer for the specified format.
     * 
     * @param format The format which should be supported by the importer.
     * @return  The importer for the specified format, or <code>null</code> if there is no importer which supports the
     * specified format.
     */
    public SciPublicationsImporter getImporterForFormat(final String format) {
        return importers.get(format.toLowerCase());
    }

    /**
     *
     * @return A list of all supported export formats.
     */
    public List<PublicationFormat> getSupportedFormats() {
        List<PublicationFormat> supportedFormats;

        supportedFormats = new ArrayList<PublicationFormat>();

        for (Map.Entry<String, SciPublicationsImporter> entry : importers.entrySet()) {
            supportedFormats.add(entry.getValue().getSupportedFormat());
        }

        return supportedFormats;
    }

}
