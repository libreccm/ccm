/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.portation.conversion.core.security;

import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.security.Permission;
import com.arsdigita.portation.modules.core.security.Role;
import com.arsdigita.portation.modules.core.security.User;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 7/14/16
 */
public class PermissionConversion {

    public static void convertAll() {
        List<com.arsdigita.kernel.permissions.Permission> trunkPermissions =
                com.arsdigita.kernel.permissions.Permission
                        .getAllObjectPermissions();

        createPermissionsAndSetAssociations(trunkPermissions);
    }

    private static void createPermissionsAndSetAssociations(List<com
            .arsdigita.kernel.permissions.Permission> trunkPermissions) {
        for (com.arsdigita.kernel.permissions.Permission trunkPermission :
                trunkPermissions) {

            // create Permissions
            Permission permission = new Permission(trunkPermission);

            // set object and opposed associations
            CcmObject object = NgCollection.ccmObjects.get(((BigDecimal)
                    trunkPermission.getACSObject().get("id")).longValue());
            if (object != null) {
                permission.setObject(object);
                object.addPermission(permission);
            }

            // set grantee and opposed associations
            Role role = NgCollection.roles.get(0); //Todo: fix "0"
            if (role != null) {
                permission.setGrantee(role);
                role.addPermission(permission);
            }

            // set creationUser
            User creationUser = NgCollection.users.get(trunkPermission
                    .getCreationUser().getID().longValue());
            if (creationUser != null)
                permission.setCreationUser(creationUser);
        }
    }
}
