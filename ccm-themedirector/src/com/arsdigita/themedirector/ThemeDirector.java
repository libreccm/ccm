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

package com.arsdigita.themedirector;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

/**
 * Base class of the ccm-themedirector application (module)
 * 
 */
public class ThemeDirector extends Application {

    private static final Logger s_log = Logger.getLogger(ThemeDirector.class);

    public static final String DEFAULT_THEME = "defaultTheme";

    public static final String BASE_DATA_OBJECT_TYPE  =
                               "com.arsdigita.themedirector.ThemeDirector";

    public static ThemeDirectorConfig s_config = new ThemeDirectorConfig();

    static {
        s_log.debug("Static initalizer starting...");
        s_config.load();
        s_log.debug("Static initalizer finished.");
    }

    public static ThemeDirectorConfig getConfig() {
        return s_config;
    }

    public ThemeDirector(DataObject obj) {
        super(obj);
    }

    public ThemeDirector(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }    

    public Theme getDefaultTheme() {
        DataObject dObj = (DataObject) get( DEFAULT_THEME );
        if( null == dObj ) return null;

        return new Theme( dObj );
    }

    public void setDefaultTheme( Theme theme ) {
        set( DEFAULT_THEME, theme );
    }

//  /*
//   * Application specific method only required if installed in its own
//   * web application context (which is deprecated now as of version 6.5)
//   */
//  public String getContextPath() {
//      return "/ccm-ldn-theme";
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
     *   <servlet-name>theme-files</servlet-name>
     *   <servlet-class>com.arsdigita.web.ApplicationFileServlet</servlet-class>
     *   <init-param>
     *     <param-name>template-path</param-name>
     *     <param-value>/templates/ccm-ldn-theme</param-value>
     *   </init-param>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>theme-files</servlet-name>
     *   <url-pattern>/ccm-ldn-theme/files/*</url-pattern>
     * </servlet-mapping>
     *
     * @return path name to the applications servlet/JSP
     */
    public String getServletPath() {
        // sufficient if executed in its own web application context (ccm-ldn-theme)
        return "/theme-files";
        // return "/ccm-ldn-theme/files";
    }
}
