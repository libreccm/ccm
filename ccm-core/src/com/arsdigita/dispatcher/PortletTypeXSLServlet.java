/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.dispatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.arsdigita.portal.PortletType;
import com.arsdigita.templating.Templating;
import com.arsdigita.util.IO;
import com.arsdigita.web.BaseServlet;


/**
 * A servlet that multiplexes all XSL files registered
 * against portlet types into one.
 * 
 * Copied from ccm-cms/com.arsdigita.cms.dispatcher.ContentTypeXSLServlet
 */
public class PortletTypeXSLServlet extends BaseServlet {
    
    private static final Logger s_log = 
        Logger.getLogger(PortletTypeXSLServlet.class);

    protected void doService(HttpServletRequest sreq,
                             HttpServletResponse sresp)
        throws ServletException, IOException {
           s_log.debug("Servicing request for portlet type xsl imports");
        String path = sreq.getPathInfo();
        if (!path.equals("/index.xsl")) {
            s_log.error("Only index.xsl is supported " + path);
            sresp.sendError(HttpServletResponse.SC_NOT_FOUND);            
        }
        
        Iterator paths = PortletType.getXSLFileURLs();
        InputStream is = Templating.multiplexXSLFiles(paths);
        
        sresp.setContentType("text/xml; charset=UTF-8");
        IO.copy(is, sresp.getOutputStream());
    }
    
}
