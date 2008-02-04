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
package com.arsdigita.forum.portlet;

import com.arsdigita.forum.Forum;
import com.arsdigita.forum.ThreadCollection;
import com.arsdigita.forum.ui.Constants;
import com.arsdigita.forum.ui.ThreadList;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.xml.Element;
import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.portal.apportlet.AppPortlet;


public class RecentPostingsPortlet extends AppPortlet {
    public static final String versionId = "$Id: RecentPostingsPortlet.java 755 2005-09-02 13:42:47Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:26:27 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.forum.RecentPostingsPortlet";

    private static final Integer DEFAULT_POSTS = new Integer(5);

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public RecentPostingsPortlet(DataObject dataObject) {
        super(dataObject);
    }

    public void initialize() {
        super.initialize();
        if (isNew()) {
            setNumPosts(DEFAULT_POSTS);
        }
    }

    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new RecentPostingsPortletRenderer(this);
    }

    public void setNumPosts(Integer iNumPosts) {
        set("numPosts", iNumPosts);
    }

    public Integer getNumPosts() {
        return (Integer) get("numPosts");
    }

}

class RecentPostingsPortletRenderer 
    extends AbstractPortletRenderer 
    implements Constants {

    private RecentPostingsPortlet m_portlet;

    public RecentPostingsPortletRenderer(RecentPostingsPortlet
                                         recentPostingsPortlet) {
        m_portlet = recentPostingsPortlet;
    }

    protected void generateBodyXML(PageState pageState,
                                   Element parent) {
        Element content = parent.newChildElement("forum:recentPostingsPortlet", 
                                                 FORUM_XML_NS);

        // XXX new post param

        Forum forum = (Forum)m_portlet.getParentApplication();

        content.addAttribute("noticeboard", (new Boolean(forum.isNoticeboard())).toString());

        Party party = Kernel.getContext().getParty();
        
        ThreadCollection threads = forum.getThreads(
            Kernel.getContext().getParty());

        threads.setRange(new Integer(1),
                         new Integer(m_portlet.getNumPosts().intValue() + 1));

        while (threads.next()) {
            MessageThread thread = threads.getMessageThread();
            Element threadEl = content.newChildElement("forum:thread", FORUM_XML_NS);
            
            ParameterMap map = new ParameterMap();
            map.setParameter(THREAD_PARAM, thread.getID());
            URL url = URL.there(forum,
                                "/thread.jsp",
                                map);
            threadEl.addAttribute("url", url.toString());

            DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(threadEl);
            xr.setWrapRoot(false);
            xr.setWrapAttributes(true);
            xr.setWrapObjects(false);
            
            xr.walk(thread, ThreadList.class.getName());
        }
    }

}
