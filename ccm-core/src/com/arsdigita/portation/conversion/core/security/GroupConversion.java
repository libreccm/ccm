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
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.security.Group;
import com.arsdigita.portation.modules.core.security.GroupMembership;
import com.arsdigita.portation.modules.core.security.User;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 7/4/16
 */
public class GroupConversion {

    public static void convertAll() {
        List<com.arsdigita.kernel.Group> trunkGroups = com.arsdigita.kernel
                .Group.getAllObjectGroups();

        trunkGroups.forEach(Group::new);

        setAssociations(trunkGroups);
    }

    private static void setAssociations(
            List<com.arsdigita.kernel.Group> trunkGroups) {
        Group group;

        for (com.arsdigita.kernel.Group trunkGroup : trunkGroups) {
            group = NgCollection.groups.get(trunkGroup.getID().longValue());

            // create groupMemberships
            UserCollection userCollection = trunkGroup.getMemberUsers();
            createGroupMemberships(group, userCollection);
        }
    }

    private static void createGroupMemberships(Group group, UserCollection
            userCollection) {
        while (userCollection.next()) {
            User member = NgCollection.users.get(userCollection.getUser()
                    .getID().longValue());

            // create groupMemeberships
            GroupMembership groupMembership = new GroupMembership(group, member);

            // set adverse associations
            group.addMembership(groupMembership);
            member.addGroupMembership(groupMembership);
        }
    }

}
