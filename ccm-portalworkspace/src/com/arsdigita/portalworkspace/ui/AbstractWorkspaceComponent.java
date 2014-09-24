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

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspacePage;

/**
 *
 *
 */
public abstract class AbstractWorkspaceComponent extends SimpleContainer {

    private WorkspaceSelectionAbstractModel m_workspace;

    private DefaultPortalSelectionModel m_portal;

    private WorkspaceDetails m_details;

    private PortalList m_portalList;

    private PersistentPortal m_portalDisplay;

    /**
     * Default constructor creates a new, empty <code>AbstractWorkspaceComponent</code> using
     * parents (SimpleContainer) default constructor.
     */
    public AbstractWorkspaceComponent() {
        this(null);
    }

    /**
     *
     * @param workspace
     */
    public AbstractWorkspaceComponent(WorkspaceSelectionAbstractModel workspace) {

        /* Creates a WorkspaceComponent (SimpleContainer) that will wrap its
         * children in the specified tag.                                     */
        super("portal:workspace", WorkspacePage.PORTAL_XML_NS);

        m_workspace = workspace;
        m_details = new WorkspaceDetails(m_workspace);
        m_portal = new DefaultPortalSelectionModel(new BigDecimalParameter(
                "portal"));
        m_portalList = createPortalList(m_portal);
        m_portalDisplay = createPortalDisplay(m_portal);

        add(m_details);
        add(m_portalList);
        add(m_portalDisplay);
    }

    /**
     *
     */
    public void setWorkspaceModel(WorkspaceSelectionAbstractModel workspace) {
        m_workspace = workspace;
        m_details.setWorkspaceModel(workspace);
        m_portalList.setWorkspaceModel(workspace);
        m_portal.setWorkspaceModel(workspace);
    }

    public Workspace getSelectedWorkspace(PageState state) {
        return m_workspace.getSelectedWorkspace(state);
    }

    /**
     * To be overwritten by children class with class specific logic.
     *
     * @param portal
     * @return
     */
    protected abstract PortalList createPortalList(PortalSelectionModel portal);

    /**
     * To be overwritten by children class with class specific logic.
     *
     * @param portal
     * @return
     */
    protected abstract PersistentPortal createPortalDisplay(
            PortalSelectionModel portal);

    /**
     *
     */
    public void register(Page page) {

        super.register(page);
        page.addComponentStateParam(this, m_portal.getStateParameter());

    }

}
