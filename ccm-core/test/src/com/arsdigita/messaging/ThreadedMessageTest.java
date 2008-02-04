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
package com.arsdigita.messaging;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.SiteNode;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;

/**
 * Test cases for threaded messages.
 *
 * @version $Id: ThreadedMessageTest.java 750 2005-09-02 12:38:44Z sskracic $
 */

public class ThreadedMessageTest extends MessageTestCase {

    private static String subject = "This is the subject";
    private static String body    = "This is the body";
    private static String type    = MessageType.TEXT_PLAIN;

    public ThreadedMessageTest(String name) {
        super(name);
    }

    /**
     * Create a simple message using the various set methods.
     */

    public void testMessageCreate001() {
        try {
            ThreadedMessage msg = new ThreadedMessage();

            msg.setFrom(from);
            msg.setSubject(subject);
            msg.setText(body);

            assertEquals(subject,msg.getSubject());
            assertEquals(body,msg.getBody());
            assertTrue(msg.isMimeType(type));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Create a simple message and associate with an ACSObject (the
     * main site node) with it.
     */

    public void testMessageCreate002() {
        ThreadedMessage msg = new ThreadedMessage(from,subject);

        SiteNode node = SiteNode.getRootSiteNode();

        msg.setBody(body,type);
        msg.setRefersTo(node);
        msg.save();

        assertEquals(subject,msg.getSubject());
        assertEquals(body,msg.getBody());
        assertEquals(node.getID(),msg.getRefersTo());
    }

    /**
     * Create a simple message and associate with an ACSObject (the
     * main site node) with it.  Retrieve it from the database and
     * verify that it was saved correctly.
     */

    public void testMessageCreate003() {
        ThreadedMessage msg0 = new ThreadedMessage(from,subject,body);

        SiteNode node = SiteNode.getRootSiteNode();

        msg0.setRefersTo(node);
        msg0.save();

        // Retrieve a copy of it and compare.

        ThreadedMessage msg1;
        try {
            msg1 = new ThreadedMessage(msg0.getOID());

            assertEquals(msg0.getSubject(),  msg1.getSubject());
            assertEquals(msg0.getBody(),     msg1.getBody());
            assertEquals(msg0.getBodyType(), msg1.getBodyType());
            assertEquals(msg0.getRefersTo(), msg1.getRefersTo());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests basic retrieve all. Adds to rows and retrieves them
     * in reverse order.  Uses the root site node as an ACSObject to
     * attach messages to.
     */

    public void testCreateMessage004() {

        SiteNode node = SiteNode.getRootSiteNode();

        ThreadedMessage msg;

        // Create several messages that all refer to the same site
        // node. This simulates messages attached to a single forum in
        // a bboard, for example.

        int count = 10;
        for (int i = 0; i < count; i++) {
            msg = new ThreadedMessage(from,subject,body);
            msg.setRefersTo(node);
            msg.save();
        }

        Session session = SessionManager.getSession();
        DataCollection thread = session.retrieve(ThreadedMessage.BASE_DATA_OBJECT_TYPE);
        thread.addEqualsFilter("objectID", node.getID());
        thread.addOrder("id desc");

        while (thread.next()) {
            msg = new ThreadedMessage(thread.getDataObject());

            assertEquals(msg.getSubject(),  subject);
            assertEquals(msg.getBody(),     body);
            assertEquals(msg.getRefersTo(), node.getID());

            count--;
        }

        assertEquals(count,0);
    }

    /**
     * Test adding several root-level threads:
     *
     * <pre>
     *    - msg0
     *    - msg1
     *    - msg2
     * </pre>
     */

    public void testCreateMessage007() {

        SiteNode node = SiteNode.getRootSiteNode();

        ThreadedMessage msg;

        // Create several messages

        int count = 3;
        for (int i = 0; i < count; i++) {
            msg = new ThreadedMessage(from,subject,body);
            msg.setRefersTo(node);
            msg.save();
        }

        Session session = SessionManager.getSession();
        DataCollection thread = session.retrieve(ThreadedMessage.BASE_DATA_OBJECT_TYPE);
        thread.addEqualsFilter("objectID", node.getID());
        thread.addOrder("id desc");

        // All messages should have both root and sort key set to
        // null.

        while (thread.next()) {
            msg = new ThreadedMessage(thread.getDataObject());

            assertEquals(msg.getRoot(), null);
            assertEquals(msg.getSortKey(), null);
            assertEquals(msg.getDepth(), 0);

            count--;
        }

        assertEquals(count,0);
    }


    /**
     * Testing:
     *
     * <pre>
     *    - msg0
     *      + msg1
     *        + msg2
     *          + ...
     * </pre>
     */

    public void testCreateMessage008() {

        SiteNode node = SiteNode.getRootSiteNode();

        ThreadedMessage msg[] = new ThreadedMessage[30];

        // Create the root messasge

        msg[0] = new ThreadedMessage(from,subject,body);
        msg[0].setRefersTo(node);
        msg[0].save();

        // Create the nested children

        for (int i = 1; i < msg.length; i++) {
            msg[i] = msg[i-1].replyTo(from,body);
            msg[i].save();
        }

        // Verify that the parent message has null values for root and
        // sort key

        assertEquals(msg[0].getRoot(), null);
        assertEquals(msg[0].getSortKey(), null);

        // Verify that all children have msg[0] as the root, refer to
        // the same ACSObject, and have the proper depth based on the
        // order they were created.

        for (int i = 1; i < msg.length; i++) {
            assertEquals(msg[0].getID(), msg[i].getRoot());
            assertEquals(msg[0].getRefersTo(), msg[i].getRefersTo());
            assertEquals(i, msg[i].getDepth());
        }
    }

    /**
     * Verify the structure of the tree (see above).
     */

    public void testCreateMessage009() {

        ThreadedMessage msg[] = prepareTree(from,subject,body);

        // Verify the structure of the tree. We should have 2 level 0
        // messages, 4 level 1 messages, 2 level 3 and 2 level 4.

        int depthCount[] = { 0, 0, 0, 0 };

        for (int i = 0; i < msg.length; i++) {
            depthCount[msg[i].getDepth()]++;
        }

        assertEquals(depthCount[0], 2);
        assertEquals(depthCount[1], 4);
        assertEquals(depthCount[2], 4);
        assertEquals(depthCount[3], 2);

        // printMessageArray(msg);
    }

    /**
     * Verify that a complete tree can be retrieved in the correct
     * order using the "getMessageTree" query.
     */

    public void testCreateMessage010() {

        ThreadedMessage msg[] = prepareTree(from,subject,body);

        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.messaging.getMessageTree");

        // Only select messages that refer to the same ACSObject
        // (everything in our message array does)

        query.addEqualsFilter("object", msg[0].getRefersTo());

        // This is the expected order of keys that will come back from
        // the query.  Use this to verify the results.

        String expectedKey[] = {
            "---",
            "000",
            "001",
            "001000",
            "001001",
            "001001000",
            "001001001",
            "002",
            "002000",
            "002001",
            "003",
            "---"
        };

        assertEquals("Didn't get expected number of messages in tree",
                     12, query.size());

        int i = 0;
        while (query.next()) {
            //printQueryResult(query);
            assertEquals(expectedKey[i++], (String) query.get("sortKey"));
        }
    }

    /**
     * Helper method for debugging.
     */

    private static void printQueryResult (DataQuery query) {
        System.out.println
            ((BigDecimal) query.get("id") + " " +
             (BigDecimal) query.get("root") + " " +
             (String) query.get("sortKey"));
    }


    /**
     * Helper method for debugging.
     */

    private static void printMessageArray (ThreadedMessage msg[]) {
        for (int i = 0; i < msg.length; i++) {
            System.out.println("-----");
            System.out.println("id: " + msg[i].getID());
            System.out.println("subject: " + msg[i].getSubject());
            System.out.println("root: " + msg[i].getRoot());
            System.out.println("sortkey: " + msg[i].getSortKey());
            System.out.println("depth: " + msg[i].getDepth());
        }
    }


    /**
     * Generates the following message structure for testing:
     *
     * <pre>
     *    - msg0        ---
     *      + msg2      000
     *      + msg3      001
     *        + msg5    001000
     *        + msg6    001001
     *          + msg7  001001000
     *          + msg8  001001001
     *      + msg4      002
     *          + msg10 002000
     *          + msg11 002001
     *      + msg9      003
     *    - msg1        ---
     * </pre>
     *
     * @return an array of messages organized in a tree structure
     */

    private static ThreadedMessage[] prepareTree (Party  from,
                                                  String subject,
                                                  String body)
    {
        SiteNode node = SiteNode.getRootSiteNode();

        ThreadedMessage msg[] = new ThreadedMessage[12];

        // Create the root messasges

        for (int i = 0; i <= 1; i++) {
            msg[i] = new ThreadedMessage(from,subject,body);
            msg[i].setRefersTo(node);
            msg[i].save();
        }

        // Create the children of msg[0]

        for (int i = 2; i <= 4 ; i++) {
            msg[i] = msg[0].replyTo(from,body);
            msg[i].save();
        }

        // Create the children of msg[3]

        for (int i = 5; i <= 6; i++) {
            msg[i] = msg[3].replyTo(from,body);
            msg[i].save();
        }

        // Create the children of msg[6]

        for (int i = 7; i <= 8; i++) {
            msg[i] = msg[6].replyTo(from,body);
            msg[i].save();
        }

        // Create a final reply to msg[0]

        msg[9] = msg[0].replyTo(from,body);
        msg[9].save();

        // Create children of msg[4]

        for (int i = 10; i <= 11; i++) {
            msg[i] = msg[4].replyTo(from,body);
            msg[i].save();
        }

        return msg;
    }

    /**
     * Test container relationships.
     */

    public void testMessageContainment() {

        SiteNode node = SiteNode.getRootSiteNode();

        ThreadedMessage msg = new ThreadedMessage(from,subject,body);
        msg.setRefersTo(node);
        msg.save();

        assertEquals(node.getID(), msg.getRefersTo());

        ACSObject container = msg.getContainer();
        assertEquals(node.getID(), container.getID());

        // Verify that the null case works
        ThreadedMessage msg2 = new ThreadedMessage(from,subject,body);
        assertEquals(null, msg2.getContainer());

        msg.setRefersTo(msg2);
        assertTrue  (msg.isContainerModified());
        assertEquals(msg2.getID(), msg.getContainer().getID());
    }


    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(TextMessageTest.class);
    }

}
