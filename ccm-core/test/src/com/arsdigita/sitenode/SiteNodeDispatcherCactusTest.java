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
package com.arsdigita.sitenode;

import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.sitenode.SiteNodeDispatcher;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.cactus.ServletTestCase;
import org.apache.cactus.ServletURL;
import org.apache.cactus.WebRequest;
import org.apache.log4j.Logger;

public class SiteNodeDispatcherCactusTest extends ServletTestCase {

    HttpServlet s;
    RequestContext rctx;

    public SiteNodeDispatcherCactusTest(String theName) {
        super(theName);
    }

    public void setUp() {

        ClassLoader cl = ClassLoader.getSystemClassLoader();

    }

    public void testSomething()
        throws javax.servlet.ServletException, java.io.IOException {

        //  s = new Tomcat32DefaultServlet();
        //  s.init(config);
        //  s.service(request, response);

        Session sm = SessionManager.getSession();
    }

    public void beginRequestContext(WebRequest req) {
        ServletURL u = req.getURL();

        req.setURL("localhost","/enterprise", "/foo", "/bar.jsp", null);
    }

    public void testRequestContext()
        throws javax.servlet.ServletException, java.io.IOException {
        final Logger s_cat =
            Logger.getLogger(SiteNodeDispatcherCactusTest.class.getName());

        s_cat.info("SiteNodeDispatcherCactusTest.testRequestContext()");

        s = new SiteNodeDispatcher() {
                public void dispatch(HttpServletRequest req,
                                     HttpServletResponse resp,
                                     RequestContext actx)
                    throws IOException, ServletException {
                    try {
                        s_cat.error("sitenodedispatchercactustest: in overrided dispatch method");
                        //          super.dispatch(req, resp, actx);
                        rctx = actx;
                    } catch (Throwable t){
                        s_cat.error("SiteNodeDispatcher error",t);
                    }
                    return;
                }
            };

        s.init(config);
        s.service(request, response);
        assertTrue(rctx != null);
        assertTrue(rctx.getOutputType() != null);
        assertEquals(rctx.getRemainingURLPart(),"/foo/bar.jsp");
        assertEquals(rctx.getProcessedURLPart(),"/enterprise");
        assertEquals(rctx.getOriginalURL(),"/enterprise/foo/bar.jsp");
        s_cat.error("All you need is \n");
        s_cat.error("SiteNodeDispatcherCactusTestLove\n");
        s_cat.error(rctx.getOriginalURL() + ", " + rctx.getRemainingURLPart()
                    + ", " + rctx.getProcessedURLPart() + "\n");
        s_cat.error(request.getRequestURI() + "\n");
    }

}
