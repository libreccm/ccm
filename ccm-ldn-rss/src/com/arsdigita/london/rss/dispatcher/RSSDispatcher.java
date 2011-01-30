/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
 */

package com.arsdigita.london.rss.dispatcher;

import com.arsdigita.london.rss.RSSChannel;
import com.arsdigita.london.rss.RSSRenderer;

import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.dispatcher.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;


public abstract class RSSDispatcher implements Dispatcher {

    /**
     * 
     * @param request
     * @param response
     * @param actx
     * @return
     * @throws ServletException
     */
    public abstract RSSChannel getChannel(HttpServletRequest request,
                                          HttpServletResponse response,
                                          RequestContext actx)
        throws ServletException;
    
    /**
     * 
     * @param request
     * @param response
     * @param actx
     * @throws IOException
     * @throws ServletException
     */
    public void dispatch(HttpServletRequest request,
                         HttpServletResponse response,
                         RequestContext actx)
            throws IOException, ServletException {
        RSSChannel channel = getChannel(request, response, actx);

        response.setContentType("text/xml");
        response.setStatus(HttpServletResponse.SC_OK);
        // Write XML to the output stream
        Document doc = new Document(RSSRenderer.generateJDOM(channel));
        
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setNewlines(true);
        xmlOutput.setIndent(true);

        response.setContentType("text/xml");
        response.setStatus(HttpServletResponse.SC_OK);
        xmlOutput.output( doc, response.getWriter() );
    }

}
