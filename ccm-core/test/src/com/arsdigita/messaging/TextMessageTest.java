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
 * Test cases for plain text messages.
 *
 * @version $Id: TextMessageTest.java 1940 2009-05-29 07:15:05Z terry $
 */

public class TextMessageTest extends MessageTestCase {


    private static String subject = "This is the subject";
    private static String body    = "This is the body";
    private static String type    = MessageType.TEXT_PLAIN;

    public TextMessageTest(String name) {
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
            msg.setText(body);

            assertEquals(subject,msg.getSubject());
            assertEquals(body,msg.getBody());
            assertTrue(msg.isMimeType(type));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Create a simple message using the default constructor.
     */

    public void testMessageCreate002() {
        try {
            Message msg = new Message(from,subject,body);

            assertEquals(subject,msg.getSubject());
            assertEquals(body,msg.getBody());
            assertTrue(msg.isMimeType(type));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Create a simple message and use the setText() method.
     */

    public void testMessageCreate003() {
        try {
            Message msg = new Message(from,subject);

            msg.setText(body);

            assertEquals(subject,msg.getSubject());
            assertEquals(body,msg.getBody());
            assertTrue(msg.isMimeType(type));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Create a simple message and save it to the database.
     */

    public void testMessageCreate004() {
        try {
            Message msg = new Message(from,subject,body);
            msg.save();

            // Retrieve a copy of the message and verify its contents

            Message msg2 = new Message(msg.getOID());
            assertEquals(msg.getSubject(), msg2.getSubject());
            assertEquals(msg.getBody(), msg2.getBody());

            // Repeat using the BigDecimal constructor

            msg2 = new Message(msg.getID());
            assertEquals(msg.getSubject(), msg2.getSubject());
            assertEquals(msg.getBody(), msg2.getBody());

            // Delete the original message

            msg.delete();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    /**
     * Verify that the default constructor and public set methods can
     * be used to create a Message.  This is essentially a test that
     * the sent date is handled correctly.
     */

    public void testMessage003() {
        Message msg = new Message();
        msg.setFrom(from);
        msg.setText(body);
        msg.setSubject(subject);
        msg.save();
    }

    /**
     * Verify that the sent date can be set explicitly
     */

    public void testMessage004() {
        Message msg = new Message(from,subject,body);
        msg.setSentDate(new java.util.Date());
        msg.save();
    }

    /**
     * Verify that saving a message with a null sender fails.
     */

    public void testMessageCreate005() {
        try {
            Message msg = new Message();
            msg.save();
            fail("Test should have failed because of an invalid party");
        } catch (Exception ex) {
            // nothing
        }
    }

    /**
     * Verify that messages can refer to other ACSObjects.  For
     * convenience this test is written to refer to another Message,
     * but it could that extends ACSObject.
     */

    public void testMessageCreate006() {

        Message msg1 = new Message(from, subject, body);
        msg1.save();

        Message msg2 = new Message(from, subject, body);

        // Test the ACSObject method
        msg2.setRefersTo(msg1);
        msg2.save();

        // Test the BigDecimal method
        msg2.setRefersTo(msg1.getID());
        msg2.save();
    }

    /**
     * Verify that messages can be stored in one of the simple text
     * formats (plain or preformatted).
     */

    public void testMessageCreate007() {

        Message msg = new Message(from, subject, body);
        assertTrue(msg.isMimeType(MessageType.TEXT_PLAIN));

        msg.setBody(body, MessageType.TEXT_PREFORMATTED);
        assertTrue(msg.isMimeType(MessageType.TEXT_PREFORMATTED));
    }

    /**
     * Test the required equivalence of the various MIME types.
     */

    public void testMessageCreate008() {

        String mimeType[] = new String [] {
            MessageType.TEXT_PLAIN,
            MessageType.TEXT_PREFORMATTED,
            MessageType.TEXT_HTML
        };

        Message msg = new Message(from, subject, body);

        msg.setBody(body, MessageType.TEXT_PLAIN);
        checkMimeType(msg, mimeType, new boolean[] { true, true, false });

        msg.setBody(body, MessageType.TEXT_PREFORMATTED);
        checkMimeType(msg, mimeType, new boolean[] { true, true, false });

        msg.setBody(body, MessageType.TEXT_HTML);
        checkMimeType(msg, mimeType, new boolean[] { false, false, true });
    }

    private static void checkMimeType (Message   msg,
                                       String [] type,
                                       boolean[] expected)
    {
        for (int i = 0; i < type.length; i++)
            assertEquals(expected[i], msg.isMimeType(type[i]));
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(TextMessageTest.class);
    }

}
