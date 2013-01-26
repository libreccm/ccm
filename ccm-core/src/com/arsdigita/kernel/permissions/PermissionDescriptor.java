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

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * Describes a privilege on an object that is granted to a party.
 * A PermissionDescriptor is used with the PermissionService to check,
 * grant, and revoke permissions.
 *
 * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
 * @see com.arsdigita.kernel.permissions.PermissionService
 *
 * @author Oumi Mehrotra
 * @version $Id: PermissionDescriptor.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class PermissionDescriptor {

    private OID m_partyOID;
    private OID m_acsObjectOID;
    private PrivilegeDescriptor m_privilege;    // not null

    /**
     * Creates a new PermissionDescriptor object for the specified party,
     * granting the specified privilege on the specified ACS object.
     *
     *
     * @param privilege the privilege being granted
     *
     * @param acsObject the ACS object on which
     * the privilege is to be granted
     *
     * @param party the party that will be granted this
     * permission
     *
     * @pre privilege != null
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.kernel.Party
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     **/
    public PermissionDescriptor(PrivilegeDescriptor privilege,
                                ACSObject acsObject, Party party) {
        Assert.exists(acsObject, "ACSObject acsObject");

        if (party != null) {
            m_partyOID = party.getOID();
        } else {
            m_partyOID = null;
        }
        m_acsObjectOID = acsObject.getOID();
        Assert.exists(privilege, "privilege");
        m_privilege = privilege;
    }

    /**
     * Creates a new PermissionDescriptor object for the party with the
     * given OID, that grants the specified privilege on the
     * ACS object with the given OID.
     *
     * @param privilege the privilege to be granted
     *
     * @param acsObjectOID the OID of the
     * ACS object on which the privilege is to be granted
     *
     * @param partyOID the OID of the party
     * that will be granted this permission
     *
     * @pre privilege != null
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.kernel.Party
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     * @see com.arsdigita.persistence.OID
     **/
    public PermissionDescriptor(PrivilegeDescriptor privilege,
                                OID acsObjectOID, OID partyOID) {
        // Make sure that passed in OIDs have the correct object type.
        if (partyOID != null) {
            try {
                ObjectType.verifySubtype(Party.BASE_DATA_OBJECT_TYPE,
                                         partyOID.getObjectType());
            } catch (RuntimeException e) {
                throw new UncheckedWrapperException("The OID for the Party has an " +
                                           "invalid object type.\nExpecting: " +
                                           "com.arsdigita.kernel.Party\nActual: " +
                                           partyOID.getObjectType().getQualifiedName(), e);
            }
        }
        m_partyOID = partyOID;

        try {
            ObjectType.verifySubtype(ACSObject.BASE_DATA_OBJECT_TYPE,
                                     acsObjectOID.getObjectType());
        } catch (RuntimeException e) {
            throw new UncheckedWrapperException("The OID for the ACSObject has an " +
                                       "invalid object type.\nExpecting: " +
                                       "com.arsdigita.kernel.ACSObject " +
                                       "\nActual: " +
                                       acsObjectOID.getObjectType().getQualifiedName(), e);
        }
        m_acsObjectOID = acsObjectOID;

        Assert.exists(privilege, "privilege");
        m_privilege = privilege;
    }

    /**
     * Returns the <code>OID</code> of the <code>Party</code> that is
     * the grantee of the <code>PrivilegeDescriptor</code> associated with this
     * <code>PermissionDescriptor</code>.
     *
     * @return The <code>OID</code> of the <code>Party</code> that is
     * the grantee of the <code>PrivilegeDescriptor</code>.
     *
     * @see com.arsdigita.kernel.Party
     * @see com.arsdigita.persistence.OID
     **/
    public OID getPartyOID() {
        return m_partyOID;
    }

    /**
     * Returns the <code>OID</code> of the <code>ACSObject</code> that
     * the <code>Party</code> has the <code>PrivilegeDescriptor</code> on.
     *
     * @return The <code>OID</code> of the <code>ACSObject</code> that
     * the <code>Party</code> has the <code>PrivilegeDescriptor</code> on.
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.persistence.OID
     **/
    public OID getACSObjectOID() {
        return m_acsObjectOID;
    }

    /**
     * Returns the <code>OID</code> of the <code>PrivilegeDescriptor</code>
     * associated with this <code>PermissionDescriptor</code>.
     *
     * @return The <code>OID</code> of the <code>Privilge</code>
     * associated with this <code>PermissionDescriptor</code>.
     *
     * @post return != null
     *
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     * @see com.arsdigita.persistence.OID
     **/
    public PrivilegeDescriptor getPrivilegeDescriptor() {
        return m_privilege;
    }

    public boolean equals(Object o) {
        PermissionDescriptor p = (PermissionDescriptor) o;
        return ( getACSObjectOID().equals(p.getACSObjectOID()) &&
                 ((getPartyOID() == null && p.getPartyOID() == null) ||
                  (getPartyOID() != null && p.getPartyOID() != null &&
                   getPartyOID().equals(p.getPartyOID()))) &&
                 getPrivilegeDescriptor().equals(p.getPrivilegeDescriptor()) );
    }

    public int hashCode() {
        return getPartyOID().hashCode() +
            getACSObjectOID().hashCode() +
            getPrivilegeDescriptor().hashCode();
    }
}
