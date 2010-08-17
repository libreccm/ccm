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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.util.Date;

import com.arsdigita.kernel.User;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectInstantiator;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObject;


import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

// Support for Logging.
import org.apache.log4j.Logger;

/**
 * Class representing the workflow engine.
 *
 * Also implements an engine factory. The factory functionality was implemented here rather in a separate class
 * to enable existing clients to retrieve a simple engine using the same static call 
 *
 * @version $Revision: #13 $ $DateTime: 2004/08/16 18:10:38 $
 * @version $Id: Engine.java 1278 2006-07-27 09:09:51Z cgyg9330 $
 */
public class Engine {


    private static final Logger s_log =
        Logger.getLogger(Engine.class);
	public static final String SIMPLE_ENGINE_TYPE = "simple";
    	
	
/*
 * ***************************************************
 * 
 * FACTORY IMPLEMENTATION
 * 
 * ***************************************************
 */
 
 	private static Map s_engines = new HashMap();


    /**
	 * gets an instance of Engine or subtypes according to the passed in string. Subtypes must 
	 * be registered before attempting to get an instance using registerEngine
	 * @param type
	 * @return
     */
	public static Engine getInstance(String type) {
		Engine engine = (Engine)s_engines.get(type);
		// lazy initialization for the simple engine
		// 2 threads might enter this if block and instantiate new
		// engines, but a check is made within synchronised method
		// when attempting to register engine. Didn't want to 
		// synchronise in this method because of potential bottleneck 
		if (engine == null && type.equals(SIMPLE_ENGINE_TYPE)) {
			engine = new Engine();
			Engine.registerEngine(SIMPLE_ENGINE_TYPE, engine);
		}
		// if null, then no engine registered for the type
		Assert.exists(engine);
		return engine;
		
	}
	
	// synchronized to prevent 2 threads invoking registerInstantiators and hence attempting double registration
	// by the time any blocked thread gets into method, s_engines won't be empty
	public static synchronized void registerEngine(String engineType, Engine engine){
		s_log.debug("registering task engine for " + engineType);
		if (s_engines.isEmpty()){
			s_log.debug("First Engine registered - registering task domainObjectInstantiators");
			registerInstantiators();
		}
		if (s_engines.get(engineType) == null) {
			s_engines.put(engineType, engine);
		}
	}

    /**
     * Gets an instance of the simple workflow engine.
     * @return an instance of the simple workflow engine.
     */
	// legacy method of retrieving a simple Engine
	public static Engine  getInstance() {
    	return getInstance(SIMPLE_ENGINE_TYPE);

    }

    
    

    /**
     * Constructor: Set up the instantiators for subclasses of task
     */
    private static void registerInstantiators() {
        DomainObjectInstantiator instTask = new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Task(dataObject);
                }
            };

        DomainObjectFactory.registerInstantiator(
                                                 Task.BASE_DATA_OBJECT_TYPE,
                                                 instTask);

        DomainObjectInstantiator instUserTask = new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new UserTask(dataObject);
                }
            };

        DomainObjectFactory.registerInstantiator(
                                                 UserTask.BASE_DATA_OBJECT_TYPE,
                                                 instUserTask);

        DomainObjectInstantiator instWorkflow = new ACSObjectInstantiator() {
                public DomainObject doNewInstance(DataObject dataObject) {
                    return new Workflow(dataObject);
                }
            };

        DomainObjectFactory.registerInstantiator(
                                                 Workflow.BASE_DATA_OBJECT_TYPE,
                                                 instWorkflow);

//         DomainObjectInstantiator instWorkflowTemplate =
//             new ACSObjectInstantiator() {
//                 public DomainObject doNewInstance(DataObject dataObject) {
//                     return new WorkflowTemplate(dataObject);
//                 }
//             };

        /*
          DomainObjectFactory.registerInstantiator(
          WorkflowTemplate.BASE_DATA_OBJECT_TYPE,
          instWorkflowTemplate);
        */
    }


/*
 * ************************************************************
 * 
 * SIMPLE ENGINE FUNCTIONS
 * 
 * ************************************************************
 */
    /**
     * From the query put the assigned list into assignedTask Array List
     * The result set from query needs to have taskID.
     *
     * @param assignedTasks assigned tasks
     * @param query the query object
     *
     */
    private void populateAssignedTaskUser(ArrayList assignedTasks ,
                                          DataQuery query) {
        Session session = SessionManager.getSession();
        BigDecimal taskID = null;

        while (query.next()) {
            taskID = (BigDecimal)query.get("taskID");

            DataObject taskDataObject =
                session.retrieve(new OID("com.arsdigita.workflow.simple.Task",
                                         taskID));
            assignedTasks.add(DomainObjectFactory.newInstance(taskDataObject));
        }
    }

    /**
     * From the query put the assigned list into assignedTask Array List
     * The result set needs to have taskID and groupID
     *
     * @param assignedTasks the assigned task
     * @param query the query object
     * @param user the user to check groups
     *
     */
    private void populateAssignedTaskGroup(ArrayList assignedTasks,
                                           DataQuery query ,
                                           User user) {
        BigDecimal taskID = null;
        BigDecimal groupID = null;

        Task tempTask = null;
        Group group = null;
        Session session = SessionManager.getSession();

        try {
            while (query.next()) {
                taskID = (BigDecimal)query.get("taskID");
                groupID = (BigDecimal)query.get("groupID");

                // Load the group and check user is a member of the group
                try {
                    group = new Group(groupID);
                } catch (DataObjectNotFoundException e) {
                    throw new UncheckedWrapperException("Could not create task group ("+ groupID +")", e);
                }

                if (group.hasMember(user)) {
                    DataObject taskDataObject =
                        session.retrieve(new OID("com.arsdigita.workflow.simple.Task",
                                                 taskID));
                    tempTask = (Task)DomainObjectFactory.newInstance(taskDataObject);

                    // Since task is already in list we do not
                    // need to check
                    if (assignedTasks.contains(tempTask)) {
                        continue;
                    }

                    assignedTasks.add(tempTask);
                }
            }

        } finally {
            query.close();
        }
    }


	
    /**
     * Returns an ArrayList containing the set of enabled tasks in all
     * processes to which the specified user is assigned.

     * @param user a system user
     * @return the iterator
     *
     **/
    public List getEnabledTasks(User user) {

        Session session = SessionManager.getSession();

        // Retrieve all tasks directly assigned to
        // user
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.workflow.simple.getTaskAssignedUsers");
        Filter filter = query.addFilter("userID = :user_id");
        filter.set("user_id", user.getID());
        query.addFilter("taskState = 'enabled'");
        query.addFilter("isActive = '1'");

        ArrayList usersTask = new ArrayList();
        populateAssignedTaskUser(usersTask, query);

        query = session.retrieveQuery
            ("com.arsdigita.workflow.simple.getTaskAssignedGroups");

        query.addFilter("isActive = '1'");
        query.addFilter("taskState = 'enabled'");

        populateAssignedTaskGroup(usersTask, query, user);
        return usersTask;
    }

    public List getEnabledTasks(User user, BigDecimal workflowId) {

        Session session = SessionManager.getSession();

        // Retrieve all tasks directly assigned to
        // user
        
        
        
        
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.workflow.simple.getTaskAssignedUsers");
        query.addFilter("userID = :user_id");
        Filter userWorkflowFilter = query.addFilter("parentID = :workflowId");

        userWorkflowFilter.set("user_id", user.getID());
        userWorkflowFilter.set("workflowId", workflowId);
        query.addFilter("taskState = 'enabled'");
        query.addFilter("isActive = '1'");

        ArrayList usersTask = new ArrayList();
        populateAssignedTaskUser(usersTask, query);

        query = session.retrieveQuery
            ("com.arsdigita.workflow.simple.getTaskAssignedGroups");

        Filter groupWorkflowFilter = query.addFilter("parentID = :workflowId");
        groupWorkflowFilter.set("workflowId", workflowId);
        query.addFilter("isActive = '1'");
        query.addFilter("taskState = 'enabled'");

        populateAssignedTaskGroup(usersTask, query, user);
        return usersTask;
    }


    /**
     * Returns an array list over the set of overdue enabled tasks in all processes
     * to which the specified user is assigned.
     *
     * @param user a system user
     * @return the array list
     *
     **/
    public List getOverdueTasks(User user) {
        ArrayList overdueTasks = new ArrayList();

        List enabledTasks = getEnabledTasks(user);

        Iterator itr = enabledTasks.iterator();
        UserTask tempTask = null;


        while (itr.hasNext()) {
            tempTask = (UserTask)itr.next();
            if (tempTask.isOverdue()) {
                overdueTasks.add(tempTask);
            }
        }
        return overdueTasks;
    }

    /**
     * Returns an iterator over the set of tasks in all processes
     * that a user has finished in a specified period of time.
     *
     * @param user a system user
     * @param start the start date
     * @param end the end date
     * @return the iterator
     *
     **/
    public List getFinishedTasks(User user, Date start,  Date end) {
        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.workflow.simple.getTaskAssignedUsers");

        Filter startDateFilter = null;
        Filter dueDateFilter =  null;

        query.addFilter("taskState = 'finished'");
        query.addFilter("isActive = '1'");

        ArrayList finishedTasks = new ArrayList();
        populateAssignedTaskUser(finishedTasks, query);

        query = session.retrieveQuery
            ("com.arsdigita.workflow.simple.getTaskAssignedGroups");

        query.addFilter("isActive = '1'");

        if (start != null) {
            startDateFilter = query.addFilter("taskStartDate >= :start");
            startDateFilter.set("start",start);
        }
        if (end != null) {
            dueDateFilter =  query.addFilter("taskDueDate <= :end");
            dueDateFilter.set("end",end);
        }

        populateAssignedTaskGroup(finishedTasks, query, user);
        return finishedTasks;
    }

    /**
     * Returns an iterator over the set of processes that currently have
     * overdue user tasks.
     * @return the iterator
     *
     **/
    public List getOverdueProcesses() {
        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.workflow.simple.getOverDueProcesses");

        BigDecimal processID = null;
        ArrayList overdueProcesses = new ArrayList();

        try {
            while (query.next()) {
                processID = (BigDecimal)query.get("processID");
                overdueProcesses.add(new Workflow(processID));
            }

        } catch(DataObjectNotFoundException e) {
            s_log.error("Error loading workflow", e);
            throw e;
        } finally {
            query.close();
        }

        return overdueProcesses;
    }


    /**
     * Returns an iterator over the set of processes that currently have
     * enabled tasks.
     *
     * @return the iterator
     **/
    public List getActiveProcesses() {
        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.workflow.simple.getActiveProcesses");

        BigDecimal processID = null;
        ArrayList activeProcesses = new ArrayList();

        while (query.next()) {
            processID = (BigDecimal)query.get("processID");
            try {
                activeProcesses.add(new Workflow(processID));
            } catch (DataObjectNotFoundException e) {
                query.close();
                throw new Error("Faled loading process");
            }
        }
        return activeProcesses;
    }

}
