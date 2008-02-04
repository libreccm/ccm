/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.kernel.Group;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;


/**
 * Notifications for moderators are treated in a simlar manner to user
 * subscriptions to forums. This gives us additional possiblity to add
 * different notification options for moderators such as daily
 * digest. At the moment only immediate alerts are implmented.
 * Everyone that is subscribed has the forum_moderate privilege
 *
 *
 * @author Nobuko Asakai (nasakai@redhat.com)
 *
 * @version $Revision: #3 $ $Author: sskracic $ $DateTime: 2004/08/17 23:26:27 $
 */

public class ModerationAlert extends ForumSubscription {
    private static final Logger s_log
        = Logger.getLogger(ModerationAlert.class);

    public static final String MODERATION_ALERTS_QUERY
        = "com.arsdigita.forum.ModerationAlert";

    public static final String BASE_DATA_OBJECT_TYPE
           //= "com.arsdigita.forum.ModerationAlert";
           = "com.arsdigita.forum.ForumSubscription";

    protected void setupSubscriptionGroup() {}

    protected void setupSubscriptionGroup(Group moderationGroup) {

        setGroup(moderationGroup);

        PrivilegeDescriptor priv
            = PrivilegeDescriptor.get(Forum.FORUM_MODERATION_PRIVILEGE);
            PermissionDescriptor permission
                = new PermissionDescriptor(priv, this,
                                           moderationGroup);
    }

    /** All subscribed parties are granted the
     * Forum.FORUM_MODERATION_PRIVILEGE */

    protected ModerationAlert(DataObject data) {
        super(data);
    }

    public ModerationAlert(Forum forum, Group moderationGroup) {
        super(BASE_DATA_OBJECT_TYPE, forum);
        setIsModerationAlert(true);
        Assert.exists(moderationGroup, Group.class);
        setupSubscriptionGroup(moderationGroup);
    }

    /**
     * Returns a header for forum moderation alerts with the following standard
     * information:
     *
     * <pre>
     * Forum Moderation Alert
     * Forum    : Name
     * Subject  : Subject
     * com.arsdigita.messaging.ThreadedMessageed by: User
     * </pre>
     *
     * @return a header to insert at the top of the alert.
     */

    public String getHeader(ThreadedMessage msg) {
        StringBuffer sb = new StringBuffer();
        sb.append("Forum Moderation Alert\n");
        sb.append(super.getHeader(msg));
        return sb.toString();
    }

    public String getSignature(ThreadedMessage msg) {
        StringBuffer sb = new StringBuffer();
        sb.append(SEPARATOR);
        sb.append(ALERT_BLURB);
        sb.append("You are receiving this email because you are a moderator ");
        sb.append("for this forum, please click on the url below to ");
        sb.append("reject/approve the message. ");
        sb.append(getReturnURLMessage((Post)msg));

        return sb.toString();
    }
}

