/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.test;

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.kernel.*;
import com.arsdigita.portalserver.*;


import org.apache.log4j.Category;

public class Initializer implements com.arsdigita.initializer.Initializer {
    public Configuration getConfiguration() {
        return null;
    }

    public void startup() throws InitializationException {
        s_log.warn("Initializing Portal...");

        TransactionContext txn =
            SessionManager.getSession().getTransactionContext();

        txn.beginTxn();

        TestPortalSetup setup = new TestPortalSetup();
        setup.setupPortal();
        txn.commitTxn();

    }

    public void shutdown() throws InitializationException {

    }

    private static Category s_log = Category.getInstance
        (Initializer.class);

    private Configuration m_conf = new Configuration();




}
