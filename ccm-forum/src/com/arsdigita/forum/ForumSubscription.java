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
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.mail.Mail;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * The ForumSubscription class provides notification capabilities on a Forum.
 *
 * Note that ForumSubscriptions should not be retrieved directly using the
 * contructors, but rather by calling DomainObjectFactory.newInstance().
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 *
 * @version $Revision: 1.3 $ $Author: chrisg23 $ $DateTime: 2004/08/17 23:26:27 $
 */

public class ForumSubscription extends Subscription {

    private static final Logger s_log =
        Logger.getLogger(ForumSubscription.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.forum.ForumSubscription";
    
    public static final String IS_MODERATION_ALERT =
        "isModerationAlert";
    public static final String FORUM = "forum";

    private Forum m_forum;

    public ForumSubscription(Forum forum) {
        this(BASE_DATA_OBJECT_TYPE, forum);
        setIsModerationAlert(false);
    }

    public ForumSubscription(String typeName, Forum forum) {
        super(typeName);

        Assert.exists(typeName, String.class);
        Assert.exists(forum, Forum.class);

        setForum(forum);
    }

    protected ForumSubscription(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    protected ForumSubscription(BigDecimal id)
        throws DataObjectNotFoundException {

        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    protected ForumSubscription(DataObject data) {
        super(data);
    }

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public String getSubscriptionGroupName() {
	return "Instant Alert Subscription Group";
    }
		
    public Forum getForum() {
        if (m_forum == null) {
            DataObject forumData = (DataObject) get(FORUM);
            if (forumData != null) {
                m_forum = (Forum)DomainObjectFactory.newInstance(forumData);
            }
        }
        return m_forum;
    }

    public void doWriteCheck() {
        getForum().assertPrivilege(PrivilegeDescriptor.READ);
    }

    private void setForum(Forum forum) {
        m_forum = forum;
        setAssociation(FORUM, m_forum);
        if (getGroup() != null) {
            // in the case of moderation alert this is null - good, because
            // mod group has already been placed in the group hierarchy 
   	
            getGroup().setName(getGroupName(forum));
            forum.getGroup().addSubgroup(getGroup());
        }
    }

    public String getGroupName(Forum forum) {
    	return forum.getTitle() + " " + getSubscriptionGroupName();
    }

    protected void afterSave() {
        PermissionService.setContext(this, getForum());
        super.afterSave();
    }

    /**
     * Returns a signature with information about replying to the
     * message.
     */
    public String getSignature(ThreadedMessage post) {
        StringBuffer sb = new StringBuffer();

        if (Mail.getConfig().sendHTMLMessageAsHTMLEmail()) {
       
	    sb.append(HTML_SEPARATOR);
	    sb.append(getReturnURLMessage((Post)post));
	    sb.append(HTML_SEPARATOR);
        sb.append(ALERT_BLURB);
        sb.append("You are receiving this email because you subscribed to ");
	    sb.append("alerts on this forum. To unsubscribe, follow the link above, return to the thread list and change the settings under the alerts tab.\n");
	    sb.append("</font>");
        } else {
        sb.append(getReturnURLMessage((Post)post));
	}

        return sb.toString();
    }

    /**
     * gets the ForumSubscription associated with the provided Forum
     */
    public static ForumSubscription getFromForum(Forum forum) {
        DataCollection subs = SessionManager.getSession()
            .retrieve(ForumSubscription.BASE_DATA_OBJECT_TYPE);
        subs.addEqualsFilter("forum.id", forum.getID());
        subs.addEqualsFilter("digest.id", null);

        if (subs.next()) {
            ForumSubscription sub =
                new ForumSubscription(subs.getDataObject());
            subs.close();
            return sub;
        }
        return null;
    }

    public void setIsModerationAlert(boolean isModerationAlert) {
        set(IS_MODERATION_ALERT, new Boolean(isModerationAlert));
    }

    public void setIsModerationAlert(Boolean isModerationAlert) {
        set(IS_MODERATION_ALERT, isModerationAlert);
    }
}
