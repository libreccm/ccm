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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.dispatcher.SimpleXMLGenerator;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.xml.Element;

/**
 * GreetingItem component displays a list of items in a category, ordered by 
 * title.  The provided links allow the list of items to be restricted by 
 * a letter of the alphabet.
 * The number, type, and attributes of the returned items can be configured 
 * from jsp page using setHowMany( int ), * setObjectType( String ), 
 * and addAttribute( String ) methods.  The object type must be either that of
 * ContentPage or extend the ContentPage.
 *
 * @author <a href="mailto:dominik@redhat.com">Dominik Kacprzak</a>
 * @version $Id: GreetingItem.java 1473 2007-03-12 15:16:39Z chrisgilbert23 $
 */
public class GreetingItem extends AbstractComponent {
    private static final Logger s_log = Logger.getLogger( GreetingItem.class );
    
    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        ContentItem item = (ContentItem)getObject();
        if (null == item || !item.isLive()) {
            return null;
        }
        
        if (!ContentItem.VERSION.equals(item.getVersion())) {
            item = item.getLiveVersion();
        }

		
        Element content = Navigation.newElement("greetingItem");
        Party currentParty = Kernel.getContext().getParty();
        if (currentParty == null) {
        	currentParty = Kernel.getPublicUser();
        }
        // check if current user can edit the current index item (nb privilege is granted on draft item, but live item 
        // has draft as its permission context
        //
        // Note that the xml that is generated is only of use if you DO NOT CACHE index pages. 
        // cg. 
        
        PermissionDescriptor edit = new PermissionDescriptor(PrivilegeDescriptor.get(SecurityManager.CMS_EDIT_ITEM), item, currentParty);
        if (PermissionService.checkPermission(edit)) {
        	content.addAttribute("canEdit", "true");
        }
        PermissionDescriptor publish = new PermissionDescriptor(PrivilegeDescriptor.get(SecurityManager.CMS_PUBLISH), item, currentParty);
        if (PermissionService.checkPermission(publish)) {
        	content.addAttribute("canPublish", "true");
        }
        
        ContentBundle bundle = (ContentBundle)item;
        ContentItem baseItem = bundle.getPrimaryInstance();
        Element itemEl = content.newChildElement("cms:item",
                                                 CMS.CMS_XML_NS);
        
        DomainObjectXMLRenderer renderer =
            new DomainObjectXMLRenderer(itemEl);
        
        renderer.setWrapAttributes( true );
        renderer.setWrapRoot( false );
        renderer.setWrapObjects( false );
        
        renderer.walk( baseItem, SimpleXMLGenerator.ADAPTER_CONTEXT );
        
        return content;
    }
    
}
