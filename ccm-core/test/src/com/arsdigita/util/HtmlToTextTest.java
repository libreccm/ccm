/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util;

import java.io.BufferedReader;
import java.io.FileReader;
import junit.framework.TestCase;

/**
 * Verify the HTML-to-text conversion class.  This unit test uses a
 * set of target files that contain HTML fragments and corresponding
 * text files with the expected conversion.
 *
 */

public class HtmlToTextTest extends TestCase {

    /**
     * All tests use a single static converter
     */

    static HtmlToText s_htmlToText =
        new HtmlToText();

    /**
     * List of predefined HTML files
     */

    static String s_testFiles[] = {
        "blockquote.html",
        "headings.html",
        "image.html",
        "strong.html",
        "em.html",
        "hr.html",
        "lists.html",
        "tags.html",
        "entities.html",
        "href.html",
        "pre.html",
        "wrap.html"
    };

    static String s_testDirectory =
        System.getProperty("test.base.dir") +
        "/com/arsdigita/util/files/";


    public HtmlToTextTest(String name) {
        super(name);
    }

    /**
     * A simple plain text case to verify that the conversion simple
     * copies the input if there are no HTML tags embedded.
     */

    public void testPlainTextConversion() {

        String in = "see the quick brown fox jump over the lazy dog.\n";
        String expected_out = in;
        String actual_out = s_htmlToText.convert(in);
        assertEquals("HTML conversion failed", expected_out, actual_out);

    }

    /**
     * Loop over target files and verify the expected output.
     */

    public void testHtmlConversion() {

        String in;
        String expected_out;
        String actual_out;

        try {
            for (int i = 0; i < s_testFiles.length; i++) {
                in = read(getHtmlPath(s_testFiles[i]));
                expected_out = read(getTextPath(s_testFiles[i])).trim();
                actual_out = s_htmlToText.convert(in).trim();

                assertEquals("HTML conversion failed",
                             expected_out,
                             actual_out);
            }
        } catch(java.io.IOException ex) {
            fail("Error processing test file: " + ex.getMessage());
        }
    }

    /**
     * Return the path for an HTML test file
     */

    private static String getHtmlPath(String testName) {
        return s_testDirectory + testName;
    }

    /**
     * Return the path for a Text test file
     */

    private static String getTextPath(String testName) {
        return  s_testDirectory +
            testName.substring(0,testName.length()-4) + "txt";
    }

    /**
     * Read a test file into a String
     */

    private static String read(String fileName)
        throws java.io.IOException {

        StringBuffer content = new StringBuffer();

        BufferedReader in = new BufferedReader
            (new FileReader(fileName));
        for (String line = in.readLine();
             line != null; line = in.readLine()) {
            content.append(line).append('\n');
        }

        return content.toString();
    }
}
