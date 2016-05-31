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
package com.arsdigita.portation.categories.User;

import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.portation.AbstractMarshaller;
import com.arsdigita.portation.Identifiable;
import com.arsdigita.portation.categories.Group.Group;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 31.05.16
 */
public class User implements Identifiable {
    private static final Logger logger = Logger.getLogger(User.class);

    private String trunkClass;
    private long id;
    private String name;
    private String personName;
    private String screenName;
    private String displayName;
    private List<Group> groups;
    private String primaryMailAdress;
    private List<String> mailAdresses;

    public User(com.arsdigita.kernel.User sysUser) {
        this.trunkClass = sysUser.getClass().getName();

        this.id = sysUser.getID().longValue();
        this.name = sysUser.getName();
        this.personName = sysUser.getPersonName().toString();
        this.screenName = sysUser.getScreenName();
        this.displayName =  sysUser.getDisplayName();
        this.groups = convertGroups(sysUser.getGroups());
        this.primaryMailAdress = sysUser.getPrimaryEmail().getEmailAddress();
        this.mailAdresses = convertMailAdresses(sysUser.getAlternateEmails());
    }

    private List<Group> convertGroups(com.arsdigita.kernel.GroupCollection groupCollection) {
        List<Group> groups = new ArrayList<>();
        if (groupCollection != null) {
            while (groupCollection.next()) {
                groups.add(new Group(groupCollection.getGroup()));
            }
            groupCollection.close();
        } else {
            logger.error("A Failed to export, due to empty user list.");
        }
        return groups;
    }

    private List<String> convertMailAdresses(Iterator it) {
        List<String> mailAdresses = new ArrayList<>();
        if (it != null) {
            while (it.hasNext()) {
                mailAdresses.add(((EmailAddress) it.next()).getEmailAddress());
            }
        } else {
            logger.error("A Failed to export, due to empty user list.");
        }
        return mailAdresses;
    }

    @Override
    public String getTrunkClass() {
        return trunkClass;
    }

    @Override
    public void setTrunkClass(String trunkClass) {
        this.trunkClass = trunkClass;
    }

    @Override
    public AbstractMarshaller<? extends Identifiable> getMarshaller() {
        return new UserMarshaller();
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<String> getMailAdresses() {
        return mailAdresses;
    }

    public void setMailAdresses(List<String> mailAdresses) {
        this.mailAdresses = mailAdresses;
    }
}
