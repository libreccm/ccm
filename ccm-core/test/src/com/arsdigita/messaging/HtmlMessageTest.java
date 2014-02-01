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

/**
 * Test cases for HTML messages.
 *
 * @version $Id: HtmlMessageTest.java 1940 2009-05-29 07:15:05Z terry $
 */

public class HtmlMessageTest extends MessageTestCase {


    private static String subject = "This is the subject";
    private static String body    = "<p>This is the <b>body</b></p>";
    private static String type    = MessageType.TEXT_HTML;

    public HtmlMessageTest(String name) {
        super(name);
    }

    /**
     * Create a simple message using the various set methods.
     */


    public void testMessageCreate001() {
        try {
            Message msg = new Message();

            msg.setFrom(from);
            msg.setSubject(subject);
            msg.setBody(body,type);

            assertEquals(subject,msg.getSubject());
            assertEquals(body,msg.getBody());
            assertEquals(type,msg.getBodyType());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Create a simple message using the default constructor.
     */

    public void testMessageCreate002() {
        try {
            Message msg = new Message(from,subject);
            msg.setBody(body,type);

            assertEquals(subject,msg.getSubject());
            assertEquals(body,msg.getBody());
            assertEquals(type,msg.getBodyType());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Create a simple message and save it to the database.
     */

    public void testCreateMessage003() {
        try {
            Message msg = new Message(from,subject);
            msg.setBody(body,type);
            msg.save();

            // Retrieve a copy of the message and verify its contents

            Message msg2 = new Message(msg.getOID());

            assertEquals(msg.getSubject(),  msg2.getSubject());
            assertEquals(msg.getBody(),     msg2.getBody());
            assertEquals(msg.getBodyType(), msg2.getBodyType());

            // Delete the original message

            msg.delete();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(HtmlMessageTest.class);
    }

}
