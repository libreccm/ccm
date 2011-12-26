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
 */

package com.arsdigita.portalworkspace;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.portalworkspace.portlet.ApplicationDirectoryPortlet;
import com.arsdigita.portalworkspace.portlet.ContentDirectoryPortlet;
import com.arsdigita.portalworkspace.portlet.FreeformHTMLPortlet;
import com.arsdigita.portalworkspace.portlet.LoginPortlet;
import com.arsdigita.portalworkspace.portlet.MyWorkspacesPortlet ;
import com.arsdigita.portalworkspace.portlet.RSSFeedPortlet;
import com.arsdigita.portalworkspace.portlet.TimeOfDayPortlet;
import com.arsdigita.portalworkspace.portlet.WorkspaceNavigatorPortlet;
import com.arsdigita.portalworkspace.portlet.WorkspaceSummaryPortlet;
import com.arsdigita.portal.PortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.Assert;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

import org.apache.log4j.Logger;

/**
 * Executes nonrecurring at install time and loads (and configures ) a default
 * workspace instance (i.e. instance of ccm-portalworkspace) in a default
 * configuration.
 *
 * Configuration can be modified by configuration parameters before processing,
 * otherwise hardcoded default values take effect. A set of portlets, part of
 * the ccm-ldn-portal package, are loaded as well, so they are statically available.
 *
 * After processing the installation values can not be modified anymore without
 * a fresh installation of the whole system.
 * 
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Peter Boy <pboy@barkhof.uni-bremen.de>
 * @version $Id: Loader.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class Loader extends PackageLoader {

    private static final Logger s_log = Logger.getLogger(Loader.class);

    private StringParameter m_url = new StringParameter(
                                "com.arsdigita.portalworkspace.default_url",
                                Parameter.REQUIRED,
                                "/portal/");

    private StringParameter m_title = new StringParameter(
                                "com.arsdigita.portalworkspace.default_title",
                                Parameter.REQUIRED,
                                "Portal Homepage");

    /** 
     * If true the group created for the instance of portal workspace will
     * contain the public user as a member.
     * NOTE: Current implementation actually doesn't check for access permission!
     */
    private BooleanParameter m_isPublic = new BooleanParameter(
			"com.arsdigita.portalworkspace.default_is_public",
			Parameter.REQUIRED, Boolean.TRUE);

    /**
     * Standard constructor loads/registers the configuration parameter.
     */
    public Loader() {
		register(m_isPublic);
		register(m_url);
		register(m_title);
    }

    /**
     * Run script invoked by com.arsdigita.packing loader script.
     *
     * @param ctx
     */
    public void run(final ScriptContext ctx) {

        new KernelExcursion() {
            public void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                createApplication((String) get(m_url),
                                  (Boolean) get(m_isPublic),
                                  (String) get(m_title));
                setupWorkspacePageType();

                loadApplicationDirectoryPortlet();
                loadContentDirectoryPortlet();
                loadFreeformHTMLPortlet();
                loadLoginPortlet();
                loadMyWorkspacesPortlet();
                loadRSSFeedPortlet();
                loadTimeOfDayPortlet();
                loadWorkspaceNavigatorPortlet();
                loadWorkspaceSummaryPortlet();
            }
        }.run();
    }

    /**
     * Prepares creation of application type by checking proper formatting of
     * applications url and determining whether a parent is specified as part
     * of the url.
     * 
     * @param url Sting containing the full url (including parents url in any
     * @param isPublic if true the group created for this instance will include
     *                 the public user as a member
     * @param title
     */
    private void createApplication(String url, Boolean isPublic, String title) {

        // First create an application type for portal Workspace
        ApplicationType type = setupWorkspaceType();

        if (url != null) {

            // check weather the url parameter is properly formatted
            s_log.debug("process url " + url);
            Assert.isTrue(url.startsWith("/"), "url starts not with /");
            Assert.isTrue(url.endsWith("/"), "url ends not with /");
            Assert.isTrue(!url.equals("/"), "url is just /");

            int last = url.lastIndexOf("/"               // last = 0 is leading slash
                                      ,url.length() - 2);// trailing slash excluded
            s_log.debug("last slash at " + last);        // last > 0 : multipe elements
            
            Application parent = null;
            String name = null;
            
            if (last > 0) {         // url has more than 1 part = has a parent
                String base = url.substring(0, last + 1);
                s_log.debug("Finding parent at " + base);
                parent = Application.retrieveApplicationForPath(base);
                name = url.substring(last + 1, url.length() - 1);
            } else {
                name = url.substring(1, url.length() - 1);
            }
            s_log.debug("node name is " + name);

            // set up the portal workspace default node (instance)
            Workspace workspace = Workspace.createWorkspace(type, name, title,
					null, parent, Boolean.TRUE.equals(isPublic));
			
        }
    }

    /**
     * Creates a workspace application type as a legacy-free application type.
     *
     * No localization here because it is an invariant configuration.
     *
     * @return created ApplicationType 
     */
    private ApplicationType setupWorkspaceType() {

        s_log.debug("Creating an application type for portal workspace. " +
                    "Base Data Object Type: " + Workspace.BASE_DATA_OBJECT_TYPE);

        /* Create legacy-free application type                                
         *
         * NOTE: The wording in the title parameter of ApplicationType
         * determines the name of the subdirectory for the XSL stylesheets.
         * It gets "urlized", i.e. trimming leading and trailing blanks and
         * replacing blanks between words and illegal characters with an
         * hyphen and converted to lower case.
         * "Portal Workspace" will become "portal-workspace".                */
        ApplicationType type = new ApplicationType( 
                                       "Portal Workspace",
                                        Workspace.BASE_DATA_OBJECT_TYPE );
        type.setDescription("Portal based collaborative workspaces");
        /* Create an application type specific group in user administration   *
         * which serves as a container for subgroups, each subgroup coupled   *
         * to an application (instances) of this type.                        */
        type.createGroup();
        return type;

    }

    /**
     * Setup WorkspacePage type.
     *
     * Creates an entry for class (=type) c.ad.portalworkspace.WorkspacePage in
     * table application_types, but not in apm_package_types.
     *
     * Uses the legacy free type of application Information (i.e. a title string
     * and the object type = fully qualified domain class name) for creation
     * @return
     */
    private ResourceType setupWorkspacePageType() {
        ResourceType type = ResourceType.createResourceType(
                                         "Portal Workspace Page",
                                         WorkspacePage.BASE_DATA_OBJECT_TYPE);
        type.setDescription("Pages for the portal workspaces");
        return type;
    }


    /**
     *
     */
    private void loadApplicationDirectoryPortlet() {
		PortletType type = PortletType.createPortletType(
				"Application Directory", PortletType.WIDE_PROFILE,
				ApplicationDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays a list of portal workspace applications");
	}

	private void loadContentDirectoryPortlet() {
		PortletType type = PortletType.createPortletType("Content Directory",
				PortletType.WIDE_PROFILE,
				ContentDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays the content directory categories");
	}

	private void loadFreeformHTMLPortlet() {
		PortletType type = PortletType.createPortletType("Freeform HTML",
				PortletType.WIDE_PROFILE,
				FreeformHTMLPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays a freeform block of HTML");
	}

	private void loadLoginPortlet() {
		PortletType type = PortletType.createPortletType("Site Login",
				PortletType.WIDE_PROFILE, LoginPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Display a login form or user details");
	}

	private void loadMyWorkspacesPortlet() {
		PortletType type = PortletType.createPortletType("My Workspaces",
				PortletType.WIDE_PROFILE, MyWorkspacesPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("MyWorkspaces: Display ????");
	}

	private void loadRSSFeedPortlet() {
		PortletType type = PortletType.createPortletType("RSS Feed",
				PortletType.WIDE_PROFILE, RSSFeedPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays an RSS Feed");
	}

    /**
     *
     */
    private void loadTimeOfDayPortlet() {
		PortletType type = PortletType.createPortletType("Time of Day",
				PortletType.WIDE_PROFILE,
				TimeOfDayPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays the current date and time");
    }

    private void loadWorkspaceNavigatorPortlet() {
		PortletType type = PortletType.createPortletType(
				"Workspace Navigator Portlet", PortletType.WIDE_PROFILE,
				WorkspaceNavigatorPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays ??");
	}

    private void loadWorkspaceSummaryPortlet() {
		PortletType type = PortletType.createPortletType(
				"Workspace Summary Portlet", PortletType.WIDE_PROFILE,
				WorkspaceSummaryPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays ???");
	}

}
