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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollectionIterator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Represents a process instance that is assigned to a particular process that
 * is associated with some object.
 *
 * @author Khy Huang
 * @author Stefan Deusch
 * @author Uday Mathur
 * @author Karl Goldstein
 * @version $Id: Workflow.java 1278 2006-07-27 09:09:51Z cgyg9330 $
 *
 *
 */
public class Workflow extends Task {

    public static final String BASE_DATA_OBJECT_TYPE =
            "com.arsdigita.workflow.simple.Workflow";
    private static final Logger s_log = Logger.getLogger(Workflow.class);
    private OID m_ACSObjectOID = null;
    public static final String ACS_OBJECT = "object";
    private static final String PROCESS_STATE = "processState";
    private static final String WF_TASKS = "wfTasks";
    public static final String TASK_WORKFLOW = "taskWf";
    private static final String PROCESS_DEF_ID = "processDefinitionID";
    // class variables, enums of constants
    public final static int NONE = -1;
    public final static int STARTED = 0;
    public final static int STOPPED = 1;
    public final static int DELETED = 2;
    public final static int INIT = 3;

    /**
     * Creates a new workflow process. The properties of this object are not
     * made persistent until the
     * <code>save</code> method is called.
     *
     * @param label the label
     * @param description the description
     *
     */
    public Workflow(String label, String description) {
        super(BASE_DATA_OBJECT_TYPE);

        initAttributes(label, description);
    }

    /**
     * Creates a new workflow process with the properties
     * <code>label</code> and
     * <code>description</code> set to null. Properties of this object are not
     * made persistent until the
     * <code>save</code> method is called. If save() is called without setting
     * these properties, an IllegalArgumentException will be thrown.
     *
     */
    public Workflow() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Restores a workflow process from a task data object.
     *
     * @param workflowDataObject
     *
     */
    public Workflow(DataObject workflowDataObject) {
        super(workflowDataObject);
        DataObject object = (DataObject) get(ACS_OBJECT);
        if (object != null) {
            m_ACSObjectOID = object.getOID();
        }
    }

    /**
     * Constructor for setting the object type.
     *
     * @param type the object type
     * @see com.arsdigita.persistence.metadata.ObjectType
     *
     */
    protected Workflow(ObjectType type) {
        super(type);
    }

    /**
     * Constructor for setting the object type name.
     *
     * @param typeName the type name
     *
     *
     */
    protected Workflow(String typeName) {
        super(typeName);
    }

    /**
     * Restores a workflow process using an OID.
     *
     * @param oid the OID
     * @see com.arsdigita.persistence.OID
     *
     *
     */
    public Workflow(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Restores a workflow process using a BigDecimal ID.
     *
     * @param id the BigDecimal ID of this object. An OID will be created
     * implicitly with the BASE_DATA_OBJECT_TYPE constant specified in this
     * file.
     *
     */
    public Workflow(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Initializes the internal state depending on whether this is a new or a
     * restored instance.
     *
     */
	@Override
    protected void initialize() {
        super.initialize();

        if (!isNew()) {
            DataObject object = (DataObject) get(ACS_OBJECT);
            if (object != null) {
                m_ACSObjectOID = object.getOID();
            }

        } else {
            setProcessState(INIT);
        }
    }

    /**
     * Returns the base data object type.
     *
     * @return the base data object type.
     */
	@Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Adds a task to this process. (persistent operation)
     *
     * @param task the task to add to this process
     * @see Task
     *
     */
    public void addTask(Task task) {
        task.setParent(this);
        if (getProcessState() == STARTED) {
            task.updateState();
        }
        task.addFinishedListener(this);
        task.save();
        updateState();
    }

    /**
     * Deletes a task. (perisistent operation).
     *
     * @param task to be removed
     *
     */
    public void removeTask(Task task) {
        remove(WF_TASKS, task);
        //task.delete()
    }

    /**
     * Removes all tasks from this workflow. (persistent operation)
     *
     */
    public void removeAllTasks() {
        DataAssociation da = getTaskAssociation();
        da.clear();
        save();
    }

    public final TaskCollection getTaskCollection() {
        return new TaskCollection(getTaskAssociation().cursor());
    }

    /**
     * Saves the current state of the process.
     *
     * @param user the user
     *
     *
     */
    public void save(User user) throws ProcessException {
        //add logging information
        save();
    }

    /**
     * Returns the number of task in this process.
     *
     * @return the numnber of tasks in the workflow
     *
     */
    public int getTaskCount() {
        return (new Long(getTaskAssociation().cursor().size())).intValue();
    }

    // ---------------- START: state methods --------------------------
    /**
     * Stops the process. (persistent operation)
     *
     * @param user the user that stopped the process
     *
     *
     */
    public void stop(User user) {
        setProcessState(STOPPED);
        save();
    }

    /**
     * Starts the process. This method marks all the tasks as active when called
     * the first time. (persistent operation)
     *
     * @param user the user that starts the process
     *
     *
     */
    public void start(User user) {

        int processState = getProcessState();

        setProcessState(STARTED);
        if (processState == INIT) {
            setActive(true);
            updateState();
            startInternal();
        }

        save();
    }

    /**
     * Gets the process spinning. This method also marks all the tasks as
     * active.
     *
     *
     *
     */
    protected void startInternal() {

        TaskCollection tasks = getTaskCollection();
        try {
            while (tasks.next()) {
                Task tempTask = tasks.getTask();
                tempTask.setActive(true);
                tempTask.save();
            }

        } finally {
            tasks.close();
        }

        // force the tasks to be reloaded from the DB
        tasks = getTaskCollection();
        try {
            while (tasks.next()) {
                // we do this in 2 steps (setting all Tasks active first,
                // and then reloading and updating their state) to avoid
                // ordering issues (a Task getting prematurely enabled because
                // an incomplete dependency has not been made active yet)
                Task tempTask = tasks.getTask();
                tempTask.updateState();

            }

        } finally {
            tasks.close();
        }
    }

    /**
     * Retrieves the state of the process.
     *
     * @return The process state
     *
     */
    public int getProcessState() {
        return getProcessStateInt((String) get(PROCESS_STATE));
    }

    /**
     * Internal method to set the state from the DB, 'stopped',
     * 'started','deleted', 'init' are allowed
     *
     * @param state the process state
     *
     *
     */
    private void setProcessState(int state) {
        set(PROCESS_STATE, getProcessStateStr(state));
        if (!isNew()) {
            save();
        }
    }

    /**
     * Sets the Object that this workflow is applied to.
     *
     * @param o the object to which to apply this workflow.
     *
     */
    public void setObject(ACSObject o) {
        m_ACSObjectOID = o.getOID();
        set(ACS_OBJECT, o);
    }

    /**
     * Set the object id
     *
     *
     */
    public void setObjectID(BigDecimal id) {
        m_ACSObjectOID = new OID(ACSObject.BASE_DATA_OBJECT_TYPE, id);
        try {
            set(ACS_OBJECT, DomainObjectFactory.newInstance(m_ACSObjectOID));
        } catch (DataObjectNotFoundException e) {
            s_log.error("unable to locate the ID for the workflow");
            throw new UncheckedWrapperException("Unable to locate object corresponding to ID " + id, e);
        }
    }

    /**
     * Helper method that converts the state string in persistence to
     * WorkflowProcess object state ids
     *
     *
     */
    private int getProcessStateInt(String state) {
        if (state.equals("stopped")) {
            return STOPPED;
        } else if (state.equals("started")) {
            return STARTED;
        } else if (state.equals("deleted")) {
            return DELETED;
        } else if (state.equals("init")) {
            return INIT;
        }
        return -1;
    }

    /**
     * Helper method that converts the state ids in WorkflowProcess object to
     * state string in persistence
     *
     * @return the persistence state string
     *
     *
     */
    private String getProcessStateStr(int state) {
        switch (state) {
            case STARTED:
                return "started";
            case STOPPED:
                return "stopped";
            case DELETED:
                return "deleted";
            case INIT:
                return "init";
            case NONE:
                return "none";
        }
        return null;
    }
    // ---------------- END: state methods -------------------

    /**
     * Returns an iterator for the tasks in this process.
     *
     * @return the list of all tasks.
     *
     *
     */
    public Iterator getTasks() {
        return new DomainCollectionIterator(getTaskCollection());
    }

    /**
     * The data association for the tasks in workflow process.
     *
     * @return the data collection
     *
     *
     */
    private DataAssociation getTaskAssociation() {
        return (DataAssociation) get(WF_TASKS);
    }

    /**
     * Returns an iterator over all enabled tasks in the process.
     *
     * @return an interator over enabled tasks.
     *
     *
     */
    public Iterator getEnabledTasks() {
        if (getProcessState() == DELETED || getProcessState() == STOPPED) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("No enabled tasks. Process state is "
                        + getProcessState());
            }
            return Collections.EMPTY_LIST.iterator();
        }


        TaskCollection tasks = getTaskCollection();
        try {
            FilterFactory factory = tasks.getFilterFactory();
            CompoundFilter filter = factory.and();
            filter.addFilter(factory.equals(Task.TASK_STATE, Task.getStateString(Task.ENABLED)));
            filter.addFilter(factory.equals(Task.IS_ACTIVE, "1"));
            tasks.addFilter(filter);

            LinkedList enabledTasks = new LinkedList();
            while (tasks.next()) {
                enabledTasks.add(tasks.getTask());
            }

            return enabledTasks.iterator();

        } finally {
            tasks.close();
        }
    }

    /**
     * Returns an iterator over all finished tasks in the process.
     *
     * @return an iterator over finished tasks.
     *
     *
     */
    public Iterator getFinishedTasks() {
        TaskCollection tasksCollection = getTaskCollection();
        Filter filter = tasksCollection.addFilter("taskState = :taskState");
        filter.set("taskState", "finished");
        return new DomainCollectionIterator(tasksCollection);
    }

    /**
     * @return an iterator over all overdue tasks in the process.
     *
     */
    public Iterator getOverdueTasks() {
        HashSet tasksSet = new HashSet();
        TaskCollection tasksCollection = getTaskCollection();
        while (tasksCollection.next()) {
            Task task = (Task) tasksCollection.getDomainObject();
            if (task instanceof UserTask && ((UserTask) task).isOverdue()) {
                tasksSet.add(task);
            }
        }

        tasksCollection.close();

        return tasksSet.iterator();
    }

    /**
     * Returns the object associated with the process.
     *
     * @return the ACS object that this process is based on.
     *
     *
     */
    public OID getObjectOID() {
        return m_ACSObjectOID;
    }

    /**
     * Get the ACSObject this Workflow is associated with.
     *
     */
    public ACSObject getObject() {
        OID objOID = getObjectOID();
        if (objOID == null) {
            return null;
        } else {
            try {
                return (ACSObject) DomainObjectFactory.
                        newInstance(getObjectOID());
            } catch (DataObjectNotFoundException de) {
                throw new UncheckedWrapperException("Could not load object with OID "
                        + getObjectOID(),
                        de);
            }
        }
    }

    /**
     * Removes a task from the underlying workflow process definition.
     *
     * @param task the task to be removed
     * @param dependentList the task definitions that are dependent on the
     * passed in task definition
     *
     *
     */
    public void removeTask(Task task, Iterator dependentList) {
        Task dependentTask;
        while (dependentList.hasNext()) {
            dependentTask = (Task) dependentList.next();
            dependentTask.removeFinishedListener(task);
            //dependentTask.updateState();
        }
    }

    /**
     * Override the update method, workflow is finished if all its task are
     * completed
     *
     *
     *
     */
	@Override
    synchronized void updateState() {
        super.updateState();


        if (getState() == ENABLED) {
            TaskCollection tasks = getTaskCollection();

            FilterFactory factory = tasks.getFilterFactory();
            CompoundFilter filter = factory.and();
            filter.addFilter(factory.notEquals(Task.TASK_STATE, Task.getStateString(Task.FINISHED)));
            filter.addFilter(factory.equals(Task.IS_ACTIVE, "1"));
            tasks.addFilter(filter);

            try {
                if (tasks.next()) {
                    return;
                }

            } finally {
                tasks.close();
            }

            try {
                finish();
            } catch (TaskException e) {
                throw new UncheckedWrapperException("failed on calling finished: ", e);
            }
        }

        if (getState() == FINISHED) {
            boolean enable = false;
            TaskCollection tasks = getTaskCollection();
            FilterFactory factory = tasks.getFilterFactory();
            tasks.addFilter(factory.notEquals(Task.TASK_STATE, Task.getStateString(Task.FINISHED)));
            try {
                enable = tasks.next();
            } finally {
                tasks.close();
            }
            if (enable) {
                enable();
            }
        }
    }

    /**
     * Performs a deep clone of the workflow. All tasks are cloned as well. The
     * cloned copy is saved to persistent storage before returning.
     *
     * @return the cloned workflow process.
     *
     *
     */
	@Override
    public synchronized Object clone() {
        Workflow workflowClone = new Workflow(getLabel(), getDescription());
        //copyAttributes(workflowClone);
        workflowClone.save();
        cloneTasks(workflowClone);
        return workflowClone;
    }

    protected void cloneTasks(Workflow workflowClone) {
        Map taskToCloneMap = new HashMap();
        Task dependOn, dependOnClone;
        Task task;
        Task taskClone, taskListenerClone, listener;
        Iterator dependencies;
        Iterator tasks, finishedListeners;

        // Clone each task definition
        tasks = getTasks();

        while (tasks.hasNext()) {
            task = (Task) tasks.next();
            taskClone = null;
            try {
                taskClone = (Task) (task.clone());
                taskClone.removeAllFinishedListeners();
                taskClone.save();
                taskClone.removeAllDependencies();
                taskClone.save();
            } catch (CloneNotSupportedException c) {
                //update to use logger
                c.printStackTrace();
                throw new RuntimeException(c.getMessage());
            }
            workflowClone.addTask(taskClone);
            taskToCloneMap.put(task, taskClone);
        }

        // Copy over Task Definition dependendencies to the cloned ones
        tasks = getTasks();

        while (tasks.hasNext()) {
            task = (Task) tasks.next();
            taskClone = (Task) taskToCloneMap.get(task);

            // Clone also copies the references to previous environment,
            // so we remove those
            // Copy dependencies of one task definition to the clone one
            dependencies = task.getDependencies();
            while (dependencies.hasNext()) {
                dependOn = (Task) dependencies.next();
                dependOnClone = (Task) taskToCloneMap.get(dependOn);
                taskClone.addDependency(dependOnClone);
            }

            //Clone the necessary FinishedListeners. If the finishedListeners
            //is not a dependency then clone them.
            finishedListeners = task.getFinishedListeners();
            while (finishedListeners.hasNext()) {
                listener = (Task) finishedListeners.next();

                // Listeners to exclude are :
                //    1. the containing workflow
                //    2. Task that depend on this task; When we added dependency
                //        a listener was already registered.
                if (!listener.equals(this)
                        && !listener.isDependency(task)) {

                    //Clone the listener and add clone task as listener
                    try {
                        taskListenerClone = (Task) listener.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                    }
                    taskListenerClone.save();
                    taskClone.addFinishedListener(taskListenerClone);
                }
            }
            taskClone.save();
        }

        workflowClone.save();
    }

    /**
     * Overrides the enable method (since the behavior is a little different).
     *
     *
     */
	@Override
    public void enable() {

        int taskState = getState();
        if (taskState == ENABLED) {
            return;
        }

        super.enable();

        //If we were in finished state, we need to move back to
        //INIT state.  In INIT State all tasks are disabled.
        /*
         if (taskState == FINISHED) {
         Iterator itr = getTasks();

         while (itr.hasNext()) {
         tempTask = (Task)itr.next();
         tempTask.disable();
         }
         setProcessState(INIT);
         start(null);
         } else if (getProcessState() == INIT) {
         start(null);
         }
         */

    }

    /**
     * On a disable event, stops the workflow.
     *
     *
     */
	@Override
    public void disableEvt() {
        Task tempTask = null;
        stop(null);

        Iterator itr = getTasks();

        while (itr.hasNext()) {
            tempTask = (Task) itr.next();
            tempTask.disable();
        }
        setProcessState(INIT);
        save();
    }

    /**
     * Set the template from which this Workflow was instantiated.
     *
     */
    public void setWorkflowTemplate(WorkflowTemplate template) {
        if (template != null) {
            set(PROCESS_DEF_ID, template.getID());
        } else {
            set(PROCESS_DEF_ID, null);
        }
    }

    /**
     * Get the template from which this workflow was instantiated.
     *
     */
    public WorkflowTemplate getWorkflowTemplate() {
        BigDecimal templateID = (BigDecimal) get(PROCESS_DEF_ID);
        if (templateID != null) {
            try {
                return (WorkflowTemplate) DomainObjectFactory.
                        newInstance(new OID(WorkflowTemplate.BASE_DATA_OBJECT_TYPE,
                        templateID));
            } catch (DataObjectNotFoundException de) {
                throw new UncheckedWrapperException("Could not load WorkflowTemplate with ID " + templateID,
                        de);
            }
        } else {
            return null;
        }
    }

    /* static methods to retrieve the workflow associated with an object */
    public static BigDecimal getItemID(Workflow w) {
        return getItemID(w.getID());
    }

    public static BigDecimal getItemID(BigDecimal workflowId) {
        BigDecimal objectID = null;
        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery("com.arsdigita.workflow.simple.getProcesses");
        query.addEqualsFilter("id", workflowId);
        if (query.next()) {
            objectID = (BigDecimal) query.get("processObjectID");
            query.close();
        }
        return objectID;
    }

    /* static methods to retrieve the workflow associated with an object */
    public static BigDecimal getObjectWorkflowID(BigDecimal id) {
        BigDecimal workflowID = null;
        final Session session = SessionManager.getSession();
        final DataQuery query = session.retrieveQuery("com.arsdigita.workflow.simple.getProcesses");
        Filter filter = query.addFilter("processObjectID = :object_id");
        filter.set("object_id", id);

        if (query.next()) {
            workflowID = (BigDecimal) query.get("processID");
            query.close();
        }

        return workflowID;
    }

    public static BigDecimal getObjectWorkflowID(ACSObject o) {
        return getObjectWorkflowID(o.getID());
    }

    public static Workflow getObjectWorkflow(BigDecimal id) {
        final BigDecimal workflowID;
        final Session session = SessionManager.getSession();
        final DataQuery query = session.retrieveQuery("com.arsdigita.workflow.simple.getProcesses");
        final Filter filter = query.addFilter("processObjectID = :object_id");
        filter.set("object_id", id);

        if (query.next()) {
            workflowID = (BigDecimal) query.get("processID");
            query.close();

            if (workflowID != null) {
                return new Workflow(workflowID);
            }
        }

        return null;
    }

    public static Workflow getObjectWorkflow(ACSObject o) {
        return getObjectWorkflow(o.getID());
    }
}
