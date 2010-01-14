/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.notification;

import com.arsdigita.messaging.ThreadedMessage;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * The abstract BaseSubscription class provides the ability for Users to sign up
 * for email notifications. Subclasses will specify the object to which the
 * notifications apply and messages that should be sent along with the
 * notifications.
 *
 * The default implementation provides instant notifications.  Subclasses
 * should override sendNotification() to alter this behavior.
 *
 * This is abstracted out from Subscription to allow for greated reusability
 * (that is, it no longer assumes it's dealing with a Post).
 *
 * @author Kevin Scaldeferri 
 */
public abstract class BaseSubscription extends ACSObject {

    private static final String GROUP = "group";

    private static final Logger s_log 
        = Logger.getLogger(BaseSubscription.class);

    private Group m_group = null;

    /**
     * A separator to use between the body and signature of an alert.
     */
    protected final static String SEPARATOR =
        getSeparator();

    protected final static String ALERT_BLURB =
        "This is an automated alert from the Discussion Forum system. ";
    protected final static String REPLY_BLURB =
        "Replying to this email will add a post to the forum. It will " +
        "not send email directly to the person who posted this message.\n\n";

    /**
     * Constructor.
     */

    public BaseSubscription(String objectType) {
        super(objectType);
        setupSubscriptionGroup();
    }

    protected void setupSubscriptionGroup() {
        Group group = new Group();
        group.setName(getSubscriptionGroupName());
        Group parentGroup = getParentGroup();
        if (parentGroup != null) {
        	parentGroup.addSubgroup(group);
        
        }
        setGroup(group);
    }

    protected Group getParentGroup () {
    	return null;
    }
    
    /**
     * Default implementation, returns a generic group name.
     * 
     * Subtypes should override this method to provide a meaningful name.
     * @return
     */
    protected String getSubscriptionGroupName() {
    	return "Bboard subscription group";
    }

    public BaseSubscription(DataObject dataObj) {
        super(dataObj);
    }

    public BaseSubscription(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * returns the Group of people who are subscribed
     */
    public Group getGroup() {
        if (m_group == null) {
            DataObject groupData = (DataObject) get(GROUP);
            if (groupData != null) {
                m_group = new Group(groupData);
            }
        }
        return m_group;
    }

    protected void setGroup(Group group) {
        m_group = group;
        setAssociation(GROUP, group);
    }

    public void setGroupName(String name) {
        getGroup().setName(name);
    }

    public final void subscribe(Party party) {
        getGroup().addMemberOrSubgroup(party);
    }

    public final void unsubscribe(Party party) {
        getGroup().removeMemberOrSubgroup(party);
    }

    public final boolean isSubscribed(Party party) {
        return getGroup().hasMember(party);
    }

    /**
     * This method will send immediate notifications to subscribed users with
     * the header and signature specified by getHeader() and getSignature().
     * Subclasses which desire different behavior should override one or more
     * of these methods.
     */
    public void sendNotification(ThreadedMessage post) {
        sendNotification(post, false);
    }

    public void sendNotification(ThreadedMessage post, boolean deleteNotification) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Sending nofication to: " + getGroup().getName());
        }

        Notification note = new Notification(getGroup(), post);
        note.setExpandGroup(new Boolean(true));
        note.setHeader(getHeader(post));
        note.setSignature(getSignature(post));
        // make sure we don't delete the post itself!!!
        note.setMessageDelete(Boolean.FALSE);
        note.setIsPermanent(new Boolean(!deleteNotification));
        note.save();

    }
    /**
     * Returns a header for forum alerts with the following standard
     * information:
     *
     * @return a header to insert at the top of the alert.
     */
    public abstract String getHeader(ThreadedMessage post);

    /**
     * Returns the signature to be appended to the alert.  The default
     * implementation returns a separator and a generic messages.
     */
    public String getSignature(ThreadedMessage post) {
        return SEPARATOR + ALERT_BLURB;
    }

    /**
     * @return an appropriate separator for the body and signature of a post.
     */

    private static String getSeparator() {
        return "\n\n" + StringUtils.repeat('-',20) + "\n\n";
    }
}
