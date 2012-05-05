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

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectObserver;

import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.DataObject;

import com.arsdigita.kernel.ACSObject;
import org.apache.log4j.Logger;
/**
 * Implements DomainObjectObserver pattern so that it can be
 * added to another DomainObject to provide automatic auditing.
 *  <p>
 *    
 *  </p>
 *
 * @author Joseph Bank 
 * @version 1.0
 * @version $Id: AuditingObserver.java 2089 2010-04-17 07:55:43Z pboy $
 */

public class AuditingObserver implements DomainObjectObserver {
public static final Logger s_log = Logger.getLogger(AuditingObserver.class);

    private BasicAuditTrail m_audit_trail;
    private AuditingSaveInfo m_save_info = null;
    private boolean m_wasNew = false;

    /**
     * Creates a new AuditingObserver using the passed in audit trail.
     */
    public AuditingObserver(BasicAuditTrail audit_trail) {
        //assert(audit_trail != null);
        m_audit_trail = audit_trail;
    }

    protected AuditingSaveInfo getSaveInfo() {
        return AuditingSaveFactory.newInstance();
    }

    /*
     * Methods that implement DomainObjectObserver.  We only care about save
     * and delete.  The others are all nops.
     */

    /**
     * Records some information about the save operation
     * and modifies the audit trail.
     */
    public void beforeSave(DomainObject dobj) throws PersistenceException {
        //this must run beforeSave to detect if the object is new or not
        if (s_log.isDebugEnabled()) {
            s_log.debug("Doing before " + dobj);
        }
        if (dobj.isNew()) {
            m_wasNew = true;
        }
        m_save_info = getSaveInfo();
    }

    /**
     * Saves the modified audit trail.
     */
    public void afterSave(DomainObject dobj) throws PersistenceException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Doing after " + dobj);
        }
        //assert(m_audit_trail != null);
        //Currently auditing only works for ACSObjects.
        if (dobj instanceof ACSObject && m_save_info != null) {
            ACSObject aobj = (ACSObject)dobj;
            if (m_audit_trail.isNew()) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Setting create " + dobj);
                }
                m_audit_trail.setID(aobj.getID());
                m_audit_trail.setCreationInfo(m_save_info);
                m_wasNew = false;
            }

            m_audit_trail.setLastModifiedInfo(m_save_info);
            m_save_info = null;
            m_audit_trail.save();
        }
    }

    public void beforeDelete(DomainObject dobj) throws PersistenceException {
        //intentionally empty
    }

    public void set(DomainObject dobj,
                    String name,
                    Object old_value,
                    Object new_value) {
        //intentionally empty
    }

    public void add(DomainObject dobj,
                    String name, DataObject dataObject) {
        // intentionally empty
    }

    public void remove(DomainObject dobj,
                       String name, DataObject dataObject) {
        //intentionally empty
    }

    public void clear(DomainObject dobj, String name) {
        //intentionally empty
    }

    public void afterDelete(DomainObject dobj) throws PersistenceException {
        //intentionally empty
    }

    public boolean equals(Object other) {
        if (other instanceof AuditingObserver) {
            return m_audit_trail.equals(
                ((AuditingObserver) other).m_audit_trail);
        }

        return false;
    }

    public int hashCode() {
        return m_audit_trail.hashCode();
    }
}
