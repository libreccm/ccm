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
package com.arsdigita.web;

import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.DummyServletConfig;
import com.arsdigita.util.DummyServletContext;
import com.arsdigita.util.HttpServletDummyRequest;
import com.arsdigita.util.HttpServletDummyResponse;
import com.arsdigita.util.TestServletContainer;

public class WebTestCase extends BaseTestCase {

    DispatcherServlet m_dispatcher;
    TestServletContainer m_container;
    protected DummyServletConfig m_config;

    public WebTestCase(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        m_config = new DummyServletConfig("DispatcherServlet");
        m_config.setInitParameter(DispatcherServlet.FALLBACK_SERVLET_PARAMETER, "DummyServlet");
        DummyServletContext ctx = (DummyServletContext) m_config.getServletContext();
        ctx.addDispacher("DummyServlet", new DummyServlet());
        m_dispatcher = new DispatcherServlet();
        m_dispatcher.init(m_config);

        HttpServletDummyRequest req = new HttpServletDummyRequest("localhost:8080", "", "/ccm", "/", null);
        HttpServletDummyResponse res = new HttpServletDummyResponse();
        m_container = new TestServletContainer(req, res);
        m_container.addServletMapping("/ccm", m_dispatcher);

    }

}
