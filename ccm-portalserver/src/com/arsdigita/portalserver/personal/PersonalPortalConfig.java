/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.portalserver.personal;

import com.arsdigita.portalserver.PortalTab;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.kernel.ResourceTypeConfig;
import com.arsdigita.web.Application;
import com.arsdigita.portal.Portlet;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.util.Assert;

/**
 * XXX Java Doc!
 *
 * @author <a href="mailto:justin@arsdigita.com">Justin Ross</a>
 * @version $Id: PersonalPortalConfig.java  pboy $
 */
public class PersonalPortalConfig extends ResourceTypeConfig {

    /**
     * Constructor registers itself with ResourceType (map).
     */
    public PersonalPortalConfig() {
        super();

        ResourceType.registerResourceTypeConfig
            (PersonalPortal.BASE_DATA_OBJECT_TYPE, this);
    }

    public void configureApplication(Application application) {

        Assert.isTrue(application instanceof PersonalPortal);

        PersonalPortal portal = (PersonalPortal) application;

        // PortalSite/Portal save ordering is in a disturbing state.
        portal.save();

        // Portlets

        PortalTab tab = PortalTab.createTab("Main");

        Portlet portlet = null;

        portlet = Portlet.createPortlet(MyPortalsPortlet.BASE_DATA_OBJECT_TYPE, 
                                        portal);
        tab.addPortlet(portlet, 1);
        tab.setPortalSite(portal);
        tab.save();

        portal.addPortalTab(tab);
        portal.addMember(portal.getOwningUser());
        portal.save();

        // Permissions

        PermissionDescriptor perm = new PermissionDescriptor
                                            (PrivilegeDescriptor.ADMIN, 
                                             portal, 
                                             portal.getOwningUser());
        PermissionService.grantPermission(perm);

    }
}
