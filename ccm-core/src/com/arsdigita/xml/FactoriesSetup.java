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
package com.arsdigita.xml;

import org.apache.log4j.Logger;

/**
 * Utility class to configure the FactoriesSetup parsers using Sun's javax.xml 
 * specified classes and methods (in the javax.xml..... packages) and should be
 * invokes as early as possible in the CCM startup process.
 *
 * Parsers rsp. transformers are created using a factory class. There are 2
 * methods available:
 *
 * Static method newinstance()
 *    searches the configuration following 4 steps:
 *    1. Use the <code>javax.xml.parsers.[name]Factory</code> system property
 *    2. Use properties file "lib/jaxp.properties" in the JRE directory
 *    3. Use the Services API, which will look in a file
 *       <code>META-INF/services/javax.xml.parsers.[name]Factory</code>
 *       in jars available to the runtime.
 *    4. Platform default <code>[name]Factory</code> instance
 *
 * Static method newInstance(String factoryClassName, ClassLoader classLoader)
 *    Currently not used by CCM code. Refactoring requirred.
 * C.f.
 * http://www.docjar.com/html/api/javax/xml/parsers/DocumentBuilderFactory.java.html
 * 
 * Previously CCM used to place the desired parser class names as runtime
 * parameters (-D...) into the Tomcat startup script. This method requires a
 * custom Tomcat configuration and constrains all other applications running in 
 * the servlet container to use the same configuration.
 * 
 * The preferred method is the second option of <code>newinstance</code>, but
 * requires to dig deeper into the CCM code.
 *
 * As an <em>intermediate</em> solution the implementation to use is stored in 
 * the configuration registry, read at startup and set as system.properties.
 *
 * Called once by c.ad.core.Initializer at startup and
 * CCMApplicationContextListener.
 *
 * modified by
 * @author pboy
 */


// /////////////////////////////////////////////////////////////////////////////
//
// NOTE: The ServiceProviderInterface as implementet by the JaxP factory 
// class newInstance() follow a specific search order as explained above.
// The META-INF directory MUST be found via ContextClasspathLoader, i.e. must
// be located in WEB-INF/classes/META-INF or in a jar-file at WEB-INF/lib (or
// in one of the locations searched by common class loader which is not useful)
// Therefore is is not possible to switch a factory implementation after
// deployment at runtime or webapp startup, because both locations are not meant
// to be changed after deployment of an application.
// The alternative instantiation using newInstance(ImplementationClass,
// LoaderClass) is no replacement, because developers may use newInstance()
// anyway and probably get an unintended implementation. 
//
// Therefore we must engage a quick'nd dirty way to achieve the goal and mess
// around with the WEB-INF/classes directory, until a better solution will be
// available.
//
// /////////////////////////////////////////////////////////////////////////////



public class FactoriesSetup {
    
    private static final Logger s_log = Logger.getLogger
        (FactoriesSetup.class);

    /* ************     public getter / setter section          ************ */
    
    /**
     * Actually configures the factory classes to use. It calls an internal
     * method which modifies the system.property. 
     */
    public static void setupFactories() {
    //  setupFactory("javax.xml.parsers.DocumentBuilderFactory",
    //               XMLConfig.getConfig().getDOMBuilderFactoryClassname());
    //  setupFactory("javax.xml.parsers.SAXParserFactory",
    //               XMLConfig.getConfig().getSAXParserFactoryClassname());
    //  setupFactory("javax.xml.transform.TransformerFactory",
    //               XMLConfig.getConfig().getXSLTransformerFactoryClassname());
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
        //  System.setProperty(name,
        //                     impl);
        } else {
            if (s_log.isInfoEnabled()) {
                s_log.info("Leaving " + name + " as " +
                           System.getProperty(name));
            }
        }
    }

}
