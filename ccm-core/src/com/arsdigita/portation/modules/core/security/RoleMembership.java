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
import com.arsdigita.portation.conversion.NgCollection;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 6/15/16
 */
public class RoleMembership implements Identifiable {

    private long membershipId;

    private Role role;
    private Party member;

    public RoleMembership(final Role role, final Party member) {
        this.membershipId = NgCollection.roleMemberships.size() + 1;

        this.role = role;
        this.member = member;

        NgCollection.roleMemberships.put(this.membershipId, this);
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new RoleMembershipMarshaller();
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
