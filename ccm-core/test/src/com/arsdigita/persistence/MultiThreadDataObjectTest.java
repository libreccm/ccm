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
package com.arsdigita.persistence;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * MultiThreadDataObjectTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #14 $ $Date: 2004/08/16 $
 **/

public class MultiThreadDataObjectTest extends PersistenceTestCase {

    public final static String versionId = "$Id: MultiThreadDataObjectTest.java 745 2005-09-02 10:50:34Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(MultiThreadDataObjectTest.class);

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/static/Node.pdl");
        load("com/arsdigita/persistence/testpdl/mdsql/Party.pdl");
        super.persistenceSetUp();
    }

    public MultiThreadDataObjectTest(String name) {
        super(name);
    }

    private static final int START = 10000;
    private static final int NUM_OBJECTS = 10;

    public Map createObjectsInOtherThread() throws InterruptedException {
        final Map objects = new HashMap();

        Thread thread = new Thread() {
                public void run() {
                    Session ssn = SessionManager.getSession();
                    TransactionContext txn = ssn.getTransactionContext();
                    txn.beginTxn();

                    for (int i = START; i < NUM_OBJECTS + START; i++) {
                        DataObject node = ssn.create("examples.Node");
                        node.set("id", new BigDecimal(i));
                        node.set("name", "Node " + i);
                        node.save();
                        objects.put(node.get("id"), node);
                    }

                    txn.commitTxn();
                }
            };

        thread.start();
        thread.join();

        return objects;
    }

    public void testMultipleThreads() throws InterruptedException {
        try {
            Map objects = createObjectsInOtherThread();

            for (Iterator it = objects.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry me = (Map.Entry) it.next();
                BigDecimal id = (BigDecimal) me.getKey();
                DataObject node = (DataObject) me.getValue();

                assertEquals("incorrect id", id, node.get("id"));
                assertEquals("incorrect name", "Node " + id, node.get("name"));
                assertEquals("incorrect parent", null, node.get("parent"));

                try {
                    node.delete();
                    fail("Shouldn't be able to delete an object " +
                         "retrieved in another transaction.");
                } catch (PersistenceException e) {
                    // Do nothing
                }
            }
        } catch (RuntimeException re) {
            s_log.warn("got RuntimeException", re);
            throw re;
        } catch (Error e) {
            s_log.warn("got Error", e);
            throw e;
        } finally {
            Session ssn = SessionManager.getSession();

            DataCollection nodes = ssn.retrieve("examples.Node");
            while (nodes.next()) {
                nodes.getDataObject().delete();
            }

            ssn.getTransactionContext().commitTxn();
        }

    }


    /**
     *  We want to test using objects across multiple transactions and
     *  we want to make sure that it throws an error when we try to do it
     */
    public void testMultipleTransaction() throws java.sql.SQLException {

        // we get two sessions to guarantee to get two transactions.
        // we have to get teh second one first so make sure that we
        // keep the stack trace

        DataQuery query = getSession().retrieveQuery("examples.nodesQuery");
        long size = query.size();

        DataObject node = getSession().create("examples.Node");
        node.set("id", new BigDecimal(START));
        node.set("name", "Root");
        node.save();

        // let's see if it is there
        assertTrue("The insert should have added a node but it did not.",
               query.size() == size + 1);

        // abort the transaction and open a new transaction
        getSession().getTransactionContext().abortTxn();
        getSession().getTransactionContext().beginTxn();

        try {
            BigDecimal a = (BigDecimal)node.get("id");
            fail("Retrieving information from a DataObject outside of the " +
                 "original transaction should throw an error.");
        } catch (PersistenceException e) {
            // this should happen
        }

        try {
            node.delete();
            fail("Deleting a DataObject outside of the original transaction " +
                 "should throw an error.");
        } catch (PersistenceException e) {
            // this should happen
        }
    }

    public void testTransactionState() {
        try {
            int id = 1;
            // Create a node, and verify that it is in a valid
            // state after creation
            Session ssn = SessionManager.getSession();
            TransactionContext txn = ssn.getTransactionContext();
            DataObject node = ssn.create("examples.Node");
            node.set("id", new BigDecimal(id++));
            node.set("name", "Node ");
            assertTrue(node.isValid() && !node.isDisconnected());

            // Verify that the node is invalid and disconnected
            // after aborted transaction.
            txn.abortTxn();
            assertTrue(!node.isValid() && node.isDisconnected());
            // Verify that setting a property of an invalid  node
            // is disallowed.
            try {
                node.set("name", "foobar");
                fail("Updated a node that is disconnected!");
            } catch (PersistenceException e) {
            }

            // Verify that saving a invalid node
            // is disallowed.
            try {
                node.save();
                fail("Saved a node that is disconnected!");
            } catch (PersistenceException e) {
            }

            txn.beginTxn();
            node = ssn.create("examples.Node");
            assertTrue(node.isValid() && !node.isDisconnected());
            BigDecimal savedId =  new BigDecimal(id++);
            node.set("id", savedId);
            node.set("name", "SavedNode");
            OID savedOID = node.getOID();
            node.save();
            txn.commitTxn();
            assertTrue(node.isValid() && node.isDisconnected());

            // Verify that setting a property of a disconnected  node
            // is disallowed.
            try {
                node.set("name", "foobar");
                fail("Updated a node that is disconnected!");
            } catch (PersistenceException e) {
            }

            // Verify that saving a disconnected node
            // is disallowed.
            try {
                node.save();
                fail("Saved a node that is disconnected!");
            } catch (PersistenceException e) {
            }

            txn.beginTxn();
            node = ssn.retrieve(savedOID);
            txn.commitTxn();
            assertTrue(node.isValid() && node.isDisconnected());
            assertEquals("Disconnected Lazy load failed on ID!", savedId, node.get("id") );
            assertEquals("Disconnected Lazy load failed on name!", "SavedNode", node.get("name") );

        } finally {
            Session ssn = SessionManager.getSession();
            TransactionContext txn = ssn.getTransactionContext();
            if(txn.inTxn()) {
                txn.abortTxn();
            }
            txn.beginTxn();

            DataCollection nodes =  getSession().retrieve("examples.Node");
            while(nodes.next()) {
                DataObject deadNode = nodes.getDataObject();
                deadNode.delete();
            }

            txn.commitTxn();

        }

    }

    public void testAssociationTransactionState() {
        class ID {
            private int m_id = START;
            public BigInteger next() {
                return new BigInteger(Integer.toString(m_id++));
            }
        }
        ID id = new ID();

        Session ssn = SessionManager.getSession();
        TransactionContext txn = ssn.getTransactionContext();

        try {

            DataObject color = ssn.create("mdsql.Color");
            color.set("id", id.next());
            color.set("name", "blue");

            DataObject jon = makeUser(ssn,
                                      id.next(),
                                      "jorris@arsdigita.com",
                                      "Jon",
                                      "Orris",
                                      "Stuff!",
                                      color);

            jon.save();
            DataObject qa = makeGroup(ssn, id.next(), "qa@arsdigita.com", "QA");

            DataAssociation members = (DataAssociation) qa.get("members");
            members.add(jon);
            qa.save();

            txn.commitTxn();
            assertTrue(qa.isValid() && qa.isDisconnected());
            assertTrue(jon.isValid() && jon.isDisconnected());
            assertTrue(color.isValid() && color.isDisconnected());
            try {
                jon.set("firstName", "Fred!");
                fail("Set value of a disconnected object!");
            } catch (PersistenceException e) {
            }

            try {
                qa.set("name", "!QA");
                fail("Set value of a disconnected object!");
            } catch (PersistenceException e) {
            }

            try {
                color.set("name", "purple");
                fail("Set value of a disconnected object!");
            } catch (PersistenceException e) {
            }

            txn.beginTxn();
            DataObject matt = makeUser(ssn,
                                       id.next(),
                                       "mboland@arsdigita.com",
                                       "Matt",
                                       "Boland",
                                       "Stuff!",
                                       null);
            matt.save();
            members.add(matt);

            try {
                qa.save();
                fail("Saved a disconnected object!");
            } catch (PersistenceException e) {
            }

            txn.abortTxn();
            //assertTrue(members.isValid() && members.isDisconnected());

        } catch (RuntimeException re) {
            s_log.warn("got RuntimeException", re);
            throw re;
        } catch (Error e) {
            s_log.warn("got Error", e);
            throw e;
        } finally {
            if( txn.inTxn() ) {
                txn.abortTxn();
            }
            txn.beginTxn();
            DataCollection users =  getSession().retrieve("mdsql.User");
            while(users.next()) {
                DataObject dead = users.getDataObject();
                DataAssociationCursor cursor = ((DataAssociation) dead.get("groups")).cursor();
                while(cursor.next()) {
                    cursor.remove();
                }
                DataObject color = (DataObject) dead.get("favorateColor");
                if(color != null) {
                    dead.set("favorateColor", null);
                    dead.save();
                    color.delete();
                }
                dead.delete();
            }

            DataCollection groups =  getSession().retrieve("mdsql.Group");
            while(groups.next()) {
                DataObject dead = groups.getDataObject();
                DataAssociationCursor cursor = ((DataAssociation) dead.get("members")).cursor();
                while(cursor.next()) {
                    cursor.remove();
                }
                dead.delete();
            }

            txn.commitTxn();

        }




    }

    private DataObject makeUser(Session ssn,
                                BigInteger id,
                                String email,
                                String firstName,
                                String lastNames,
                                String bio,
                                DataObject color)
    {
        DataObject user = ssn.create("mdsql.User");
        user.set("id", id);
        user.set("email", email);
        user.set("firstName", firstName);
        user.set("lastNames", lastNames);
        if( null != bio ) {
            user.set("bio",bio);
        }
        if (null != color) {
            user.set("favorateColor", color);
        }
        return user;

    }


    private DataObject makeGroup(Session ssn, BigInteger id, String email, String name) {
        DataObject group = ssn.create("mdsql.Group");
        group.set("id", id);
        group.set("email", email);
        group.set("name", name);

        return group;

    }

    public void testRefetchOnInvalidation() {
        DataObject test, icle;
        final OID ICLE = new OID("test.Icle",
                                 new BigInteger(Integer.toString(START)));
        final OID TEST = new OID("test.Test",
                                 new BigInteger(Integer.toString(START)));

        Session ssn = SessionManager.getSession();
        TransactionContext txn = ssn.getTransactionContext();

        try {
            icle = ssn.create(ICLE);
            icle.save();

            test = ssn.create(TEST);
            test.set("required", icle);
            test.save();

            test = ssn.retrieve(TEST);

            txn.commitTxn();
            txn.beginTxn();

            assertTrue("test was not disconnected", test.isDisconnected());
            test = ssn.retrieve(TEST);
            icle = (DataObject) test.get("required");
            icle.set("name", "will abort");

            txn.abortTxn();
            txn.beginTxn();

            assertTrue("icle was not invalidated", !icle.isValid());
            icle = (DataObject) test.get("required");
            assertTrue("icle was not refetched", icle.isValid());

            txn.abortTxn();
        } catch (RuntimeException re) {
            s_log.warn("got RuntimeException", re);
            throw re;
        } catch (Error e) {
            s_log.warn("got Error", e);
            throw e;
        } finally {
            if (!txn.inTxn()) {
                txn.beginTxn();
            }

            test = ssn.retrieve(TEST);
            if (test != null) {
                test.delete();
            }
            icle = ssn.retrieve(ICLE);
            if (icle != null) {
                icle.delete();
            }

            txn.commitTxn();
            txn.beginTxn();
        }
    }

}
