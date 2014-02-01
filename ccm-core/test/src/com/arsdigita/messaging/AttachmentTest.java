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

import java.io.File;
import java.util.Iterator;

/**
 * Test cases for attachments.
 *
 * @version $Id: AttachmentTest.java 1940 2009-05-29 07:15:05Z terry $
 */

public class AttachmentTest extends MessageTestCase {


    private static String subject = "This is the subject";
    private static String body    = "This is the body";
    private static String type    = "text/plain";

    private static String name    = "MyAttachment";
    private static String text    = "An attached message";
    private static String html    = "<p>An attached HTML message</p>";

    final static String TEST_IMAGE =
        "attch.gif";
    final static String TEST_IMAGE_PATH =
        System.getProperty("test.base.dir") + "/com/arsdigita/messaging/" + TEST_IMAGE;
    final static String TEST_IMAGE_TYPE =
        "image/gif";

    final static String TEST_FILE =
        "attch.html";
    final static String TEST_FILE_PATH =
        System.getProperty("test.base.dir") + "/com/arsdigita/messaging/" + TEST_FILE;
    final static String TEST_FILE_TYPE =
        MessageType.TEXT_HTML;

    public AttachmentTest(String name) {
        super(name);
    }

    /**
     * Create a simple message using the various set methods and
     * attach something to it.
     */

    public void testAttachment001() {

        Message msg = new Message(from,subject,body);

        // Attach something simple to the message
        msg.attach(text, name);

        // When we save the message, all attachments will become
        // persistent.
        msg.save();
    }

    /**
     * Try attaching several things and then iterating over them.
     */

    public void testAttachment002() {

        Message msg = new Message(from,subject,body);
        msg.attach(text,name);
        msg.attach(text,name);
        msg.save();

        Iterator parts = msg.getAttachments();

        while (parts.hasNext()) {
            MessagePart part = (MessagePart) parts.next();
            assertEquals(name, part.getName());
        }
    }

    /**
     * Try attaching a part that was created separately.
     */

    public void testAttachment003() {

        MessagePart part = new MessagePart();
        part.setName(name);
        part.setText(text);

        Message msg = new Message(from,subject,body);
        msg.attach(part);
        msg.save();

        assertEquals(1, msg.getAttachmentCount());
    }

    /**
     * Try attaching an HTML message.
     */

    public void testAttachment004() {

        MessagePart part = new MessagePart(name, null);
        part.setContent(html,MessagePart.TEXT_HTML);

        Message msg = new Message(from,subject,body);
        msg.attach(part);
        msg.save();

        assertEquals(1, msg.getAttachmentCount());

    }

    /**
     * Try attaching an image from a file.
     */

    public void testAttachment005() {

        File image = new File(TEST_IMAGE_PATH);

        MessagePart part = new MessagePart();
        part.setContent(image, TEST_IMAGE, "A sample image");

        Message msg = new Message(from,subject,body);
        msg.attach(part);
        msg.save();

        // Verify that we have one attachment
        assertEquals(1, msg.getAttachmentCount());

        // Retrieve it and verify the contents
        part = (MessagePart) msg.getAttachments().next();
        assertEquals(TEST_IMAGE, part.getName());
        assertEquals(TEST_IMAGE_TYPE, part.getContentType());
    }

    /**
     * Try attaching an HTML document from a file.
     */

    public void testAttachment006() {

        File image = new File(TEST_FILE_PATH);

        MessagePart part = new MessagePart();
        part.setContent(image, TEST_FILE, "A sample document");

        Message msg = new Message(from,subject,body);
        msg.attach(part);
        msg.save();

        // Verify that we have one attachment
        assertEquals(1, msg.getAttachmentCount());

        // Retrieve it and verify the contents
        part = (MessagePart) msg.getAttachments().next();
        assertEquals(TEST_FILE, part.getName());
        assertEquals(TEST_FILE_TYPE, part.getContentType());
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(AttachmentTest.class);
    }

}
