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
package com.arsdigita.forum;

import java.math.BigDecimal;

import com.arsdigita.bebop.PageState;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.mail.Mail;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.notification.Notification;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

/**
 * Class for managing subscriptions to individual threads in a Forum.
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 */
public class ThreadSubscription extends Subscription {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.forum.ThreadSubscription";

    public static final String THREAD = "thread";

    private MessageThread m_thread = null;

    public ThreadSubscription() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    public ThreadSubscription(String objectType) {
        super(objectType);
    }

    public ThreadSubscription(DataObject dataObj) {
        super(dataObj);
    }

    public ThreadSubscription(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

	public String getSubscriptionGroupName() {
		// not overridden because group should be based on 
		// thread root post name, but thread hasn't been set when
		// this is called. Group name is updated in setThread method
		return super.getSubscriptionGroupName();
		
	}
	
	public String getSubscriptionGroupName(Forum forum) {
		return forum.getTitle() + ": " + getThreadReal().getRootMessage().getSubject() + " Subscription Group";
	}
	
	protected Group getParentGroup() {
		GroupCollection forumGroups = ((Forum)Kernel.getContext().getResource()).getGroup().getSubgroups();
		forumGroups.addEqualsFilter("name", Forum.THREAD_SUBSCRIPTION_GROUPS_NAME);
		Group parent = null;
		if (forumGroups.next()) {
			parent = forumGroups.getGroup();
			forumGroups.close();
		}
		return parent;
	}
	
    public ThreadSubscription(BigDecimal id)
        throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * @deprecated the return type of this method will soon become
     *             MessageThread
     */
    public Post getThread() {
        return (Post)getThreadReal().getRootMessage();
    }

    public MessageThread getThreadReal() {
        if (m_thread == null) {
            DataObject threadData = (DataObject) get(THREAD);
            if (threadData != null) {
                m_thread = new MessageThread(threadData);
            }
        }
        return m_thread;
    }

    public void doWriteCheck() {
        getThread().assertPrivilege(PrivilegeDescriptor.READ);
    }

    /**
     * @deprecated
     */
    public void setThread(Post post) {
        setThread(post.getThread());
    }

    public void setThread(MessageThread thread) {
        m_thread = thread;
        setAssociation(THREAD, thread);
        getGroup().setName(getSubscriptionGroupName((Forum)Kernel.getContext().getResource()));
    }

    protected void afterSave() {
        PermissionService.setContext(this, getThreadReal());
        PermissionService.setContext(getGroup(), getThreadReal());
        super.afterSave();
    }

    /**
     *  Delete all notifications sent with the sender being the group
     * associated with this subscription.
     */
    protected void beforeDelete() {
        DataCollection notifications = SessionManager.getSession()
            .retrieve(Notification.BASE_DATA_OBJECT_TYPE);
        notifications.addEqualsFilter("partyTo", getGroup().getID());
        while (notifications.next()) {
            Notification no = new Notification(notifications.getDataObject().getOID());
            no.setMessageDelete(Boolean.FALSE);
            no.delete();
        }
        super.beforeDelete();
    }

    /**
     * Retrieves the subscription associated with a thread.  Note:
     * post must be the root of the thread.
     *
     * @deprecated
     */
    public static ThreadSubscription getThreadSubscription(Post post) {
        return ThreadSubscription.getThreadSubscription(post.getThread());
    }

    public static ThreadSubscription getThreadSubscription(
                                                           MessageThread thread) {

        DataCollection subs = SessionManager.getSession().
            retrieve(BASE_DATA_OBJECT_TYPE);

        subs.addEqualsFilter("thread.id", thread.getID());

        ThreadSubscription sub = null;
        if (subs.next()) {
            sub = new ThreadSubscription(subs.getDataObject());
        }

        subs.close();
        return sub;
    }

    public static DomainCollection getSubsForUser(Party party, PageState state) {
        // chris.gilbert@westsussex.gov.uk replace query with standard filtering
        DataCollection subscriptions = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        subscriptions.addEqualsFilter("group.allMembers.id", party.getID());
        
        // currently specified in config, but could be selected from widget on screen
        // ie show thread subscriptions for this forum/all forums
        //
        // Currently, if subscription for a different forum is selected, the thread is displayed within 
        // the context of this forum which is not good. I suspect there is a need to set the forum in the 
        // forum context when thread.jsp is reached. Have not implemented changes as we are only displaying 
        // subscriptions for current forum cg.
        if (!Forum.getConfig().showThreadAlertsForAllForums()){        
        	subscriptions.addEqualsFilter("thread.root.objectID", ForumContext.getContext(state).getForum().getID());
        }    

        return new DomainCollection(subscriptions);

    }

    /**
     * Returns a signature with information about replying to the
     * message
     */
    public String getSignature(ThreadedMessage post) {
		StringBuffer sb = new StringBuffer();
		if (Mail.getConfig().sendHTMLMessageAsHTMLEmail()) {
			sb.append(HTML_SEPARATOR);
			sb.append(getReturnURLMessage((Post)post));
			sb.append(HTML_SEPARATOR);
			sb.append(ALERT_BLURB);
			sb.append("You are receiving this email because you subscribed to ");
			sb.append("alerts on this thread. To unsubscribe, follow the link above and click the 'stop watching thread' link at the top of the page.\n");
        	sb.append("</font>");
		} else {
			sb.append(SEPARATOR);
			sb.append(ALERT_BLURB);
			sb.append("You are receiving this email because you subscribed to ");
			sb.append("alerts on this thread.\n\n");
			sb.append(REPLY_BLURB);
			sb.append(getReturnURLMessage((Post)post));
		}
		  return sb.toString();

       

    }
}
