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
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;

import org.apache.log4j.Logger;

/**
 * Application domain class for the Login application.
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
     * @return base data aoject type as String
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Provides an absolute URL (leading slash) into the system login page.
     * It is relative to document root without any constant prefix if there is
     * one configured.
     *
     * XXX This implementation starts with a leading slash and ends with a slash.
     * In previous configurations String urls began without a slash in order
     * to be able to provide a full URL which also contains the context part.
     * Since version 5.2 the context part is handled by (new) dispatcher.
     * The leading slash it API change! It's impacts have to be checked. (2011-02)
     *
     * @return URL for login page as String
     */
    // In old LegacyInitializer
    // LOGIN_PAGE_KEY = page.kernel.login =  register/
    public static String getLoginPageURL() {
        return LOGIN_PAGE_URL;
    }

    /**
     * 
     */
    @Override
    public String getServletPath() {
        return "/login";
    }

    
}
