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

package com.arsdigita.docmgr.installer;

import org.apache.log4j.Logger;

import com.arsdigita.db.DbHelper;
import com.arsdigita.docmgr.Constants;
import com.arsdigita.docmgr.DocBlobject;
import com.arsdigita.docmgr.File;
import com.arsdigita.docmgr.Folder;
import com.arsdigita.docmgr.Repository;
import com.arsdigita.docmgr.ResourceImpl;
import com.arsdigita.docmgr.ui.RecentUpdatedDocsPortlet;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortletSetup;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.web.ApplicationSetup;
import com.arsdigita.web.ApplicationType;


/**
 * Initializes the document manager package, sets up the
 * DomainFactory, registers knowledge types and portlets.
 *
 * @author Stefan Deusch
 * @author David Dao
 *
 * @version $Revision: #7 $ $Date: 2003/07/10 $
 */

//public class Initializer implements com.arsdigita.initializer.Initializer {
public class Initializer extends CompoundInitializer {


    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-docmgr.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }


//     private Configuration m_conf = new Configuration();

//     private static final String SEPARATOR = java.io.File.separator;

    private static String PACKAGE_TYPE_NAME = "docs";

    private static Logger s_log =
        Logger.getLogger(Initializer.class);


    public void init(DomainInitEvent e) {
	s_log.warn("Document Manager is initializing using .init(DomainInitEvent e)");
	setupDomainFactory();

	      TransactionContext txn = SessionManager.getSession()
            .getTransactionContext();
        txn.beginTxn();

        ApplicationType docsAppType = setupDocs();
        setupDocManagerPortlet(docsAppType);

        txn.commitTxn();
        s_log.info("Document Manager Initializer completed.");
    }

//     public Initializer() { }

//     /**
//      * Returns the configuration object used by this initializer.
//      */
//     public Configuration getConfiguration() {
//         return m_conf;
//     }

//     /**
//      * Called on startup.
//      */
//     public void startup() {
//         s_log.warn("Document Manager is initializing.");

//         setupDomainFactory();

//         TransactionContext txn = SessionManager.getSession()
//             .getTransactionContext();
//         txn.beginTxn();

//         ApplicationType docsAppType = setupDocs();
//         setupDocManagerPortlet(docsAppType);

//         txn.commitTxn();
//         s_log.info("Document Manager Initializer completed.");
//     }

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
        setup.setStylesheet("/packages/docmgr/xsl/docs.xsl");
        setup.setInstantiator(new ACSObjectInstantiator() {
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

//     /**
//      * Shutdown the document manager.
//      */
//     public void shutdown() { }

}
