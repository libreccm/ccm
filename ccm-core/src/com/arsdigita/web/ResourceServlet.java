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

import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.util.IO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;


/**
 * A servlet that maps the ResourceManager#findResource
 * method into the http:// URL space. This enables negotiated
 * resolution of resources across webapps. For example, a request
 * to:
 * <pre>
 *   http://www.example.com/resource/myproj,ccm-cms,ROOT/packages/bebop/xsl/bebop.xsl
 * </pre>
 * Will look for the following real files:
 * <pre>
 *   http://www.example.com/myproj/packages/bebop/xsl/bebop.xsl
 *   http://www.example.com/ccm-cms/packages/bebop/xsl/bebop.xsl
 *   http://www.example.com/packages/bebop/xsl/bebop.xsl
 * </pre>
 */
public class ResourceServlet extends BaseServlet {
    
    private static final Logger s_log = 
        Logger.getLogger(ResourceServlet.class);

    protected void doService(HttpServletRequest sreq,
                             HttpServletResponse sresp)
        throws ServletException, IOException {

        String path = sreq.getPathInfo();
        InputStream stream = Web.findResourceAsStream(path);
        
        if (stream == null) {
            s_log.error("Cannot find resource for " + path);
            sresp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        MimeType type = MimeType.guessMimeTypeFromFile(path);
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Mime type is " + (type == null ? null : type.getOID()));
        }

        sresp.setContentType(type == null ? "text/plain" : type.getMimeType());
        IO.copy(stream, sresp.getOutputStream());
    }
    
}
   
