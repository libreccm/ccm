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
import com.arsdigita.kernel.User;
import com.arsdigita.mail.ByteArrayDataSource;
import com.arsdigita.messaging.Message;
import com.arsdigita.messaging.MessagePart;
import com.arsdigita.persistence.OID;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import javax.activation.DataHandler;

/**
 * Test suite for notifications. Constructors and simple setXXX, getXXX methods are
 * tested.
 *
 * @version $Id: NotificationTest.java 1940 2009-05-29 07:15:05Z terry $
 */

public class NotificationTest extends BaseTestCase {


    User to;
    User from;

    final static String subject = "this is the subject";
    final static String body    = "this is the body";

    public NotificationTest(String name) {
        super(name);
    }

    public void setUp() {
        to   = NotificationSuite.getUser();
        from = NotificationSuite.getUser();
    }

    /**
     * Test default constructor
     */
    public void testNotificationCreate() {
        Notification n = new Notification(to,from,subject,body);
        assertNotNull(n);
        n.save();
    }

    /**
     * Test the save (update) method
     */
    public void testNotificationUpdate() {
        Notification n = new Notification(to,from,subject,body);
        n.setTo(NotificationSuite.getUser());
        n.save();
    }

    /**
     * Test the delete method
     */
    public void testNotificationDelete() {
        Notification n = new Notification(to,from,subject,body);
        n.save();
        n.delete();
    }

    /**
     * Test retrieving an existing notification from the database.
     */
    public void testNotificationRetrieve() {
        Notification n1 = new Notification(to,from,subject,body);
        n1.save();

        try {
            Notification n2 = new Notification(n1.getOID());
            assertEquals(n1.getOID(), n2.getOID());
        } catch (DataObjectNotFoundException e) {
            fail(e.getMessage());
        }

    }

    /**
     * Test sending to multiple recipients (not a group)
     */
    public void testMultipleRecipients() {
        Message msg = new Message(from, subject, body);
        msg.save();

        for (int i = 0; i < 10; i++) {
            Notification n = new Notification(to,msg);
            n.save();
        }
    }


    /**
     * Test automatic deletion of an internal Message object
     *  when setMessageDelete(true)
     */
    public void testSetMessageDelete001() {
        Message msg = new Message(from,subject,body);
        msg.save();

        OID oid = msg.getOID();

        Notification n = new Notification(to,msg);
        n.setMessageDelete(Boolean.TRUE);
        n.save();

        assertEquals(Boolean.TRUE, n.getMessageDelete());

        n.delete();

        // retrieving the message from the database should throw an
        // exception
        try {
            msg = new Message(oid);
            fail("Should not be able to load message associated to a "+
                 "notification on which setMessageDelete(true) was called.");
        } catch (DataObjectNotFoundException e) {
            // correct, we expected that
        }
    }

    /**
     * opposite of 006, test if message is preserved if we delete the
     * notification
     *
     */
    public void testSetMessageDelete002() {
        Message msg = new Message(from,subject,body);
        msg.save();
        OID oid = msg.getOID();
        Notification n = new Notification(to,msg);
        n.setMessageDelete(Boolean.FALSE);
        n.save();

        assertEquals(Boolean.FALSE, n.getMessageDelete());

        n.delete();

        // retrieving the message from the database should work
        try {
            msg = new Message(oid);
        } catch (DataObjectNotFoundException e) {
            fail("Should be able to load message associated to a "+
                 "notification on which setMessageDelete(false) was called.");
        }
    }


    /**
     * test default settings after creation of a simple notification
     * before scheduling it.
     */

    public void testDefaultSettings() {

        Notification n = new Notification(to,from,subject,body);
        // by default ...

        // fullfill date should be null
        assertNull(n.getFulfillDate());



        // respective message should be deleted
        assertEquals(Boolean.TRUE, n.getMessageDelete());

        /*
          fails return null instead of false

          // a request is expunged after processed
          assertEquals(Boolean.FALSE, n.getPermanent());

          // request date should be null
          assertNull(n.getRequestDate());

          // status should be pending
          assertEquals(n.PENDING, n.getStatus());

        */
    }

    /**
     * Test notification with an "iso-8859-1" attachement
     */

    public void testAttachments() {

        String text     = "Umlaute "+'\u00C4'+'\u00D6'+'\u00DC';
        String name     = "strings.txt";
        String encoding = "iso-8859-1";

        // Create a DataHandler for our attachment

        DataHandler dh = new DataHandler
            (new ByteArrayDataSource
             (text, MessagePart.TEXT_PLAIN, name, encoding));

        MessagePart part = new MessagePart();
        part.setDataHandler(dh);
        part.setDescription("an iso-8859-1 attachment");

        Message msg = new Message(from, subject + " with attachment", body);
        msg.attach(part);
        msg.save();

        Notification n = new Notification(to,msg);
        n.save();
    }

    /**
     * Test using the setHeader and setSignature methods
     */

    public void testHTMLHeaderFooter() {
        Message msg = new Message();
        msg.setFrom(from);
        msg.setSubject(subject);
        msg.setBody("<p>an HTML message</p>", msg.TEXT_HTML);
        msg.save();

        Notification n = new Notification(to,msg);
        n.setHeader("<< HEADER >>");
        n.setSignature("<< SIGNATURE >>");
        n.save();
    }

    /**
     * Test using the setHeader and setFooter methods with text/plain
     * messages.
     */

    public void testPlainTextHeaderFooter() {

        Notification n = new Notification(to,from,subject,body);
        n.setHeader("<< HEADER >>");
        n.setSignature("<< SIGNATURE >>");
        n.save();

        Message msg = new Message(from,subject);
        msg.setBody(body, msg.TEXT_PREFORMATTED);

        n.setMessage(msg);
        n.setHeader("<< HEADER >>");
        n.setSignature("<< SIGNATURE >>");
        n.save();
    }

    /**
     * Test conversion of HTML messages to plain text.
     */

    public void testHtmlConversion() {

        String header = "--HEADER--";
        String signature = "--SIGNATURE--";
        String body = "a <b>simple</b> body";

        // This should be the complete content of the email
        String msgBody =
            header + "\na *simple* body\n" + signature + "\n";

        Message msg = new Message(from,subject);
        msg.setBody(body, msg.TEXT_HTML);

        Notification n = new Notification(to,from,subject,body);
        n.setHeader(header);
        n.setSignature(signature);
        n.setMessage(msg);
        n.save();

        // Verify the message is constructed in the correct
        // format. Because of the way QueueItems are handled
        // internally, we need to create, save and retrieve one from
        // the database to perform this check.

        try {
            QueueItem q = new QueueItem(n,to);
            q.save();
            q = new QueueItem(q.getOID());
            assertEquals(msgBody, q.getBody());
        } catch(DataObjectNotFoundException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main(String args[]) {
        junit.textui.TestRunner.run(NotificationTest.class);
    }
}
