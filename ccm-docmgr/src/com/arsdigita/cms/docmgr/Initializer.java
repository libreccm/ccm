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

import org.apache.log4j.Logger;

import com.arsdigita.cms.docmgr.DocFolder;
import com.arsdigita.cms.docmgr.Document;
import com.arsdigita.cms.docmgr.DocumentCategoryBrowserApplication;
import com.arsdigita.cms.docmgr.LegacyCategoryBrowserApplication;
import com.arsdigita.cms.docmgr.Repository;
// unused: import com.arsdigita.cms.docmgr.search.SearchUtils;
import com.arsdigita.cms.docmgr.ui.CategoryDocsNavigatorPortlet;
import com.arsdigita.cms.docmgr.ui.LegacyCategoryDocsNavigatorPortlet;
import com.arsdigita.cms.docmgr.ui.RecentUpdatedDocsPortlet;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
// import com.arsdigita.initializer.Configuration;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortletSetup;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.web.ApplicationSetup;
import com.arsdigita.web.ApplicationType;


/**
 * Initializes the document manager package, sets up the
 * DomainFactory, registers knowledge types and portlets.
 *
 * @author Stefan Deusch
 * @author David Dao
 *
 * @version $Revision: #11 $ $Date: 2004/01/14 $
 */
public class Initializer extends CompoundInitializer {

    /** Private Logger instance for debugging purpose.                        */
    private static Logger s_log = Logger.getLogger(Initializer.class);

    // private Configuration m_conf = new Configuration();

    private static final String SEPARATOR = java.io.File.separator;

    private static String PACKAGE_TYPE_NAME = "cmsdocs";
    private static String CATEGORY_PACKAGE_TYPE_NAME = "cmsdocs-categories";
    private static String LEGACY_PACKAGE_TYPE_NAME = "cmsdocs-categories-legacy";

    public static final String CONTENT_SECTION = "contentSection";
    public static final String INTERNAL_GROUP_ID = "internalGroupID";
    public static final String LEGACY_FOLDER_NAME = "legacyFolderName";

    /**
     * 
     */
    public Initializer() {
    }

    /**
     * Returns the configuration object used by this initializer.
     */
//  public Configuration getConfiguration() {
//      return m_conf;
//  }

    /**
     * 
     * @param e 
     */
    @Override
    public void init(DomainInitEvent e) {
        s_log.debug("Document (CCM) Manager is Domain initializing ... ");

        /* Register REPOSITORY application type.*/
        e.getFactory().registerInstantiator(
            Repository.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Repository(dataObject);
                }
            });

        /* Register Portlet  DocumentBrowser.                             */
        e.getFactory().registerInstantiator(
            RecentUpdatedDocsPortlet.BASE_DATA_OBJECT_TYPE,
            new ACSObjectInstantiator() {
                @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new RecentUpdatedDocsPortlet(dataObject);
                }
            });

        /* Set up domain object factories for basic document manager
         * object types.                                                      */

        DomainObjectFactory.registerInstantiator(
             Document.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
            @Override
                  public DomainObject doNewInstance(DataObject dataObject) {
                        return new Document(dataObject);
                        }
                  }
        );
        DomainObjectFactory.registerInstantiator(
             DocFolder.BASE_DATA_OBJECT_TYPE, new ACSObjectInstantiator() {
            @Override
                  public DomainObject doNewInstance(DataObject dataObject) {
                        return new DocFolder(dataObject);
                        }
                  }
        );


        startup();
    }

    /**
     * Called on startup.
     */
    public void startup() {
        s_log.info("Document Manager is initializing.");

    //  setupDomainFactory();

        TransactionContext txn = SessionManager.getSession()
                                               .getTransactionContext();
        txn.beginTxn();

  //    ApplicationType docsAppType = setupDocs();
  //    setupDocManagerPortlet(docsAppType);

  //    ApplicationType categoryBrowseDocsAppType = setupCategoryBrowsing();
  //    setupCategoryDocsPortlet(categoryBrowseDocsAppType);

  //    ApplicationType legacyCategoryBrowseDocsAppType = 
  //        setupLegacyCategoryBrowsing();
  //    setupLegacyCategoryDocsPortlet(legacyCategoryBrowseDocsAppType);

        // de-activate search for now
        //SearchUtils.setSearcher
        //    (new com.arsdigita.cms.docmgr.search.IntermediaSearcher());

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
        setup.setTitle("Document Manager (CMS) Application");
        setup.setSingleton(false);
        setup.setDescription
            ("The document manager empowers users to share documents.");
        setup.setDispatcherClass("com.arsdigita.cms.docmgr.ui.DMDispatcher");
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
        setup.setTitle("Recent Documents");
        setup.setDescription("Displays the most recent documents in the document manager.");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setProviderApplicationType(provider);
        setup.setInstantiator(new ACSObjectInstantiator() {
            @Override
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new RecentUpdatedDocsPortlet(dataObject);
                }
            });

        setup.run();
    }

    private ApplicationType setupCategoryBrowsing() {
        ApplicationSetup setup = new ApplicationSetup(s_log);
        setup.setApplicationObjectType(DocumentCategoryBrowserApplication.BASE_DATA_OBJECT_TYPE);
        setup.setKey(CATEGORY_PACKAGE_TYPE_NAME);
        setup.setTitle("Browse Documents Application");
        setup.setSingleton(true);
        setup.setDescription
            ("Browse documents by category.");
        setup.setDispatcherClass("com.arsdigita.cms.docmgr.ui.DCNDispatcher");
        // Class Stylesheet and database backed stylesheet locations are
        // deprecated and removed. New StylesheetResolver is pattern based.
        //setup.setStylesheet("/packages/cms-docmgr/xsl/docs.xsl");
        setup.setInstantiator(new ACSObjectInstantiator() {
            @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new DocumentCategoryBrowserApplication(dataObject);
                }
            });
        return setup.run();

    }
    private ApplicationType setupLegacyCategoryBrowsing() {
        ApplicationSetup setup = new ApplicationSetup(s_log);
        setup.setApplicationObjectType(LegacyCategoryBrowserApplication.BASE_DATA_OBJECT_TYPE);
        setup.setKey(LEGACY_PACKAGE_TYPE_NAME);
        setup.setTitle("Taxonomy Browser");
        setup.setSingleton(true);
        setup.setDescription
            ("Browse documents by category.");
        setup.setDispatcherClass("com.arsdigita.cms.docmgr.ui.DCNDispatcher");
        // Class Stylesheet and database backed stylesheet locations are
        // deprecated and removed. New StylesheetResolver is pattern based.
        //setup.setStylesheet("/packages/cms-docmgr/xsl/docs.xsl");
        setup.setInstantiator(new ACSObjectInstantiator() {
            @Override
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new LegacyCategoryBrowserApplication(dataObject);
                }
            });
        return setup.run();

    }

    private void setupCategoryDocsPortlet(ApplicationType provider) {
        // Create the document manager portlet
        AppPortletSetup setup = new AppPortletSetup(s_log);

        setup.setPortletObjectType(CategoryDocsNavigatorPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Document Category Navigator");
        setup.setDescription("Browse documents by category.");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setProviderApplicationType(provider);
        setup.setInstantiator(new ACSObjectInstantiator() {
            @Override
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new CategoryDocsNavigatorPortlet(dataObject);
                }
            });

        setup.run();
    }

    private void setupLegacyCategoryDocsPortlet(ApplicationType provider) {
        // Create the document manager portlet
        AppPortletSetup setup = new AppPortletSetup(s_log);

        setup.setPortletObjectType(LegacyCategoryDocsNavigatorPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Taxonomy Browser");
        setup.setDescription("Browse documents by category.");
        setup.setProfile(PortletType.WIDE_PROFILE);
        setup.setProviderApplicationType(provider);
        setup.setInstantiator(new ACSObjectInstantiator() {
            @Override
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new LegacyCategoryDocsNavigatorPortlet(dataObject);
                }
            });

        setup.run();
    }


    /**
     * Shutdown the document manager.
     */
    public void shutdown() { }

}
