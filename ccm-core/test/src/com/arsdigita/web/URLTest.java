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
package com.arsdigita.web;

import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.util.HttpServletDummyRequest;
import com.arsdigita.util.HttpServletDummyResponse;
import com.arsdigita.util.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: jorris
 * Date: Nov 26, 2003
 * Time: 8:47:20 AM
 * To change this template use Options | File Templates.
 */
public class URLTest extends WebTestCase {

    public URLTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        HttpServletDummyRequest request = m_container.getRequest();
        HttpServletDummyResponse response = m_container.getResponse();
        Web.init(request, m_config.getServletContext(),
                 new UserContext(request, response));
    }

    public void testConstruction() {
        // verifies fix for bug 110058
        HttpServletDummyRequest request = m_container.getRequest();
        request.setAttribute("com.arsdigita.dispatcher.DispatcherPrefix", "/plaintext");
        URL test = new URL("http",
                "localhost",
                9000,
                "/apps",
                "/ccm",
                "/portal/homepage.jsp",
                null );

        final String linkUrl = "/apps/plaintext/ccm/portal/homepage.jsp";
        assertEquals(linkUrl, test.toString());
        final String fullUrl = "http://localhost:9000" + linkUrl;
        assertEquals(fullUrl, test.getURL());
    }

    public void testGetMethods() {
        URL test = new URL("http",
                "localhost",
                9000,
                "/apps",
                "/ccm",
                "/portal/homepage.jsp",
                null );
        // No dispatcher prefix, will fail (bug 111040)
        assertTrue(StringUtils.emptyString(test.getDispatcherPrefix()));
    }
}
