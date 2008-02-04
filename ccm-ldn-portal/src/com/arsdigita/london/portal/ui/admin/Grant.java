/*
 * Copyright (C) 2001, 2002, 2003 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.london.portal.ui.admin;

import java.math.BigDecimal;

import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.persistence.metadata.ObjectType;

import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
//import com.arsdigita.kernel.permissions.ParameterizedPrivilege;

import org.apache.log4j.Logger;

class Grant {
    public static final String versionId =
        "$Id: //portalserver/dev/src/com/arsdigita/portalserver/permissions/Grant.java#3 $" +
        "$Author: dennis $" +
        "$DateTime: 2003/08/15 13:46:34 $";

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

    public int hashCode() {
        if (objectType == null) {
            return granteeID.hashCode() + objectID.hashCode();
        }

        return granteeID.hashCode() +
            objectID.hashCode() +
            objectType.hashCode();
    }

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

    public String toString() {
        return marshal(granteeID, objectID, basePrivilege, objectType);
    }

    void populatePrivilege(PrivilegeDescriptor priv) {
//         if (priv instanceof ParameterizedPrivilege) {
//             ParameterizedPrivilege pPriv = (ParameterizedPrivilege) priv;
//             String objectTypeName = pPriv.getParam();
//             if (objectTypeName != null) {
//                 objectType =
//                     MetadataRoot.getMetadataRoot()
//                     .getObjectType(objectTypeName);
//                 objectTypeDisplayName = objectType.getName();
//                 /*
//                   KnObjectType kot =
//                   KnRoot.getRoot().getObjectType(objectTypeName);
//                   objectTypeDisplayName = kot.getName();
//                   if (objectTypeDisplayName == null) {
//                   objectTypeDisplayName = objectTypeName;
//                   }
//                 */
//             } else {
//                 objectTypeDisplayName = "All Types";
//             }

//             basePrivilege = pPriv.getBasePrivilege();
//             level = getPrivilegeLevel(basePrivilege);
//         } else {
            basePrivilege = priv;
            level = getPrivilegeLevel(priv);
            objectTypeDisplayName = "All Types";
//         }
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
