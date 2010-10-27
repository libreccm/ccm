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
package com.arsdigita.kernel;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * 
 * Represents a role within a group. The class of users who have a given role
 * within a group are treated as a party, so that permissions can be assigned
 * to a role within a group.
 *
 * @see com.arsdigita.kernel.Group#createRole(String)
 * @author Michael Bryzek 
 * @version 1.0
 * @version $Id: Role.java 1169 2006-06-14 13:08:25Z fabrice $
 **/
public class Role extends DomainObject {

    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.kernel.Role";

    private Group m_sourceGroup;
    private Group m_roleGroup;

    private static final String ROLE_ID = "roleId";
    private static final String NAME = "name";
    private static final String GROUP = "sourceGroup";
    private static final String DESCRIPTION = "description";

    // TODO: Hack until we integrated roles with permissions
    private static final String IMPLICIT_GROUP = "implicitGroup";

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "Role".
     *
     * @param group The group for which you are creating this role
     * @param roleName The name of the role you are creating.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    Role(Group group, String roleName) {
        super(BASE_DATA_OBJECT_TYPE);
        setGroup(group);
        setName(roleName);
    }


    /**
     * Constructor.
     *
     * @param dataObject The data object to use to create this role
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(DataObject)
     **/
    public Role(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid the <code>OID</code> for the retrieved
     * <code>DataObject</code>
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public Role(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }


    /**
     * Wrapper for {@link #Role(OID)}.
     **/
    public Role(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    protected void initialize() {
        super.initialize();
        if (getID() == null) {
            try {
                setID(Sequences.getNextValue());
            } catch (SQLException e) {
                throw new UncheckedWrapperException(e);
            }
        }
    }


    /**
     * should only be called once on new objects
     **/
    private void setID(BigDecimal id) {
        set(ROLE_ID, id);
    }

    /**
     * Gets the ID of this role.
     *
     * @return the ID of this role.
     **/
    public BigDecimal getID() {
        return (BigDecimal) get(ROLE_ID);
    }

    /**
     * Sets the name of this role.
     *
     * @param name the name of this role
     **/
    public void setName(String name) {
        set(NAME, name);
        DataObject obj = (DataObject) get(IMPLICIT_GROUP);
        if (obj == null) {
            // Create the subgroup to store people in this role.
            m_roleGroup = new Group();
            m_roleGroup.addSupergroup(getSourceGroup());
            setAssociation(IMPLICIT_GROUP, m_roleGroup);
        } else {
            initializeRoleGroup();
        }
        // Set/Update the name of the subgroup
        m_roleGroup.setName(getSourceGroup().getName() + " " + name);
    }

    /**
     * Gets the name of this role.
     *
     * @return the name of this role.
     **/
    public String getName() {
        return (String) get(NAME);
    }

    /**
     * Sets the description of this role.
     *
     * @param description the description of this role
     **/
    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    /**
     * Gets the description of this role.
     *
     * @return the description of this role.
     **/
    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    /**
     * Sets the context <code>Group</code> with an
     * <code>OID</code> specified by <code>groupOID</code>
     * that this <code>Role</code> refers to.
     *
     * @param group The <code>Group</code> to set
     *
     * @see com.arsdigita.kernel.Group
     * @see com.arsdigita.persistence.OID
     *
     * @pre group != null
     **/
    private void setGroup(Group group) {
        setAssociation(GROUP, group);
    }


    /**
     * Returns a group of the members in a given role.
     *
     * @param group The <code>Group</code> to set
     *
     * @see com.arsdigita.kernel.Group
     * @see com.arsdigita.persistence.OID
     **/
    private Group getSourceGroup() {
        if (m_sourceGroup == null) {
            m_sourceGroup = new Group((DataObject) get(GROUP));
        }
        return m_sourceGroup;
    }


    /**
     * Returns a collection of users that are direct members in this role.
     *
     * NOTE: Any prior calls to addMember() or removeMember() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing the member(s).
     *
     * @deprecated Use Role.getContainedUsers()
     * @return a collection of users that are direct members in this role.
     **/
    public UserCollection getMemberUsers() {
        return getContainedUsers();
    }


    /**
     * Returns a collection of users that are direct members in this role.
     *
     * NOTE: Any prior calls to addMember() or removeMember() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing the member(s).
     *
     * @return a collection of users that are direct members in this role.
     **/
    public UserCollection getContainedUsers() {
        initializeRoleGroup();
        return m_roleGroup.getMemberUsers();
    }


    /**
     * Returns a collection of groups that belong to this role.
     *
     * NOTE: Any prior calls to add() or remove() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing the member(s).
     *
     * @return a collection of groups that belong to this role.
     **/
    public GroupCollection getContainedGroups() {
        initializeRoleGroup();
        return m_roleGroup.getSubgroups();
    }

    /**
     * Returns a collection of parties (users and subgroups) that
     * belong to this role.
     *
     * NOTE: Any prior calls to add() or remove() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing any members.
     *
     * @return a collection of parties that belong to this role
     **/
    public PartyCollection getContainedParties() {
        initializeRoleGroup();
        return m_roleGroup.getContainedParties();
    }

    /**
     * Adds a party to this role.
     *
     * @param party the party to add to this role
     **/
    public void add(Party party) {
        initializeRoleGroup();
        if (party instanceof User) {
            m_roleGroup.addMember((User) party);
        } else if (party instanceof Group) {
            m_roleGroup.addSubgroup((Group) party);
        }
    }

    /**
     * Removes a party from this role.
     *
     * @param party the party to remove from this role
     **/
    public void remove(Party party) {
        initializeRoleGroup();
        if (party instanceof User) {
            m_roleGroup.removeMember((User) party);
        } else if (party instanceof Group) {
            m_roleGroup.removeSubgroup((Group) party);
        }
    }


    /**
     * Adds a user to this role.
     *
     * @deprecated Use Role.add()
     * @param user the user to add to this role
     **/
    public void addMember(User user) {
        add(user);
    }

    /**
     * Removes a user from this role.
     *
     * @deprecated Use Role.remove()
     * @param user the user to remove from this role
     **/
    public void removeMember(User user) {
        remove(user);
    }


    /**
     * Initializes the group (m_roleGroup) storing members in this role.
     **/
    private void initializeRoleGroup() {
        if (m_roleGroup == null) {
            DataObject obj = (DataObject) get(IMPLICIT_GROUP);
            if (obj == null) {
                throw new RuntimeException
                    ("There is no implicit group set up for the role named \"" +
                     getName() + ".\" You probably forgot to call save() " +
                     "prior to adding members to this role");
            }
            m_roleGroup = new Group(obj);
        }
    }

    /**
     * Persists any changes made to this object. Also automatically
     * generates an ID for this role if one is not already specified.
     *
     * @see com.arsdigita.persistence.DataObject#save()
     **/
    protected void beforeSave() {
        if (m_roleGroup != null) {
            m_roleGroup.save();
        }
        super.beforeSave();
    }

    /**
     * Deletes this role and all the users in it.
     **/
    public void delete() throws PersistenceException {
        deleteRoleGroup();
        super.delete();
    }


    /**
     * Deletes the group that represents this role. Removes all its
     * members and removes the group as a subgroup of this roles
     * source group.
     **/
    private void deleteRoleGroup() {
        DataObject obj = (DataObject) get(IMPLICIT_GROUP);
        if (obj != null) {
            initializeRoleGroup();
            // Remove all the members, then remove the group
            UserCollection members = m_roleGroup.getAllMemberUsers();
            while (members.next()) {
                m_roleGroup.removeMember(members.getUser());
            }
            GroupCollection subgroups = m_roleGroup.getAllSubgroups();
            while (subgroups.next()) {
                m_roleGroup.removeSubgroup(subgroups.getGroup());
            }
            // We have to save the group to register the removal of the members.
            m_roleGroup.save();

            // Remove the subgroup association
            Group sourceGroup = getSourceGroup();
            m_roleGroup.removeSupergroup(sourceGroup);
            m_roleGroup.save();

            // Now delete the actual group
            m_roleGroup.delete();
        }
    }


    /**
     * Grants users in this role the specified privilege on the
     * specified target ACS object.
     * @param target the ACS object on which to grant the privilege
     * @param priv the privilege to grant
     *
     * @see PermissionService#grantPermission(PermissionDescriptor)
     **/
    public void grantPermission(ACSObject target, PrivilegeDescriptor priv) {
        initializeRoleGroup();
        PermissionService.grantPermission
            (new PermissionDescriptor(priv, target, m_roleGroup));
    }

    /**
     * Wrapper to grant a PrivilegeDescriptor to users in this role on the group
     * that represents the people in this role.
     * @param priv the privilege to grant
     **/
    public void grantPermission(PrivilegeDescriptor priv) {
        initializeRoleGroup();
        grantPermission(m_roleGroup, priv);
    }

    /**
     * Checks whether users in this role have the specified PrivilegeDescriptor on
     * the specified target ACS object.
     *
     * @param target the ACS object to check
     * @param priv the privilege to check for
     *
     * @return <code>true</code> if the users have the PrivilegeDescriptor
     * on the specified target; <code>false</code> otherwise.
     *
     * @see PermissionService#checkPermission(PermissionDescriptor)
     **/
    public boolean checkPermission(ACSObject target, PrivilegeDescriptor priv) {
        initializeRoleGroup();
        return PermissionService.checkPermission
            (new PermissionDescriptor(priv, target, m_roleGroup));
    }

    /**
     * Wrapper to check a PrivilegeDescriptor for users in this role on the
     * group that represents the people in this role.
     *
     **/
    public boolean checkPermission(PrivilegeDescriptor priv) {
        initializeRoleGroup();
        return checkPermission(m_roleGroup, priv);
    }

    /**
     * Revokes the specified PrivilegeDescriptor on the specified target ACS
     * object for users who have this role.
     *
     * @param target the ACS object that has the privilege to revoke
     * @param priv the privilege to revoke
     *
     * @see PermissionService#revokePermission(PermissionDescriptor)
     **/
    public void revokePermission(ACSObject target, PrivilegeDescriptor priv) {
        initializeRoleGroup();
        PermissionService.revokePermission
            (new PermissionDescriptor(priv, target, m_roleGroup));
    }

    /**
     * Revokes the specified PrivilegeDescriptor on the group representing
     * people in this role for users who have this role.
     *
     * @param priv the privilege to revoke
     *
     * @see PermissionService#revokePermission(PermissionDescriptor)
     **/
    public void revokePermission(PrivilegeDescriptor priv) {
        initializeRoleGroup();
        revokePermission(m_roleGroup, priv);
    }

    /**
     * Gets the group that represents the members of
     * this role. This is used primarily to support tests.
     *
     * <font color="red">This is a temporary solution to support CMS
     * permissioning. It will change in the future and this method
     * will probably not be supported at that point.</font>
     **/
    public Group getGroup() {
        initializeRoleGroup();
        return m_roleGroup;
    }

}
