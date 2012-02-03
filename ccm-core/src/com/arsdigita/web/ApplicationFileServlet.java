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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Classes;

import org.apache.log4j.Logger;

/**
 * <p>
 * A simple servlet for dispatching abstract URLs to concrete JSP files.
 * </p>
 * <p>
 * This is intended to be used in all places where BebopMapServlet would 
 * have traditionally been used, since it allows projects to override the
 * JSP at a particular location & add additional JSPs at new URLs without 
 * Java code changes. 
 * </p>
 * <p>
 * The application should set its web.xml to contain
 * </p>
 * <pre>
 * &lt;web-app&gt;
 *   &lt;servlet&gt;
 *     &lt;servlet-name&gt;files&lt;/servlet-name&gt;
 *     &lt;servlet-class&gt;com.arsdigita.web.ApplicationFileServlet&lt;/servlet-class&gt;
 *     &lt;init-param&gt;
 *       &lt;param-name&gt;template-path&lt;/param-name&gt;
 *       &lt;param-value&gt;/templates/ccm-mywebapp&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 *   &lt;/servlet&gt;
 * 
 *   &lt;servlet-mapping&gt;
 *     &lt;servlet-name&gt;files&lt;/servlet-name&gt;
 *     &lt;url-pattern&gt;/files/*&lt;/url-pattern&gt;
 *   &lt;/servlet-mapping&gt;
 * &lt;/web-app&gt;
 * </pre>
 * 
 * <p>
 * In the Application class it should define:
 * </p>
 * <pre>
 *  public String getContextPath() {
 *     return "ccm-mywebapp";
 *  }
 * 
 *  public String getServletPath() {
 *    return "/files";
 *  }
 * </pre>
 * <p>
 * It can then put JSP files in a 'templates/ccm-mywebapp' directory at
 * the root of its private webapp. Files in this directory can be scoped by
 * application URL stub, so a link to 
 * </p>
 * <pre>
 * /ccm/myparentapp/myappinstance/foo.jsp
 * </pre>
 * <p>
 * gets resolved to 
 * </p>
 * <pre>
 * /templates/myparentapp/myappinstance/foo.jsp
 * /templates/myparentapp/foo.jsp
 * /templates/foo.jsp
 * </pre>
 * <p>
 * until one matches.
 * </p>
 */
public class ApplicationFileServlet extends BaseApplicationServlet {

    /** Logger instance for debugging */
    private static Logger s_log = Logger.getLogger(ApplicationFileServlet.class);

    private String m_templatePath;
    private ApplicationFileResolver m_resolver;
    
    /**
     * Servlet Standard Initializer.
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config)
                throws ServletException {
        
        super.init(config);
        
        // init-param named template-path from ~/WEB-INF/web.xml
        m_templatePath = config.getInitParameter("template-path");
        Assert.exists(m_templatePath, String.class);
        Assert.isTrue(m_templatePath.startsWith("/"),
                     "template-path starts with '/'");
        Assert.isTrue(!m_templatePath.endsWith("/"),
                     "template-path must not end with '/'");

        
        // optional init-param named file-resolver from ~/WEB-INF/web.xml
        String resolverName = config.getInitParameter("file-resolver");
        if (resolverName == null) {
            m_resolver = Web.getConfig().getApplicationFileResolver();
        } else {
            m_resolver = (ApplicationFileResolver)Classes.newInstance(resolverName);
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Template path is " + m_templatePath +
                        " with resolver " + m_resolver.getClass().getName());
        }
    }

    /**
     * 
     * @param sreq
     * @param sresp
     * @param app
     * @throws ServletException
     * @throws IOException 
     */
    protected void doService(HttpServletRequest sreq,
                             HttpServletResponse sresp,
                             Application app)
                   throws ServletException, IOException {
        
        RequestDispatcher rd = m_resolver.resolve(m_templatePath,
                                                  sreq, sresp, app);
        
        if (rd == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No template found, sending 404");
            }
            sresp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Dispatching with " + rd);
        }

        sreq = DispatcherHelper.restoreOriginalRequest(sreq);
        rd.forward(sreq, sresp);
    }

}
