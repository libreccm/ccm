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
package com.arsdigita.bebop.jsp;

import com.arsdigita.test.HttpUnitTestCase;
import com.arsdigita.util.StringUtils;
import com.meterware.httpunit.WebResponse;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Affirmative-validation test to sanity-check /bebop-jsp.  Note that
 * any slight change to the output could cause any one of these tests
 * to fail.
 *
 *
 * @author Bill Schneider
 * @version 1.0
 */

public class BebopJSPTest extends HttpUnitTestCase {
    public static final String versionId = "$Id: BebopJSPTest.java 748 2005-09-02 11:57:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Perl5Util m_re;

    public BebopJSPTest(String name) {
        super(name);
    }

    public void setUp() {
        m_re = new Perl5Util();
    }

    public void tearDown() { }

    private final static String s_testPagePattern =
        ".*<form[^>]*name=\"myForm\"[^>]*>.*"
        + "<input\\s+name=\"foo\"\\s+type=\"text\"[^>]*>"
        + ".*<input\\s+name=\"bar\"[^>]*type=\"submit\"[^>]*>.*";

    public void testBasic() {
        try {
            WebResponse resp = getResponse("/bebop-jsp/test.jsp");
            String text = StringUtils.stripNewLines(resp.getText());
            assertTrue("basic test page",
                       m_re.match("m$" + s_testPagePattern + "$", text));
            resp = getResponse("/bebop-jsp/all-in-one.jsp");
            text = StringUtils.stripNewLines(resp.getText());
            assertTrue("basic test page defined/shown in same JSP",
                       m_re.match("m$" + s_testPagePattern + "$", text));
        } catch (Exception e) {
            System.out.println(e);
            fail(e.toString());
        }
    }

    private final static String s_testFromClassPattern =
        ".*<form[^>]*name=\"exampleForm\"[^>]*>.*"
        + "<input name=\"textField\" type=\"text\"[^>]*>"
        + ".*<input name=\"submitWidget\"[^>]*type=\"submit\" [^>]*>.*";

    public void testFromClass() {
        try {
            WebResponse resp = getResponse("/bebop-jsp/test-class.jsp");
            String text = StringUtils.stripNewLines(resp.getText());
            assertTrue("basic test from class",
                       m_re.match("m$" + s_testFromClassPattern + "$", text));
        } catch (Exception e) {
            System.out.println(e);
            fail(e.toString());
        }
    }

    private final static String s_testIncludePattern =
        "include another page.*<form[^>]*name=\"exampleForm\"[^>]*>.*"
        + "<input name=\"textField\" type=\"text\"[^>]*>"
        + ".*<input name=\"submitWidget\"[^>]*type=\"submit\" [^>]*>.*"
        + "include done";

    public void testInclude() {
        try {
            WebResponse resp = getResponse("/bebop-jsp/test-include.jsp");
            String text = StringUtils.stripNewLines(resp.getText());
            assertTrue("test static include",
                       m_re.match("m$" + s_testIncludePattern + "$", text));
        } catch (Exception e) {
            System.out.println(e);
            fail(e.toString());
        }
    }

    private final static String s_testListPattern =
        "(<li>list item #\\d+: <a[^>]*type=\"control\"[^>]*>.*?)+";

    public void testList() {
        try {
            WebResponse resp = getResponse("/bebop-jsp/list.jsp");
            String text = StringUtils.stripNewLines(resp.getText());
            assertTrue("test model-backed list",
                       m_re.match("m$" + s_testListPattern + "$", text));
        } catch (Exception e) {
            System.out.println(e);
            fail(e.toString());
        }
    }

    private final static String s_tablePattern =
        "<table\\s+cellspacing=\"4\">\\s*<tr\\s+bgcolor=\"#99ccff\">"
        + "\\s*<th>[^<]+</th>\\s*<th>[^<]+</th>\\s*<th>[^<]+</th>\\s*</tr>"
        + "([^<]*<tr bgcolor=\"#99ffcc\">[^<]*<td>[^<]*</td>[^<]*<td>[^<]*</td>"
        + "[^<]*<td>[^<]*</td>[^<]*</tr>)+";

    public void testTable() {
        try {
            WebResponse resp = getResponse("/bebop-jsp/table.jsp");
            String text = StringUtils.stripNewLines(resp.getText());
            assertTrue("test model-backed table",
                       m_re.match("m$" + s_tablePattern + "$", text));
        } catch (Exception e) {
            System.out.println(e);
            fail(e.toString());
        }
    }

    private final static String s_selectPattern =
        "<select name=\"select\">(<option[^>]+>[^<]+</option>)+</select>";

    private final static String s_multiSelectPattern =
        "<select multiple name=\"multi\">(<option [^>]*>[^<]*</option>)+"
        + "</select>";

    private final static String s_radioPattern =
        "(<input type=\"radio\" name=\"radio\"[^>]+>"
        + "[^<]+</input>.*)+";

    private final static String s_checkboxPattern =
        "(<input type=\"checkbox\" name=\"cb\"[^>]+>"
        + "<label [^>]+>[^<]+</label>.*)+";

    public void testOptions() {
        try {
            WebResponse resp = getResponse("/bebop-jsp/options.jsp");
            String text = StringUtils.stripNewLines(resp.getText());
            assertTrue("single select",
                       m_re.match("m$" + s_selectPattern + "$", text));
            assertTrue("multi select",
                       m_re.match("m$" + s_multiSelectPattern + "$", text));
            assertTrue("radio group",
                       m_re.match("m$" + s_radioPattern + "$", text));
            assertTrue("checkbox group",
                       m_re.match("m$" + s_checkboxPattern + "$", text));
        } catch (Exception e) {
            System.out.println(e);
            fail(e.toString());
        }
    }


}
