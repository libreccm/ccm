/*
 * Copyright (C) 2010 Peter Boy <pb@zes.uni-bremen.de> All Rights Reserved.
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

package com.arsdigita.ui.login;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;

import org.apache.log4j.Logger;

/**
 * Application domain class for the Login application.
 * 
 * Serves as the main entry point into Login application. Because there are no
 * instance specific functions (there is only a single instance to be installed)
 * it provides mainly required configuration information and service methods
 * for client classes.
 *
 * @author pb
 * @version $Id: Login.java $
 */
public class Login extends Application {

    /** Logger instance for debugging  */
    private static final Logger s_log = Logger.getLogger(Login.class);

    // pdl stuff (constants)
    public static final String BASE_DATA_OBJECT_TYPE
                               = "com.arsdigita.ui.login.Login";

    /** URL stub of Login page in ServletPath format (with leading slash and
     *  without trailing slash                                                */
    // Don't modify without adapting instantiation in Loader class and 
    // updating existing databases (table applications)!
    protected static final String LOGIN_PAGE_URL = "/register";

    /**
     * Constructor retrieving Login from the database usings its OID.
     *
     * @param obj 
     * @throws DataObjectNotFoundException
     */
    public Login(OID oid)
        throws DataObjectNotFoundException {

        super(oid);
    }

    /**
     * Constructs a service domain object from the underlying data object.
     * 
     * @param obj the DataObject
     */
    public Login(DataObject obj) {
        super(obj);
    }


    /**
     * Getter to retrieve the base database object type name
     *
     * @return base data a object type as String
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Service method provides an (absolute) URL into the system login page as
     * servletPath() (leading slash) according servlet API. It is relative to
     * document root without any constant prefix if there is one configured.
     *
     * It is used by other classes to determine the login page.
     * 
     * @return URL for login page as String
     */
    public static String getLoginPageURL() {
        return LOGIN_PAGE_URL;
    }

    /**
     * Returns the path name of the location of the applications servlet/JSP.
     *
     * The method overwrites the super class to provide an application specific
     * location for servlets/JSP. This is necessary if you whish to install the
     * module (application) along with others in one context. If you install the
     * module into its own context (no longer recommended) you may use a 
     * standard location.
     *
     * Usually it is a symbolic name/path, which will be mapped in the web.xml
     * to the real location in the file system. Example:
     * <servlet>
     *   <servlet-name>login</servlet-name>
     *   <servlet-class>com.arsdigita.ui.login.LoginServlet</servlet-class>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>login</servlet-name>
     *   <url-pattern>/login/*</url-pattern>
     * </servlet-mapping>
     *
     * The appended "/*" ensures BaseServlet will find additional JSP's.
     * 
     * @return path name to the applications servlet/JSP
     */
    @Override
    public String getServletPath() {
        return "/login";
    }

    
}
