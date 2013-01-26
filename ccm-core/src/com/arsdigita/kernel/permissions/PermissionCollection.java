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
package com.arsdigita.kernel.permissions;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;

/**
 * A class that represents a collection of permission records.
 *
 * @author Michael Bryzek
 * @version 1.0
 * @version $Id: PermissionCollection.java 287 2005-02-22 00:29:02Z sskracic $
 **/
class PermissionCollection extends DomainCollection {

    /**
     * Constructor.
     *
     * @see com.arsdigita.domain.DomainCollection#DomainCollection(DataCollection)
     */
    PermissionCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     *
     * @see com.arsdigita.domain.DomainObject#getDomainObject()
     */
    public DomainObject getDomainObject() {
        DataObject data = m_dataCollection.getDataObject();
        return new Permission(data);
    }

    /**
     * Wrapper to {@link #getDomainObject()} that casts returned
     * {@link DomainObject} as a <code>Permission</code>.
     *
     * @return A permission for the current position in the
     * collection.
     *
     * @see #getDomainObject()
     * @see ObjectPermission
     */
    public Permission getPermission() {
        return (Permission) getDomainObject();
    }

}
