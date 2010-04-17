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
package com.arsdigita.web;

import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.Startup;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ApplicationTool.java 738 2005-09-01 12:36:52Z sskracic $
 */
public final class ApplicationTool {

    private static final Logger s_log = Logger.getLogger(ApplicationTool.class);

    public static final void main(final String[] args) {
        if (args.length != 3) {
            System.out.println
                ("Usage: ApplicationTool APPLICATION-TYPE URL-FRAGMENT TITLE");
            System.exit(1);
        }

	new Startup().run();

	final Session session = SessionManager.getSession();
	final TransactionContext transaction = session.getTransactionContext();
	transaction.beginTxn();

        Application.createApplication(args[0], args[1], args[2], null);

	transaction.commitTxn();
    }

}
