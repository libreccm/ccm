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

import com.arsdigita.kernel.UserCollection;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.security.Group;
import com.arsdigita.portation.modules.core.security.GroupMembership;
import com.arsdigita.portation.modules.core.security.User;

import java.util.List;

/**
 * Class for converting all trunk-{@link com.arsdigita.kernel.Group}s into
 * ng-{@link Group}s as preparation for a successful export of all trunk
 * classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 4.7.16
 */
public class GroupConversion {

    /**
     * Retrieves all trunk-{@link com.arsdigita.kernel.Group}s from the
     * persistent storage, collects them in a list and removes all groups
     * representing actually a {@link com.arsdigita.kernel.Role} in the
     * trunk-system. Then calls for creating the equivalent ng-{@link Group}s
     * focusing on keeping all the associations in tact.
     */
    public static void convertAll() {
        System.err.printf("\tFetching groups from database...");
        List<com.arsdigita.kernel.Group> trunkGroups = com.arsdigita.kernel
                .Group.getAllObjectGroups();
        System.err.println("done.");

        System.err.printf("\tConverting groups and group memberships...\n");
        createGroupsAndSetAssociations(trunkGroups);
        System.err.println("\tdone.\n");
    }

    /**
     * Creates the equivalent ng-class of the {@code Category} and restores
     * the associations to other classes.
     *
     * @param trunkGroups List of all {@link com.arsdigita.kernel.Group}s
     *                    from this old trunk-system.
     */
    private static void createGroupsAndSetAssociations(
            List<com.arsdigita.kernel.Group> trunkGroups) {
        int pGroups = 0, pMemberships = 0;

        for (com.arsdigita.kernel.Group trunkGroup : trunkGroups) {
            // create groups
            Group group = new Group(trunkGroup);

            // groupMemberships
            UserCollection userCollection = trunkGroup.getMemberUsers();
            pMemberships += createGroupMemberships(group, userCollection);

            pGroups++;
        }
        System.err.printf("\t\tCreated %d groups and\n" +
                          "\t\tcreated %d group memberships.\n",
                          pGroups, pMemberships);
    }

    /**
     * Method for creating {@link GroupMembership}s between {@link Group}s
     * and {@link User}s which is an association-class and has not been
     * existent in this old trunk-system.
     *
     * @param group The {@link Group}
     * @param userCollection A collection of the
     *                       {@link com.arsdigita.kernel.User}s belonging to
     *                       the given group
     */
    private static long createGroupMemberships(Group group, UserCollection
            userCollection) {
        int processed = 0;

        while (userCollection.next()) {
            User member = NgCoreCollection.users.get(userCollection.getUser()
                    .getID().longValue());

            if (group != null && member != null) {
                // create groupMemeberships
                GroupMembership groupMembership = new GroupMembership(group, member);

                // set opposed associations
                group.addMembership(groupMembership);
                member.addGroupMembership(groupMembership);

                processed++;
            }

        }

        return processed;
    }

}
