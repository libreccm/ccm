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
package com.arsdigita.atoz.ui.admin;

import com.arsdigita.atoz.AtoZ;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.ui.admin.applications.AbstractApplicationManager;
import com.arsdigita.ui.admin.applications.ApplicationInstanceAwareContainer;

/**
 * Application Manager implementation for the AtoZ application integrating the admin UI for AtoZ
 * into the Applications tab at {@code /ccm/admin/}.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class AtoZApplicationManager extends AbstractApplicationManager<AtoZ> {

    @Override
    public Class<AtoZ> getApplication() {
        return AtoZ.class;
    }

    @Override
    public ApplicationInstanceAwareContainer getApplicationAdminForm() {
        final ApplicationInstanceAwareContainer container = new ApplicationInstanceAwareContainer();

        final BigDecimalParameter providerParam = new BigDecimalParameter("provider");
        final AtoZAdminPane adminPane = new AtoZAdminPane(container, providerParam);
        container.add(adminPane);
        
        return container;
    }

    @Override
    public boolean allowRoot() {
        return true;
    }
}
