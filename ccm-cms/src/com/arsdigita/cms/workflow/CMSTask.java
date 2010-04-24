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
package com.arsdigita.cms.workflow;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.messaging.Message;
import com.arsdigita.notification.Notification;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.Assert;
import com.arsdigita.util.StringUtils;
import com.arsdigita.versioning.TagCollection;
import com.arsdigita.versioning.Transaction;
import com.arsdigita.versioning.TransactionCollection;
import com.arsdigita.versioning.Versions;
import com.arsdigita.web.URL;
import com.arsdigita.workflow.simple.TaskComment;
import com.arsdigita.workflow.simple.TaskException;
import com.arsdigita.workflow.simple.UserTask;
import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a task in the CMS system. This task is
 * Assignable, and has an associated task type. The task type
 * determines the class which performs the "action" for this page
 *
 *
 * @author Uday Mathur (umathu
 * @version $Id: CMSTask.java 1637 2007-09-17 10:14:27Z chrisg23 $
 **/
public class CMSTask extends UserTask {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.workflow.CMSTask";

    public static final String TASK_TYPE = "taskType";

    private static Map s_taskURLGeneratorCache = new HashMap();

    // not really an operation, but we treat it like one so we can reuse the
    // Notification-sending code
    public static final String UNFINISHED_OP = "unfinished";

    // a Map containing a list of the list of operations that 
    // alerts should be sent out for
    private static final Map s_alerts = new HashMap(5);
    private static final String ALERT_OPERATIONS = "operations";
    private static final String ALERT_RECIPIENTS = "recipients";
    private static final String ALERT_RECIPIENT_ALL = "_ALL";
    private static final String ALERT_RECIPIENT_LASTAUTHOR = "_LASTAUTHOR";

    private static final Logger s_log = Logger.getLogger(CMSTask.class);
    
    private boolean m_authorOnly = false;
    
    /**
     * Constructor
     */
    public CMSTask() {
        this(BASE_DATA_OBJECT_TYPE);
    }


    /**
     * Constructor for cms task used for setting object type
     *
     * @param type the object type
     *
     **/
    protected CMSTask(ObjectType type) {
        super(type);
    }

    /*
     * Constructor for cms task used for setting object type
     *
     * @param typeName the type name
     *
     **/
    protected CMSTask(String typeName) {
        super(typeName);
    }

    /**
     * Constructor for restoring user task with a data object
     *
     * @param CMSTaskObject the data object
     *
     **/
    public CMSTask(DataObject CMSTaskObject) {
        super(CMSTaskObject);
    }

    /**
     * Restores a task  from an OID
     *
     * @param oid an OID
     *
     **/
    public CMSTask(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Restores a task from a BigDecimal by constructing an OID
     *
     * @param id - the BigDecimal Id of this task
     *
     **/
    public CMSTask(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Initialize setting the TaskType to Authoring by default.
     *
     **/
    protected void initialize() {
        super.initialize();
        if (isNew()) {
            setTaskType(CMSTaskType.retrieve(CMSTaskType.AUTHOR));
        }

    }

    /**
     * Sets the type of this Task to the corresponding taskType
     *
     * @param taskType BigDecimal corresponding to the id of the taskType
     **/
    public void setTaskType(CMSTaskType taskType) {
        setAssociation(TASK_TYPE, taskType);
    }

    public CMSTaskType getTaskType() {
        return new CMSTaskType((DataObject)get(TASK_TYPE));
    }

    public String getFinishURL(BigDecimal itemId) {
        if (itemId == null) {
            return "";
        }
        TaskURLGenerator generator = getURLGenerator(getTaskType().getID());
        //assert not null here
        return generator.generateURL(itemId, getID());
    }

    protected String getAuthoringURL(ContentItem item) {
        return ContentItemPage.getItemURL(item, ContentItemPage.AUTHORING_TAB);
    }

    /**
     * Get the item associated with this Workflow.
     * Assumes that we're always going to be
     * working with ContentItems
     **/
    public ContentItem getItem() {
        DataQuery query = SessionManager.getSession().
            retrieveQuery("com.arsdigita.cms.workflow.getItemFromTask");
        query.setParameter("taskID", getID());
        try {
            if (query.next()) {
                DataObject obj = (DataObject) query.get("obj");
                return (ContentItem) DomainObjectFactory.
                    newInstance(obj);
            } else {
                return null;
            }
        } finally {
            query.close();
        }
    }
    public void enableEvt() {
        super.enableEvt();
        // Remove the record of previously sent "unfinished notifications".
        // We need to do this in case this Task has been rolled back,
        // because we want the timers to restart from the beginning.
        DataOperation oper = SessionManager.getSession().
            retrieveDataOperation("com.arsdigita.cms.workflow.clearNotifications");
        oper.setParameter("taskID", getID());
        oper.execute();
    }

    private TaskURLGenerator getURLGenerator(Integer taskTypeID) {
        TaskURLGenerator t = (TaskURLGenerator)
            s_taskURLGeneratorCache.get(taskTypeID);
        if (t == null) {
            Session s = SessionManager.getSession();
            DataQuery query = s.retrieveQuery("com.arsdigita.cms.workflow.getTaskTypes");
            query.addEqualsFilter("Id", taskTypeID);
            if (query.next()) {
                String className = (String)query.get("className");
                query.close();
                try {
                    Class URLGenerator = Class.forName(className);
                    t = (TaskURLGenerator)URLGenerator.newInstance();
                    s_taskURLGeneratorCache.put(taskTypeID, t);

                } catch (ClassNotFoundException c) { //add debug messages here
                    s_log.error("Couldn't find class", c);
                } catch (IllegalAccessException i) {
                    s_log.error("Couldn't access constructor or newInstance", i);
                } catch (InstantiationException n) {
                    s_log.error("Couldn't instantiate", n);
                }
            }
        }
        return t;
    }

    protected Message generateMessage(String operation, Party sender) {
        ContentItem item = getItem();
        Assert.exists(item, "item associated with this CMSTask");
        
        String authoringURL = getAuthoringURL(item);
        String fullURL = getTaskType().getURLGenerator(operation, item).generateURL(item.getID(), getID());
        s_log.debug("URL retrieved from generator: " + fullURL);
        if (!fullURL.startsWith("http")) {
	    // url is not fully qualified
            fullURL = URL.there(fullURL, null).getURL();
        	
        }
        // see CMSResources.properties for how these values are used
        Object[] g11nArgs = new Object[11];
		g11nArgs[0] = item.getDisplayName();
        g11nArgs[1] = new Double(getTaskType().getID().doubleValue());
        g11nArgs[2] = fullURL;
        g11nArgs[3] = KernelHelper.getSiteName();
        g11nArgs[4] = KernelHelper.getSystemAdministratorEmailAddress();
        g11nArgs[5] = new Date();
        TaskComment comment = getLastCommentInWorkflow();
        User commenter = null;
        if (comment != null) {
            commenter = comment.getUser();
            g11nArgs[6] = comment.getComment();
        } else {
            g11nArgs[6] = "";
        }
        if (commenter != null) {
            g11nArgs[7] = commenter.getName();
        } else {
            g11nArgs[7] = (String) GlobalizationUtil.globalize("cms.ui.unknown").localize();
        }
        g11nArgs[8] = getStartDate();
        g11nArgs[9] = URL.there(authoringURL, null).getURL();
	//if added to email, allows recipient to identify if the item is in a folder 
	// they are interested in
        g11nArgs[10] = ((ContentItem)item.getParent()).getPath();
        String subject = (String) GlobalizationUtil.globalize("cms.ui.workflow.email.subject." + operation,
                                                     g11nArgs).localize();
        String body = (String) GlobalizationUtil.globalize("cms.ui.workflow.email.body." + operation,
                                                           g11nArgs).localize();
        Message msg = new Message(sender, subject, body);
        msg.save();
        return msg;
    }

    /**
     * Creates a deep copy of this task and stores a persistent copy.
     * TODO: refactor this method in this class, and its parents
     * */
    public Object clone() {
        CMSTask taskClone = new CMSTask();
        copyAttributes(taskClone);
        return taskClone;
    }

    /**
     * exports the attributes of this domain object.
     *
     * @param task DomainObject to which this method copies the
     * attributes of this object
     * */
    protected void copyAttributes(CMSTask task) {
        super.copyAttributes(task);
        task.setTaskType(getTaskType());
    }


    /**
     * Get the ContentSection of the ContentItem that this
     * Task is associated with.  If an item is moved between
     * ContentSections, that code must be responsible for updating
     * all of the assigned users and groups to members of the
     * new ContentSection.
     **/
    public ContentSection getContentSection() {
        ContentItem item = getItem();
        if (item != null) {
            return item.getContentSection();
        } else {
            return null;
        }
    }

    /**
     * Send a notification that this task has been unfinished for too long.
     * Should only ever be called by the UnfinishedTaskNotifier.
     **/
    protected void sendUnfinishedNotification() {
        Party sender = getNotificationSender();
        if (sender != null) {
            // we don't need to check any other flags before we send
            // the message, because this method is only called by the
            // UnfinishedTaskNotifier TimerTask, and that task will never
            // be scheduled unless we're supposed to send these messages
            Message msg = generateMessage(UNFINISHED_OP, sender);
            m_authorOnly = false;
            sendMessageToAssignees(msg);
        }
    }

    public static void addAlert(ContentSection section, 
                                String typeLabel, String operation) {
        if (section == null ||
            typeLabel == null ||
            operation == null) {
            return;
        }
        // get rid of any extra spaces
        typeLabel = typeLabel.trim();
        operation = operation.trim();
        // get the typeLabel -> list-of-operations map for this section
        Map typeMap = (Map) s_alerts.get(section.getID());
        if (typeMap == null) {
            typeMap = new HashMap(5);
            s_alerts.put(section.getID(), typeMap);
        }
        Map operations = (Map) typeMap.get(typeLabel);
        if (operations == null) {
        	operations = new HashMap(2);
        	typeMap.put(typeLabel,operations);
        }
        Set operationSet = (Set) operations.get(ALERT_OPERATIONS);
        if (operationSet == null) {
            operationSet = new HashSet(5);
            operations.put(ALERT_OPERATIONS, operationSet);
        }
        Set authorOnlySet = (Set) operations.get(ALERT_RECIPIENTS);
        if (authorOnlySet == null) {
        	authorOnlySet = new HashSet(5);
            operations.put(ALERT_RECIPIENTS, authorOnlySet);
        }
        // sufix _LASTAUTHOR to send the alert only to auditing.lastModifiedUser
        // default if _ALL - send alert to all task assignees
        String recipients = ALERT_RECIPIENT_ALL;
        if (operation.endsWith(ALERT_RECIPIENT_LASTAUTHOR)) {
        	operation = operation.substring(0,operation.length() - ALERT_RECIPIENT_LASTAUTHOR.length());
        	authorOnlySet.add(operation);
        	recipients = ALERT_RECIPIENT_LASTAUTHOR;
        } else if (operation.endsWith(ALERT_RECIPIENT_ALL)) {
        	operation = operation.substring(0,operation.length() - ALERT_RECIPIENT_ALL.length());
        }
        operationSet.add(operation);

        s_log.info("Added alert for \"" + operation + "\" of " + typeLabel +
                   " task in section \"" + section.getName() + "\" recipients flag: "+recipients);
    }

    protected static boolean shouldSendAlert(ContentSection section,
            String typeLabel,
            String operation) {
    	return checkAlertsConfig(section, typeLabel, operation, ALERT_OPERATIONS); 
    }

    protected static boolean shouldSendToAuthorOnly(ContentSection section,
            String typeLabel,
            String operation) {
    	return checkAlertsConfig(section, typeLabel, operation, ALERT_RECIPIENTS); 
    }

    private static boolean checkAlertsConfig(ContentSection section,
                                             String typeLabel,
                                             String operation,
                                             String field) {
        if (section == null ||
            typeLabel == null ||
            operation == null) {
            // if any of these values are null, we won't be able to
            // check if we're supposed to send the alert; default
            // to no
            return false;
        }
        // get rid of any extra spaces
        typeLabel = typeLabel.trim();
        operation = operation.trim();
        boolean send = false;
        Map typeMap = (Map) s_alerts.get(section.getID());
        Set operationSet = null;
        if (typeMap != null) {
        	Map operations = (Map) typeMap.get(typeLabel);
        	if (operations != null) {
                operationSet = (Set) operations.get(field);        		
        	}
        }
        if (operationSet != null) {
            send = operationSet.contains(operation);
        }
        s_log.debug("operation " + operation + " field " + field + " of task " + typeLabel + "?: " + send);
        return send;
    }

    protected boolean sendAlerts(String operation) {
    	ContentSection section = getContentSection();
    	String label = getLabel();
    	m_authorOnly = shouldSendToAuthorOnly(section, label, operation);
        return (super.sendAlerts(operation) &&
                shouldSendAlert(section, label, operation));
    }

    /**
     * Send a message to all assignees which
     * <ul><li>are in the appropriate role(s) <em>and</em>
     *     <li>have appropriate permissions on the item
     * </ul> <strong>or</strong>
     * <ul><li>have been assigned to the task directly <em>and</em>
     *     <li>have appropriate permissions on the item
     * </ul>
     * Overrides {@link UserTask#sendMessageToAssignees(Message)}
     *
     * @param msg the message
     * @see com.arsdigita.messaging.Message
     * @see #filterUsersAndSendMessage
     */
    protected void sendMessageToAssignees(Message msg) {
    	if (m_authorOnly) {
    		ContentItem item = getItem();
            User author = null;
    		// XXX lastModifiedUser in audit trail is overwritten on each save  
            // author = item.getLastModifiedUser();
            // workaround: use the latest history record with 'Authored' tag 
            TransactionCollection hist = Versions.getTaggedTransactions(item.getOID());
            while (author == null && hist.next()) {
                Transaction txn = hist.getTransaction();
                TagCollection tags = txn.getTags();
                while (tags.next()) {
                    String tag = tags.getTag().getDescription();
                    if ("Authored".equals(tag)) {
                        author = txn.getUser();
                        if (s_log.isDebugEnabled()) {
                            s_log.debug("author from hist="+author+" at "+txn.getTimestamp());    
                        }
                    }
                }
            }
			// bugfix - if author is null above then we break out 
			// of loop early. If normal exit and so cursor has already closed then the 
			// next line has no effect
			hist.close();
    		if (author == null) {
    			// fallback: creator is always available in audit trail 
    			author = item.getCreationUser();
    		}
    		if (s_log.isDebugEnabled()) {
    			s_log.debug("spamming ONLY author " + author);
    		}
    		Notification notification = new Notification(author, msg);
			if (ContentSection.getConfig().deleteWorkflowNotifications()) {
				notification.setIsPermanent(Boolean.FALSE);
	        	// true is set as default column value in DB for all
	        	// notifications, but set explicitly here in case that 
	        	// changes
				notification.setMessageDelete(Boolean.TRUE);
	        }
            notification.save();
    		return;
    	}
        /* NOTE:
         * it would be cleaner to simply change getAssignedUsers()
         * to do what we want; however that is used by cms.ui.workflow.UserTaskComponent
         * and I didn't want to break that.
         * Plus the API doesn't state exactly what getAssignedUsers()
         * is supposed to return, so I decided to leave it alone.
         */
        UserCollection uc = null;

        // (1) spam users
        uc = new UserCollection(getAssignedUserAssociation().cursor());
        s_log.debug("spamming users...");
        filterUsersAndSendMessage( uc, msg );

        // (2) resolve all assigned groups, and spam the resulting set of users
        Iterator itr = getAssignedGroups();
		List groups = new ArrayList();
        Group tmpGroup = null;
        while (itr.hasNext()) {
            tmpGroup = (Group)itr.next();
			groups.add(tmpGroup.getID());
		     
		}
		s_log.debug("spamming groups...");
        
		if (groups.size() > 0) {
        
			uc = User.retrieveAll();
        
			uc.addFilter("allGroups in :assignedGroups").set("assignedGroups", groups);
       
			filterUsersAndSendMessage(uc, msg);
		}
		/*
		 * 
		 cg - update this to retrieve all users in one query - this 
		 is more efficient, and also it is easy to set up task
		 assignment and permissions in such a way that people receive 
		 2 emails with this arrangement. New implementation only gets
		 user once (unless the task has been assigned to user explicitly
		 AND they're in a group to which the task is assigned.  
		  
          
		 while (itr.hasNext()) {
			tmpGroup = (Group)itr.next();
            uc = tmpGroup.getAllMemberUsers();
            if (s_log.isDebugEnabled()) {
                s_log.debug("group " + tmpGroup + " has " + uc.size()
                            + " user members");
            }
            filterUsersAndSendMessage(uc, msg);
		}*/
    }

    /**
     * Filter the <tt>UserCollection</tt> given as the argument against 
     * <tt>PermissionService.EDIT</tt> permission on {@link #getItem() the item}.
     * Send the <tt>Message msg</tt> to the resulting set of users.
     *<p>This method is used by {@link #sendMessageToAssignees} to restrict the
     *  amount of users that are getting spammed by workflow associations.</p>
     *
     *@param uc collection of users which will be filtered and used as the set
     *  of receivers for the given message <tt>msg</tt>
     *@param msg the <tt>Message</tt> to send
     *@see #sendMessageToAssignees
     */
    protected void filterUsersAndSendMessage(UserCollection uc, Message msg) {
        CMSTaskType type = getTaskType();
		PrivilegeDescriptor pd = type.getPrivilege();

        Filter pFilter = PermissionService
            .getObjectFilterQuery(uc.getFilterFactory(),
                                  "id",
                                  pd,
                                  getItem().getOID());
        uc.addFilter(pFilter);

        Notification notification = null;
        //
        // if workflows, roles and folder permissions are set up well, there is
        // no possibility of duplicated emails. In practice it is easy to 
        // accidentally cause duplications. Running users through a set gets
        // rid of duplicates. We don't cater for situations where a user 
        // is assigned a task due to group membership and is also assigned the 
        // task individually. This will cause duplication, but there is 
        // only so much we can do - cg 
		Set spamVictims = new HashSet();

        while (uc.next()) {
        	spamVictims.add(uc.getUser());
        }
        Iterator spamIt = spamVictims.iterator();
        while (spamIt.hasNext()) {
        
        	User user = (User)spamIt.next();
            if (s_log.isDebugEnabled()) {
                s_log.debug("spamming user " + user);
            }
            notification = new Notification(user, msg);
            if (ContentSection.getConfig().deleteWorkflowNotifications()) {
            	notification.setIsPermanent(Boolean.FALSE);
            	// true is set as default column value in DB for all
            	// notifications, but set explicitly here in case that 
            	// changes
            	notification.setMessageDelete(Boolean.TRUE);
            }
            notification.save();
        }
    }

    public void finish() throws TaskException {
        super.finish();
        Integer type = getTaskType().getID();
        ContentItem item = getItem();
        if (type.equals(CMSTaskType.AUTHOR)) {
            item.applyTag("Authored");
        } else if (type.equals(CMSTaskType.EDIT)) {
            item.applyTag("Edited");
        } else if (type.equals(CMSTaskType.DEPLOY)) {
            item.applyTag("Deployed");
        } else {
            throw new IllegalStateException
                ("unknown task type: " + type);
        }
    }
}
