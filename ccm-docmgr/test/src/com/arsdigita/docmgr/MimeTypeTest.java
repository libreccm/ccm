/*
* Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
*
* The contents of this file are subject to the CCM Public
* License (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of
* the License at http://www.redhat.com/licenses/ccmpl.html
*
* Software distributed under the License is distributed on an "AS
* IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
* implied. See the License for the specific language governing
* rights and limitations under the License.
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
 * @version $Id: //apps/docmgr/dev/test/src/com/arsdigita/docmgr/MimeTypeTest.java#4 $
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
