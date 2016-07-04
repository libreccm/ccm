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

import com.arsdigita.auditing.AuditedACSObject;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents the properties of a Task.
 * Some programming guidelines to follow:
 * <UL>
 * <LI>A task must be saved before adding it as a dependency to other tasks.
 * <LI>Methods that trigger task "state" changes (from disable to enable)
 *    result in save() being called internally.
 * <LI> In general, work with one task at a time due to reduce the possibility
 *    of working with outdated tasks in memory.<BR><BR>  For example,
 *    assume that task A is dependent on task B.  The developer loads
 *    task A and task B in memory to modify some properties and
 *    calls finish on task B.  Task B will update its state and
 *    notify task A that it is is finished, which results in task A changing from disabled
 *    to enabled. Task A in memory may now be outdated, depending on
 *    whether the reference to task A in task B is to the one in memory.
 *    If task A and task B were loaded into memory separately, then the reference
 *    to task A is outdated.
 * </UL>
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Khy Huang
 * @author Stefan Deusch
 * @version $Id: Task.java 1278 2006-07-27 09:09:51Z cgyg9330 $
 **/
public class Task extends AuditedACSObject implements Cloneable {

    private static final Logger s_log = Logger.getLogger(Task.class);

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.workflow.simple.Task";

    public static final String LABEL = "label";
    public static final String DESCRIPTION = "description";
    public static final String IS_ACTIVE = "isActive";
    public static final String DEPENDENCY_LIST = "dependsOn";
    public static final String PARENT_TASK_ID = "parentTaskID";
    public static final String COMMENTS = "comments";
    public static final String TASK_STATE = "taskState";
    public static final String FINISHED_LISTENERS = "taskFinishedListeners";

    static final String DEFAULT_DESCRIPTION = "none";
    static final String DEFAULT_LABEL = "none";

    public final static int DISABLED = 0;
    public final static int ENABLED  = 1;
    public final static int FINISHED = 2;
    public final static int DELETED  = 3;
    public final static int INACTIVE = 4;

    //-------------------- Constructors Section -------------------------------
    /**
     * Creates a new task.  Properties of this object are not
     * made persistent until the <code>save()</code> method is called.
     *
     * @param label the task  label
     * @param description the task  description
     *
     **/
    public Task(String label, String description) {
        this(BASE_DATA_OBJECT_TYPE);
        initAttributes(label,description);
    }

    /**
     * Restores a task  from a data object.
     *
     * @param taskDataObject the data object
     *
     **/
    public Task(DataObject taskDataObject) {
        super(taskDataObject);
    }

    /**
     * Creates a new task.  Properties of this object are not made
     * persistent until the <code>save()</code> method is called.
     * The properties <code>label</code> and <code>description</code> are
     * set to null. If save() is called without setting these
     * properties, an IllegalArgumentException will be thrown.
     *
     **/
    public Task() {
        this(BASE_DATA_OBJECT_TYPE);
        setState(DISABLED);
    }

    /**
     * Creates a new task for a given OID.
     *
     * @param oid the Object ID
     *
     **/
    public Task(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Creates a new task for a given ID.
     *
     * @param oid the object ID
     *
     **/
    public Task(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates a new task given the object type.
     *
     * @param type the object type
     *
     **/
    protected Task(ObjectType type) {
        super(type);
    }

    /**
     * Creates a new task given the object type name.
     *
     * @param typeName the name of the type
     *
     **/
    protected Task(String typeName) {
        super(typeName);
    }

    //-------------------- Initialization Section -----------------------------
    /**
     * Initializes a task.
     *
     **/
    @Override
    protected void initialize() {
        super.initialize();
        if (isNew()) {
            setLabel(DEFAULT_LABEL);
            setDescription(DEFAULT_DESCRIPTION);
            setState(DISABLED);
            // set directly to avoid calling get() while the value is null
            set(IS_ACTIVE, Boolean.FALSE);
        }
    }


    /**
     * Sets the label and description for this task.
     *
     * @param label the task label
     * @param description the task description
     *
     **/
    protected final void initAttributes(String label, String description) {
        setLabel(label);
        setDescription(description);
    }


    //-------------------- Domain Object Attributes Section -------------------

    /**
     * Retrieves the type of the base data object.
     *
     * @return the basic data object type.
     *
     **/
    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Retrieves the objectType for typeName
     *
     * @param the typeName
     *
     **/
    private static ObjectType getObjectType(String typeName) {
        return SessionManager.getMetadataRoot().getObjectType(typeName);
    }

    /**
     * Sets the label for this task.
     *
     * @param label the new label for this task
     **/
    public void setLabel(String label) {
        set(LABEL, label);
    }

    /**
     * Gets the label for this task.
     *
     * @return the task label.
     **/
    public String getLabel() {
        return (String)get(LABEL);
    }

    /**
     * Gets the state in a string for the task.
     *
     * @return the state string.
     **/
    public String getStateString() {
        return (String) get(TASK_STATE);
    }

    /**
     * Sets the task description.
     *
     * @param description the task  description
     *
     **/
    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    /**
     * Gets the task description.
     *
     * @return the task description.
     *
     **/
    public String getDescription() {
        return (String)get(DESCRIPTION);
    }

    /**
     * Marks this task as active, which indicates that it
     * is fully configured and ready to become part of an active
     * process. (Until then, the task is still under editing.)
     * Calls the <code>updateState</code> method for finished listeners
     * to let them know that is either part of the model or not.
     * (persistent operation)
     *
     * @param isActive <code>true</code> to active this task as part
     * of an active task.
     *
     **/
    public void setActive(boolean isActive) {
        boolean currentIsActive = isActive();

        if (currentIsActive != isActive) {
            set(IS_ACTIVE, isActive ? Boolean.TRUE : Boolean.FALSE);
            if ( s_log.isDebugEnabled() ) {
                s_log.debug("Setting task " + getID() + " to " + isActive);
            }
            // if we call save on a new, we lose the ability to set
            // the id and do double click protection.
            if (!isNew()) {
                save();
            }
        }

        triggerListenerUpdateState();
    }

    /**
     * Checks whether the task part of the active process.
     *
     * @return  <code>true</code> if the task part of the active
     * process; <code>false</code> otherwise.
     *
     **/
    public boolean isActive() {
        return ((Boolean) get(IS_ACTIVE)).booleanValue();
    }

    /**
     * The ID of the process that this task is in.
     *
     * @return the process ID.
     *
     **/
    public BigDecimal getParentID() {
        return (BigDecimal)get(PARENT_TASK_ID);
    }

    /**
     * Get the Workflow that this Task is in
     **/
    public Workflow getWorkflow() {
        BigDecimal workflowID = getParentID();
        if (workflowID != null) {
            try {
                return (Workflow) DomainObjectFactory.
                    newInstance(new OID(Workflow.BASE_DATA_OBJECT_TYPE,
                                        workflowID));
            } catch (DataObjectNotFoundException de) {
                throw new UncheckedWrapperException("Could not load Workflow with ID " +
                                                    workflowID, de);
            }
        } else {
            return null;
        }
    }


    /**
     * Sets the parent task ID. This method is called from
     * Workflow.addTask(), which calls this method
     * in the <code>addTask</code> method.
     * (Persistent after the save method is called).
     *
     * @param parent the parent task (workflow)
     *
     **/
    void setParent(Workflow parent) {
        setParentID(parent.getID());
    }

    /**
     * Method to set the parent of this method. This package scoped
     * method should be called from Workflow.addTask().
     *
     * @param parent ID
     *
     **/
    void setParentID(BigDecimal id) {
        set(PARENT_TASK_ID,id);
    }

    /**
     * A wrapper to get the generic data association dependency list
     *
     * @return the dependency data association
     *
     **/
    private DataAssociation getDependsOnAssociation() {
        return (DataAssociation)get(DEPENDENCY_LIST);
    }

    //-------------------- Dependency Collection  Section ---------------------
    /**
     * Retrieves the task dependencies.
     *
     * @return an iterator of tasks corresponding to the dependencies
     * of this task.
     **/
    public Iterator getDependencies() {
        return getDependenciesInternal().iterator();
    }

    public final TaskCollection getRequiredTasks() {
        return new TaskCollection(getDependsOnAssociation());
    }

    /**
     *  This method retrieves the dependencies for this task
     *  Internal: removes tasks from the association list
     *
     * @return the task dependencies
     *
     **/
    private Collection getDependenciesInternal() {
        Collection dependencies = new HashSet();
        TaskCollection dependsOn =
            new TaskCollection(getDependsOnAssociation().cursor());

        while (dependsOn.next()) {
            if (dependsOn.getTask()!=null) {
                dependencies.add(dependsOn.getTask());
            }
        }

        dependsOn.close();

        return dependencies;

    }

    /**
     * Adds a dependency to this task.  Dependencies must be completed
     * before this task becomes enabled.
     *
     * @param task Another task that this task depends on
     * @return <code>true</code> if the task was added successfully;
     * <code>false</code> otherwise.
     *
     **/
    public boolean addDependency(Task task) {
        if (getDependenciesInternal().contains(task)) {
            return false;
        }

        HashSet alreadyVisited = new HashSet();
        alreadyVisited.add(task);
        alreadyVisited.add(this);
        if (hasLoop(task, alreadyVisited)) {
            return false;

        }

        add(DEPENDENCY_LIST, task);
        notifyAddDependency(task);
        save();

        updateState();
        return true;
    }

    /**
     * Removes a dependency from this task. (persistent operation)
     * If the state changed, then a call to save <code>save</code> method is made.
     *
     * @param task
     **/
    public void removeDependency(Task task) {
        remove(DEPENDENCY_LIST, task);
        if ( s_log.isDebugEnabled() ) {
            s_log.debug("Removed dependency " + task.getID() + " for " + getID());
        }
        notifyRemoveDependency(task);
        this.updateState();
    }

    /**
     * Checks whether this task depends directly on another task.
     *
     * @param task the task to check
     * @return <code>true</code> if this task depends on
     * a passed in task; <code>false</code> otherwise.
     *
     **/
    public boolean isDependency(Task task) {
        return getDependenciesInternal().contains(task);
    }

    /**
     * Notification that a dependency was removed.
     *
     * @param task the task
     *
     **/
    private void notifyRemoveDependency(Task task) {
        task.removeFinishedListener(this);
    }


    /**
     * Notify that dependency has been added
     *
     * @param task
     *
     **/
    private void notifyAddDependency(Task task) {
        if ( s_log.isDebugEnabled() ) {
            s_log.debug("Adding finish listener for " + task.getID() +
                        " for task " + getID());
        }

        task.addFinishedListener(this);
        if (this.isNew()) {
            this.save();
        }
        task.save();
    }

    /**
     * Removes all dependencies from this task. (persistent operation)
     *
     **/
    public void removeAllDependencies() {
        Iterator iter = getDependencies();
        Task task;
        while (iter.hasNext()) {
            task = (Task)iter.next();
            remove(DEPENDENCY_LIST, task);
            notifyRemoveDependency(task);
        }
    }

    /**
     * Removes all dependencies from this task. (persistent operation)
     *
     **/
    public void removeAllFinishedListeners() {
        Iterator iter = getFinishedListeners();
        Task task;
        while (iter.hasNext()) {
            task = (Task)iter.next();
            remove(FINISHED_LISTENERS,task);
        }
    }

    /**
     * Retrieves the number of dependencies.
     *
     * @return the number of dependencies
     *
     **/
    public int getDependencyCount() {
        return (new Long(getDependsOnAssociation().cursor().size())).intValue();
    }


    /**
     * Traverse each dependency path for the passed in task and
     * checks if any task  exist more than once. To check for loops
     * before adding a dependency:
     * <code> HashSet alreadyVisited = new HashSet();
     * alreadyVisited.add(currTask);
     *                      alreadyVisited.add(wantToAddDependency);
     * if (hasLoop(currTask, alreadyVisited())
     *                      ... </code>
     *
     * @param currTask - the task  to check
     * @param alreadyVisited - the path traversed so far prior to vising Task
     *                          (this should contain the
     *                         currTask)
     *
     * @return whether a task  was visited more than once along a
     *         path
     *
     **/
    private boolean hasLoop(Task currTask, HashSet alreadyVisited) {


        //Depth first search algorithm

        Iterator dep_iter = currTask.getDependencies();
        Task task;

        while (dep_iter.hasNext()) {
            task = (Task)dep_iter.next();

            // Check whether the Task is already in the path
            if (alreadyVisited.contains(task)) {
                return true;
            }
            alreadyVisited.add(task);
            if (hasLoop(task, alreadyVisited)) {
                return true;
            }
            alreadyVisited.remove(task);
        }
        return false;
    }

    /**
     * An object is equal to this if the object is of type
     * <code>task</code> and the IDs are the same.
     *
     * @param object the object
     *
     **/
    public boolean equals(Object object) {
        if (object instanceof Task) {
            return ((Task)object).getID().equals(getID());
        }
        return false;
    }


    /**
     * Clones a task. Copies dependencies.
     *
     **/
    public Object clone() throws CloneNotSupportedException {

        Task taskClone = new Task();
        copyAttributes(taskClone);
        return taskClone;
    }

    /**
     * Exports the attributes of this domain object.
     *
     * @param task the domain object to which this method copies the
     * attributes of this object
     * */
    protected void copyAttributes(Task task) {
        // Iterator i = getDependencies();
        // Task listener;
        /*
          while (i.hasNext()) {
          task.addDependency((Task)i.next());
          }
          i = getFinishedListeners();
          while (i.hasNext()) {
          listener = (Task) i.next();
          if (!listener.isDependency(task)) {
          task.addFinishedListener(listener);
          }
          }
        */
        task.setLabel(getLabel());
        task.setDescription(getDescription());
        task.setActive(isActive());
    }

    /**
     * Returns a string describing the task.
     *
     **/
    public String toString() {
        return getOID().toString();
    }

    //-------------------- Task Comment Section -------------------------------

    /**
     * Adds a comment (persistent after save).
     *
     * @param c the comment to add
     *
     */
    public void addComment(TaskComment c) {
        // if the comment has already been added then we just return
        if (!getCommentsInternal().contains(c)) {
            c.setTask(this);
            add(COMMENTS,c);
        }
    }

    /**
     * Adds a comment specifying the user (persistent after save).
     *
     * @param user the user
     * @param comment the comment
     *
     **/
    public void addComment(User user, String comment) {
        addComment(new TaskComment(getID(), user,comment));
    }

    /**
     * Adds a comment specifying the description string (persistent after save).
     *
     * @param comment the comment
     *
     **/
    public void addComment(String comment) {
        addComment(null, comment);

    }

    /**
     * Removes comment (persistent after save).
     *
     * @param taskComment the comment
     *
     **/
    public void removeComment(TaskComment taskComment) {
        remove(COMMENTS, taskComment);
    }

    /**
     * Returns an iterator over a set of task comments.
     *
     * @return the comments for this task.
     *
     **/
    public Iterator getComments() {
        return getCommentsInternal().iterator();
    }


    /**
     * Gets the number of comments.
     *
     * @return the number comments for this task.
     *
     **/
    public int getCommentsSize() {
        return getCommentsInternal().size();
    }

    /**
     * Get the collection of comments.
     * Internal: retrieve the task comemnts associations
     *
     **/
    private Collection getCommentsInternal() {
        Collection comments = new ArrayList();
        DataAssociationCursor commentDataAssociation =
            getCommentAssociation().cursor();

        while (commentDataAssociation.next()) {
            comments.add(new TaskComment(commentDataAssociation.getDataObject()));
        }

        commentDataAssociation.close();

        return comments;
    }

    /**
     * Returns the comment data collection
     *
     * @return the comment data collection
     *
     **/
    private DataAssociation getCommentAssociation() {
        return (DataAssociation)get(COMMENTS);
    }

    /**
     * Get the last comment that was added to this Task
     **/
    protected TaskComment getLastComment() {
        DataAssociationCursor comments = getCommentAssociation().cursor();
        comments.addOrder("commentDate desc");
        try {
            if (comments.next()) {
                return (TaskComment) DomainObjectFactory.
                    newInstance(comments.getDataObject());
            } else {
                return null;
            }
        } finally {
            comments.close();
        }
    }

    /**
     * Get the last comment that was added to any task in this Workflow
     **/
    protected TaskComment getLastCommentInWorkflow() {
        DataQuery query = SessionManager.getSession().
            retrieveQuery("com.arsdigita.workflow.simple.getCommentsInWorkflow");
        query.addOrder("comment.commentDate desc");
        query.setParameter("taskID", getID());
        try {
            if (query.next()) {
                return (TaskComment) DomainObjectFactory.
                    newInstance((DataObject) query.get("comment"));
            } else {
                return null;
            }
        } finally {
            query.close();
        }
    }

    //-------------------- Listener  Section ---------------------------------


    /**
     * Returns the list of finished listeners.
     *
     * @return an iterator of listening tasks.
     *
     */
    public Iterator getFinishedListeners() {
        return getFinishedListenersInternal().iterator();
    }


    /**
     * Retrieves the finished listeners.
     * Internal: Get the finished Listeners data association
     *
     * @return the set of finished listeners.
     *
     **/
    protected Collection getFinishedListenersInternal() {
        Collection listeners = new HashSet();
        TaskCollection tasksCollection = new TaskCollection
            ((DataAssociation) get(FINISHED_LISTENERS));

        while ( tasksCollection.next() ) {
            listeners.add(tasksCollection.getTask());
        }

        tasksCollection.close();

        return listeners;
    }


    /**
     * Adds a task as a listener to this task.  This task is notified
     * in the finish method. (persistent operation)
     *
     * @param task a listener task in the process
     *
     **/
    public void addFinishedListener(Task task) {
        add(FINISHED_LISTENERS,task);
    }

    /**
     * Removes a task from the list of listeners.
     * (persistent after save)
     *
     * @param task the task to remove
     *
     **/
    public void removeFinishedListener(Task task) {
        remove(FINISHED_LISTENERS,task);
    }

    /**
     * Returns the number of finished listeners
     *
     * @return the number of finished listeners
     *
     **/
    public int getFinishedListenersCount() {
        DataAssociation da = (DataAssociation) get(FINISHED_LISTENERS);
        return (new Long(da.cursor().size())).intValue();
    }

    //-------------------- Task State Section ---------------------------------
    /**
     * Enables a task and calls enableEvt, which is overwritten
     * by subclasses to extend functionality. (persistent operation)
     *
     **/
    public void enable()  {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Enabling task '" + getLabel() + "'");
        }

        checkEnableProcess();

        if (getState() == DISABLED) {
            s_log.debug("The task is disabled; enabling it");

            setState(ENABLED);

            save();

            // Save before we call enableEvt(), so queries in
            // enableEvt() will see this Task in the correct state.
            enableEvt();
        } else if (getState() == FINISHED) {
            s_log.debug("The task is finished; reenabling it");

            setState(ENABLED);

            save();

            enableEvt();

            triggerListenerUpdateState();
        } else {
            s_log.debug("The task is in state '" + getStateString() + "'; " +
                        "doing nothing");
        }
    }

    /**
     * Disables a task.  This occurs when a task is finished. (persistent operation)
     * TODO: need to trigger update on listeners when this is called
     *
     **/
    public void disable() {
        checkEnableProcess();
        int currState = getState();
        if (currState == DISABLED) {
            return;
        }

        setState(DISABLED);
        save();

        //if finished then inform other dependent
        // task that it was rolled back
        if (currState == FINISHED) {
            rollbackEvt();
            triggerListenerUpdateState();
        } else if (currState == ENABLED) {
            disableEvt();
        }


    }
    /**
     * Method used to trigger calls to task listeners to
     * update their state because of a state change in this task
     *
     **/
    private void triggerListenerUpdateState() {

        Iterator finishedListeners = getFinishedListeners();
        Task tempTask = null;

        while (finishedListeners.hasNext()) {
            tempTask = (Task)finishedListeners.next();
            tempTask.updateState();
        }
    }

    /**
     * Checks the task is in process that is in a started state.
     * Not complete.
     *
     **/
    private void checkEnableProcess()  {
        /*
          if (m_wfProcess.getTaskStateInternal() != WorkflowProcess.STARTED) {
          throw new TaskException("Process must in start state");
          }
        */

    }

    /**
     * Get the state of a task.
     *
     * @return the task state.
     *
     **/
    public int getState() {
        return getStateMapping(getStateString());
    }

    /**
     * <b> DO NOT USE </b> Should be private.
     * Sets the current state of task (persistent after save).
     *
     * @param state the state to set the task
     *
     */
    public final void setState(int state) {
        set(TASK_STATE, getStateString(state));
    }

    /**
     * Helper method to convert from object presentation to
     * persistent presentation
     *
     * @return the State String
     *
     **/
    public static String getStateString(int state) {
        switch (state) {
        case ENABLED:  return  "enabled";
        case DISABLED: return  "disabled";
        case FINISHED: return  "finished";
        case DELETED:  return  "deleted";
        }
        return null;
    }

    /**
     * Helper method to convert from persistent representation to
     * object representation
     *
     * @param taskState the state string
     * @return the object representation
     *
     **/
    public static int getStateMapping(String taskState) {

        if (taskState.compareTo("enabled") == 0) {
            return ENABLED;
        }
        if (taskState.compareTo("disabled") == 0) {
            return DISABLED;
        }
        if (taskState.compareTo("finished") == 0) {
            return FINISHED;
        }
        if (taskState.compareTo("deleted") == 0) {
            return DELETED;
        }
        return DISABLED;
    }

    /**
     * Marks the task as finished.  This operation is only valid if the
     * task is enabled.  (persistent operation)
     *
     **/
    public void finish() throws TaskException {
        checkEnableProcess();
        if (!isEnabled()) {
            throw new TaskException("Task is not enabled");
        }

        // set to finish
        setState(FINISHED);
        save();
        finishEvt();

        if ( s_log.isDebugEnabled() ) {
            s_log.debug("Task finished" + getID());
            s_log.debug("Task state finished is " + isFinished());
            s_log.debug("Task active state is " + isActive());
        }

        Iterator finishedListeners = getFinishedListeners();
        Task tempTask;
        try {
            while (finishedListeners.hasNext()) {
                tempTask = (Task)finishedListeners.next();
                if ( s_log.isDebugEnabled() ) {
                    s_log.debug("preNotifyFinished listeners " + tempTask.getID());
                }
                tempTask.notifyFinished(this);
                if ( s_log.isDebugEnabled() ) {
                    s_log.debug("processing listeners " + tempTask.getID());
                }
            }
        } catch (TaskException taskException) {
            if ( s_log.isDebugEnabled() ) {
                taskException.printStackTrace();
                 s_log.debug("setting state to be enabled " + getID());
            }
            setState(ENABLED);
        }
    }

    /**
     * Tests whether the task is enabled.
     *
     * @return <code>true</code> if the task is enabled;
     * <code>false</code>otherwise.
     **/
    public boolean isEnabled() {
        return  (getState() == ENABLED);
    }

    /**
     * Tests whether the task is finished.
     *
     * @return <code>true</code> if the task is finished; otherwise.
     **/
    public boolean isFinished() {
        return (getState() == FINISHED);
    }


    /**
     * Update the task state. This triggers this task to check whether
     * its enabled or not.  It triggers state changes for this task,
     * which could result in a save.
     *
     **/
    synchronized void updateState()  {
        boolean dependenciesSatisfied = true;

        if ( s_log.isDebugEnabled() ) {
            s_log.debug("Updating state for " + getID() + " " + getLabel());
        }

        if ((getState() == DELETED) || !isActive()) {
            return;
        }

        Iterator dependencies = getDependencies();
        Task dependsOnTask;

        while (dependencies.hasNext()) {
            dependsOnTask = (Task)dependencies.next();
            if ( s_log.isDebugEnabled() ) {
                s_log.debug("Checking dependency " + dependsOnTask.getID() +
                            " for " + getID());
            }
            if ((!dependsOnTask.isFinished()) && dependsOnTask.isActive()) {
                if ( s_log.isDebugEnabled() ) {
                    s_log.debug("Dependency not yet satisfied: " + dependsOnTask.getID());
                    s_log.debug("Dependency finished state: " + dependsOnTask.isFinished());
                    s_log.debug("Dependency active state: " + dependsOnTask.isActive());
                }
                dependenciesSatisfied = false;
                break;
            }
        }

        if ( s_log.isDebugEnabled() ) {
            s_log.debug("dependencies state is " + dependenciesSatisfied + " for " +
                        getID());
            s_log.debug("the state is " + getState());
        }

        // Rollback case. Previously finished task, but parent tasks
        // are re-enabled.
        if (getState() == FINISHED) {
            if (!dependenciesSatisfied) {
                disable();
                return;
            } else {
                enable();
                return;
            }
        }

        if (getState() == ENABLED) {
            if (!dependenciesSatisfied) {
                disable();
                return;
            }
        }
        if (getState() == DISABLED) {
            if (dependenciesSatisfied) {
                enable();
                return;
            }
        }
    }

    /**
     * Notifies finished listeners.
     *
     * @param senderTask the task that is completed
     *
     **/
    public void notifyFinished(Task senderTask)
        throws TaskException, ProcessException{
        updateState();
    }

    @Override
    public void delete() {
        triggerListenerUpdateState();
        super.delete();
    }

    @Override
    public String getDisplayName() {
        return getLabel();
    }

    // This is our interface that is to be implemented by subclasses
    // that want some action on these events

    /**
     * When the task is moved from enabled to disabled state. This method is
     * called.
     *
     **/
    protected void rollbackEvt()  {
    };

    /**
     * Called when the task is enabled.
     **/
    protected void enableEvt() {
    };

    /**
     * Called when a task is disabled.
     *
     **/
    protected void disableEvt() {
    };

    /**
     * Called when a task is finished.
     **/
    protected void finishEvt() {
    };

}
