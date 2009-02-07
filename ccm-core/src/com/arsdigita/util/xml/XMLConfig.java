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

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

import org.apache.log4j.Logger;

/**
 * A configuration record for the configuration of the XML factories:
 * - Document Builder
 * - Sax Parser
 * - XSL Transformer
 *
 */
public final class XMLConfig extends AbstractConfig {

    public final static String versionId =
        "$Id: XMLConfig.java 1393 2006-11-28 09:12:32Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger
        (XMLConfig.class);

    private static XMLConfig s_config;

    /**
     * Returns the singleton configuration record for the XML factory
     * configuration.
     *
     * @return The <code>XMLConfig</code> record; it cannot be null
     */
    public static final synchronized XMLConfig getConfig() {
        if (s_config == null) {
            s_config = new XMLConfig();
            // read values from the persistent storage
            s_config.load();
        }

        return s_config;
    }

    // Obviously unfinished work and not helpful here. In any case we
    // have to handle just class names.
    // private final AliasedClassParameter m_xfmr;
    // private final AliasedClassParameter m_builder;
    // private final AliasedClassParameter m_parser;
    private final Parameter m_xfmr     = new StringParameter
                ("waf.xml.xsl_transformer", Parameter.REQUIRED, "saxon");

    private final Parameter m_builder  = new StringParameter
                ("waf.xml.dom_builder",     Parameter.REQUIRED, "xerces");

    private final Parameter m_parser   = new StringParameter
                ("waf.xml.sax_parser",      Parameter.REQUIRED, "xerces");



    /**
     * Constructs an empty XMLConfig object following the singelton pattern.
     *
     * They are meant as an singelton pattern (with private constructor), but
     * it does not work with the associated classes AbstractConfig and
     * ConfigRegistry because they can currently not deal with a private constructor
     */

    // private XMLConfig() {
    public XMLConfig() {
        
        register(m_xfmr);
        register(m_builder);
        register(m_parser);

        loadInfo();
    }

    
    /* ************     public getter / setter section          ************ */


    /**
     * Returns the XSL Transformer factory class name to use
     *
     * @return String XSL Transformer factory class name
     */
    public final String getXSLTransformerFactoryClassname() {
        return XSLTransformer.get( (String) get(m_xfmr) );
    }

    /**
     * Returns the Document Builder factory class name to use
     *
     * @return String Document Builder factory class name
     */
    public final String getDOMBuilderFactoryClassname() {
        return DOMBuilder.get( (String) get(m_builder) );
    }

    /**
     * Returns the Sax Parser factory class name to use
     *
     * @return String Sax Parser factory class name
     */
    public final String getSAXParserFactoryClassname() {
        return SAXParser.get( (String) get(m_parser) );
    }

}
