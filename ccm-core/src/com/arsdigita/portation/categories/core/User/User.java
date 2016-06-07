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
package com.arsdigita.portation.categories.core.User;

import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;
import com.arsdigita.portation.categories.core.Group.Group;
import com.arsdigita.portation.categories.core.Party.Party;
import com.arsdigita.portation.categories.core.Utils.CollectionConverter;

import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 31.05.16
 */
public class User extends Party implements Identifiable {

    private String personName;
    private String screenName;
    private List<Group> groups;

    public User(com.arsdigita.kernel.User sysUser) {
        super(sysUser);

        this.personName = sysUser.getPersonName().toString(); //family and given name split
        this.screenName = sysUser.getScreenName();
        this.groups = CollectionConverter.convertGroups(sysUser.getGroups());
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new UserMarshaller();
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
