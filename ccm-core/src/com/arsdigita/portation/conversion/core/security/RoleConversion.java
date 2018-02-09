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
import com.arsdigita.portation.AbstractConversion;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.arsdigita.portation.modules.core.security.Party;
import com.arsdigita.portation.modules.core.security.Role;
import com.arsdigita.portation.modules.core.security.RoleMembership;

import java.util.List;

/**
 * Class for converting all trunk-{@link com.arsdigita.kernel.Role}s into
 * ng-{@link Role}s as preparation for a successful export of all trunk
 * classes into the new ng-system.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 4.7.16
 */
public class RoleConversion extends AbstractConversion{
    private static RoleConversion instance;

    static {
        instance = new RoleConversion();
    }

    /**
     * Retrieves all trunk-{@link com.arsdigita.kernel.Role}s from the
     * persistent storage and collects them in a list. Then calls for
     * creating the equivalent ng-{@link Role}s focusing on keeping all the
     * associations in tact.
     */
    @Override
    public void convertAll() {
        System.out.print("\tFetching roles from database...");
        List<com.arsdigita.kernel.Role> trunkRoles = com.arsdigita.kernel
                .Role.getAllObjectRoles();
        System.out.println("done.");

        System.out.print("\tCreating roles and role memberships...\n");
        createRolesAndSetAssociations(trunkRoles);
        System.out.println("\tdone.\n");
    }

    /**
     * Creates the equivalent ng-class of the {@code Role} and restores
     * the associations to other classes.
     *
     * @param trunkRoles List of all {@link com.arsdigita.kernel.Role}s from
     *                   this old trunk-system.
     */
    private void createRolesAndSetAssociations(
            List<com.arsdigita.kernel.Role> trunkRoles) {
        int pRoles = 0, pMemberships = 0;

        for (com.arsdigita.kernel.Role trunkRole : trunkRoles) {
            // create roles
            Role role = new Role(trunkRole);

            // roleMemberships
            PartyCollection partyCollection = trunkRole.getContainedParties();
            pMemberships += createRoleMemberships(role, partyCollection);

            pRoles++;
        }
        System.out.printf("\t\tCreated %d roles and\n" +
                          "\t\tcreated %d role memberships.\n",
                          pRoles, pMemberships);
    }

    /**
     * Method for creating {@link RoleMembership}s between {@link Role}s
     * and {@link Party}s which is an association-class and has not been
     * existent in this old trunk-system.
     *
     * @param role The {@link Role}
     * @param partyCollection A collection of the
     *                        {@link com.arsdigita.kernel.Party}s belonging to
     *                        the given group
     */
    private long createRoleMemberships(Role role, PartyCollection
            partyCollection) {
        int processed = 0;

        while (partyCollection.next()) {
            Party member = NgCoreCollection.parties.get(partyCollection.getParty()
                    .getID().longValue());

            if (role != null && member != null) {
                // create roleMemberships
                RoleMembership roleMembership = new RoleMembership(role, member);

                // set opposed associations
                role.addMembership(roleMembership);
                member.addRoleMembership(roleMembership);

                processed++;
            }
        }

        return processed;
    }

    /**
     * Getter for the instance of the singleton.
     *
     * @return instance of this singleton
     */
    public static RoleConversion getInstance() {
        return instance;
    }
}
