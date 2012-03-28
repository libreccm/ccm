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

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
// import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
// import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.UncheckedWrapperException;

import com.arsdigita.web.Application;
import com.arsdigita.web.Web;
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

    private static Logger s_log =
        Logger.getLogger(Repository.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.docmgr.Repository";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    // pdl constants
    public static final String OWNER = "ownerID";
    public static final String ROOT  = "rootID";

    private DocFolder m_root = null;
    private static String s_repositoryRoot = null;

    /**
     * Retreives a repository from the database usings its OID.
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
            //m_root = new Folder(getID().toString(), null);
            //m_root.save();
            //obj.set(ROOT, m_root.getID());

            ContentSectionCollection csl = ContentSection.getAllSections();
            csl.addEqualsFilter("label",DocMgr.getConfig().getContentSection());
            if (!csl.next()) {
                s_log.fatal("could not retrive section: "+
                            DocMgr.getConfig().getContentSection());
                csl.close(); return;
            }
            final ContentSection cs = csl.getContentSection();
            csl.close();
          
            m_root = new DocFolder();

	    // need this block here *and* in afterSave() to nullify the
	    // automatic setting of context in contentItem.afterSave()

            KernelExcursion excursion = new KernelExcursion() {
                    protected void excurse() {
                        setParty(Kernel.getSystemParty());
                        Assert.exists(m_root, "Folder m_root");
			if (s_repositoryRoot == null) {
			    m_root.setParent(cs.getRootFolder());
			    s_log.debug("typical repository (no legacy folder)");
			} else {
			    m_root.setParent
				(cs.getRootFolder().getItem
				 (DocMgr.getConfig().getLegacyFolderName(),true));
			    s_log.debug("legacy folder, using folder: "+
					DocMgr.getConfig().getLegacyFolderName());
			}
                        PermissionService.setContext(m_root, Repository.this);
                    }
                };
            excursion.run();

            // name the folder with the id of the repository
            m_root.setName(getID().toString());
            m_root.setLabel("root folder for repository: "+getID().toString());
            obj.set(ROOT,m_root.getID());
	    m_root.save();

            s_log.info("Created root folder for repository " + getID());
        }
    }

     private boolean m_wasNew;

     protected void beforeSave() {
         if (isNew()) {
             m_wasNew = true;
         }

         super.beforeSave();
     }

     /**
      * Grant write permission to the Portal participants.
      */
     protected void afterSave() {
         super.afterSave();

         s_log.debug("aftersave");
         if (m_wasNew) {
             s_log.debug("aftersave new");
             final Repository rep = Repository.this;
             KernelExcursion excursion = new KernelExcursion() {
                     protected void excurse() {
                         setParty(Kernel.getSystemParty());

                         Assert.exists(m_root, "Folder m_root");

                         PermissionService.setContext(m_root, rep);
                         s_log.debug("set context for "+m_root.getTitle());
                         s_log.debug("  parent is "+rep.getID().toString());
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

        //repository.save();
        s_log.debug("create");

        return repository;
    }

    /**
     * Special case, only called by import script, so we can specify
     * where the root folders of repositories belong.  For instance,
     * if the content section is "foo", and the folderName argument is
     * "special", whenever a Repository is instantiated it will create
     * its root DocFolder under "foo/special".
     */
    public static void setRepositoryRoot(String folderName) {
        s_repositoryRoot = folderName;
    }

    /**
     * Sets the display name of the repository.
     */
    private void setName(String name) {
        //setTitle(name);
        set("name", name);
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
    public DocFolder getRoot() {
        BigDecimal id = (BigDecimal)get(ROOT);
        DocFolder root = null;

        try {
            root = new DocFolder(id);
        } catch (DataObjectNotFoundException e) {
            throw new UncheckedWrapperException
                ("Repository root folder does not exist");
        }

        return root;
    }

    /* just a hack for netegrity imports */
    public void setLegacyRoot() {

        ContentSectionCollection csl = ContentSection.getAllSections();
        csl.addEqualsFilter("label",DocMgr.getConfig().getContentSection());
        if (!csl.next()) {
            s_log.fatal("could not retrive section: "+
                        DocMgr.getConfig().getContentSection());
            csl.close();
        }
        ContentSection cs = csl.getContentSection();
        csl.close();

        m_root = new DocFolder();
        DocFolder oldRoot = getRoot();
        m_root.setParent
            (cs.getRootFolder().getItem
             (DocMgr.getConfig().getLegacyFolderName(),true));
        s_log.debug("setLegacyRoot, using folder: "+
                    DocMgr.getConfig().getLegacyFolderName());
        if (oldRoot != null) {
            oldRoot.delete();
        }
        m_root.setName(getID().toString());
        m_root.setLabel("root folder for repository: "+getID().toString());
        m_root.save();
        set(ROOT,m_root.getID());
    }

    public static DocumentCollection getRecentlyModifiedDocuments() {

      HttpServletRequest req = Web.getRequest();
      Repository rep = getCurrentRepository(req); 
      return getRecentlyModifiedDocuments(rep);
    }

    public static DocumentCollection getRecentlyModifiedDocuments(Repository rep) {
      DataCollection dataCollection =
       SessionManager.getSession().retrieve(Document.BASE_DATA_OBJECT_TYPE);
      //SessionManager.getSession().retrieve(Document.BASE_DATA_OBJECT_TYPE);

      //String path = getPath();

      dataCollection.addFilter(dataCollection.getFilterFactory().contains
          ("ancestors", "/"+rep.getRoot().getID().toString()+"/", false));
      //dataCollection.addEqualsFilter("isFolder", Boolean.FALSE);

      DocumentCollection rCollection =
          new DocumentCollection(dataCollection);
      return rCollection;
    }

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
     *   <servlet-name>docmgr-repository</servlet-name>
     *   <servlet-class>com.arsdigita.cms.docmgr.ui.RepositoryServlet</servlet-class>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>docmgr-repository</servlet-name>
     *   <url-pattern>/docmgr-repo/*</url-pattern>
     * </servlet-mapping>
     *
     * @return ServelPath of the applications servlet
     */
    @Override
    public String getServletPath() {
        return "/docmgr-repo";
    }

}
