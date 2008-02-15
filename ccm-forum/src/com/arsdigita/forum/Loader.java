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

import com.arsdigita.loader.PackageLoader;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.SessionManager;

import com.arsdigita.runtime.ScriptContext;

import com.arsdigita.web.ApplicationType;

import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.portal.PortletType;
import com.arsdigita.forum.portlet.MyForumsPortlet;
import com.arsdigita.forum.portlet.RecentPostingsPortlet;

import org.apache.log4j.Logger;


/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 1628 2007-09-17 08:10:40Z chrisg23 $
 */
public class Loader extends PackageLoader {
    public final static String versionId =
        "$Id: Loader.java 1628 2007-09-17 08:10:40Z chrisg23 $" +
        "$Author: chrisg23 $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static final Logger s_log = Logger.getLogger(Loader.class);

    public void run(final ScriptContext ctx) {
        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                setupPrivileges();
                setupForumAppType();
                //setupInboxAppType();
                setupRecentPostingsPortletType();
                setupMyForumsPortletType();
                setupDigestUser();
		SessionManager.getSession().flushAll();
            }
        }.run();
    }

    private static ApplicationType setupForumAppType() {
        ApplicationType type = ApplicationType
            .createApplicationType(Forum.PACKAGE_TYPE,
                                   "Discussion Forum Application",
                                   Forum.BASE_DATA_OBJECT_TYPE);
        type.setDescription("An electronic bulletin board system.");
        return type;
    }


    private static ApplicationType setupInboxAppType() {
        ApplicationType type = ApplicationType
            .createApplicationType(Forum.PACKAGE_TYPE,
                                   "Inbox",
                                   "com.arsdigita.forum.Inbox");
        type.setDescription("Inbox");
        return type;
    }

    public static AppPortletType setupRecentPostingsPortletType() {
        AppPortletType type = AppPortletType
            .createAppPortletType("Recent Forum Postings",
                                  PortletType.WIDE_PROFILE,
                                  RecentPostingsPortlet.BASE_DATA_OBJECT_TYPE);
        type.setProviderApplicationType(Forum.BASE_DATA_OBJECT_TYPE);
        type.setPortalApplication(true);
        type.setDescription("Displays the most recent postings " +
                            "to the bulletin board.");
        return type;
    }

	public static PortletType setupMyForumsPortletType() {
		
		PortletType type = PortletType
				   .createPortletType("My Forums", 
									  PortletType.WIDE_PROFILE,
		MyForumsPortlet.BASE_DATA_OBJECT_TYPE);
			   type.setDescription("Lists forums that user has access to, with last posting date");
			
			return type;
		}

    private static void setupDigestUser() {
        s_log.debug("Setting up the digest user");

        // Email address corresponding to the digest sender, as
        // specified in the configuration file.

        String email = Forum.getConfig().getDigestUserEmail();
		UserCollection users = User.retrieveAll();
		users.addEqualsFilter("primaryEmail", email);
		if (users.next()) {
			s_log.debug("user exists");
		} else {

            s_log.debug("Creating a user with the email " + email);
        User user = new User();
        user.setPrimaryEmail(new EmailAddress(email));
        user.getPersonName().setGivenName("Forum");
        user.getPersonName().setFamilyName("Digest Sender");
			users.close();
		}
		
		
    }

    public static void setupPrivileges() {
        PrivilegeDescriptor.createPrivilege(
            Forum.FORUM_MODERATION_PRIVILEGE);
        PrivilegeDescriptor.createPrivilege(
            Forum.CREATE_THREAD_PRIVILEGE);
		PrivilegeDescriptor.createPrivilege(
					Forum.RESPOND_TO_THREAD_PRIVILEGE);
		PrivilegeDescriptor.addChildPrivilege(Forum.FORUM_MODERATION_PRIVILEGE, Forum.CREATE_THREAD_PRIVILEGE);
		PrivilegeDescriptor.addChildPrivilege(Forum.CREATE_THREAD_PRIVILEGE, Forum.RESPOND_TO_THREAD_PRIVILEGE);
		PrivilegeDescriptor.addChildPrivilege(Forum.RESPOND_TO_THREAD_PRIVILEGE, PrivilegeDescriptor.READ.getName());
		
		
		
    }

}
