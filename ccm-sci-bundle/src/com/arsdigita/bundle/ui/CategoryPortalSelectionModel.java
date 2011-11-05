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

package com.arsdigita.bundle.ui;

import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.navigation.NavigationModel;

import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.DataCollection;

import com.arsdigita.domain.DomainObjectFactory;

import com.arsdigita.categorization.Category;
import com.arsdigita.util.Assert;
import com.arsdigita.bebop.PageState;

import com.arsdigita.portalworkspace.Workspace;
import com.arsdigita.portalworkspace.ui.WorkspaceSelectionModel;

public class CategoryPortalSelectionModel extends WorkspaceSelectionModel {

    protected Workspace getDefaultWorkspace(PageState state) {
        NavigationModel model = Navigation.getConfig().getDefaultModel();
        Category cat = model.getCategory();
        Assert.exists(cat);

        DataCollection workspaces 
            = SessionManager.getSession().retrieve(
                Workspace.BASE_DATA_OBJECT_TYPE);
        workspaces.addEqualsFilter("categories.id", cat.getID());
        
        if (workspaces.next()) {
            Workspace wk = (Workspace)DomainObjectFactory
                .newInstance(workspaces.getDataObject());
            workspaces.close();
            return wk;
        }

        return null;
    }
}
