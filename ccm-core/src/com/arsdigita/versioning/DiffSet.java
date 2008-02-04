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

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import com.arsdigita.util.AssertionError;

import java.math.BigInteger;
import java.util.Iterator;

import org.apache.log4j.Logger;

// new versioning

/**
 * <p>A collection of {@link DataObjectDiff diff} objects that can be applied in
 * order to rollback a data object to a previous state.  The diff set represent
 * the difference between the current state of the data object and one of its
 * specified past states.</p>
 *
 * <p>In order to compute the difference any two past points in the data
 * object's history, you can compute the difference between two diff sets. In
 * other words, if <em>N</em> is now, and <em>P<sub>1</sub></em> and
 * <em>P<sub>2</sub></em> are two points in the past, then</p>
 *
 * <blockquote>
 *  <em>P<sub>2</sub></em> - <em>P<sub>1</sub></em> = 
 *    (<em>N</em> - <em>P<sub>1</sub></em>) - 
 *    (<em>N</em> - <em>P<sub>2</sub></em>)
 * </blockquote>
 *
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Revision: #5 $ $DateTime: 2004/08/16 18:10:38 $
 */
final class DiffSet implements Constants {

    private static final Logger s_log = Logger.getLogger(DiffSet.class);

    private QueueMap m_queue;
    private BigInteger m_txnID;
    private DataObjectDiff m_firstEnqueued;
    private RollbackListener m_listener;
    private final ObjectTypeMetadata m_otmd; // stored for convenience only

    /**
     * @param oid the oid of the data object that we want roll backed to the
     * point specified by <code>txnID</code>.
     *
     * @param txnID the id of the versioning txn to which we want
     *              to roll back to.
     **/
    public DiffSet(OID oid, BigInteger txnID) throws NoSuchTxnException {
        this(oid, txnID, null);
    }

    /**
     * @param oid the oid of the data object that we want roll backed to the
     * point specified by <code>txnID</code>.
     * @param txnID the id of the transaction to roll back to
     * @param listener the rollback listener to invoke on important events
     *
     * @pre backTo != null
     **/
    public DiffSet(OID oid, BigInteger txnID, RollbackListener listener) {
        Assert.exists(oid, OID.class);
        m_txnID = txnID;
        m_queue = new QueueMap();
        if (listener == null) {
            m_listener = new RollbackAdapter();
        } else {
            m_listener = listener;
        }
        m_firstEnqueued = enqueue(oid);
        m_otmd = ObjectTypeMetadata.getInstance();
    }


    // -= METHODS THAT OPERATE ON m_queue =-

    /**
     * Enqueues the data object diff associated with <code>oid</code> for processing.
     * An oid can only be enqueued once. Subsequent attempts to enqueue the same oid
     * do not change the queue.
     *
     * @return the data object diff associated with <code>oid</code>.
     */
    private DataObjectDiff enqueue(OID oid) {
        Assert.exists(oid, OID.class);
        if ( m_queue.contains(oid) ) {
            return (DataObjectDiff) m_queue.get(oid);
        }
        DataObjectDiff dobjDiff = new DataObjectDiff(oid, m_listener);
        m_queue.enqueue(oid, dobjDiff);
        m_listener.onEnqueue(oid);
        return dobjDiff;
    }

    /**
     * Returns an iterator over {@link java.util.Map.Entry key-value pairs}
     * where the key is an {@link com.arsdigita.persistence.OID oid} and the
     * value is the {@link DataObjectDiff data object diff} correspoding to the
     * <code>oid</code>.
     **/
    private DataObjectDiff dequeue() {
        DataObjectDiff dobjDiff = (DataObjectDiff) m_queue.dequeue();
        m_listener.onDequeue(dobjDiff.getOID());
        return dobjDiff;
    }

    /**
     * Returns the data object diff mapped to <code>oid</code> in this diff set.
     *
     * @throws IllegalArgumentException if this diff set does not contain the
     * specified <code>oid</code>.
     **/
    public DataObjectDiff get(OID oid) throws IllegalArgumentException {
        DataObjectDiff result = (DataObjectDiff) m_queue.get(oid);
        if ( result == null ) {
            throw new IllegalArgumentException
                ("No data object diff for this oid: " + oid);
        }
        return result;
    }

    private boolean hasNext() {
        return m_queue.hasNext();
    }

    /**
     * Rewinds the queue.
     **/
    private void rewind() {
        m_queue.rewind();
    }


    public DataObject rollback() {
        computeDifferences();
        reify();
        applyDifferences();
        removeTerminalObjects();
        return m_firstEnqueued.getDataObject();
    }

    void computeDifferences() {
        m_listener.onDiffStart();

        while ( hasNext() ) {
            DataObjectDiff dobjDiff = dequeue();
            ObjectChangeCollection occ =
                new ObjectChangeCollection(dobjDiff.getOID(), m_txnID);
            computeDiff(dobjDiff, occ);
            ObjectType objType = dobjDiff.getOID().getObjectType();

            // This may or may not enqueue additional oids in the diff set.
            if ( m_otmd.isFullyVersioned(objType) ) {
                enqueueCompoundAttributes(dobjDiff);
            }
            if ( !m_otmd.isUnreachable(objType) ) {
                enqueueComponents(dobjDiff);
            }
            occ.close();
        }
        rewind();
        m_listener.onDiffFinish();
    }

    Difference diff(BigInteger toID) {
        computeDifferences();

        DiffSet to;
        if (toID == null) {
            to = null;
        } else {
            to = new DiffSet(m_firstEnqueued.getOID(), toID);
            to.computeDifferences();
        }

        Difference result = new Difference();

        while (hasNext()) {
            DataObjectDiff dobjDiff = dequeue();
            if (to == null) {
                dobjDiff.diff(null, result);
            } else {
                dobjDiff.diff((DataObjectDiff) to.m_queue.get(dobjDiff.getOID()),
                              result);
            }
        }

        rewind();

        return result;
    }

    /**
     * Traverses the collection of changes made to the data object represented
     * <code>dobjDiff</code> and accumulates those changes in the data object
     * diff, using <code>dobjDiff</code> as the accumulator holding the totality
     * of changes that were made to its underlying data object.
     **/
    private static void computeDiff(DataObjectDiff dobjDiff,
                                    ObjectChangeCollection occ) {

        while ( occ.next() ) {
            DataCollection ops = occ.getOperations();
            while ( ops.next() ) {
                BigInteger id = (BigInteger) ops.get(ID);
                Integer opType = (Integer) ops.get(SUBTYPE);
                DataObject op = specializeOperation(id, opType);
                dobjDiff.undoEvent(op);
            }
        }
    }

    private static DataObject specializeOperation(BigInteger id, Integer opType) {
        OID oid = null;
        if ( GENERIC_OPERATION.integerValue().equals(opType) ) {
            oid = new OID(GENERIC_OPERATION.datatype(), id);
        } else if (CLOB_OPERATION.integerValue().equals(opType) ) {
            oid = new OID(CLOB_OPERATION.datatype(), id);
        } else if (BLOB_OPERATION.integerValue().equals(opType) ) {
            oid = new OID(BLOB_OPERATION.datatype(), id);
        } else {
            throw new AssertionError("can't happen: id=" + id +
                                     ", opType" + opType);
        }
        return SessionManager.getSession().retrieve(oid);
    }

    /**
     * Checks if the data object diff has any compound attributes.  Such
     * attributes may need to be rolled back.  Therefore, calling this method
     * may result in additional oids being enqueued.
     **/
    private void enqueueCompoundAttributes(DataObjectDiff dobjDiff) {
        Iterator oids = dobjDiff.compoundAttributes();
        while ( oids.hasNext() ) {
            enqueue((OID) oids.next());
        }
    }

    /**
     * By the time this method is called, we know what components need to be
     * added or removed from dobjDiff in order to roll it back. The components
     * that need to be added or removed have already been enqueued in this diff
     * set.  What hasn't been enqueued yet is those components that dobjDiff has
     * now and had back at the point to which we are rolling back. All such
     * components also need to be rolled back.
     **/
    private void enqueueComponents(DataObjectDiff dobjDiff) {
        DataObject dobj = SessionManager.getSession().retrieve(dobjDiff.getOID());
        // if the object doesn't currently exist, then, clearly, it doesn't have
        // any components at the moment.
        if ( dobj == null ) return;

        ObjectType type = dobjDiff.getOID().getObjectType();
        ObjectTypeMetadata otmd = ObjectTypeMetadata.getInstance();

        for (Iterator props=type.getProperties(); props.hasNext(); ) {
            Property prop = (Property) props.next();
            if ( !prop.isComponent() || otmd.isUnversionedProperty(prop)
                 || prop.getType().isSimple() ||
                 otmd.isUnreachable((ObjectType) prop.getType()) ) {

                continue;
            }

            if ( prop.isCollection() ) {
                DataAssociationCursor cur =
                    ((DataAssociation) dobj.get(prop.getName())).cursor();

                while ( cur.next() ) {
                    enqueue(((DataObject) cur.getDataObject()).getOID());
                }
                cur.close();
            } else {
                DataObject comp = (DataObject) dobj.get(prop.getName());
                if ( comp != null )  enqueue(comp.getOID());
            }
        }
    }

    private void reify() {
        m_listener.onReifyStart();
        while ( hasNext() ) {
            DataObjectDiff dobjDiff = dequeue();
            m_listener.onReifyStart(dobjDiff.getOID(), dobjDiff.getState());
            dobjDiff.reify();
            m_listener.onReifyFinish(dobjDiff.getOID(), dobjDiff.getState());
        }

        rewind();

        while ( hasNext() ) {
            dequeue().reifyCompoundAttributes(this);
        }
        rewind();
        m_listener.onReifyFinish();
    }

    private void applyDifferences() {
        m_listener.onApplyStart();

        while ( hasNext() ) {
            DataObjectDiff dobjDiff = dequeue();
            m_listener.onApplyStart(dobjDiff.getOID(), dobjDiff.getState());
            dobjDiff.apply();
            m_listener.onApplyFinish(dobjDiff.getOID());
        }
        rewind();
        m_listener.onApplyFinish();
    }

    private void removeTerminalObjects() {
        m_listener.onTerminalStart();
        while ( hasNext() ) {
            DataObjectDiff dobjDiff = dequeue();
            dobjDiff.deleteIfTerminal();
        }
        m_listener.onTerminalFinish();
    }

    private static class NoSuchTxnException extends VersioningException {
        private final OID m_oid;

        public NoSuchTxnException(OID oid) {
            m_oid = oid;
        }

        public String getMessage() {
            return "No such txn: " + m_oid;
        }
    }
}
