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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.ui.admin.GlobalizationUtil;

import com.arsdigita.web.Application;

/**
 * This pane shows informations about a specific instance of a multi instance application, like title, parent 
 * application (if any) and the path. Also it contains a form for editing settings specific to the instance.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ApplicationInstancePane extends SegmentedPanel {

    public ApplicationInstancePane(final Application appInstance, final ApplicationInstanceAwareContainer appAdminPane) {

        super();
        
        final PropertySheet appInstInfoPanel = new PropertySheet(new ApplicationInstancePropertySheetModelBuilder(
                appInstance));

        addSegment(new Label(GlobalizationUtil.globalize(
                "ui.admin.applications.ApplicationInstancePane.info.heading")),
                   appInstInfoPanel);

        if (appAdminPane == null) {
            addSegment(new Label(com.arsdigita.ui.util.GlobalizationUtil.globalize(
                    "ui.admin.MultiInstanceApplicationPane.manage.heading")),
                       new Label(com.arsdigita.ui.util.GlobalizationUtil.globalize(
                    "ui.admin.MultiInstancePane.manage.no_instance_admin_pane_found",
                    new String[]{appInstance.getApplicationType().getApplicationObjectType()})));
        } else {
            appAdminPane.setAppInstance(appInstance);
            addSegment(new Label(GlobalizationUtil.globalize(
                    "ui.admin.applications.ApplicationInstancePane.manage.heading")),
                       appAdminPane);
        }
    }

}
