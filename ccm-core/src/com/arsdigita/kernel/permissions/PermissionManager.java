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

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * default implementation of PermissionService.
 *
 * @author Oumi Mehrotra
 * @author Michael Bryzek
 * @author Scott Seago
 * @version 1.0
 * @see com.arsdigita.kernel.permissions.PermissionService
 **/
public class PermissionManager {

    // The names of the attributes in the OID that we retrieve
    private static final String OBJECT_ID_ATTRIBUTE = "id";
    private static final String PARTY_ID_ATTRIBUTE = "id";
    public static final int VIRTUAL_PUBLIC_ID = -200;
    public static final int VIRTUAL_REGISTERED_ID = -202;
    public static final int SYSTEM_PARTY = -204;

    /**
     * Checks the permission
     * represented by the passed in {@link PermissionDescriptor}.
     *
     * @param permission the {@link PermissionDescriptor} to
     * provide service to
     *
     * @return <code>true</code> if the PermissionDescriptor's base object has the
     * specified permission; <code>false</code> otherwise.
     **/
    public boolean checkPermission(PermissionDescriptor permission) {


        // For performance, we use different queries depending on whether
        // the party (from the permission descriptor) is a user or a group.
        // Start out assuming the party is a user
        boolean isUser=true;

        OID partyOID = permission.getPartyOID();
        // if partyOID is null, assume it is the public user.
        if (partyOID == null) {
            return checkPermission
                (new PermissionDescriptor(permission.getPrivilegeDescriptor(),
                                          permission.getACSObjectOID(),
                                          getPublicPartyOID()));
        }

        String queryName = "CheckPermissionForParty";
        return doCheck(queryName, permission);
    }

    boolean checkDirectPermission(PermissionDescriptor permission) {
        return checkDirectPermission(permission, true);
    }

    boolean checkDirectPermission(
        PermissionDescriptor permission,
        boolean useImpliedPrivs) {

        if (useImpliedPrivs) {
            return doCheck("CheckDirectGrantWithImpliedPrivileges", permission);
        } else {
            DataQuery query = getQuery("CheckDirectGrant");
            query.setParameter(
                "privilege",
                permission.getPrivilegeDescriptor().getName());

            query.setParameter(
                "objectID",
                permission.getACSObjectOID().get("id"));
            query.setParameter("partyID", constructAccessList(permission.getPartyOID()));

            if (query.next()) {
                query.close();
                return true;
            } else {
                query.close();
                return false;
            }
        }
    }

    /**
     * Check a universal permission.  This will soon be optimized via
     * some sort of caching of universal permissions.
     **/
    private boolean
        checkPermission(UniversalPermissionDescriptor permission)
    {
            return doCheck("CheckUninheritedPermissionForParty",permission);
    }

    private boolean doCheck(String queryName,
                            PermissionDescriptor permission) {
        DataQuery query = getQuery("PermissionCheckPlaceholder");

        Filter f = query.addFilter(" exists ( com.arsdigita.kernel.permissions."
                + queryName + " and  RAW["
                + permission.getPrivilegeDescriptor().getColumnName() + " = '1' ])");
        f.set("objectID", permission.getACSObjectOID().get("id"));
        f.set("partyID", constructAccessList(permission.getPartyOID()));//.get("id"));

        if (query.next()) {
            query.close();
            return true;
        } else {
            query.close();
            return false;
        }
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
     **/
    public void grantPermission(PermissionDescriptor permission) {
        OID partyOID = permission.getPartyOID();
        if (partyOID != null && !checkDirectPermission(permission, false)) {
            final Permission p = new Permission();
            p.setPartyOID(partyOID);
            p.setACSObjectOID(permission.getACSObjectOID());
            p.setPrivilege(permission.getPrivilegeDescriptor());

            if ( KernelConfig.isPermissionCheckEnabled() ) {
                p.save();
            } else {
                // This prevents an assertion failure in the save method.
                // Smells like a hack because PermissionManager shouldn't need
                // to know that there is an assertion in the save() method in
                // the Permission class.
                new KernelExcursion() {
                    public void excurse() {
                        setEffectiveParty(Kernel.getSystemParty());
                        p.save();
                    }
                }.run();
            }
        }
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
     **/
    public void revokePermission(PermissionDescriptor permission) {
        OID partyOID = permission.getPartyOID();
        if (partyOID != null) {
            try {
                Permission p = new Permission(createPermissionOID(permission));
                p.delete();
            } catch (DataObjectNotFoundException e) {
                // Do nothing. This means the record was already deleted.
            }
        }
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
     **/
    public DataObject getContext(OID oid) {
        ObjectContext objContext;

        try {
            objContext = new ObjectContext(oid.get(OBJECT_ID_ATTRIBUTE));
            return objContext.getContext();
        } catch (DataObjectNotFoundException e) {
            return null;
        }
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
     **/
    public DataObject getContext(ACSObject acsObject) {
        return getContext(acsObject.getOID());
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
    public void setContext(ACSObject acsObject, ACSObject context)
        throws PersistenceException
    {
        ObjectContext objContext;

        try {
            objContext = new ObjectContext(acsObject.getID());
        } catch (DataObjectNotFoundException e) {
            objContext = new ObjectContext();
            objContext.setObject(acsObject);
        }
	// only do the update if the value has changed
        if (context == null) {
        	if (objContext.getContext() != null) {
        		objContext.setContext(context);
        	}
        } else {
        	if (!context.getOID().equals(objContext.getContextOID())){
        		// objContext.getContextOID returns null if context is null
        objContext.setContext(context);
    }
        }
       
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
    public void setContext(OID acsObjectOID, OID contextOID)
        throws PersistenceException
    {
        ObjectContext objContext;

        try {
            objContext =
                new ObjectContext(acsObjectOID.get(OBJECT_ID_ATTRIBUTE));
        } catch (DataObjectNotFoundException e) {
            objContext = new ObjectContext();
            objContext.setObject(acsObjectOID);
        }

        objContext.setContext(contextOID);
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
     * @param acsObject the object whose permission context is being
     * set
     *
     */
    public void clonePermissions(ACSObject acsObject)
        throws PersistenceException
    {
        clonePermissions(acsObject.getOID());
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
     *
     */
    public void clonePermissions(OID acsObjectOID)
        throws PersistenceException
    {
        DataObject permParent = getContext(acsObjectOID);
        setContext(acsObjectOID, null);
        if (permParent != null) {
            ObjectPermissionCollection perms =
                getGrantedPermissions(permParent.getOID());

            while (perms.next()) {
                PermissionDescriptor desc =
                    new PermissionDescriptor(perms.getPrivilege(), acsObjectOID,
                                             perms.getGranteeOID());
                grantPermission(desc);
            }
        }
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
    public ObjectPermissionCollection getGrantedPermissions(OID acsObjectOID)
    {
        try {
            ObjectType.verifySubtype(ACSObject.BASE_DATA_OBJECT_TYPE,
                                     acsObjectOID.getObjectType());
        } catch (RuntimeException e) {
            throw new UncheckedWrapperException("The OID for the ACSObject has an " +
                                       "invalid object type.\nExpecting: " +
                                       ACSObject.BASE_DATA_OBJECT_TYPE +
                                       "\nActual: " +
                                       acsObjectOID.getObjectType().getQualifiedName(), e);
        }

        DataQuery query = getQuery("ObjectPermissionCollection");
        query.setParameter("objectID", acsObjectOID.get("id"));
        query.addOrder("isInherited");
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
    public ObjectPermissionCollection
        getGrantedUniversalPermissions()
    {
        return getGrantedPermissions(UniversalPermissionDescriptor.ROOT_CONTEXT_OID);
    }

    /**
     * <BR>
     * Filters a data collection to include only those objects that the
     * specified party has the specified privilege on.
     *
     * @param dataCollection the collection to filter. Must be
     * a collection of type ACSObject.
     * @param privilege the required privilege
     * @param partyOID the OID of the party whose access is being filtered
     *
     */
    public void filterObjects(DataCollection dataCollection,
                              PrivilegeDescriptor privilege,
                              OID partyOID) {

        ObjectType.verifySubtype(ACSObject.BASE_DATA_OBJECT_TYPE,
                                 dataCollection.getObjectType());

        filterQuery(dataCollection, "id", privilege, partyOID);
    }

    /**
     *
     * Filters a data query to include only those results where the
     * specified party has the specified privilege on the ACSObject identified
     * by the specified property name.
     *
     * @param dataQuery the query to filter
     *
     * @param propertyName the name of the query property that contains the
     * ID values to filter. (The values are assumed to be IDs of ACSObjects.)
     *
     * @param privilege the required privilege
     *
     * @param partyOID the OID of the party whose access is being filtered
     *
     */
    public void filterQuery(DataQuery dataQuery,
                            String propertyName,
                            PrivilegeDescriptor privilege,
                            OID partyOID) {

        // Substitute the Public User if no OID is specified.
        partyOID = checkOID(partyOID);

        try {
            ObjectType.verifySubtype(Party.BASE_DATA_OBJECT_TYPE,
                                     partyOID.getObjectType());
        } catch (RuntimeException e) {
            throw new UncheckedWrapperException("The OID for the Party has an " +
                                       "invalid object type.\nExpecting: " +
                                       Party.BASE_DATA_OBJECT_TYPE +
                                       "\nActual: " +
                                       partyOID.getObjectType().getQualifiedName(), e);
        }

        Filter f = getFilterQuery( dataQuery.getFilterFactory(),
                                   propertyName,
                                   privilege,
                                   partyOID );
        dataQuery.addFilter( f );
    }

    /**
     *
     * Returns a Filter to include only those results where the specified party
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
     * @param partyOID the OID of the party whose access is being filtered
     *
     * @return a filter which is true if the party has the required permission
     * on the specified property
     */
    public Filter getFilterQuery(FilterFactory factory,
                                 String propertyName,
                                 PrivilegeDescriptor privilege,
                                 OID partyOID) {
        partyOID = checkOID(partyOID);

        try {
            ObjectType.verifySubtype(Party.BASE_DATA_OBJECT_TYPE,
                                     partyOID.getObjectType());
        } catch (RuntimeException e) {
            throw new UncheckedWrapperException("The OID for the Party has an " +
                                       "invalid object type.\nExpecting: " +
                                       Party.BASE_DATA_OBJECT_TYPE +
                                       "\nActual: " +
                                       partyOID.getObjectType().getQualifiedName(),e);
        }

        UniversalPermissionDescriptor universalPermission =
            new UniversalPermissionDescriptor(privilege, partyOID);

        if (!checkPermission(universalPermission)) {


            Filter f = factory.simple(
                " exists ( com.arsdigita.kernel.permissions.PartyPermissionFilterQuery"
                + " and RAW[dogc.pd_object_id] = " + propertyName
                + " and RAW[" + privilege.getColumnName() + "] = '1')");
             f.set("partyID", constructAccessList(partyOID));//partyOID.get("id"));
             return f;
        }

        // Party has universal permission, so return always true filter
        return factory.simple( "1 = 1" );
    }

    public Filter getObjectFilterQuery(FilterFactory factory,
                                       String propertyName,
                                       PrivilegeDescriptor privilege,
                                       OID objectOID) {
        Filter f = factory.simple(
                " exists ( com.arsdigita.kernel.permissions.ObjectPermissionFilterQuery"
                + " and RAW[dgm.pd_member_id] = " + propertyName
                + " and RAW[" + privilege.getColumnName() + "] = '1')");
        f.set("objectID", objectOID.get("id"));
        return f;
    }

    /**
     * Gets the privileges that the specified party has on the
     * specified object.
     *
     * @param object the OID of the target object of the privileges to be returned
     * @param party the OID of the party that privileges are to be returned for
     *
     * @return an iterator of PrivilegeDescriptors.
     **/
    public Iterator getPrivileges(OID object, OID party) {

        return getPrivilegeSet(object, party, false).iterator();
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
     **/
    public Iterator getImpliedPrivileges(OID object, OID party) {
        return getPrivilegeSet(object, party, true).iterator();
    }

    /**
     * Gets the privileges that the specified <i>party</i> has on the
     * specified <i>object</i>.
     *
     * @param object The OID of the target object of the returned privilegs.
     * @param party The OID of the party who has the returned privileges.
     *
     * @return A set of privileges
     **/
    private HashSet getPrivilegeSet(OID object, OID party, boolean impliedPrivs) {
        try {
            ObjectType.verifySubtype(ACSObject.BASE_DATA_OBJECT_TYPE,
                                     object.getObjectType());
        } catch (RuntimeException e) {
            throw new UncheckedWrapperException("The OID for the ACSObject has an " +
                                       "invalid object type.\nExpecting: " +
                                       ACSObject.BASE_DATA_OBJECT_TYPE +
                                       "\nActual: " +
                                       object.getObjectType().getQualifiedName(),e);
        }
        try {
            ObjectType.verifySubtype(Party.BASE_DATA_OBJECT_TYPE,
                                     party.getObjectType());
        } catch (RuntimeException e) {
            throw new UncheckedWrapperException("The OID for the Party has an " +
                                       "invalid object type.\nExpecting: " +
                                       Party.BASE_DATA_OBJECT_TYPE +
                                       "\nActual: " +
                                       party.getObjectType().getQualifiedName(),e);
        }

        DataQuery query;
        if (impliedPrivs) {
            query = getQuery("PrivilegesForParty");
        } else {
            query = getQuery("ImpliedPrivilegesForParty");
        }

        query.setParameter("objectID", object.get("id"));
        query.setParameter("partyID", party.get("id"));

        HashSet set = new HashSet();
        while (query.next()) {
            set.add(PrivilegeDescriptor.get((String) query.get("privilege")));
        }
        query.close();
        return set;
    }

    /**
     * Return an <code>OID</code> for retrieving a
     * {@link Permission} generated from the
     * {@link PermissionDescriptor} specified by
     * <i>PermissionDescriptor</i>.
     *
     * @param permission The {@link PermissionDescriptor} used
     * to generate an <code>OID</code> for a permission.
     *
     * @return An <code>OID</code> for retrieving a
     * <code>Permission</code> generated from the
     * {@link PermissionDescriptor} specified by
     * <i>PermissionDescriptor</i>.
     *
     * @see com.arsdigita.kernel.permissions.PermissionDescriptor
     * @see com.arsdigita.kernel.permissions.Permission
     * @see com.arsdigita.persistence.OID
     **/
    private OID createPermissionOID(PermissionDescriptor permission) {
        OID oid = new OID(Permission.BASE_DATA_OBJECT_TYPE);
        oid.set(Permission.OBJECT_ID,
                permission.getACSObjectOID().get(OBJECT_ID_ATTRIBUTE));
        oid.set(Permission.PARTY_ID,
                permission.getPartyOID().get(PARTY_ID_ATTRIBUTE));
        oid.set(Permission.PRIVILEGE,
                permission.getPrivilegeDescriptor().getName());
        return oid;
    }

    // Gets the query with the specified name.  Assumes the query is in
    // the model called com.arsdigita.kernel.permissions.
    private DataQuery getQuery(String queryName) {
        return SessionManager.getSession()
            .retrieveQuery("com.arsdigita.kernel.permissions." + queryName);
    }

    /**
     * Returns the OID of the Virtual Public User.
     *
     * @return The Virtual Public User OID.
     */
    private OID getPublicPartyOID() {
        return new OID(User.BASE_DATA_OBJECT_TYPE, VIRTUAL_PUBLIC_ID);
    }

    /**
     * Returns the collection of Users to include in permission checks for
     * a given User.  if the User is the Virtual Public User, then only
     * the Virtual Public User is appropriate.  if the User is an authenticated
     * User, that User, the Virtual Public User, and the Virtual registered User
     * must also be checked as a permission granted to either Virtual User must
     * be extended to any real User.
     *
     * This has been made "public static" instead of "private" so that
     * the information can be used for an inner query in
     * FolderTreeModelBuilder in CMS
     * @param partyOID an <code>OID</code> value
     * @return a <code>Collection</code> value
     *
     * @deprecated this is slower than using "normal" permission checks
     * since "in ("") transformed into union by Oracle SQL Optimizer
     */
    public static Collection constructAccessList(OID partyOID) {
        ArrayList users = new ArrayList();
        BigDecimal partyId = (BigDecimal)partyOID.get("id");

        // if the public user is checking, only check if the public user
        // has access.
        if ( partyId.equals(new BigDecimal(VIRTUAL_PUBLIC_ID)) ) {
            users.add(new BigDecimal(VIRTUAL_PUBLIC_ID));
        } else {
            // if a registered user is checking, check if the public
            // user and all registered users have access.
            users.add(new BigDecimal(VIRTUAL_PUBLIC_ID));
            users.add(partyId);
            users.add(new BigDecimal(VIRTUAL_REGISTERED_ID));
        }
        return users;
    }


    /**
     *  this takes in an OID and returns the public OID if the passed in
     *  value is null.  Otherwise, it returns the passed in value.
     */
    public OID checkOID(OID oid) {
        // Substitute the Public User if no OID is specified.
        if ( oid == null) {
            oid = getPublicPartyOID();
        }
        return oid;
    }

}
