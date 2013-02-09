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

package com.arsdigita.portalworkspace.ui.portlet;

import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.kernel.permissions.UniversalPermissionDescriptor;
import com.arsdigita.kernel.security.Credential;
import com.arsdigita.kernel.security.CredentialEncodingException;
import com.arsdigita.portalworkspace.portlet.LoginPortlet;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.ui.UI;
import com.arsdigita.ui.login.LoginConstants;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.xml.Element;

public class LoginPortletRenderer extends AbstractPortletRenderer {

	private LoginPortlet m_portlet;

	/**
     * Constructor.
     * @param portlet
     */
    public LoginPortletRenderer(LoginPortlet portlet) {
		m_portlet = portlet;
	}

	/** 
     * 
     * @param state
     * @param parent
     */
    public void generateBodyXML(PageState state, Element parent) {
		Element login = parent.newChildElement("portlet:login",
				WorkspacePage.PORTLET_XML_NS);

		Party party = Kernel.getContext().getParty();
		if (party == null) {
			String name = LoginConstants.FORM_TIMESTAMP;
			long expiry = System.currentTimeMillis() + 1000
					* LoginConstants.TIMESTAMP_LIFETIME_SECS;
			String timestamp = null;
			try {
				timestamp = Credential.create(LoginConstants.FORM_TIMESTAMP,
						1000 * LoginConstants.TIMESTAMP_LIFETIME_SECS)
						.toString();
			} catch (CredentialEncodingException ex) {
				throw new UncheckedWrapperException("cannot create credential",
						ex);
			}

			Element content = login.newChildElement("portlet:loginform",
					WorkspacePage.PORTLET_XML_NS);

            //  content.addAttribute("url", LegacyInitializer
            //                       .getURL(LegacyInitializer.LOGIN_PAGE_KEY));
			content.addAttribute("url", UI.getLoginPageURL() );
			content.addAttribute("timestamp", timestamp);
		} else {
			User user = (User) party;
			Element content = login.newChildElement("portlet:loginuser",
					WorkspacePage.PORTLET_XML_NS);
			content.addAttribute("email", user.getPrimaryEmail().toString());
			content.addAttribute("givenName", user.getPersonName()
					.getGivenName());
			content.addAttribute("familyName", user.getPersonName()
					.getFamilyName());

			Link editProfile = new Link("Edit profile", 
					                    UI.getEditUserProfilePageURL());
//					"/" + LegacyInitializer.getURL(LegacyInitializer.EDIT_PAGE_KEY));
			editProfile.generateXML(state, content);

			Link changePassword = new Link("Change password", 
                                           UI.getRecoverPasswordPageURL());
//					"/" + LegacyInitializer.getURL(LegacyInitializer.CHANGE_PAGE_KEY));
			changePassword.generateXML(state, content);

			Link logout = new Link("Logout",
                                   UI.getLogoutPageURL() );
//					"/" + LegacyInitializer.getURL(LegacyInitializer.LOGOUT_PAGE_KEY));
			logout.generateXML(state, content);

			// Test whether the user can do anything in any content section
			PrivilegeDescriptor newItem = PrivilegeDescriptor
					.get(SecurityManager.CMS_NEW_ITEM);
			Session session = SessionManager.getSession();

			DataQuery q = session
					.retrieve(ContentSection.BASE_DATA_OBJECT_TYPE);
			PermissionService.filterQuery(q, "id", newItem, user.getOID());

			// If so, display a content section link
			if (q.next()) {
				Link link = new Link("Content Center", "/content-center/");
				link.generateXML(state, content);
			}
			q.close();

			// display the shortcuts admin link if the user is an administrator
			UniversalPermissionDescriptor admin = new UniversalPermissionDescriptor(
					PrivilegeDescriptor.ADMIN, user);

			if (PermissionService.checkPermission(admin)) {
				ApplicationCollection apps = Application
						.retrieveAllApplications();
				apps.addFilter(Application.PRIMARY_URL + " like :path").set(
						"path", "/admin/%");

				while (apps.next()) {
					Application app = (Application) apps.getDomainObject();
					// Don't encourage them to use broken stuff!
					if (app.getPath().equals("/admin/sitemap")) {
						continue;
					}
					Link adminLink = new Link(app.getTitle(), app.getPath()
							+ "/");
					adminLink.generateXML(state, content);
				}
			}

		}
	}
}
