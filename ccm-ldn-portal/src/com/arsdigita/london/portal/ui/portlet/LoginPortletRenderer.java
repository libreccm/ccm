/*
 * Copyright (C) 2001 ArsDigita Corporation. All Rights Reserved.
 *
 * The contents of this file are subject to the ArsDigita Public 
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.arsdigita.com/ADPL.txt
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.portal.ui.portlet;

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
import com.arsdigita.kernel.security.LegacyInitializer;
import com.arsdigita.london.portal.portlet.LoginPortlet;
import com.arsdigita.london.portal.ui.PortalConstants;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.ui.login.LoginConstants;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.xml.Element;

public class LoginPortletRenderer extends AbstractPortletRenderer {
	private LoginPortlet m_portlet;

	public LoginPortletRenderer(LoginPortlet portlet) {
		m_portlet = portlet;
	}

	public void generateBodyXML(PageState state, Element parent) {
		Element login = parent.newChildElement("portlet:login",
				PortalConstants.PORTLET_XML_NS);

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
					PortalConstants.PORTLET_XML_NS);
			content.addAttribute("url", LegacyInitializer
					.getURL(LegacyInitializer.LOGIN_PAGE_KEY));
			content.addAttribute("timestamp", timestamp);
		} else {
			User user = (User) party;
			Element content = login.newChildElement("portlet:loginuser",
					PortalConstants.PORTLET_XML_NS);
			content.addAttribute("email", user.getPrimaryEmail().toString());
			content.addAttribute("givenName", user.getPersonName()
					.getGivenName());
			content.addAttribute("familyName", user.getPersonName()
					.getFamilyName());

			Link editProfile = new Link("Edit profile", "/"
					+ LegacyInitializer.getURL(LegacyInitializer.EDIT_PAGE_KEY));
			editProfile.generateXML(state, content);

			Link changePassword = new Link("Change password", "/"
					+ LegacyInitializer.getURL(LegacyInitializer.CHANGE_PAGE_KEY));
			changePassword.generateXML(state, content);

			Link logout = new Link("Logout", "/"
					+ LegacyInitializer.getURL(LegacyInitializer.LOGOUT_PAGE_KEY));
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
