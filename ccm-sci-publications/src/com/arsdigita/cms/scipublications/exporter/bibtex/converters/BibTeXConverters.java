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
package com.arsdigita.cms.scipublications.exporter.bibtex.converters;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;

/**
 * Provides easy access to all available BibTeX converters.
 *
 * @author Jens Pelzetter
 */
public class BibTeXConverters {

    private static final Logger logger =
                                Logger.getLogger(BibTeXConverters.class);
    /**
     * Associates the available converters with their supported CCM types.
     */
    private Map<String, BibTeXConverter> converters =
                                         new HashMap<String, BibTeXConverter>();

    /**
     * Finds all available {@link BibTeXConverter}s and puts them into the
     * map.
     */
    private BibTeXConverters() {
        logger.debug("Loading BibTeX converters...");
        ServiceLoader<BibTeXConverter> converterServices;

        converterServices = ServiceLoader.load(BibTeXConverter.class);

        for (BibTeXConverter converter : converterServices) {
            logger.debug(String.format("Found converter for CCM type '%s'...",
                                       converter.getCcmType()));
            converters.put(converter.getCcmType(), converter);
        }
        logger.debug(String.format("Found %d converters.", converters.size()));
    }

    private static class Instance {

        private static BibTeXConverters INSTANCE = new BibTeXConverters();
    }

    /**
     * @return The instance of this class.
     */
    public static BibTeXConverters getInstance() {
        return Instance.INSTANCE;
    }

    /**
     * Converts the provided publication to BibTeX using the responsible
     * converter. If no converter is found, the
     * {@link PublicationWithPublisherConverter} or the
     * {@link PublicationConverter} are used, depending one the base type of
     * the publication.
     *
     * @param publication The publication to convert.
     * @return The publication converted to BibTeX.
     */
    public String convert(final Publication publication) {
        try {
            BibTeXConverter converter;
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
            logger.warn("Failed to instaniate BibTeX converter.", ex);
            return null;
        } catch (IllegalAccessException ex) {
            logger.warn("Failed to instaniate BibTeX converter.", ex);
            return null;
        }
    }
}
