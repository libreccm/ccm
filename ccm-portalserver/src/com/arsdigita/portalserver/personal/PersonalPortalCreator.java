/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.personal;

import com.arsdigita.web.Application;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.web.URL;

/**
 * <p><strong>Experimental</strong></p>
 *
 * This doesn't do anything right now.  We would use it if we wanted
 * to present the user with an initial configuration UI for creating
 * personal portals.
 *
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: PersonalPortalCreator.java  pboy $
 */
public class PersonalPortalCreator extends Application {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workspace.personal.PersonalWorkspaceCreator";

    /**
     * Constructor
     * 
     * @param dataObject 
     */
//  protected PersonalPortalCreator(DataObject dataObject) {
    public PersonalPortalCreator(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * 
     * @return 
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Returns the path name of the location of the applications servlet/JSP.
     *
     * Application implementations may overwrite this method to provide an
     * application specific location, especially if an application (module) is
     * to be installed along with others in one context.
     *
     * If you install the module into its own context you may use a standard
     * location. In most cases though all modules (applications) of an
     * webapplication should be installed into one context.
     *
     * Frequently it is a symbolic name/path, which will be mapped in the web.xml
     * to the real location in the file system. Example:
     * <servlet>
     *   <servlet-name>applicationName-files</servlet-name>
     *   <servlet-class>com.arsdigita.web.ApplicationFileServlet</servlet-class>
     *   <init-param>
     *     <param-name>template-path</param-name>
     *     <param-value>/templates/ccm-applicationName</param-value>
     *   </init-param>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>applicationName-files</servlet-name>
     *   <url-pattern>/ccm-applicationName/files/*</url-pattern>
     * </servlet-mapping>
     *
     * NOTE: According to Servlet API the path always starts with a leading '/'
     * and includes either the servlet name or a path to the servlet, but does 
     * not include any extra path information or a query string. Returns an
     * empry string ("") is the servlet used was matched using the "/*" pattern.
     * 
     * @return path name to the applications servlet/JSP
     */
    @Override
    public String getServletPath() {
        return URL.SERVLET_DIR + "/personal-portal-creator";
    }

}
