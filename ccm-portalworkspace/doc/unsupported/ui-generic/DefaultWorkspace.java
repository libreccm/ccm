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

package com.arsdigita.portalworkspace.ui.personal;

import com.arsdigita.portalworkspace.ui.DefaultWorkspaceSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.portalworkspace.WorkspacePageCollection;
import com.arsdigita.london.util.DomainObjectCopier;
import com.arsdigita.portal.Portlet;
import com.arsdigita.portal.PortletCollection;
import com.arsdigita.portalworkspace.ui.WorkspaceComponent;
import com.arsdigita.web.LoginSignal;

import java.math.BigDecimal;

import org.apache.log4j.Logger;



// Intended usage undocumented, class actually not referenced by any code.
// Probably an experimental alternativ implementation to create a portal page
// based on / derived from WorkspaceComponent (which in turn derived from 
// SimpleComponent).
// Altermnative to WorkspaceEditor / WorkspaceViewer, derived from 
// class AbstractWorkspaceComponent (which in turn derived form SimpleComponent
// as well).
// Seems to use the same WorkspaceSelectionModel process to fetch the actual
// instance to serve.



/**
 * Another specific entry point into a portal workspace page, here based
 * upon WorkspaceComponent (as an alternative to be based on 
 * AbstractWorkspaceComponent).
 * Presumably intended to construct a (personal) homepage / startpage when a
 * user logs in / is logged in.
 */
// As of APLAWS 1.0.4 / CCM 6.6.x this class may never have been used and is
// propably unfinished work or unfinished port from ccm-portalserver module.
// As with WorkspaceViewer it should be invoked  by a jsp. It is not directly
// used by any java code.
public class DefaultWorkspace extends WorkspaceComponent 
                              implements ActionListener {

    private static final Logger s_log = Logger.getLogger(DefaultWorkspace.class);

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

            BigDecimal old_id = (BigDecimal) getPortalModel()
                                                 .getSelectedKey(state);
            BigDecimal new_id = null;

            WorkspacePageCollection pages = workspace.getPages();
            pages.addOrder(WorkspacePage.SORT_KEY);
            while (pages.next()) {
                WorkspacePage old_page = pages.getPage();
                WorkspacePage new_page = clone.addPage(old_page.getTitle(),
                                                       old_page.getDescription(),
                                                       old_page.getLayout(),
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
