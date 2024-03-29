/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;


// This class uses a non-standard way to construct a kind of repository of
// webapp contexts various applications of CCM used to be installed up to
// version 1.0.4
// With version 2.0 all modules of CCM are installed into one directory 
// (context) and this servlet is nolonger used (and will be removed soon).


/**
 * <p>
 Every application running in its own webapp should
 declare an instance of this servlet in their web.xml,
 marking it to load on startup. This is a work around
 for bz 114688 - Tomcat ServletContext#getWebContext(String)
 always returns the ROOT context in releases < 4.1.20
 * Map into your web.xml as follows:
 * </p>
 * <pre>
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;reg&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;com.arsdigita.web.ContextRegistrationServlet&lt;/servlet-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;uri&lt;/param-name&gt;
 *     &lt;param-value&gt;/ccm-ldn-theme/&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 *   &lt;load-on-startup&gt;1&lt;/load-on-startup&gt;
 * &lt;/servlet&gt;
 *
 * &lt;servlet-mapping&gt;
 *   &lt;servlet-name&gt;reg&lt;/servlet-name&gt;
 *   &lt;url-pattern&gt;/templates/null/reg/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 *</pre>
 *<p>
 * The value of the init-param should be the same as the 
 * @name attribute on ccm:application tag in application.xml
 *</p>
 */
public class ContextRegistrationServlet extends HttpServlet {
    
    /**
     * The uri on an application, manually configured as servlet parameter in
     * web.xml
     */
    private String m_uri;

    @Override
    public void init(final ServletConfig sconfig) throws ServletException {
        m_uri = sconfig.getInitParameter("uri");
        Assert.exists(m_uri, String.class);
        Assert.isTrue(m_uri.startsWith("/"), "uri starts with /");
        Assert.isTrue(m_uri.endsWith("/"), "uri ends with /");

        /*
         * Registers this web application providing
         * (a) m_uri = this web application context as configured in web.xml
         * (b) sconfig.getServletContext = this servlet's context object, 
         *     bypassing a bug in early versions of tomcat (see above). Because
         *     getServletContext is retrieved by the ContextRegistrationServlet
         *     loaded first it is not affected by the bug.
         */
    //  Web.registerServletContext(m_uri,
    //                             sconfig.getServletContext());
    }
    
    @Override
    public void destroy() {
    //  Web.unregisterServletContext(m_uri);
    }
}
