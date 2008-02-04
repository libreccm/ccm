/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util.xml;

import org.apache.log4j.Logger;

public class XML {
    
    private static final Logger s_log = Logger.getLogger
        (XMLConfig.class);

    private static XMLConfig s_config;
    
    static XMLConfig getConfig() {
        if (s_config == null) {
            s_config = new XMLConfig();
            s_config.load();
        }
        return s_config;
    }

    public static void setupFactories() {
        setupFactory("javax.xml.parsers.DocumentBuilderFactory",
                     getConfig().getDOMBuilderFactory());
        setupFactory("javax.xml.parsers.SAXParserFactory",
                     getConfig().getSAXParserFactory());
        setupFactory("javax.xml.transform.TransformerFactory",
                     getConfig().getXSLTransformerFactory());
    }
    
    static void setupFactory(String name,
                             Class impl) {
        if (impl != null) {
            if (s_log.isInfoEnabled()) {
                s_log.info("Setting " + name + " to " + impl);
            }
            System.setProperty(name,
                               impl.getName());
        } else {
            if (s_log.isInfoEnabled()) {
                s_log.info("Leaving " + name + " as " +
                           System.getProperty(name));
            }
        }
    }
}
