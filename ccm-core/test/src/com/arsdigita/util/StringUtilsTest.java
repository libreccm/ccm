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
package com.arsdigita.util;

import java.util.HashMap;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

public class StringUtilsTest extends TestCase {

    private static final Logger s_log = Logger.getLogger(StringUtilsTest.class);

    public StringUtilsTest(String name) {
        super(name);
    }

    public void testSmartText() {
        String src = "foo *bar* wibble /eek/\n" +
            "and mailto:dan@berrange.com eek!\n" +
            "\n" +
            "the second =paragraph= contains\n" +
            "a link to http://www.google.com\n" +
            "and the fractions 1/2 3/4 1/4 and\n" +
            "the symbols for copyright (C),\n" +
            "trademark (TM) and rights (R)\n" + 
            "\n" +
            "* a bullet list\n" +
            "* more *bullets* in\n" +
            " this list element\n" +
            "* a final element\n" +
            "\n" +
            "-------\n" +
            "\n" +
            "+ now an enumerated list item\n" +
            "+ and one /more/\n" +
            "+ this one is split over two lines\n" +
            "for testing purposes\n"+
            "\n" +
            "___\n" +
            "\n" +
            "and now the end is near, lets test\n" +
            "@google(http://www.google.com) a few\n" +
            "titled links, including a mailto\n" +
            "@Dan B(mailto:dan@@berrange.com)";
        String expected = "<div>\n" + 
            "foo <strong>bar</strong> wibble <em>eek</em>\n" + 
            "and <a href=\"mailto:dan@berrange.com\">mailto:dan@berrange.com</a> eek!\n" + 
            "</div>\n" + 
            "\n" +
            "<div>\n" + 
            "the second <code>paragraph</code> contains\n" + 
            "a link to <a href=\"http://www.google.com\">http://www.google.com</a>\n" + 
            "and the fractions &frac12; &frac34; &frac14; and\n" + 
            "the symbols for copyright &copy;,\n" + 
            "trademark <sup>TM</sup> and rights &reg;\n" + 
            "</div>\n" + 
            "\n" +
            "<ul>\n" + 
            "<li>\n" + 
            "a bullet list\n" + 
            "</li>\n" + 
            "<li>\n" + 
            "more <strong>bullets</strong> in\n" + 
            " this list element\n" + 
            "</li>\n" + 
            "<li>\n" + 
            "a final element</li>\n" + 
            "</ul>\n" + 
            "\n" +
            "<hr/>\n" + 
            "\n" +
            "<ol>\n" + 
            "<li>\n" + 
            "now an enumerated list item\n" + 
            "</li>\n" + 
            "<li>\n" + 
            "and one <em>more</em>\n" + 
            "</li>\n" + 
            "<li>\n" + 
            "this one is split over two lines\n" + 
            "for testing purposes</li>\n" + 
            "</ol>\n" + 
            "\n" +
            "<hr/>\n" + 
            "\n" +
            "<div>\n" + 
            "and now the end is near, lets test\n" + 
            "<a href=\"http://www.google.com\">google</a> a few\n" + 
            "titled links, including a mailto\n" + 
            "<a href=\"mailto:dan@berrange.com\">Dan B</a>\n" + 
            "</div>\n";
        String actual = StringUtils.smartTextToHtml(src);
        
        s_log.debug("Input: {" + src + "}\n");
        s_log.debug("Expected: {" + expected + "}\n");
        s_log.debug("Actual: {" + actual + "}\n");
        
        assertTrue(expected.equals(actual));
    }

    public void testEmptyString() {

        assertTrue( StringUtils.emptyString(null) );
        assertTrue( StringUtils.emptyString("") );
        assertTrue( StringUtils.emptyString((Object)"") );

        assertTrue( ! StringUtils.emptyString("foo") );
        assertTrue( ! StringUtils.emptyString((Object)"foo") );
        assertTrue( ! StringUtils.emptyString((Object)(new Integer(1))) );

    }

    public void testQuoteHtml() {

        assertEquals( "", StringUtils.quoteHtml(null) );
        assertEquals( "", StringUtils.quoteHtml("") );
        assertEquals( "foo", StringUtils.quoteHtml("foo") );
        assertEquals( "foo&amp;", StringUtils.quoteHtml("foo&") );
        assertEquals( "&amp;foo", StringUtils.quoteHtml("&foo") );
        assertEquals( "&amp;foo&amp;", StringUtils.quoteHtml("&foo&") );
        assertEquals( "&amp;&quot;&lt;&gt;&quot;&amp;",
                      StringUtils.quoteHtml("&\"<>\"&") );

    }

    public void testGetParameter() throws Exception {

        String plist = "boyspet=play,pet=dog,play=yes,age=34,eopt=,opt=23";
        verifyGet(plist,"boyspet","play");
        verifyGet(plist,"pet","dog");
        verifyGet(plist,"play","yes");
        verifyGet(plist,"age","34");
        verifyGet(plist,"eopt","");
        verifyGet(plist,"opt","23");
        verifyGet(plist,"spet",null);
        verifyGet(plist,"notin",null);
    }

    // helper method for above test.
    private static void verifyGet(String plist, String key, String expected) {
        String found = StringUtils.getParameter(key, plist, ',');
        assertEquals("Expected parameter not found, key=" +key+
                     " expected=" + expected + " found=" +found,
                     expected, found);
    }




    public void testSplit() throws Exception {

        String plist = "cat,hat,,bat,rat";
        String [] ar = StringUtils.split(plist, ',');
        verifySplit("cat", ar[0]);
        verifySplit("hat", ar[1]);
        verifySplit("", ar[2]);
        verifySplit("bat",ar[3]);
        verifySplit("rat",ar[4]);
        assertEquals("expected array length 5, found="
                     + ar.length,ar.length,5);
        plist = ",,dog,fish,,,";
        ar = StringUtils.split(plist, ',');
        verifySplit("",ar[0]);
        verifySplit("",ar[1]);
        verifySplit("dog", ar[2]);
        verifySplit("fish", ar[3]);
        verifySplit("", ar[4]);
        verifySplit("", ar[5]);
        verifySplit("", ar[6]);
        assertEquals("expected array length 7, found="
                     + ar.length,ar.length,7);

    }

    // helper method for above test.
    private void verifySplit(String expected, String found) {
        String errMsg = "Split, expected = " + expected +
            " found = " + found;
        assertEquals(errMsg, expected, found);
    }



    public void testStripWhiteSpace() throws Exception {

        String in = " <   H>   e \t\n ll/>   o  . \n   ";
        String expected_out = "< H> e ll/> o .";
        String actual_out = StringUtils.stripWhiteSpace(in);
        assertEquals("stripWhiteSpace failed.  Expected = '" +
                     expected_out + "', Found = '" + actual_out + "'",
                     expected_out, actual_out);

    }

    public void testAddNewline() throws Exception {
        String in = "*";
        String nl = System.getProperty("line.separator");

        String expected_out = in + nl;
        String actual_out = StringUtils.addNewline(in);
        assertEquals("failed to add newline", expected_out, actual_out);

        in = "* ";
        expected_out = in;
        actual_out = StringUtils.addNewline(in);
        assertEquals("added unecessary newline", expected_out, actual_out);

        in = "*" + nl;
        expected_out = in;
        actual_out = StringUtils.addNewline(in);
        assertEquals("added unecessary newline", expected_out, actual_out);
    }

    public void testHtmlToText() {

        String in = "<p>this is the text<br>newline .</p>one<br><b>two</b><br>";
        String expected_out = "\n\nthis is the text\nnewline .one\ntwo\n";
        String actual_out = StringUtils.htmlToText(in);
        assertEquals("htmlToText invalid", expected_out, actual_out);

        in = "Text with <a <b <c > strange markup";
        expected_out = "Text with  strange markup";
        actual_out = StringUtils.htmlToText(in);
        assertEquals(expected_out, actual_out);

    }

    public void testTrimleft() {
        String in = "a";
        String expected_out = "a";
        String actual_out = StringUtils.trimleft(in);
        assertEquals("trimleft invalid", expected_out, actual_out);

        in = " a";
        expected_out = "a";
        actual_out = StringUtils.trimleft(in);
        assertEquals("trimleft invalid", expected_out, actual_out);

        in = " ";
        expected_out = "";
        actual_out = StringUtils.trimleft(in);
        assertEquals("trimleft invalid", expected_out, actual_out);
    }

    public void testRepeat() {
        String in = "a";
        String expected_out = "aaaaa";
        String actual_out = StringUtils.repeat(in,5);
        assertEquals("repeat invalid", expected_out, actual_out);

        actual_out = StringUtils.repeat('a',5);
        assertEquals("repeat invalid", expected_out, actual_out);
    }

    public void testWrap() {

        // Identity test
        String in = "a\n";
        String expected_out = in;
        String actual_out = StringUtils.wrap(in);
        assertEquals("wrap failed identify test",
                     expected_out,
                     actual_out);

        // Identify test with multiple words
        in = "a b c d e\n";
        expected_out = in;
        actual_out = StringUtils.wrap(in);
        assertEquals("wrap failed identify test",
                     expected_out,
                     actual_out);

        // Simple test with short lines
        in = StringUtils.repeat("1234 ",5);
        expected_out = StringUtils.repeat("1234\n",5);
        actual_out = StringUtils.wrap(in,1);
        assertEquals("wrap invalid",
                     expected_out,
                     actual_out);

        // Verify preservation of line breaks
        in = StringUtils.repeat("1234\n",5);
        expected_out = in;
        actual_out = StringUtils.wrap(in,100);
        assertEquals("line break preservation failed",
                     expected_out,
                     actual_out);

        // Verify a "standard" wrapping case
        in = StringUtils.repeat("1234 ",10);
        expected_out =
            StringUtils.repeat("1234 ",5).trim() + "\n" +
            StringUtils.repeat("1234 ",5).trim() + "\n";

        actual_out = StringUtils.wrap(in,25);
        assertEquals("line wrapping failed",
                     expected_out,
                     actual_out);
    }

    public void testPlaceholders() {
        String in = "foo ::bar:: wizz";
        String expected_out = "foo eek wizz";
        String actual_out = StringUtils.interpolate(in, "bar", "eek");

        assertEquals("interpolate failed simple placeholder",
                     expected_out,
                     actual_out);

        HashMap vars = new HashMap();
        vars.put("bar", "eek");
        vars.put("more", "wibble");

        in = "foo ::bar:: wizz ::more:: done";
        expected_out = "foo eek wizz wibble done";
        actual_out = StringUtils.interpolate(in, vars);
        assertEquals("interpolate failed hashmap test",
                     expected_out,
                     actual_out);

    }

    public void testReplace() {
        String[] pairs = {null, null,
                          "foobar", "foobar",
                          ";foobar", "\\;foobar",
                          ";foo;bar;baz", "\\;foo\\;bar\\;baz",
                          ";;foobar", "\\;\\;foobar",
                          "f;o;obar", "f\\;o\\;obar",
                          "f;o;;bar", "f\\;o\\;\\;bar",
                          "foobar;", "foobar\\;",
                          "foobar;;", "foobar\\;\\;"};

        for (int ii=0, jj=1; jj<pairs.length; ii+=2, jj+=2) {
            System.err.println("ii=" + ii + ", pairs[ii]=" + pairs[ii] +
                               ", jj=" + jj + ", pairs[jj]=" + pairs[jj]);
            String expected = pairs[jj];
            String actual = StringUtils.replace(pairs[ii], ";", "\\;");
            assertEquals(expected, actual);

            expected = pairs[ii];
            actual = StringUtils.replace(pairs[jj], "\\;", ";");
            assertEquals(expected, actual);
        }
    }
}
