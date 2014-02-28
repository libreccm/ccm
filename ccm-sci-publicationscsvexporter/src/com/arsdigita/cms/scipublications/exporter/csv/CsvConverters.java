/*
 * Copyright (c) 2014 Jens Pelzetter
 *
 * This library is free software, you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library, if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.scipublications.exporter.csv;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.cms.contenttypes.PublicationWithPublisher;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Registry for the available {@link CsvConverter} implementations.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class CsvConverters {

    private static final Logger LOGGER = Logger.getLogger(CsvConverters.class);

    private final Map<String, CsvConverter> converters = new HashMap<String, CsvConverter>();

    private CsvConverters() {
        //Nothing
    }

    private static class Instance {

        private final static CsvConverters INSTANCE = new CsvConverters();
    }

    public static CsvConverters getInstance() {
        return Instance.INSTANCE;
    }

    public static void register(final CsvConverter converter) {
        getInstance().registerConverter(converter);
    }

    public void registerConverter(final CsvConverter converter) {
        converters.put(converter.getCCMType(), converter);
    }

    public Map<CsvExporterConstants, String> convert(final Publication publication) {
        try {
            CsvConverter converter;
            LOGGER.debug(String.format("Trying to find converter for type '%s'.",
                                       publication.getClass().getName()));
            converter = converters.get(publication.getClass().getName());

            if (converter == null) {
                LOGGER.debug("No converter found...");
                if (publication instanceof PublicationWithPublisher) {
                    LOGGER.debug("Publication is a PublicationWithPublisher, using"
                                 + "converter for this type.");
                    converter = converters.get(PublicationWithPublisher.class.getName());
                } else {
                    LOGGER.debug("Publication is a Publication, using"
                                 + "converter for this type.");
                    converter = converters.get(Publication.class.getName());
                }
            }

            if (converter == null) {
                LOGGER.error(String.format("Converter is null. This should not happen. Publication "
                    + "to convert is of type '%s'.", publication.getClass().getName()));
            }
            
            converter = converter.getClass().newInstance();

            return converter.convert(publication);
        } catch (InstantiationException ex) {
            LOGGER.warn("Failed to instaniate CSV converter.", ex);
            return null;
        } catch (IllegalAccessException ex) {
            LOGGER.warn("Failed to instaniate CSV converter.", ex);
            return null;
        }
    }

}
