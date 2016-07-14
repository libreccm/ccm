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


import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.portation.conversion.NgCollection;
import com.arsdigita.portation.modules.core.security.Party;
import com.arsdigita.portation.modules.core.security.Role;
import com.arsdigita.portation.modules.core.security.RoleMembership;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 7/4/16
 */
public class RoleConversion {

    public static void convertAll() {
        List<com.arsdigita.kernel.Role> trunkRoles = com.arsdigita.kernel
                .Role.getAllObjectRoles();

        // create roles
        trunkRoles.forEach(Role::new);

        // set associations
        setAssociations(trunkRoles);
    }

    private static void setAssociations(List<com.arsdigita.kernel.Role>
                                                trunkRoles) {
        for (com.arsdigita.kernel.Role trunkRole : trunkRoles) {
            Role role = NgCollection.roles.get(trunkRole.getID().longValue());

            // create roleMemberships
            PartyCollection partyCollection = trunkRole.getContainedParties();
            createRoleMemberships(role, partyCollection);
        }
    }

    private static void createRoleMemberships(Role role, PartyCollection
            partyCollection) {
        while (partyCollection.next()) {
            Party member = NgCollection.parties.get(partyCollection.getParty()
                    .getID().longValue());

            if (role != null && member != null) {
                // create roleMemberships
                RoleMembership roleMembership = new RoleMembership(role, member);

                // set adverse associations
                role.addMembership(roleMembership);
                member.addRoleMembership(roleMembership);
            }
        }
    }
}
