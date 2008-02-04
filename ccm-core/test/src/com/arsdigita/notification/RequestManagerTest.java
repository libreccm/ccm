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
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * Test of RequestManager
 *
 * @author Stefan Deusch
 * @version $Id: RequestManagerTest.java 745 2005-09-02 10:50:34Z sskracic $
 */
public class RequestManagerTest extends BaseTestCase {

    public static final String versionId = "$Id: RequestManagerTest.java 745 2005-09-02 10:50:34Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    static ManagerDispatcher requestMgr;

    protected void setUp() {
        requestMgr  = new ManagerDispatcher(new RequestManager());
    }

    protected void tearDown() {
        requestMgr.interrupt();
    }


    public RequestManagerTest (String name) {
        super(name);
    }

    /**
     * test if Request Manager processes requests correctly
     * only persistent objects can be processed, therefore we
     * use external Transaction context in NotificationSuite.
     *
     */
    public void testRequestManager001() {
        Session session = SessionManager.getSession();

        Notification n = NotificationSuite.getNotification();
        // transaction context is resurrected in getNotification above
        assertEquals("Newly created notification not in PENDING status",
                     Notification.PENDING, n.getStatus());
        OID id = n.getOID();
        session.getTransactionContext().commitTxn();

        // create new RequestManager via ManagerDispatcher and run it
        NotificationSuite.runManager(requestMgr);

        try {
            session.getTransactionContext().beginTxn();
            n = new Notification(id);

            assertEquals("Newly created notification not in QUEUED status",
                         Notification.QUEUED, n.getStatus());

            session.getTransactionContext().commitTxn();

            // test for SENT status is in SimpleQueueManagerTest

        } catch(DataObjectNotFoundException e) {
            // ignore
        }

    }




    /**
     * Main method required to make this test runnable.
     */
    public static void main (String args[]) {
        junit.textui.TestRunner.run(RequestManagerTest.class);
    }
}
