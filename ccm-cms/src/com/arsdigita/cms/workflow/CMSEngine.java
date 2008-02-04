/*
 * Created on 07-Jun-06
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.arsdigita.cms.workflow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.workflow.simple.Engine;
import com.arsdigita.workflow.simple.Task;
import com.arsdigita.workflow.simple.UserTask;
import com.arsdigita.workflow.simple.Workflow;

/**
 * @author chris.gilbert@westsussex.gov.uk
 *
 * Workflow task engine that applies permission checks according to the privilege assigned to 
 * the task type of the retrieved task
 */
public class CMSEngine extends Engine {
	
	public static final String CMS_ENGINE_TYPE  = "cms";
	/**
	 * 
	 */
	
	private static final Logger s_log =
			Logger.getLogger(CMSEngine.class);

		
		
	public List getEnabledTasks(User user, BigDecimal workflowID) {
		s_log.debug("getEnabledTasks");
			DataCollection tasks = SessionManager.getSession().retrieve(CMSTask.BASE_DATA_OBJECT_TYPE);
			// include the object and task type so we can do a permission check 
			// without separate db access to get these items
			tasks.addPath(Workflow.TASK_WORKFLOW + "." + Workflow.ACS_OBJECT);
			tasks.addPath(CMSTask.TASK_TYPE);
			tasks.addEqualsFilter(Task.PARENT_TASK_ID, workflowID);
			tasks.addEqualsFilter(Task.IS_ACTIVE, Boolean.TRUE);
			tasks.addEqualsFilter(Task.TASK_STATE,"enabled");
			Filter assignedToUser = tasks.getFilterFactory().equals(UserTask.ASSIGNED_USERS + "." + User.ID, user.getID());
			Filter assignedToUserGroup = tasks.getFilterFactory().equals(UserTask.ASSIGNED_GROUPS + ".allMembers." + Group.ID,user.getID());
			tasks.addFilter(tasks.getFilterFactory().or().addFilter(assignedToUser).addFilter(assignedToUserGroup));
			// add to set, as query returns row for same task for each assigned user/group
			Set userTasks = new HashSet();
			while (tasks.next()) {
				userTasks.add(tasks.getDataObject().getOID());
			}
			Iterator it = userTasks.iterator();
			// carry out permission check in interation on set to prevent permission check (& hence db access)
			// on all the duplicated tasks returned by query
			ArrayList accessibleUserTasks = new ArrayList();
			while(it.hasNext()) {
				CMSTask task = (CMSTask)DomainObjectFactory.newInstance((OID) it.next());
				s_log.debug("task retrieved is " + task.getID());
				
				Workflow workflow = task.getWorkflow();
				s_log.debug("workflow for task is " + workflow.getID());
				
				ACSObject object = workflow.getObject();
				PrivilegeDescriptor privilege = task.getTaskType().getPrivilege();
				PermissionDescriptor taskAccess = new PermissionDescriptor(privilege, object, user);
				s_log.debug("checking " + privilege.getName() + " privilege on object " + object.getID());
				if (PermissionService.checkPermission(taskAccess)) {
					s_log.debug("user " + user.getID() + " can access this task");
					accessibleUserTasks.add(task);
				}
				
				
			}
			
			return accessibleUserTasks;
		}
		
	
		
}
