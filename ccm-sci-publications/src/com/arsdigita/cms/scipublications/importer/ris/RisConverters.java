package com.arsdigita.cms.scipublications.importer.ris;

import com.arsdigita.cms.scipublications.imexporter.ris.RisType;
import com.arsdigita.cms.scipublications.importer.report.PublicationImportReport;
import com.arsdigita.cms.scipublications.importer.util.ImporterUtil;
import java.util.EnumMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;

/**
 * Central access point to retrieve {@link RisConverter}s for importing publication data in the RIS format.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class RisConverters {

    private static final Logger LOGGER = Logger.getLogger(RisConverters.class);
    private Map<RisType, RisConverter> converters = new EnumMap<RisType, RisConverter>(RisType.class);

    /**
     * The constructor loads all available implementations of the
     * {@link RisConverter} interface using the {@link ServiceLoader}.
     */
    private RisConverters() {
        LOGGER.debug("Loading RIS import converters...");
        final ServiceLoader<RisConverter> converterServices = ServiceLoader.load(RisConverter.class);

        for (RisConverter converter : converterServices) {
            LOGGER.debug(String.format("Found converter for RIS type '%s'.", converter.getRisType().toString()));

            converters.put(converter.getRisType(), converter);
        }
        LOGGER.debug(String.format("Found %d import converters.", converters.size()));
    }

    /**
     * Keeps the instance of this class.
     */
    private static class Instance {

        private static RisConverters INSTANCE = new RisConverters();
    }

    /**
     *
     * @return The instance of this class.
     */
    public static RisConverters getInstance() {
        return Instance.INSTANCE;
    }

    /**
     * 
     * @param dataset     
     * @param importerUtil 
     * @param pretend
     * @param publishNewItems
     * @return
     * @throws RisConverterException  
     */
    public PublicationImportReport convert(final RisDataset dataset,
                                           final ImporterUtil importerUtil,
                                           final boolean pretend,
                                           final boolean publishNewItems) throws RisConverterException {
        try {
            LOGGER.debug(String.format("Trying to find converter for RIS type '%s'...", dataset.getType().toString()));
            RisConverter converter = converters.get(dataset.getType());

            if (converter == null) {
                throw new RisConverterException(String.format("Failed to find a converter for RIS type '%s'.",
                                                                       dataset.getType().toString()));
            }

            converter = converter.getClass().newInstance();
                        
            return converter.convert(dataset, importerUtil, pretend, publishNewItems);
        } catch (InstantiationException ex) {
           throw new RisConverterException("Converter instantiation failed.", ex);
        } catch (IllegalAccessException ex) {
            throw new RisConverterException("Converter instantiation failed.", ex);
        }       
    }

}
