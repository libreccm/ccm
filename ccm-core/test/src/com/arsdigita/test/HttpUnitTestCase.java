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
package com.arsdigita.test;

import com.arsdigita.web.URL;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import junit.framework.TestCase;

/**
 *
 * Provide a wrapper for common use functionalities in HttpUnit.
 *
 * @author (<a href="mailto:ddao@arsdigita.com">David Dao</a>)
 * @version $Id: HttpUnitTestCase.java 750 2005-09-02 12:38:44Z sskracic $ $DateTime: 2004/08/16 18:10:38 $
 * @since
 *
 */

public abstract class HttpUnitTestCase extends TestCase {

    protected WebConversation browser = new WebConversation();
    protected String baseUrl = null;

    public HttpUnitTestCase(String name) {
        super(name);

        // Get base url from test.server.url system properties.
        baseUrl = System.getProperty("test.server.url");

    }
    protected WebForm getForm(String url, int index) throws Exception {
        WebResponse response = getResponse(url);

        return response.getForms()[index];

    }

    protected WebForm getForm(WebRequest req, int index) throws Exception {
        WebResponse response = browser.getResponse(req);

        return response.getForms()[index];
    }

    protected WebForm getForm(WebLink link, int index) throws Exception {
        WebRequest request = link.getRequest();

        return getForm(request, index);
    }

    protected WebForm getFormWithName(String url, String name) throws Exception {
        WebResponse response = getResponse(url);

        return response.getFormWithName(name);

    }

    protected WebForm getFormWithName(WebRequest req, String name) throws Exception {
        WebResponse response = browser.getResponse(req);

        return response.getFormWithName(name);
    }

    protected WebForm getFormWithName(WebLink link, String name) throws Exception {
        WebRequest request = link.getRequest();

        return getFormWithName(request,name);
    }

    protected WebTable getTable(String url, int index) throws Exception {
        WebResponse response = getResponse(url);

        return response.getTables()[index];
    }

    protected WebResponse submit(WebRequest request) throws Exception {
        return browser.getResponse(request);
    }

    protected WebResponse getResponse(WebRequest request) throws Exception {
        return browser.getResponse(request);
    }

    protected WebResponse getResponse(String url) throws Exception {
        return browser.getResponse(baseUrl + URL.getDispatcherPath() + url);
    }

    protected WebResponse getResponse(WebLink link) throws Exception {
        return browser.getResponse(link.getRequest());
    }
}
