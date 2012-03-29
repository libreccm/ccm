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

import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.kernel.PackageType;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.DispatcherHelper;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * <p>An application servlet that does what SiteNodeDispatcher does
 * but in the context of the newer CCM dispatcher.  Gets the package
 * type associated with an application and uses its dispatcher.</p>
 *
 * <p>Use web.xml entries like the following to deploy this
 * servlet:</p>
 *
 * <blockquote><pre>
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;legacy-adapter&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;com.arsdigita.web.LegacyAdapterServlet&lt;/servlet-class&gt;
 * &lt;/servlet&gt;
 *
 * &lt;servlet-mapping&gt;
 *   &lt;servlet-name&gt;legacy-adapter&lt;/servlet-name&gt;
 *   &lt;url-pattern&gt;/themes/servlet/legacy-adapter/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre></blockquote>
 *
 * @see com.arsdigita.web.DispatcherServlet
 * @see com.arsdigita.sitenode.SiteNodeDispatcher
 * 
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: LegacyAdapterServlet.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class LegacyAdapterServlet extends BaseApplicationServlet {

    private static final Logger s_log = Logger.getLogger
        (LegacyAdapterServlet.class);

    /**
     * <p>Fetches the dispatcher for the package type of the current
     * application and dispatches to it with the {@link
     * RequestContext} constructed in {@link
     * com.arsdigita.web.BaseApplicationServlet}.</p>
     *
     * @param app The application which is being served.
     */
    public void doService(HttpServletRequest sreq,
                          HttpServletResponse sresp,
                          Application app)
            throws ServletException, IOException {
        s_log.debug("LegacyAdapterServlet.doService called for request " +
                    sreq);

        PackageType type = app.getApplicationType().getPackageType();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Using package type '" + type.getKey() + "'");
        }

        Assert.exists(type, "PackageType type");

        String jsp = "/packages/" + type.getKey() + "/www" + sreq.getPathInfo();
        File file = new File(getServletContext().getRealPath(jsp));

        if (file.exists() && !file.isDirectory()) {
            // XXX Need to handle welcome files.

            RequestDispatcher rd = sreq.getRequestDispatcher(jsp);

            Assert.exists(rd, "RequestDispatcher rd");

            rd.forward(sreq, sresp);
        } else {
            try {
                RequestContext rc = DispatcherHelper.getRequestContext();

                Assert.exists(rc, "RequestContext rc");

                Dispatcher dispatcher = type.getDispatcher();

                Assert.exists(dispatcher, "Dispatcher dispatcher");

                if (s_log.isDebugEnabled()) {
                    s_log.debug
                        ("Dispatching using dispatcher '" + dispatcher + "'");
                }

                dispatcher.dispatch(sreq, sresp, rc);
            } catch (ClassNotFoundException cnfe) {
                throw new UncheckedWrapperException(cnfe);
            } catch (InstantiationException ie) {
                throw new UncheckedWrapperException(ie);
            } catch (IllegalAccessException iae) {
                throw new UncheckedWrapperException(iae);
            } catch (InvocationTargetException ite) {
                throw new UncheckedWrapperException(ite);
            }
        }
    }
}
