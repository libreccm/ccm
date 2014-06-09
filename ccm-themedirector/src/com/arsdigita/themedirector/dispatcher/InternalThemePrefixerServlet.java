/*
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
 */

package com.arsdigita.themedirector.dispatcher;

import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.web.InternalPrefixerServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * This class pulls out the theme information from the URL so that other
 * sections can correctly allow "previewing".
 * The big difference between this and the InternalPrefixerServlet
 * is that the InternalPrefixerServlet only allows a single "/yyy/"
 * where this file required "/theme/themename/"
 */
public class InternalThemePrefixerServlet extends InternalPrefixerServlet {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int the runtime environment
     *  and set 
     *  com.arsdigita.themedirector.dispatcher.InternalThemePrefixerServlet=DEBUG 
     *  by uncommenting or adding the line.                                   */
    private static final Logger s_log =
        Logger.getLogger(InternalPrefixerServlet.class);

    /** The web application context Themedirector is executing within. 
     *  Dynamically determined at runtime.                                   */
    private static ServletContext s_context;
    /**  String containing the preview prefix as the servlet is actually
     *   configured in web.xml (usually "/theme")                            */
    private String m_prefix;
    /** This value is placed as an attribute in the request when this is
     *  actually a request where the user is previewing the theme.
     *  The value of the attribute is the URL of the theme that is being
     *  previewed.                                                           */
    public final static String THEME_PREVIEW_URL = "themePreviewURL";


    /**
     * Standard servlet intialization. Initializes required variables.
     * 
     * @throws ServletException 
     */
    @Override
    public void init()
                throws ServletException {
        
        ServletConfig conf = getServletConfig();
        m_prefix = (String)conf.getInitParameter("prefix");
        
        s_context = getServletContext();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Prefix is " + m_prefix);
        }
    }

    @Override
    protected void service(HttpServletRequest req,
                           HttpServletResponse resp)
        throws ServletException, IOException {

        // We need to add the correct values to the request for 
        // use by PageTransformer
        req.setAttribute(PageTransformer.FANCY_ERRORS, Boolean.TRUE);
        req.setAttribute(PageTransformer.CACHE_XSL_NONE, Boolean.TRUE);

        String pathInfo = req.getPathInfo();
        // we still need to strip off the fist /yyy/
        // since that gives us the identity of the theme
        // that we are previewing
        String path = null;
        if (pathInfo.length() > 1) {
            if (pathInfo.startsWith("/")) {
                if (pathInfo.indexOf("/", 1) > -1) {
                    path = pathInfo.substring(pathInfo.indexOf("/", 1));
                } 
            } else {
                path = pathInfo.substring(pathInfo.indexOf("/"));
            }
        }

        String prefix;
        if (path != null) {
            String themeName = pathInfo.substring(0, pathInfo.indexOf(path));
            prefix = m_prefix + themeName;
            req.setAttribute(THEME_PREVIEW_URL, themeName);
        } else {
            prefix = m_prefix + pathInfo;
            req.setAttribute(THEME_PREVIEW_URL, pathInfo);
            path = "/index.jsp";
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Forwarding " + path);
            s_log.debug("Forwarding Prefix " + prefix);
        }

        DispatcherHelper.setDispatcherPrefix(req, prefix);

        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher(path);
        rd.forward(req, resp);
    }


    /**
     *  This checks the request to see if this is the preview of 
     *  a theme and if so, it returns the url of the theme that
     *  is being previewed.  If this is not a "preview" request
     *  then this will return null.
     * @param request
     * @return 
     */
    public static String getThemePreviewURL(HttpServletRequest request) {
        return (String)request.getAttribute(THEME_PREVIEW_URL);
    }
    
    /**
     * Service method to provide the actual context Themedirector is executing
     * within.
     * 
     * @return the ServletContext Themedirector is executing within
     */
    public static ServletContext getThemedirectorContext() {
        return s_context;
    }

}
