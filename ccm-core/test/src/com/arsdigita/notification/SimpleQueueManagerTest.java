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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.mail.SimpleServer;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * Test of SimpleQueueManager. Check if non-digest notifications are processed
 * correctly. The class ManagerDispatcher is used to run the SimpleQueueManager
 * and the RequestManager when necessary. The latter are set up for each test
 * in the setup method.
 * In the ACS production environment their execution is controlled by a Timer
 * which is called in the Initializer class at startup with the parameters
 * specified in enterprise.init.
 *
 * Note: since the Request- and SimpleQueueManager only work on persistent data
 * we must create/reload the notifications elsewhere, here in NotificationSuite,
 * and make them persistent. The test-scope transaction context would not create
 * usable, persistent objects for testing.
 *
 * @author Stefan Deusch
 * @version $Id: SimpleQueueManagerTest.java 1940 2009-05-29 07:15:05Z terry $
 */
public class SimpleQueueManagerTest extends BaseTestCase {


    static final int MAX_WAIT = 5;

    static ManagerDispatcher requestMgr;
    static ManagerDispatcher simpleQMgr;


    public SimpleQueueManagerTest (String name) {
        super(name);
    }

    /**
     * set up SimpleServer (SMTP mail server) on localhost,
     * and one RequestManager plus SimpleQueueManager
     */
    protected void setUp() {
        SimpleServer.startup();

        requestMgr  = new ManagerDispatcher(new RequestManager());
        simpleQMgr = new ManagerDispatcher(new SimpleQueueManager());
    }

    /**
     * deallocate resources from setUp()
     */
    protected void tearDown() {
        requestMgr.interrupt();
        simpleQMgr.interrupt();
    }


    /**
     * test if SimpleQueueManager sends mails correctly,
     * watch mail arriving on SimpleServer on localhost
     */
    public void testSimpleQueueManager001() {
        Session session = SessionManager.getSession();

        // create a persistent notification, check status
        Notification n = NotificationSuite.getNotification();
        // transaction context is resurrected in getNotification above
        assertEquals("Newly created notification not in PENDING status",
                     Notification.PENDING, n.getStatus());
        OID id = n.getOID();
        session.getTransactionContext().commitTxn();

        // crank through RequestManager and wait for results
        NotificationSuite.runManager(requestMgr);

        // reload
        try {
            session.getTransactionContext().beginTxn();
            n = new Notification(id);
            assertEquals("Notification not in QUEUED status",
                         Notification.QUEUED, n.getStatus());
            session.getTransactionContext().commitTxn();
        } catch(DataObjectNotFoundException e) {
            fail("Could not reload notification ");
        }

        // run SimpleQueueManager and wait for results
        NotificationSuite.runManager(simpleQMgr);

        // rerun RequestManager
        NotificationSuite.runManager(requestMgr);

        // check if this notification is SENT
        try {
            session.getTransactionContext().beginTxn();
            n =  new Notification(id);
            String status = n.getStatus();
            session.getTransactionContext().commitTxn();
            assertEquals("Just sent notification not in SENT status",
                         Notification.SENT, status);

        } catch(DataObjectNotFoundException e) {
            fail("Could not reload notification ");
        }

        /*
          check received mails on server
          Note: the number of received emails during this test is only equal
          to the number of send mails here, IFF
          1. no other mail test runs concurrently,
          2. no stalled mail accumulated in the queue

          if(!NotificationSuite.assertReceive(1, MAX_WAIT))
          fail("Local mail server did not receive "+n+
          " emails within "+MAX_WAIT+" s.");
        */
    }


    /**
     * Test that sending a notification with isPermaent = false
     * correctly deletes the notification after processing.
     */

    public void testSimpleQueueManager002() {
        Session session = SessionManager.getSession();

        Notification n = NotificationSuite.getNotification();
        // transaction context is resurrected in getNotification above

        // set notification to be deleted after sent
        n.setIsPermanent(Boolean.FALSE); // default value is true
        OID id = n.getOID();
        session.getTransactionContext().commitTxn();

        NotificationSuite.saveNotification(n);

        processQueue();

        // Reloading the notification should fail now

        try {
            session.getTransactionContext().beginTxn();
            n = new Notification(id);
            session.getTransactionContext().commitTxn();
            fail("Should not be possible to reload notification which marked "+
                 "for deletion after being sent");
        } catch(Exception e) {
            // expected error
        }

    }

    /**
     * Test sending a notification with headers and signatures set,
     * both with and without trailing whitespace (to make sure that
     * wrapping behavior works correctly).
     */

    public void testSimpleQueueManager003() {
        Notification n = NotificationSuite.getNotification();
        n.setHeader("<< HEADER >>");
        n.setSignature("<< SIGNATURE >>");
        n.save();

        Session session = SessionManager.getSession();
        session.getTransactionContext().commitTxn();
        session.getTransactionContext().beginTxn();

        processQueue();
    }

    /**
     * Run one comlete cycle of the dispatch managers
     */

    private static void processQueue() {
        NotificationSuite.runManager(requestMgr);
        NotificationSuite.runManager(simpleQMgr);
        NotificationSuite.runManager(requestMgr);
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(SimpleQueueManagerTest.class);
    }
}
