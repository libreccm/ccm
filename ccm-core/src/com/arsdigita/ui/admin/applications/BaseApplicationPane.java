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
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.web.Application;
import com.arsdigita.web.ApplicationCollection;
import com.arsdigita.web.ApplicationType;

/**
 * Basic application pane containing the parts common for singleton and multi instance applications types. Shows 
 * informations about a specific application type.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class BaseApplicationPane extends SegmentedPanel {

    //private final ApplicationType applicationType;
    public BaseApplicationPane(final ApplicationType applicationType) {
        super();

        //this.applicationType = applicationType;

        final InfoPanel appInfoPanel = new InfoPanel();
        appInfoPanel.addLine("ui.admin.applications.ApplicationInfoSection.title.label",
                             applicationType.getTitle());
        appInfoPanel.addLine("ui.admin.applications.ApplicationInfoSection.app_class.label",
                             applicationType.getApplicationObjectType());
        if (applicationType.isSingleton()) {
            appInfoPanel.addLine("ui.admin.applications.ApplicationInfoSection.singleton.label",
                                 "ui.admin.applications.ApplicationInfoSection.singleton.yes",
                                 true);
        } else {
            appInfoPanel.addLine("ui.admin.applications.ApplicationInfoSection.singleton.label",
                                 "ui.admin.applications.ApplicationInfoSection.singleton.no",
                                 true);
        }
        appInfoPanel.addLine("ui.admin.applications.ApplicationInfoSection.desc.label",
                             applicationType.getDescription());
        if (applicationType.isSingleton()) {
            final ApplicationCollection applications = Application.retrieveAllApplications(applicationType.
                    getApplicationObjectType());
            if (applications.next()) {
                appInfoPanel.addLine(
                        "ui.admin.applications.ApplicationInfoSection.singleton_instance.path.label",
                        applications.getApplication().getPath());
            } else {
                appInfoPanel.addLine(
                        "ui.admin.applications.ApplicationInfoSection.singleton_instance.path.label",
                        "ui.admin.applications.ApplicationInfoSection.singleton_instance.no_instance_found");
            }
            applications.close();
        }
                      
        addSegment(new Label(GlobalizationUtil.globalize(
                "ui.admin.applications.ApplicationInfoSection.heading")), 
                   appInfoPanel);
    }

}
