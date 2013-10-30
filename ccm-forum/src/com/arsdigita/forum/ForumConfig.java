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

import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ResourceParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.web.Web;

import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * A set of configuration parameters for forums.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Chris Gilbert Westsussex Council / Westsussex Learning Grid
 * @version $Id: ForumConfig.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class ForumConfig extends AbstractConfig {

    /** A logger instance to assist debugging                             .  */
    private static final Logger s_log = Logger.getLogger(ForumConfig.class);

    /** Singelton config object.  */
    private static ForumConfig s_conf;

    /**
     * Gain a WorkspaceConfig object.
     *
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return Instance of ForumConfig
     */
    public static synchronized ForumConfig getInstanceOf() {
        if (s_conf == null) {
            s_conf = new ForumConfig();
            s_conf.load();
        }

        return s_conf;
    }


    // ////////////////////////////////////////////////////////////////////////
    // set of configuration parameters
    // ////////////////////////////////////////////////////////////////////////

    /** XML renderer rules. Provide rules for configuring information in 
     *  generated XML                                                         */
    private final Parameter m_adapters = new ResourceParameter(
                "com.arsdigita.forum.traversal_adapters",
                Parameter.REQUIRED,
                "/WEB-INF/resources/forum-adapters.xml");
    /** Admins can edit posts. Whether administrators can edit any posts      */
    private final Parameter m_adminEditPosts= new BooleanParameter(
                "com.arsdigita.forum.admin_can_edit_posts",
                Parameter.REQUIRED,
                Boolean.TRUE);
    /** Prevent other users besides admin to create new topics. Whether only 
     *  admin can create (true) topics or all users (default false)           */
    private final Parameter m_adminOnlyCreateTopics = new BooleanParameter(
                "com.arsdigita.forum.admin_only_to_create_topics",
                Parameter.REQUIRED,
                Boolean.FALSE);
    /**                           */
    private final Parameter m_authorEditPosts = new BooleanParameter(
                "com.arsdigita.forum.author_can_edit_posts",
                Parameter.REQUIRED,
                Boolean.TRUE);
    /**                           */
    private final Parameter m_authorDeletePosts = new BooleanParameter(
                "com.arsdigita.forum.author_can_delete_posts",
                Parameter.REQUIRED,
                Boolean.TRUE);
    /**                           */
    private final Parameter m_digestUserEmail = new StringParameter(
                "com.arsdigita.forum.digest_user_email",
                Parameter.OPTIONAL,
                null);
    /**                           */
    private final Parameter m_replyHostName = new StringParameter(
                "com.arsdigita.forum.reply_host_name",
                Parameter.OPTIONAL,
                null);
    /**                           */
    private final Parameter m_disablePageCaching = new BooleanParameter(
                "com.arsdigita.forum.disable_page_caching",
                Parameter.REQUIRED,
                Boolean.FALSE);
    /**                           */
    private final Parameter m_maxImageSize = new IntegerParameter(
                "com.arsdigita.forum.maximum_image_size",
                Parameter.OPTIONAL, null);
    /**                           */
    private final Parameter m_maxFileSize = new IntegerParameter(
                "com.arsdigita.forum.maximum_file_size",
                Parameter.OPTIONAL, null);
    /**                           */
    private final Parameter m_showAllThreadAlerts = new BooleanParameter(
                "com.arsdigita.forum.show_all_forum_thread_alerts",
                Parameter.OPTIONAL,
                Boolean.TRUE);
    /**                           */
    private final Parameter m_showNewTabs = new BooleanParameter(
                "com.arsdigita.forum.show_new_tabs",
                Parameter.OPTIONAL,
                Boolean.TRUE);
    /**                           */
    private final Parameter m_useWysiwygEditor = new BooleanParameter(
                "com.arsdigita.forum.use_wysiwyg_editor",
                Parameter.OPTIONAL,
                Boolean.FALSE);
    /**                           */
    private final Parameter m_rejectionMessage = new StringParameter(
                "com.arsdigita.forum.rejection_form_message.example",
                Parameter.OPTIONAL,
                null);
    /**                           */
    private final Parameter m_threadPageSize = new IntegerParameter(
                "com.arsdigita.forum.thread_page_size",
                Parameter.REQUIRED, new Integer(10));
    /**                           */
    private final Parameter m_threadOrderField = new StringParameter(
                "com.arsdigita.forum.thread_order_field",
                Parameter.REQUIRED, new String("lastUpdate"));
    /**                           */
    private final Parameter m_threadOrderDir = new StringParameter(
                "com.arsdigita.forum.thread_order_dir",
                Parameter.REQUIRED, new String("desc"));
    /**                           */
    private final Parameter m_quickFinish = new BooleanParameter(
                "com.arsdigita.forum.allow_quick_finish",
                Parameter.OPTIONAL,
                Boolean.FALSE);
    /**                           */
    private final Parameter m_deleteSentSubscriptionNotifications = new BooleanParameter(
                "com.arsdigita.forum.delete_sent_subscription_notifications",
                Parameter.OPTIONAL,
                Boolean.FALSE);

    /**
     * 
     */
    public ForumConfig() {

        register(m_adapters);
        register(m_adminEditPosts);
        register(m_adminOnlyCreateTopics);
        register(m_authorEditPosts);
        register(m_authorDeletePosts);
        register(m_digestUserEmail);
        register(m_replyHostName);
        register(m_disablePageCaching);
        register(m_maxImageSize);
        register(m_maxFileSize);
        register(m_showAllThreadAlerts);
        register(m_showNewTabs);
        register(m_useWysiwygEditor);
        register(m_rejectionMessage);
        register(m_threadPageSize);
        register(m_threadOrderField);
        register(m_threadOrderDir);
        register(m_quickFinish);
        register(m_deleteSentSubscriptionNotifications);

        loadInfo();
    }

    InputStream getTraversalAdapters() {
        return (InputStream) get(m_adapters);
    }

    public boolean canAdminEditPosts() {
        return ((Boolean) get(m_adminEditPosts)).booleanValue();
    }

    /**
     * If true, disables topic tab for non admin users. Topic tab does not
     * access control topic creation, so set this to true to maintain control of
     * the topics on the forum.
     *
     * @return
     */
    public boolean topicCreationByAdminOnly() {
        return ((Boolean) get(m_adminOnlyCreateTopics)).booleanValue();
    }

    public boolean canAuthorEditPosts() {
        return ((Boolean) get(m_authorEditPosts)).booleanValue();
    }

    boolean canAuthorDeletePosts() {
        return ((Boolean) get(m_authorDeletePosts)).booleanValue();
    }

    public String getDigestUserEmail() {
        String email = (String) get(m_digestUserEmail);
        if (email == null) {
            email = "forum-robot@" + Web.getConfig().getServer().getName();
        }
        return email;
    }

    public String getReplyHostName() {
        String hostName = (String) get(m_replyHostName);
        if (hostName == null) {
            hostName = Web.getConfig().getServer().getName();
        }
        return hostName;
    }

    /**
     * Supports prevention of client and middleware caching - use in situations
     * where users with different permissions share machines
     *
     * @return
     */
    public boolean disableClientPageCaching() {
        return ((Boolean) get(m_disablePageCaching)).booleanValue();
    }


    public User getDigestUser() {
        String email = getDigestUserEmail();

        UserCollection users = User.retrieveAll();
        users.addEqualsFilter("primaryEmail",
                email);

        if (!users.next()) {
            throw new RuntimeException("cannot find user " + email);
        }

        User user = users.getUser();
        users.close();
        return user;
    }

    /**
     * returns the maximum allowed size (in bytes) of image files attached to
     * posts. Any larger files are rejected by UI validation
     *
     * @return
     */
    public long getMaxImageSize() {
        Integer size = (Integer) get(m_maxImageSize);
        long longSize = Long.MAX_VALUE;
        if (size != null) {
            longSize = size.longValue();
        }
        return longSize;
    }

    /**
     * returns the maximum allowed size (in bytes) of files attached to posts.
     * Any larger files are rejected by UI validation
     *
     * @return
     */
    public long getMaxFileSize() {
        Integer size = (Integer) get(m_maxFileSize);
        long longSize = Long.MAX_VALUE;
        if (size != null) {
            longSize = size.longValue();
        }
        return longSize;
    }

    /**
     * if true, alerts tab displays thread alerts for this and all other forums.
     * If false, only display thread subscriptions for current forum.
     *
     * @return
     */
    /*
     * If true, the thread alert page lists thread alerts from all forums -
     * alerts not from the current forum have links to the thread displayed
     * within the context of the current forum. Looks weird and needs to be
     * sorted out presumably the correct forum needs to be set in the
     * ForumContext when a link is selected
     */
    public boolean showThreadAlertsForAllForums() {
        return ((Boolean) get(m_showAllThreadAlerts)).booleanValue();
    }

    /**
     * if true, displays setup and permissions tabs
     *
     * @return
     */
    public boolean showNewTabs() {
        return ((Boolean) get(m_showNewTabs)).booleanValue();
    }

    public boolean useWysiwygEditor() {
        return ((Boolean) get(m_useWysiwygEditor)).booleanValue();
    }

    /**
     * message added to the bottom of the moderation reection email. May give
     * details about what the poster can do if not happy with rejection
     *
     * @return
     */
    public String getRejectionMessage() {
        return (String) get(m_rejectionMessage);
    }

    public int getThreadPageSize() {
        return ((Integer) get(m_threadPageSize)).intValue();
    }

    public String getThreadOrder() {
        String field = (String) get(m_threadOrderField);
        String dir = (String) get(m_threadOrderDir);

        // Test for validity
        if(!field.equals("lastUpdate") && !field.equals("root.sent")) {
            field = "lastUpdate"; // Default behaviour
        }

        if(!dir.equals("asc") && !dir.equals("desc")) {
            dir = "desc"; // Default behaviour
        }

        return field + " " + dir;
    }

    public boolean quickFinishAllowed() {
        return ((Boolean) get(m_quickFinish)).booleanValue();
    }

    public boolean deleteNotifications() {
        return ((Boolean) get(m_deleteSentSubscriptionNotifications)).booleanValue();
    }
}
