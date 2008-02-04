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
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.SessionManager;

import com.arsdigita.runtime.ScriptContext;

import com.arsdigita.web.ApplicationType;

import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.portal.PortletType;
import com.arsdigita.forum.portlet.RecentPostingsPortlet;

import org.apache.log4j.Logger;


/**
 * Loader.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 755 2005-09-02 13:42:47Z sskracic $
 */
public class Loader extends PackageLoader {
    public final static String versionId =
        "$Id: Loader.java 755 2005-09-02 13:42:47Z sskracic $" +
        "$Author: sskracic $" +
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

    private static void setupDigestUser() {
        s_log.debug("Setting up the digest user");

        // Email address corresponding to the digest sender, as
        // specified in the configuration file.

        String email = Forum.getConfig().getDigestUserEmail();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Creating a user with the email " + email);
        }

        User user = new User();
        user.setPrimaryEmail(new EmailAddress(email));
        user.getPersonName().setGivenName("Forum");
        user.getPersonName().setFamilyName("Digest Sender");
    }

    public static void setupPrivileges() {
        PrivilegeDescriptor.createPrivilege(
            Forum.FORUM_MODERATION_PRIVILEGE);
    }

}
