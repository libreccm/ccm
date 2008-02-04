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

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Tests basic functionality of kernel classes
 *
 *
 * @author Oumi Mehrotra
 * @version 1.0
 * @see com.arsdigita.kernel
 */


public class GroupsBasicTest extends BaseTestCase {

    public static final String versionId = "$Id: GroupsBasicTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Constructs a GroupsBasicTest with the specified name.
     *
     * @param name Test case name.
     **/
    public GroupsBasicTest( String name ) {
        super( name );
    }

    /**
     * Tests group CRUD operations
     **/
    public void testCRUD() throws Exception {
        // CREATE
        Group group = new Group();
        BigDecimal idval = group.getID();
        group.setName("foo barber");
        group.save();

        // RETRIEVE
        OID groupOID = new OID(Group.BASE_DATA_OBJECT_TYPE, idval);
        group = new Group(groupOID);
        assertEquals("Group name is wrong",
                     group.getName(),
                     "foo barber");

        // UPDATE - still need to do.

        // DELETE
        group.delete();
        try {
            group = new Group(groupOID);
            fail("Group " + idval + " not deleted");
        } catch (Exception e) {
            // Exception is supposed to happen.  Continue.
        }
    }

    /**
     * Test membership add/retrieve/remove
     */
    public void testAddGetRemoveMembers() throws Exception {
        // Create group
        Group group = new Group();
        BigDecimal groupID=group.getID();
        group.setName("Some Group " + groupID.toString());
        group.save();

        // Create user
        User user = new User();
        BigDecimal userID=user.getID();
        String screenName = "screen name " + groupID.toString();
        user.setScreenName(screenName);
        user.getPersonName().setGivenName("Oumi");
        user.getPersonName().setFamilyName("Mehrotra");
        String email = "oumi(" + groupID + ")@arsdigita.com";
        user.setPrimaryEmail(new EmailAddress(email));
        user.save();

        // retrieve the group (just for kicks)
        OID oid = new OID(Group.BASE_DATA_OBJECT_TYPE, groupID);
        group = new Group(oid);

        group.addMember(user);
        group.save();

        PartyCollection members = group.getMembers();
        assertTrue("No members retrieved", members.next());
        assertEquals("Wrong user id", userID, members.getID());
        assertEquals("Wrong object type", User.BASE_DATA_OBJECT_TYPE,
                     members.getSpecificObjectType());
        assertEquals("Wrong user id when instantiating domain object", userID,
                     members.getParty().getID());
        assertTrue("Wrong domain class instantiated",
                   members.getParty() instanceof User);
        assertTrue("More than one member retrieved", !members.next());

        User user1 = createUser("givenName1", "familyName1", "address1",
                                "screenName1");
        User user2 = createUser("givenName2", "familyName2", "address2",
                                "screenName2");
        User user3 = createUser("givenName3", "familyName3", "address3",
                                "screenName3");
        User user4 = createUser("givenName4", "familyName4", "address4",
                                "screenName4");
        saveAll(new User[] {user1, user2, user3, user4});

        // let's add 2 more users and make sure that the three users
        // in the group are correct
        group.addMember(user1);
        group.addMember(user2);
        group.save();

        assertEquals(3, group.countMembers());
        assertEquals(3, group.countAllMembers());
        assertEquals(0, group.countSubgroups());
        assertEquals(0, group.countAllSubgroups());
        assertTrue(!group.isEmpty());

        members = group.getMembers();
        UserCollection users = group.getMemberUsers();

        // they should be the same size
        assertTrue("getMembers and getMemberUsers should return collections " +
                   "of the same size since there are only user members in this " +
                   "group", members.size() == users.size());

        // now let's check that they have the same members
        validateCollection(users, new User[] {user, user1, user2});
        validateCollection(members, new User[] {user, user1, user2});

        // now I add a member and remove a different member
        // user3 is not a member
        assertTrue("User 3 is not a member but GROUP thinks it is",
                   !group.hasMember(user3));
        group.addMember(user3);
        group.addMember(user4);
        group.save();

        assertTrue("User 3 is a member but group.hasMember thinks it is not",
                   group.hasMember(user3));
        assertTrue("User 3 is a member but group.hasDirectMember thinks it is not",
                   group.hasDirectMember(user3));

        validateCollection(group.getMembers(),
                           new User[] {user, user1, user2, user3, user4});



        group.removeMember(user2);
        group.save();
        validateCollection(group.getMembers(),
                           new User[] {user, user1, user3, user4});

        assertTrue("User 2 is a not a member but group.hasMember thinks it is",
                   !group.hasMember(user2));
        assertTrue("User 2 is a not member but group.hasDirectMember thinks it is",
                   !group.hasDirectMember(user2));

        assertEquals(4, group.countMembers());
        assertEquals(4, group.countAllMembers());
        assertEquals(0, group.countSubgroups());
        assertEquals(0, group.countAllSubgroups());
        assertTrue(!group.isEmpty());

        group.clearMembers();

        assertEquals(0, group.countMembers());
        assertEquals(0, group.countAllMembers());
        assertEquals(0, group.countSubgroups());
        assertEquals(0, group.countAllSubgroups());
        assertTrue(group.isEmpty());
    }


    /**
     * Test subgroups add/retrieve/remove
     */
    public void testAddGetRemoveSubgroups() throws Exception {
        // Create group
        Group group = new Group();
        BigDecimal groupID=group.getID();
        group.setName("Some Group " + groupID.toString());
        group.save();

        // Create subgroup
        Group subgroup = new Group();
        BigDecimal subgroupID=subgroup.getID();
        String name = "group name " + groupID.toString();
        subgroup.setName(name);
        subgroup.save();

        // retrieve the group (just for kicks)
        OID oid = new OID(Group.BASE_DATA_OBJECT_TYPE, groupID);
        group = new Group(oid);

        group.addSubgroup(subgroup);
        group.save();

        GroupCollection subgroups = group.getSubgroups();
        assertTrue("No subgroups retrieved", subgroups.next());
        assertEquals("Wrong subgroup id", subgroupID, subgroups.getID());
        assertEquals("Wrong subgroup name", name, subgroups.getName());
        assertEquals("Wrong subgroup id when instantiating domain object",
                     subgroupID, subgroups.getGroup().getID());
        assertTrue("Wrong domain class instantiated",
                   subgroups.getGroup() instanceof Group);
        assertTrue("More than one subgroup retrieved", !subgroups.next());


        // now we create a third group
        Group subgroup2 = new Group();
        BigDecimal subgroup2ID=subgroup2.getID();
        String name2 = "group name " + subgroup2ID.toString();
        subgroup2.setName(name2);
        subgroup2.save();

        //create a sub-sub group
        subgroup.addSubgroup(subgroup2);
        subgroup.save();
        subgroups = subgroup.getSubgroups();
        assertTrue("No subgroups retrieved", subgroups.next());
        assertEquals("Wrong subgroup id", subgroup2ID, subgroups.getID());
        subgroups.close();

        // now we add a member to the subsubgroup and make sure that
        // hasMember and hasDirectMember work correctly.
        User user = createUser("givenName1", "familyName1", "address1",
                               "screenName1");
        subgroup2.addMember(user);
        subgroup2.save();

        assertTrue("group should have the user via subgroup2",
                   group.hasMember(user));
        assertTrue("group does not have any members but thinks it does",
                   !group.hasDirectMember(user));
        assertTrue("subgroup should have the user via subgroup2",
                   subgroup.hasMember(user));
        assertTrue("subgroup does not have any members but thinks it does",
                   !subgroup.hasDirectMember(user));

        subgroup.addMember(user);
        subgroup.save();
        assertTrue("group should have the user via subgroup",
                   group.hasMember(user));
        assertTrue("group does not have any members but thinks it does",
                   !group.hasDirectMember(user));

        subgroup2.removeMember(user);
        subgroup2.save();
        assertTrue("subgroup2 does not have any members but hasMember thinks it " +
                   "does", !subgroup2.hasDirectMember(user));
        assertTrue("subgroup2 does not have any members but hasMember thinks it " +
                   "does", !subgroup2.hasMember(user));


        // now remove subgroup from group and see how hasMember works
        group.removeSubgroup(subgroup);
        group.save();

        // TODO: fix this
        //        assertTrue("group does not have a member but it thinks it does",
        //               !group.hasMember(user));
        assertTrue("subgroup has a member but it thinks it does not",
                   subgroup.hasMember(user));

        subgroup.removeSubgroup(subgroup2);
        subgroup.save();


        //
        // test getting subgroups
        //
        group.addSubgroup(subgroup);
        subgroup2.addSupergroup(group);
        group.save();
        subgroup2.save();

        subgroups = group.getSubgroups();

        // now we add subgroup2 under subgroup and make sure it only appears
        // once
        subgroup.addSubgroup(subgroup2);
        subgroup.save();
        GroupCollection subgroups2 = group.getAllSubgroups();

        for (int i = 0; i < 2; i++) {
            if (!subgroups2.next()) {
                fail("subgroups should have 2 items but only had " +
                     new Integer(i));
            }
            assertTrue("the group retrieved is not one of the ones inserted.",
                       subgroups2.getGroup().equals(subgroup2) ||
                       subgroups2.getGroup().equals(subgroup));

        }

        assertTrue("Subgroups should have only had 3 items", subgroups.next());
        subgroups.close();

        // now we remove subgroup2 from group and make sure we get the
        // same thing
        group.removeSubgroup(subgroup2);
        group.save();
        subgroups2.close();
        subgroups2 = group.getAllSubgroups();

        for (int i = 0; i < 2; i++) {
            if (!subgroups2.next()) {
                fail("subgroups should have 2 items but only had " +
                     new Integer(i));
            }
            assertTrue("the group retrieved is not one of the ones inserted.",
                       subgroups2.getGroup().equals(subgroup) ||
                       subgroups2.getGroup().equals(subgroup2));

        }
        subgroups2.close();

        // make sure that there is only one subgroup for group
        assertTrue("Group only has one subgroup but it thinks it has more",
                   group.getSubgroups().size() == 1);


        // let's make sure that getting the supergroup returns one
        // group but getting all returns 2
        GroupCollection superGroups = subgroup2.getSupergroups();
        assertTrue("supergroup should have had one group", superGroups.next());
        assertTrue("supergroup2 should only have the supergroup of subgroup",
                   superGroups.getGroup().equals(subgroup));

        assertTrue("supergroup should have had only one group",
                   !superGroups.next());

        // now make sure that there are two supergroups
        superGroups = subgroup2.getAllSupergroups();
        assertTrue("supergroup should have had two groups but did not have any",
                   superGroups.next());
        assertTrue("supergroup2 should only have the supergroup of subgroup or " +
                   "group", superGroups.getGroup().equals(subgroup) ||
                   superGroups.getGroup().equals(group));
        assertTrue("supergroup should have had two groups but only had one",
                   superGroups.next());
        assertTrue("supergroup2 should only have the supergroup of subgroup or " +
                   "group", superGroups.getGroup().equals(subgroup) ||
                   superGroups.getGroup().equals(group));
        assertTrue("supergroup should have only had two groups but had more " +
                   "than that.", !superGroups.next());

        // remove group and make sure that there is only one supergroup left
        subgroup.removeSupergroup(group);
        subgroup.save();

        superGroups = subgroup2.getAllSupergroups();
        assertTrue("supergroup should have one group but did not have any",
                   superGroups.next());
        assertTrue("subgroup has wrong supergroup ",
                   superGroups.getGroup().equals(subgroup));
        assertTrue("supergroup should have had one group but thought it had more",
                   !superGroups.next());

        // Test that a subgroup can be deleted.
        subgroup.delete();
    }


    public void testCircularConstraint() {
        Group group1 = new Group();
        group1.setName("name1");
        group1.save();
        Group group2 = new Group();
        group2.setName("name2");
        group2.save();

        // now let's make sure that we cannot have an infinite loop
        // with the subgroups
        try {
            group1.addSubgroup(group2);
            group1.save();
            group2.addSubgroup(group1);
            group2.save();
            fail("creating a loop through subgrouping should throw an " +
                 "exception");
        } catch (PersistenceException e) {
            // it should remove the group just added to avoid the same
            // exception
        }
    }


    public void testCreateGetRoles() throws Exception {
        Group group = new Group();
        BigDecimal groupID=group.getID();
        group.setName("Some Group " + groupID.toString());

        // make sure you cannot create a Role before saving
        try {
            Role role = group.createRole("administrator");
            fail("A Role was created before the group was saved.");
        } catch (Exception e) {
            // this should fall through
        }

        group.save();
        Role adminRole = group.createRole("administrator");
        Role programmerRole = group.createRole("programmer");
        Role developerRole = group.createRole("developer");
        group.save();

        RoleCollection roleCollection = group.getRoles();
        assertTrue("roleCollection should have 3 items but only has 1",
                   roleCollection.next());
        assertTrue("the first item in order role collection should be one that " +
                   "was inserted.",
                   roleCollection.getRole().equals(adminRole) ||
                   roleCollection.getRole().equals(programmerRole) ||
                   roleCollection.getRole().equals(developerRole));
        assertTrue("roleCollection should have 3 items but only has 1",
                   roleCollection.next());
        assertTrue("the first item in order role collection should be one that " +
                   "was inserted.",
                   roleCollection.getRole().equals(adminRole) ||
                   roleCollection.getRole().equals(programmerRole) ||
                   roleCollection.getRole().equals(developerRole));
        assertTrue("roleCollection should have 3 items but only has 1",
                   roleCollection.next());
        assertTrue("the first item in order role collection should be one that " +
                   "was inserted.",
                   roleCollection.getRole().equals(adminRole) ||
                   roleCollection.getRole().equals(programmerRole) ||
                   roleCollection.getRole().equals(developerRole));
        assertTrue("roleCollection should have 3 items but has at least 4",
                   !roleCollection.next());


        // finally, make sure that getOrderedRoles works
        roleCollection = group.getOrderedRoles();
        assertTrue("roleCollection should have 3 items but only has 1",
                   roleCollection.next());
        assertTrue("the first item in order role collection should be admin",
                   roleCollection.getRole().equals(adminRole));
        assertTrue("roleCollection should have 3 items but only has 1",
                   roleCollection.next());
        assertTrue("the third item in order role collection should be developer",
                   roleCollection.getRole().equals(developerRole));
        assertTrue("roleCollection should have 3 items but only has 1",
                   roleCollection.next());
        assertTrue("the second item in order role collection should be programmer",
                   roleCollection.getRole().equals(programmerRole));

        assertTrue("roleCollection should have 3 items but has at least 4",
                   !roleCollection.next());

    }


    private Group createGroup(String baseName) throws Exception {
        Group g = new Group();
        BigDecimal id=g.getID();
        g.setName(baseName + id.toString());
        g.save();
        return g;
    }

    private User createUser(String givenName,
                            String familyName,
                            String emailAddress,
                            String screenName)
        throws Exception
    {
        User u = new User();
        BigDecimal id=u.getID();
        u.getPersonName().setGivenName(givenName);
        u.getPersonName().setFamilyName(familyName);
        EmailAddress a = new EmailAddress(emailAddress + id.toString());
        u.setPrimaryEmail(a);
        u.setScreenName(screenName + id.toString());
        u.save();
        return u;
    }

    /**
     * Test subgroups add/retrieve/remove
     */
    public void testGetAllSubgroups() throws Exception {
        // Create group
        Group greatGrandma = createGroup("Great Grandma");

        // Create subgroups
        Group grandpa = createGroup("Grandpa");
        Group grandma = createGroup("Grandma");
        Group dad = createGroup("Dad");
        Group mom = createGroup("Mom");
        Group aunt = createGroup("Aunt");
        Group cousin = createGroup("Cousin");
        Group child = createGroup("Child");

        greatGrandma.addSubgroup(grandpa);
        greatGrandma.addSubgroup(grandma);
        grandpa.addSubgroup(dad);
        grandpa.addSubgroup(mom);
        grandpa.addSubgroup(aunt);
        grandma.addSubgroup(dad);
        grandma.addSubgroup(mom);
        grandma.addSubgroup(aunt);
        dad.addSubgroup(child);
        mom.addSubgroup(child);
        aunt.addSubgroup(cousin);

        saveAll(new Group[] {child, cousin, dad, mom, aunt,
                             grandma, grandpa, greatGrandma });

        assertEquals(0, greatGrandma.countMembers());
        assertEquals(0, greatGrandma.countAllMembers());
        assertEquals(2, greatGrandma.countSubgroups());
        assertEquals(7, greatGrandma.countAllSubgroups());
        assertTrue(!greatGrandma.isEmpty());

        validateCollection(greatGrandma.getAllSubgroups(),
                           new Group[] {grandpa, grandma, dad,
                                        mom, aunt, cousin, child});

        assertEquals(0, grandpa.countMembers());
        assertEquals(0, grandpa.countAllMembers());
        assertEquals(3, grandpa.countSubgroups());
        assertEquals(5, grandpa.countAllSubgroups());
        assertTrue(!grandpa.isEmpty());
        validateCollection(grandpa.getAllSubgroups(),
                           new Group[] {dad, mom, aunt,
                                        cousin, child});

        assertEquals(0, grandma.countMembers());
        assertEquals(0, grandma.countAllMembers());
        assertEquals(3, grandma.countSubgroups());
        assertEquals(5, grandma.countAllSubgroups());
        assertTrue(!grandma.isEmpty());
        validateCollection(grandma.getAllSubgroups(),
                           new Group[] {dad, mom, aunt,
                                        cousin, child});

        assertEquals(0, dad.countMembers());
        assertEquals(0, dad.countAllMembers());
        assertEquals(1, dad.countSubgroups());
        assertEquals(1, dad.countAllSubgroups());
        assertTrue(!dad.isEmpty());
        validateCollection(dad.getAllSubgroups(),
                           new Group[] {child});

        assertEquals(0, mom.countMembers());
        assertEquals(0, mom.countAllMembers());
        assertEquals(1, mom.countSubgroups());
        assertEquals(1, mom.countAllSubgroups());
        assertTrue(!mom.isEmpty());
        validateCollection(mom.getAllSubgroups(),
                           new Group[] {child});

        assertEquals(0, child.countMembers());
        assertEquals(0, child.countAllMembers());
        assertEquals(0, child.countSubgroups());
        assertEquals(0, child.countAllSubgroups());
        assertTrue(child.isEmpty());
        validateCollection(child.getAllSubgroups(),
                           new Group[] {});

        assertEquals(0, aunt.countMembers());
        assertEquals(0, aunt.countAllMembers());
        assertEquals(1, aunt.countSubgroups());
        assertEquals(1, aunt.countAllSubgroups());
        assertTrue(!aunt.isEmpty());
        validateCollection(aunt.getAllSubgroups(),
                           new Group[] {cousin});

        assertEquals(0, cousin.countMembers());
        assertEquals(0, cousin.countAllMembers());
        assertEquals(0, cousin.countSubgroups());
        assertEquals(0, cousin.countAllSubgroups());
        assertTrue(cousin.isEmpty());
        validateCollection(cousin.getAllSubgroups(),
                           new Group[] {});


    }

    /**
     * Test subgroups add/retrieve/remove
     */
    public void testGetAllMembers() throws Exception {
        // Create group
        Group greatGrandma = createGroup("Great Grandma");

        // Create subgroups
        Group parentGroup = createGroup("Parent");
        Group childGroup = createGroup("Child");

        parentGroup.addSubgroup(childGroup);

        User parentMember1 = createUser("Member1", "Parent",
                                        "member1@parent.com", "ParentMember1");

        User parentMember2 = createUser("Member2", "Parent",
                                        "member2@parent.com", "ParentMember2");

        User parentMember3 = createUser("Member3", "Parent",
                                        "member3@parent.com", "ParentMember3");

        User childMember1 = createUser("Member1", "Child",
                                       "member1@child.com", "ChildMember1");

        User childMember2 = createUser("Member2", "Child",
                                       "member2@child.com", "ChildMember2");


        parentGroup.addMember(parentMember1);
        parentGroup.addMember(parentMember2);
        parentGroup.addMember(parentMember3);
        childGroup.addMember(childMember1);
        childGroup.addMember(childMember2);

        saveAll(new DomainObject[] {childMember1, childMember2,
                                    parentMember1, parentMember2,
                                    parentMember3,
                                    childGroup, parentGroup});

        validateCollection(childGroup.getAllMembers(),
                           new Party[] {childMember1, childMember2});

        validateCollection(parentGroup.getAllMembers(),
                           new Party[] {parentMember1, parentMember2,
                                        parentMember3, childMember1,
                                        childMember2});
    }


    private void saveAll(DomainObject[] objects) {
        saveAll(Arrays.asList(objects));
    }

    private void saveAll(List objects) {
        Iterator iter = objects.iterator();
        while (iter.hasNext()) {
            DomainObject o = (DomainObject) iter.next();
            // I'm having trouble in case where things are saved twice in
            // a row.  This if statement is here so I can easily disable
            // saving in case where isModified() is false.
            if (o.isModified()) {
                o.save();
            } else {
                o.save();
            }
        }
    }

    private void validateCollection(DomainCollection objects,
                                    ACSObject[] targetObjects)
        throws Exception
    {
        Collection targetIDs = getIDs(targetObjects);
        int ctr=0;
        while (objects.next()) {
            ctr++;
            ACSObject o = (ACSObject) objects.getDomainObject();
            if (!targetIDs.remove(o.getID())) {
                fail("Object collection should not contain " + o.getID());
            }
        }
        if (ctr != targetObjects.length) {
            fail("Object collection contains wrong number of objects.  " +
                 "Expected " + targetObjects.length + " objects." +
                 "Got " + ctr + " objects.");
        }

    }

    private Collection getIDs(ACSObject[] objects) {
        ArrayList list = new ArrayList();
        for (int i=0; i<objects.length; i++) {
            list.add(objects[i].getID());
        }
        return list;
    }



}
