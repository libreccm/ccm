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
package com.arsdigita.navigation;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.navigation.ui.admin.NavigationCreateForm;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;
import com.arsdigita.ui.admin.applications.ApplicationManager;

/**
 * {@link ApplicationManager} implementation for the Navigation application type. Provides a form for creating
 * new navigation instances and a form for managing the instance specific settings.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class NavigationAppManager implements ApplicationManager<Navigation> {

    public Class<Navigation> getApplication() {
        return Navigation.class;
    }

    public ApplicationInstanceAwareContainer getApplicationAdminForm() {
//        final ApplicationInstanceAwareContainer container = new ApplicationInstanceAwareContainer();
//        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
//        container.add(panel);
//
//        panel.add(new Label(NavigationGlobalizationUtil.globalize("ui.admin.instance_not_compatible_yet")));
//
//        return container;
        return new ApplicationAdminForm();
    }

    public Form getApplicationCreateForm() {
        return new NavigationCreateForm();
    }

    private class ApplicationAdminForm extends ApplicationInstanceAwareContainer {

        public ApplicationAdminForm() {
            super();
            final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
            add(panel);

            panel.add(new Label(NavigationGlobalizationUtil.globalize("ui.admin.instance_not_compatible_yet")));            
            panel.add(new Link(new PrintListener() {
                public void prepare(final PrintEvent event) {
                    final Link link = (Link) event.getTarget();

                    link.setTarget(String.format("%s/admin", getAppInstance().getPath()));
                    link.setChild(new Label(getAppInstance().getTitle()));
                }

            }));
        }

    }
}
