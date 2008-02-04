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

// Logging
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Stores auditing information for an object.
 *  <p>
 *    
 *  </p>
 *
 * @author Joseph Bank 
 * @version 1.0
 **/
public class BasicAuditTrail extends DomainObject implements Audited {

    public static final String versionId = "$Id: BasicAuditTrail.java 1547 2007-03-29 14:24:57Z chrisgilbert23 $";
    private static final Logger s_cat =
        Logger.getLogger(BasicAuditTrail.class.getName());

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.auditing.BasicAuditTrail";

    private final static String RETRIEVEQUERY =
        "com.arsdigita.auditing.auditTrailForACSObject";

    public String getBaseDataObjectType() { return BASE_DATA_OBJECT_TYPE; }

    /**
     * Retrieves a BasicAuditTrail object for an ACSObject.
     * @param aobj the object to retrieve the audit trail for
     * @return the BasicAuditTrail object for the specified object.
     */
    public static BasicAuditTrail retrieveForACSObject(ACSObject aobj) {
        if (!aobj.isNew()) {
            DataQuery query =
                SessionManager.getSession().retrieveQuery(RETRIEVEQUERY);
            Filter f = query.addFilter("id=:id");
            f.set("id", aobj.getOID().get("id"));
            try {
                if (query.next()) {
                    return new BasicAuditTrail((BigDecimal)query.get("id"));
                }
            } catch (DataObjectNotFoundException e) {
                s_cat.debug("retrieveForObject: " +
                            "couldn't find audit trail, creating new one." + e);
            } finally {
                query.close();
            }
        }
        return new BasicAuditTrail();
    }

    public BasicAuditTrail(DataObject object) {
        super(object);
    }

    /**
     ** Constructs a new audit trail.
     **/
    public BasicAuditTrail() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    private BasicAuditTrail(BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

	public final static String CREATION_USER = "creationUser";
    public final static String CREATION_DATE = "creationDate";
	public final static String CREATION_IP = "creationIP";
	public final static String LAST_MODIFIED_USER = "lastModifiedUser";
	public final static String LAST_MODIFIED_DATE = "lastModifiedDate";
	public final static String LAST_MODIFIED_IP = "lastModifiedIP";

    /**
     ** Implementation of the Audited interface
     **/

    /**
     * Gets the creation user.
     */
    public User getCreationUser() {
        Object o = get(CREATION_USER);
        if (o == null) {
            return null;
        }
        return User.retrieve((DataObject)o);
    }

    /**
     * Gets the creation date.
     */
    public java.util.Date getCreationDate() {
        return (java.util.Date) get(CREATION_DATE);
    }

    /**
     * Gets the creation IP address.
     */
    public String getCreationIP() {
        return (String) get(CREATION_IP);
    }

    /**
     * Gets the last modified user.
     */
    public User getLastModifiedUser() {
        Object o = get(LAST_MODIFIED_USER);
        if (o == null) {
            return null;
        }
        return User.retrieve((DataObject)o);
    }

    /**
     * Gets the last modified date.
     */
    public java.util.Date getLastModifiedDate() {
        return (java.util.Date) get(LAST_MODIFIED_DATE);
    }

    /**
     * Gets the last modified IP address.
     */
    public String getLastModifiedIP() {
        return (String) get(LAST_MODIFIED_IP);
    }

    //These all have package access so they can be accessed by
    //the AuditingObserver

    void setID(BigDecimal id) {
        if (isNew()) {
            set("id", id);
        } else {
            throw new RuntimeException("Can't set the id " +
                                       "for an existing object");
        }
    }

    void setLastModifiedInfo(User user,
                             java.util.Date date,
                             String ip_addr) {
        setAssociation( LAST_MODIFIED_USER, user);
        set( LAST_MODIFIED_DATE, date);
        set( LAST_MODIFIED_IP, ip_addr);
    }

    void setCreationInfo(User user,
                         java.util.Date date,
                         String ip_addr) {
        setAssociation( CREATION_USER, user);
        set( CREATION_DATE, date);
        set( CREATION_IP, ip_addr);
        setLastModifiedInfo(user, date, ip_addr);
    }

    void setCreationInfo(AuditingSaveInfo info) {
        setCreationInfo(info.getSaveUser(),
                        info.getSaveDate(),
                        info.getSaveIP());
    }

    void setLastModifiedInfo(AuditingSaveInfo info) {
        setLastModifiedInfo(info.getSaveUser(),
                            info.getSaveDate(),
                            info.getSaveIP());
    }

}
