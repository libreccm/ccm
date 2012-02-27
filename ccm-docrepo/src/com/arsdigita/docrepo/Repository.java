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
 *
 */
package com.arsdigita.docrepo;


// import com.arsdigita.docrepo.util.GlobalizationUtil;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
//import com.arsdigita.kernel.SiteNode;
import com.arsdigita.kernel.User;
// import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
//import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
//import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
//import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.UncheckedWrapperException;

import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
//import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * A repository is the application that provides access to files and
 * folders.
 *
 * @author Stefan Deusch (stefan@arsdigita.com)
 * @author Ron Henderson (ron@arsdigita.com)
 */

public class Repository extends Application {

    /** Logger instance for debugging purpose.  */
    private static Logger s_log = Logger.getLogger(Repository.class);

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.docrepo.Repository";

    // pdl constants
    private static final String ACTION      = "action";
    public  static final String CONTENT     = "content";
    public  static final String DESCRIPTION = "description";
    private static final String DURATION    = "duration";
    private static final String FOLDER_ID   = "folderID";
    public  static final String IS_FOLDER   = "isFolder";
    private static final String LAST_MODIFIED_DATE = "lastModifiedDate";
    private static final String MIME_TYPE_LABEL = "mimeTypeDescription";
    public  static final String NAME        = "name";
    private static final String OBJECT_ID   = "objectID";
    public  static final String PARENT      = "parent";
    private static final String PARTY_ID    = "partyID";
    public  static final String PATH        = "path";
    public  static final String SIZE        = "size";
    public  static final String TYPE        = "mimeType";
    private static final String USER_ID     = "userID";
    private static final String OWNER = "ownerID";
    private static final String ROOT  = "rootID";

    String REPOSITORIES_MOUNTED = "subscribedRepositories";

    // MIME type constants

    public  static final String TEXT_PLAIN  = com.arsdigita.mail.Mail.TEXT_PLAIN;
    public  static final String TEXT_HTML   = com.arsdigita.mail.Mail.TEXT_HTML;

    private Folder m_root = null;

    /**
     * 
     * @return
     */
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Constructor retrieves a repository from the database usings its OID.
     *
     * @param oid the OID of the repository
     */
    public Repository(OID oid) throws DataObjectNotFoundException {
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
    public Repository(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructs a repository from the underlying data object.
     */
    public Repository(DataObject obj) {
        super(obj);

        if (obj.isNew()) {
            s_log.info("Create root folder for repository");

            // Generate a unique name for the root folder based on the
            // primary key of the repository.

            m_root = new Folder(getID().toString(), null);
            m_root.save();
            obj.set(ROOT, m_root.getID());

            s_log.info("Created root folder for repository " + getID());
        }
    }

    private boolean m_wasNew;

    /**
     *
     */
    @Override
    protected void beforeSave() {
        if (isNew()) {
            m_wasNew = true;
        }

        super.beforeSave();
    }

    /**
     * Grant write permission to the Portal participants.
     */
    @Override
    protected void afterSave() {
        super.afterSave();

        if (m_wasNew) {
            KernelExcursion excursion = new KernelExcursion() {
                    protected void excurse() {
                        setParty(Kernel.getSystemParty());

           //           Assert.assertNotNull(m_root, "Folder m_root");
                        Assert.exists(m_root, "Folder m_root");

                        PermissionService.setContext(m_root, Repository.this);
                    }
                };
            excursion.run();
        }
    }

    /**
     * This is called when the application is created.
     */
    public static Repository create(String urlName,
                                    String title,
                                    Application parent) {
        Repository repository = (Repository) Application.createApplication
            (BASE_DATA_OBJECT_TYPE, urlName, title, parent);

        repository.save();

        return repository;
    }

    /**
     * Sets the display name of the repository.
     */
    private void setName(String name) {
        set(NAME, name);
    }

    /**
     * Sets the owner id of this repository.
     */
    private void setOwner(BigDecimal ownerID) {
        set(OWNER, ownerID);
    }




    /**
     * Convenience method to retrieve a resource (file or folder) by
     * absolute path name.
     *
     * @returns a Resource or null of no resource exists with the
     * specified absolute path.
     */
    public static Resource retrieveResource(String absPath) {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     * @param party
     */
    private static void assertParty(Party party) {
        BigDecimal id = party.getID();

        if (id == null) {
            throw new RuntimeException("User not peristent");
        }
    }

    /**
     * Returns the party owning the repository.
     * @return the party owning the repository.
     */
    public Party getOwner() {
        BigDecimal id = (BigDecimal)get(OWNER);
        Party party = null;

        if (id != null) {
            try {
                party = User.retrieve(id);
            } catch (DataObjectNotFoundException e1) {
                // try to load a group
                try {
                    party = new Group(id);
                } catch (DataObjectNotFoundException e2) {
                    throw new RuntimeException("No User or Group found.");
                }
            }
        }

        return party;
    }

    /**
     * @return the root file folder for this repository
     */
    public Folder getRoot() {
        BigDecimal id = (BigDecimal)get(ROOT);
        Folder root = null;

        try {
            root = new Folder(id);
        } catch (DataObjectNotFoundException e) {
            throw new UncheckedWrapperException
                ("Repository root folder does not exist");
        }

        return root;
    }

    /**
     * 
     * @return
     */
    public static ResourceImplCollection getRecentlyModifiedDocuments() {

        HttpServletRequest req = Web.getRequest();
        Repository rep = getCurrentRepository(req);
        DataCollection dataCollection = SessionManager
                    .getSession().retrieve(ResourceImpl.BASE_DATA_OBJECT_TYPE);

        dataCollection.addFilter(dataCollection.getFilterFactory().startsWith
            ("path", rep.getRoot().getPath(), true));
        dataCollection.addEqualsFilter("isFolder", Boolean.FALSE);

        ResourceImplCollection rCollection =
            new ResourceImplCollection(dataCollection);
        return rCollection;
    }

    /**
     * 
     * @param rep
     * @return
     */
    public static ResourceImplCollection getRecentlyModifiedDocuments(Repository rep) {
      DataCollection dataCollection =
       SessionManager.getSession().retrieve(ResourceImpl.BASE_DATA_OBJECT_TYPE);
      dataCollection.addFilter(dataCollection.getFilterFactory().startsWith
          ("path", rep.getRoot().getPath(), true));
      dataCollection.addEqualsFilter("isFolder", Boolean.FALSE);

      ResourceImplCollection rCollection =
          new ResourceImplCollection(dataCollection);
      return rCollection;
    }

    /**
     * 
     * @param req
     * @return
     */
    public static Repository getCurrentRepository(HttpServletRequest req) {
        Application app = Application.getCurrentApplication(req);
        if(app instanceof Repository) {
            return (Repository) app;
        }
        return null;
    }
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
     *   <servlet-name>docrepo</servlet-name>
     *   <servlet-class>com.arsdigita.docrepo.RepositoryServlet</servlet-class>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>docrepo</servlet-name>
     *   <url-pattern>/docrepo/*</url-pattern>
     * </servlet-mapping>
     *
     * @return ServelPath of the applications servlet
     */
    @Override
    public String getServletPath() {
        return "/docrepo/";
    }

}
