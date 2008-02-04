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

import com.arsdigita.auditing.Audited;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.FlushException;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import com.redhat.persistence.EventProcessorManager;
import com.redhat.persistence.pdl.VersioningMetadata.NodeVisitor;

import java.math.BigInteger;
import java.util.Date;

import org.apache.log4j.Logger;

// new versioning

/**
 * This class provides methods for rolling back to a previous point in the
 * versioning log.
 *
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @author Stanislav Freidin
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @version $Revision: #32 $ $DateTime: 2004/08/16 18:10:38 $
 */
public final class Versions {
    private static final Logger s_log = Logger.getLogger(Versions.class);
    private static final String SORT_KEY_DESC = Constants.ID + " desc";

    /**
     * This should be considered an implementation detail that is subject to
     * change.
     **/
    public static final NodeVisitor NODE_VISITOR = new NodeVisitorImpl();

    /**
     * This should be considered an implementation detail that is subject to
     * change.
     **/
    public static final EventProcessorManager EPM =
        new VersioningProcessorManager();

    private Versions() {}

    /**
     * Tags changes made in the current session.
     *
     * @param tag a short string identifying the set of changes made in the
     * current session.
     */
    public static void tag(OID oid, String tag) {
        VersioningProcessorManager.getVersioningEventProcessor().tag(oid, tag);
    }

    private static void flushPendingEvents() throws VersioningException {
        try {
            SessionManager.getSession().flushAll();
        } catch (FlushException ex) {
            throw new VersioningException("Session not flushable", ex);
        }
    }

    /**
     * <p>Suspends the recording of changes to versioned data objects within the
     * current transaction.  All changes made after this call will be discarded.
     * All pending tags will be applied.</p>
     *
     * @see #resumeVersioning()
     * @throws VersioningException if there are unflushable pending events
     **/
    public static void suspendVersioning() throws VersioningException {
        flushPendingEvents();
        VersioningProcessorManager.getVersioningEventProcessor().
            suspendVersioning();
    }

    /**
     * <p>Resumes the recording of changes to versioned data objects within the
     * current transaction. </p>
     *
     * @see #suspendVersioning()
     * @throws VersioningException if there are unflushable pending events
     **/
    public static void resumeVersioning() throws VersioningException {
        flushPendingEvents();
        VersioningProcessorManager.getVersioningEventProcessor().
            resumeVersioning();
    }

    /**
     * Returns the most recent versioning txn id for this <code>oid</code> that
     * has been tagged with <code>tag</code>.
     *
     * @pre oid != null
     **/
    public static BigInteger getMostRecentTxnID(OID oid, String tag) {
        DataCollection dc = getTaggedTxns(oid, SORT_KEY_DESC);
        dc.addEqualsFilter(Constants.TAGS_TAG, tag);

        BigInteger txnID = null;
        if ( dc.next() ) {
            txnID = (BigInteger) dc.get(Constants.ID);
        }
        dc.close();
        return txnID;
    }

    public static TransactionCollection getTaggedTransactions(OID oid,
                                                              boolean down) {

        if (down) {
            return new TransactionCollection(getTaggedTxns(oid, SORT_KEY_DESC));
        } else {
            return new TransactionCollection(getTaggedTxns(oid, Constants.ID));
        }
    }

    public static TransactionCollection getTaggedTransactions(OID oid) {
        return getTaggedTransactions(oid, true);
    }


    /**
     * This returns a collection of txns tagged for this oid. Note that this may
     * include txns in which no changes were made to the data object identified
     * by this oid.  This is in contrast to the {@link #getTxns(OID)} method,
     * which only returns txns in which changes were made to the specified data
     * object.
     **/
    private static DataCollection getTaggedTxns(OID oid, String order) {
        DataCollection dc = SessionManager.getSession().retrieve
            (Constants.TXN_DATA_TYPE);
        dc.addOrder(order);
        dc.addEqualsFilter(Constants.TAGS_TAGGED_OID, Adapter.serialize(oid));
        return dc;
    }

    private static DataCollection getTxns(OID oid) {
        DataCollection dc = SessionManager.getSession().retrieve
            (Constants.TXN_DATA_TYPE);
        dc.addEqualsFilter("changes." + Constants.OBJ_ID, Adapter.serialize(oid));
        return dc;
    }

    /**
     * Returns a collection of all txns in which changes were made to the
     * specified data object.
     **/
    static TransactionCollection getTransactions(OID oid) {
        return new TransactionCollection(getTxns(oid));
    }

    public static Difference diff(OID oid, BigInteger fromID,
                                  BigInteger toID) {
        Assert.exists(oid, OID.class);

        DiffSet from = new DiffSet(oid, fromID);
        return from.diff(toID);
    }

    public static Difference diff(OID oid, Transaction from, Transaction to) {
        return diff(oid,
                    from == null ? null : from.getID(),
                    to == null ? null : to.getID());
    }

    /**
     * Rolls back the versioned data object specified by <code>oid</code> to an
     * earlier state specified by the <code>changeID</code> and returns the rolled
     * back data object.
     *
     * <p>The resulting rolled back data objects are not explicitly saved.
     * Unless you choose to call <code>save()</code> explicitly in your own code
     * after rollback, the rolled back objects will be saved implicitly when the
     * transaction is committed. </p>
     *
     * @pre oid != null
     * @pre changeID != null
     */
    public static DataObject rollback(final OID oid, final BigInteger txnID) {
        Assert.exists(oid, OID.class);
        Assert.exists(txnID, BigInteger.class);
        return new DiffSet(oid, txnID).rollback();
    }

    /**
     * This is for unit testing.
     **/
    static DataObject rollback(OID oid, BigInteger txnID, RollbackListener rl) {
        rl.onStart();
        DataObject result = new DiffSet(oid, txnID, rl).rollback();
        rl.onFinish();
        return result;
    }

    static void computeDifferences
        (final OID oid, final BigInteger txnID, RollbackListener rl) {
        Assert.exists(oid, OID.class);
        Assert.exists(txnID, BigInteger.class);
        Assert.exists(rl, RollbackListener.class);
        rl.onStart();
        new DiffSet(oid, txnID, rl).computeDifferences();
        rl.onFinish();
    }


    /**
     * <p>Returns the audit info for the data object identified by
     * <code>oid</code>. May return <code>null</code> if no auditing info can be
     * found for the specified <code>oid</code>.</p>
     **/
    public static Audited getAuditInfo(OID oid) {
        DataObject creationTxn = getCreationTxn(oid);
        if ( creationTxn == null ) return null;

        DataObject lastTxn = getLastTxn(oid);
        Assert.exists(lastTxn, DataObject.class);
        return new FullAuditInfo(creationTxn, lastTxn);
    }

    private static DataObject getCreationTxn(OID oid) {
        DataCollection dc = getTxns(oid);
        dc.addOrder(Constants.ID + " asc");

        DataObject txn = null;
        if ( dc.next() ) {
            txn =  dc.getDataObject();
        }
        dc.close();
        return txn;
    }

    private static DataObject getLastTxn(OID oid) {
        DataCollection dc = getTxns(oid);
        dc.addOrder(SORT_KEY_DESC);
        DataObject txn = null;
        if ( dc.next() ) {
            txn = dc.getDataObject();
        }
        dc.close();
        return txn;
    }

    private static class AuditInfo {
        private final Date m_date;
        private final String m_ip;
        private final User m_user;

        public AuditInfo(DataObject txn) {
            m_date = (Date) txn.get(Constants.TIMESTAMP);
            m_ip = (String) txn.get(Constants.MODIFYING_IP);
            DataObject userDobj = (DataObject) txn.get(Constants.MOD_USER);
            m_user = (User) DomainObjectFactory.newInstance(userDobj);
        }

        public Date getDate() {
            return m_date;
        }

        public String getIP() {
            return m_ip;
        }

        public User getUser() {
            return m_user;
        }
    }

    private static class FullAuditInfo implements Audited {
        private final AuditInfo m_creationInfo;
        private final AuditInfo m_lastInfo;

        public FullAuditInfo(DataObject creationTxn, DataObject lastTxn) {
            m_creationInfo = new AuditInfo(creationTxn);
            m_lastInfo = new AuditInfo(lastTxn);
        }

        public Date getCreationDate() {
            return m_creationInfo.getDate();
        }

        public String getCreationIP() {
            return m_creationInfo.getIP();
        }

        public User getCreationUser() {
            return m_creationInfo.getUser();
        }

        public Date getLastModifiedDate() {
            return m_lastInfo.getDate();
        }

        public String getLastModifiedIP() {
            return m_lastInfo.getIP();
        }

        public User getLastModifiedUser() {
            return m_lastInfo.getUser();
        }
    }

    private static class NodeVisitorImpl implements NodeVisitor {
        public void onObjectType(ObjectType objType, boolean isMarked) {
            ObjectTypeMetadata.getInstance().
                addGraphNode(GraphNode.getInstance(objType), isMarked);
        }

        public void onVersionedProperty(Property property) {
            ObjectTypeMetadata.getInstance().
                addVersionedProperty(property);
        }

        public void onUnversionedProperty(Property property) {
            ObjectTypeMetadata.getInstance().
                markEdgeUnversioned(property);
        }

        public void onFinish() {
            ObjectTypeMetadata.getInstance().initialize();
        }
    }
}
