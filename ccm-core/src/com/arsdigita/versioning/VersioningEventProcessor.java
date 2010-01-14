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
package com.arsdigita.versioning;

import com.arsdigita.auditing.AuditingSaveFactory;
import com.arsdigita.auditing.AuditingSaveInfo;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.redhat.persistence.AddEvent;
import com.redhat.persistence.CreateEvent;
import com.redhat.persistence.DeleteEvent;
import com.redhat.persistence.Event;
import com.redhat.persistence.EventProcessor;
import com.redhat.persistence.ObjectEvent;
import com.redhat.persistence.PropertyEvent;
import com.redhat.persistence.RemoveEvent;
import com.redhat.persistence.SetEvent;
import com.arsdigita.util.Assert;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

// new versioning

/**
 * This class interfaces the versioning package with persistence.
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-02-20
 * @version $Revision: #18 $ $Date: 2004/08/16 $
 **/
final class VersioningEventProcessor extends EventProcessor
    implements Constants {

    private final static Logger s_log =
        Logger.getLogger(VersioningEventProcessor.class);

    // VersioningEventProcessor should only be run once.
    // Originally it had been controlled by the package this.initializer,
    // which uses the old initializer system and has been commented out in
    // enterprise.ini for a long time.
    // As a quick replacement we introduce the variable here.
    // Fixme: It might be necessary to controle it by the core initializer, so
    // CCM can be restarted in a servlet container using management extension
    // (and without restarting the container itself).
    // (2010-01-14, as of version 6.6.0)
    private static final boolean s_hasRun = false;

    private Event.Switch m_switch;
    private VersioningTxn m_vTxn;
    private boolean m_suspended;

    VersioningEventProcessor() {
        m_vTxn = null;
        m_switch = new EventSwitch();
        m_suspended = false;
    }

    // implementation of the EventProcessor interface
    protected void write(Event event) {
        event.dispatch(m_switch);
    }

    protected void flush() {}

    protected void cleanUp(boolean isCommit) {
        if ( m_vTxn != null ) {
            m_vTxn.cleanUp();
        }
        m_vTxn = null;
        m_suspended = false;
    }

    // end of implementation of the EventProcessor interface


    /**
     * Tags the current state of the data object whose oid is <code>oid</code>.
     **/
    void tag(OID oid, String tag) {
        getTxn().tag(oid, tag);
    }

    /**
     * Causes all subsequent events to be discarded.  Effective till the end of
     * the current txn.
     **/
    void suspendVersioning() {
        m_suspended = true;
    }

    /**
     * Resumes versioning suspended by a call to
     * <code>suspendVersioning()</code>.
     **/
    void resumeVersioning() {
        m_suspended = false;
    }


    // Lazy instantiation is important because it prevents mutual recursion with
    // the Session object that would occur, if we were to instantiate m_vTxn in
    // the VersioningEventProcessor constructor.
    private VersioningTxn getTxn() {
        if ( m_vTxn == null ) {
            m_vTxn = new VersioningTxn();
        }
        return m_vTxn;
    }

    private class EventSwitch extends Event.Switch {
        public void onSet(final SetEvent ev) {
            if (canBeIgnored(ev)) return;
            getChange(ev).recordEvent(ev);
        }

        public void onAdd(AddEvent ev) {
            if (canBeIgnored(ev)) return;
            getChange(ev).recordEvent(ev);
        }

        public void onRemove(RemoveEvent ev) {
            if (canBeIgnored(ev)) return;
            getChange(ev).recordEvent(ev);
        }

        private DataObjectChange getChange(Event ev) {
            return getTxn().getDataObjectChange((DataObject) ev.getObject());
        }

        public void onCreate(CreateEvent ev) {
            if (canBeIgnored(ev)) return;
            getChange(ev).recordEvent(ev);
        }

        public void onDelete(DeleteEvent ev) {
            if (canBeIgnored(ev)) return;
            getChange(ev).recordEvent(ev);
        }
    }

    private static boolean isCommitted(Event ev) {
        return ((DataObject) ev.getObject()).isCommitted();
    }

    private boolean canBeIgnored(PropertyEvent ev) {
        if ( m_suspended || !isCommitted(ev) ) { return true; }

        final ObjectType objType = functions.getObjectType(ev);
        String modelName = objType.getModel().getName();
        if ( PDL_MODEL.equals(modelName) ) {
            return true;
        }

        if ( isUnversionedProperty(objType.getQualifiedName(),
                                   ev.getProperty().getName()) ) {

            return true;
        }

        return isUnreachable(objType.getQualifiedName());
    }

    private boolean canBeIgnored(ObjectEvent ev) {
        // XXX FixMe
        // refers to the old style initializer of the package versioning which
        // is commented out of the enterprise.ini file (since an unkown time,
        // currently version 1.0.5
        // Must be dealt with internally here!
    //  if ( !Initializer.hasRun() ) { return true; }
        if ( !s_hasRun ) { return true; }
        if ( m_suspended ) { return true; }

        final ObjectType objType = functions.getObjectType(ev);

        if ( PDL_MODEL.equals(objType.getModel().getName()) ) {
            // is reentrant
            return true;
        }
        return isUnreachable(objType.getQualifiedName());
    }

    private static boolean isUnreachable(String objTypeName) {
        return ObjectTypeMetadata.getInstance().isUnreachable(objTypeName);
    }

    private static boolean isUnversionedProperty(String containerName,
                                                 String propertyName) {

        return ObjectTypeMetadata.getInstance().isUnversionedProperty
            (containerName, propertyName);
    }

    private static class VersioningTxn {
        private Map m_dobjChanges;
        private DataObject m_txn;

        public VersioningTxn() {
            m_dobjChanges =  new HashMap();
            m_txn = SessionManager.getSession().create(TXN_DATA_TYPE);
            m_txn.set(ID, functions.nextTxnID());
            addAuditTrail();
        }

        public DataObjectChange getDataObjectChange(OID oid) {
            DataObjectChange result = (DataObjectChange) m_dobjChanges.get(oid);
            if ( result != null ) return result;

            result = new DataObjectChange(oid);
            m_dobjChanges.put(oid, result);
            result.setTxn(m_txn);
            return result;
        }

        public DataObjectChange getDataObjectChange(DataObject dobj) {
            Assert.exists(dobj, DataObject.class);
            return getDataObjectChange(dobj.getOID());
        }

        public void cleanUp() {
            m_dobjChanges.clear();
        }

        public void tag(OID oid, String tag) {
            DataObject tagDobj = SessionManager.getSession().
                create(TAG_DATA_TYPE);
            tagDobj.set(ID, functions.nextSequenceValue());
            tagDobj.set(TAG, tag);
            tagDobj.set(TAGGED_OID, Adapter.serialize(oid));

            ((DataAssociation) m_txn.get(TAGS)).add(tagDobj);
        }

        private void addAuditTrail() {
            AuditingSaveInfo auditInfo = AuditingSaveFactory.newInstance();
            Date timestamp = auditInfo.getSaveDate();
            m_txn.set(TIMESTAMP,    timestamp);
            m_txn.set(MODIFYING_IP, auditInfo.getSaveIP());

            User user = auditInfo.getSaveUser();
            if ( user != null ) {
                /* There are at least three possible hacks for getting the
                 * underlying data object out of the User domain object. One is
                 * to use DomainObjectInterfaceExposer.  The other to
                 * reinstantiate the data object, given user.getID().  The third
                 * is to wrap m_txn in a throwaway domain object, like so:
                 */
                new DomainObject(m_txn) {
                    public void set(String attr, Object value) {
                        super.set(attr, value);
                    }
                }.set(MOD_USER, user);
                // ugly but works
            }
        }
    }
}
