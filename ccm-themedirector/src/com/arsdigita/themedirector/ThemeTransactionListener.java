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

package com.arsdigita.themedirector;

import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.persistence.TransactionListener;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.Host;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

/**
 * Calls the appropriate jsp pages on all of the hosts so that
 * published files are synced across servers
 */
class ThemeTransactionListener implements TransactionListener, ThemeDirectorConstants {
    
    /** A logger instance.  */
    private static final Logger s_log = 
                                Logger.getLogger(ThemeTransactionListener.class);

    private Collection m_urls;

    /**
     *  Gets a list of hosts in the system
     */
    public void beforeCommit(TransactionContext txn) 
        throws PersistenceException {
        
        m_urls = new ArrayList();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Running beforeCommit event in theme transaction listener");
        }

        // tell other servers to update their files
        Set themeSet = (Set)txn.getAttribute(ThemeObserver.THEME_TXN_ATTR);
        if (themeSet == null) {
            return;
        }

        // find the mount location
        ApplicationCollection collection = Application.retrieveAllApplications();
        collection.filterToApplicationType(ThemeDirector.BASE_DATA_OBJECT_TYPE);
        String mountPoint = null;
        if (collection.next()) {
            // it should only be mounted once but the jsp does not
            // care about the application so even if it is mounted multiple
            // times that is fine.
            mountPoint = collection.getPrimaryURL();
        }
        collection.close();

        if (mountPoint == null) {
            // this means that the application is not mounted which also means
            // we should never have gotten this far
            return;
        }

        // at this point, we are not using the themes...rather, we just
        // make the jsp sync up with everything since it is a relatively
        // low cost background operation and it something that needs
        // to be done periodically anyway

        DomainCollection hosts = Host.retrieveAll();
        while (hosts.next()) {
            Host host = (Host)hosts.getDomainObject();
            m_urls.add(host.getURL(mountPoint + SYNC_JSP, null).getURL());
        }
    }
    
    /**
     *  now that the information is created, we can kick off a background
     *  thread to ask the other servers to sync up.
     */
    public void afterCommit(TransactionContext txn) {
        Thread thread = new SyncServers(m_urls);
        thread.setDaemon(true);
        thread.start();
    }

    // don't need these
    public void beforeAbort(TransactionContext txn) {}
    public void afterAbort(TransactionContext txn) {}

    /** 
     * 
     */
    private class SyncServers extends Thread {
        private Collection m_allURLs;
        public SyncServers(Collection urls) {
            m_allURLs = urls;
        }

        @Override
        public void run() {
            Iterator iter = m_allURLs.iterator();
            while (iter.hasNext()) {
                String urlString = (String)iter.next();
                s_log.info("Syncing themes for " + urlString);
                try {
                    URL url = new URL(urlString);
                    // is there a better way to load the page without having
                    // to wait for all of the content?
                    URLConnection connection = url.openConnection();
                    // this only works on local servers so we don't use it yet
                    //LocalRequestPassword.setLocalRequestPassword(connection);
                    connection.getContent();
                } catch (IOException e) {
                    s_log.error("Error syncing theme files with URL " + urlString, e);
                }
            }
        }
    }
}
