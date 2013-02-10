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

package com.arsdigita.portalworkspace.ui;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;

/**
 * Support class to determine users permission for viewing and/or modifying a
 * portal.
 */
public class PortalHelper {

    /**
     * Check if user is allowed to view a portal.
     * 
     * @param party
     * @param object
     * @return
     */
    public static boolean canBrowse(Party party, ACSObject object) {
        PermissionDescriptor perm =
                new PermissionDescriptor(PrivilegeDescriptor.READ,
                                         object,
                                         party);
        return PermissionService.checkPermission(perm);
    }

    /**
     * Check if user is allowed to modify (customize) a portal.
     * 
     * @param party
     * @param object
     * @return
     */
    public static boolean canCustomize(Party party, ACSObject object) {
        PermissionDescriptor perm =
                new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                         object,
                                         party);
        return PermissionService.checkPermission(perm);
    }

}
