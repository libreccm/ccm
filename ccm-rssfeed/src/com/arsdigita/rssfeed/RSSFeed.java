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

package com.arsdigita.rssfeed;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import com.arsdigita.web.Application;
import org.apache.log4j.Logger;

/**
 * Base class of the RSSFeed application (module).
 * 
 */
public class RSSFeed extends Application {

    /** A logger instance to assist debugging.  */
    private static final Logger logger = Logger.getLogger(RSSFeed.class);

    // pdl stuff (constants)
    public static final String BASE_DATA_OBJECT_TYPE 
                               = "com.arsdigita.rssfeed.RSSFeed";
    
   /** Config object containing various parameter    */
    private static final RSSFeedConfig s_config = RSSFeedConfig.getConfig();


    /**
     * Provides the configuration record for client classes.
     *
     * @return A <code>RSSFeedConfig</code> configuration record; it
     * cannot be null
     */
    public static RSSFeedConfig getConfig() {
        return s_config;
    }

    public RSSFeed(DataObject obj) {
        super(obj);
    }

    public RSSFeed(OID oid) {
        super(oid);
    }

//  /*
//   * Application specific method only required if installed in its own
//   * web application context
//   */
//  public String getContextPath() {
//      return "/ccm-rssfeed";
//  }

    /**
     * Returns the path name of the location of the applications servlet/JSP.
     *
     * The methods overwrites the super class to provide an application specific
     * location for servlets/JSP. This is necessary if you whish to install the
     * module (application) along with others in one context. If you install the
     * module into its own context (no longer recommended for versions newer
     * than 1.0.4) you may use a standard location.
     *
     * Usually it is a symbolic name/path, which will be mapped in the web.xml
     * to the real location in the file system. You will use the following
     * kind of code:
     * <servlet>
     *   <servlet-name>rss-files</servlet-name>
     *   <servlet-class>com.arsdigita.web.ApplicationFileServlet</servlet-class>
     *   <init-param>
     *     <!-- where to find the jsp files -->
     *     <param-name>template-path</param-name>
     *     <param-value>/templates/ccm-rssfeed</param-value>
     *   </init-param>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>rssfeed-files</servlet-name>
     *   <url-pattern>/ccm-rssfeed/files/*</url-pattern>
     * </servlet-mapping>
     *
     * @return path name to the applications servlet/JSP
     */
    @Override
    public String getServletPath() {
        return "/ccm-rssfeed/files";
    }
}
