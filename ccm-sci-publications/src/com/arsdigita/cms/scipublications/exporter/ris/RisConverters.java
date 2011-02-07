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
package com.arsdigita.cms.scipublications.exporter.ris;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;

/**
 * Central access point to retrieve {@link RisConverter}s.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class RisConverters {

    private static final Logger logger = Logger.getLogger(RisConverters.class);
    /**
     * Associates the available converters with their supported CCM types.
     */
    private Map<String, RisConverter> converters =
                                      new HashMap<String, RisConverter>();

    /**
     * The constructor loads all available implementations of the
     * {@link RisConverter} interface using the {@link ServiceLoader}.
     */
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
     * Converts a {@link Publication} content item to an RIS reference. Tries
     * to find a suitable converter for the type of the publication. If no
     * converter to the special type is found, the
     * {@link PublicationWithPublisherConverter} or the
     * {@link PublicationConverter} is used, depending on the base class of the
     * publication item.
     *
     * @param publication The publication to convert.
     * @return The publication as RIS reference.
     */
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
