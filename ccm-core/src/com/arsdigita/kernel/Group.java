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

// Identity class.
import java.math.BigDecimal;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a group.
 *
 * @version 1.0
 * @version $Id: Group.java 287 2005-02-22 00:29:02Z sskracic $
 **/
public class Group extends Party {

    private Collection m_roles = new ArrayList();

    /**
     * Every instance of group must encapsulate a data object whose
     * object type is either this base type or a subtype of this base type.
     */
    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.Group";

    @Override
    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public Group(DataObject groupData) {
        super(groupData);
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "Group".
     *
     * @see Party#Party(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public Group() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by the string
     * <i>typeName</i>.
     *
     * @param typeName the name of the <code>ObjectType</code> of the
     * contained <code>DataObject</code>
     *
     * @see Party#Party(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public Group(String typeName) {
        super(typeName);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> specified by <i>type</i>.
     *
     * @param type the <code>ObjectType</code> of the created object
     *
     * @see Party#Party(ObjectType)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public Group(ObjectType type) {
        super(type);
    }

    /**
     * Constructor in which the  contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid the <code>OID</code> for the retrieved
     * <code>DataObject</code>
     *
     * @see Party#Party(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public Group(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor in which the contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and <code>Group.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id the <code>id</code> for the retrieved
     * <code>DataObject</code>
     *
     * @see Party#Party(OID)
     * @see Group#BASE_DATA_OBJECT_TYPE
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     **/
    public Group(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Gets the name of this group.
     *
     * @return the name of this group.
     **/
    public String getName() {
        return (String) get("name");
    }

    /**
     * Sets the name of this group.
     *
     * @param name this group's name
     **/
    public void setName(String name) {
        set("name", name);
    }

    /**
     * Returns a collection of parties that are direct members of this group.
     * Currently, this is the same as getMemberUsers().  However, in
     * the future we may allow other types of parties to be members
     * of groups (specifically, we may allow groups to be members of groups).
     * 
     * NOTE: Any prior calls to addMember() or removeMember() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing the member(s).
     *
     * @return a collection of parties that are direct members of this group.
     **/
    public PartyCollection getMembers() {
        return getMemberUsers();
    }

    /**
     * Returns a collection of users that are direct members of this group.
     * 
     * NOTE: Any prior calls to addMember() or removeMember() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing the member(s).
     *
     * @return a collection of users that are direct members of this group.
     **/
    public UserCollection getMemberUsers() {
        return new UserCollection(getMembersCursor());
    }

    /**
     * Adds a user to this group.
     *
     * @param user the user to add to this group
     **/
    public void addMember(User user) {
        add("members", user);
    }

    /**
     * Removes a user from this group.
     *
     * @param user the user to remove from this group
     **/
    public void removeMember(User user) {
        remove("members", user);
    }

    /**
     * 
     * Checks whether a user is a direct member of this group.
     *
     * @param user the user to check
     * @return <code>true</code> if the user is a direct member of group;
     * <code>false</code> otherwise.
     **/
    public boolean hasDirectMember(Party party) {
        DataQuery members =
            getDataQuery("com.arsdigita.kernel.GroupDirectMembers");
        members.setParameter("groupID", getID());
        members.addEqualsFilter("memberID", party.getID());
        return (members.size() == 1);
    }

    /**
     * 
     * Returns a count of this group's direct members.
     * @return a count of this group's direct members.
     **/
    public long countMembers() {
        DataQuery members =
            getDataQuery("com.arsdigita.kernel.GroupDirectMembers");
        members.setParameter("groupID", getID());
        return members.size();
    }


    /**
     * 
     * Checks whether a group is a direct subgroup of this group.
     *
     * @param subgroup the subgroup to check
     * @return <code>true</code> if the specified group is a direct
     * subgroup of this group; <code>false</code> otherwise.
     **/
    public boolean hasDirectSubgroup(Group subgroup) {
        DataQuery subgroups =
            getDataQuery("com.arsdigita.kernel.GroupDirectSubgroups");
        subgroups.setParameter("groupID", getID());
        subgroups.addEqualsFilter("subgroupID", subgroup.getID());
        return (subgroups.size() == 1);
    }

    /**
     * 
     * Returns a count of this group's direct subgroups.
     * @return a count of this group's direct subgroups.
     **/
    public long countSubgroups() {
        DataQuery subgroups =
            getDataQuery("com.arsdigita.kernel.GroupDirectSubgroups");
        subgroups.setParameter("groupID", getID());
        return subgroups.size();
    }


    /**
     * 
     * Checks whether this group has any members or subgroups.
     *
     * @return <code>false</code> if this group has any members or
     * subgroups; <code>true</code> otherwise.
     */
    public boolean isEmpty() {
        return (countMembers()==0 && countSubgroups()==0);
    }

    /**
     * 
     * Removes all direct members from this group.
     *
     * @throws PersistenceException if members could not be cleared
     * because of a database or persistence exception.
     */
    public void clearMembers() {
        DataOperation op =
            getDataOperation("com.arsdigita.kernel.ClearMembers");
        op.setParameter("groupID", getID());
        op.execute();
    }


    /**
     * 
     * Removes all direct subgroups from this group.
     *
     * @throws PersistenceException if subgroups could not be cleared
     * because of a database or persistence exception.
     */
    public void clearSubgroups()
        throws PersistenceException
    {
        DataOperation op =
            getDataOperation("com.arsdigita.kernel.ClearSubgroups");
        op.setParameter("groupID", getID());
        op.execute();
    }

    /**
     * 
     * Removes this group from all groups of which this is a direct subgroup.
     *
     * @throws PersistenceException if supergroups could not be cleared
     * because of a database or persistence exception.
     *
     * @see #delete()
     */
    public void clearSupergroups()
        throws PersistenceException
    {
        DataOperation op =
            getDataOperation("com.arsdigita.kernel.ClearSupergroups");
        op.setParameter("groupID", getID());
        op.execute();
    }

    /**
     * Returns a collection of groups that are direct subgroups of this group.
     * <P>
     * NOTE: Any prior calls to addSubgroup() or removeSubgroup() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing the subgroup(s).
     *
     * @return a collection of groups that are direct subgroups of this group.
     **/
    public GroupCollection getSubgroups() {
        return new GroupCollection(getSubgroupsCursor());
    }

    /**
     * Adds a subgroup to this group.
     *
     * @param subgroup the subgroup to add to this group
     **/
    public void addSubgroup(Group subgroup) {
        add("subgroups", subgroup);
    }

    /**
     * Removes a subgroup from this group.
     *
     * @param subgroup the subgroup to remove from this group
     **/
    public void removeSubgroup(Group subgroup) {
        remove("subgroups", subgroup);
    }

    /**
     * Returns a collection of groups that are direct supergroups of this
     * group (that is, groups of which this is a subgroup).
     * <P>
     * NOTE: Any prior calls to addSupergroup() or removeSupergroup() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing the supergroup(s).
     *
     * @return a collection of groups that are direct supergroups of this
     * group.
     **/
    public GroupCollection getSupergroups() {
        return new GroupCollection(getSupergroupsCursor());
    }

    /**
     * 
     * Adds a supergroup to this group (that is, adds this group as
     * a subgroup of the specified group).
     * <P>
     * This has the same effect
     * as calling <i>supergroup</i>.addSubgroup(<i>this</i>), except
     * in that case the change persists when the <i>supergroup</i>
     * is saved.  For example:
     *
     * <pre>
     * groupA.addSubgroup(groupB);
     * groupA.save();
     * </pre>
     *
     * is equivalent to
     *
     * <pre>
     * groupB.addSupergroup(groupA);
     * groupB.save();
     * </pre>
     *
     * @param supergroup the supergroup to add to this group
     **/
    public void addSupergroup(Group supergroup) {
        add("supergroups", supergroup);
    }

    /**
     * 
     * Removes a supergroup from this group (that is, removes this group as
     * a subgroup of the specified group).
     * <P>
     * This has the same effect
     * as calling <i>supergroup</i>.removeSubgroup(<i>this</i>), except
     * in that case, the change persists when the <i>supergroup</i>
     * is saved.  For example:
     *
     * <pre>
     * groupA.removeSubgroup(groupB);
     * groupA.save();
     * </pre>
     *
     * is equivalent to
     *
     * <pre>
     * groupB.removeSupergroup(groupA);
     * groupB.save();
     * </pre>
     *
     * @param supergroup the supergroup to remove from this group
     **/
    public void removeSupergroup(Group supergroup) {
        remove("supergroups", supergroup);
    }


    /**
     * Returns a collection of parties that are directly members or subgroups
     * of this group.
     * <P>
     * NOTE: Any prior calls to membership or subgroup methods will not
     * affect the resulting collection until save() has been called.
     *
     * @return a collection of parties that are directly contained in this
     * group
     **/
    public PartyCollection getContainedParties() {
        DataCollection dc =
            getSession().retrieve("com.arsdigita.kernel.Party");
        Filter f = dc.addInSubqueryFilter(
                                          "id", "com.arsdigita.kernel.ContainedParties");
        f.set("groupID", getID());
        return new PartyCollection(dc);
    }

    /*
     * TRANSITIVE CLOSURES
     */

    /**
     * Returns a collection of groups that are subgroups of this group
     * directly or indirectly.
     * <P>
     * NOTE: any prior calls to addSubgroup() or removeSubgroup() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing the subgroup(s).
     *
     * @return A collection of groups that are subgroups of this group.
     **/
    public GroupCollection getAllSubgroups() {
        // The "false" is there so we don't include the current group.
        return new GroupCollection(getAllSubgroupsCursor());
    }

    /**
     * Returns a collection of groups that are directly or indirectly
     * supergroups of this group.
     * <P>
     * NOTE: Any prior calls to addSupergroup() or removeSupergroup() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing the supergroup(s).
     *
     * @return a collection of groups that are supergroups of this group.
     **/
    public GroupCollection getAllSupergroups() {
        DataAssociation da = (DataAssociation) get("allSupergroups");
        DataAssociationCursor cursor = da.getDataAssociationCursor();
        return new GroupCollection(cursor);
    }

    /**
     * Returns a collection of parties that are directly or indirectly
     * members or subgroups of this group.
     * <P>
     * NOTE: Any prior calls to membership or subgroup methods will not
     * affect the resulting collection until save() has been called.
     *
     * @return a collection of parties that are contained in this group
     **/
    public PartyCollection getAllContainedParties() {
        DataCollection dc =
            getSession().retrieve("com.arsdigita.kernel.Party");
        Filter f = dc.addInSubqueryFilter(
                                          "id", "com.arsdigita.kernel.AllContainedParties");
        f.set("groupID", getID());
        return new PartyCollection(dc);
    }

    /**
     * Returns a collection of users that are  either directly or indirectly
     * members of this group.  For example, if user x is a member of
     * group A, and A is a subgroup of B, then B.getAllMembers() will contain
     * user x.
     * <P>
     * Currently, this is the same as getAllMemberUsers().  However, in
     * the future we may allow other types of parties to be members
     * of groups (specifically, we may allow groups to be members of groups).
     * <P>
     * NOTE: Any prior calls to addMember() or removeMember() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing the member(s).
     *
     * @return a collection of partiess that are members of this group.
     *
     * @see PartyCollection
     **/
    public PartyCollection getAllMembers() {
        return getAllMemberUsers();
    }

    /**
     * Returns a collection of users that are  either directly or indirectly
     * members of this group.  For example, if user x is a member of
     * group A, and A is a subgroup of B, then B.getAllMemberUsers() will
     * contain user x.
     * <P>
     * NOTE: Any prior calls to addMember() or removeMember() will not
     * affect the resulting collection unless save() has been called after
     * adding or removing the member(s).
     *
     * @return a collection of users that are members of this group.
     *
     * @see UserCollection
     **/
    public UserCollection getAllMemberUsers() {
        DataAssociationCursor assoc =
            ((DataAssociation) get("allMembers")).cursor();
        return new UserCollection(assoc);
    }


    /**
     * Checks whether a user is directly or indirectly a member of
     * this group.
     *
     * @param user the user to check
     * @return <code>true</code> if the user is a direct or indirect member;
     * <code>false</code> otherwise.
     **/
    public boolean hasMember(Party party) {
        DataQuery members =
            getDataQuery("com.arsdigita.kernel.GroupMembers");
        members.setParameter("groupID", getID());
        members.addEqualsFilter("memberID", party.getID());
        return (members.size() == 1);
    }

    /**
     * 
     * Returns a count of this group's members (direct plus indirect).
     * @return the total number of members of this group.
     **/
    public long countAllMembers() {
        DataQuery members =
            getDataQuery("com.arsdigita.kernel.GroupMembers");
        members.setParameter("groupID", getID());
        return members.size();
    }

    /**
     * 
     * Checks whether a group is a subgroup of this group (either
     * directly or indirectly).
     * @param subgroup the subgroup to check
     * @return <code>true</code> if the specified group is a direct
     * or indirect subgroup of this group; <code>false</code>
     * otherwise.
     **/
    public boolean hasSubgroup(Group subgroup) {
        DataQuery subgroups =
            getDataQuery("com.arsdigita.kernel.GroupSubgroups");
        subgroups.setParameter("groupID", getID());
        subgroups.addEqualsFilter("subgroupID", subgroup.getID());
        return (subgroups.size() == 1);
    }

    /**
     * 
     * Returns a count of this group's subgroups (direct plus indirect).
     * @return the total number of subgroups of this group.
     **/
    public long countAllSubgroups() {
        DataQuery subgroups =
            getDataQuery("com.arsdigita.kernel.GroupSubgroups");
        subgroups.setParameter("groupID", getID());
        return subgroups.size();
    }





    /**
     * 
     * Checks whether a party is a member of this group (if the party
     * is a User), or a subgroup of this group (if the party is a
     * Group).
     *
     * @param party the user/group to check
     * @return <code>true</code> if the specified party is a user that
     * is a direct member of this group, or if the specified party is
     * a group that is a direct subgroup of this group;
     * <code>false</code> otherwise.
     **/
    public boolean hasDirectMemberOrSubgroup(Party party) {
        if (party instanceof User) {
            return hasDirectMember((User)party);
        } else if (party instanceof Group) {
            return hasDirectSubgroup((Group)party);
        } else {
            throw new IllegalArgumentException("Require a User or Group");
        }
    }

    /**
     * Adds a member/subgroup to this group.  (If the party is a User,
     * it's made a member.  If it's a Group, it's made a subgroup.)
     *
     * @param party the party to add to this group
     **/
    public void addMemberOrSubgroup(Party party) {
        if (party instanceof User) {
            addMember((User)party);
        } else if (party instanceof Group) {
            addSubgroup((Group)party);
        } else {
            throw new IllegalArgumentException("Require a User or Group");
        }
    }

    /**
     * Removes a member/subgroup from this group.
     *
     * @param party the party to remove from this group
     **/
    public void removeMemberOrSubgroup(Party party) {
        if (party instanceof User) {
            removeMember((User)party);
        } else if (party instanceof Group) {
            removeSubgroup((Group)party);
        } else {
            throw new IllegalArgumentException("Require a User or Group");
        }
    }




    /*
     * ROLES
     */

    /**
     * Creates a new role with the specified name in this group. The
     * role is automatically saved whenever the group itself is
     * saved. Note that roles can only be created after the group for
     * which the role is being created is itself saved.
     * @param roleName the name for the role to be added to this group
     * @return the new role.
     * @see Role
     *
     * @pre !isNew()
     **/
    public Role createRole(String roleName) {
        if (isNew()) {
            throw new RuntimeException("Roles can only be created after a " +
                                       "group has been saved");
        }
        Role role = new Role(this, roleName);
        m_roles.add(role);
        return role;
    }

    /**
     * Returns a collection of roles in this group.
     * <P>
     * NOTE: Any prior calls to createRole() will not affect the
     * resulting collection unless save() has been called after
     * adding or removing the role(s).
     *
     * @return a collection of roles in this group.
     **/
    public RoleCollection getRoles() {
        return new RoleCollection(getRolesCursor());
    }

    /**
     * 
     * Returns the role with the specified name, or null if no such
     * role exists for this group.
     * <P>
     * NOTE: any prior calls to createRole() will not affect the
     * resulting collection unless save() has been called after
     * adding or removing the role(s).
     * @param name the name of the role to retrieve
     * @return the specified role.
     **/
    public Role getRole(String name) {
        DataAssociationCursor roles = getRolesCursor();
        roles.addEqualsFilter("name", name);
        if (!roles.next()) {
            return null;
        }
        DataObject role = roles.getDataObject();
        roles.close();
        return new Role(role);
    }


    /**
     * Returns a collection of roles in this group ordered by name.
     * <P>
     * NOTE: Any prior calls to createRole() will not affect the
     * resulting collection unless save() has been called after
     * adding or removing the role(s).
     *
     * @return a collection of roles in this group, ordered by name.
     **/
    public RoleCollection getOrderedRoles() {
        DataAssociationCursor roles = getRolesCursor();
        roles.addOrder("upper(name)");
        return new RoleCollection(roles);
    }


    protected void beforeSave() throws PersistenceException {
        super.beforeSave();
        Iterator iter = m_roles.iterator();
        while (iter.hasNext()) {
            Role r = (Role) iter.next();
            r.save();
        }
    }

    /**
     * Deletes this group.  The group is removed from all
     * supergroups (see clearSupergroups()).
     *
     * @pre isEmpty()
     **/
    public void delete() throws PersistenceException {
        clearMembers();
        clearSubgroups();
        clearSupergroups();
        super.delete();
    }

    /**
     * Retrieves all groups.
     *
     * @return  a collection of all groups.
     **/
    public static GroupCollection retrieveAll() {
        return new GroupCollection(
                                   SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE)
                                   );
    }

    /*
     * Private helpers
     */

    private DataAssociationCursor getRolesCursor() {
        DataAssociation assoc = (DataAssociation) get("roles");
        return assoc.cursor();
    }

    private DataAssociationCursor getMembersCursor() {
        DataAssociation assoc = (DataAssociation) get("members");
        return assoc.cursor();
    }

    private DataAssociationCursor getSubgroupsCursor() {
        DataAssociation assoc = (DataAssociation) get("subgroups");
        return assoc.cursor();
    }

    private DataAssociationCursor getSupergroupsCursor() {
        DataAssociation assoc = (DataAssociation) get("supergroups");
        return assoc.cursor();
    }

    private DataAssociationCursor
        getAllSubgroupsCursor() {

        DataAssociationCursor a =
            ((DataAssociation) get("allSubgroups")).cursor();

        return a;
    }

    private DataQuery getDataQuery(String name) {
        return SessionManager.getSession().retrieveQuery(name);
    }
    private DataOperation getDataOperation(String name) {
        return SessionManager.getSession().retrieveDataOperation(name);
    }

}
