/*
 * Copyright (c) 2010 Jens Pelzetter
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
package com.arsdigita.cms.scipublications.exporter.bibtex.builders;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.log4j.Logger;

/**
 * This class provides a central access point to all available 
 * {@link BibTeXBuilder}s. The {@link ServiceLoader} is used to find all 
 * available <code>BibTeXBuilder</code>s.
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class BibTeXBuilders {

    private static final Logger logger = Logger.getLogger(BibTeXBuilders.class);
    /**
     * Map which associates the builders with their supported type.
     */
    private Map<String, BibTeXBuilder> builders =
                                       new HashMap<String, BibTeXBuilder>();

    /**
     * Private constructor to ensure that no instances of this class can be created.
     */
    private BibTeXBuilders() {
        //Nothing
    }

    /**
     * Static inner class which keeps the one and only instance of this class.
     */
    private static class Instance {
        private static final BibTeXBuilders INSTANCE = new BibTeXBuilders();
    }        

    /**
     *
     * @return The instance of this class.
     */
    public static BibTeXBuilders getInstance() {
        return Instance.INSTANCE;
    }
    
    public static void register(final BibTeXBuilder builder) {
        getInstance().registerBibTeXBuilder(builder);
    }
    
    public void registerBibTeXBuilder(final BibTeXBuilder builder) {
        builders.put(builder.getBibTeXType(), builder);
    }

    /**
     * Retrieves a builder for a BibTeX type.
     *
     * @param type The BibTeX type.
     * @return A BibTeX builder for the provided type, or <code>null</code>
     * if no builder for provided type is found.
     */
    public BibTeXBuilder getBibTeXBuilderForType(final String type) {
        if (builders.containsKey(type)) {
            try {
                return builders.get(type).getClass().newInstance();
            } catch (InstantiationException ex) {
                logger.warn(String.format("Failed to create BibTeXBuilder "
                                          + "for type '%s'.", type),
                            ex);
                return null;
            } catch (IllegalAccessException ex) {
                logger.warn(String.format("Failed to create BibTeXBuilder "
                                          + "for type '%s'.", type),
                            ex);
                return null;
            }
        } else {
            return null;
        }
    }
}
