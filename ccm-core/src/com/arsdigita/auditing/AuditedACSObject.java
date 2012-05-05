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
package com.arsdigita.auditing;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;

import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.metadata.ObjectType;


import java.util.Date;

/**
 * Base class. Provides default functionality for auditing ACSObjects.
 *
 *  <p>
 *    
 *  </p>
 *
 * @author Joseph Bank 
 * @version 1.0
 * @version $Id: AuditedACSObject.java 2089 2010-04-17 07:55:43Z pboy $
 */

public abstract class AuditedACSObject extends ACSObject implements Audited {

    /**
     * Audit trail.
     */

    private BasicAuditTrail m_audit_trail;

    /**
     * Gets the user who created the object. May be null.
     * @return the user who created the object.
     */

    public User getCreationUser() {
        return m_audit_trail.getCreationUser();
    }

    /**
     * Gets the creation date of the object.
     * @return the creation date.
     */

    public Date getCreationDate() {
        return m_audit_trail.getCreationDate();
    }

    /**
     * Gets the IP address associated with creating an object. May be
     * null.
     * @return the creation IP address.
     */

    public String getCreationIP() {
        return m_audit_trail.getCreationIP();
    }

    /**
     * Gets the user who last modified the object. May be null.
     * @return the last modifying user.
     */

    public User getLastModifiedUser() {
        return m_audit_trail.getLastModifiedUser();
    }

    /**
     * Gets the last modified date.
     * @return the last modified date.
     */

    public Date getLastModifiedDate() {
        return m_audit_trail.getLastModifiedDate();
    }

    /**
     * Gets the last modified IP address. May be null.
     * @return the IP address associated with the last modification.
     */

    public String getLastModifiedIP() {
        return m_audit_trail.getLastModifiedIP();
    }

    /**
     * Initializes with a basic audit trail and an Auditing Observer.
     * This method is called from the DomainObject constructor, so it
     * is invoked whenever a new ACSObject is constructed.
     *
     * @post m_audit_trail != null
     */

    protected void initialize() {
        super.initialize();

        //Get the audit trail for this object

        m_audit_trail = BasicAuditTrail.retrieveForACSObject(this);
        addObserver(new AuditingObserver(m_audit_trail));
    }


    /**
     * Equaivalent to the corresponding ACSObject constructor.
     */

    protected AuditedACSObject(DataObject AuditedACSObjectData) {
        super(AuditedACSObjectData);
    }

    /**
     * Equaivalent to the corresponding ACSObject constructor.
     */

    public AuditedACSObject(String typeName) {
        super(typeName);
    }

    /**
     * Equaivalent to the corresponding ACSObject constructor.
     */
    public AuditedACSObject(ObjectType type) {
        super(type);
    }

    /**
     * Equaivalent to the corresponding ACSObject constructor.
     */

    public AuditedACSObject(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }
}
