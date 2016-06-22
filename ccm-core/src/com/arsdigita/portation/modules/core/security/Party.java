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

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 01.06.16
 */
public class Party implements Identifiable {

    private long partyId;
    private String name;
    private Set<RoleMembership> roleMemberships = new HashSet<>();

    public Party(final com.arsdigita.kernel.Party trunkParty) {

    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new PartyMarshaller();
    }

    public long getPartyId() {
        return partyId;
    }

    public void setPartyId(long partyId) {
        this.partyId = partyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RoleMembership> getRoleMemberships() {
        return roleMemberships;
    }

    public void setRoleMemberships(Set<RoleMembership> roleMemberships) {
        this.roleMemberships = roleMemberships;
    }
}
