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
package com.arsdigita.portalserver.permissions;

import java.math.BigDecimal;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;

import com.arsdigita.kernel.permissions.PrivilegeDescriptor;

import org.apache.log4j.Logger;

/**
 * 
 * @version $Id: com/arsdigita/portalserver/permissions/Grant.java $
 */
class Grant {

    private static Logger s_log = Logger.getLogger(Grant.class.getName());

    static final PrivilegeDescriptor[] s_interestingPrivileges = {
        PrivilegeDescriptor.READ,
        PrivilegeDescriptor.EDIT,
        PrivilegeDescriptor.ADMIN
    };

    // Create is implicit; s_interestingPrivileges is used for display
    // while s_privileges is not.
    static final PrivilegeDescriptor[] s_privileges = {
        PrivilegeDescriptor.READ,
        PrivilegeDescriptor.EDIT,
        PrivilegeDescriptor.ADMIN,
        PrivilegeDescriptor.CREATE
    };

    static final String[] s_privilegePrettyNames = {
        "Read", "Edit", "Manage"
    };

    static final int CREATE = 3;
    static final int ADMIN = 2;
    static final int EDIT = 1;
    static final int READ = 0;

    static int getPrivilegeLevel(PrivilegeDescriptor priv) {
        if (PrivilegeDescriptor.EDIT.equals(priv)) {
            return 1;
        } else if (PrivilegeDescriptor.ADMIN.equals(priv)) {
            return 2;
        } else if (PrivilegeDescriptor.READ.equals(priv)) {
            return 0;
        } else if (PrivilegeDescriptor.CREATE.equals(priv)) {
            return 3;
        }
        return -1;
    }

    public BigDecimal granteeID;
    public String granteeName;
    public boolean granteeIsUser;
    public BigDecimal objectID;
    public String objectName;
    public PrivilegeDescriptor basePrivilege;
    public ObjectType objectType = null;
    public String objectTypeDisplayName = null;
    public int level;

    @Override
    public int hashCode() {
        if (objectType == null) {
            return granteeID.hashCode() + objectID.hashCode();
        }

        return granteeID.hashCode() +
            objectID.hashCode() +
            objectType.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof Grant)) {
            return false;
        }

        Grant otherGrant = (Grant) other;

        if (!granteeID.equals(otherGrant.granteeID)) {
            return false;
        }

        if (!objectID.equals(otherGrant.objectID)) {
            return false;
        }

        if (objectType == null && otherGrant.objectType == null) {
            return true;
        }

        if (objectType == null || otherGrant.objectType == null) {
            return false;
        }

        return objectType.equals(otherGrant.objectType);
    }

    @Override
    public String toString() {
        return marshal(granteeID, objectID, basePrivilege, objectType);
    }

    void populatePrivilege(PrivilegeDescriptor priv) {
    	basePrivilege = priv;
    	level = getPrivilegeLevel(priv);
    	objectTypeDisplayName = "All Types";
    }

    static Grant unmarshal(String encoded) {
        Grant result = new Grant();
        int dot1 = encoded.indexOf('.');
        int dot2 = encoded.indexOf('.', dot1 + 1);
        int dot3 = encoded.indexOf('.', dot2 + 1);

        result.granteeID = new BigDecimal(encoded.substring(0, dot1));
        result.objectID = new BigDecimal(encoded.substring(dot1 + 1, dot2));
        result.basePrivilege =
            PrivilegeDescriptor.get(encoded.substring(dot2 + 1, dot3));
        result.level = getPrivilegeLevel(result.basePrivilege);

        if (dot3 + 1 < encoded.length()) {
            String objectTypeName = encoded.substring(dot3 + 1);
            result.objectType =
                MetadataRoot.getMetadataRoot().getObjectType(objectTypeName);
        }

        return result;
    }

    private static String marshal(BigDecimal granteeID,
                                  BigDecimal objectID,
                                  PrivilegeDescriptor privilege,
                                  ObjectType objectType) {
        if (objectType != null) {
            return granteeID + "." +
                objectID + "." +
                privilege.getName() + "." +
                objectType.getQualifiedName();
        } else {
            return granteeID + "." +
                objectID + "." +
                privilege.getName() + ".";
        }
    }
}
