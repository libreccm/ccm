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


package com.arsdigita.london.navigation.ui;

import com.arsdigita.templating.Templating;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.dispatcher.Dispatcher;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ComponentDispatcher implements Dispatcher {
    
    private Component m_root;
    
    public ComponentDispatcher(Component root) {
        m_root = root;
        m_root.lock();
    }

    public void dispatch(HttpServletRequest request,
                         HttpServletResponse response,
                         RequestContext actx)
        throws IOException,
               ServletException {
        
        Element content = m_root.generateXML(request,
                                             response);
        PresentationManager pm = Templating.getPresentationManager();
        try {
            pm.servePage(new Document(content),
                         request,
                         response);
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
            throw new ServletException("cannot transform document", ex);
        }
    }
}
