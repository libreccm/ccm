/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.workflow.simple;

import java.util.Iterator;

import com.arsdigita.kernel.User;
import com.arsdigita.kernel.Group;


/**
 * Interface for assignment capability to a class.
 *
 * @author Karl GoldStein 
 * @author Khy Huang      
 * @author Stefan Deusch  
 *
 **/
public interface Assignable {

    public static final String versionId = "$Id: Assignable.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Assigns a user to this task. (persistent operation)
     *
     * @param user an active user of the system
     *
     **/
    public void assignUser(User user);

    /**
     * Assigns a group of users to this task. (persistent operation)
     *
     * @param group a group of users
     *
     **/
    public void assignGroup(Group group);

    /**
     * Removes a user from the task assignment list.
     *
     * @param user the user to remove
     *
     **/
    public void removeUser(User user);

    /**
     * Removes a group from the task assignment list.
     *
     * @param group the group to remove
     *
     **/
    public void removeGroup(Group group);


    /**
     * Tests whether any user is assigned to this task.
     *
     * @return <code>true</code> if anyone is assigned
     * to this task; <code>false</code> otherwise.
     *
     **/
    public boolean isAssigned();

    /**
     * Tests whether a user is assigned to this task.
     *
     * @param user a system user
     * @return <code>true</code> if the user is assigned
     * to this task; <code>false</code> otherwise.
     *
     **/
    public boolean isAssigned(User user);

    /**
     * Tests whether a group is assigned to this task.
     *
     * @param group aA user group
     * @return <code>true</code> if the group is assigned
     * to this task; <code>false</code> otherwise.
     *
     **/
    public boolean isAssigned(Group group);

    /**
     * Returns a GroupCollection over the set of groups assigned to this task.
     *
     * @return the groups assigned to this task.
     *
     **/
    public Iterator getAssignedGroups();

    /**
     * Returns a UserCollection over the set of users assigned to this task.
     *
     * @return the users assigned to this task.
     *
     **/
    public Iterator getAssignedUsers();


}
