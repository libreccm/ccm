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
import com.arsdigita.kernel.Group;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.web.Web;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;

import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.OID;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * A class that represents a privilege granted to a <code>Party</code>
 * on a <code>DataObject</code>. This class is only used by the
 * <code>PermissionService</code>. This class might be moved into
 * <code>PermissionService</code> as an inner class.
 *
 * @author Phong Nguyen
 * @version 1.0
 * @version $Id: Permission.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class Permission extends DomainObject {

    // Get the category named the same as this class
    private static final Logger s_log = Logger.getLogger(Permission.class);

    // The names of the attributes we use when creating permission
    // objects
    static final String OBJECT_ID = "objectId";
    static final String ID = "id";
    static final String PARTY_ID = "partyId";
    static final String PRIVILEGE = "privilege";

    static final String BASE_DATA_OBJECT_TYPE =
                        "com.arsdigita.kernel.permissions.Permission";

    /**
     * 
     */
    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "Permission".
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     */
    protected Permission() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Creates a Permission object with the specified data object.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(DataObject)
     */
    protected Permission(DataObject data) {
        super(data);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     */
    protected Permission(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Gets the value of the ID property.
     *
     * This is a convenience method that is roughly equivalent
     * to getOID().get("id"). In general, it should be used
     * instead of the getOID method to get any ACSObject's ID.
     *
     * @return the value of the ID property.
     */
    public BigDecimal getID() {
        return (BigDecimal) get(ID);
    }

    /**
     * Returns the <code>OID</code> of the <code>Party</code> that is
     * the grantee of the privilege associated with this
     * <code>Permission</code>.
     *
     * @return The <code>OID</code> of the <code>Party</code> that is
     * the grantee of the privilege.
     *
     * @see com.arsdigita.kernel.Party
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     * @see com.arsdigita.persistence.OID
     */
    public OID getPartyOID() {
        return new OID(Party.BASE_DATA_OBJECT_TYPE, get(PARTY_ID));
    }

    /**
     * Sets the <code>Party</code> specified by the <code>OID</code>
     * <i>partyOID</i> that is the grantee of the
     * <code>PrivilegeDescriptor</code> associated with this
     * <code>Permission</code>.
     *
     * @param partyOID The <code>OID</code> of the <code>Party</code>
     * that is the grantee of the privilege associated
     * with this <code>Permission</code>.
     *
     * @see com.arsdigita.kernel.Party
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     * @see com.arsdigita.persistence.OID
     */
    public void setPartyOID(OID partyOID) {
        set(PARTY_ID, partyOID.get("id"));
    }

    /**
     * Returns the <code>OID</code> of the <code>ACSObject</code> that
     * the privilege is granted on.
     *
     * @return The <code>OID</code> of the <code>ACSObject</code> that
     * the privilege is granted on.
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     * @see com.arsdigita.persistence.OID
     */
    public OID getACSObject() {
        return new OID("com.arsdigita.kernel.ACSObject", get(OBJECT_ID));
    }

    /**
     * Sets the <code>ACSObject</code> specified by the
     * <code>OID</code> <i>acsObjectOID</i> that the
     * <code>PrivilegeDescriptor</code> is granted on.
     *
     * @param acsObjectOID The <code>OID</code> of the
     * <code>ACSObject</code> that the privilege is
     * granted on.
     *
     * @see com.arsdigita.kernel.ACSObject
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     * @see com.arsdigita.persistence.OID
     */
    public void setACSObjectOID(OID acsObjectOID) {
        set(OBJECT_ID, acsObjectOID.get("id"));
    }

    /**
     * Returns the <code>PrivilegeDescriptor</code> associated with this
     * <code>Permission</code>.
     *
     * @return The <code>OID</code> of the <code>PrivilegeDescriptor</code>
     * associated with this <code>Permission</code>.
     *
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     * @see com.arsdigita.persistence.OID
     */
    public PrivilegeDescriptor getPrivilege() {
        return PrivilegeDescriptor.get((String) get(PRIVILEGE));
    }

    /**
     * Sets the <code>PrivilegeDescriptor</code> specified by the
     * <code>OID</code> <i>PrivilegeDescriptorOID</i> associated with this
     * <code>Permission</code>.
     *
     * @param prvivilegeOID The <code>OID</code> of the
     * <code>PrivilegeDescriptor</code> associated with this
     * <code>Permission</code>.
     *
     * @see com.arsdigita.kernel.permissions.PrivilegeDescriptor
     * @see com.arsdigita.persistence.OID
     */
    public void setPrivilege(PrivilegeDescriptor privilege) {
        set(PRIVILEGE, privilege.getName());
    }

    /**
     * Get the user who created the object (may be null)
     */
    public User getCreationUser() {
        Object o = get("creationUser");
        if (o == null) {
            return null;
        }
        return User.retrieve((DataObject)o);
    }

    /**
     * Get the creation date
     */
    public Date getCreationDate() {
        return (Date) get("creationDate");
    }

    /**
     * Get the creation IP address (may be null)
     */
    public String getCreationIP() {
        return (String) get("creationIP");
    }

    /**
     * Persists any changes made to this object.
     *
     * @see com.arsdigita.persistence.DataObject#save()
     */
    protected void beforeSave() throws PersistenceException {
        if (!isNew()) {
            throw new
                RuntimeException("Permission entries cannot be modified");
        }

        Party party = Kernel.getContext().getEffectiveParty();

        if (party == null
                || !party.getID().equals(Kernel.getSystemParty().getID())) {
            if (party == null) {
                try {
                    party = new User
                        (new BigDecimal(PermissionManager.VIRTUAL_PUBLIC_ID));
                } catch (DataObjectNotFoundException e) {
                    ACSObject o;

                    try {
                        o = (ACSObject) DomainObjectFactory.newInstance(getACSObject());
                    } catch (DataObjectNotFoundException de) {
                        throw new IllegalStateException();
                    }

                    throw new PermissionException(PrivilegeDescriptor.ADMIN, o);
                }
            }

            PermissionService.assertPermission
                (new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                          getACSObject(),
                                          party.getOID()));
        }


        setCreationInfo();

        super.beforeSave();
    }

    /**
     * 
     */
    private void setCreationInfo() {
        User user = Web.getWebContext().getUser();
        // The user may be null.
        
        HttpServletRequest req = Web.getRequest();
        String ip = null;

        if (req == null) {
            ip = "127.0.0.1";
        } else {
            ip = req.getRemoteAddr();
        }

        Date date = new Date();

        setAssociation("creationUser", user);
        set("creationDate", date);
        set("creationIP", ip);
    }

    /**
     * Retrieves all objects of this type stored in the database. Very
     * necessary for exporting all entities of the current work environment.
     *
     * @return List of all permissions
     */
    public static List<Permission> getAllObjectPermissions() {
        List<Permission> permissionList = new ArrayList<>();

        final Session session = SessionManager.getSession();
        DomainCollection collection = new DomainCollection(session.retrieve(
                Permission.BASE_DATA_OBJECT_TYPE));

        while (collection.next()) {
            Permission permission = (Permission) collection.getDomainObject();
            if (permission != null) {
                permissionList.add(permission);
            }
        }

        collection.close();
        return permissionList;
    }
}
