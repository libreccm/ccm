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

package com.arsdigita.portalworkspace.ui.jsp;

import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.portalworkspace.ui.WorkspaceSelectionModel;
import com.arsdigita.portalworkspace.ui.AbstractWorkspaceComponent;
import com.arsdigita.portalworkspace.ui.PersistentPortal;
import com.arsdigita.portalworkspace.ui.PortalList;
import com.arsdigita.portalworkspace.ui.PortalListViewer;
import com.arsdigita.portalworkspace.ui.PortalSelectionModel;

/**
 * Main (default) entry point into a standard portal workspace page where the
 * page is constructed in "view" mode to present information to a visitor.
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

    /**
     * 
     * @param portal
     * @return 
     */
    protected PortalList createPortalList(PortalSelectionModel portal) {
        return new PortalListViewer(portal);
    }

    /**
     * 
     * @param portal
     * @return 
     */
    protected PersistentPortal createPortalDisplay(PortalSelectionModel portal) {
        return new PersistentPortal(portal, WorkspacePage.MODE_DISPLAY);
    }

}
