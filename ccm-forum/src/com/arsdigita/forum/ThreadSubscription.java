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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.notification.Notification;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;

/**
 * <font color="red">Experimental</font>
 * Class for managing subscriptions to individual threads in a Forum.
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 */
public class ThreadSubscription extends Subscription {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.forum.ThreadSubscription";

    private static final String THREAD = "thread";

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

    public static DomainCollection getSubsForUser(Party party) {
        DataQuery subs = SessionManager.getSession()
            .retrieveQuery("com.arsdigita.forum.getUserThreadSubscriptions");

        subs.setParameter("userID", party.getID());

        return new DomainCollection(new DataQueryDataCollectionAdapter(subs, "subscription")) {
                public DomainObject getDomainObject() {
                    return new ThreadSubscription(m_dataCollection.getDataObject());
                }
            };
    }

    /**
     * Returns a signature with information about replying to the
     * message
     */
    public String getSignature(Post post) {

        return SEPARATOR
            + ALERT_BLURB
            + "You are receiving this email because you subscribed to "
            + "alerts on this thread.\n\n"
            + REPLY_BLURB
            + getReturnURLMessage(post);
    }
}
