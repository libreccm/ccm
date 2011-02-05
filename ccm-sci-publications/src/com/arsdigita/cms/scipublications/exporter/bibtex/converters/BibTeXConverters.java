package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author jensp
 */
public class BibTeXConverters {

    private static final Logger logger = Logger.getLogger(BibTeXConverters.class);

    private Map<String, BibTeXConverter> converters =
                                         new HashMap<String, BibTeXConverter>();

    private BibTeXConverters() {
        logger.debug("Loading BibTeX converters...");
        ServiceLoader<BibTeXConverter> converterServices;

        converterServices = ServiceLoader.load(BibTeXConverter.class);

        for(BibTeXConverter converter : converterServices) {
            logger.debug(String.format("Found converter for CCM type '%s'...",
                    converter.getCcmType()));
            converters.put(converter.getCcmType(), converter);
        }
        logger.debug(String.format("Found %d converters.", converters.size()));
    }

    private static class Instance {

        private static BibTeXConverters INSTANCE = new BibTeXConverters();
    }

    public static BibTeXConverters getInstance() {
        return Instance.INSTANCE;
    }

    public String convert(final Publication publication) {
        try {
            BibTeXConverter converter;
            logger.debug(String.format("Trying to find converter for type '%s'.",
                                       publication.getClass().getName()));
            converter = converters.get(publication.getClass().getName());
            if (converter == null) {
                logger.debug("No converter found...");
                if (publication instanceof PublicationWithPublisher) {
                    logger.debug("Publication is a PublicationWithPublisher, using" +
                                 "converter for this type.");
                    converter =
                    converters.get(PublicationWithPublisher.class.getName());
                } else {
                    logger.debug("Publication is a Publication, using" +
                                 "converter for this type.");
                    converter =
                    converters.get(Publication.class.getName());
                }
            }
            converter = converter.getClass().newInstance();
            return converter.convert(publication);

        } catch (InstantiationException ex) {
            logger.warn("Failed to instaniate BibTeX converter.", ex);
            return null;
        } catch (IllegalAccessException ex) {
            logger.warn("Failed to instaniate BibTeX converter.", ex);
            return null;
        }
    }
}
