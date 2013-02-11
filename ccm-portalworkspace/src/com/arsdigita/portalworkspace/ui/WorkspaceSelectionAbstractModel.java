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
 *
 */

package com.arsdigita.portalworkspace.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.portalworkspace.Workspace;

/**
 * Basic methods to select a workspace (portal application type instance) 
 * to either display in view mode (eg using WorkspaceViewer class) or in 
 * edit mode (eg using WorkspaceEdit class). 
 * 
 * Child classes are specifically required to overwrite the abstract method 
 * getDefaultWorkspace and adapt the selection procedure to specific 
 * application / usage requirements.
 */
public abstract class WorkspaceSelectionAbstractModel {

    /** Workspace. Bound to the current HTTP request. */
    private RequestLocal m_workspace;

    /**
     * Constructor.
     */
    public WorkspaceSelectionAbstractModel() {
        m_workspace = new RequestLocal() {
            @Override
            public Object initialValue(PageState state) {
                return getDefaultWorkspace(state);
            }
        };
    }

    /**
     * 
     * @param state
     * @return
     */
    protected abstract Workspace getDefaultWorkspace(PageState state);

    /**
     * 
     * @param state
     * @return
     */
    public Workspace getSelectedWorkspace(PageState state) {
        return (Workspace) m_workspace.get(state);
    }

    /**
     * 
     * @param state
     * @param workspace
     */
    public void setSelectedWorkspace(PageState state, Workspace workspace) {
        m_workspace.set(state, workspace);
    }
    
}
