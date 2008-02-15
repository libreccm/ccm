/*
 * Copyright (C) 2007 Chris Gilbert. All Rights Reserved.
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
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.bebop.PageState;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.formatters.DateFormatter;
import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.apportlet.AppPortlet;

/**
 * 
 * @author chris.gilbert@westsussex.gov.uk
 * 
 * portlet with no attributes that displays links to all forums that user has read access to
 *
 */
public class MyForumsPortlet extends Portlet {
    public static final String versionId = "$Id: MyForumsPortlet.java,v 1.4 2006/07/13 10:19:28 cgyg9330 Exp $ by $Author: cgyg9330 $, $DateTime: 2004/08/17 23:26:27 $";

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.forum.MyForumsPortlet";

    
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public MyForumsPortlet(DataObject dataObject) {
        super(dataObject);
    }

    

    protected AbstractPortletRenderer doGetPortletRenderer() {
        return new MyForumsPortletRenderer(this);
    }

   
}

class MyForumsPortletRenderer 
    extends AbstractPortletRenderer 
    implements Constants {

    private MyForumsPortlet m_portlet;

    public MyForumsPortletRenderer(MyForumsPortlet
                                         portlet) {
        m_portlet = portlet;
    }

    protected void generateBodyXML(PageState pageState,
                                   Element parent) {
        Element content = parent.newChildElement(FORUM_XML_PREFIX + ":myForumsPortlet", 
                                                 FORUM_XML_NS);

        
        Party party = Kernel.getContext().getParty();
        if (party == null) {
        	party = Kernel.getPublicUser();
        }
        
        DataCollection forums = SessionManager.getSession().retrieve(Forum.BASE_DATA_OBJECT_TYPE);
        forums.addOrder("lower(" + Forum.TITLE + ")");
        PermissionService.filterObjects(forums, PrivilegeDescriptor.READ, party.getOID());
      

        while (forums.next()) {
            Forum forum = (Forum)DomainObjectFactory.newInstance(forums.getDataObject());
            Element forumEl = content.newChildElement(FORUM_XML_PREFIX + ":forum", FORUM_XML_NS);
            URL url = URL.there(forum, "/", null);
            forumEl.addAttribute("url", url.toString());
			forumEl.addAttribute("title", forum.getTitle());
			// display last forum update info
			ThreadCollection threads = forum.getThreads();
			threads.addOrder(MessageThread.LAST_UPDATE);
			if (threads.next()) {
				MessageThread lastUpdatedThread = threads.getMessageThread();
				forumEl.addAttribute("lastUpdated", new DateFormatter().format(lastUpdatedThread.getLatestUpdateDate()));
				threads.close();
			} else {
				forumEl.addAttribute("lastUpdated", "");
			}

           
        }
    }

}
