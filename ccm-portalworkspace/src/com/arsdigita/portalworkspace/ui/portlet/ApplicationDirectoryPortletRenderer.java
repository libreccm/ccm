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

package com.arsdigita.portalworkspace.ui.portlet;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.portal.AbstractPortletRenderer;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Resource;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.portalworkspace.portlet.ApplicationDirectoryPortlet;
import com.arsdigita.portalworkspace.ui.PortalConstants;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.URL;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

public class ApplicationDirectoryPortletRenderer extends
		AbstractPortletRenderer {
	private ApplicationDirectoryPortlet m_portlet;

	public ApplicationDirectoryPortletRenderer(
			ApplicationDirectoryPortlet portlet) {
		m_portlet = portlet;
	}

	public void generateBodyXML(PageState state, Element parent) {
		Element content = parent.newChildElement(
				"portlet:applicationDirectory", PortalConstants.PORTLET_XML_NS);

		Application current = Web.getContext().getApplication();
		Party party = Kernel.getContext().getParty();

		ApplicationCollection apps = current.getChildApplications();
		apps.addOrder(Resource.TITLE);
		PermissionService.filterObjects(apps, PrivilegeDescriptor.READ,
				party == null ? null : party.getOID());

		while (apps.next()) {
			Application app = apps.getApplication();
			Element child = content.newChildElement(
					"portlet:applicationDirectoryEntry",
					PortalConstants.PORTLET_XML_NS);

			child.addAttribute("title", app.getTitle());
			child.addAttribute("description", app.getDescription());
			child.addAttribute("url", URL.there(app, "/", null).toString());
		}
	}
}
