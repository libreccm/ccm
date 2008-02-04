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

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.URLParameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.UncheckedWrapperException;

import com.arsdigita.kernel.UserCollection;
import com.arsdigita.kernel.User;
import com.arsdigita.web.Web;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import org.apache.log4j.Logger;

/**
 * A set of configuration parameters for forums.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: ForumConfig.java 1377 2006-11-17 10:39:41Z sskracic $
 */
public class ForumConfig extends AbstractConfig {
    public final static String versionId =
        "$Id: ForumConfig.java 1377 2006-11-17 10:39:41Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static final Logger s_log = Logger.getLogger(ForumConfig.class);

    private Parameter m_adminEditPosts;
    private Parameter m_authorEditPosts;
    private Parameter m_digestUserEmail;
    private Parameter m_replyHostName;
    private final Parameter m_adapters;

    public ForumConfig() {
        m_adminEditPosts = new BooleanParameter(
            "com.arsdigita.forum.admin_can_edit_posts",
            Parameter.REQUIRED,
            Boolean.TRUE);
        m_authorEditPosts = new BooleanParameter(
            "com.arsdigita.forum.author_can_edit_posts",
            Parameter.REQUIRED,
            Boolean.TRUE);
        m_replyHostName = new StringParameter(
            "com.arsdigita.forum.reply_host_name",
            Parameter.OPTIONAL,
            null);
        m_digestUserEmail = new StringParameter(
            "com.arsdigita.forum.digest_user_email",
            Parameter.OPTIONAL,
            null);

        try {
            m_adapters = new URLParameter
                ("com.arsdigita.forum.traversal_adapters",
                 Parameter.REQUIRED,
                 new URL(null,
                         "resource:WEB-INF/resources/forum-adapters.xml"));
        } catch (MalformedURLException ex) {
            throw new UncheckedWrapperException("Cannot parse URL", ex);
        }

        register(m_digestUserEmail);
        register(m_adminEditPosts);
        register(m_authorEditPosts);
        register(m_replyHostName);
        register(m_adapters);
        loadInfo();
    }

    InputStream getTraversalAdapters() {
        try {
            return ((URL)get(m_adapters)).openStream();
        } catch (IOException ex) {
            throw new UncheckedWrapperException("Cannot read stream", ex);
        }
    }

    public boolean canAdminEditPosts() {
        return ((Boolean)get(m_adminEditPosts)).booleanValue();
    }

    public boolean canAuthorEditPosts() {
        return ((Boolean)get(m_authorEditPosts)).booleanValue();
    }

    public String getDigestUserEmail() {
        String email = (String)get(m_digestUserEmail);
        if (email == null) {
            email = "forum-robot@" + Web.getConfig().getServer().getName();
        }
        return email;
    }

    public String getReplyHostName() {
        String hostName = (String)get(m_replyHostName);
        if (hostName == null) {
            hostName = Web.getConfig().getServer().getName();
        }
        return hostName;
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
}
