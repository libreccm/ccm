/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.docmgr;

import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.tools.junit.framework.BaseTestCase;

import java.io.*;


/**
 * Test cases for Mime types.
 *
 * @author ron@arsdigita.com
 * @author stefan@arsdigita.com
 * @version $Id: //apps/docmgr/dev/test/src/com/arsdigita/docmgr/MimeTypeTest.java#6 $
 */

public class MimeTypeTest extends BaseTestCase {

    final static String MIME_TYPE = "test/docmgr";
    final static String MIME_EXTENSION = "dmgr";
    final static String MIME_LABEL= "Test Mime Type";
    final static String MIME_CLASS = "MimeType";

    public MimeTypeTest(String name) {
        super(name);
    }

    /**
     * Test loading a simple XML configuration.
     */

    public void testMimeTypeCreation() {

        // create an XML configuration and load it

        loadMimeType(getMimeXML(MIME_TYPE,
                MIME_EXTENSION,
                MIME_LABEL,
                MIME_CLASS));

        // verify the created type can be retrieved

        MimeType type = MimeType.loadMimeType(MIME_TYPE);
        assertNotNull(type);

        // verify that all attributes are correct, and that file types
        // are guessed correctly based on the extensions.

        assertEquals(MIME_EXTENSION, type.getFileExtension());
        assertEquals(MIME_LABEL, type.getLabel());
        assertEquals(MIME_TYPE,
                type.guessMimeType("dmgr").getMimeType());
        assertEquals(MIME_TYPE,
                type.guessMimeTypeFromFile("test.dmgr").getMimeType());
    }

    /**
     * Test that MIME types are updated.
     */

    public void testMimeTypeUpdates() {

        // create a MIME type that will be updated

        String javaClass = MimeType.class.getName();

        MimeType type = MimeType.createMimeType
                (MIME_TYPE, javaClass, MimeType.TYPE);
        type.setLabel(MIME_LABEL + " to be updated");
        type.setFileExtension(MIME_EXTENSION);
        type.save();

        // load the new configuration

        loadMimeType(getMimeXML(MIME_TYPE,
                MIME_EXTENSION,
                MIME_LABEL,
                MIME_CLASS));

        // verify that the type was updated

        type = MimeType.loadMimeType(MIME_TYPE);
        assertEquals(MIME_LABEL, type.getLabel());
    }

    /**
     * Creates the XML document describing the MIME type specified by
     * the arguments.
     */

    private static String getMimeXML(String type,
                                     String extensions,
                                     String label,
                                     String mimeClass) {
        return
                "<mimetypes name=\"Document Manager Mime types\">"+
                "<mimetype" +
                "  name=\"" + type + "\"" +
                "  extension=\"" + extensions + "\"" +
                "  label=\"" + label + "\"" +
                "  class=\"" + mimeClass + "\" />" +
                "</mimetypes>";
    }

    /**
     * Invokes the XML loader to read a configuration from a String.
     */

    private static void loadMimeType(String mimeXML) {
        try {
            MimeTypeXMLLoader.parse
                    (new StringBufferInputStream(mimeXML));
        } catch(java.io.IOException ioex) {
            System.out.println("DOCS: "+ioex.getMessage());
        } catch (InvalidMimeTypeFormatException imex) {
            System.out.println("DOCS: "+imex.getMessage());
        }
    }


    /**
     * Main method required to make this test runnable.
     */

    public static void main(String args[]) {
        junit.textui.TestRunner.run(MimeTypeTest.class);
    }
}
