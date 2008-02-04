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

import com.arsdigita.kernel.Party;

import com.arsdigita.persistence.OID;

/**
 * Represents a permission that applies universally to all objects.
 *
 * @author Oumi Mehrotra 
 * @version 1.0
 *
 * @see com.arsdigita.kernel.permissions.PermissionDescriptor
 * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
 * @see com.arsdigita.kernel.permissions.PermissionService
 **/
public class UniversalPermissionDescriptor extends PermissionDescriptor {

    public static final String versionId = "$Id: UniversalPermissionDescriptor.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    static final OID ROOT_CONTEXT_OID =
        new OID("com.arsdigita.kernel.ACSObject", 0);

    /**
     * Creates a new UniversalPermissionDescriptor object for the specified party.
     *
     * @param privilege the privilege that is to be universal
     *
     * @param party the party that this privilege is to be universally granted to
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.kernel.Party
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     **/
    public UniversalPermissionDescriptor(PrivilegeDescriptor privilege, Party party) {
        this(privilege, (party == null) ? (OID) null : party.getOID());
    }

    /**
     * Creates a new UniversalPermissionDescriptor object for the party with the specified OID.
     *
     * @param privilege the privilege descriptor that is to be universal
     *
     * @param partyOID the OID of the party this this privilege
     * is to be universally granted to
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.kernel.Party
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     * @see com.arsdigita.persistence.OID
     **/
    public UniversalPermissionDescriptor(PrivilegeDescriptor privilege, OID partyOID) {
        super(privilege, ROOT_CONTEXT_OID, partyOID);
    }
}
