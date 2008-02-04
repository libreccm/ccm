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

package com.arsdigita.docmgr;


import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.Web;

/**
 * A repository is the application that provides access to files and
 * folders.
 *
 * @author Stefan Deusch (stefan@arsdigita.com)
 * @author Ron Henderson (ron@arsdigita.com)
 */

public class Repository extends Application implements Constants {
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.docs.Repository";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    private static Logger s_log =
        Logger.getLogger(Repository.class);

    // pdl constants
    private static final String OWNER = "ownerID";
    private static final String ROOT  = "rootID";

    private Folder m_root = null;

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

            m_root = new Folder(getID().toString(), null);
            m_root.save();
            obj.set(ROOT, m_root.getID());

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

        if (m_wasNew) {
            KernelExcursion excursion = new KernelExcursion() {
                    protected void excurse() {
                        setParty(Kernel.getSystemParty());

                        Assert.assertNotNull(m_root, "Folder m_root");

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

    public static ResourceImplCollection getRecentlyModifiedDocuments() {

      HttpServletRequest req = Web.getRequest();
      Repository rep = getCurrentRepository(req); 
      DataCollection dataCollection =
       SessionManager.getSession().retrieve(ResourceImpl.BASE_DATA_OBJECT_TYPE);

      dataCollection.addFilter(dataCollection.getFilterFactory().startsWith
          ("path", rep.getRoot().getPath(), true));
      dataCollection.addEqualsFilter("isFolder", Boolean.FALSE);

      ResourceImplCollection rCollection =
          new ResourceImplCollection(dataCollection);
      return rCollection;
    }

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

    public static Repository getCurrentRepository(HttpServletRequest req) {
        Application app = Application.getCurrentApplication(req);
        if(app instanceof Repository) {
            return (Repository) app;
        }
        return null;
    }
}
