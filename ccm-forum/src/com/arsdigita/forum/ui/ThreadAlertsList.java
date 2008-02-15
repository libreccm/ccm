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
package com.arsdigita.forum.ui;


import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;

import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.domain.DomainCollection;

import com.arsdigita.kernel.Kernel;


import com.arsdigita.web.Application;
import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;

import com.arsdigita.xml.Element;

import com.arsdigita.forum.ThreadSubscription;



public class ThreadAlertsList extends SimpleComponent 
    implements Constants {

    private IntegerParameter m_pageNumber =
        new IntegerParameter(PAGINATOR_PARAM);
    
    private int m_pageSize = 5;

    public void register(Page p) {
        super.register(p);
        
        p.addGlobalStateParam(m_pageNumber);
    }

    public void generateXML(PageState state,
                            Element parent) {
        Element content = parent.newChildElement(
            FORUM_XML_PREFIX + ":threadAlertList", FORUM_XML_NS);
        exportAttributes(content);

        DomainCollection subs = ThreadSubscription.
            getSubsForUser(Kernel.getContext().getParty(), state);

        while (subs.next()) {
            ThreadSubscription sub = (ThreadSubscription)subs.getDomainObject();
            content.addContent(generateAlertXML(sub));
        }
        
    }
    
    
    public Element generateAlertXML(ThreadSubscription sub) {
        Element subEl = new Element(
            FORUM_XML_PREFIX + ":threadAlert", FORUM_XML_NS);

        ParameterMap map = new ParameterMap();
        map.setParameter(THREAD_PARAM, sub.getThreadReal().getID());
        URL url = URL.there((Application)Kernel.getContext().getResource(),
                            "/thread.jsp",
                            map);
        subEl.addAttribute("url", url.toString());

        
        DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(subEl);
        xr.setWrapRoot(false);
        xr.setWrapAttributes(true);
        xr.setWrapObjects(false);
        
        xr.walk(sub, ThreadAlertsList.class.getName());
        return subEl;
    }
}
