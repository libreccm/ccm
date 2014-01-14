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

import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Kernel;

import com.arsdigita.messaging.MessageThread;

import com.arsdigita.web.Application;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.web.ParameterMap;

import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ForumContext;
import com.arsdigita.forum.ThreadCollection;

import java.math.BigDecimal;

/** 
 * Creates a list of existing threads as a GUI component. Currently invoked
 * by ThreadsPanel only.
 * A paginator is added to handle a high number of threads.
 * 
 */
public class ThreadsList extends SimpleComponent implements Constants {

    /**  */
    private IntegerParameter m_pageNumber =
        new IntegerParameter(PAGINATOR_PARAM);
    /** Default max number of threads (rows) per page */
    private int m_pageSize = 15;

    /**
     * 
     * @param p 
     */
    @Override
    public void register(Page p) {
        super.register(p);
        
        p.addGlobalStateParam(m_pageNumber);
    }

    /**
     * 
     * @param state
     * @return
     */
    private ThreadCollection getThreads(PageState state) {

        ForumContext context = ForumContext.getContext(state);
        Party party = Kernel.getContext().getParty();
        Forum forum = context.getForum();
        
        BigDecimal categoryID = context.getCategorySelection();
        if (categoryID != null
            && categoryID.equals(TOPIC_ANY)) {
            categoryID = null;
        }
        
        ThreadCollection threads = forum.getThreads(categoryID,
                                                    Kernel.getContext().getParty());
        
        return threads;
    }

    /**
     * Create the xml for this component.
     * @param state
     * @param parent
     */
    @Override
    public void generateXML(PageState state,
                            Element parent) {
        // Begin of thread list, XSL constructs (and localizes) the list title bar
        Element content = parent.newChildElement(FORUM_XML_PREFIX +
                                                 ":threadList", FORUM_XML_NS);

        ThreadCollection threads = getThreads(state);

        Integer page = (Integer)state.getValue(m_pageNumber);
        int pageNumber = (page == null ? 1 : page.intValue());
        long objectCount = threads.size();
        int pageCount = (int)Math.ceil((double)objectCount / (double)m_pageSize);
        
        if (pageNumber < 1) {
            pageNumber = 1;
        }

        if (pageNumber > pageCount) {
            pageNumber = (pageCount == 0 ? 1 : pageCount);
        }
        
        long begin = ((pageNumber-1) * m_pageSize);
        int count = (int)Math.min(m_pageSize, (objectCount - begin));
        long end = begin + count;

        generatePaginatorXML(content,
                             pageNumber,
                             pageCount,
                             m_pageSize,
                             begin,
                             end,
                             objectCount);
        
        if (begin != 0 || end != 0) {
            threads.setRange(new Integer((int)begin+1),
                             new Integer((int)end+1));
        }
        
        while (threads.next()) {   // step through ThreadCollections

            MessageThread thread = threads.getMessageThread();
            Element threadEl = content.newChildElement(FORUM_XML_PREFIX +
                                                       ":thread", FORUM_XML_NS);
            
            // create link to a JSP which provide a List of messages for the
            // thread, i.e. this first message and all its followup messages
            ParameterMap map = new ParameterMap();
            map.setParameter(THREAD_PARAM, thread.getID());
            URL url = URL.there((Application)Kernel.getContext().getResource(),
                                "/thread.jsp",
                                map);
            threadEl.addAttribute("url", XML.format(url));

            DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(threadEl);
            xr.setWrapRoot(false);
            xr.setWrapAttributes(true);
            xr.setWrapObjects(false);
            
            xr.walk(thread, ThreadsList.class.getName());
        }
        
    }

    /**
     * Create the paginators xml
     * @param parent
     * @param pageNumber
     * @param pageCount
     * @param pageSize
     * @param begin
     * @param end
     * @param objectCount
     */
    protected void generatePaginatorXML(Element parent,
                                        int pageNumber,
                                        int pageCount,
                                        int pageSize,
                                        long begin,
                                        long end,
                                        long objectCount) {
        Element paginator = parent.newChildElement(FORUM_XML_PREFIX +
                                                   ":paginator", FORUM_XML_NS);
        
        URL here = Web.getContext().getRequestURL();
        ParameterMap params = new ParameterMap(here.getParameterMap());
        params.clearParameter(PAGINATOR_PARAM);

        URL url = new URL(here.getScheme(),
                          here.getServerName(),
                          here.getServerPort(),
                          here.getContextPath(),
                          here.getServletPath(),
                          here.getPathInfo(),
                          params);
        
        paginator.addAttribute("param", PAGINATOR_PARAM);
        paginator.addAttribute("baseURL", XML.format(url));
        paginator.addAttribute("pageNumber", XML.format(new Integer(pageNumber)));
        paginator.addAttribute("pageCount", XML.format(new Integer(pageCount)));
        paginator.addAttribute("pageSize", XML.format(new Integer(pageSize)));
        paginator.addAttribute("objectBegin", XML.format(new Long(begin+1)));
        paginator.addAttribute("objectEnd", XML.format(new Long(end)));
        paginator.addAttribute("objectCount", XML.format(new Long(objectCount)));
    }

}
