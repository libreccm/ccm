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
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.WorkspacePage;
import com.arsdigita.portalworkspace.WorkspacePageCollection;

import java.math.BigDecimal;


/**
 *
 * 
 */
public class DefaultPortalSelectionModel extends ACSObjectSelectionModel
		                         implements PortalSelectionModel {

    private WorkspaceSelectionModel m_workspace;

    /**
     *
     * @param param
     */
    public DefaultPortalSelectionModel(BigDecimalParameter param) {
        this(null, param);
    }

    public DefaultPortalSelectionModel(WorkspaceSelectionModel workspace,
                                       BigDecimalParameter param) {
        super(WorkspacePage.class.getName(),
              WorkspacePage.BASE_DATA_OBJECT_TYPE, param);

        m_workspace = workspace;
    }

    /**
     *
     * @param workspace
     */
    public void setWorkspaceModel(WorkspaceSelectionModel workspace) {
        m_workspace = workspace;
    }

    /**
     *
     * @param state
     * @return
     */
    public Workspace getSelectedWorkspace(PageState state) {
        return m_workspace.getSelectedWorkspace(state);
    }

    /**
     *
     * @param state
     * @return
     */
    @Override
    public Object getSelectedKey(PageState state) {
        BigDecimal key = (BigDecimal) super.getSelectedKey(state);
        if (key == null) {
            loadDefaultPortal(state);
        }
        return super.getSelectedKey(state);
    }

    /**
     * 
     * @param state
     * @return
     */
    @Override
    public DomainObject getSelectedObject(PageState state) {
        BigDecimal key = (BigDecimal) super.getSelectedKey(state);
        if (key == null) {
            loadDefaultPortal(state);
        }
        return super.getSelectedObject(state);
    }

    /**
     *
     * @param state
     * @return
     */
    public WorkspacePage getSelectedPortal(PageState state) {
        return (WorkspacePage) getSelectedObject(state);
    }

    /**
     * 
     * @param state
     */
    private void loadDefaultPortal(PageState state) {

        Workspace workspace = getSelectedWorkspace(state);
        WorkspacePageCollection portals = workspace.getPages();
        portals.addOrder(WorkspacePage.SORT_KEY);
        if (portals.next()) {
            WorkspacePage portal = portals.getPage();
            setSelectedObject(state, portal);
            portals.close();
        } else {
            WorkspacePage portal = workspace.addPage("Main", "Main page");
            setSelectedObject(state, portal);
        }

    }

}
