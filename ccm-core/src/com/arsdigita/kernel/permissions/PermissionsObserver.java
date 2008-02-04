/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.GlobalObserver;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelHelper;
import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;
import org.apache.log4j.Logger;

/**
 * Global observer to enforce permissions on ACS Objects. This
 * observer verifies that the user has write access to the object
 * before the object is saved.
 *
 * @author richardl@redhat.com
 *
 */
public class PermissionsObserver implements GlobalObserver {

    private static final Logger s_log =
        Logger.getLogger(PermissionsObserver.class);

    public void set(DomainObject dobj,
                    String name,
                    Object old_value,
                    Object new_value) { }

    public void add(DomainObject dobj,
                    String name, DataObject dataObject) { }

    public void remove(DomainObject dobj,
                       String name, DataObject dataObject) { }

    public void clear(DomainObject dobj, String name) { }

    public void beforeSave(DomainObject dobj) {
        if (isKernelMode()) { 
            if (s_log.isDebugEnabled()) {
                s_log.debug("In kernel mode so skipping " + 
                            (dobj.isNew() ? "CREATE" : "EDIT") +
                            " privilege check on object " + dobj.getOID());
            }
            return; 
        }

        if ( dobj.isNew() ) {
            ((ACSObject) dobj).doCreateCheck();
        } else {
            ((ACSObject) dobj).doWriteCheck();
        }
    }

    public void afterSave(DomainObject dobj) { }

    public void beforeDelete(DomainObject dobj) {
        if (isKernelMode()) { 
            if (s_log.isDebugEnabled()) {
                s_log.debug("In kernel mode so skipping ADMIN privilege " +
                            "check on object " + dobj.getOID());
            }
            return; 
        }

        ((ACSObject) dobj).assertPrivilege(PrivilegeDescriptor.ADMIN);
    }

    public void afterDelete(DomainObject dobj) { }

    public boolean shouldObserve(DomainObject dobj) {
        ObjectType type = dobj.getObjectType();
        return type.isSubtypeOf(ACSObject.BASE_DATA_OBJECT_TYPE);
    }

    private boolean isKernelMode() {
        Party currentParty = KernelHelper.getCurrentEffectiveParty();

        return (currentParty != null &&
                currentParty.getID().equals(Kernel.getSystemParty().getID()));
    }

}
