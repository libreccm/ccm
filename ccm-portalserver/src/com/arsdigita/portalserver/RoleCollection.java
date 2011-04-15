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
 *
 */
package com.arsdigita.portalserver;

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataCollection;

public class RoleCollection extends GroupCollection {

    private PortalSite m_portalsite;

    protected RoleCollection(DataCollection dc, PortalSite p) {
        super(dc);
        m_portalsite = p;
    }

    public RoleCollection(DataCollection dc) {
        super(dc);
    }

    public String getRoleName() {
        return (String) m_dataCollection.get("roleName");
    }

    public boolean isSystem() {
        return ((Boolean) m_dataCollection.get("isSystem")).booleanValue();
    }

    public PortalSite getPortalSite() {
        if (m_portalsite == null) {
            return (PortalSite) DomainObjectFactory.newInstance(
                              (DataObject) m_dataCollection.get("workspace"));
        } else {
            return m_portalsite;
        }
    }

    public Role getRole() {
        return (Role)getDomainObject();
    }
}
