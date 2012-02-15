/*
 * Copyright (C) 2012 Peter Boy All Rights Reserved.
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

package com.arsdigita.cms;

import com.arsdigita.cms.dispatcher.SimpleCache;
import com.arsdigita.developersupport.DeveloperSupport;
import com.arsdigita.kernel.security.UserContext;
import com.arsdigita.kernel.security.Util;
import com.arsdigita.ui.login.LoginHelper;
import com.arsdigita.web.Application;
import com.arsdigita.web.BaseApplicationServlet;

import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.Web;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * CMS Workspace (content-center) application servlet serves all request made 
 * within the Content Center application. 
 * 
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: WorkspaceServlet.java 2161 2011-02-02 00:16:13Z pboy $
 */
public class WorkspaceServlet extends BaseApplicationServlet {

    /**Error logging    */
    private static Logger s_log = Logger
                                  .getLogger(WorkspaceServlet.class.getName());

    /** The path of the file that maps resources.     */
    public final static String DEFAULT_MAP_FILE =
                               "/WEB-INF/resources/content-center-map.xml";

    /** Mapping between a relative URL and the class name of a ResourceHandler.*/
    private static HashMap s_pageClasses = WorkspaceSetup.getURLToClassMap();
    private static HashMap s_pageURLs = WorkspaceSetup.getClassToURLMap();

    /**
     * Instantiated ResourceHandlers cache. This allows for lazy loading.
     */
    private static SimpleCache s_pages = new SimpleCache();

//     private Dispatcher m_notFoundHandler;
    private ArrayList m_trailingSlashList = new ArrayList();



    /**
     * Implements the (abstract) doService method of BaseApplicationServlet to
     * create the Worspace page.
     * 
     * @see com.arsdigita.web.BaseApplicationServlet#doService
     *      (HttpServletRequest, HttpServletResponse, Application)
     */
    protected void doService( HttpServletRequest sreq, 
                              HttpServletResponse sresp, 
                              Application app)
                   throws ServletException, IOException {

        if (s_log.isDebugEnabled()) {
            s_log.info("starting doService method");
        }
        DeveloperSupport.startStage("ContentCenterServlet.doService");

        // Check user access.
        checkUserAccess(sreq, sresp);

        
        DeveloperSupport.endStage("ContentCenterServlet.doService");
        if (s_log.isDebugEnabled()) {
            s_log.info("doService method completed");
        }
    }

    
    
    
    
    /**
     * Verify that the user is logged in and is able to view the
     * page. Subclasses can override this method if they need to, but
     * should always be sure to call super.checkUserAccess(...)
     *
     * @param request  The HTTP request
     * @param response The HTTP response
     * @param actx     The request context
     **/
    protected void checkUserAccess(final HttpServletRequest request,
                                   final HttpServletResponse response //,
///                                 final RequestContext actx
                                  )
                   throws ServletException {
        
        if (!Web.getUserContext().isLoggedIn()) {
            throw new LoginSignal(request);

        }
    }

    /**
     * Redirects the client to the login page, setting the return url to
     * the current request URI.
     *
     * @exception ServletException If there is an exception thrown while
     * trying to redirect, wrap that exception in a ServletException
     **/
    protected void redirectToLoginPage(HttpServletRequest req,
                                       HttpServletResponse resp)
        throws ServletException {
        String url = Util.getSecurityHelper()
                         .getLoginURL(req)
                         +"?"+LoginHelper.RETURN_URL_PARAM_NAME
                         +"="+UserContext.encodeReturnURL(req);
        try {
            LoginHelper.sendRedirect(req, resp, url);
        } catch (IOException e) {
            s_log.error("IO Exception", e);
            throw new ServletException(e.getMessage(), e);
        }
    }

}
