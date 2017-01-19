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
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created on 6/15/16
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class GroupMembership implements Portable {

    private long membershipId;

    @JsonBackReference
    private Group group;
    @JsonBackReference
    private User member;

    public GroupMembership(final Group group, final User member) {
        this.membershipId = NgCollection.groupMemberships.size() + 1;

        this.group = group;
        this.member = member;

        NgCollection.groupMemberships.put(this.membershipId, this);
    }

    @Override
    public AbstractMarshaller<? extends Portable> getMarshaller() {
        return new GroupMembershipMarshaller();
    }

    public long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(final long membershipId) {
        this.membershipId = membershipId;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(final Group group) {
        this.group = group;
    }

    public User getMember() {
        return member;
    }

    public void setMember(final User member) {
        this.member = member;
    }
}
