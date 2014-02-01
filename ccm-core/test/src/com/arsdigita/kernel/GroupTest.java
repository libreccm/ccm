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
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashSet;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * Tests basic functionality of kernel classes
 *
 *
 * @author Tristan Cohen
 * @version 1.0
 * @see com.arsdigita.kernel
 */


public class GroupTest extends BaseTestCase {


    private static final String m_baseDataObjectType =
        "com.arsdigita.kernel.Group";

    private static final String m_badBaseDataObjectType =
        "com.arsdigita.kernel.User";

    // Creates a s_logging category with name = to the full name of
    // the EmailAddressTest class.
    private static Logger s_log = Logger.getLogger( GroupTest.class.getName() );

    /**
     * Constructs a EmailAddressTest with the specified name.
     *
     * @param name Test case name.
     **/
    public GroupTest( String name ) {
        super( name );
    }

    public static Group _createGroup() {
        Group group = new Group();
        try {
            group.setName( "Taco Eaters Anonymous(" + Sequences.getNextValue() + ")" );
        } catch ( SQLException e ) {
            fail( "DB error whent trying to create a new group" );
        }
        return group;
    }

    public void _testGetSetName( Group group ) {

        // Should name be unique?
        String name = "The Taco Club";

        group.setName( name );
        assertEquals( group.getName(), name );
    }

    public User _createUser() throws Exception {
        User new_user = new User();

        new_user.setPrimaryEmail( new EmailAddress( "tristan(" + Sequences.getNextValue() +")@arsdigita.com" ) );
        new_user.getPersonName().setGivenName( "Mega Toucus" );
        new_user.getPersonName().setFamilyName( "Jehosophat" );

        return new_user;
    }

    public static Group _createGroup( String groupName ) {
        Group new_group = new Group();
        new_group.setName( groupName );

        return new_group;
    }


    private User _createUser( String email, String given_name, String family_name ) {
        User new_user = new User();

        try {
            new_user.setPrimaryEmail( new EmailAddress( email + "_" + Sequences.getNextValue() ) );
        } catch ( Exception e ) {
            fail( "Database Error in _createUser( 3 args )" );
        }

        new_user.getPersonName().setGivenName( given_name );
        new_user.getPersonName().setFamilyName( family_name );

        return new_user;
    }

    public static void _validateCollection( PartyCollection collection, Party[] members ) {

        HashSet expected = new HashSet();
        HashSet actual = new HashSet();

        for ( int i = 0; i < members.length; i++ ) {
            expected.add( members[i].getDisplayName() );
        }

        Party p;
        while ( collection.next() ) {
            actual.add(collection.getDisplayName());
        }

        assertEquals(expected, actual);
    }

    public void _bulkSave( ACSObject[] objects ) {
        _bulkSave( objects, false );
    }

    public void _bulkSave( ACSObject[] objects, boolean forceP  ) {
        // if i had my druthers you wouldn't have to always save.
        // if the system is altered to deal with that uncomment this.
        // if ( true ) { return; }

        for ( int i = 0; i < objects.length; i++ ) {
            objects[i].save();
        }
    }

    public void testMembershipInheritance() throws Exception {

        User tristan = _createUser( "tristan@arsdigita.com", "Tristan", "Cohen" );
        User crag    = _createUser( "cwolfe@arsdigita.com", "Crag", "Wolfe" );
        User andrew  = _createUser( "aegruemet@arsdigita.com", "Andrew", "Gruemet" );
        User oumi    = _createUser( "oumi@arsdigita.com", "Oumi", "Mehrota" );
        User mike    = _createUser( "mbryzek@arsdigita.com", "Mike", "Bryzek" );
        User guido   = _createUser( "guido@python.org", "Guido", "Van Rossum" );
        User bill    = _createUser( "billg@microsoft.com", "Bill", "Gates" );

        Group worldBank = _createGroup( "World Bank" );
        Group site59    = _createGroup( "Site59" );
        Group prof_ser  = _createGroup( "aD Professional Services" );
        Group product   = _createGroup( "aD Product" );
        Group pure_evil = _createGroup( "Microsoft" );
        Group open_source = _createGroup( "Open Source Developers" );

        Group common_sense = _createGroup( "Common Sense" );

        ACSObject[] all_obs = { tristan, crag, andrew, oumi, mike, guido, bill, common_sense, worldBank, site59, prof_ser, product, pure_evil, open_source };

        _bulkSave( all_obs, true );

        // Set up group hierarchy.
        prof_ser.addSubgroup( site59 );
        prof_ser.addSubgroup( worldBank );
        prof_ser.addSubgroup( common_sense );

        open_source.addSubgroup( product );
        open_source.addSubgroup( prof_ser );
        open_source.addSubgroup( common_sense );

        worldBank.addSubgroup( common_sense );

        product.addSubgroup( common_sense );

        // Set up group membership.

        site59.addMember( tristan );
        site59.addMember( crag );

        worldBank.addMember( andrew );

        product.addMember( mike );
        product.addMember( oumi );
        product.addMember( tristan );

        open_source.addMember( guido );
        open_source.addMember( crag );

        pure_evil.addMember( bill );

        _bulkSave( all_obs, true );

        // 1 deep groups
        s_log.info( "Testing immediate subGroups" );
        _validateCollection( open_source.getSubgroups(), new Group[] { prof_ser, common_sense, product } );
        _validateCollection( prof_ser.getSubgroups(), new Group[] { site59, worldBank, common_sense } );
        _validateCollection( product.getSubgroups(), new Group[] { common_sense } );
        _validateCollection( pure_evil.getSubgroups(), new Group[] {} );
        _validateCollection( site59.getSubgroups(), new Group[] {} );
        _validateCollection( worldBank.getSubgroups(), new Group[] { common_sense } );
        _validateCollection( common_sense.getSubgroups(), new Group[] {} );

        // all sub groups.
        s_log.info( "Testing all subGroups" );
        _validateCollection( open_source.getAllSubgroups(), new Group[] { product, prof_ser, site59, worldBank, common_sense } );
        _validateCollection( prof_ser.getAllSubgroups(), new Group[] { site59, worldBank, common_sense } );
        _validateCollection( product.getAllSubgroups(), new Group[] { common_sense } );
        _validateCollection( pure_evil.getAllSubgroups(), new Group[] {} );
        _validateCollection( site59.getAllSubgroups(), new Group[] {} );
        _validateCollection( worldBank.getAllSubgroups(), new Group[] { common_sense } );
        _validateCollection( common_sense.getAllSubgroups(), new Group[] {} );


        // 1 deep users
        s_log.info( "Testing 1 deep users" );
        _validateCollection( open_source.getMembers(), new User[] { guido, crag } );
        _validateCollection( prof_ser.getMembers(), new User[] {} );
        _validateCollection( product.getMembers(), new User[] { oumi, mike, tristan } );
        _validateCollection( pure_evil.getMembers(), new User[] { bill } );
        _validateCollection( site59.getMembers(), new User[] { tristan, crag } );
        _validateCollection( worldBank.getMembers(), new User[] { andrew } );
        _validateCollection( common_sense.getMembers(), new User[] {} );

        // all users
        s_log.info( "Testing all Members" );
        _validateCollection( open_source.getAllMembers(), new User[] { guido, crag, tristan, mike, oumi, andrew } );
        _validateCollection( prof_ser.getAllMembers(), new User[] { crag, tristan, andrew } );
        _validateCollection( product.getAllMembers(), new User[] { oumi, mike, tristan } );
        _validateCollection( pure_evil.getAllMembers(), new User[] { bill } );
        _validateCollection( site59.getAllMembers(), new User[] { crag, tristan } );
        _validateCollection( worldBank.getAllMembers(), new User[] { andrew } );
        _validateCollection( common_sense.getAllMembers(), new User[] {} );

        // 1 deep contained parties
        s_log.info( "Testing 1 deep contained parties" );
        _validateCollection( open_source.getContainedParties(),new Party[] {
            guido, crag, prof_ser, common_sense, product } );
        _validateCollection( prof_ser.getContainedParties(), new Party[] {
            site59, worldBank, common_sense } );
        _validateCollection( product.getContainedParties(), new Party[] {
            oumi, mike, tristan, common_sense } );
        _validateCollection( pure_evil.getContainedParties(), new Party[] {
            bill } );
        _validateCollection( site59.getContainedParties(), new Party[] {
            tristan, crag } );
        _validateCollection( worldBank.getContainedParties(), new Party[] {
            andrew, common_sense } );
        _validateCollection( common_sense.getContainedParties(), new Party[] {} );

        // all conatined parties
        s_log.info( "Testing all contained parties" );
        _validateCollection(open_source.getAllContainedParties(), new Party[] {
            guido, crag, tristan, mike, oumi, andrew, common_sense, site59,
            worldBank, prof_ser, product } );
        _validateCollection( prof_ser.getAllContainedParties(), new Party[] {
            crag, tristan, andrew, site59, worldBank, common_sense } );
        _validateCollection( product.getAllContainedParties(), new Party[] {
            oumi, mike, tristan, common_sense } );
        _validateCollection( pure_evil.getAllContainedParties(), new Party[] {
            bill } );
        _validateCollection( site59.getAllContainedParties(), new Party[] {
            crag, tristan } );
        _validateCollection( worldBank.getAllContainedParties(), new Party[] {
            andrew, common_sense } );
        _validateCollection( common_sense.getAllContainedParties(), new Party[] {} );

        // all member users
        s_log.info( "Testing all MemberUsers" );
        _validateCollection( open_source.getAllMemberUsers(), new User[] { guido, crag, tristan, mike, oumi, andrew } );
        _validateCollection( prof_ser.getAllMemberUsers(), new User[] { crag, tristan, andrew } );
        _validateCollection( product.getAllMemberUsers(), new User[] { oumi, mike, tristan } );
        _validateCollection( pure_evil.getAllMemberUsers(), new User[] { bill } );
        _validateCollection( site59.getAllMemberUsers(), new User[] { crag, tristan } );
        _validateCollection( worldBank.getAllMemberUsers(), new User[] { andrew } );
        _validateCollection( common_sense.getAllMemberUsers(), new User[] {} );

        // Remove a member in a situation where the member should be removed.
        s_log.debug( "Removing Member" );
        worldBank.removeMember( andrew );

        _bulkSave( all_obs );

        s_log.debug( "Immediate Membership should change" );
        _validateCollection( worldBank.getAllMemberUsers(), new User[] {} );
        s_log.debug( "Deeper membership shouldn't change" );
        _validateCollection( prof_ser.getAllMemberUsers(), new User[] { crag, tristan } );
        worldBank.addMember( andrew );

        _bulkSave( all_obs );

        // Remove a member, but due to sub-grouping .. the user should still be there.
        s_log.debug( "Removing a user from one sub-group who is also in a different sub-group" );
        open_source.removeMember( crag );

        _bulkSave( all_obs );

        _validateCollection( open_source.getAllMemberUsers(), new User[] { guido, crag, tristan, mike, oumi, andrew } );
        open_source.addMember( crag );

        _bulkSave( all_obs );

        // sub-group removal .. should change membership.
        s_log.debug( "Removing a subGroup" );
        prof_ser.removeSubgroup( site59 );

        _bulkSave( all_obs );

        _validateCollection( prof_ser.getAllSubgroups(), new Group[] { worldBank, common_sense } );
        prof_ser.addSubgroup( site59 );

        _bulkSave( all_obs );

        // removal shouldn't make a difference
        s_log.debug( "Removing a subGroup, but the group is in the system by some other group" );
        open_source.removeSubgroup( common_sense );

        _bulkSave( all_obs );

        _validateCollection( open_source.getAllSubgroups(), new Group[] { product, prof_ser, site59, worldBank, common_sense } );
        open_source.addSubgroup( common_sense );

        _bulkSave( all_obs );
    }

    public void testUserDeletion() {
        User guido = _createUser("guido@python.org", "Guido", "Van Rossum");
        Group open_source = _createGroup("Open Source Developers");
        open_source.addMember(guido);
        open_source.save();
        assertTrue(open_source.getMembers().size() == 1);
        guido.delete();
        assertTrue(open_source.getMembers().size() == 0);
    }

    public void testGroupDeletion() {
        User cb = _createUser("blizzard@redhat.com", "Chris", "Blizzard");
        Group humanity = _createGroup("Humans");
        Group open_source = _createGroup("Open Source Developers");
        Group red_hat = _createGroup("Red Hat Developers");
        humanity.addSubgroup(open_source);
        humanity.save();
        open_source.addSubgroup(red_hat);
        open_source.addMember(cb);
        open_source.save();
        red_hat.addMember(cb);
        red_hat.save();
        assertTrue(humanity.hasSubgroup(red_hat));
        assertTrue(humanity.hasMember(cb));
        open_source.delete();
        assertFalse(humanity.hasSubgroup(red_hat));
        assertFalse(humanity.hasMember(cb));
    }

    /**
     * Test designed to prevent circular grouping.
     **/
    public void testCircularity() {

        // since users cannot have sub-users we are not going to any
        // circular user tests.

        // it is also assumed that we are going to catch circularity at
        // the save or add group events.


        Group pa = _createGroup( "Dad" );
        Group daughter = _createGroup( "Daughter" );

        _bulkSave( new ACSObject[] { pa, daughter } );

        try {
            pa.addSubgroup( pa );
            pa.save();
            fail( "Successfully added a group to itself as a subGroup" );
        } catch ( Exception e ) {}


        // fix up the group.
        try {
            pa.removeSubgroup( pa );
            pa.save();
        } catch ( Exception e ) {}

        if ( true ) { return; }

        // try adding a group which has a subGroup which is the group you
        // are trying to add to.
        try {
            daughter.addSubgroup( pa );
            daughter.save();
            // below is the bad part.
            pa.addSubgroup( daughter );
            pa.save();
            fail( "Successfully add a subGroup which contains the group" );
        } catch ( Exception e ) {}

        // fix up our groups.
        try {
            daughter.removeSubgroup( pa );
            daughter.save();
        } catch ( Exception e ) {}

        try {
            pa.removeSubgroup( daughter );
            pa.save();
        } catch ( Exception e ) {}

        pa.addSubgroup( daughter );
        pa.save();

        // try to add a group to its own subGroup
        try {
            daughter.addSubgroup( pa );
            daughter.save();
            fail( "Successfully added a group to its own subGroup" );
        } catch ( Exception e ) {}

    }

    public void testPersistence() {
        // maybe put a test in here later.
    }

    /**
     *  Test instantiation via DataObject
     **/
    public void testInstantiationViaDataObject() {
        s_log.debug( "Instantiation By DataObject" );

        Group group;
        DataObject dob;
        s_log.debug( "Attempting to instantiate improperly" );

        dob = SessionManager.getSession().create( m_badBaseDataObjectType );

        try {
            group = new Group( dob );
            fail( "Successfully created object with incorrect DataObject" );
        } catch ( Exception e ) {}

        s_log.debug( "Attempting to instantiate properly." );

        dob = SessionManager.getSession().create( m_baseDataObjectType );
        group = new Group( dob );

        _testGetSetName( group );
    }

    /**
     *  Test instantiation via ObjectType
     **/
    public void testInstantiationViaObjectType() throws Exception {
        s_log.debug( "Instantiation By ObjectType" );

        ObjectType o;
        Group group;

        o = SessionManager.getMetadataRoot().getObjectType( m_badBaseDataObjectType );
        try {
            group = new Group( o );
            fail( "Successfully created a Group with an invalid ObjectType object." );
        } catch( Exception e ) {
        }

        o = SessionManager.getMetadataRoot().getObjectType( m_baseDataObjectType );

        group = new Group( o );

        _testGetSetName( group );
    }

    /**
     *  Test instantiation via ObjectTypeName
     **/
    public void testInstantiationViaObjectTypeName() throws Exception {
        s_log.debug( "Instantiation by Object Type Name" );

        Group group;
        try {
            group = new Group( m_badBaseDataObjectType );
            fail( "Successfully created a Group with an invalid ObjectType Name." );
        } catch( Exception e ) {
        }

        group = new Group( m_baseDataObjectType );

        _testGetSetName( group );
    }

    /**
     * Test instantiation with blank constructor
     **/
    public void testInstantiationViaBlankConstructor() throws Exception {
        s_log.debug( "Instantiation by Blank Constructor" );
        Group group = new Group();

        _testGetSetName( group );
    }


    /**
     * Test instantiation with OID constructor
     **/

    public void testInstantiationViaOIDConstructor() throws Exception {
        s_log.debug( "Instantiation by OID Constructor" );

        OID oid;
        Group group;


        oid = new OID( m_badBaseDataObjectType, Sequences.getNextValue());

        try {
            group = new Group( oid );
            fail( "Successfully instantiated Group with an invalid OID." );
        } catch( Exception e ) {}


        group = _createGroup();

        group.save();

        group = new Group( group.getOID() );

        _testGetSetName( group );
    }

    public static void _validateCollection( RoleCollection collection, String[] roleNames ) {

        HashSet results = new HashSet();

        for ( int i = 0; i < roleNames.length; i++ ) {
            results.add( roleNames[i] );
        }

        Role r;
        while ( collection.next() ) {
            r = collection.getRole();
            s_log.info( "Evaluating : Role " + r.getName() );
            assertTrue( results.remove( r.getName() ) );
        }

        s_log.info( "About to confirm that the set is empty." );
        assertEquals( 0, results.size() );
    }

    /**
     * Tests creating and retrieving roles through groups
     **/
    public void testRoles() {
        Group group = _createGroup( "Test Roles Group" );
        group.save();
        group = _retrieveGroup(group.getID());

        String[] roleNames = new String[] { "Role 1", "Role 2", "Role 3" };
        for (int i = roleNames.length - 1; i >= 0; i--) {
            group.createRole(roleNames[i]);
        }
        group.save();

        // Retrieve the group and make sure we can access the roles
        group = _retrieveGroup(group.getID());
        RoleCollection roles = group.getRoles();

        assertEquals("We got " + roles.size() +
                     " roles when we expected " + roleNames.length,
                     roleNames.length, roles.size());

        // Make sure that the correct set of roles was returned.
        _validateCollection (roles, roleNames);

        // Now make sure ordering works correctly
        group = _retrieveGroup(group.getID());
        roles = group.getOrderedRoles();
        assertEquals("We got " + roles.size() +
                     " roles when we expected " + roleNames.length,
                     roleNames.length, roles.size());

        int i = 0;
        while (roles.next()) {
            Role role = roles.getRole();
            assertEquals("After ordering, Role name was " + role.getName() +
                         ", not " + roleNames[i] + " as expected",
                         role.getName(), roleNames[i]);
            i++;
        }

    }



    /**
     * Tests creating and retrieving one role in a group. This exposed
     * a bug once upon a time.
     **/
    public void testOneRole() {
        Group group = _createGroup( "Test One Role Group" );
        group.save();
        group = _retrieveGroup(group.getID());

        String roleName = "test";
        Role r = group.createRole(roleName);
        r.setDescription("Foo");
        group.save();

        // Retrieve the group and make sure we can access the roles
        group = _retrieveGroup(group.getID());
        RoleCollection roles = group.getOrderedRoles();
        assertTrue("One role did not return a data association. It returned a: " +
                   roles.getClass().getName(),
                   roles instanceof RoleCollection);

        try {
            // Make sure we can retrieve the role by id
            Role foo = new Role(r.getID());
        } catch (DataObjectNotFoundException e) {
            e.printStackTrace();
            fail("Could not retrieve role by id");
        }
    }

    /**
     * Test the membership methods
     *
     **/
    public void testMembership() {
        Group supergroup = _createGroup("Test super group");
        Group subgroup = _createGroup("Test sub group");
        User oneUser = null;
        try {
            oneUser = _createUser();
        } catch (Exception e) {
            fail("TestMembershipCheck failed on creating user");
        }

        oneUser.save();
        supergroup.save();
        subgroup.save();

        supergroup.addSubgroup(subgroup);
        subgroup.addMember(oneUser);
        supergroup.save();
        subgroup.save();

        assertTrue("user is a direct member of subgroup failed",
                   subgroup.hasDirectMember(oneUser));

        assertTrue("user is not a direct member of super group",
                   !supergroup.hasDirectMember(oneUser));

        assertTrue("user should be a member of super group",
                   supergroup.hasMember(oneUser));

    }

    /**
     * Tests if members of a group's subgroup remain members when the direct
     * path from the group to the subgroup is removed, leaving only an
     * indirect path.
     */
    public void testMembersFromSubgroup() throws Exception {
        Group g1 = _createGroup("g1");
        Group g2 = _createGroup("g2");
        Group g3 = _createGroup("g3");
        User user = _createUser("man@example.com", "a", "man");
        g1.addSubgroup(g2);
        g2.addSubgroup(g3);
        g3.addMember(user);
        g3.save();

        g1.addSubgroup(g3);
        g1.save();
        assertEquals(1, g1.getAllMembers().size());

        g1.removeSubgroup(g3);
        g1.save();
        assertEquals(1, g1.getAllMembers().size());
    }

    /**
     * Tests if a group remains a subgroup even if an indirect path by which
     * it is a subgroup is removed.
     */
    public void testRemoveSubgroup() {
        Group g1 = _createGroup("g1");
        Group g2 = _createGroup("g2");
        Group g3 = _createGroup("g3");
        Group g4 = _createGroup("g4");

        // make g3 a subgroup of g1 directly and through g2
        g2.addSubgroup(g3);
        g1.addSubgroup(g2);
        g1.addSubgroup(g3);
        g3.addSubgroup(g4);
        g3.save();

        g2.removeSubgroup(g3);
        g2.save();

        // g1 should have g2, g3, and g4 as subgroups
        assertEquals(3, g1.getAllSubgroups().size());
    }

    /**
     * Tests if all subgroups are removed when the head of a small graph of
     * subgroups is removed.
     */
    public void testSubsubgroups() {
        Group g1 = _createGroup("g1");
        Group g2 = _createGroup("g2");
        Group g3 = _createGroup("g3");
        Group g4 = _createGroup("g4");

        g2.addSubgroup(g3);
        g2.addSubgroup(g4);
        g3.addSubgroup(g4);
        g1.addSubgroup(g2);

        assertEquals(3, g1.getAllSubgroups().size());

        g1.removeSubgroup(g2);
        g1.save();

        assertEquals(0, g1.getAllSubgroups().size());
    }

    private Group _retrieveGroup(BigDecimal groupId) {
        Group group = null;
        try {
            group = new Group(groupId);
        } catch (DataObjectNotFoundException e) {
            fail("Could not retrieve group: " + groupId);
        }
        return group;
    }

    public static Test suite() {
        //
        // Reflection is used here to add all
        // the testXXX() methods to the suite.
        //
        return new TestSuite(GroupTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
