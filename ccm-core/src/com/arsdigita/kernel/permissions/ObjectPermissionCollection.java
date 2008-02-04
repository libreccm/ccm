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


import com.arsdigita.domain.DomainQuery;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;

import com.arsdigita.kernel.User;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.EmailAddress;

import java.math.BigDecimal;

/**
 * 
 * Represents a collection of permissions that have been
 * granted on a single object.
 *
 * @see PermissionService
 * @see PermissionDescriptor
 * @see PrivilegeDescriptor
 * @see com.arsdigita.kernel.User
 * @see com.arsdigita.kernel.Group
 *
 * @author Oumi Mehrotra 
 * @version 1.0
 **/
public class ObjectPermissionCollection extends DomainQuery {

    public static final String versionId = "$Id: ObjectPermissionCollection.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Constructor.
     *
     * @see com.arsdigita.domain.DomainCollection#DomainCollection(DataCollection)
     **/
    protected ObjectPermissionCollection(DataQuery query) {
        super(query);
    }

    /**
     * Gets the party ID of the grantee of the current permission record.
     * @return the party ID of the grantee of the current permission record.
     */
    public BigDecimal getGranteeID() {
        return (BigDecimal) m_dataQuery.get("granteeID");
    }

    /**
     * Returns the party OID of the grantee of the current permission record.
     * The returned OID's object type is a subtype of
     * Party.BASE_DATA_OBJECT_TYPE.
     * @return the party OID of the grantee of the current permission record.
     */
    public OID getGranteeOID() {
        if (granteeIsUser()) {
            return new OID (User.BASE_DATA_OBJECT_TYPE,
                            m_dataQuery.get("granteeID"));
        } else {
            return new OID (Group.BASE_DATA_OBJECT_TYPE,
                            m_dataQuery.get("granteeID"));
        }
    }

    /**
     * Determines whether the grantee of the current permission record
     * is a User (as opposed to a Group).
     * @return <code>true</code> if the grantee is a User; <code>false</code> if
     * the grantee is a  Group.
     *
     * @see com.arsdigita.kernel.User
     * @see com.arsdigita.kernel.Group
     */
    public boolean granteeIsUser() {
        return ((Boolean) m_dataQuery.get("granteeIsUser")).booleanValue();
    }

    /**
     * Returns the name of the grantee of the current permission record.
     * @return the name of the grantee of the current permission record.
     *
     * @see com.arsdigita.kernel.Party#getName()
     */
    public String getGranteeName() {
        if (!granteeIsUser()) {
            return (String) m_dataQuery.get("granteeName");
        }
        return getGranteePersonName().toString();
    }

    /**
     * Returns a PersonName object representing the name of the grantee of
     * the current permission record, assuming the grantee is a User.
     * @return  a PersonName object representing the name of the grantee of
     * the current permission record.
     *
     * @see com.arsdigita.kernel.User#getPersonName()
     */
    public PersonName getGranteePersonName() {
        if (!granteeIsUser()) {
            throw new RuntimeException ("Grantee is a group, not a user.");
        }

        return new PersonName((DataObject) get("granteePersonName"));
    }

    /**
     * Gets the primary email address of the grantee of
     * the current permission record.
     * @return the primary email address of the grantee of
     * the current permission record.
     *
     * @see com.arsdigita.kernel.Party#getPrimaryEmail()
     */
    public EmailAddress getGranteeEmail() {
        return new EmailAddress((String) m_dataQuery.get("granteeEmail"));
    }

    /**
     * Gets the granted privilege.
     * @return the granted privilege.
     */
    public PrivilegeDescriptor getPrivilege() {
        return PrivilegeDescriptor.get((String) m_dataQuery.get("privilege"));
    }

    /**
     * Determines whether the current permission record was inherited from the
     * permission context of the object of this permission collection.
     *
     * @return <code>true</code> if the current permission record was inherited from the
     * permission context of the object of this permission collection; <code>false</code>
     * otherwise.
     */
    public boolean isInherited() {
        return ((Boolean) m_dataQuery.get("isInherited")).booleanValue();
    }
}
