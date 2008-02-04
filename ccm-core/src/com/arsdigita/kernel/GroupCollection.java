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
package com.arsdigita.kernel;

import com.arsdigita.persistence.DataCollection;

/**
 * Represents a collection of groups.
 *
 * @author Phong Nguyen
 * @version 1.0
 **/
public class GroupCollection extends PartyCollection {
    public static final String versionId = 
        "$Id: GroupCollection.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    /**
     * Constructor.
     *
     * @see PartyCollection#PartyCollection(DataCollection)
     **/
    public GroupCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Returns the name of this group.
     *
     * @return the name of this group.
     **/
    public String getName() {
        return (String) m_dataCollection.get("name");
    }

    /**
     * Wrapper to <code>getDomainObject()</code> that casts the returned
     * <code>DomainObject</code> as a <code>Group</code>.
     *
     * @return a <code>Group</code> for the current position in the
     * collection.
     *
     * @see #getDomainObject()
     * @see Group
     **/
    public Group getGroup() {
        return (Group) getDomainObject();
    }
}
