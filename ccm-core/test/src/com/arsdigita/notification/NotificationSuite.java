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
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.User;
import com.arsdigita.mail.SimpleServer;
import com.arsdigita.messaging.Message;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import com.arsdigita.tools.junit.extensions.PermissionDecorator;
import com.arsdigita.tools.junit.framework.PackageTestSuite;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.ListIterator;
import junit.framework.Test;

/**
 * Test Suite for Notifiction. Besides the standard jobs of the TestSuite file, this
 * class provides factory methods for <code>User</code>, <code>Notification</code>,
 * and <code>Digest</code> for peristent data objects which are needed at runtime.
 * The static cleanup method at the end cleans out the database from the junk test data
 * after the tests completed.
 *
 * @author Stefan Deusch
 * @version $Id: NotificationSuite.java 1940 2009-05-29 07:15:05Z terry $
 * @see com.arsdigita.mail.SimpleServer for a mockup SMTP mail server needed on localhost here.
 */
public class NotificationSuite extends PackageTestSuite {


    public void addTest(Test test) {
        super.addTest(new PermissionDecorator(test));
    }

    public static Test suite() {
        NotificationSuite suite = new NotificationSuite();
        suite.addTestSuite(NotificationTest.class);
        suite.addTestSuite(DigestTest.class);
        CoreTestSetup wrapper = new CoreTestSetup(suite);

        // Cleanup code is insufficient; notification tests are causing problems in other tests.
        // Commented out until it can be corrected.
        // populateSuite(suite);

        // anonymous inner class overwriting run method to assure
        // cleanup method is called
//         BaseTestSetup wrapper = new BaseTestSetup(suite) {
//             public void run(TestResult result) {
//                 super.run(result);
//                 cleanup();
//             }
//         };
//         wrapper.setInitScriptTarget ("com.arsdigita.notification.Initializer");

        return wrapper;
    }

    /**
     * Containers for test data.  We need to keep track of them so we
     * can delete them from the database at the end of the test.
     * Because several transactions are involved in the same test, we
     * have to commit a fairly large amount of test data to the
     * database while running the tests.
     */

    private static ArrayList userList = new ArrayList();
    private static ArrayList notificationList = new ArrayList();
    private static ArrayList digestList = new ArrayList();

    /**
     * Factory method for tests to provide a persistant User.
     */

    static User getUser() {

        String first = "Person" + userList.size();
        String last  = "Anybody";
        // insert unique token for unique email address
        String email = first + System.currentTimeMillis() + "@somedomain.net";

        User user = new User();
        user.getPersonName().setGivenName(first);
        user.getPersonName().setFamilyName(last);
        user.setPrimaryEmail(new EmailAddress(email));
        user.save();

        userList.add(user.getID());
        return user;
    }


    /**
     * A helper to fetch a User from the database and catch the
     * possible exception.
     */

    static User getUser (OID uid) {
        try {
            return new User(uid);
        } catch (DataObjectNotFoundException e) {
            return null;
        }
    }

    /**
     * Factory method for tests that serves a persistent notification,
     * creates a unique notification with some real dummy users.
     * When called, the method assumes an existing transaction context.
     */

    static Notification getNotification() {

        Session session = SessionManager.getSession();

        int n_count = notificationList.size();

        User   to      = getUser();
        User   from    = getUser();
        String subject = "Notification "+n_count+": Subject";
        String body    = "Notification "+n_count+": This is the body";

        Message msg = new Message(from,subject,body);
        msg.save();

        Notification n = new Notification(to,msg);
        n.setMessageDelete(Boolean.TRUE);
        n.save();

        OID id = n.getOID();
        session.getTransactionContext().commitTxn();
        session.getTransactionContext().beginTxn();

        // need to reload return persistenly

        try {
            n = new Notification(id);
        } catch(DataObjectNotFoundException e) {
            // ignore
        }

        notificationList.add(id);
        return n;
    }


    /**
     * Creates a digest with multiple notifications.  This is for the
     * simple cases where a digest sends one message to each user.
     * The method assumes an open transaction context when starting.
     *
     * @param size is the number of notifications to create
     */

    static Digest getDigest (int size) {

        Session session  = SessionManager.getSession();
        String subject   = "This is the subject";
        String header    = "My Digest";
        String signature = "End of My Digest";

        OID noticeID[]  = new OID[size];

        for (int i = 0; i < size; i++) {

            if (i > 0) {
                session.getTransactionContext().beginTxn();
            }

            Notification notice = getNotification();
            noticeID[i] = notice.getOID();
            session.getTransactionContext().commitTxn();
        }

        session.getTransactionContext().beginTxn();
        Digest d = new Digest(getUser(), subject, header, signature);
        d.save();
        OID id = d.getOID();
        digestList.add(id);
        session.getTransactionContext().commitTxn();

        // Assign each notification to the Digest

        for (int i = 0; i < size; i++) {
            session.getTransactionContext().beginTxn();

            // We have to reload the digest and each notification
            // inside the new transaction to get around the
            // requirements of persistence.

            try {
                Notification n = new Notification(noticeID[i]);
                n.setDigest(new Digest(id));
                n.save();
            } catch (DataObjectNotFoundException e) {
                // ignore
            }
            session.getTransactionContext().commitTxn();
        }

        session.getTransactionContext().beginTxn();

        try {
            d = new Digest(id);
        } catch(DataObjectNotFoundException e) {
            // ignore
        }

        return d;
    }


    /**
     * Create a digest with multiple notifications to a single user.
     * Assumes an open transaction context at the beginning.
     *
     * @param size is the number of notifications to send
     * @param uid is the ID of the user to send the notifications to
     */

    static Digest getDigest (int size, OID uid) {

        String subject   = "Digest subject";
        String header    = "My Digest";
        String signature = "End of My Digest";

        SessionManager.getSession().getTransactionContext().commitTxn();

        // restart tranaction context
        Session session = SessionManager.getSession();
        TransactionContext txt = session.getTransactionContext();
        txt.beginTxn();

        User to   = getUser(uid);
        User from = getUser();

        Digest digest = new Digest(from, subject, header, signature);
        digest.setFrequency(Digest.HOURLY);
        digest.save();

        for (int i = 0; i < size; i++) {
            Notification n = new Notification
                (digest, to, from, "subject " + i, "body " + i);
            n.setMessageDelete(Boolean.TRUE);
            n.save();
            notificationList.add(n.getOID());
        }
        txt.commitTxn();

        digestList.add(digest.getOID());
        return digest;
    }


    /**
     * Reload a notification from the db
     */

    static Notification getNotification (OID id)
        throws DataObjectNotFoundException
    {
        Notification n = null;

        try {
            Session session = SessionManager.getSession();
            TransactionContext txt = session.getTransactionContext();
            txt.beginTxn();
            n = new Notification(id);
            txt.commitTxn();
        }  catch(DataObjectNotFoundException e) {
            // for testing only
            throw e;
        }

        return n;
    }


    /**
     * Save notification in its own transaction context
     */

    static boolean saveNotification (Notification n) {
        Session session = SessionManager.getSession();

        try {
            session.getTransactionContext().beginTxn();
            n.setMessageDelete(Boolean.TRUE);
            n.save();
            session.getTransactionContext().commitTxn();
        } catch (Exception e) {
            return false;
        }

        return true;
    }


    /**
     * run or rerun a ManagerDispatcher, return on completing
     * a cycle.
     */

    static void runManager(ManagerDispatcher md) {
        if(md.getCycles()!=0)
            md.restart();
        else
            md.start();
        md.waitCycle();
    }


    /**
     * Utility that waits a maxs seconds until the local
     * mail server has received n emails or it fails to do so.
     * Note that you have to call SimpleServer.reset() to reset
     * the mail count if you use this function repeately in a test.
     */

    static boolean assertReceive (int n, int maxs) {
        int waited = 0;

        // System.out.println("***** received " +
        // SimpleServer.getReceivedCount() +
        // ", waiting for " + n);

        while (SimpleServer.getReceivedCount() != n) {
            try {
                Thread.sleep(1000); // wait a sec
            } catch (InterruptedException e) { }
            if (++waited > maxs) {
                return false;
            }
        }
        return true;
    }


    /**
     *  Delete all notifications, digests, and users we allocated in TestSuite
     *  so that the database contains no test junk.
     *
     *  Due to the behavior of persistence, we need to create our own transaction
     *  contexts for every persistent invocation. This might change, however, with
     *  future re-factorings of persistence.
     */
    private static void cleanup() {

        System.out.println("NotificationSuite.cleanup(): removing test data");

        Session session = SessionManager.getSession();
        TransactionContext tx;
        ListIterator it;
        OID oid;

        // delete notifications

        tx = null;
        it = notificationList.listIterator();

        for(int j = 0; j < notificationList.size(); j++) {
            try {
                tx = session.getTransactionContext();
                tx.beginTxn();
                (new Notification((OID)it.next())).delete();
                tx.commitTxn();
            } catch (Exception e) {
                if(tx!=null) {
                    tx.commitTxn();
                }
            }
        }

        // delete digests

        tx = null;
        it = digestList.listIterator();

        for (int j = 0; j < digestList.size(); j++) {
            try {
                tx = session.getTransactionContext();
                tx.beginTxn();
                (new Digest((OID) it.next())).delete();
                tx.commitTxn();
            } catch(Exception e) {
                if(tx!=null) {
                    tx.commitTxn();
                }
            }
        }

        // delete test users

        tx = null;
        it = userList.listIterator();

        for (int j = 0; j < userList.size(); j++) {
            try {
                tx = session.getTransactionContext();
                tx.beginTxn();
                (new User((BigDecimal)it.next())).delete();
                tx.commitTxn();
            } catch(Exception e) {
                if(tx!=null) {
                    tx.commitTxn();
                }
            }
        }
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
