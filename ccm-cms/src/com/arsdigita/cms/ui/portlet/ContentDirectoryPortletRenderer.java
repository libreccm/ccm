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
import com.arsdigita.cms.portlet.ContentDirectoryPortlet;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;


public class ContentDirectoryPortletRenderer extends AbstractPortletRenderer {
    private static Logger s_log = Logger.getLogger
        (ContentDirectoryPortletRenderer.class.getName());

    private ContentDirectoryPortlet m_portlet;
    
    public ContentDirectoryPortletRenderer(ContentDirectoryPortlet portlet) {
        m_portlet = portlet;
    }

    public void generateBodyXML(PageState state,
                                Element parent) {
        HashMap cats = new HashMap();

        Element element = parent.newChildElement("portlet:contentDirectory",
                                       "http://www.arsdigita.com/portlet/1.0");
        element.addAttribute("id",getIdAttr());

        DataQuery categories = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.london.portal.portlet.getContentDirectory");

        while (categories.next()) {
            BigDecimal categoryID = (BigDecimal) categories.get("categoryID");
            BigDecimal parentID = (BigDecimal) categories.get("parentID");
            String name = (String) categories.get("name");
            String description = (String) categories.get("description");
            String path = (String) categories.get("path");

            int depth = 0;
            int offset = path.indexOf('/');
            while (offset != -1) {
                depth++;
                offset = path.indexOf('/', offset+1);
            }
            
            // XXX depth is currently 2 greater than it actually is
            // since it includes Site Root, & Navigation cats
            depth -= 2;
            // XXX We really shouldn't pull out the categories if we don't
            // actually want them
            if (!(depth == 1 || depth == 2)) {
                continue;
            }

            if ( depth == 1 ) {
                Element categoryElement = element.newChildElement(
                    "portlet:contentDirectoryEntry", 
                    "http://www.arsdigita.com/portlet/1.0"
                );
                categoryElement.addAttribute("categoryID", categoryID.toString());
                categoryElement.addAttribute("name", name);
                categoryElement.addAttribute("description", description);

                cats.put(categoryID, categoryElement);
            } else if ( depth == 2 ) {
                Element parentElement = (Element)cats.get(parentID);
                if (parentElement != null) {
                    Element categoryElement = parentElement.newChildElement(
                        "portlet:contentDirectorySubentry", 
                        "http://www.arsdigita.com/portlet/1.0"
                    );
                    categoryElement.addAttribute("categoryID", 
                                                 categoryID.toString());
                    categoryElement.addAttribute("name", name);
                    categoryElement.addAttribute("description", description);

                    cats.put(categoryID, categoryElement);
                } else {
                    s_log.warn("Cannot find parent category element " + 
                               parentID + " for " + categoryID);
                }
            }
        }
        categories.close();


    }
}
