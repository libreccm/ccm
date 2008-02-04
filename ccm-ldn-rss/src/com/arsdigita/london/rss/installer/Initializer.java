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

package com.arsdigita.london.rss.installer;

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;

import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.BaseInitializer;
import com.arsdigita.kernel.ACSObjectInstantiator;

import com.arsdigita.categorization.CategoryPurpose;
import com.arsdigita.london.rss.RSS;
import com.arsdigita.london.rss.Feed;

import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationSetup;
import com.arsdigita.web.ApplicationType;
import com.arsdigita.web.URL;

import org.apache.log4j.Logger;


/**
 * Initialize the RSS package.
 *
 * @author Bryan Quinn (bquinn@arsdigita.com)
 * @version $Revision: #11 $, $Date: 2004/03/19 $
 */
public class Initializer extends BaseInitializer {
    private static Logger s_log =
        Logger.getLogger(Initializer.class);

    private Configuration m_conf = new Configuration();

    public Initializer() {
    }

    /**
     * Returns the configuration object used by this initializer.
     **/
    public Configuration getConfiguration() {
        return m_conf;
    }

    /**
     * Called on startup.
     **/
    protected void doStartup() throws InitializationException {

        String catKey = RSS.getConfig().getCategoryKey();
        s_log.info("Setting RSS Category Key to " + catKey + ".");

        TransactionContext txn = SessionManager.getSession()
            .getTransactionContext();
        txn.beginTxn();
 
        if (!CategoryPurpose.purposeExists(catKey)) {
            (new CategoryPurpose(catKey, "RSS Feed")).save();
        }
        
        setupChannelControlCenter();
        setupLocalFeeds();

        txn.commitTxn();
    }

    public void setupChannelControlCenter() {
        ApplicationSetup setup = new ApplicationSetup(s_log);
        
        setup.setApplicationObjectType(RSS.BASE_DATA_OBJECT_TYPE);
        setup.setKey("rss");
        setup.setTitle("RSS Channels");
        setup.setDescription("RSS Channels");
        setup.setSingleton(true);
        setup.setInstantiator(new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new RSS(dataObject);
                }
            });
        ApplicationType type = setup.run();
        type.save();
        
        if (!Application.isInstalled(RSS.BASE_DATA_OBJECT_TYPE,
                                     "/channels/")) {
            Application app =
                Application.createApplication(type,
                                              "channels",
                                              "RSS",
                                              null);
            app.save();
        }
    }

    /**
     * Called on shutdown.
     **/
    protected void doShutdown() throws InitializationException {
    }


    public void setupLocalFeeds() {
        URL external = URL.there("/channels/rss/external.rss", null);
        
        try {
            Feed feed = Feed.retrieve(external.getURL());
        } catch (DataObjectNotFoundException ex) {
            Feed feed = Feed.create(external.getURL(),
                                    "External feeds",
                                    "External rss content feeds",
                                    true);
            feed.save();
        }

        URL index = URL.there("/channels/rss/index.rss", null);
        try {
            Feed feed = Feed.retrieve(index.getURL());
        } catch (DataObjectNotFoundException ex) {
            Feed feed = Feed.create(index.getURL(),
                                    "Local content feeds",
                                    "Local CMS content feeds",
                                    true);
            feed.save();
        }
    }
}
