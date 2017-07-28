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

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.conversion.NgCoreCollection;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
@JsonIdentityInfo(generator = RoleMembershipIdGenerator.class,
                  property = "customMemId")
public class RoleMembership implements Portable {

    private long membershipId;
    @JsonIdentityReference(alwaysAsId = true)
    private Role role;
    @JsonIdentityReference(alwaysAsId = true)
    private Party member;

    public RoleMembership(final Role role, final Party member) {
        this.membershipId = ACSObject.generateID().longValue();

        this.role = role;
        this.member = member;

        NgCoreCollection.roleMemberships.put(this.membershipId, this);
    }


    public long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(final long membershipId) {
        this.membershipId = membershipId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public Party getMember() {
        return member;
    }

    public void setMember(final Party member) {
        this.member = member;
    }
}
