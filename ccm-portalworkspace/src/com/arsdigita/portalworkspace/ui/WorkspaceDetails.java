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

package com.arsdigita.portalworkspace.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.domain.DomainObjectXMLRenderer;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

/**
 * 
 * 
 */
public class WorkspaceDetails extends SimpleComponent {

	private WorkspaceSelectionModel m_workspace;

	public WorkspaceDetails(WorkspaceSelectionModel workspace) {
		m_workspace = workspace;
	}

	public void setWorkspaceModel(WorkspaceSelectionModel workspace) {
		m_workspace = workspace;
	}

	public Workspace getSelectedWorkspace(PageState state) {
		return m_workspace.getSelectedWorkspace(state);
	}

	public void generateXML(PageState state, Element parent) {
		Workspace workspace = getSelectedWorkspace(state);

		Element content = new Element("portal:workspaceDetails",
				PortalConstants.PORTAL_XML_NS);
		exportAttributes(content);

		generateWorkspaceXML(state, content, workspace);
		generatePermissionXML(state, content, workspace);

		parent.addContent(content);
	}

	protected void generateWorkspaceXML(PageState state, Element content,
			Workspace workspace) {

		DomainObjectXMLRenderer xr = new DomainObjectXMLRenderer(content);
		xr.setWrapRoot(false);
		xr.setWrapAttributes(true);
		xr.setWrapObjects(false);

		xr.walk(workspace, WorkspaceDetails.class.getName());
	}

	protected void generatePermissionXML(PageState state, Element content,
			Workspace workspace) {
		Party party = Kernel.getContext().getParty();

		PermissionDescriptor edit = new PermissionDescriptor(
				PrivilegeDescriptor.EDIT, workspace, party);
		PermissionDescriptor admin = new PermissionDescriptor(
				PrivilegeDescriptor.ADMIN, workspace, party);

		content.addAttribute("canEdit", XML.format(new Boolean(
				PermissionService.checkPermission(edit))));
		content.addAttribute("canAdmin", XML.format(new Boolean(
				PermissionService.checkPermission(admin))));
	}
}
