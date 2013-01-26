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
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.kernel.security.SecurityLogger;

import org.apache.log4j.Priority;

/**
 * PermissionException
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 * @version $Id: PermissionException.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public class PermissionException extends RuntimeException {

    private PermissionDescriptor m_permission;
    private String m_msg;

    /**
     * Used for permission failures because of no authenticated party.
     **/
    public PermissionException(PrivilegeDescriptor priv, ACSObject obj) {
        m_permission = null;
        m_msg = "Unauthenticated party does not have the " + priv +
            " privilege on Object " + obj.getOID() + ".";
        SecurityLogger.log(Priority.WARN, m_msg);
    }

    /**
     * 
     * @param permission 
     */
    public PermissionException(PermissionDescriptor permission) {
        m_permission = permission;

        OID partyOID = m_permission.getPartyOID();
        String userID = partyOID == null ? null : "" + partyOID.get("id");
        String priv = m_permission.getPrivilegeDescriptor().getDisplayName();
        String objectID = "" + m_permission.getACSObjectOID().toString();

        String contextID = null;
        DataObject context = PermissionService.getContext(
            m_permission.getACSObjectOID()
        );

        if ( context != null ) {
            contextID = context.getOID().toString();
        }

        m_msg = "User " + userID + " does not have the " + priv + " privilege " +
            " on Object " + objectID + " with context " + contextID;


        SecurityLogger.log(Priority.WARN, m_msg);
    }

    /**
     * 
     * @param priv
     * @param obj
     * @param message 
     */
    public PermissionException(PrivilegeDescriptor priv, 
                               ACSObject obj, 
                               String message) {
        m_permission = null;
        m_msg = "Permission denied. Attempted operation using " + 
            priv + " on Object " + obj.getOID() + ": " + message;
        SecurityLogger.log(Priority.WARN, m_msg);
    }

    /**
     * 
     * @return 
     */
    public PermissionDescriptor getPermission() {
        return m_permission;
    }

    /**
     * 
     * @return 
     */
    public String getMessage() {
        return m_msg;
    }

}
