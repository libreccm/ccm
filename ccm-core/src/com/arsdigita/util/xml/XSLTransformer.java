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

public final class XSLTransformer {
    public static final String RESIN =
        "com.caucho.xsl.Xsl";
    public static final String JD_XSLT =
        "jd.xml.xslt.trax.TransformerFactoryImpl";
    public static final String XSLTC =
        "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";
    public static final String SAXON =
        "com.icl.saxon.TransformerFactoryImpl";
    public static final String XALAN =
        "org.apache.xalan.processor.TransformerFactoryImpl";

    public final static String get(String key) {

        // UGLY style, but sufficient for a temporay solution

        // Defined values: saxon (default)|jd.xslt|resin|xalan|xsltc
        if(key.toLowerCase().equals("xsltc"))   return XSLTC;
        if(key.toLowerCase().equals("xalan"))   return XALAN ;
        if(key.toLowerCase().equals("resin"))   return RESIN;
        if(key.toLowerCase().equals("jd.xslt")) return JD_XSLT;
        // return defaultValue
        return SAXON;

    }
}
