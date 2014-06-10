/*
 * Copyright (C) 2014 Peter Boy, Universitaet Bremen. All Rights Reserved.
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

package com.arsdigita.cms.portlet;

import com.arsdigita.portal.JSRPortlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.log4j.Logger;


/**
 * Currently a wrapper for ContentItemPortlet to deliver content to an JSR
 * compliant portal server.
 * 
 * WORK IN PROGRESS!
 * 
 * @author pb
 */
public class ContentItemJSRPortlet extends JSRPortlet {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int the runtime environment
     *  and set com.arsdigita.portal.JSRPortlet=DEBUG 
     *  by uncommenting or adding the line.                                                   */
    private static final Logger s_log = Logger.getLogger(ContentItemJSRPortlet.class);


    /**
     * 
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException 
     */
    @Override
    protected void doEdit(RenderRequest request, RenderResponse response) 
              throws PortletException, IOException  {
        response.setContentType("text/html");  
        PrintWriter writer = new PrintWriter(response.getWriter());
        writer.println("You're now in Edit mode.");  
    }

    /**
     * 
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException 
     */
    @Override
    protected void doHelp(RenderRequest request, RenderResponse response) 
              throws PortletException, IOException  {
        response.setContentType("text/html");  
        PrintWriter writer = new PrintWriter(response.getWriter());
        writer.println("You're now in Help mode."); 
    }

    /**
     *
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     */
    @Override
    protected void doView(RenderRequest request, RenderResponse response) 
             throws PortletException, IOException  {
         response.setContentType("text/html");
         PrintWriter writer = new PrintWriter(response.getWriter());
         writer.println("Hello world! You're in View mode.");
    }
    
}