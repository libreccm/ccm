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

package com.arsdigita.london.navigation;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;


import com.arsdigita.web.Application;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;


public class Navigation extends Application {

    private static final Logger logger = Logger.getLogger(Navigation.class);

    public static final String NAV_NS =
        "http://ccm.redhat.com/london/navigation";
    public static final String NAV_PREFIX = "nav";
    public static final String OID = "oid";

    public static final String CAT_ID_REDIRECT = "categoryID";

    private static NavigationConfig s_config = new NavigationConfig();
    private static NavigationContext s_context = new NavigationContext();

    static {
        logger.debug("Static initalizer starting...");
        s_config.load();
        logger.debug("Static initalizer finished.");
    }

    public static NavigationConfig getConfig() {
        return s_config;
    }

    public static NavigationContext getContext() {
        return s_context;
    }

    public static Element newElement(String name) {
        return new Element(NavigationConstants.NAV_PREFIX + ":" + name,
                           NavigationConstants.NAV_NS);
    }
    
    public static String redirectURL(OID oid) {
        ParameterMap map = new ParameterMap();
        map.setParameter( NavigationConstants.OID, oid.toString() );

        URL here = Web.getContext().getRequestURL();

        return (new URL(here.getScheme(),
                        here.getServerName(),
                        here.getServerPort(),
                        "",
                        "",
                        "/redirect/", map )).toString();
    }

    public static String redirectURL(OID oid, 
                                     ParameterMap inMap) {
        
        ParameterMap map = null;
        try {
            map = (ParameterMap) inMap.clone();
        } catch (CloneNotSupportedException e) {
            map = inMap;
        }
        map.setParameter( NavigationConstants.OID, oid.toString() );

        return URL.there( Web.getRequest(), "/redirect/", map ).toString();
    }


    public static final String BASE_DATA_OBJECT_TYPE 
        = "com.arsdigita.london.navigation.Navigation";

    public Navigation(DataObject obj) {
        super(obj);
    }

    public Navigation(OID oid) {
        super(oid);
    }

    /*
    public String getContextPath() {
        return "ccm-ldn-navigation";
    }
    */

    /**
     * Returns the path to the location of the applications servlet/JSP.
     *
     * The methods overwrites the super class to provide a application specific
     * location for servlets/JSP.
     *
     * Usually it is a symbolic name/path, which will be mapped in the web.xml
     * to the real location in the file system. You will find the following
     * kind of code:
     * <servlet>
     *   <servlet-name>navigation-files</servlet-name>
     *   <servlet-class>com.arsdigita.web.ApplicationFileServlet</servlet-class>
     *   <init-param>
     *     <param-name>template-path</param-name>
     *     <param-value>/templates/ccm-ldn-navigation</param-value>
     *   </init-param>
     *   <init-param>
     *     <param-name>file-resolver</param-name>
     *     <param-value>com.arsdigita.london.navigation.NavigationFileResolver
     *     </param-value>
     *   </init-param>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>navigation-files</servlet-name>
     *   <url-pattern>/ccm-ldn-navigation/files/*</url-pattern>
     * </servlet-mapping>
     *
     * @return path to the applications servlet/JSP
     */
    public String getServletPath() {
        //return "/files";
        return "/ccm-ldn-navigation/files";
    }

}
