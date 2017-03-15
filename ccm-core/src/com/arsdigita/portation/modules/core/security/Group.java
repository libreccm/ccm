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
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.conversion.NgCollection;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 31.05.16
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = GroupIdResolver.class,
                  property = "name")
public class Group extends Party implements Portable {

    @JsonIgnore
    private Set<GroupMembership> memberships;


    public Group(final com.arsdigita.kernel.Group trunkGroup) {
        super(trunkGroup);
        this.memberships = new HashSet<>();

        NgCollection.groups.put(this.getPartyId(), this);
    }

    @Override
    public AbstractMarshaller<? extends Portable> getMarshaller() {
        return new GroupMarshaller();
    }

    public Set<GroupMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(final Set<GroupMembership> memberships) {
        this.memberships = memberships;
    }

    public void addMembership(final GroupMembership member) {
        memberships.add(member);
    }

    public void removeMembership(final GroupMembership member) {
        memberships.remove(member);
    }
}
