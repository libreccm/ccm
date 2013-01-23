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
import com.arsdigita.web.*;

import java.math.BigDecimal;

import org.apache.log4j.Logger;


/**
 * Application domain class for the CMS module user entry page (content-center)
 *
 * @author pb
 * @version $Id: ContentCenter.java $
 */
public class ContentCenter extends Application {

    /** A logger instance, primarily to assist debugging .                    */
    private static final Logger s_log = Logger.getLogger(ContentSection.class);

    // pdl stuff (constants)
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.cms.ContentCenter";

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
    public ContentCenter(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor retrieving the contained <code>DataObject</code> from the
     * persistent storage mechanism with an <code>OID</code> specified by id.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     */
    public ContentCenter(BigDecimal key)  throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, key));
    }

    /**
     * Constructs a repository from the underlying data object.
     */
    public ContentCenter(DataObject dataObject) {
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
    public static ContentCenter create(String urlName,
                                       String title,
                                       Application parent) {

        ContentCenter app =
            (ContentCenter) Application.createApplication
            (BASE_DATA_OBJECT_TYPE, urlName, title, parent);

        app.save();

        return app;
    }


    /**
     * Fetch the URL of the CMS ContentCenter. 
     * 
     * Currently only one Content Center application installed is allowed!
     * Therefore we simply may return the URL used to load and initialise the
     * Content Center.
     * 
     * @return The URL of the CMS ContentCenter (currently including trailing slash)
     */
    public static String getURL() {
        // quick 'nd dirty!
        return "/"+PACKAGE_KEY;

    //  Doesn't work as expected
    //  see c.ad.ui.login.UserInfo for a working (hopefully) example.
    //  ApplicationCollection apps = Application
    //                               .retrieveAllApplications(BASE_DATA_OBJECT_TYPE);
    //  if (apps.next()) {
    //      // Note: Currently only one Content Center application is allowed!
    //      s_log.error("Instance of ContentCenter found!");
    //      return apps.getPrimaryURL();
    //  } else {
    //      s_log.error("No instance of ContentCenter could be found!");
    //      return null;            
    //  }
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
