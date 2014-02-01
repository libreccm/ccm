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

import javax.mail.MessagingException;
import junit.framework.TestCase;

/**
 * Unit tests for the ACS Mail service MailQueue class.  Currently
 * tests are only provided for creating and storing messages in the
 * queue, but not actually sending them.
 *
 * @version $Id: MailQueueTest.java 1940 2009-05-29 07:15:05Z terry $
 */

public class MailQueueTest extends TestCase {


    // Data for the test cases

    final static String to      = "to@somedomain.net";
    final static String from    = "from@somedomain.net";
    final static String subject = "this is the subject";
    final static String text    = "this is the body in plain text.\n";

    public MailQueueTest(String name) {
        super(name);
    }

    protected void setUp() {
        SimpleServer.startup();
    }

    /**
     * Check the API for adding messages to the queue.
     */

    public static void testMailQueue001 () {

        MailQueue queue = new MailQueue();

        for (int i = 0 ; i < 10; i++) {
            queue.addMail
                (new Mail(to, from, subject,
                          "Message " + i + ": " + text));
        }

        // Make sure they all made it
        assertTrue(queue.getCount() == 10);

        // Try sending all messages

        try {
            queue.send();
        } catch (MessagingException mex) {
            fail(mex.getMessage());
        }
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(MailQueueTest.class);
    }

}
