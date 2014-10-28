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
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

/**
 *
 *
 */
public class WorkspaceDetails extends SimpleComponent {

    private WorkspaceSelectionAbstractModel workspaceModel;

    public WorkspaceDetails(final WorkspaceSelectionAbstractModel workspace) {
        super();
        this.workspaceModel = workspace;
    }

    public void setWorkspaceModel(final WorkspaceSelectionAbstractModel workspace) {
        this.workspaceModel = workspace;
    }

    public Workspace getSelectedWorkspace(final PageState state) {
        return workspaceModel.getSelectedWorkspace(state);
    }
    
    
    /**
     * Overridden {@link SimpleComponent#isVisible(com.arsdigita.bebop.PageState)} to ensure that
     * {@code WorkspaceDetails} is not rendered when there is not Workspace to show. 
     * 
     * Previously an none existing Workspace (which can occur when using a PortalWorkspace on a
     * category page) caused a NPE.
     * 
     * @param state
     * @return 
     */
    @Override
    public boolean isVisible(final PageState state) {
        return super.isVisible(state) && workspaceModel.getSelectedWorkspace(state) != null;
    }

    @Override
    public void generateXML(final PageState state, final Element parent) {
        final Workspace workspace = getSelectedWorkspace(state);

        final Element content = new Element("portal:workspaceDetails",
                                            WorkspacePage.PORTAL_XML_NS);
        exportAttributes(content);

        generateWorkspaceXML(state, content, workspace);
        generatePermissionXML(state, content, workspace);

        parent.addContent(content);
    }

    protected void generateWorkspaceXML(final PageState state,
                                        final Element content,
                                        final Workspace workspace) {

        final DomainObjectXMLRenderer renderer = new DomainObjectXMLRenderer(content);
        renderer.setWrapRoot(false);
        renderer.setWrapAttributes(true);
        renderer.setWrapObjects(false);

        renderer.walk(workspace, WorkspaceDetails.class.getName());
    }

    protected void generatePermissionXML(final PageState state,
                                         final Element content,
                                         final Workspace workspace) {
        final Party party = Kernel.getContext().getParty();

        final PermissionDescriptor edit = new PermissionDescriptor(
            PrivilegeDescriptor.EDIT, workspace, party);
        final PermissionDescriptor admin = new PermissionDescriptor(
            PrivilegeDescriptor.ADMIN, workspace, party);

        content.addAttribute("canEdit", XML.format(PermissionService.checkPermission(edit)));
        content.addAttribute("canAdmin", XML.format(PermissionService.checkPermission(admin)));
    }

}
