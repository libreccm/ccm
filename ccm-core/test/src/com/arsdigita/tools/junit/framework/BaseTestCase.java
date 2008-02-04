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
package com.arsdigita.tools.junit.framework;

import com.arsdigita.kernel.TestHelper;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import java.util.Locale;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

public abstract class BaseTestCase extends TestCase {

    private static Logger s_log =
        Logger.getLogger(BaseTestCase.class.getName());

    /**
     * Constructs a test case with the given name.
     */
    public BaseTestCase(String name) {
        super(name);
    }

    /**
     * Runs the bare test sequence.
     * @exception Throwable if any exception is thrown
     */
    public void runBare() throws Throwable {
        baseSetUp();
        try {
            try {
                setUp();
                runTest();
            } catch(Throwable t) {
                try {
                    tearDown();
                } catch (Throwable t2) {
                    System.err.println ( "Error in teardown: " );
                    t2.printStackTrace ( System.err );
                }
                throw t;
            }
            tearDown();
        } finally {
            baseTearDown ();
        }
    }

    protected void baseSetUp() {
        s_log.warn (this.getClass().getName() + "." + getName() +  " started");

        Session sess = SessionManager.getSession();
        sess.getTransactionContext().beginTxn();
        TestHelper.setLocale(Locale.ENGLISH);
    }

    protected void baseTearDown() {
        Session sess = SessionManager.getSession();
        if (sess.getTransactionContext().inTxn()) {
            sess.getTransactionContext().abortTxn();
        }

        s_log.info (this.getClass().getName() + " finished");
    }
}
