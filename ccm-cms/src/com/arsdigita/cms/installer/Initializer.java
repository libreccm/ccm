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
package com.arsdigita.cms.installer;

import com.arsdigita.auditing.BasicAuditTrail;
import com.arsdigita.cms.dispatcher.ContentCenterDispatcher;
import com.arsdigita.cms.dispatcher.ItemDispatcher;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;

import org.apache.log4j.Logger;




/**
 * <p>The main initializer for the Content Management System.</p>
 *
 * <p>Initializes the Content Management System, including the Content Center
 * and CMS Service applications and CMS Mime Types service. This class also
 * optionally initializes user-defined content types and user-defined content
 * sections.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #47 $ $Date: 2004/08/17 $
 * @since ACS 5.0
 */
public class Initializer extends com.arsdigita.kernel.BaseInitializer {

    private static Logger s_log = Logger.getLogger(Initializer.class);

    private final static String CACHE_ITEMS        
        = "cacheItems";
    private static final String UPDATE_MASTER
        = "updateMasterObject";
    private final static String CONTENT_CENTER_MAP
        = "contentCenterMap";
    private final static String WORKSPACE            
        = "workspace";
    // Init script parameters
    private Configuration m_conf = new Configuration();


    public Initializer() throws InitializationException {
        m_conf.initParameter
            (WORKSPACE, "The name of the workspace package instance",
             String.class);
        m_conf.initParameter
            (CACHE_ITEMS, 
             "Enable caching of content items", 
             Boolean.class,
             Boolean.TRUE);
        m_conf.initParameter
            (UPDATE_MASTER,
             "If true, attempts to recursively set the correct master object for " +
             "all content items within the system.",
             Boolean.class,
             Boolean.FALSE);
        m_conf.initParameter
            (CONTENT_CENTER_MAP,
             "XML Mapping of the content center tabs to " +
             "URLs, see ContentCenterDispatcher",
             String.class,
             ContentCenterDispatcher.DEFAULT_MAP_FILE);
    }

    public Configuration getConfiguration() {
        return m_conf;
    }


    /**
     * Check if CMS package type exists. If not, then:
     *
     * <ol>
     *   <li>create CMS package type</li>
     *   <li>create Workspace package type and instance</li>
     *   <li>create CMS Service package type and instance</li>
     * </ol>
     */
    protected void doStartup() {

        TransactionContext txn =
            SessionManager.getSession().getTransactionContext();
        txn.beginTxn();

        try {

            final String workspaceURL = (String) m_conf
                .getParameter(WORKSPACE);
            final String contentCenterMap = (String)m_conf
                .getParameter(CONTENT_CENTER_MAP);

            // Update master object if upgrading from old versioning
            // XXX: shouldn't we just gut this section (and
            // VersioningUpgrader)? It is an upgrade fix from 5.1 or
            // earlier, and relying on VersionedACSObject is
            // deprecated 
            final boolean updateMaster = 
                ((Boolean)m_conf.getParameter(UPDATE_MASTER)).booleanValue();
            if (updateMaster) {
                VersioningUpgrader.updateMasterObject();
            }

            // XXX: ItemDispatcher is no longer used. Is the following
            // still a valid enterprise.init parameter? Do we need t
            // oset ContentSectionServlet.s_cacheItems instead of the
            // below (which is currently always true), or does this go
            // away entirely?
            final boolean cacheItems = 
                ((Boolean)m_conf.getParameter(CACHE_ITEMS)).booleanValue();
            s_log.debug("Set cache items to " + cacheItems);
            ItemDispatcher.setCacheItems(cacheItems);

            ContentCenterSetup centerSetup = new ContentCenterSetup(
                workspaceURL,
                contentCenterMap);

            centerSetup.run();

        } finally {
            txn.commitTxn();
        }

    }

    protected void doShutdown() {}

    
    
}
