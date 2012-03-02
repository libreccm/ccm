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
 */

package com.arsdigita.bookmarks;

import com.arsdigita.db.DbHelper;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.SessionManager;
// unused import com.arsdigita.persistence.OID;
import com.arsdigita.web.*;
import com.arsdigita.kernel.*;
// unused import com.arsdigita.sitenode.*;
import com.arsdigita.bookmarks.ui.*;
// unused  import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObject;
// import com.arsdigita.initializer.Configuration;
// unused import com.arsdigita.initializer.InitializationException;
// unused import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.pdl.ManifestSource;
import com.arsdigita.persistence.pdl.NameFilter;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortletSetup;
import com.arsdigita.runtime.CompoundInitializer;
// unused import com.arsdigita.runtime.DataInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.runtime.PDLInitializer;
import com.arsdigita.runtime.RuntimeConfig;
// unusd import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;


/**
 * <p><strong>Experimental</strong></p>
 *
 * @author <a href="mailto:jparsons@redhat.com">Jim Parsons</a>
 */
public class Initializer extends CompoundInitializer {

    private static final Logger s_log = Logger.getLogger
        (Initializer.class);

    //  required by Old Initializer.
    //  private Configuration m_conf = new Configuration();

    public Initializer() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int database = DbHelper.getDatabaseFromURL(url);

        add(new PDLInitializer
            (new ManifestSource
             ("ccm-bookmarks.pdl.mf",
              new NameFilter(DbHelper.getDatabaseSuffix(database), "pdl"))));
    }


    @Override
    public void init(DomainInitEvent e) {
	    s_log.info("Bookmarks app is initializing using .init(DomainInitEvent e)");
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
