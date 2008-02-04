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


import java.math.BigDecimal;
import java.util.Date;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.ObservableDomainObject;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.domain.DomainObjectFactory;

// Support for Logging.
import org.apache.log4j.Logger;

/**
 * A comment on a task.
 * Must be loaded in full because we don't handle ID.
 *
 * @author Stefan Deusch 
 * @author Khy Huang 
 */
public class TaskComment extends ObservableDomainObject {
    public static final String versionId = "$Id: TaskComment.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_cat =
        Logger.getLogger(TaskComment.class.getName());

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.workflow.simple.TaskComment";


    private static final String COMMENT = "taskComment";
    private static final String DATE    = "commentDate";
    private static final String USER_ID = "partyID";
    private static final String TASK = "task";
    private static final String COMMENT_ID = "id";


    /**
     * Constructor for a task comment.
     *
     * @param taskID the task ID
     * @param user   the user
     * @param comment the comment
     *
     */
    public TaskComment(BigDecimal taskID, User user, String comment) {
        this(taskID, user, comment, new Date());
    }

    /**
     * Constructor for a task comment.
     *
     * @param commentID the comment ID
     * @param taskID the task ID
     * @param user   the user
     * @param comment the comment
     *
     */
    public TaskComment(BigDecimal commentID, BigDecimal taskID,
                       User user, String comment) {
        this(commentID, taskID, user, comment, new Date());
    }

    /**
     * Constructor for a task comment with a date setting.
     *
     * @param taskID the task ID
     * @param user   the user
     * @param comment the comment
     * @param date   the date
     *
     */
    public TaskComment(BigDecimal taskID, User user, String comment,
                       Date date) {
        this(null, taskID, user, comment,date);
    }

    /**
     * Constructor for a task comment.
     *
     * @param commentID the comment ID
     * @param taskID the task ID
     * @param user   the user
     * @param comment the comment
     * @param date the Date of the comment
     *
     */
    public TaskComment(BigDecimal commentID, BigDecimal taskID, User user,
                       String comment, Date date) {
        this(BASE_DATA_OBJECT_TYPE);
        if (commentID == null) {
            set(COMMENT_ID, generateID());
        } else {
            set(COMMENT_ID, commentID);
        }
        set(COMMENT, comment);
        if (user != null) {
            set(USER_ID,  user.getID());
        }

        setTask((Task) DomainObjectFactory.newInstance
                (new OID(Task.BASE_DATA_OBJECT_TYPE, taskID)));
        set(DATE, date);
    }

    private BigDecimal generateID() throws PersistenceException {
        try {
            return Sequences.getNextValue();
        } catch (java.sql.SQLException e) {
            final String errorMsg = "Unable to generate a unique " +
                "TaskComment id.";
            s_cat.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }

    /**
     * Constructor for a task comment without a specific user.
     *
     * @param taskID  the task ID
     * @param comment the comment
     *
     **/
    public TaskComment(BigDecimal taskID, String comment) {
        this(taskID, null, comment, new Date());
    }

    /**
     * Constructor to restore a task comment by data object.
     *
     * @param commentDataObject the data object
     *
     **/
    public TaskComment(DataObject commentDataObject) {
        super(commentDataObject);
    }

    /**
     * Constructor for setting the object type.
     *
     * @param type the object type
     *
     **/
    protected TaskComment(ObjectType type) {
        super(type);
    }

    /**
     *
     * Constructor for setting the object type name.
     *
     * @param the type name string
     *
     **/
    public TaskComment(String typeName) {
        super(typeName);
    }

    /**
     * Restores the task ID with the OID.
     *
     * @param oid the OID
     * @see com.arsdigita.persistence.OID
     **/
    public TaskComment(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Gets the base data object type.
     *
     **/
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Retrieves the comment string.
     * @return the comment string.
     *
     **/
    public String getComment() {
        return (String)get(COMMENT);
    }


    /**
     * Retrieves the creation date of the comment.
     * @return the creation date of the comment.
     *
     **/
    public Date getDate() {
        return (Date)get(DATE);
    }

    /**
     * Retrieves the user OID of the user who
     * created the comment.
     * @return the  user OID of the user who
     * created the comment.
     *
     * @see com.arsdigita.persistence.OID
     **/
    public OID getUserOID() {
        BigDecimal user_id = (BigDecimal)get(USER_ID);
        if (user_id == null) {
            return null;
        }
        return new OID(User.BASE_DATA_OBJECT_TYPE,user_id);
    }

    /**
     * Get the user who added this comment.
     *
     * @return a User, or null if no User is associated with this comment
     * @throws UncheckedWrapperException if the OID does not reference a valid User
     **/
    public User getUser() {
        OID oid = getUserOID();
        if (oid != null) {
            try {
                return (User) DomainObjectFactory.
                    newInstance(oid);
            } catch (DataObjectNotFoundException de) {
                throw new UncheckedWrapperException("Could not load User with OID: " + oid,
                                                    de);
            }
        } else {
            return null;
        }
    }

    /**
     * Retrieves the task ID of the
     * task owning this comment.
     *
     * @return the task ID.
     *
     **/
    public BigDecimal getTaskID() {
        Task t = getTask();
        if (t == null) {
            return null;
        } else {
            return getTask().getID();
        }
    }

    void setTask(Task task) {
        setAssociation(TASK, task);
    }

    public Task getTask() {
        return (Task) DomainObjectFactory.newInstance((DataObject) get(TASK));
    }

}
