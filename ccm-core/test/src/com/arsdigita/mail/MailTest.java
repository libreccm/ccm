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
package com.arsdigita.mail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.activation.DataHandler;
import javax.mail.MessagingException;
import junit.framework.TestCase;

/**
 * Unit tests for the ACS Mail service.  Note that these tests only
 * check the process of constructing an email, not actually sending
 * one.  Even the construction is pretty lightweight because content
 * items like attachments aren't accessed until you invoke the
 * Mail.send() or Mail.writeTo() methods.
 *
 * NOTE: All of the tests involving attachments from Files are
 * currently commented out.  This isn't because File attachments don't
 * work -- they do.  It's because I haven't figured out yet how to
 * access relatives files correctly from within a unit test.
 *
 * @version $Id: MailTest.java 1940 2009-05-29 07:15:05Z terry $
 */

public class MailTest extends TestCase {


    // Data for the test cases

    final static String to    = "to@somedomain.net";
    final static String reply = "reply@somedomain.net";
    final static String from  = "from@somedomain.net";
    final static String cc    = "cc@somedomain.net";
    final static String bcc   = "bcc@somedomain.net";

    final static String subject = "this is the subject";

    final static String text = "this is the body in plain text.\n";
    final static String html = "<p>this is the body in <b>HTML</b>.</p>\n";

    // Source data for attachment tests

    final static String TEST_IMAGE =
        System.getProperty("test.base.dir") +
        "/com/arsdigita/mail/attch.gif";
    final static String TEST_FILE =
        System.getProperty("test.base.dir") +
        "/com/arsdigita/mail/attch.html";


    public MailTest(String name) {
        super(name);
    }

    protected void setUp() {
        SimpleServer.startup();
    }

    /**
     * Utility to sleep for a specified number of seconds.
     */

    static private void threadSleep (int seconds)
    {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * Utility to wait until the correct number of messages have been
     * received by the server.
     */

    static void waitForReceivedCount (int n, int maxwait) {
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

    static void assertSend (Mail msg) throws MessagingException {
        int n = SimpleServer.getReceivedCount();

        msg.send();

        // only wait for local server - I needed to test also on a
        // real server

        if (Mail.getSmtpServer().equalsIgnoreCase("localhost"))
            waitForReceivedCount(n+1, 20);
    }

    /**
     * Simple plain text message
     */

    public static void testMessage001 () {
        try {
            Mail msg = new Mail(to, from, subject, text);
            assertSend(msg);
        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Simple plain text message with additional recipient info
     */

    public static void testMessage002 () {
        try {
            Mail msg = new Mail(to, from, subject, text);
            msg.setReplyTo(reply);
            msg.setCc(cc);
            msg.setBcc(bcc);
            assertSend(msg);
        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Simple plain text message with multiple recipients
     */

    public static void testMessage003 () {

        String to =
            "person1@somedomain.net, " +
            "Person2 person2@somedomain.net, " +
            "Person Three person3@somedomain.net";

        try {
            Mail msg = new Mail(to, from, subject, text);
            assertSend(msg);
        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }


    /**
     * Simple plain text message with additional headers
     */

    public static void testMessage004 () {
        try {
            Mail msg = new Mail(to, from, subject, text);
            msg.addHeader("Priorty", "low");
            assertSend(msg);
        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Rich text message (HTML and plain text alternative)
     */

    public static void testMessage005FAILS () {
        try {
            Mail msg = new Mail(to, from, subject);
            msg.setBody (html, text);
            assertSend(msg);
        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Plain text message with an attached image from a local file.
     */

    public static void testMessage006 () {
        try {
            Mail msg = new Mail(to, from, subject);
            msg.setBody (text);

            File file = new File(TEST_IMAGE);
            msg.attach (file, "attch.gif", "Sample Image");

            assertSend(msg);

        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Rich text message with an attached image
     */

    public static void testMessage007 () {
        try {
            Mail msg = new Mail(to, from, subject);
            msg.setBody (html,text);

            File file = new File(TEST_IMAGE);
            msg.attach (file, "attch.gif", "Sample Image");

            assertSend(msg);

        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Plain text message with an attached image from a URL
     */

    public static void testMessage008 () {
        try {
            Mail msg = new Mail(to, from, subject);
            msg.setBody (text);

            try {

                URL url = new URL(new File(TEST_IMAGE).toURL().toExternalForm());
                msg.attach(url, "logo.gif", "ArsDigita Logo");
            } catch (java.net.MalformedURLException e) {
                fail(e.getMessage());
            }

            assertSend(msg);

        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Plain text message with an attached HTML document
     */

    public static void testMessage009 () {
        try {
            Mail msg = new Mail(to, from, subject);
            msg.setBody (text);

            File file = new File(TEST_FILE);
            msg.attach(file, "attch.html", "Sample HTML document");

            assertSend(msg);

        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Plain text message with an attached HTML document (in-memory)
     */

    public static void testMessage010 () {
        try {
            // Our HTML document
            String hdoc = "<p>this is an attached HTML document</p>";
            String type = Mail.TEXT_HTML;
            String name = "A document";

            Mail msg = new Mail(to, from, subject);
            msg.setBody (text);
            msg.attach  (hdoc, Mail.TEXT_HTML, "A document");
            assertSend(msg);

        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Plain text message with an attached HTML document from a byte-array
     */

    public static void testMessage011 () {
        try {
            // Our HTML document
            String hdoc = "<p>this is an attached HTML document</p>";
            byte[] data = hdoc.getBytes();
            String type = Mail.TEXT_HTML;
            String name = "A document";

            Mail msg = new Mail(to, from, subject);
            msg.setBody(text);
            msg.attach (data, type, name);
            assertSend(msg);

        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Plain text message with an attached HTML document from a DataHandler
     */

    public static void testMessage012 () {
        try {
            // Our HTML document
            String hdoc = "<p>this is an attached HTML document</p>";
            String type = Mail.TEXT_HTML;
            String name = "A document";

            DataHandler dataHandler = new DataHandler
                (new ByteArrayDataSource(hdoc,type,name));

            Mail msg = new Mail(to, from, subject);
            msg.setBody(text);
            msg.attach (dataHandler);
            assertSend(msg);

        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * A test for the simple static convenience method
     */

    public static void testMessage013 () {
        try {
            Mail.send(to,from,subject,text);
        } catch (MessagingException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(MailTest.class);
    }

}
