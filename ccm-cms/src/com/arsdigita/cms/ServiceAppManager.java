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
package com.arsdigita.cms;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.ui.admin.applications.AbstractSingletonApplicationManager;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ServiceAppManager  extends AbstractSingletonApplicationManager<Service> {
    
    public Class<Service> getApplication() {
        return Service.class;
    }
    
    /**
     * 
     * @return Just a panel with a label because this app has no settings yet.
     */
    public ApplicationInstanceAwareContainer getApplicationAdminForm() {
        final ApplicationInstanceAwareContainer container = new ApplicationInstanceAwareContainer();
        
        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);               
        panel.add(new Label(GlobalizationUtil.globalize("ui.admin.applications.no_settings")));
        
        container.add(panel);
        
        return container;
    }
    
}
