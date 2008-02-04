package com.arsdigita.london.importer;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import java.util.Iterator;

/**
 *  The mapping between source (ie those found in XML files)
 * and destination (newly created on successful import) OIDs.
 *
 * <p>
 *  A RemoteOidMapping instance is actually a
 * (system_id, src_oid, dst_oid) tuple.  system_id is an arbitrary
 * string identifying the source of import data.  There is
 * unique constraint on (system_id, src_oid) combination.
 * </p>
 * <p>
 *  Whenever a domain object is imported and persisted to database,
 * a RemoteOidMapping instance is stored along.  Tracking imported
 * objects allow us to skip those already imported.  As already
 * mentioned, mapping is created for role properties of the imported
 * content items as well, which enables us to import "shared" or
 * "repository" assets that are being pointed to by more than one
 * master object.
 * </p>
 * <p>
 *  Since there is absolutely no harm in running importer over the
 * same data set more than once, it's trivial to sync up two or more
 * CCM instances by simply importing the complete dump of one
 * master node.  Those already imported will be simply skipped over.
 * However, keep in mind that importer never deletes objects.
 * </p>
 *
 * @see com.arsdigita.london.importer
 */
public class RemoteOidMapping extends DomainObject {

    public static final String SYSTEM_ID = "systemId";
    public static final String   SRC_OID = "srcOid";
    public static final String   DST_OID = "dstOid";

    public static final String BASE_DATA_OBJECT_TYPE = RemoteOidMapping.class.getName();

    /**
     * Creates new RemoteOidMapping instance.
     */
    public RemoteOidMapping() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Makes persistence happy.
     */
    public RemoteOidMapping(DataObject dobj) {
        super(dobj);
    }

    /**
     * Makes persistence happy camper.
     */
    public RemoteOidMapping(OID mappingOID) throws DataObjectNotFoundException {
        super(mappingOID);
    }

    /**
     * Convenience constructor which creates new RemoteOidMapping instance.
     */
    public RemoteOidMapping(String systemId, String srcOid, String dstOid) {
        this();
        setSystemId(systemId);
        setSrcOid(srcOid);
        setDstOid(dstOid);
    }

    /**
     * @return null if no mapping is found.
     */
    public static RemoteOidMapping retrieve(String systemId, String srcOid) {
        OID oid = new OID(BASE_DATA_OBJECT_TYPE);
        oid.set(SYSTEM_ID, systemId);
        oid.set(SRC_OID, srcOid);
        RemoteOidMapping mapping = null;
        try {
            mapping = new RemoteOidMapping(oid);
        } catch (DataObjectNotFoundException nfe) {
            // return null if none is found.
        }
        return mapping;
    }

    /**
     * @return null if no mapping is found.
     */
    public static String retrieveDstOid(String systemId, String srcOid) {
        RemoteOidMapping mapping = retrieve(systemId, srcOid);
        if (mapping == null) {
            return null;
        }
        return mapping.getDstOid();
    }

    /**
     * @return oid of importer object, or null if no mapping is found
     */
    public static String retrieveDstOid(String systemId, OID srcOid) {
        return retrieveDstOid(systemId, srcOid.toString());
    }

    /**
     * @return true if mapping for given systemId and srcOid exists
     */
    public static boolean exists(String systemId, String srcOid) {
        return retrieve(systemId, srcOid) != null;
    }

    /**
     * @return true if mapping for given systemId and srcOid exists
     */
    public static boolean exists(String systemId, OID srcOid) {
        return retrieve(systemId, srcOid.toString()) != null;
    }

    /**
     * @return source OID for given system ID and destination OID,
     *         or null if no such mapping exists
     */
    public static String retrieveSrcOid(String systemId, String dstOid) {
        RemoteOidMapping map = retrieveByDstOid(systemId, dstOid);
        if (map == null) {
            return null;
        }
        return map.getSrcOid();
    }

    /**
     * @return RemoteOidMapping for given system ID and destination OID,
     *         or null if no such mapping exists
     */
    public static RemoteOidMapping retrieveByDstOid(String systemId, OID dstOid) {
        return retrieveByDstOid(systemId, dstOid.toString());
    }

    /**
     * @return RemoteOidMapping for given system ID and destination OID,
     *         or null if no such mapping exists
     */
    public static RemoteOidMapping retrieveByDstOid(String systemId, String dstOid) {
        DataCollection dc = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        dc.addEqualsFilter(SYSTEM_ID, systemId);
        dc.addEqualsFilter(DST_OID, dstOid);
        if (dc.next()) {
            RemoteOidMapping map = new RemoteOidMapping(dc.getDataObject());
            dc.close();
            return map;
        }
        return null;
    }

    public String getSystemId() {
        return (String) get(SYSTEM_ID);
    }

    public String getSrcOid() {
        return (String) get(SRC_OID);
    }

    public String getDstOid() {
        return (String) get(DST_OID);
    }

    public void setSystemId(String systemId) {
        set(SYSTEM_ID, systemId);
    }

    public void setSrcOid(String srcOid) {
        set(SRC_OID, srcOid);
    }

    public void setDstOid(String dstOid) {
        set(DST_OID, dstOid);
    }

    /**
     *  Here we will take care that all dependants get deleted
     * as well.  We traverse the association tree and issue
     * a delete() on all mappings that exist.
     */
    protected void beforeDelete() {
        super.beforeDelete();
        // 1. find all associated objects
        // 2. check whether any of them has RemoteOidMapping
        //    2a.  if yes, delete the mapping
        DataObject dobj = SessionManager.getSession()
                              .retrieve(OID.valueOf(getDstOid()));
        if (dobj == null) {
            return;
        }
        ObjectType type = dobj.getObjectType();
        for (Iterator i = type.getProperties(); i.hasNext(); ) {
            Property prop = (Property) i.next();

            String propName = prop.getName();
            Object propValue = dobj.get(propName);
            if (propValue == null) {
                continue;
            }

            if (prop.isCollection()) {
                DataAssociationCursor daCursor =
                    ((DataAssociation) propValue).getDataAssociationCursor();
                while (daCursor.next()) {
                    OID oid = daCursor.getDataObject().getOID();
                    RemoteOidMapping associatedMapping = retrieveByDstOid(getSystemId(), oid);
                    if (associatedMapping != null) {
                        associatedMapping.delete();
                    }
                }
            } else if (prop.isRole()) {
                OID oid = ((DataObject)propValue).getOID();
                RemoteOidMapping associatedMapping = retrieveByDstOid(getSystemId(), oid);
                if (associatedMapping != null) {
                    associatedMapping.delete();
                }
            }
        }
    }

}

