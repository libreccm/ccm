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

package com.arsdigita.london.cms.freeform;

import com.arsdigita.db.DbHelper;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.Script;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.runtime.Runtime;
import com.arsdigita.util.JavaPropertyReader;
import com.arsdigita.util.jdbc.Connections;
import java.sql.Connection;
import org.apache.log4j.Logger;

/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 1763 2009-01-22 20:01:31Z pboy $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    @Override
    public void run(final ScriptContext ctx) {
        // Nada yet
    }

    public static final void main(final String[] args) throws Exception {
        final Connection conn = Connections.acquire
            (RuntimeConfig.getConfig().getJDBCURL());

        final int database = DbHelper.getDatabaseFromURL
            (conn.getMetaData().getURL());
        final String dir = DbHelper.getDatabaseDirectory(database);

        load(conn, "ccm-ldn-freeform/" + dir + "-create.sql");

        final Runtime runtime = new Runtime();
        runtime.add(new Initializer());
        runtime.startup();

        final Session session = SessionManager.getSession();
        final TransactionContext txn = session.getTransactionContext();

        txn.beginTxn();

        final ScriptContext context = new ScriptContext
            (session, new JavaPropertyReader(System.getProperties()));

        final Script script = new Loader();
        script.run(context);

        session.create(new OID("com.arsdigita.runtime.Initializer",
                               Initializer.class.getName()));

        txn.commitTxn();
    }
}
