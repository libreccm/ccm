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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentCenter;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.ui.UI;
import com.arsdigita.ui.admin.Admin;
import com.arsdigita.ui.login.LoginServlet;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Global navigation elements for the CMS admin UIs.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: GlobalNavigation.java 1942 2009-05-29 07:53:23Z terry $
 */
// Made public (instead of unspecified, resulting in protected) in 6.6.8
public class GlobalNavigation extends SimpleComponent {

	private static final Logger s_log = Logger.getLogger(GlobalNavigation.class);
	private final String m_centerPath;
	private final String m_adminPath;
	private final String m_wspcPath;
	private final String m_signOutPath;
	private final String m_helpPath;

	/**
	 *
	 */
	public GlobalNavigation() {
		m_centerPath = ContentCenter.getURL();
		m_adminPath = Admin.getInstance().getServletPath();
		m_wspcPath = UI.getWorkspaceURL();
		m_signOutPath = LoginServlet.getLogoutPageURL();
		m_helpPath = "/nowhere"; // We don't have this yet XXX.
	}

	/**
	 *
	 * @param state
	 * @param parent
	 */
	@Override
	public void generateXML(final PageState state, final Element parent) {
		if (isVisible(state)) {
			final HttpServletRequest sreq = state.getRequest();

			final Element nav = parent.newChildElement("cms:globalNavigation", CMS.CMS_XML_NS);
			final String centerTitle = lz("cms.ui.content_center");
			final String adminTitle = lz("cms.ui.admin_center");
			final String wspcTitle = lz("cms.ui.my_workspace");
			final String signOutTitle = lz("cms.ui.sign_out");
			final String helpTitle = lz("cms.ui.help");

			link(sreq, nav, "cms:contentCenter", m_centerPath, centerTitle);

			/* If the current user has admin permissions, insert a link to the admin center */
			if (PermissionService.checkPermission(new PermissionDescriptor(
				PrivilegeDescriptor.ADMIN,
				Admin.getInstance(),
				Kernel.getContext().getParty()))) {
				link(sreq, nav, "cms:adminCenter", m_adminPath, adminTitle);
			}

			link(sreq, nav, "cms:workspace", m_wspcPath, wspcTitle);
			link(sreq, nav, "cms:signOut", m_signOutPath, signOutTitle);
			link(sreq, nav, "cms:help", m_helpPath, helpTitle);
		}
	}

	/**
	 *
	 * @param sreq
	 * @param parent
	 * @param name
	 * @param path
	 * @param title
	 * @return
	 */
	private static Element link(final HttpServletRequest sreq,
		final Element parent,
		final String name,
		final String path,
		final String title) {
		final Element link = parent.newChildElement(name, CMS.CMS_XML_NS);

		link.addAttribute("href", URL.there(sreq, path).toString());
		link.addAttribute("title", title);

		return link;
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	private static String lz(final String key) {
		return (String) ContentSectionPage.globalize(key).localize();
	}
}
