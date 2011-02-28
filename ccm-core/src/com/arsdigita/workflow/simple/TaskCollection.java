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

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import org.apache.log4j.Logger;

/**
 * Represents a collection on a task.
 *
 * @author Uday Mathur
 * @author Khy Huang
 * @version 1.0
 * @version $Id: TaskCollection.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class TaskCollection extends DomainCollection {

    private static final Logger s_log = Logger.getLogger(TaskCollection.class);

    /**
     * Constructor.
     *
     * @param dataCollection the data collection
     * @see com.arsdigita.domain.DomainCollection#DomainCollection(DataCollection)
     *
     **/
    public TaskCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Internal method to get the object type information
     *
     * @return the object type
     *
     **/
    private static ObjectType getObjectType(String typeName) {
        return SessionManager.getMetadataRoot().getObjectType(typeName);
    }

    /**
     * Wrapper to <code>getDomainObject()</code> that casts the
     * returned domain object as a task.
     *
     * @return a task.
     **/
    public Task getTask() {
        return (Task) getDomainObject();
    }

    /**
     * Sets the order of this collection. Should be called
     * before <code>next()</code> is called on this collection.
     *
     **/
    public void addOrder(String order) {
        m_dataCollection.addOrder(order);
    }

    public final void childrenOf(final Task parent) {
        m_dataCollection.addEqualsFilter(Task.PARENT_TASK_ID, parent.getID());
    }
}
