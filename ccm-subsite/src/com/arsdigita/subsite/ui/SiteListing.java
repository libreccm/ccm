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

package com.arsdigita.subsite.ui;

import com.arsdigita.subsite.Subsite;
import com.arsdigita.subsite.Site;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.PageState;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.xml.Element;


/**
 * 
 * 
 */
public class SiteListing extends SimpleComponent {
    
    private final SiteSelectionModel m_site;

    public SiteListing(SiteSelectionModel site) {
        m_site = site;
    }

    @Override
    public void generateXML(PageState state,
                            Element parent) {
        Element content = parent.newChildElement(
            Subsite.SUBSITE_XML_PREFIX + "siteListing",
            Subsite.SUBSITE_XML_NS
        );

        Object key = m_site.getSelectedKey(state);

        if (key != null) {
            content.addAttribute("selected", key.toString());
        }
        
        DomainObjectXMLRenderer renderer = new DomainObjectXMLRenderer(content);
        renderer.setWrapRoot(true);
        renderer.setWrapAttributes(true);

        DomainCollection sites = 
            new DomainCollection(SessionManager.getSession()
                                 .retrieve(Site.BASE_DATA_OBJECT_TYPE));
        sites.addOrder(Site.TITLE);

        while (sites.next()) {
            DomainObject site = sites.getDomainObject();
            
            renderer.walk(site, getClass().getName());
        }
    }
}
