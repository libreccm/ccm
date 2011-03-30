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

package com.arsdigita.london.portal.ui;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.london.portal.Workspace;
import com.arsdigita.london.portal.WorkspacePage;
import com.arsdigita.london.portal.WorkspacePageCollection;
import com.arsdigita.london.util.DomainObjectCopier;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletCollection;
import com.arsdigita.web.LoginSignal;

public class DefaultWorkspace extends WorkspaceComponent implements
		ActionListener {

	private static final Logger s_log = Logger
			.getLogger(DefaultWorkspace.class);

	/**
     * Constructor
     */
    public DefaultWorkspace() {
		super(new DefaultWorkspaceSelectionModel());

		addCustomizeListener(this);
	}

	/**
     * 
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
		PageState state = e.getPageState();

		Workspace workspace = getWorkspaceModel().getSelectedWorkspace(state);

		Party party = Kernel.getContext().getParty();
		if (party == null) {
			throw new LoginSignal(state.getRequest());
		}

		PermissionDescriptor admin = new PermissionDescriptor(
				PrivilegeDescriptor.ADMIN, workspace, party);

		// don't create user workspaces if the required parameter is set
		if (!party.equals(workspace.getParty())
				&& !PermissionService.checkPermission(admin)
				&& Workspace.getConfig().getCreateUserWorkspaces()) {

			// Clone the workspace
			Workspace parent = (Workspace) Kernel.getContext().getResource();

			Workspace clone = Workspace.createWorkspace("personal-"
					+ party.getID(), "Personal Workspace for "
					+ party.getDisplayName(), parent, (User) party);

			BigDecimal old_id = (BigDecimal) getPortalModel().getSelectedKey(
					state);
			BigDecimal new_id = null;

			WorkspacePageCollection pages = workspace.getPages();
			pages.addOrder(WorkspacePage.SORT_KEY);
			while (pages.next()) {
				WorkspacePage old_page = pages.getPage();
				WorkspacePage new_page = clone.addPage(old_page.getTitle(),
						old_page.getDescription(), old_page.getLayout(),
						old_page.getSortKey());

				if (old_page.getID().equals(old_id)) {
					new_id = new_page.getID();
				}

				PortletCollection portlets = old_page.getPortlets();
				while (portlets.next()) {
					Portlet old_portlet = portlets.getPortlet();
					Portlet new_portlet = (Portlet) new DomainObjectCopier()
							.copy(old_portlet);
					new_page.addPortlet(new_portlet, old_portlet
							.getCellNumber());
				}
			}
			clone.save();

			getPortalModel().setSelectedKey(state, new_id);
			getWorkspaceModel().setSelectedWorkspace(state, workspace);
		}
	}
}
