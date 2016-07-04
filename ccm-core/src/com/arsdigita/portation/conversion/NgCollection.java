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
package com.arsdigita.portation.conversion;

import com.arsdigita.portation.modules.core.categorization.Categorization;
import com.arsdigita.portation.modules.core.categorization.Category;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.security.Group;
import com.arsdigita.portation.modules.core.security.GroupMembership;
import com.arsdigita.portation.modules.core.security.Party;
import com.arsdigita.portation.modules.core.security.User;
import com.arsdigita.portation.modules.core.workflow.Task;
import com.arsdigita.portation.modules.core.workflow.Workflow;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created the 6/27/16
 */
public class NgCollection {

    public static Map<Long, CcmObject> ccmObjects = new HashMap<>();
    public static Map<Long, Workflow> workflows = new HashMap<>();
    public static Map<Long, Task> tasks = new HashMap<>();
    public static Map<Long, Category> categories = new HashMap<>();
    public static Map<Long, Categorization> categorizations = new HashMap<>();
    public static Map<Long, Party> parties = new HashMap<>();
    public static Map<Long, User> users = new HashMap<>();
    public static Map<Long, Group> groups = new HashMap<>();
    public static Map<Long, GroupMembership> groupMemberships = new HashMap<>();

    private NgCollection() {}
}
