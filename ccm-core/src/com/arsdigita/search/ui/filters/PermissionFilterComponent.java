/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search.ui.filters;

import com.arsdigita.search.FilterSpecification;
import com.arsdigita.search.ui.StaticFilterComponent;
import com.arsdigita.search.filters.PermissionFilterSpecification;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.bebop.PageState;

public class PermissionFilterComponent extends StaticFilterComponent {

    private PrivilegeDescriptor m_priv;

    public PermissionFilterComponent(PrivilegeDescriptor priv) {
        m_priv = priv;
    }

    public PermissionFilterComponent(String privilege) {
        this(PrivilegeDescriptor.get(privilege));
    }

    
    public FilterSpecification getFilter(PageState state) {
        Party party = Kernel.getContext().getParty();
        if (party == null) {
            // There is no logged-in user, so filter based on permissions
            // granted to "the public".
            party = Kernel.getPublicUser();
        }

        return new PermissionFilterSpecification(
            party,
            m_priv
        );
    }

}
