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
package com.arsdigita.util;

import com.arsdigita.dispatcher.RequestContext;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 *  Dummy RequestContext object for unit testing of form methods that
 *  include requests in their signatures.
 *
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 */

public class DummyRequestContext implements RequestContext {

    public static final String versionId = "$Id: DummyRequestContext.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private ServletContext m_servletContext;
    private HttpServletRequest m_request;
    private boolean m_debugging;
    private boolean m_debuggingXML;
    private boolean m_debuggingXSL;

    public DummyRequestContext(HttpServletRequest request,
                               ServletContext servletContext) {
        this(request, servletContext, true);
    }

    public DummyRequestContext(HttpServletRequest request,
                               ServletContext servletContext, boolean isDebug) {
        m_request = request;
        m_servletContext = servletContext;
        m_debugging = isDebug;
    }

    /**
     * @return the portion of the URL that has not been used yet
     * by a previous dispatcher in the chain, and must be used by
     * the current dispatcher
     */
    public String getRemainingURLPart() {
        return null;
    }

    /**
     * @return the portion of the URL that has already been used
     * by previous dispatchers in the chain.
     */
    public String getProcessedURLPart() {
        return null;
    }

    /**
     * @return The original URL requested by the end-user's browser.
     * All generated HREF, IMG SRC, and FORM ACTION attributes will
     * be relative to this URL, as will redirects.
     */
    public String getOriginalURL() {
        return m_request.getRequestURI();
    }

    /**
     * @return the current servlet context; must be set by implementation.
     */
    public ServletContext getServletContext() {
        return null;
    }

    public Locale getLocale() {
        return Locale.ENGLISH;
    }

    public ResourceBundle getResourceBundle() {
        return null;
    }

    public String getOutputType() {
        return null;
    }

    public boolean getDebugging() {
        return m_debugging;
    }

    public boolean getDebuggingXML() {
        return m_debuggingXML;
    }

    public boolean getDebuggingXSL() {
        return m_debuggingXSL;
    }

    public String getPageBase() {
        return null;
    }

}
