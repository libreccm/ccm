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
     * Private constructor to ensure that no instances of this class can be created.
     */
    private SciPublicationsImporters() {
        //Nothing
    }

    public static SciPublicationsImporters getInstance() {
        return Instance.INSTANCE;
    }
    
    public static void register(final SciPublicationsImporter importer) {
        getInstance().registerImporter(importer);
    }
    
    public void registerImporter(final SciPublicationsImporter importer) {
        importers.put(importer.getSupportedFormat().getName().toLowerCase(), importer);
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
