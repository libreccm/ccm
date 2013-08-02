/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.portalworkspace;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.portalworkspace.ui.admin.WorkspaceCreateForm;
import com.arsdigita.ui.admin.applications.AbstractApplicationManager;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class WorkspaceApplicationManager extends AbstractApplicationManager<Workspace> {

    @Override
    public boolean allowRoot() {
        return false;
    }

    public Class<Workspace> getApplication() {
        return Workspace.class;
    }

    @Override
    public Form getApplicationCreateForm() {
        return new WorkspaceCreateForm();
    }
    
    public ApplicationInstanceAwareContainer getApplicationAdminForm() {
        final ApplicationInstanceAwareContainer container = new ApplicationInstanceAwareContainer();

        final BoxPanel panel = new BoxPanel();
        panel.add(
                new Label(new GlobalizedMessage("cw.workspace.ui.admin.no_settings",
                                                "com.arsdigita.portalworkspace.WorkspaceResources")));

        container.add(panel);

        return container;
    }
}
