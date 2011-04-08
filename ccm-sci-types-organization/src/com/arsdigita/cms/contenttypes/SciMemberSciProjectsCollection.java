/*
 * Copyright (c) 2011 Jens Pelzetter,
 * for the Center of Social Politics of the University of Bremen
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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentType;
import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;

/**
 *
 * @author Jens Pelzetter
 * @version $Id$
 */
public class SciMemberSciProjectsCollection extends DomainCollection {

    public static final String LINK_MEMBER_ROLE = "link.role_name";
    public static final String LINK_STATUS = "link.status";
    public static final String MEMBER_ROLE = "role_name";
    public static final String STATUS = "status";

    public SciMemberSciProjectsCollection(DataCollection dataCollection) {
        super(dataCollection);

        m_dataCollection.addFilter(String.format("type = %s",
                                                 ContentType.
                findByAssociatedObjectType(SciProject.class.getName()).getID().
                toString()));

        m_dataCollection.addOrder("title");
    }

    public String getRoleName() {
        return (String) m_dataCollection.get(LINK_MEMBER_ROLE);
    }

    public void setRoleName(String roleName) {
        DataObject link = (DataObject) this.get("link");

        link.set(MEMBER_ROLE, roleName);
    }

    public String getStatus() {
        return (String) m_dataCollection.get(LINK_STATUS);
    }

    public void setStatus(String status) {
        DataObject link = (DataObject) this.get("link");

        link.set(STATUS, status);
    }

    public SciProject getProject() {
        return (SciProject) DomainObjectFactory.newInstance(m_dataCollection.
                getDataObject());
    }
}
