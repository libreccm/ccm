/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */
package com.arsdigita.london.terms;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

/**
 * Base class of the ccm-ldn-terms application (module)
 * 
 */
public class Terms extends Application {

    private static final Logger logger = Logger.getLogger(Terms.class);
    public static final String XML_NS =
                               "http://xmlns.redhat.com/london/terms/1.0";
    public static final String XML_PREFIX = "terms";
    private static TermsConfig s_config = new TermsConfig();

    static {
        logger.debug("Static initalizer starting...");
        s_config.load();
        logger.debug("Static initalizer finished.");
    }

    public static TermsConfig getConfig() {
        return s_config;
    }

    public Element newElement(String name) {
        return new Element(XML_PREFIX + ":" + name,
                           XML_NS);
    }
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.london.terms.Terms";

    public Terms(DataObject obj) {
        super(obj);
    }

    public Terms(OID oid) {
        super(oid);
    }

//  /*
//   * Application specific method only required if installed in its own
//   * web application context
//   */
//  public String getContextPath() {
//      return "/ccm-ldn-terms";
//  }
    /**
     * Returns the path name of the location of the applications servlet/JSP.
     *
     * The method overwrites the super class to provide an application specific
     * location for servlets/JSP. This is necessary if you whish to install the
     * module (application) along with others in one context. If you install the
     * module into its own context (no longer recommended for versions newer
     * than 1.0.4) you may use a standard location.
     *
     * Usually it is a symbolic name/path, which will be mapped in the web.xml
     * to the real location in the file system. Example:
     * <servlet>
     *   <servlet-name>terms-files</servlet-name>
     *   <servlet-class>com.arsdigita.web.ApplicationFileServlet</servlet-class>
     *   <init-param>
     *     <param-name>template-path</param-name>
     *     <param-value>/templates/ccm-ldn-terms</param-value>
     *   </init-param>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>terms-files</servlet-name>
     *   <url-pattern>/ccm-ldn-terms/files/*</url-pattern>
     * </servlet-mapping>
     *
     * @return path name to the applications servlet/JSP
     */
    public String getServletPath() {
        // sufficient if running in its own web application context ccm-ldn-terms
        // return "/files";
        return "/ccm-ldn-terms/files";
    }
}
