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
package com.arsdigita.portation.categories.core.Group;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;
import com.arsdigita.portation.categories.core.Party.Party;
import com.arsdigita.portation.categories.core.Utils.CollectionConverter;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 31.05.16
 */
public class Group extends Party {

    private List<Party> members;
    private List<Group> subGroups;

    public Group(com.arsdigita.kernel.Group sysGroup) {
        super(sysGroup);

        this.members = CollectionConverter.convertParties(sysGroup.getMembers());
        this.subGroups = CollectionConverter.convertGroups(sysGroup.getSubgroups());
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new GroupMarshaller();
    }

    public List<Party> getMembers() {
        return members;
    }

    public void setMembers(List<Party> members) {
        this.members = members;
    }

    public List<Group> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<Group> subGroups) {
        this.subGroups = subGroups;
    }
}
