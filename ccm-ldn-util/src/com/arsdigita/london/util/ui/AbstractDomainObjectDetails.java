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

package com.arsdigita.london.util.ui;

import com.arsdigita.bebop.PageState;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;

import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;


import java.util.Iterator;

public abstract class AbstractDomainObjectDetails
    extends AbstractDomainObjectComponent {
    
    private String m_prefix;

    public AbstractDomainObjectDetails(String name,
                                       String prefix,
                                       String xmlns) {
        super(prefix + ":" + name, xmlns);
        m_prefix = prefix;
    }
    
    protected abstract DomainObject getDomainObject(PageState state);
    
    public void generateXML(PageState state,
                            Element parent) {
        Element content = generateParent(parent);
        
        DomainObject obj = getDomainObject(state);
        generateObjectXML(state, content, obj);
    }
    
    protected void generateObjectXML(PageState state,
                                     Element parent,
                                     DomainObject dobj) {
        DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(parent);
        xr.setNamespace(m_prefix, getNamespace());
        xr.setWrapRoot(false);
        xr.setWrapAttributes(true);
        xr.setWrapObjects(false);
        
        xr.walk(dobj,
                getClass().getName());
        
        generateActionXML(state, parent, dobj);
    }

    protected void generateActionXML(PageState state,
                                     Element parent,
                                     DomainObject dobj) {
        Iterator actions = getDomainObjectActions();
        while (actions.hasNext()) {
            String action = (String)actions.next();
			boolean actionVisible = true;
			PrivilegeDescriptor privilege =	getDomainObjectActionPrivilege(action);
			if (privilege != null) {
				Party party = Kernel.getContext().getParty();
				if (party == null) {
					party = Kernel.getPublicUser();
				}
				Assert.truth(dobj.getObjectType().isSubtypeOf(ACSObject.BASE_DATA_OBJECT_TYPE),
					"I can only check permissions on ACS Objects - this domain Object is not a subtype of ACSObject ");

				PermissionDescriptor permission =	new PermissionDescriptor(privilege,(ACSObject) dobj,party);
				actionVisible = PermissionService.checkPermission(permission);
			}
			if (actionVisible) {
            Element actionEl = parent.newChildElement(m_prefix + ":action",
                                                      getNamespace());
            actionEl.addAttribute("name", action);
            actionEl.addAttribute("url", 
                                  getDomainObjectActionLink(state, dobj, action));
        }
    }
}
}
