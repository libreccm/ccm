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
package com.arsdigita.notification;

import com.arsdigita.kernel.User;
import com.arsdigita.mail.SimpleServer;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * Test of DigestQueueManager, similar to SimpleQueueManagerTest.
 *
 * @author Stefan Deusch
 * @version $Id: DigestQueueManagerTest.java 1940 2009-05-29 07:15:05Z terry $
 */
public class DigestQueueManagerTest extends BaseTestCase {


    static ManagerDispatcher requestMgr;
    static ManagerDispatcher digestQMgr;

    /**
     * Timeout for email processing
     */

    final static int MAX_WAIT = 25;

    public DigestQueueManagerTest (String name) {
        super(name);
    }

    protected void setUp() {
        SimpleServer.startup();

        requestMgr = new ManagerDispatcher(new RequestManager());
        digestQMgr = new ManagerDispatcher(new DigestQueueManager());
    }

    protected void tearDown() {
        requestMgr.interrupt();
        digestQMgr.interrupt();
    }


    /**
     * test if DigestQueueManager sends mails correctly,
     * Watch mail arriving on SimpleServer on localhost
     * 5 mails with one message each should be sent to
     * 5 recipients.
     */
    public void testDigestQueueManager001() {
        Session session = SessionManager.getSession();

        // create a Digest with 5 notifications to 5 users

        int count = 5;
        Digest d1 = NotificationSuite.getDigest(count);

        // transaction context is resurrected in getDigest above

        SimpleServer.reset();

        NotificationSuite.runManager(requestMgr);
        NotificationSuite.runManager(digestQMgr);
        NotificationSuite.runManager(requestMgr);

        if (!NotificationSuite.assertReceive(count,MAX_WAIT))
            fail("did not receive " + count + " emails in " + MAX_WAIT + " seconds");
        else
            System.out.println("testDigestQueueManager001 passed");

    }

    /**
     * test if DigestQueueManager sends mails correctly,
     * Watch mail arriving on SimpleServer on localhost and count.
     * 1 Mail containing 5 messages to 1 User should be received.
     *
     */
    public void testDigestQueueManager002() {
        Session session = SessionManager.getSession();

        SimpleServer.reset();

        // create a Digest with 5 notifications to one user
        int count = 5;
        User   to = NotificationSuite.getUser();
        Digest d1 = NotificationSuite.getDigest(count,to.getOID());

        // transaction context is resurrected in getDigest above

        NotificationSuite.runManager(requestMgr);
        NotificationSuite.runManager(digestQMgr);
        NotificationSuite.runManager(requestMgr);

        if (!NotificationSuite.assertReceive(1,MAX_WAIT))
            fail("did not receive 1 email in " + MAX_WAIT + " seconds");
        else
            System.out.println("testDigestQueueManager002 passed");
    }


    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(DigestQueueManagerTest.class);
    }
}
