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

package com.arsdigita.portalworkspace.ui.personalws;

import com.arsdigita.portalworkspace.ui.WorkspaceComponent;

/**
 * Another specific entry point into a portal workspace page, here based upon
 * WorkspaceComponent (as an alternative to AbstractWorkspaceComponent.
 */
// As of APLAWS 1.0.4 / CCM 6.6.x this class may never have been used and is
// propably unfinished work or undinished port from ccm-portalserver module.
// As with WorkspaceViewer it should be invoked  by a jsp. It is not directly
// used by any java code.
public class UserWorkspace extends WorkspaceComponent {

    public UserWorkspace() {
        super(new UserWorkspaceSelectionModel());
    }

}
