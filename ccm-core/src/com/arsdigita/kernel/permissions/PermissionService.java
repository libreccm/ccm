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

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainService;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.UncheckedWrapperException;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * A utility class for controlling user access to domain/data objects.
 * @see com.arsdigita.kernel.permissions.PermissionDescriptor
 *
 * @author Oumi Mehrotra
 * @author Michael Bryzek
 * @version 1.0
 * @version $Id: PermissionService.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class PermissionService extends DomainService {

    /** Private logger instance for debugging purpose  */
    private static final Logger s_log = Logger.getLogger(PermissionService.class);

    // Reference to the PermissionManager to use for permissions checks.
    private static PermissionManager s_manager = null;

    /**
     * Gets the current PermissionManager for PermissionService. If one has not
     * yet been initialized, a new PermissionManager (of the default
     * implementation) is created.
     *
     * @return The permission manager currently in use
     */
    private static PermissionManager getPermissionManager() {
        if (s_manager == null) {
            s_manager = new PermissionManager();
        }
        return s_manager;
    }

    /**
     * Sets the PermissionManager
     *
     * @param manager The permission manager to use. If the value is null,
     * the default PermissionManager is used,
     */
    public static void setPermissionManager(PermissionManager manager) {
        if (manager == null) {
            s_manager = new PermissionManager();
        } else {
            s_manager = manager;
        }
    }

    private static boolean s_enabled = true;


    /**
     * Returns true if permission checks are currently enabled, false if they
     * are disabled.
     *
     * @return True if permission checks are currently enabled, false if they
     *         are disabled.
     */
    public static final boolean isEnabled() {
        return s_enabled;
    }


    /**
     * Used to enable or disable permission checks on a system wide basis.
     *
     * @param value If value is false then permission wide system checks are
     *              disabled.
     */
    public static final void setEnabled(boolean value) {
        s_enabled = value;
    }

    /**
     * Throws a PermissionException if the result of checkPermission on the
     * given PermissionDescriptor is false.
     *
     * @param permission the {@link PermissionDescriptor} to check
     */
    public static void assertPermission(PermissionDescriptor permission) {
        if (!isEnabled()) { return; }
        if (!checkPermission(permission)) {
            throw new PermissionException(permission);
        }
    }


    /**
     * Checks the permission
     * represented by the passed in {@link PermissionDescriptor}.
     *
     * @param permission the {@link PermissionDescriptor} to
     * provide service to
     *
     * @return <code>true</code> if the PermissionDescriptor's base object has the
     * specified permission; <code>false</code> otherwise.
     */
    public static boolean checkPermission(PermissionDescriptor permission) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("checking " + permission.getPrivilegeDescriptor()
                        + " on " + permission.getACSObjectOID()
                        + " for " + permission.getPartyOID());
        }
        return getPermissionManager().checkPermission(permission);
    }

    public static boolean checkDirectPermission(PermissionDescriptor permission) {
        return getPermissionManager().checkDirectPermission(permission);
    }

    /**
     * Grants the permission as
     * specified by the PermissionDescriptor
     * parameters.
     *
     * @param permission the PermissionDescriptor to retrieve
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     */
    public static void grantPermission(PermissionDescriptor permission) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("granting " + permission.getPrivilegeDescriptor()
                        + " on " + permission.getACSObjectOID()
                        + " for " + permission.getPartyOID());
        }
        getPermissionManager().grantPermission(permission);
    }

    /**
     * Revokes the permission that is
     * specified by the passed in
     * {@link PermissionDescriptor}.
     *
     * @param permission the PermissionDescriptor that contains the
     * parameters of the permission to revoke
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     */
    public static void revokePermission(PermissionDescriptor permission) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("revoking " + permission.getPrivilegeDescriptor()
                        + " on " + permission.getACSObjectOID()
                        + " for " + permission.getPartyOID());
        }
        getPermissionManager().revokePermission(permission);
    }

    /**
     * Returns the data object that serves as the permission
     * context of the ACS object specified by OID.
     * The permission context is the object from which the specified
     * object inherits permissions.
     *
     * @param oid the OID of the ACS object
     * for which to retrieve the permission context
     *
     * @return the data object that serves as the permission
     * context of the ACS object specified by OID.
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.persistence.OID
     */
    public static DataObject getContext(OID oid) {
        return getPermissionManager().getContext(oid);
    }


    /**
     * Returns the data object that serves as the permission
     * context of the specified ACS object.
     * The permission context is the object from which the specified
     * object inherits permissions.
     *
     * @param acsObject the ACS object for which to retrieve
     * the permission context
     *
     * @return the data object that serves as the permission
     * context of the specified ACS object.
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.persistence.OID
     */
    public static DataObject getContext(ACSObject acsObject) {
        return getPermissionManager().getContext(acsObject);
    }

    /**
     * Sets the permission context of the object specified by <i>acsObject</i>
     * to the object specified by <i>context</i>.  This means
     * that the object specified by <i>acsObject</i> will inherit
     * any permissions on the object specified by <i>context</i>.
     * If the context is set to null, the object does not
     * inherit permissions from any other object, but universal permissions
     * still apply.
     *
     * @exception PersistenceException when the setting of the
     * permission context could not be saved.
     *
     * @param acsObject the object whose permission context is being
     * set
     *
     * @param context the object to set as the permission context
     *
     * @see UniversalPermissionDescriptor
     */
    public static void setContext(ACSObject acsObject, ACSObject context)
        throws PersistenceException
    {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting context of " + acsObject + " to " +
                        context);
        }

        getPermissionManager().setContext(acsObject, context);
    }

    /**
     * Sets the permission context of the object specified by <i>acsObjectOID</i>
     * to the object specified by <i>contextOID</i>.  This means
     * that the object specified by <i>acsObjectOID</i> will inherit
     * any permissions on the object specified by <i>contextOID</i>.
     * If the context is set to null, the object does not
     * inherit permissions from any other object, but universal permissions
     * still apply.
     *
     * @exception PersistenceException when the setting of the
     * permission context could not be saved.
     *
     * @param acsObjectOID the object whose permission context is being
     * set
     *
     * @param contextOID the object to set as the permission context
     *
     * @see UniversalPermissionDescriptor
     */
    public static void setContext(OID acsObjectOID, OID contextOID)
        throws PersistenceException
    {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting context of " + acsObjectOID + " to " +
                        contextOID);
        }
        getPermissionManager().setContext(acsObjectOID, contextOID);
    }

    /**
     * Adds explicit permissions to the object specified by
     * <i>acsObject</i> to all permissions currently inherited from
     * its permission context and resets the permissions context to
     * null.
     *
     * @exception PersistenceException when the setting of the
     * permission context could not be saved.
     *
     * @param acsObject the object whose permission context is being
     * set
     */
    public static void clonePermissions(ACSObject acsObject)
        throws PersistenceException
    {
        getPermissionManager().clonePermissions(acsObject);
    }

    /**
     * Adds explicit permissions to the object specified by
     * <i>acsObjectOID</i> to all permissions currently inherited from
     * its permission context and resets the permissions context to
     * null.
     *
     * @exception PersistenceException when the setting of the
     * permission context could not be saved.
     *
     * @param acsObjectOID the object whose permission context is being
     * set
     */
    public static void clonePermissions(OID acsObjectOID)
        throws PersistenceException
    {
        getPermissionManager().clonePermissions(acsObjectOID);
    }

    /**
     *
     * Returns the set of permissions that have been granted on
     * the specified object, including those inherited from
     * the object's permission context.  In the result set,
     * direct permissions are returned first, followed by inherited
     * permissions.
     *
     * @param acsObjectOID the OID of the ACS object whose permissions
     * are to be returned
     *
     * @return the permissions that have been granted on the specified
     * object (direct permissions followed by inherited
     * permisions).
     */
    public static ObjectPermissionCollection
        getGrantedPermissions(OID acsObjectOID) {
        return getPermissionManager().getGrantedPermissions(acsObjectOID);
    }

    /**
     *  Returns the set of
     * permissions that have been granted directly on the specified
     * object, excluding those inherited from the object's permission
     * context.
     *
     * @param acsObjectOID the OID of the ACS object whose permissions
     * are to be returned
     *
     * @return the permissions that have been granted on the specified
     * object (direct permissions followed by inherited
     * permisions).
     */
    public static ObjectPermissionCollection
        getDirectGrantedPermissions(OID acsObjectOID)
    {
        checkType(acsObjectOID, ACSObject.BASE_DATA_OBJECT_TYPE);

        DataQuery query = getQuery("ObjectDirectPermissionCollection");
        query.setParameter("objectID", acsObjectOID.get("id"));
        query.addOrder("granteeID");
        query.addOrder("privilege");
        return new ObjectPermissionCollection(query);
    }

    /**
     *
     * Returns the set of permissions that have been granted universally.
     *
     * @return the permissions that have been granted to all ACS objects.
     */
    public static ObjectPermissionCollection
        getGrantedUniversalPermissions()
    {
        return getPermissionManager().getGrantedUniversalPermissions();
    }

    /**
     *
     * Filters a data collection to include only those objects that the
     * specified user has the specified privilege on.
     *
     * @param dataCollection the collection to filter. Must be
     * a collection of type ACSObject.
     * @param privilege the required privilege
     * @param userOID the OID of the user whose access is being filtered
     */
    public static void filterObjects(DataCollection dataCollection,
                                     PrivilegeDescriptor privilege,
                                     OID userOID) {
        getPermissionManager().filterObjects(dataCollection,
                                             privilege,
                                             userOID);
    }

    /**
     *
     * Filters a domain collection to include only those objects that the
     * specified user has the specified privilege on.
     *
     * @param domainCollection the collection to filter
     * @param privilege the required privilege
     * @param userOID the OID of the user whose access is being filtered
     */
    public static void filterObjects(DomainCollection domainCollection,
                                     PrivilegeDescriptor privilege,
                                     OID userOID) {
        if (!isEnabled()) { return; }

        filterObjects(getDataCollection(domainCollection),
                      privilege, userOID);
    }

    /**
     * <BR>
     * Filters a data query to include only those results where the
     * specified user has the specified privilege on the ACSObject identified
     * by the specified property name.
     *
     * @param dataQuery the query to filter
     *
     * @param propertyName the name of the query property that contains the
     * ID values to filter. (The values are assumed to be IDs of ACSObjects.)
     *
     * @param privilege the required privilege
     *
     * @param userOID the OID of the user whose access is being filtered
     */
    public static void filterQuery(DataQuery dataQuery,
                                   String propertyName,
                                   PrivilegeDescriptor privilege,
                                   OID userOID) {
        if (!isEnabled()) { return; }

        Filter f = getFilterQuery(dataQuery.getFilterFactory(),
                                  propertyName,
                                  privilege,
                                  userOID);
        dataQuery.addFilter(f);
    }

    /**
     *
     * Filters a data query that retrieves a list of users by only
     * allowing the users who have access to the specified
     * object. This filter handles three cases:
     *
     * <ol>
     *  <li>The grantee is a user.</li>
     *  <li>The grantee is a group.</li>
     *  <li>The grantee is a site-wide admin.</li>
     * </ol>
     *
     * @param dataQuery the query to filter
     *
     * @param propertyName the name of the query property that contains the
     * ID values to filter.
     *
     * @param privilege the required privilege
     *
     * @param objectOID the OID of the object that the users are trying to access
     */
    public static void objectFilterQuery(DataQuery dataQuery,
                                         String propertyName,
                                         PrivilegeDescriptor privilege,
                                         OID objectOID) {
        if ( !isEnabled() ) { return; }

        Filter f = getObjectFilterQuery(dataQuery.getFilterFactory(),
                                        propertyName,
                                        privilege,
                                        objectOID);
        dataQuery.addFilter(f);

    }

    /**
     *
     * Returns a Filter to include only those results where the specified user
     * has the specified privilege on the ACSObject identified by the specified
     * property name.
     *
     * @param factory A FilterFactory to generate the filter
     *
     * @param propertyName the name of the query property that contains the
     * ID values to filter. (The values are assumed to be IDs of ACSObjects.)
     *
     * @param privilege the required privilege
     *
     * @param partyOID the OID of the user whose access is being filtered
     *
     * @return a filter which is true if the user has the required permission
     * on the specified property
     */
    public static Filter getFilterQuery(FilterFactory factory,
                                        String propertyName,
                                        PrivilegeDescriptor privilege,
                                        OID partyOID) {

        return getPermissionManager().getFilterQuery
            ( factory, propertyName, privilege, partyOID );
    }

    public static Filter getObjectFilterQuery(FilterFactory factory,
                                              String propertyName,
                                              PrivilegeDescriptor privilege,
                                              OID oid) {
        return getPermissionManager().getObjectFilterQuery
            (factory, propertyName, privilege, oid);
    }

    /**
     * Gets the privileges that the specified party has been directly granted
     * on the specified object.
     *
     * @param object the OID of the specified object
     * @param party the OID of the specified party
     */
    public static Iterator getDirectPrivileges(OID object, OID party) {
        checkType(object, ACSObject.BASE_DATA_OBJECT_TYPE);
        checkType(party, Party.BASE_DATA_OBJECT_TYPE);

        DataCollection dc = SessionManager.getSession()
            .retrieve("com.arsdigita.kernel.permissions.Permission");
        dc.addEqualsFilter("objectId", object.get("id"));
        dc.addEqualsFilter("partyId", party.get("id"));

        ArrayList privs = new ArrayList();
        while(dc.next()) {
            privs.add(PrivilegeDescriptor.get((String) dc.get("privilege")));
        }
        dc.close();

        return privs.iterator();
    }

    /**
     * Gets the privileges that the specified party has on the
     * specified object.
     *
     * @param object the OID of the target object of the privileges to be returned
     * @param party the OID of the party that privileges are to be returned for
     *
     * @return an iterator of PrivilegeDescriptors.
     */
    public static Iterator getPrivileges(OID object, OID party) {
        return getPermissionManager().getPrivileges(object, party);
    }

    /**
     * Gets all the privileges that the specified party has on the
     * specified object, including implied privileges. If
     * PrivilegeDescriptor.ADMIN is returned, then all privileges are
     * returned because admin implies all privileges.
     *
     * @param object the OID of the target object of the privileges to be returned
     * @param party the OID of the party that privileges are to be returned for
     *
     * @return an iterator of PrivilegeDescriptors.
     *
     * @see #getPrivileges(OID, OID)
     */
    public static Iterator getImpliedPrivileges(OID object, OID party) {
        return getPermissionManager().getImpliedPrivileges(object, party);
    }


    /**
     * Revoke all permissions belonging to the specified party.
     *
     * @param partyOID OID of the party whose permissions are to be revoked.
     */
    public static void revokePartyPermissions(OID partyOID) {
        DataOperation revoke = getDataOperation("RevokePartyPermissions");
        revoke.setParameter("partyID", partyOID.get("id"));
        revoke.execute();
    }

    /**
     * Verifies the type of the specified OID against the specified type.
     *
     * @param objectOID the specified OID
     * @param baseTypeName the type the OID is checked for
     * @throws RuntimeException when specified OID is not an instance of the
     * specified type.
     */
    private static void checkType(OID objectOID, String baseTypeName) {
        try {
            ObjectType.verifySubtype(baseTypeName,
                                     objectOID.getObjectType());
        } catch (RuntimeException e) {
            throw new UncheckedWrapperException(
                                       "The OID has an invalid object type.\n" +
                                       "Expected: " + baseTypeName + "\nActual: " +
                                       objectOID.getObjectType().getQualifiedName(),e);
        }
    }

    private static DataOperation getDataOperation(String opName) {
        return SessionManager.getSession()
            .retrieveDataOperation("com.arsdigita.kernel.permissions." +
                                   opName);
    }

    // Gets the query with the specified name.  Assumes the query is in
    // the model called com.arsdigita.kernel.permissions.
    private static DataQuery getQuery(String queryName) {
        return SessionManager.getSession()
            .retrieveQuery("com.arsdigita.kernel.permissions." + queryName);
    }
}
