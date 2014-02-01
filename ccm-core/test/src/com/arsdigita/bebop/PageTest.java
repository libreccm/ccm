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
package com.arsdigita.bebop;

import com.arsdigita.bebop.parameters.StringParameter;
import junit.framework.TestCase;

/**
 * Regression tests for Page.
 *
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 */

public class PageTest extends TestCase {


    SimpleComponent m_comp;
    Page m_page;
    PageState m_ps;

    public PageTest (String id) throws javax.servlet.ServletException {
        super(id);
    }

    public void setUp() {
        m_page = new Page();
        m_comp = new SimpleComponent();
        // m_ps = new PageState(m_page, new HttpServletDummyRequest(), new HttpServletDummyResponse());
    }

    public void tearDown() {
        m_page = null;
        m_comp = null;
        m_ps = null;
    }

    public void testMangling() {
        String name = "name";
        StringParameter p = new StringParameter(name);
        m_page.addComponent(m_comp);
        m_page.addComponentStateParam(m_comp, p);
        assertTrue(! name.equals(p.getName()));
        m_page.addGlobalStateParam(p);
        assertEquals(name, p.getName());
    }

}
