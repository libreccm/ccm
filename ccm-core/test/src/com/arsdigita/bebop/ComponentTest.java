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

import com.arsdigita.util.HttpServletDummyRequest;
import com.arsdigita.util.HttpServletDummyResponse;
import com.arsdigita.xml.Element;
import java.util.Iterator;
import junit.framework.TestCase;

/**
 * Regression tests for the Component interface.
 *
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */

public class ComponentTest extends TestCase {

    public static final String versionId = "$Id: ComponentTest.java 742 2005-09-02 10:29:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    ComponentImpl m_ComponentImpl;
    Page m_page;
    PageState m_ps;

    public static final boolean FAILS = true;

    public ComponentTest (String id) throws javax.servlet.ServletException {
        super(id);

        m_ComponentImpl = new ComponentImpl();
        m_page = new Page();
        m_ps = new PageState(m_page, new HttpServletDummyRequest(), new HttpServletDummyResponse());
    }

    public void testGenerateXML() {
        Element e = new Element("foo");
        m_ComponentImpl.lock();
        m_ComponentImpl.generateXML(m_ps,e);
    }

    public void testRespond() throws javax.servlet.ServletException {
        m_ComponentImpl.respond(m_ps);
    }

    public void testChildren() {
        Iterator i = m_ComponentImpl.children();
    }

}
