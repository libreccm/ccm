/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.intermedia;

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;

/**
 * SyncInitializer
 *
 * Forces a sync of the database.
 * Should be run as the last initializer, so that initial
 * setup data will immediately be available to search.
 * Presently has no configuration available, either it runs
 * or it doesn't.
 *
 * @author David Eison
 * @version $Revision: #4 $ */

// Support for Logging.
import org.apache.log4j.Logger;

public class SyncInitializer
    implements com.arsdigita.initializer.Initializer {

    private Configuration m_conf = new Configuration();

    private static final Logger s_log =
        Logger.getLogger(Initializer.class);

    public final static String versionId = "$Id";

    public SyncInitializer() throws InitializationException {
    }

    /**
     * Returns the configuration object used by this initializer.
     **/
    public Configuration getConfiguration() {
        return m_conf;
    }

    /**
     * Called on startup.  Forces a sync.
     **/
    public void startup() {

        TransactionContext txn = SessionManager.getSession()
            .getTransactionContext();
        txn.beginTxn();
        BuildIndex.forceSyncNow();
        txn.commitTxn();
    }

    /**
     * Called on shutdown.  No-op.
     **/
    public void shutdown() {
    }
}
