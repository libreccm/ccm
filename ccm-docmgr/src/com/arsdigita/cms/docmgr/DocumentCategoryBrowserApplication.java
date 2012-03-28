/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.cms.docmgr;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.Application;

/**
 * A repository is the application that provides access to files and
 * folders.
 *
 * @author Stefan Deusch (stefan@arsdigita.com)
 * @author Ron Henderson (ron@arsdigita.com)
 */

public class DocumentCategoryBrowserApplication extends Application {

    private static Logger s_log =
        Logger.getLogger(DocumentCategoryBrowserApplication.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.docmgr.DocumentCategoryBrowserApplication";

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    // pdl constants
    private static final String OWNER = "ownerID";
    private static final String ROOT  = "rootID";

    private DocFolder m_root = null;

    /**
     * Retreives a repository from the database usings its OID.
     *
     * @param oid the OID of the repository
     */
    public DocumentCategoryBrowserApplication(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     */
    public DocumentCategoryBrowserApplication(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructs a repository from the underlying data object.
     */
    public DocumentCategoryBrowserApplication(DataObject obj) {
        super(obj);

    }

    /**
     * This is called when the application is created.
     */
    public static DocumentCategoryBrowserApplication create(String urlName,
                                    String title,
                                    Application parent) {
        DocumentCategoryBrowserApplication app = 
            (DocumentCategoryBrowserApplication) Application.createApplication
            (BASE_DATA_OBJECT_TYPE, urlName, title, parent);

        app.save();

        return app;
    }

    /**
     * Sets the display name of the app.
     */
    //private void setName(String name) {
    //    //setTitle(name);
    //    set("name", name);
    //}

    /**
     * Returns the servletPath part of the URL to the application servlet.
     * (see Servlet API specification or web.URL for more information)
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
     *   <servlet-name>docmgr-categorybrowser</servlet-name>
     *   <servlet-class>com.arsdigita.cms.docmgr.ui.CategoryBrowserServlet</servlet-class>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>docmgr-categorybrowser</servlet-name>
     *   <url-pattern>/docmgr-cat/*</url-pattern>
     * </servlet-mapping>
     *
     * @return ServelPath of the applications servlet
     */
    @Override
    public String getServletPath() {
        return "/docmgr-cat";
    }


}
