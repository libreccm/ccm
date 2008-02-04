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
package com.arsdigita.dispatcher;

import com.arsdigita.dispatcher.BaseDispatcherServlet;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.dispatcher.RedirectException;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.dispatcher.RequestEvent;
import com.arsdigita.dispatcher.RequestListener;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.cactus.ServletTestCase;
import org.apache.cactus.ServletURL;
import org.apache.cactus.WebRequest;
import org.apache.log4j.Logger;

public class BaseDispatcherServletCactusTest extends ServletTestCase {

    public static final String versionId = "$Id: BaseDispatcherServletCactusTest.java 748 2005-09-02 11:57:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    BaseDispatcherServlet s;
    RequestContext rctx;

    boolean startFired;
    boolean finishFired;

    MultipartHttpServletRequest mp_request;

    final Logger s_cat =
        Logger.getLogger(BaseDispatcherServletCactusTest.class.getName());

    public BaseDispatcherServletCactusTest(String theName) {
        super(theName);
    }

    public void setUp() {
        s = new BaseDispatcherServlet() {
                protected RequestContext authenticateUser
                    (HttpServletRequest req,
                     HttpServletResponse resp,
                     RequestContext ctx)
                    throws RedirectException {
                    return ctx;
                }

                public void dispatch(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     RequestContext actx)
                    throws javax.servlet.ServletException, java.io.IOException {
                    try {
                        rctx = actx;

                        if (req instanceof MultipartHttpServletRequest) {
                            mp_request = (MultipartHttpServletRequest) req;
                        }

                        PrintWriter out = resp.getWriter();
                        out.println("<html><head><title>hello</title>" +
                                    "</head><body>hello!</body></html>");

                    } catch (Throwable t){
                        s_cat.error("BaseDispatcherServlet error",t);
                    }
                    return;
                }
            };
    }

    public void beginRequestContext(WebRequest req) {
        ServletURL u = req.getURL();

        req.setURL("localhost","/enterprise",
                   "/foo", "/bar.jsp", null);
    }


    /** Tests creation of request context & validates some
        URL bookkeeping functions.
    */

    public void testRequestContext()
        throws javax.servlet.ServletException, java.io.IOException {

        s_cat.info("BaseDispatcherServletCactusTest.testRequestContext()");

        s.init(config);
        s.service(request, response);
        assertTrue(rctx != null);
        assertTrue(rctx.getOutputType() != null);
        assertEquals(rctx.getRemainingURLPart(),"/foo/bar.jsp");
        assertEquals(rctx.getProcessedURLPart(),"/enterprise");
        assertEquals(rctx.getOriginalURL(),"/enterprise/foo/bar.jsp");

        s_cat.info("All you need is ");
        s_cat.info("BaseDispatcherServletCactusTest Love\n");
        s_cat.info(rctx.getOriginalURL() + ", " + rctx.getRemainingURLPart()
                   + ", " + rctx.getProcessedURLPart() + "\n");
        s_cat.info(request.getRequestURI() + "\n");
    }

    /** Tests registering of an RequestListener and basic event firing
     */

    public void beginRequestListener(WebRequest req) {
        req.setURL("localhost","/enterprise",
                   "/foo", "/bar.jsp", null);
    }

    public void testRequestListener()
        throws javax.servlet.ServletException, java.io.IOException {

        startFired = false;
        finishFired = false;

        RequestListener rl = new RequestListener() {
                public void requestStarted(RequestEvent e) {
                    startFired = true;
                }

                public void requestFinished(RequestEvent e) {
                    finishFired = true;
                }
            };

        s.addRequestListener(rl);
        s.init(config);
        s.service(request,response);

        assertTrue(startFired);
        assertTrue(finishFired);
    }



    public void beginMultipartRequestFails(WebRequest req) {
        req.setURL("localhost","/enterprise",
                   "/somePackage", "/someAdditionalPath", null);
        req.addHeader("Content-type","multipart/form-data");
        req.addParameter("foo","bar",WebRequest.POST_METHOD);
        req.addParameter("baz","qux",WebRequest.POST_METHOD);
    }

    /** Tests creation of a MultipartRequest.

    This'll fail until we get cactus support for
    multipart/form-data requests.

    */

    public void dontTestMultipartRequest()
        throws javax.servlet.ServletException, java.io.IOException {
        s.init(config);
        s.service(request, response);
        assertNotNull(mp_request);
    }

}
