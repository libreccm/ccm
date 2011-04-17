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
package com.arsdigita.docrepo.installer;

import com.arsdigita.docrepo.ui.RecentUpdatedDocsPortlet;
import com.arsdigita.docrepo.File;
import com.arsdigita.docrepo.Folder;
import com.arsdigita.docrepo.ResourceImpl;
import com.arsdigita.docrepo.DocBlobject;
import com.arsdigita.docrepo.Repository;
import com.arsdigita.docrepo.Constants;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
//import com.arsdigita.util.ResourceManager;
import com.arsdigita.web.ApplicationSetup;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.portal.apportlet.AppPortletSetup;
import com.arsdigita.portal.PortletType;

//import java.io.FileInputStream;
import org.apache.log4j.Logger;


/**
 * Initializes the document manager package, sets up the
 * DomainFactory, registers knowledge types and portlets.
 *
 * @author Stefan Deusch
 * @author David Dao
 *
 * @version $Revision: #10 $ $Date: 2004/08/17 $
 */

public class Initializer implements com.arsdigita.initializer.Initializer {

    private Configuration m_conf = new Configuration();

    private static final String SEPARATOR = java.io.File.separator;

    private static String PACKAGE_TYPE_NAME = "docs";

    private static Logger s_log =
        Logger.getLogger(Initializer.class);

    public Initializer() { }

    /**
     * Returns the configuration object used by this initializer.
     */
    public Configuration getConfiguration() {
        return m_conf;
    }

    /**
     * Called on startup.
     */
    public void startup() {
        s_log.info("Document Manager is initializing.");

        setupDomainFactory();

        TransactionContext txn = SessionManager.getSession()
            .getTransactionContext();
        txn.beginTxn();

        ApplicationType docsAppType = setupDocs();
        setupDocManagerPortlet(docsAppType);

        txn.commitTxn();
        s_log.info("Document Manager Initializer completed.");
    }

    /**
     * Set up the document manager.  Checks to see if the necessary
     * package exists, and if not it creates it for the first time.
     */

    private ApplicationType setupDocs() {
        ApplicationSetup setup = new ApplicationSetup(s_log);
        setup.setApplicationObjectType(Repository.BASE_DATA_OBJECT_TYPE);
        setup.setKey(PACKAGE_TYPE_NAME);
        setup.setTitle("Document Manager Application");
        setup.setSingleton(true);
        setup.setDescription
            ("The document manager empowers users to share documents.");
        setup.setDispatcherClass("com.arsdigita.docmgr.ui.DMDispatcher");
    //  setup.setStylesheet("/packages/docmgr/xsl/docs.xsl");
        setup.setInstantiator(new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Repository(dataObject);
                }
            });

        return setup.run();

    }

    private void setupDocManagerPortlet(ApplicationType provider) {
        // Create the document manager portlet
        AppPortletSetup setup = new AppPortletSetup(s_log);

        setup.setPortletObjectType(RecentUpdatedDocsPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Recently Updated Documents");
        setup.setDescription("Displays the most recent documents in the document manager.");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setProviderApplicationType(provider);
        setup.setInstantiator(new ACSObjectInstantiator() {
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new RecentUpdatedDocsPortlet(dataObject);
                }
            });

        setup.run();
    }

    /**
     * Set up domain object factories for basic document manager
     * object types.
     */
    private void setupDomainFactory() {
        DomainObjectFactory.registerInstantiator
            (ResourceImpl.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 public DomainObject doNewInstance(DataObject obj) {
                     Boolean isFolder = (Boolean) obj.get(Constants.IS_FOLDER);
                     if (isFolder != null && isFolder.booleanValue()) {
                         return new Folder(obj);
                     } else {
                         return new File(obj);
                     }
                 }
             });
        // File
        DomainObjectFactory.registerInstantiator(
             File.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                  public DomainObject doNewInstance(DataObject dataObject) {
                        return new File(dataObject);
                        }
                  }
        );

        // Folder
        DomainObjectFactory.registerInstantiator(
             Folder.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
                  public DomainObject doNewInstance(DataObject dataObject) {
                        return new Folder(dataObject);
                        }
                  }
        );

        DomainObjectFactory.registerInstantiator(
             DocBlobject.BASE_DATA_OBJECT_TYPE, new DomainObjectInstantiator() {
                  public DomainObject doNewInstance(DataObject dataObject) {
                        return new DocBlobject(dataObject);
                        }
                  }
        );
    }

    /**
     * Shutdown the document manager.
     */
    public void shutdown() { }

}
