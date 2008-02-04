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
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.ComponentTestHarness;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;

/**
 * Regression tests for the text field widget
 *
 * @version $Revision: #12 $ $Date: 2004/08/16 $
 */

public class SelectTest extends Fixture {

    public static final String versionId = "$Id: SelectTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public SelectTest (String id) {
        super(id);
    }

    /**
     *  Test a Select
     */
    public void testSelect() {
        Select sel = new SingleSelect("select");
        m_form.add(sel);
        sel.addOption(new Option("foovalue", "foo"));
        sel.addOption(new Option("barvalue", "bar"));
        sel.addOption(new Option("bazvalue", "baz"));
        m_page.lock();
        testComponent(sel, "select");
    }

    /**
     *  Test a Select with options added in a print listener
     */
    public void testSelectPrintListener() {
        Select sel = new SingleSelect("select");
        m_form.add(sel);
        sel.addOption(new Option("foovalue", "foo"));
        try {
            sel.addPrintListener(new PrintListener() {
                    public void prepare(PrintEvent evt) {
                        Select target = (Select)evt.getTarget();
                        target.addOption(new Option("barvalue", "bar"));
                        target.addOption(new Option("bazvalue", "baz"));
                    }
                });
        } catch (java.util.TooManyListenersException e) { }
        m_page.lock();
        testComponent(sel, "select");
    }

    /**
     *  Test a Select with options added in a form init listener
     */
    public void testSelectFormInitListener() {
        final Select sel = new SingleSelect("select");
        m_form.add(sel);
        sel.addOption(new Option("foovalue", "foo"));
        m_page.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    PageState ps = evt.getPageState();
                    sel.addOption(new Option("barvalue", "bar"), ps);
                    sel.addOption(new Option("bazvalue", "baz"), ps);
                }
            });
        // NOTE: this is an oddball method because the default testComponent
        // method builds a new page and doesn't call action listeners
        // or form listeners on it.

        final ComponentTestHarness harness = new ComponentTestHarness(m_page);
        try {
            String xml = m_page.buildDocument(harness.getServletRequest(), harness.getServletResponse()).toString();
            assertTrue(xml, xml.indexOf("foo") >= 0);
            assertTrue(xml, xml.indexOf("bar") >= 0);
            assertTrue(xml, xml.indexOf("baz") >= 0);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
