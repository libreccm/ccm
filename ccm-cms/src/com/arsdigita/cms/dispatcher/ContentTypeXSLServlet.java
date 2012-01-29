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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.cms.ContentType;

import com.arsdigita.util.IO;
import com.arsdigita.web.BaseServlet;
import com.arsdigita.templating.Templating;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;

import org.apache.log4j.Logger;


/**
 * A servlet that multiplexes all XSL files registered
 * against content types into one.
 */
public class ContentTypeXSLServlet extends BaseServlet {
    
    private static final Logger s_log = 
        Logger.getLogger(ContentTypeXSLServlet.class);

    @Override
    protected void doService(HttpServletRequest sreq,
                             HttpServletResponse sresp)
        throws ServletException, IOException {

        String path = sreq.getPathInfo();
        if (!path.equals("/index.xsl")) {
            s_log.error("Only index.xsl is supported " + path);
            sresp.sendError(HttpServletResponse.SC_NOT_FOUND);            
        }
        
        Iterator paths = ContentType.getXSLFileURLs();
        InputStream is = Templating.multiplexXSLFiles(paths);
        
        sresp.setContentType("text/xml; charset=UTF-8");
        IO.copy(is, sresp.getOutputStream());
    }
    
}
   
