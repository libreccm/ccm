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

import com.arsdigita.cms.util.Util;
import com.arsdigita.forum.portlet.MyForumsPortlet;
import com.arsdigita.forum.portlet.RecentPostingsPortlet;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portal.PortletType;
import com.arsdigita.portal.apportlet.AppPortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringArrayParameter;
import com.arsdigita.web.ApplicationType;

import org.apache.log4j.Logger;

/**
 * Loader executes nonrecurring at install time and loads (installs and
 * initializes) the Forum module.
 *
 * It loads an application type into database. Detailed configuration is done
 * during initialization at each application start using configuration
 * parameters.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 2261 2012-01-08 17:17:10Z pboy $
 */
public class Loader extends PackageLoader {

    //  ///////////////////////////////////////////////////////////////////
    //  Configurable parameters during load step.
    //  ///////////////////////////////////////////////////////////////////
    
    /**
     * Intentionally we don't create forum instance(s) by default during load
     * step.  Forum instances are created on demand using the admin ui.
     * 
     * By specifying forum urls during load step you may install one or more
     * forum instances, which are installed as root applications (i.e. without
     * a parent application).
     * 
     * Example:
     *   com.arsdigita.forum.forum_names=general-discussions,specific-discussion
     * will create 2 forum instances accessible at
     *   [host]:/ccm/general-discussions
     *   [host]:/ccm/specific-discussions
     * 
     * Additional forum instances can be created using the sitmap.jsp ui 
     * choosing either /navigation (i.e. an Navigation instance), /content (i.
     * e. a ContentSection instance) or one of the created instances as
     * parent applicaation.
     */
    private final Parameter m_forumInstances = new StringArrayParameter(
                    "com.arsdigita.forum.forum_names",
                    Parameter.OPTIONAL,
                    new String[] { }
                    );

    /** Private logger instance for debugging purpose. */
    private static final Logger s_log = Logger.getLogger(Loader.class);


    /**
     * Standard constructor.
     */
    public Loader() {
        s_log.debug("forum.Loader (Constructor) invoked");

        register(m_forumInstances);
        
        s_log.debug("forum.Loader (Constructor) completed");
    }

    /**
     * 
     * @param ctx 
     */
    public void run(final ScriptContext ctx) {
        new KernelExcursion() {

            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                setupPrivileges();
                setupDigestUser();

                setupForumAppType( (String[]) get(m_forumInstances) );
                setupRecentPostingsPortletType();
                setupMyForumsPortletType();

                SessionManager.getSession().flushAll();
            }
        }.run();
    }

    /**
     * Creates Forum as a legacy-free application type.
     * 
     * We just create the application type, intentionally not an application
     * instance (see above).
     * 
     * @return
     */
    private static void setupForumAppType(String[] forumNames) {

        /* NOTE: The wording in the title parameter of ApplicationType
         * determines the name of the subdirectory for the XSL stylesheets.
         * It gets "urlized", i.e. trimming leading and trailing blanks and
         * replacing blanks between words and illegal characters with an
         * hyphen and converted to lower case.
         * "Forum" will become "forum".                                       */
        ApplicationType type = new ApplicationType("Forum",
                                                   Forum.BASE_DATA_OBJECT_TYPE );

        type.setDescription("An electronic bulletin board system (disussion forum).");
        type.save();

        for (int i = 0 ; i < forumNames.length ; i++) {

            final String forumName = forumNames[i];
            Util.validateURLParameter("name", forumName);
            s_log.info("Creating forum instance on /" + forumName);
            
            Forum.create(forumName,forumName,null);

        }
        s_log.info("Forum setup completed");
        return ;
    }

    /**
     * 
     * @return
     */
    public static AppPortletType setupRecentPostingsPortletType() {
        AppPortletType type =
                       AppPortletType.createAppPortletType(
                                   "Recent Forum Postings",
                                    PortletType.WIDE_PROFILE,
                                    RecentPostingsPortlet.BASE_DATA_OBJECT_TYPE);
        type.setProviderApplicationType(Forum.BASE_DATA_OBJECT_TYPE);
        type.setPortalApplication(true);
        type.setDescription("Displays the most recent postings "
                            + "to the bulletin board.");
        return type;
    }

    /**
     * 
     */
    public static PortletType setupMyForumsPortletType() {

        PortletType type = PortletType.createPortletType(
                "My Forums",
                PortletType.WIDE_PROFILE,
                MyForumsPortlet.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Lists forums that user has access to, " + ""
                            + "           with last posting date");

        return type;
    }

    /**
     * 
     */
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
            // Fixes a NPE in Loader of ccm-forum if screen_names are being used 
            user.setScreenName("Forum");
            users.close();
        }

    }

    /**
     * 
     */
    public static void setupPrivileges() {

        PrivilegeDescriptor.createPrivilege(
                 Forum.FORUM_READ_PRIVILEGE);
        PrivilegeDescriptor.createPrivilege(
                 Forum.FORUM_MODERATION_PRIVILEGE);
        PrivilegeDescriptor.createPrivilege(
                 Forum.CREATE_THREAD_PRIVILEGE);
        PrivilegeDescriptor.createPrivilege(
                 Forum.RESPOND_TO_THREAD_PRIVILEGE);
        // Establich privilege hierarchie, eg. moderation includes createThread
        PrivilegeDescriptor.addChildPrivilege(
                 Forum.FORUM_MODERATION_PRIVILEGE,
                 Forum.CREATE_THREAD_PRIVILEGE);
        PrivilegeDescriptor.addChildPrivilege(
                 Forum.CREATE_THREAD_PRIVILEGE,
                 Forum.RESPOND_TO_THREAD_PRIVILEGE);
        PrivilegeDescriptor.addChildPrivilege(
                 Forum.RESPOND_TO_THREAD_PRIVILEGE,
                 PrivilegeDescriptor.READ.getName()); // general read privilege
        PrivilegeDescriptor.addChildPrivilege(
                 Forum.RESPOND_TO_THREAD_PRIVILEGE,
                 Forum.FORUM_READ_PRIVILEGE);
    }
}
