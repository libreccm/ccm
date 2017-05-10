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
package com.arsdigita.portation.modules.core.security;

import com.arsdigita.portation.conversion.NgCollection;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 01.06.16
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = PartyIdResolver.class,
                  property = "name")
public class Party {

    private long partyId;
    private String name;
    @JsonIgnore
    private Set<RoleMembership> roleMemberships;

    public Party(final com.arsdigita.kernel.Party trunkParty) {
        this.partyId = trunkParty.getID().longValue();
        this.name = trunkParty.getName().
                replace(" ", "_").
                replace(".", "_").
                replace("/", "\\").
                replace("(", "_").
                replace(")", "_");

        this.roleMemberships = new HashSet<>();

        NgCollection.parties.put(this.partyId, this);
    }

    public long getPartyId() {
        return partyId;
    }

    public void setPartyId(final long partyId) {
        this.partyId = partyId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<RoleMembership> getRoleMemberships() {
        return roleMemberships;
    }

    public void setRoleMemberships(final Set<RoleMembership> roleMemberships) {
        this.roleMemberships = roleMemberships;
    }

    public void addRoleMembership(final RoleMembership roleMembership) {
        roleMemberships.add(roleMembership);
    }

    public void removeRoleMembership(final RoleMembership roleMembership) {
        roleMemberships.remove(roleMembership);
    }
}
