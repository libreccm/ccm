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
import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.notification.Digest;
import com.arsdigita.notification.Notification;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * Extends Subscription to implement digest (periodic) notifications
 * on a forum.
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 *
 * @version $Revision: 1.1 $ $Author: chrisg23 $ $DateTime: 2004/08/17 23:26:27 $
 */

public class DailySubscription extends ForumSubscription {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.forum.ForumSubscription";

    private Digest m_digest;

    private static final String DIGEST = "digest";

    /**
     * @param forum the Forum that is subscribed to
     * @param sender the Party which will appear in the From: header
     */
    public DailySubscription(Forum forum, Party sender) {
        this(BASE_DATA_OBJECT_TYPE, forum, sender);
    }

    /**
     * @param forum the Forum that is subscribed to
     * @param sender the Party which will appear in the From: header
     */
    public DailySubscription(String typeName, Forum forum, Party sender) {
        super(typeName, forum);

        Assert.exists(sender, Party.class);

        Digest digest = null;

        String subject = "Daily digest from " + forum.getDisplayName()
            + " forum";
        digest = new Digest(sender, subject, subject, "-- End digest --");
        digest.setFrequency(Digest.DAILY);

        setDigest(digest);
        setIsModerationAlert(false);
    }

    protected DailySubscription(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    protected DailySubscription(BigDecimal id)
        throws DataObjectNotFoundException {

        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    protected DailySubscription(DataObject data) {
        super(data);
    }

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    public String getSubscriptionGroupName() {
	return "Daily Digest Subscription Group"; 
    }

    private Digest getDigest() {
        if (m_digest == null) {
            DataObject digestData = (DataObject) get(DIGEST);
            if (digestData != null) {
                m_digest = new Digest(digestData);
            }
        }
        return m_digest;
    }

    private void setDigest(Digest digest) {
        m_digest = digest;
        setAssociation(DIGEST, m_digest);
    }

    /**
     * Queues the post for once-daily notification of new posts.
     */
    public void sendNotification(Post post) {
        Notification notification = new Notification(getGroup(), post);
        notification.setDigest(getDigest());
        if (Forum.getConfig().deleteNotifications()) {
//        	 make sure we don't delete the post itself!!!
        	notification.setMessageDelete(Boolean.FALSE);
        	notification.setIsPermanent(Boolean.FALSE);
        }
        notification.save();
    }

    /**
     * @return the DailySubscription associated with the provided Forum
     */
    public static ForumSubscription getFromForum(Forum forum) {
        DataCollection subs = SessionManager.getSession()
            .retrieve(DailySubscription.BASE_DATA_OBJECT_TYPE);
        subs.addEqualsFilter("forum.id", forum.getID());
        subs.addNotEqualsFilter("digest.id", null);

        if (subs.next()) {
            DailySubscription sub =
                new DailySubscription(subs.getDataObject());
            subs.close();
            return sub;
        }
        return null;
    }
}
