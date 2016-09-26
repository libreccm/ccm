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

import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.security.Permission;
import com.arsdigita.portation.modules.core.security.Role;
import com.arsdigita.portation.modules.core.security.RoleMembership;
import com.arsdigita.portation.modules.core.security.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class for converting all
 * trunk-{@link com.arsdigita.kernel.permissions.Permission}s into
 * ng-{@link Permission}s as preparation for a successful export of all trunk
 * classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 14.7.16
 */
public class PermissionConversion {

    /**
     * Retrieves all trunk-{@link com.arsdigita.kernel.permissions.Permission}s
     * from the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link Permission}s focusing on keeping all
     * the associations in tact. The association to the {@code
     * grantee}-{@link Role} has to be recreated separately.
     */
    public static void convertAll() {
        List<com.arsdigita.kernel.permissions.Permission> trunkPermissions =
                com.arsdigita.kernel.permissions.Permission
                        .getAllObjectPermissions();

        createPermissionsAndSetAssociations(trunkPermissions);

        setGranteeDependency(trunkPermissions);
    }

    /**
     * Creates the equivalent ng-class of the {@code Permission} and restores
     * the associations to other classes.
     *
     * @param trunkPermissions List of all
     *                         {@link com.arsdigita.kernel.permissions.Permission}s
     *                         from the old trunk-system
     */
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

            // set creationUser
            com.arsdigita.kernel.User trunkCreationUser = trunkPermission
                    .getCreationUser();
            if (trunkCreationUser != null) {
                User creationUser = NgCollection.users.get(trunkCreationUser
                        .getID().longValue());

                if (creationUser != null)
                    permission.setCreationUser(creationUser);
            }
        }
    }

    /**
     * Method recreating the association to class {@link Role} representing the
     * {@code grantee} of a Permission. Because the {@code grantee} in the
     * trunk-{@link com.arsdigita.kernel.permissions.Permission} is instance
     * of the trunk-{@link Party}-class, there need to be separated two
     * cases:
     *      a)  were the {@code grantee} of the trunk-system is of class
     *          {@link com.arsdigita.kernel.Group} therefore listing {@code
     *          Roles}, represented by this {@code Group}, which represent
     *          the {@code grantee} of ng-{@link Permission}s.
     *      b)  were the {@code grantee} of the trunk-system is of class
     *          {@link com.arsdigita.kernel.User} therefore having no {@code
     *          Role}-representation, which has specifically to be created.
     *
     * @param trunkPermissions List of all
     *                         {@link com.arsdigita.kernel.permissions.Permission}s
     *                         from the old trunk-system
     */
    private static void setGranteeDependency(List<com.arsdigita.kernel
            .permissions.Permission> trunkPermissions) {
        for (com.arsdigita.kernel.permissions.Permission trunkPermission :
                trunkPermissions) {
            Permission permission = NgCollection.permissions.get
                    (trunkPermission.getID().longValue());

            BigDecimal trunkGranteeId = (BigDecimal) trunkPermission
                    .getPartyOID().get("id");
            List<Party> trunkParties = Party.getAllObjectParties();
            trunkParties.stream().filter(p -> Objects.equals(p.getID(),
                    trunkGranteeId)).collect(Collectors.toList());

            for (Party trunkGranteeParty : trunkParties) {
                // grantee instance of Group, possibly multiple roles
                if (trunkGranteeParty instanceof Group) {
                    RoleCollection granteeCollection = ((Group)
                            trunkGranteeParty).getRoles();
                    boolean multipleGrantees = false;
                    while (granteeCollection.next()) {
                        Role role = NgCollection.roles.get(granteeCollection
                                .getRole().getID().longValue());

                        // set grantee and opposed associations
                        if (!multipleGrantees) {
                            permission.setGrantee(role);
                            role.addPermission(permission);
                            multipleGrantees = true;
                        } else {
                            Permission duplicatePermission = new Permission
                                    (permission);
                            duplicatePermission.setGrantee(role);
                            role.addPermission(duplicatePermission);
                        }
                    }
                // grantee instance of User, new Role necessary
                } else if (trunkGranteeParty instanceof com.arsdigita.kernel
                        .User) {
                    com.arsdigita.kernel.User trunkGranteeUser = (com
                            .arsdigita.kernel.User) trunkGranteeParty;

                    // create new role for this user and its membership
                    User member = NgCollection.users.get
                            (trunkGranteeUser.getID().longValue());
                    // might cause problems cause the
                    // task assignments are missing
                    Role granteeRole = new Role(member.getName() + "_role");
                    RoleMembership roleMembership = new RoleMembership
                            (granteeRole, member);
                    member.addRoleMembership(roleMembership);
                    granteeRole.addMembership(roleMembership);

                    // set grantee and opposed association
                    permission.setGrantee(granteeRole);
                    granteeRole.addPermission(permission);
                }
            }
        }
    }
}
