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
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.security.Group;
import com.arsdigita.portation.modules.core.security.Party;
import com.arsdigita.portation.modules.core.security.Permission;
import com.arsdigita.portation.modules.core.security.Role;
import com.arsdigita.portation.modules.core.security.RoleMembership;
import com.arsdigita.portation.modules.core.security.User;
import com.arsdigita.portation.modules.core.security.util.PermissionIdMapper;

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

        System.err.println("Converting permissions...");
        createPermissionsAndSetAssociations(trunkPermissions);
        System.err.println("Setting grantee on permissions...");
        try {
            setGranteeDependency(trunkPermissions);
        } catch(Throwable ex) {
            System.err.println("Fatal error:");
            System.err.println(ex.getMessage());
            ex.printStackTrace(System.err);
            System.exit(-1);
        }
    }

    /**
     * Creates the equivalent ng-class of the {@code Permission} and restores
     * the associations to other classes.
     *
     * @param trunkPermissions List of all
     *                         {@link com.arsdigita.kernel.permissions.Permission}s
     *                         from the old trunk-system
     */
    private static void createPermissionsAndSetAssociations(List<com.arsdigita.kernel.permissions.Permission> trunkPermissions) {

        long processed = 0;

        for (com.arsdigita.kernel.permissions.Permission trunkPermission :
                trunkPermissions) {

            //Skip permissions generated by SQL install script. These are system internal permissions which are not
            // needed in the export because they also exist in every other installation,
            // including LibreCCM 8 (LibreCCM NG).
            if (-204 == ((BigDecimal) trunkPermission.getPartyOID().get("id")).longValue()
                    || -300 == ((BigDecimal) trunkPermission.getPartyOID().get("id")).longValue()
                    || -200 == ((BigDecimal) trunkPermission.getPartyOID().get("id")).longValue()) {
                System.err.println("Skiping because it is a internal permission");
                continue;
            }

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

            processed++;
        }

        System.err.printf("Created %d permissions%n", processed);
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
    private static void setGranteeDependency(final List<com.arsdigita.kernel.permissions.Permission> trunkPermissions) {

        long processed = 0;

        for (com.arsdigita.kernel.permissions.Permission trunkPermission : trunkPermissions) {

            // Skip permissions generated by SQL install script. These are system internal permissions which are not
            // needed in the export because they also exist in every other installation,
            // including LibreCCM 8 (LibreCCM NG).
            if (-204 == ((BigDecimal) trunkPermission.getPartyOID().get("id")).longValue()
                    || -300 == ((BigDecimal) trunkPermission.getPartyOID().get("id")).longValue()
                    || -200 == ((BigDecimal) trunkPermission.getPartyOID().get("id")).longValue()) {
                System.err.println("Skiping because it is a internal permission");
                continue;
            }

            final String oldId = Permission.genOldId(trunkPermission);

            final long permissionId = PermissionIdMapper.map.get(oldId);
            final Permission permission = NgCollection.permissions.get(permissionId);

            // get all parties serving as the grantee of this permission
            final BigDecimal trunkGranteeId = (BigDecimal) trunkPermission
                    .getPartyOID()
                    .get("id");
            System.err.printf("Trying to set grantee for permission with oldId = \"%s\" | objectId = %d | granteeId = %d | privilege = \"%s\" -> %d%n",
                    oldId,
                    ((BigDecimal) trunkPermission.getACSObject().get("id")).longValue(),
                    trunkGranteeId.longValue(),
                    trunkPermission.getPrivilege().getName(),
                    permissionId);
        
            final List<com.arsdigita.kernel.Party> availableParties = com.arsdigita.kernel.Party.getAllObjectParties();
            System.err.println("Got available parties...");
            System.err.printf("Available parties:%d%n", availableParties.size());
            for(com.arsdigita.kernel.Party party : availableParties) {
                System.err.printf("\t%s%n", Objects.toString(party.getID()));
            }
            final List<com.arsdigita.kernel.Party> trunkParties = availableParties
                .stream()                
                .filter(p -> {
                    /*System.err.printf("p = %s; trunkGranteeId = %s%n",
                                Objects.toString(p.getID(),
                                Objects.toString(trunkGranteeId)));*/
                                return Objects.equals(p.getID(), trunkGranteeId);
                            })
                .collect(Collectors.toList());

            System.err.printf("Found %d parties associated with the permission%n",
                              trunkParties.size());

           for (com.arsdigita.kernel.Party trunkGranteeParty : trunkParties) {
            System.err.printf("Trying to set grantee for permission %d to party %s%n",
                              permission.getPermissionId(),
                              trunkGranteeParty.getName());

 
                // grantee instance of Group, possibly multiple roles or none
                if (trunkGranteeParty instanceof com.arsdigita.kernel.Group) {
                    final com.arsdigita.kernel.Group trunkGranteeGroup =
                            (com.arsdigita.kernel.Group) trunkGranteeParty;

                    final RoleCollection roleCollection = trunkGranteeGroup.getRoles();
                    // if group contains 1 or more roles
                    if (!roleCollection.isEmpty()) {
                        while (roleCollection.next()) {
                            final Role grantee = NgCollection.roles.get(roleCollection
                                    .getRole().getID().longValue());

                            final Permission duplicatePermission = new Permission
                                    (permission);
                            duplicatePermission.setGrantee(grantee);
                            grantee.addPermission(duplicatePermission);

                            final CcmObject object = duplicatePermission.getObject();
                            long objectId = 0;
                            if (object != null) {
                                objectId = object.getObjectId();
                            }

                            //final String duplicateOldId = Long.toString(objectId) + "_"  +  Long.toString(grantee.getRoleId() + "_" + permission.getGrantedPrivilege());
                            final String duplicateOldId = String.format("%d_%d_%s",
                                    objectId,
                                    grantee.getRoleId(),
                                    permission.getGrantedPrivilege());
                            PermissionIdMapper.map.put(duplicateOldId,
                                    duplicatePermission.getPermissionId());
                        }
                    }
                    // new Role for this group
                    final Group member = NgCollection.groups.get
                            (trunkGranteeGroup.getID().longValue());

                    final Role granteeRole = createNewRole(member);

                    // set grantee and opposed association
                    permission.setGrantee(granteeRole);
                    granteeRole.addPermission(permission);


                // grantee instance of User, new Role necessary
                } else if (trunkGranteeParty instanceof com.arsdigita.kernel.User) {
                    final com.arsdigita.kernel.User trunkGranteeUser = (com
                            .arsdigita.kernel.User) trunkGranteeParty;

                    final User member = NgCollection.users.get
                            (trunkGranteeUser.getID().longValue());

                    final Role granteeRole = createNewRole(member);

                    // set grantee and opposed association
                    permission.setGrantee(granteeRole);
                    granteeRole.addPermission(permission);
                } else {
                    System.err.printf("!!!Failed to convert grantee for permission %s%n",
                            trunkPermission.getOID().toString());
                }
            }

            if(permission.getGrantee() == null) {
                System.err.printf("PermissionConversation: No Grantee for permission with database id %d%n",  ((BigDecimal) trunkPermission.getACSObject().get("id")).
                        longValue());
                //System.exit(-1);
            } else {
                System.out.printf("Set grantee for permission %d%n%n", permission.getPermissionId());
            }

            processed++;
        }

        System.err.printf("Set grantee for %d permissions.%n", processed);
    }

    /**
     * Creates a new role for a given member and sets its membership.
     *
     * @param member Member of the newly created role
     *
     * @return A role for the specified member
     */
    private static Role createNewRole(Party member) {
        // might cause problems cause the
        // task assignments are missing
        Role granteeRole = new Role(member.getName() + "_role");

        RoleMembership roleMembership = new RoleMembership(granteeRole, member);
        member.addRoleMembership(roleMembership);
        granteeRole.addMembership(roleMembership);

        return granteeRole;
    }
}
