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

import com.arsdigita.portalworkspace.ui.WorkspaceSelectionAbstractModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.portalworkspace.Workspace;


/**
 * Intended usage undocumented.
 * 
 * Seems to provides a lighter version if WorkspaceSelectionDefaultModel
 * which takes not into account whether to jump to a personal workspace 
 * or not.
 * 
 * In order to makle use of it the portalworkspace JSP must be modified, 
 * selecting this WorkspaceSelectionModel, eg.
 * 
 * instead of:
 *     ((AbstractWorkspaceComponent)edit).setWorkspaceModel(
 *                                       new WorkspaceSelectionDefaultModel());
 * use
 *     ((AbstractWorkspaceComponent)edit).setWorkspaceModel(
 *                                       new WorkspaceSelectionPackageModel());
 * 
 */
public class WorkspaceSelectionPackageModel extends WorkspaceSelectionAbstractModel {

    /**
     * 
     * @param state
     * @return
     */
    protected Workspace getDefaultWorkspace(PageState state) {
        return (Workspace) Kernel.getContext().getResource();
    }

}
