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

package com.arsdigita.bookmarks.installer;

import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.OID;
import com.arsdigita.web.*;
import com.arsdigita.kernel.*;
import com.arsdigita.sitenode.*;
import com.arsdigita.bookmarks.*;
import com.arsdigita.bookmarks.ui.*;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortletSetup;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.DataInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;


/**
 * <p><strong>Experimental</strong></p>
 *
 * @author <a href="mailto:jparsons@redhat.com">Jim Parsons</a>
 */
// public class Initializer extends BaseInitializer {
public class Initializer extends CompoundInitializer {

    private static final Logger s_log = Logger.getLogger
        (Initializer.class);

    private Configuration m_conf = new Configuration();

//     public Initializer() throws InitializationException {
//         super();
        
//     }

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-bookmarks.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }


//     public Configuration getConfiguration() {
//         return m_conf;
//     }


    public void init(DomainInitEvent e) {
	s_log.warn("Bookmarks app is initializing using .init(DomainInitEvent e)");
// 	setupDomainFactory();

	      TransactionContext txn = SessionManager.getSession()
            .getTransactionContext();
        txn.beginTxn();
	setupBookmarks();
        txn.commitTxn();
        s_log.info("Bookamrks Initializer completed.");
    }



//     public final void doStartup() {
//         s_log.warn("Initializing Bookmarks...");

//         TransactionContext txn =
//             SessionManager.getSession().getTransactionContext();

//         txn.beginTxn();

//             setupBookmarks();

//         txn.commitTxn();

//     }

    private void setupBookmarks() {

        ApplicationSetup appsetup = new ApplicationSetup(s_log);
        appsetup.setApplicationObjectType( BookmarkApplication.BASE_DATA_OBJECT_TYPE);
        appsetup.setKey("bookmarks");
        appsetup.setTitle("Bookmarks Application");
        appsetup.setDescription("Bookmarks for a Portal");
        appsetup.setDispatcherClass("com.arsdigita.bookmarks.BookmarkDispatcher");
        appsetup.setStylesheet("/packages/bookmarks/xsl/bookmarks.xsl");
        appsetup.setPortalApplication(true);
        appsetup.setInstantiator(new ACSObjectInstantiator() {
                protected DomainObject doNewInstance(DataObject dataObject) {
                    return new BookmarkApplication(dataObject);
                }
            });

        ApplicationType bmrkAppType = appsetup.run();


        AppPortletSetup setup = new AppPortletSetup(s_log);

        setup.setPortletObjectType
            (BookmarkPortlet.BASE_DATA_OBJECT_TYPE);
        setup.setTitle("Portal Bookmarks");
        setup.setDescription("Displays bookmarks for this portal.");
        setup.setProfile(PortletType.NARROW_PROFILE);
        setup.setPortalApplication(false);
        setup.setProviderApplicationType(bmrkAppType);
        setup.setInstantiator(new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new BookmarkPortlet(dataObject);
                }
            });
        setup.run();
    }


//     public final void doShutdown() {}
}
