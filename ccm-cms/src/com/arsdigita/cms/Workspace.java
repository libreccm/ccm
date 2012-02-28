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
package com.arsdigita.cms;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.URL;

import java.math.BigDecimal;

import org.apache.log4j.Logger;


/**
 * Application domain class for the CMS module user entry page (content-center)
 *
 * @author pb
 * @version $Id: Workspace.java $
 */
public class Workspace extends Application {

    private static final Logger s_log = Logger.getLogger(ContentSection.class);

    // pdl stuff (constants)
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.Workspace";

    // general constants
    public static final String PACKAGE_KEY = "content-center";
    public static final String INSTANCE_NAME = "Content Center";
//  public static final String DISPATCHER_CLASS =
//                             "com.arsdigita.cms.dispatcher.ContentCenterDispatcher";

    /**
     * Constructor retrieving a workspace from the database usings its OID.
     *
     * @param oid the OID of the workspace (content-center)
     * @throws DataObjectNotFoundException
     */
    public Workspace(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor retrieving the contained <code>DataObject</code> from the
     * persistent storage mechanism with an <code>OID</code> specified by id.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     */
    public Workspace(BigDecimal key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    /**
     * Constructs a repository from the underlying data object.
     */
    public Workspace(DataObject dataObject) {
        super(dataObject);
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
     * This is called when the application is created.
     */
    public static Workspace create(String urlName,
                                   String title,
                                   Application parent) {

        Workspace app =
            (Workspace) Application.createApplication
            (BASE_DATA_OBJECT_TYPE, urlName, title, parent);

        app.save();

        return app;
    }

    /**
     * Returns an instance of the Workspace application. There must not more
     * than one instance exist. May return null.
     */
    public static Application getInstance() {
        ApplicationType workspaceType = ApplicationType.
            retrieveApplicationTypeForApplication(BASE_DATA_OBJECT_TYPE);
        if ( workspaceType == null ) { return null; }

        ApplicationCollection apps = Application.retrieveAllApplications();
        apps.addEqualsFilter("resourceType.id", workspaceType.getID());
        if ( !apps.next() ) { return null; }

        Application result = apps.getApplication();
        apps.close();
        return result;
    }

    /**
     * Fetch the location (URL) of the CMS Workspace. There must not more than
     * one instance exist. 
     * 
     * @return The URL of the CMS Workspace (currently including trailing slash)
     */
    public static String getURL() {
        
        Application app = Workspace.getInstance(); 
        if (app == null) {
            return null;
        } else {
            String url = (String) app.getPrimaryURL();
            return url;
        }
    }

    /**
     * Returns the path name of the location of the applications servlet/JSP.
     *
     * Application implementations should overwrite this method to provide an
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
     *   <servlet-name>content-center</servlet-name>
     *   <servlet-class>com.arsdigita.cms.WorkspaceServlet</servlet-class>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>content-center</servlet-name>
     *   <url-pattern>/ccm-applicationName/content-center/*</url-pattern>
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
        return URL.SERVLET_DIR + "/content-center";
    }

}
