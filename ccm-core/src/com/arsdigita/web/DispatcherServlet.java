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

import com.arsdigita.dispatcher.RequestEvent;
import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.util.Assert;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * <p>The CCM main dispatcher.  This servlet serves as the main
 * servlet (mapped to "/someprefix/*") for requests to the CCM
 * webapp.</p>
 *
 * <p>Upon finding an {@link com.arsdigita.web.Application
 * application} at the requested URL, this class sets a request
 * attribute storing the ID of the application and forwards to the
 * servlet associated with it.  If instead no application is found,
 * the request is forwarded to the fallback servlet, if defined.</p>
 *
 * <p>This servlet may be deployed using web.xml entries like
 * these:</p>
 *
 * <blockquote><pre>
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;ccm-dispatcher&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;com.arsdigita.web.DispatcherServlet&lt;/servlet-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;fallback-servlet&lt;/param-name&gt;
 *     &lt;param-value&gt;the-old-site-node-dispatcher&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 * &lt;/servlet&gt;
 *
 * &lt;servlet-mapping&gt;
 *   &lt;servlet-name&gt;ccm-dispatcher&lt;/servlet-name&gt;
 *   &lt;url-pattern&gt;/ccm/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre></blockquote>
 *
 * <p>It's important to also edit the com.arsdigita.web.Initializer
 * options to reflect where you've put your dispatcher.</p>
 *
 * <blockquote><pre>
 * init com.arsdigita.web.Initializer {
 *     ...
 *     // This corresponds to a servlet mapping of "/ccm/*" and
 *     // assumes CCM is the default webapp.
 *     dispatcherContextPath = "";
 *     dispatcherServletPath = "/ccm";
 *     ...
 * }
 * </pre></blockquote>
 *
 * @see com.arsdigita.web.BaseApplicationServlet
 * @author Justin Ross &lt;<a href="mailto:jross@redhat.com">jross@redhat.com</a>&gt;
 * @version $Id: DispatcherServlet.java 738 2005-09-01 12:36:52Z sskracic $
 */
public class DispatcherServlet extends BaseServlet {
    public static final String versionId =
        "$Id: DispatcherServlet.java 738 2005-09-01 12:36:52Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static Logger s_log = Logger.getLogger(DispatcherServlet.class);

    public static final String FALLBACK_SERVLET_PARAMETER = "fallback-servlet";

    public static final String FALLING_BACK_ATTRIBUTE =
        DispatcherServlet.class.getName() + ".falling_back";

    private String m_fallbackName = null;
    private final BaseDispatcher m_dispatcher;

    public DispatcherServlet() {
        m_dispatcher = new BaseDispatcher();
    }

    public void doInit() throws ServletException {
        m_fallbackName =
            getServletConfig().getInitParameter(FALLBACK_SERVLET_PARAMETER);

        Assert.exists(m_fallbackName, String.class);

        Assert.exists(getServletConfig().getServletContext().getNamedDispatcher
                      (m_fallbackName), RequestDispatcher.class);
    }

    protected void doService(final HttpServletRequest sreq,
                             final HttpServletResponse sresp)
            throws ServletException, IOException {
        DeveloperSupport.requestStart
            (new RequestEvent(sreq, sresp, null, true, false));

        if (s_log.isDebugEnabled()) {
            s_log.debug("Servicing request '" + sreq.getRequestURI() + "'");
        }

        DeveloperSupport.startStage("BaseDispatcher.dispatch");

        boolean dispatched = m_dispatcher.dispatch(sreq, sresp);

        DeveloperSupport.endStage("BaseDispatcher.dispatch");

        if (dispatched) {
            s_log.debug("Successfully dispatched to an application");
        } else {
            s_log.debug("Could not dispatch this request to an " +
                        "application;  using the fallback servlet");

            sreq.setAttribute(FALLING_BACK_ATTRIBUTE, Boolean.TRUE);

            DeveloperSupport.startStage("BaseDispatcher.forward");

            RequestDispatcher fallbackDispatcher =
                getServletConfig().getServletContext().getNamedDispatcher
                (m_fallbackName);

            m_dispatcher.forward(fallbackDispatcher, sreq, sresp);

            DeveloperSupport.endStage("BaseDispatcher.forward");
        }
    }

    protected void doDestroy() {
        m_fallbackName = null;
    }
}
