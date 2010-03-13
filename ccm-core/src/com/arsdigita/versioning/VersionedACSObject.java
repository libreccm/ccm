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
package com.arsdigita.versioning;

import com.arsdigita.auditing.Audited;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.Assert;

import java.math.BigInteger;
import java.util.Date;

import org.apache.log4j.Logger;

// old versioning

/**
 * Versioned ACSObject
 *
 * @author Joseph A. Bank  (jbank@alum.mit.edu)
 * @author Stanislav Freidin
 * @deprecated with no replacement.  See the note in {@link com.arsdigita.versioning}.
 * @version $Revision: #30 $ $Date: 2004/08/16 $
 **/
public class VersionedACSObject extends ACSObject implements Audited {
    private static final Logger s_log = Logger.getLogger(VersionedACSObject.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        Constants.PDL_MODEL + ".VersionedACSObject";
    public static final String MASTER     = "master";
    public static final String IS_DELETED = "isDeleted";


    // Canonical reference to the object type for VersionedACSObject; for
    // internal use only
    private static ObjectType s_properType = null;

    // Cached master object
    private VersionedACSObject m_master;

    /**
     * @deprecated
     **/
    protected VersionedACSObject(DataObject data) {
        super(data);
    }

    /**
     * @deprecated
     **/
    public VersionedACSObject(String typeName) {
        super(typeName);
    }

    /**
     * @deprecated
     **/
    public VersionedACSObject(ObjectType type) {
        super(type);
    }

    /**
     * @deprecated
     */
    protected VersionedACSObject(OID oid, boolean checkDeleted) {
        super(oid);
        if (!checkDeleted) {
            throw new Error("not implemented");
        }
    }

    /**
     * @deprecated
     **/
    public VersionedACSObject(OID oid) {
        this(oid, true);
    }
        
    /**
     * @deprecated
     */
    protected void beforeSave() {
        super.beforeSave();        
        if (get(IS_DELETED) == null) {
                    set(IS_DELETED, Boolean.FALSE);
         }  
    }

    /**
     * @deprecated
     */
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * @deprecated
     */
    public boolean recordAttributeChange
        (String action, String attr, Object oldValue, Object newValue) {
        return false;
    }

    /**
     * @deprecated
     */
    public boolean trackChanges() {
        return true;
    }

    /**
     * @deprecated
     */
    protected void propagateMaster(VersionedACSObject master) {}

    private static final Audited NULL = new Audited() {
        public User   getCreationUser()     { return null; }
        public Date   getCreationDate()     { return null; }
        public String getCreationIP()       { return null; }
        public User   getLastModifiedUser() { return null; }
        public Date   getLastModifiedDate() { return null; }
        public String getLastModifiedIP()   { return null; }
    };

    private Audited getAuditInfo() {
        Audited result = Versions.getAuditInfo(getOID());
        if (result == null) {
            result = NULL;
        }
        return result;
    }

    /**
     * @deprecated
     */
    public User getCreationUser() {
        return getAuditInfo().getCreationUser();
    }

    /**
     * @deprecated
     */
    public Date getCreationDate() {
        return getAuditInfo().getCreationDate();
    }

    /**
     * @deprecated
     */
    public String getCreationIP() {
        return getAuditInfo().getCreationIP();
    }

    /**
     * @deprecated
     */
    public User getLastModifiedUser() {
        return getAuditInfo().getLastModifiedUser();
    }

    /**
     * @deprecated
     */
    public Date getLastModifiedDate() {
        return getAuditInfo().getLastModifiedDate();
    }

    /**
     * @deprecated
     */
    public String getLastModifiedIP() {
        return getAuditInfo().getLastModifiedIP();
    }

    /**
     * @deprecated
     */
    public VersionedACSObject getMaster() {
        if(m_master == null) {
            DataObject d = (DataObject)super.get(MASTER);
            if(d != null) {
                m_master =
                    (VersionedACSObject) DomainObjectFactory.newInstance(d);
            } else {
                m_master = this;
            }
        }
        if ( m_master != this ) {
            m_master = m_master.getMaster();
        }
        return m_master;
    }

    /**
     * @deprecated
     */
    public void setMaster(VersionedACSObject master) {
        Assert.exists(master, "master object");
        Assert.isTrue
            (!isRolledBack(), "Object " + getID() + " is rolled back");
        Assert.isTrue
            (!master.isRolledBack(),
             "Master Object " + master.getID() + " is rolled back");

        m_master = master;

        if(master.equals(this)) {
            setAssociation(MASTER, null);
        } else {
            setAssociation(MASTER, master);
        }
    }

    /**
     * @deprecated
     */
    public boolean isMaster() {
        return equals(getMaster());
    }

    /**
     * @deprecated
     */
    public String applyUniqueTag(String prefix) {
        throw new Error("not implemented");
    }

    /**
     * @deprecated
     */
    public void applyTag(String tag) {
        Versions.tag(getOID(), tag);
    }

    /**
     * @deprecated
     */
    public void rollBackTo(String tag) {
        BigInteger id = Versions.getMostRecentTxnID(getOID(), tag);
        Versions.rollback(getOID(), id);
    }

    /**
     * @deprecated
     */
    public void rollBackTo(Transaction trans) {
        throw new Error("not implemented");
    }

    /**
     * @deprecated
     */
    public boolean isRolledBack() {
        return false;
    }

    /**
     * @deprecated
     */
    public Transaction getRolledBackTo() {
        throw new Error("not implemented");
    }

    /**
     * @deprecated
     */
    public void rollForward() {
        throw new Error("not implemented");
    }

    /**
     * @deprecated
     */
    public TransactionCollection getTransactions(boolean descending) {
        return Versions.getTaggedTransactions(getOID(), descending);        
    }

    /**
     * @deprecated
     */
    public TransactionCollection getTransactions() {
        return Versions.getTaggedTransactions(getOID(), true);
    }

    /**
     * @deprecated
     */
    public void autoPropagateMaster(VersionedACSObject master) {
        VersionController.autoPropagateMaster(this, master);
    }

    /**
     * @deprecated
     */
    public void permanentlyDelete() {
        throw new Error("not implemented");
    }

    /**
     * @deprecated
     */
    public void save(String tag) {
        applyTag(tag);
        save();
    }

    /**
     * @deprecated
     */
    protected static boolean isSubtype(ObjectType subType) {
        if (subType == null) return false;

        if (s_properType == null) {
            s_properType = SessionManager.getSession().getMetadataRoot().
                getObjectType(BASE_DATA_OBJECT_TYPE);
            Assert.exists
                (s_properType, "Object type " + BASE_DATA_OBJECT_TYPE);
        }
        return subType.isSubtypeOf(s_properType);
    }
}
