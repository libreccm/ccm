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

/**
 * Utility class to configure the XML parsers using Sun's javax.xml specified
 * classes and methods (in the javax.xml..... packages).
 * 
 * Currently CCM uses a quite simple but rather thumb method of configuration.
 * It places the desired parser class names into the system environment
 * (previously by startup script, now by setProperties), where they are
 * picked up by the Sun provided classes.
 * 
 * This method contrains all programs in a JVM (e.g. all instances of CCM in
 * a servlet container) to use the same configuration. Other methods are
 * available but we have to dig deeper into the CCM code.
 *
 * Called once by c.ad.core.Initializer at startup.
 *
 * modified by
 * @author pboy
 */
public class XML {
    
    private static final Logger s_log = Logger.getLogger
        (XML.class);

    /* ************     public getter / setter section          ************ */
    
    /**
     * Actually configures the factory classes to use. It calls an internal
     * method which modifies the system.property. 
     */
    public static void setupFactories() {
        setupFactory("javax.xml.parsers.DocumentBuilderFactory",
                     XMLConfig.getConfig().getDOMBuilderFactoryClassname());
        setupFactory("javax.xml.parsers.SAXParserFactory",
                     XMLConfig.getConfig().getSAXParserFactoryClassname());
        setupFactory("javax.xml.transform.TransformerFactory",
                     XMLConfig.getConfig().getXSLTransformerFactoryClassname());
    }
    
    /* ************     internal worker methods section          ************ */

    /**
     * Actually configures the factories to use by setting the system.properties
     * 
     * ToDo: Use an alternative Factory constructor of javax.xml. ... (e.g.
     * DocumentBuilderFactory) which directly accepts a classname and a
     * class loader, so we do not depend on a system wide configuration.
     *
     * @param name  the system property name to set according to the javax.xml spec.
     * @param impl  the value of the class name of the factory to use
     */
    static void setupFactory(String name,
                             String impl) {
        if (impl != null) {
            if (s_log.isInfoEnabled()) {
                s_log.info("Setting " + name + " to " + impl);
            }
            System.setProperty(name,
                               impl);
        } else {
            if (s_log.isInfoEnabled()) {
                s_log.info("Leaving " + name + " as " +
                           System.getProperty(name));
            }
        }
    }
}
