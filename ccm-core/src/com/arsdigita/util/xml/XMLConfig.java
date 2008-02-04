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
import com.arsdigita.util.AliasedClassParameter;
import com.arsdigita.util.parameter.Parameter;

import org.apache.log4j.Logger;

/**
 */
public final class XMLConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger
        (XMLConfig.class);

    private final AliasedClassParameter m_xfmr;
    private final AliasedClassParameter m_builder;
    private final AliasedClassParameter m_parser;

    public XMLConfig() {
        m_xfmr = new AliasedClassParameter
            ("waf.xml.xsl_transformer",
             Parameter.OPTIONAL,
             null);

        m_xfmr.addAlias("jd.xslt", XSLTransformer.JD_XSLT);
        m_xfmr.addAlias("xsltc", XSLTransformer.XSLTC);
        m_xfmr.addAlias("xalan", XSLTransformer.XALAN);
        m_xfmr.addAlias("saxon", XSLTransformer.SAXON);
        m_xfmr.addAlias("resin", XSLTransformer.RESIN);



        m_builder = new AliasedClassParameter
            ("waf.xml.dom_builder",
             Parameter.OPTIONAL,
             null);

        m_builder.addAlias("xerces", DOMBuilder.XERCES);
        m_builder.addAlias("resin", DOMBuilder.RESIN);



        m_parser = new AliasedClassParameter
            ("waf.xml.sax_parser",
             Parameter.OPTIONAL,
             null);

        m_parser.addAlias("xerces", SAXParser.XERCES);
        m_parser.addAlias("resin", SAXParser.RESIN);

        register(m_xfmr);
        register(m_builder);
        register(m_parser);

        loadInfo();
    }

    public final Class getXSLTransformerFactory() {
        return (Class)get(m_xfmr);
    }

    public final Class getDOMBuilderFactory() {
        return (Class)get(m_builder);
    }

    public final Class getSAXParserFactory() {
        return (Class)get(m_parser);
    }

}
