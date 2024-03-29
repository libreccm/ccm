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
package com.arsdigita.shortcuts;

import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.shortcuts.ui.AdminPanel;
import com.arsdigita.ui.admin.applications.AbstractSingletonApplicationManager;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class ShortcutsAppManager extends AbstractSingletonApplicationManager<Shortcuts>{

    public Class<Shortcuts> getApplication() {
        return Shortcuts.class;
    }

    public ApplicationInstanceAwareContainer getApplicationAdminForm() {
        final ApplicationInstanceAwareContainer container = new ApplicationInstanceAwareContainer();
        container.add(new AdminPanel());
        
        return container;
    }
    
    
    
}
