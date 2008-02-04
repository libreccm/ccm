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

/**
 * Test to illustrate a problem discovered with changing group
 * memberships.
 *
 * @author Kevin Scaldeferri
 */


public class GroupMembershipTest
    extends com.arsdigita.tools.junit.framework.BaseTestCase {

    public static final String versionId = "$Id: GroupMembershipTest.java 748 2005-09-02 11:57:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Group theGroup;
    private User user1;
    private User user2;

    public GroupMembershipTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(GroupMembershipTest.class);
    }

    protected void setUp() {
        theGroup = new Group();
        theGroup.setName("testMemberhipChange");
        theGroup.save();

        user1 = new User("Foo", "Bar", "user1@foo.com");
        user1.save();

        user2 = new User("Foo", "Bar", "user2@foo.com");
        user2.save();

    }

    /*
     * Group doesn't define an isMember() method currently, so
     * I have to fake something
     */
    private static Boolean isMember(Group group, User user) {
        UserCollection uc = group.getAllMemberUsers();
        Boolean retValue = Boolean.FALSE;
        while (uc.next()) {
            User testUser = uc.getUser();
            if (testUser.equals(user)) {
                retValue = Boolean.TRUE;
                uc.close();
                break;
            }
        }

        return retValue;
    }

    public void testMembershipChange() {
        theGroup.addMember(user1);
        theGroup.save();

        assertTrue("User 1 didn't pass the test",
                   isMember(theGroup, user1).booleanValue());

        assertTrue("User 2 did pass the test, but shouldn't have",
                   ! isMember(theGroup, user2).booleanValue());

        assertEquals(1, theGroup.getAllMemberUsers().size());

        theGroup.addMember(user2);
        theGroup.save();

        assertEquals(2, theGroup.getAllMemberUsers().size());
        assertTrue("User 1 didn't pass the second test",
                   isMember(theGroup, user2).booleanValue());
        assertTrue("User 2 didn't pass the second test",
                   isMember(theGroup, user2).booleanValue());
    }

    public void testDuplicateAddAndSave() {
        theGroup.addMember(user1);
        theGroup.save();
        theGroup.addMember(user1);
        theGroup.save();
    }

    public void testDuplicateAddAndRemove() {
        theGroup.addMember(user1);
        theGroup.addMember(user1);
        theGroup.save();

        assertTrue("User 1 wasn't a member after being added",
                   isMember(theGroup, user1).booleanValue());

        assertEquals(1, theGroup.getAllMemberUsers().size());

        theGroup.removeMember(user1);
        theGroup.save();

        assertTrue("User 1 was a member after being removed",
                   ! isMember(theGroup, user1).booleanValue());

        assertEquals(0, theGroup.getAllMemberUsers().size());
    }


    public void testContainedParties() {
        theGroup.addMember(user1);
        theGroup.save();
        
        DomainCollection parties = theGroup.getContainedParties();
        assertTrue("There is at least one contained party", 
                   parties.next());
        DomainObject party = parties.getDomainObject();
        assertTrue("The contained party is user1",
                   party.equals(user1));
        
        assertTrue("There is not a second contained party",
                   !parties.next());
    }
}
