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
package com.arsdigita.cms.ui.portlet;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.cms.portlet.ContentSectionsPortlet;
import com.arsdigita.cms.portlet.ContentSectionsQuery;
import com.arsdigita.xml.Element;


public class ContentSectionsPortletRenderer extends AbstractPortletRenderer {
    private ContentSectionsPortlet m_portlet;

    public ContentSectionsPortletRenderer(ContentSectionsPortlet portlet) {
        m_portlet = portlet;
    }

    public void generateBodyXML(PageState state,
                                Element parent) {
        Element content = parent.newChildElement("portlet:contentSections",
                                       "http://www.arsdigita.com/portlet/1.0");

        ContentSectionsQuery sections = new ContentSectionsQuery();
        while (sections.next()) {
            Element child = content.newChildElement("portlet:contentSection",
                                       "http://www.arsdigita.com/portlet/1.0");

            child.addAttribute("name", sections.getName());
            child.addAttribute("url", sections.getURL());
        }
    }
}
