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

import com.arsdigita.kernel.RoleCollection;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.security.*;
import com.arsdigita.portation.modules.core.security.util.PermissionIdMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private static int rolesCreated = 0;

    /**
     * Retrieves all trunk-{@link com.arsdigita.kernel.permissions.Permission}s
     * from the persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link Permission}s focusing on keeping all
     * the associations in tact. The association to the {@code
     * grantee}-{@link Role} has to be recreated separately.
     */
    public static void convertAll() {
        System.err.printf("\tFetching permissions from database...");
        List<com.arsdigita.kernel.permissions.Permission> trunkPermissions =
                com.arsdigita.kernel.permissions.Permission
                        .getAllObjectPermissions();
        System.err.println("done.");

        System.err.printf("\tConverting permissions...\n");
        createPermissionsAndSetAssociations(trunkPermissions);

        try {
            setGranteeDependency(trunkPermissions);
        } catch(Throwable ex) {
            System.err.println("Fatal error:");
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(-1);
        }

        System.err.println("\tdone.\n");
    }

    /**
     * Creates the equivalent ng-class of the {@code Permission} and restores
     * the associations to other classes.
     *
     * @param trunkPermissions List of all
     *                         {@link com.arsdigita.kernel.permissions.Permission}s
     *                         from the old trunk-system
     */
    private static void createPermissionsAndSetAssociations(final List<com
            .arsdigita.kernel.permissions.Permission> trunkPermissions) {
        int processed = 0, skipped = 0;

        for (com.arsdigita.kernel.permissions.Permission trunkPermission :
                trunkPermissions) {
            // Skip permissions generated by SQL install script. These are
            // system internal permissions which are not needed in the export
            // because they also exist in every other installation, including
            // LibreCCM 8 (LibreCCM NG).
            if (-204 == ((BigDecimal) trunkPermission.getPartyOID()
                    .get("id")).longValue()
                    || -300 == ((BigDecimal) trunkPermission.getPartyOID()
                    .get("id")).longValue()
                    || -200 == ((BigDecimal) trunkPermission.getPartyOID()
                    .get("id")).longValue()) {
                /*System.err.println(
                        "Skiping because it is a internal permission");*/
                skipped++;
                continue;
            }

            // create Permissions
            Permission permission = new Permission(trunkPermission);

            // set object and opposed associations
            CcmObject object = NgCoreCollection.ccmObjects.get(((BigDecimal)
                    trunkPermission.getACSObject().get("id")).longValue());
            if (object != null) {
                permission.setObject(object);
                object.addPermission(permission);
            }

            // set creationUser
            com.arsdigita.kernel.User trunkCreationUser = trunkPermission
                    .getCreationUser();
            if (trunkCreationUser != null) {
                User creationUser = NgCoreCollection.users.get(trunkCreationUser
                        .getID().longValue());

                if (creationUser != null)
                    permission.setCreationUser(creationUser);
            }

            processed++;
        }
        System.err.printf("\t\tCreated %d permissions and skipped: %d.\n",
                processed, skipped);
    }

    /**
     * Method for recreating the association to class {@link Role}, which
     * represents the {@code grantee} of a Permission. Because the {@code
     * grantee} in the
     * trunk-{@link com.arsdigita.kernel.permissions.Permission} is an instance
     * of the trunk-{@link Party}-class, there have to be two separate
     * cases:
     *      a)  were the {@code grantee} of the trunk-system is of class
     *          {@link com.arsdigita.kernel.Group} and therefore listing {@code
     *          Roles} represented by this {@code Group}, which represent
     *          the {@code grantee} of ng-{@link Permission}s.
     *      b)  were the {@code grantee} of the trunk-system is of class
     *          {@link com.arsdigita.kernel.User} and therefore having no {@code
     *          Role}-representation yet, which has specifically to be created.
     *
     * @param trunkPermissions List of all
     *                         {@link com.arsdigita.kernel.permissions.Permission}s
     *                         from the old trunk-system
     */
    private static void setGranteeDependency(final List<com.arsdigita.kernel
            .permissions.Permission> trunkPermissions) {
        int duplicates = 0;

        for (com.arsdigita.kernel.permissions.Permission trunkPermission :
                trunkPermissions) {
            // Skip permissions generated by SQL install script. These are
            // system internal permissions which are not needed in the export
            // because they also exist in every other installation, including
            // LibreCCM 8 (LibreCCM NG).
            if (-204 == ((BigDecimal) trunkPermission.getPartyOID()
                    .get("id")).longValue()
                    || -300 == ((BigDecimal) trunkPermission.getPartyOID()
                    .get("id")).longValue()
                    || -200 == ((BigDecimal) trunkPermission.getPartyOID()
                    .get("id")).longValue()) {
                continue;
            }

            final String oldId = Permission.genOldId(trunkPermission);
            final long permissionId = PermissionIdMapper.map.get(oldId);
            // get ng permission
            final Permission permission = NgCoreCollection.permissions.get(
                    permissionId);

            // get all parties serving as the grantee of this permission
            final BigDecimal trunkGranteeId = (BigDecimal) trunkPermission
                    .getPartyOID().get("id");
            final List<com.arsdigita.kernel.Party> allTrunkParties =
                    com.arsdigita.kernel.Party.getAllObjectParties();
            final List<com.arsdigita.kernel.Party> trunkGranteeParties =
                    allTrunkParties
                            .stream()
                            .filter(p->Objects.equals(p.getID(),trunkGranteeId))
                            .collect(Collectors.toList());

            for (com.arsdigita.kernel.Party trunkGranteeParty :
                    trunkGranteeParties) {
                // grantee instance of Group, possibly multiple roles or none
                if (trunkGranteeParty instanceof com.arsdigita.kernel.Group) {
                    final com.arsdigita.kernel.Group trunkGranteeGroup =
                            (com.arsdigita.kernel.Group) trunkGranteeParty;

                    final RoleCollection roleCollection = trunkGranteeGroup
                            .getRoles();
                    // if group contains 1 or more roles
                    if (!roleCollection.isEmpty()) {
                        while (roleCollection.next()) {
                            final Role grantee = NgCoreCollection.roles.get(
                                    roleCollection.getRole().getID().longValue());

                            // duplicate permission for found role as grantee
                            final Permission duplicatePermission = new Permission(
                                    permission);
                            // set grantee and opposed association
                            duplicatePermission.setGrantee(grantee);
                            grantee.addPermission(duplicatePermission);

                            final String duplicateOldId = Permission.genOldId
                                    (duplicatePermission);
                            PermissionIdMapper.map.put(duplicateOldId,
                                    duplicatePermission.getPermissionId());

                            duplicates++;
                        }
                    }
                    // new Role for this group
                    final Group member = NgCoreCollection.groups.get
                            (trunkGranteeGroup.getID().longValue());
                    final Role granteeRole = getRoleIfExists(member);

                    // set grantee and opposed association
                    permission.setGrantee(granteeRole);
                    granteeRole.addPermission(permission);

                // grantee instance of User, new Role necessary
                } else if (trunkGranteeParty instanceof com.arsdigita.kernel.User) {
                    // new Role for this user
                    final com.arsdigita.kernel.User trunkGranteeUser = (com
                            .arsdigita.kernel.User) trunkGranteeParty;
                    final User member = NgCoreCollection.users.get
                            (trunkGranteeUser.getID().longValue());
                    final Role granteeRole = getRoleIfExists(member);

                    // set grantee and opposed association
                    permission.setGrantee(granteeRole);
                    granteeRole.addPermission(permission);
                } else {
                    System.err.printf("!!!Failed to convert grantee for " +
                                    "permission %s%n", trunkPermission.getOID()
                                    .toString());
                }
            }

            if (permission.getGrantee() == null) {
                System.err.printf("PermissionConversation: No Grantee for " +
                        "permission with database id %d%n", ((BigDecimal)
                        trunkPermission.getACSObject().get("id")).longValue());
            }
        }
        System.err.printf("\t\t(Created %d duplicates and created %d new " +
                        "roles.)\n", duplicates, rolesCreated);
    }

    /**
     * Creates a new role for a given member and sets its membership.
     *
     * @param member Member of the newly created role
     *
     * @return A role for the specified member
     */
    private static Role getRoleIfExists(Party member) {
        // might cause problems cause the
        // task assignments are missing
        String roleName = member.getName() + "_role";

        List<Role> roles = new ArrayList<>(NgCoreCollection.roles.values());
        for (Role role : roles) {
            if (role.getName().equals(roleName))
                return role;
        }

        Role granteeRole = new Role(roleName);
        rolesCreated++;

        RoleMembership roleMembership = new RoleMembership(granteeRole, member);
        member.addRoleMembership(roleMembership);
        granteeRole.addMembership(roleMembership);

        return granteeRole;

    }
}
