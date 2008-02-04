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

import com.arsdigita.mail.Mail;
import com.arsdigita.mail.SimpleServer;
import java.util.Iterator;

/**
 * Test cases to make sure that Messaging and Mail work together.
 *
 * @version $Id: MailMessageTest.java 744 2005-09-02 10:43:19Z sskracic $
 */

public class MailMessageTest extends MessageTestCase {

    private static String subject = "This is the subject";
    private static String body    = "This is the body";
    private static String type    = "text/plain";

    private static String name    = "My attachment";
    private static String text    = "An attached message";
    private static String html    = "<p>An attached HTML message</p>";

    public MailMessageTest(String name) {
        super(name);
    }


    protected void setUp() {
        super.setUp();

        // Start the SMTP test server
        SimpleServer.startup();
    }


    /**
     * Utility to sleep for a specified number of seconds.
     */

    static private void threadSleep(int seconds) {
        try {
            java.lang.Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * Utility to wait until the correct number of messages have been
     * received by the server.
     */

    static void waitForReceivedCount(int n, int maxwait) {
        int waited = 0;
        while (SimpleServer.getReceivedCount() < n) {
            threadSleep(1);
            if (++waited > maxwait) {
                fail("Waited longer than : " + maxwait);
            }
        }
    }

    /**
     * Utility to send mail and wait until server acknowledges
     * processing.
     */

    static void assertSend(Mail msg) throws javax.mail.MessagingException {
        int n = SimpleServer.getReceivedCount();
        msg.send();
        waitForReceivedCount(n+1, 20);
    }

    /**
     * Create a simple message and convert it an email message
     */

    public void testMailMessage001() {

        Message msg = new Message(from,subject,body);
        msg.save();

        // Compose an email based on this message

        try {
            Mail mail = new Mail();
            mail.setTo("user@somewhere.net");
            mail.setFrom("user@somewhere.net");
            mail.setSubject(msg.getSubject());
            mail.setBody(msg.getBody());

            assertSend(mail);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Create a simple message with an attachment and convert it to an
     * email message
     */

    public void testMailMessage002() {

        Message msg = new Message(from,subject,body);

        // Attach an HTML document to this message

        MessagePart part = new MessagePart();
        msg.attach(part);

        part.setContent(html, MessagePart.TEXT_HTML);
        part.setName("document.html");
        part.setDescription("An attached HTML document");
        part.setDisposition(part.ATTACHMENT);

        msg.save();

        // Try to send an email based on this message

        try {
            getMail(msg).send();
        } catch (javax.mail.MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Attach a simple text document
     */

    public void testMailMessage003() {

        Message msg = new Message(from,subject,body);
        msg.attach("A simple text document", "document.txt");
        msg.save();

        // Try to send an email based on this message

        try {
            getMail(msg).send();
        } catch (javax.mail.MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * A helper method to construct an email from a message.
     */

    private static Mail getMail(Message msg) {

        Mail mail = new Mail();

        try {
            mail.setTo("user@somewhere.net");
            mail.setFrom(msg.getFrom().getPrimaryEmail().toString());
            mail.setSubject(msg.getSubject());
            mail.setBody(msg.getBody());

            // Add the attachments

            Iterator iter = msg.getAttachments();
            while (iter.hasNext()) {
                MessagePart part = (MessagePart) iter.next();
                mail.attach(part.getDataHandler(),
                            part.getDescription(),
                            part.getDisposition());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }

        return mail;
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main(String args[]) {
        junit.textui.TestRunner.run(MailMessageTest.class);
    }

}
