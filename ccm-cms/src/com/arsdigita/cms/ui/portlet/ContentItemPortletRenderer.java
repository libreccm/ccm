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
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentItemXMLRenderer;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.ItemResolver;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.cms.portlet.ContentItemPortlet;
import com.arsdigita.dispatcher.AccessDeniedException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.web.LoginSignal;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;


public class ContentItemPortletRenderer extends AbstractPortletRenderer {
    private static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(ContentItemPortletRenderer.class);

    private ContentItemPortlet m_portlet;

    public ContentItemPortletRenderer(ContentItemPortlet portlet) {
        m_portlet = portlet;
    }

    public void generateBodyXML(PageState state,
                                Element parent) {
        Element content = parent.newChildElement("portlet:contentItem",
                                       "http://www.uk.arsdigita.com/portlet/1.0");
                
        ContentItem item = m_portlet.getContentItem();
        
        Party currentParty = Kernel.getContext().getParty();
        if (currentParty == null) {
            currentParty = Kernel.getPublicUser();
        }
        
        PermissionDescriptor read = new PermissionDescriptor(PrivilegeDescriptor.get(SecurityManager.CMS_READ_ITEM), item, currentParty);
        if (!PermissionService.checkPermission(read)) {
            if (Web.getUserContext().isLoggedIn()) {
                throw new AccessDeniedException("User does cannot read content item " + item.getName());
            }
            throw new LoginSignal(Web.getRequest());
        }
        
        PermissionDescriptor edit = new PermissionDescriptor(PrivilegeDescriptor.get(SecurityManager.CMS_EDIT_ITEM), item, currentParty);
        if (PermissionService.checkPermission(edit)) {
            content.addAttribute("canEdit", "true");
        }
        PermissionDescriptor publish = new PermissionDescriptor(PrivilegeDescriptor.get(SecurityManager.CMS_PUBLISH), item, currentParty);
        if (PermissionService.checkPermission(publish)) {
            content.addAttribute("canPublish", "true");
        }

        if( null == item ) {
            s_log.warn( "No content item for content item portlet " +
                        m_portlet.getOID() );
            return;
        }
        if( !item.isLive() ) {
            s_log.info( "No live version for content item portlet " +
                        m_portlet.getOID() );
            return;
        }
        renderItem(item.getPublicVersion(), content);
    }

    protected void renderItem(ContentItem item, Element content) {
        Element contentItem = content.newChildElement("cms:item",
                CMS.CMS_XML_NS);

        ContentItemXMLRenderer renderer = new ContentItemXMLRenderer(contentItem);

        renderer.setWrapAttributes(true);
        renderer.setWrapRoot(false);
        renderer.setWrapObjects(false);

        renderer.walk(item,
                SimpleXMLGenerator.ADAPTER_CONTEXT);
    }
    
    @Override
    public String getCacheKey(PageState state) {
        ContentItem item = m_portlet.getContentItem();
        if( null == item ) {
            return "";
        }
        if( item.isLive() ) {
            return item.getPublicVersion().getOID().toString();
        }

        // Don't cache it if it's not live
        return "";
    }

    // For a given cache key a contnet item is *never* dirty,
    // since upon republishing of a live item, the item_id
    // changes!
    @Override
    public boolean isDirty(PageState state) {
        return false;
    }

}
