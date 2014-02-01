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

import java.net.URL;
import javax.activation.DataHandler;

/**
 * Test cases for MessageParts.
 *
 * @version $Id: MessagePartTest.java 1940 2009-05-29 07:15:05Z terry $
 */

public class MessagePartTest extends MessageTestCase {


    private static String name = "attachment";
    private static String desc = "an attached message";
    private static String text = "text body";
    private static String html = "<html><body>html body</body></html>";


    private static String imageURL =
        "http://www.arsdigita.com/graphics/images-Jan-2001/logotag.gif";
    private static String imageType = "image/gif";

    public MessagePartTest (String name) {
        super(name);
    }

    /**
     * Try setting text/plain content
     */

    public void testMessagePart001 () {
        try {
            String type = MessagePart.TEXT_PLAIN;

            MessagePart part = new MessagePart(name,desc);
            part.setText(text);

            assertEquals(name, part.getName());
            assertEquals(text, part.getContent());
            assertEquals(type, part.getContentType());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Try setting text/html content
     */

    public void testMessagePart002 () {
        try {
            String type = MessagePart.TEXT_HTML;

            MessagePart part = new MessagePart(name,desc);
            part.setContent(html, type);

            assertEquals(name, part.getName());
            assertEquals(html, part.getContent());
            assertEquals(type, part.getContentType());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Try setting the content to an image.
     * This test relies on a now invalid URL. Why it relies on an external URL
     * in the first place....
     */
    public void FAILStestMessagePart003 () {
        try {
            DataHandler dh = new DataHandler(new URL(imageURL));

            MessagePart part = new MessagePart();
            part.setDataHandler(dh);

            assertEquals(part.getName(), dh.getName());
            assertEquals(part.getContentType(), dh.getContentType());

            // Need to add further checks on the actual content being
            // handled, but I don't think this is working correctly
            // inside the persistence layer yet.

            dh = part.getDataHandler();
            assertEquals(dh.getContentType(), imageType);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Main method required to make this test runnable.
     */

    public static void main (String args[]) {
        junit.textui.TestRunner.run(AttachmentTest.class);
    }
}
