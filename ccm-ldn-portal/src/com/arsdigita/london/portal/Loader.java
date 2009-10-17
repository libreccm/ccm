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

package com.arsdigita.london.portal;

import org.apache.log4j.Logger;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.ResourceType;
import com.arsdigita.loader.PackageLoader;
import com.arsdigita.london.portal.portlet.ApplicationDirectoryPortlet;
import com.arsdigita.london.portal.portlet.ContentDirectoryPortlet;
import com.arsdigita.london.portal.portlet.FreeformHTMLPortlet;
import com.arsdigita.london.portal.portlet.LoginPortlet;
import com.arsdigita.london.portal.portlet.NavigationDirectoryPortlet;
import com.arsdigita.london.portal.portlet.RSSFeedPortlet;
import com.arsdigita.london.portal.portlet.TimeOfDayPortlet;
import com.arsdigita.london.portal.portlet.WorkspaceDirectoryPortlet;
import com.arsdigita.portal.PortletType;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.Assert;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationType;

/**
 * Executes nonrecurring at install time and loads (and configures ) a default
 * workspace instance (i.e. instance of ccm-ldn-portal) in a default configuration.
 *
 * Configuration can be modified by configuration parameters before processing,
 * otherwise hardcoded default values take effect. A set of portlets, part of
 * the ccm-ldn-portal package, are loaded as well, so they are statically available.
 *
 * After processing the installation values can not be modified anymore without
 * a fresh installation of the whole system.
 * 
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: Loader.java 1718 2008-07-16 14:08:38Z terry $
 */
public class Loader extends PackageLoader {
    // versionID no longer used as a system variable. version information is
    // kept as part of java doc.
	// public final static String versionId = "$Id: Loader.java 1718 2008-07-16 14:08:38Z terry $"
	//		+ "$Author: terry $" + "$DateTime: 2004/03/02 06:33:42 $";

	private static final Logger s_log = Logger.getLogger(Loader.class);

	private StringParameter m_url = new StringParameter(
			"com.arsdigita.london.portal.default_url", Parameter.REQUIRED,
			"/portal/");

	private StringParameter m_title = new StringParameter(
			"com.arsdigita.london.portal.default_title", Parameter.REQUIRED,
			"Portal Homepage");

	private BooleanParameter m_isPublic = new BooleanParameter(
			"com.arsdigita.london.portal.default_is_public",
			Parameter.REQUIRED, Boolean.TRUE);

	/**
     * Standard constructor. 
     */
    public Loader() {
		register(m_isPublic);
		register(m_url);
		register(m_title);
	}

	/**
     * Run script invoked by the loader script.
     *
     * @param ctx
     */
    public void run(final ScriptContext ctx) {
		new KernelExcursion() {
			public void excurse() {
				setEffectiveParty(Kernel.getSystemParty());
				createApplication((String) get(m_url),
						(Boolean) get(m_isPublic), (String) get(m_title));

				setupWorkspacePageType();
				loadTimeOfDayPortlet();
				loadContentDirectoryPortlet();
				loadRSSFeedPortlet();
				loadFreeformHTMLPortlet();
				loadLoginPortlet();
				loadApplicationDirectoryPortlet();
				loadWorkspaceDirectoryPortlet();
                NavigationDirectoryPortlet.loadPortletType();
			}
		}.run();
	}

	/**
     * 
     * @param url
     * @param isPublic
     * @param title
     */
    private void createApplication(String url, Boolean isPublic, String title) {

		ApplicationType type = setupWorkspaceType();

		if (url != null) {

            // check weather the url parameter is properly formatted
			s_log.debug("process url " + url);
			Assert.isTrue(url.startsWith("/"), "url starts with /");
			Assert.isTrue(url.endsWith("/"), "url ends with /");
			Assert.isTrue(!url.equals("/"), "url is not /");

			int last = url.lastIndexOf("/", url.length() - 2);
			s_log.debug("last slash at " + last);
			Application parent = null;
			String name = null;
			if (last > 0) {
				String base = url.substring(0, last + 1);
				s_log.debug("Finding parent at " + base);
				parent = Application.retrieveApplicationForPath(base);
				name = url.substring(last + 1, url.length() - 1);
			} else {
				name = url.substring(1, url.length() - 1);
			}
			s_log.debug("node name is " + name);

			// set up the portal node
			Workspace workspace = Workspace.createWorkspace(name, title,
					parent, Boolean.TRUE.equals(isPublic));
			
		}
	}

	/**
     * Creates a workspace application type as a legacy-compatible application
     * type.
     *
     * No localization here because it is an invariant configuration.
     *
     * @return
     */
    private ApplicationType setupWorkspaceType() {
        // The first string is a key parameter used to create a
        // legacy package type to back the new application type.
		ApplicationType type = ApplicationType.createApplicationType(
				"workspace", "Portal Workspace",
				Workspace.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Portal based collaborative workspaces");
		type.createGroup();
		return type;
	}

	private ResourceType setupWorkspacePageType() {
		ResourceType type = ResourceType.createResourceType(
				"Portal Workspace Page", WorkspacePage.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Pages for the portal workspaces");
		return type;
	}

	private void loadTimeOfDayPortlet() {
		PortletType type = PortletType.createPortletType("Time of Day",
				PortletType.WIDE_PROFILE,
				TimeOfDayPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays the current date and time");
	}

	private void loadContentDirectoryPortlet() {
		PortletType type = PortletType.createPortletType("Content Directory",
				PortletType.WIDE_PROFILE,
				ContentDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays the content directory categories");
	}

	private void loadRSSFeedPortlet() {
		PortletType type = PortletType.createPortletType("RSS Feed",
				PortletType.WIDE_PROFILE, RSSFeedPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays an RSS Feed");
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

	private void loadApplicationDirectoryPortlet() {
		PortletType type = PortletType.createPortletType(
				"Application Directory", PortletType.WIDE_PROFILE,
				ApplicationDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays a list of applications");
	}

	private void loadWorkspaceDirectoryPortlet() {
		PortletType type = PortletType.createPortletType("Workspace Directory",
				PortletType.WIDE_PROFILE,
				WorkspaceDirectoryPortlet.BASE_DATA_OBJECT_TYPE);
		type.setDescription("Displays a list of workspaces");
	}
}
