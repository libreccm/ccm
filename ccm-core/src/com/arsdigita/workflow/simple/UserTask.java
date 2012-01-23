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
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.messaging.Message;
import com.arsdigita.notification.Notification;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * User task that is associated with an interface for performing some manual
 * operation.
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Khy Huang
 * @author Stefan Deusch
 * @version $Id: UserTask.java 1564 2007-04-18 16:15:27Z apevec $
 *
 */
public class UserTask extends Task implements Assignable {

    /**
     * Private logger instance for log4j.
     */
    private static final Logger s_log = Logger.getLogger(UserTask.class);
    /**
     * Private configuration object, singleton design pattern
     */
    private static final WorkflowConfig s_conf = WorkflowConfig.getInstance();
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.workflow.simple.UserTask";
    public static final String ASSIGNED_USERS = "assignedUsers";
    public static final String ASSIGNED_GROUPS = "assignedGroups";
    public static final String LOCKING_USER_ID = "lockingUserId";
    public static final String IS_LOCKED = "isLocked";
    public static final String DUE_DATE = "dueDate";
    public static final String START_DATE = "startDate";
    public static final String DURATION_MINUTES = "durationMinutes";
    public static final String NOTIFICATION_SENDER_ID = "notificationSenderID";
    public static final int DEFAULT_DURATION = 1440;
    private User m_userLock; // Used to lock process
    private HashSet m_assignedUsers;
    private HashSet m_assignedGroups;
    private User m_finished_user;
    private Party m_notificationSender; // email alerts are from this sender
    // identify the different operations a Task can undergo
    public static final String ENABLE_OP = "enable";
    public static final String DISABLE_OP = "disable";
    public static final String ROLLBACK_OP = "rollback";
    public static final String FINISH_OP = "finish";

    /**
     * Constructor for a user task with usage information.
     *
     * @param label the task label
     * @param description the task description
     * @param is_active whether the task is in use
     * @param duration_minutes the projected duration of the task in minutes
     *
     */
    public UserTask(String label, String description,
                    boolean is_active, int duration_minutes) {
        this(label, description);
        setDuration(new Duration(0, 0, duration_minutes));
    }

    /**
     * Constructor for a user task without runtime information.
     *
     * @param label the task definition label
     * @param description the description
     *
     */
    public UserTask(String label, String description) {
        this(BASE_DATA_OBJECT_TYPE);
        setDuration(new Duration(DEFAULT_DURATION));
        initAttributes(label, description);
    }

    /**
     * Creates a new task definition and sets the properties
     * <code>label</code> and
     * <code>description</code> to null. The properties of this object are not
     * made persistent until the
     * <code>save</code> method is called. If save() is called without setting
     * these properties, an IllegalArgumentException will be thrown.
     *
     *
     */
    public UserTask() {
        this(BASE_DATA_OBJECT_TYPE);
        setDuration(new Duration(0));
    }

    /**
     * Constructor for a user task that is used for setting the object type.
     *
     * @param type the object type
     *
     *
     */
    protected UserTask(ObjectType type) {
        super(type);
    }

    /**
     * Constructor for user task that is used for setting the object type by
     * name.
     *
     * @param typeName the type name
     *
     *
     */
    protected UserTask(String typeName) {
        super(typeName);
        setDuration(new Duration(0, 0, 0));
    }

    /**
     * Constructor for restoring a user task from a data object.
     *
     * @param userTaskObject the data object
     *
     *
     */
    public UserTask(DataObject userTaskObject) {
        super(userTaskObject);
    }

    /**
     * Restores a user task definition from an OID.
     *
     * @param oid the user task OID
     *
     *
     */
    public UserTask(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Restores a task definition from an OID as BigDecimal.
     *
     * @param id the user task ID as BigDecimal
     *
     *
     */
    public UserTask(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Returns the OID of this user task.
     *
     * @return user task OID.
     * @see com.arsdigita.persistence.OID
     *
     */
    public OID getUserTaskOID() {
        return new OID(getBaseDataObjectType(), getID());
    }

    /**
     * Retrieves the start date of this user task.
     *
     * @return the start date of this user task.
     *
     *
     */
    public Date getStartDate() {
        return (Date) get(START_DATE);
    }

    /**
     * Retrieves the due date of this user task.
     *
     * @return the due date of this user task.
     *
     *
     */
    public Date getDueDate() {
        return (Date) get(DUE_DATE);
    }

    /**
     * Sets the start date for a user task.
     *
     * @param startDate the date the task is supposed to start
     *
     *
     */
    public void setStartDate(Date startDate) {
        set(START_DATE, startDate);
    }

    /**
     * Sets the duration for this user task. Updates the start date and due date
     * accordingly.
     *
     * @param duration the duration for this task
     *
     *
     */
    private void setDuration(Duration duration) {
        setStartDate(duration.getStartDate());
        setDueDate(duration.getDueDate());
        set(DURATION_MINUTES, new BigDecimal(duration.getDuration()));
    }

    /**
     * Returns the duration attribute for this user task.
     *
     * @return the duration for this user task.
     *
     */
    public Duration getDuration() {
        BigDecimal minutes = (BigDecimal) get(DURATION_MINUTES);

        if (minutes == null) {
            minutes = new BigDecimal(0);
        }

        return new Duration(minutes.intValue());
    }

    /**
     * Sets the due date of the user task.
     *
     * @param dueDate the imposed due date of the user task
     *
     *
     */
    public void setDueDate(Date dueDate) {
        set(DUE_DATE, dueDate);
    }

    /**
     * Marks the task as finished. (persistent operation) <P>This operation is
     * only valid if the task is enabled. Only the user who previously locked
     * the task can call this method.
     *
     * @param user the user who checks off the task
     *
     *
     */
    public void finish(User user) throws TaskException {

        if (isLocked()) {
            if (!getLockedUser().equals(user)) {
                // SF patch [ 1587168 ] Show locking user
                String currentUserName = (user == null ? "(unknown)" : user.
                                          getName());
                String lockedUserName = (getLockedUser() == null ? "(unknown)"
                                         : getLockedUser().getName());
                throw new TaskException(currentUserName
                                        + " is not Locking User, task locked by "
                                        + lockedUserName);
            }
        }
        m_finished_user = user;
        finish();
    }

    /**
     * Set the startDate to the current date, carrying the duration over from
     * the current value.
     *
     */
    public void enable() {
        // Create a new Duration (whose start date is now)
        // with the specified duration.
        setDuration(new Duration(getDuration().getDuration()));
        super.enable();
    }

    /**
     * Enables an event action. Sends out notification to assigned users.
     *
     *
     */
    public void enableEvt() {
        Party sender = getNotificationSender();

        if (sendAlerts(ENABLE_OP) && sender != null) {
            Message msg = generateMessage(ENABLE_OP, sender);
            sendMessageToAssignees(msg);
        }
        super.enableEvt();
    }

    /**
     * Sends email to assignees with information about who completed the task
     * and when.
     *
     *
     */
    protected void finishEvt() {
        User current = null;
        Party party = Kernel.getContext().getParty();

        if (party == null) {
            return;
        }

        try {
            current = User.retrieve(party.getOID());
        } catch (DataObjectNotFoundException e) {
            throw new UncheckedWrapperException(e);
        }

        unlock(current);

        Party sender = getNotificationSender();
        if (sendAlerts(FINISH_OP) && sender != null) {
            Message msg = generateMessage(FINISH_OP, sender);
            if (s_log.isDebugEnabled()) {
                s_log.debug("Sending alert message with body " + msg.getBody()
                            + " for finishEvt.");
            }
            sendMessageToAssignees(msg);
        }
        super.finishEvt();
    }

    /**
     * Sends email to subscribed events that this task was rolled back.
     *
     *
     */
    protected void rollbackEvt() {
        Party sender = getNotificationSender();
        if (sendAlerts(ROLLBACK_OP) && sender != null) {
            Message msg = generateMessage(ROLLBACK_OP, sender);
            sendMessageToAssignees(msg);
        }
        super.rollbackEvt();
    }

    /**
     * Sends email that this task has been disabled.
     *
     *
     */
    protected void disableEvt() {
        Party sender = getNotificationSender();
        if (sendAlerts(DISABLE_OP) && sender != null) {
            Message msg = generateMessage(DISABLE_OP, sender);
            sendMessageToAssignees(msg);
        }
        super.disableEvt();
    }

    /**
     * Generate the message to send if for the specified event
     *
     */
    protected Message generateMessage(String operation, Party sender) {
        String subject = null;
        String body = null;
        if (ENABLE_OP.equals(operation)) {
            subject = getLabel() + "is in ready state.";
            body = getLabel() + " moved to ready state from disabled on "
                   + (new Date());
        } else if (DISABLE_OP.equals(operation)) {
            subject = getLabel() + " moved to disable state.";
            body = getLabel() + " was moved to disable state from ready on "
                   + (new Date());
        } else if (ROLLBACK_OP.equals(operation)) {
            subject = getLabel() + " moved to disable state.";
            body = getLabel() + " moved to disable state from finished on "
                   + (new Date());
        } else if (FINISH_OP.equals(operation)) {
            subject = getLabel() + "was finished.";
            body = getLabel() + " completed on " + (new Date()) + (m_finished_user
                                                                   != null ? "by: "
                                                                             + m_finished_user.
                                                                   getName()
                                                                   : "");
        } else {
            throw new IllegalArgumentException("Invalid workflow operation: "
                                               + operation);
        }

        body += "\nDescription: " + getDescription() + "\n";
        Message msg = new Message(sender, subject, body);
        msg.save();
        return msg;
    }

    /**
     * Internal sends a message to all assignees of this task.
     *
     * @param msg the message
     * @see com.arsdigita.messaging.Message
     */
    protected void sendMessageToAssignees(Message msg) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Sending message: " + msg.getBody()
                        + " to all assignees.");
        }

        Iterator itr = getAssignedUsers();
        Party tempParty = null;
        Notification notification = null;
        while (itr.hasNext()) {
            tempParty = (Party) itr.next();
            notification = new Notification(tempParty, msg);

            if (s_log.isDebugEnabled()) {
                s_log.debug("Sending message to user " + tempParty.
                        getDisplayName());
            }

            notification.save();
        }
        itr = getAssignedGroups();
        while (itr.hasNext()) {
            tempParty = (Party) itr.next();

            if (s_log.isDebugEnabled()) {
                s_log.debug("Sending message to group " + tempParty.
                        getDisplayName());
            }

            notification = new Notification(tempParty, msg);
            notification.save();
        }
    }

    /**
     * Marks the task as finished with an additional comment. (persistent
     * operation)
     *
     * @param user the user checking off the task as finished
     * @param comment a comment
     *
     *
     */
    public void finish(User user, String comment) throws TaskException {
        finish();
        addComment(user, comment);
    }

    /**
     * Locks the task for finishing by a specified user. (persistent operation)
     *
     * @param user the user who is locking the task
     *
     */
    public void lock(User user) {

        m_userLock = user;
        set(LOCKING_USER_ID, user.getID());
        set(IS_LOCKED, "t");
    }

    /**
     * Releases the lock on the task if it is currently locked. (persistent
     * operation)
     *
     * @param user the user who is unlocking the task
     *
     *
     */
    public void unlock(User user) {
        m_userLock = null;
        set(LOCKING_USER_ID, null);
        set(IS_LOCKED, "f");
    }

    /**
     * Checks whether the task is locked by a user.
     *
     * @return
     * <code>true</code> if the task is locked by a user;
     * <code>false</code> otherwise.
     *
     *
     */
    public boolean isLocked() {
        return (getLockedUser() != null);
    }

    /**
     * Retrieves the user who locked the process.
     *
     * @return the user who locked the process.
     *
     */
    public User getLockedUser() {
        if (m_userLock == null) {

            BigDecimal userID = (BigDecimal) get(LOCKING_USER_ID);
            try {
                if (userID != null) {
                    m_userLock = User.retrieve(userID);
                }
            } catch (DataObjectNotFoundException e) {
                m_userLock = null;
            }
        }
        return m_userLock;
    }

    /**
     * Assigns a user to this task. You must use the
     * <code>save</code> method to make it persistant.
     *
     * @param user an active user of the system
     *
     */
    public void assignUser(User user) {
        Collection users = getInternalAssignedUsers();
        if (!users.contains(user)) {
            add(ASSIGNED_USERS, user);
            users.add(user);
            //user.addToAssociation(getAssignedUserAssociation());
        }
    }

    /**
     * Assigns a group of users to this task. Use the
     * <code>save</code> method to make it persistent.
     *
     * @param group the group to assign
     *
     */
    public void assignGroup(Group group) {
        add(ASSIGNED_GROUPS, group);
        Collection groups = getInternalAssignedGroups();
        groups.add(group);
        //group.addToAssociation(getAssignedGroupAssociation());
    }

    /**
     * Removes a user from the assignment list.
     *
     * @param user the user to be removed
     *
     */
    public void removeUser(User user) {
        Collection users = getInternalAssignedUsers();
        users.remove(user);
        user.removeFromAssociation(getAssignedUserAssociation());
    }

    /**
     * Removes a group from assignment list.
     *
     * @param group the group to be removed
     *
     */
    public void removeGroup(Group group) {
        Collection groups = getInternalAssignedGroups();
        groups.remove(group);
        group.removeFromAssociation(getAssignedGroupAssociation());
    }

    /**
     * Removes all groups assigned to this task.
     *
     */
    public void removeAllGroupAssignees() {
        Collection groups = getInternalAssignedGroups();
        Iterator i = groups.iterator();
        Group g;

        while (i.hasNext()) {
            g = (Group) i.next();
            g.removeFromAssociation(getAssignedGroupAssociation());
        }
        groups.clear();
    }

    /**
     * Tests whether any user or group is assigned to this task.
     *
     * @return
     * <code>true</code> if a user or a group is assigned;
     * <code>false</code> otherwise.
     *
     *
     */
    public boolean isAssigned() {
        return ((getAssignedUserCount() > 0) || (getAssignedGroupCount() > 0));
    }

    /**
     * Tests whether a specificv user is assigned to this task.
     *
     * @param user a system user
     * @return
     * <code>true</code> if the user is assigned to this task;
     * <code>false</code> otherwise.
     *
     */
    public boolean isAssigned(User user) {
        Collection users = getInternalAssignedUsers();
        Iterator userItr = users.iterator();
        User tmpUser = null;

        while (userItr.hasNext()) {
            tmpUser = (User) userItr.next();
            if (tmpUser == null) {
                return false;
            }
            if ((tmpUser.getID()).equals(user.getID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether a specific group is assigned to this task.
     *
     * @param group a system group
     * @return
     * <code>true</code> if the group is actually assigned to this task;
     * <code>false</code> otherwise.
     *
     */
    public boolean isAssigned(Group group) {
        Collection groups = getInternalAssignedGroups();
        Iterator groupItr = groups.iterator();
        Group tmpGroup = null;

        while (groupItr.hasNext()) {
            tmpGroup = (Group) groupItr.next();
            if (tmpGroup == null) {
                return false;
            }
            if ((tmpGroup.getID()).equals(group.getID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the number of assigned users.
     *
     * @return the number of assigned users.
     *
     */
    public int getAssignedUserCount() {
        return getInternalAssignedUsers().size();
    }

    /**
     * Gets number of assigned groups.
     *
     * @return the number of assigned groups.
     *
     */
    public int getAssignedGroupCount() {
        return getInternalAssignedGroups().size();
    }

    /**
     * Returns all assigned users.
     *
     * @return an iterator over all assigned users.
     *
     */
    public Iterator getAssignedUsers() {
        return getInternalAssignedUsers().iterator();
    }

    /**
     * Return the internal Collection of users assigned to this task.
     *
     * @return a Collection of all assigned users
     * @see java.util.Collection
     *
     */
    private Collection getInternalAssignedUsers() {
        if (m_assignedUsers == null) {
            UserCollection uc = new UserCollection(getAssignedUserAssociation().
                    cursor());
            m_assignedUsers = new HashSet();

            while (uc.next()) {
                m_assignedUsers.add(uc.getDomainObject());
            }
        }
        return m_assignedUsers;
    }

    /**
     * Return the internal Collection of groups assigned to this task.
     *
     * @return a Collection of all assigned groups
     * @see java.util.Collection
     *
     */
    private Collection getInternalAssignedGroups() {
        if (m_assignedGroups == null) {
            m_assignedGroups = new HashSet();

            GroupCollection gc =
                            new GroupCollection(getAssignedGroupAssociation().
                    cursor()) {
            };
            while (gc.next()) {
                m_assignedGroups.add(gc.getDomainObject());
            }
        }
        return m_assignedGroups;
    }

    /**
     * Returns the assigned groups.
     *
     * @return an iterator over all assigned groups.
     *
     */
    public Iterator getAssignedGroups() {
        return getInternalAssignedGroups().iterator();
    }

    /**
     * Retrieve the assigned users datacollection.
     *
     * @return the assigned user data collection
     *
     */
    protected DataAssociation getAssignedUserAssociation() {
        return (DataAssociation) get(ASSIGNED_USERS);
    }

    public final UserCollection getAssignedUserCollection() {
        return new UserCollection(getAssignedUserAssociation().cursor());
    }

    /**
     * Retrieve the assigned group data collection.
     *
     * @return the assigned group data collection.
     *
     */
    protected DataAssociation getAssignedGroupAssociation() {
        return (DataAssociation) get(ASSIGNED_GROUPS);
    }

    public final GroupCollection getAssignedGroupCollection() {
        return new GroupCollection(getAssignedGroupAssociation().cursor());
    }

    /**
     * Removes all assigned users from this task.
     *
     *
     */
    private void clearUser() {
        Collection users = getInternalAssignedUsers();
        Collection users2 = new HashSet();

        Iterator itr = users.iterator();
        while (itr.hasNext()) {
            users2.add(itr.next());
        }

        itr = users2.iterator();
        while (itr.hasNext()) {
            removeUser((User) itr.next());
        }
    }

    /**
     * Removes all assigned groups from this task.
     *
     *
     */
    private void clearGroup() {
        Collection groups = getInternalAssignedGroups();
        HashSet groups2 = new HashSet();

        Iterator itr = groups.iterator();
        while (itr.hasNext()) {
            groups2.add(itr.next());
        }

        itr = groups2.iterator();
        while (itr.hasNext()) {
            removeGroup((Group) itr.next());
        }

    }

    /**
     * Deletes this user task. (persistent operation).
     */
    public void delete() {
        if (getAssignedUserCount() > 0) {
            clearUser();
        }
        if (getAssignedGroupCount() > 0) {
            clearGroup();
        }
        super.delete();
    }

    /**
     * Tests whether the task is overdue.
     *
     * @return
     * <code>true</code> if the task is overdue;
     * <code>false</code> otherwise.
     *
     *
     */
    public boolean isOverdue() {
        Date now = new Date();
        Date dueDate = getDueDate();
        if (dueDate == null) {
            return false;
        }
        return (dueDate.getTime() < now.getTime());
    }

    /**
     * Specifies who is sending out the notification.
     *
     * @param party the sender in email messages
     * @return the previous sender if one exists otherwise null
     *
     *
     */
    public Party setNotificationSender(Party party) {
        Party previousSender = getInternalNotificationSender();
        setInternalNotificationSender(party);
        return previousSender;
    }

    /**
     * Returns the notification sender.
     *
     * @return the notification sender.
     *
     *
     */
    public Party getNotificationSender() {
        return getInternalNotificationSender();
    }

    /**
     * Method used internally to set the notification sender in the persistence
     * layer.
     *
     * @param party the party sending the notification
     */
    private void setInternalNotificationSender(Party party) {
        if (party == null) {
            set(NOTIFICATION_SENDER_ID, null);
        } else {
            set(NOTIFICATION_SENDER_ID, party.getID());
            m_notificationSender = party;
        }
    }

    /**
     * Method used internally to get the notification sender in the persistence
     * layer.
     *
     * @return party, party sending out the notification.
     *
     */
    private Party getInternalNotificationSender() {
        if (m_notificationSender == null) {
            final BigDecimal senderID =
                             (BigDecimal) get(NOTIFICATION_SENDER_ID);

            if (senderID == null) {
                return null;
            }

            try {
                m_notificationSender =
                (Party) DomainObjectFactory.newInstance(new OID(
                        Party.BASE_DATA_OBJECT_TYPE, senderID));

                Assert.exists(m_notificationSender, "Party m_notificationSender");
            } catch (DataObjectNotFoundException e) {
                throw new UncheckedWrapperException(
                        "Error restoring notification sender",
                                                    e);
            }
        }

        return m_notificationSender;
    }

    /**
     * Clones a user task. Deep cloning (except the primary key). Clones class
     * and db-row The cloned copy is saved to persistent storage before
     * returning.
     *
     * @return a clone of the user task definition.
     *
     *
     */
    public Object clone() {
        UserTask taskClone = new UserTask();
        copyAttributes(taskClone);
        return taskClone;
    }

    /**
     * Exports the attributes of this domain object.
     *
     * @param task the domain object to which this method copies the attributes
     * of this object
     *
     */
    protected void copyAttributes(UserTask task) {
        super.copyAttributes(task);
        task.setDuration(new Duration(0, 0, getDuration().getDuration()));

        Collection assignedUsers = getInternalAssignedUsers();
        User user;
        Iterator userItr = assignedUsers.iterator();

        while (userItr.hasNext()) {
            user = (User) userItr.next();
            task.assignUser(user);
        }

        Collection assignedGroups = getInternalAssignedGroups();
        Group group;
        Iterator groupItr = assignedGroups.iterator();

        while (groupItr.hasNext()) {
            group = (Group) groupItr.next();
            task.assignGroup(group);
        }

        // this isn't strictly cloning, but we'd like to be
        // able to change the notification sender simply by changing
        // enterprise.init
        task.setNotificationSender(getAlertsSender());
    }

    public static Party getAlertsSender() {
        String email = s_conf.getAlertsSender();
        if (email == null) {
            return null;
        }
        PartyCollection parties = Party.retrieveAllParties();
        parties.addEqualsFilter("primaryEmail", email.toLowerCase());
        try {
            if (parties.next()) {
                return parties.getParty();
            } else {
                return null;
            }
        } finally {
            parties.close();
        }
    }

    /**
     * Whether or not to send alerts. Subclasses can override this to send
     * alerts based on different criteria, but they should always check
     * super.sendAlerts() as well.
     *
     */
    protected boolean sendAlerts(String operation) {
        return s_conf.isAlertsEnabled();
    }
}
