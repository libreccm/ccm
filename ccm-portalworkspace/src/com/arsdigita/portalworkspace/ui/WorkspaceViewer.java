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

/**
 * Main (default) entry point into a standard portal workspace page where the
 * page is constructed in"view" mode to present information to a visitor.
 *
 * It is used via a jsp page which is invoked at the applications url.
 *
 * Example code stub:
 * <pre>
 * <define:component name="view"
 *              classname="com.arsdigita.portalworkspace.ui.WorkspaceViewer" />
 * <jsp:scriptlet>
 *    ((AbstractWorkspaceComponent) view).setWorkspaceModel(
 *                                        new DefaultWorkspaceSelectionModel());
 * </jsp:scriptlet>
 * </pre>
 *
 * Currently there is a jsp for the default url at
 * (web)/templates/ccm-portalworkspace/index.jsp which is mapped via web.xml
 * to /ccm/portal/ in the default, pre-configured configuration.
 */
public class WorkspaceViewer extends AbstractWorkspaceComponent {

    /**
     * Default Constructor constructs a new, empty WorkspaceViewer
     */
    public WorkspaceViewer() {
        this(null);
    }

    /**
     * Constructor instantiates a WorkspaceViewer for a specific workspace
     * object and sets the xml tags accordingly.
     *
     * @param workspace
     */
    public WorkspaceViewer(WorkspaceSelectionModel workspace) {
        super(workspace);
    }

    protected PortalList createPortalList(PortalSelectionModel portal) {
        return new PortalListViewer(portal);
    }

    protected PersistentPortal createPortalDisplay(PortalSelectionModel portal) {
        return new PersistentPortal(portal, PortalConstants.MODE_DISPLAY);
    }

}
