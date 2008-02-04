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
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.Page;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.TagTestElement;

public class TagTest extends BaseTestCase {

    public static final String versionId = "$Id: TagTest.java 748 2005-09-02 11:57:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public TagTest(String name) {
        super(name);
    }

    // http://developer.arsdigita.com/acs5/sdm/one-ticket?ticket_id=153343

    /** This is a simple white box test that verifies a BeBop
     *  component renders to the appropriate XML element.
     */

    // needs to be refactored to include a fixture and testing methods
    // for different widgets

    public void testCheckbox() throws javax.servlet.ServletException {

        Page p = new Page();
        ComponentTestHarness harness = new ComponentTestHarness(p);
        Element parent = new Element("test_parent");

        Widget w = new CheckboxGroup("check_test");
        w.generateWidget(harness.getPageState(), parent);

        java.util.List l = parent.getChildren();
        Element child = (Element) l.get(0);

        // need access to child's internals to verify
        // the right tag was generated.  hmm.
        // i have an idea -- maybe we can cheat!

        org.w3c.dom.Element e =
            TagTestElement.getInternalElementForTesting(child);
        assertEquals(e.getTagName(), w.getElementTag());
    }


}
