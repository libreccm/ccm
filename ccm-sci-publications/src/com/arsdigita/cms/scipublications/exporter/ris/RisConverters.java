package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;

/**
 *
 * @author Jens Pelzetter
 */
public class RisConverters {

    private static final Logger logger = Logger.getLogger(RisConverters.class);
    /**
     * Associates the available converters with their supported CCM types.
     */
    private Map<String, RisConverter> converters =
                                      new HashMap<String, RisConverter>();

    private RisConverters() {
        logger.debug("Loading RIS converters...");
        ServiceLoader<RisConverter> converterServices;

        converterServices = ServiceLoader.load(RisConverter.class);

        for (RisConverter converter : converterServices) {
            logger.debug(String.format("Found converter for CCM type '%s'...",
                                       converter.getCcmType()));
            converters.put(converter.getCcmType(), converter);
        }
        logger.debug(String.format("Found %d converters.", converters.size()));
    }

    private static class Instance {

        private static RisConverters INSTANCE = new RisConverters();
    }

    public static RisConverters getInstance() {
        return Instance.INSTANCE;
    }

    public String convert(final Publication publication) {
        try {
            RisConverter converter;
            logger.debug(String.format("Trying to find converter for type '%s'.",
                                       publication.getClass().getName()));
            //Get the responsible converter
            converter = converters.get(publication.getClass().getName());
            if (converter == null) {
                logger.debug("No converter found...");
                if (publication instanceof PublicationWithPublisher) {
                    logger.debug("Publication is a PublicationWithPublisher, using"
                                 + "converter for this type.");
                    converter =
                    converters.get(PublicationWithPublisher.class.getName());
                } else {
                    logger.debug("Publication is a Publication, using"
                                 + "converter for this type.");
                    converter =
                    converters.get(Publication.class.getName());
                }
            }
            //Create an new instance of this converter.
            converter = converter.getClass().newInstance();
            //Run the converter.
            return converter.convert(publication);

        } catch (InstantiationException ex) {
            logger.warn("Failed to instaniate RIS converter.", ex);
            return null;
        } catch (IllegalAccessException ex) {
            logger.warn("Failed to instaniate RIS converter.", ex);
            return null;
        }
    }
}
