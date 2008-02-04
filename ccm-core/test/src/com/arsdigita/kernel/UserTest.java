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
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupCollection;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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


public class UserTest extends BaseTestCase {

    public static final String versionId = "$Id: UserTest.java 744 2005-09-02 10:43:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Session m_ssn;

    private static final String s_badDataObjectType =
        "com.arsdigita.kernel.Group";

    // Creates a logging category with name = to the full name of
    // this UserTest class.
    private static Logger s_log = Logger.getLogger( UserTest.class.getName() );

    private String m_screenName;
    private EmailAddress m_email;

    /**
     * Constructs a UserTest with the specified name.
     *
     * @param name Test case name.
     **/
    public UserTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        s_log.debug( "Setting up" );
        m_screenName = "OoooMe!";
        m_email = new EmailAddress( "oumi(" + Sequences.getNextValue() +
                                    ")@arsdigita.com" );

        try {
            m_ssn = SessionManager.getSession();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.getMessage());
        }
    }

    public void tearDown() {
    }

    public User _createUser() throws Exception {
        User new_user = new User();
        new_user.setPrimaryEmail( new EmailAddress( "tristan(" +
                                                    Sequences.getNextValue() +
                                                    ")@arsdigita.com" ) );
        new_user.getPersonName().setGivenName( "Mega Toucus" );
        new_user.getPersonName().setFamilyName( "Jehosophat" );

        return new_user;
    }

    public Group _createBadUser() throws Exception {
        Group new_group = new Group();

        new_group.setPrimaryEmail( new EmailAddress( "tristan(" +
                                                     Sequences.getNextValue() +
                                                     ")@arsdigita.com" ) );
        new_group.setName("tristan");

        return new_group;
    }

    /**
     *  Test instantiation via DataObject
     *     1) This is
     **/
    public void testInstantiationViaDataObject() throws Exception {
        s_log.debug( "Instantiation By DataObject" );

        DataObject dob =
            SessionManager.getSession().create( User.BASE_DATA_OBJECT_TYPE );

        User user;
        user = new User( dob );
        _runStandardTests( user );
        dob = SessionManager.getSession().create( s_badDataObjectType );

        try {
            user = new User( dob );
            fail( "Successfully created a user with an invalid data object." );
        } catch( Exception e ) {
        }
    }

    /**
     *  Test instantiation via ObjectType
     **/
    public void testInstantiationViaObjectType() throws Exception {
        s_log.debug( "Instantiation By ObjectType" );
        ObjectType o =
            SessionManager.getMetadataRoot().getObjectType( User.BASE_DATA_OBJECT_TYPE );
        User user;
        try {
            user = new User( o );
            _runStandardTests( user );
        } catch( Exception e ) {
            fail( e.getMessage() );
        }

        o = SessionManager.getMetadataRoot().getObjectType( s_badDataObjectType );
        try {
            user = new User( o );
            fail( "Successfully created a user with an invalid ObjectType object." );
        } catch( Exception e ) {
        }
    }

    /**
     *  Test instantiation via ObjectTypeName
     **/
    public void testInstantiationViaObjectTypeName() throws Exception {
        s_log.debug( "Instantiation by Object Type Name" );

        User user;
        try {
            user = new User( User.BASE_DATA_OBJECT_TYPE );
            _runStandardTests( user );
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }

        try {
            user = new User( s_badDataObjectType );
            fail( "Successfully created a user with an invalid ObjectType Name." );
        } catch ( Exception e ) {}
    }

    /**
     * Test instantiation with blank constructor
     **/
    public void testInstantiationViaBlankConstructor() throws Exception {
        s_log.debug( "Instantiation by Blank Constructor" );
        User user = new User();
        _runStandardTests( user );
    }


    /**
     * Test instantiation with OID constructor
     **/

    public void testInstantiationViaOIDConstructor() throws Exception {
        s_log.debug( "Instantiation by OID Constructor" );

        // Shouldn't be able to instantiate a user with an OID constructor
        // unless the OID belongs to an object already in the database.

        try {
            User dump_user = new User(new OID(User.BASE_DATA_OBJECT_TYPE, 0));
            fail( "Successfully instantiated a User by OID for a user whose OID is not currently in the database." );
        } catch ( Exception e ) {}

        User n_user = _createUser();

        n_user.save();

        OID oid = n_user.getOID();

        User user;
        try {
            user = new User( oid );
            _runStandardTests( user );
        } catch( Exception e ) {
            fail( e.getMessage() );
        }


        Group bad_user = _createBadUser();

        bad_user.save();
        oid = bad_user.getOID();

        try {
            user = new User( oid );
            fail( "Successfully instantiated user with an invalid OID." );
        } catch( Exception e ) {}

        // Clean up our junk
        bad_user.delete();
        n_user.delete();
    }

    public void testRetrieveAllUsers() throws Exception {
        // get a count of how many users are already in the DB.
        int origUserCount = (int) m_ssn.retrieve(User.BASE_DATA_OBJECT_TYPE).size();

        HashMap addedUsers = new HashMap();

        // Add 20 more users
        for (int i=0; i<20; i++) {
            User u = _createUser();
            u.setScreenName("Test Screen Name " + Sequences.getNextValue());
            u.save();
            addedUsers.put(u.getID(), u);
        }

        // Now verify that the 20 new users are retrieved by
        // User.retrieveAll().

        UserCollection allUsers = User.retrieveAll();
        int j=0;

        while (allUsers.next()) {
            j++;

            User addedUser = (User) addedUsers.get(allUsers.getID());

            if (addedUser == null) {
                // skip to next iteration because the user we retrieved
                // is not one of the users that we added.
                continue;
            }

            User retrievedUser = allUsers.getUser();

            assertEquals(addedUser.getID(), retrievedUser.getID() );
            assertEquals(addedUser.getScreenName(), retrievedUser.getScreenName() );
            assertEquals(addedUser.getPrimaryEmail(), retrievedUser.getPrimaryEmail() );
            // right now PersonName.equals() isn't implemented.
            assertEquals(addedUser.getPersonName().toString(), retrievedUser.getPersonName().toString() );

            // Remove each user that we retrieved
            addedUsers.remove(retrievedUser.getID());

        }

        assertEquals("Incorrect number of users were retrieved",
                     origUserCount + 20, j);

        // users should be empty if we retrieved all the users that were added
        assertTrue("Some of the added users were not retrieved",
                   addedUsers.isEmpty());
    }

    /**
     * Check that the user group memberships are working
     *
     **/
    public void testGroupMembership() {

        Group supergroup = _createGroup();
        Group subgroup = _createGroup();
        Group miscgroup = _createGroup();

        User oneUser = null;
        try {
            oneUser = _createUser();
        } catch (Exception e) {
            fail("TestMembershipCheck failed on creating user");
        }

        // user is direct member of subgroup
        // user is a member (direct and indirect) of sub group and super group
        oneUser.save();
        subgroup.save();
        supergroup.save();

        supergroup.addSubgroup(subgroup);
        subgroup.addMember(oneUser);
        subgroup.save();
        supergroup.save();

        GroupCollection groupCollection = null;

        groupCollection = oneUser.getGroups();

        assertEquals("Expect only one member in direct membership",
                     1, groupCollection.size());

        groupCollection.next();

        Group reloadGroup = groupCollection.getGroup();
        assertEquals("the subgroup should be the direct member user is in",
                     reloadGroup, subgroup);

        groupCollection.close();

        groupCollection = oneUser.getAllGroups();

        assertEquals("getAllmembers should return two groups",
                     2, groupCollection.size());

        // Add user to direct membership for verification
        miscgroup.addMember(oneUser);
        miscgroup.save();

        groupCollection = oneUser.getGroups();
        assertEquals("There should be two direct groups ",
                     2, groupCollection.size());


    }

    public void testGetEmailAddresses() throws Exception {
        User user = _createUser();

        assertEquals(1, iteratorSize(user.getEmailAddresses()));
        assertEquals(0, iteratorSize(user.getAlternateEmails()));

        user.save();

        assertEquals(1, iteratorSize(user.getEmailAddresses()));
        assertEquals(0, iteratorSize(user.getAlternateEmails()));

        user.addEmailAddress(new EmailAddress("alternate1@email.com"));
        user.addEmailAddress(new EmailAddress("alternate2@email.com"));

        assertEquals(3, iteratorSize(user.getEmailAddresses()));
        assertEquals(2, iteratorSize(user.getAlternateEmails()));

        user.save();

        assertEquals(3, iteratorSize(user.getEmailAddresses()));
        assertEquals(2, iteratorSize(user.getAlternateEmails()));

    }

    private int iteratorSize(Iterator iter) {
        int i = 0;
        while (iter.hasNext()) {
            iter.next();
            i++;
        }
        return i;
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

    public void _runStandardTests( User user ) throws Exception {
        _testPersonName( user );
        _testScreenName( user );
        _testPrimaryEmail( user );
        _testParty( user );
        _testPersistence( user );
    }

    public void _testParty( User party ) throws Exception {

        EmailAddress[] e = { new EmailAddress( "tristan(" + Sequences.getNextValue() +")@arsdigita.com" ),
                             new EmailAddress( "oumi(" + Sequences.getNextValue() + ")@arsdigita.com" ),
                             new EmailAddress( "justin(" + Sequences.getNextValue() + ")@arsdigita.com" )
        };

        _testPartyPrimaryEmail( party, e );
        _testPartyMultipleEmailAddresses( party, e );
        _testPartyURI( party );
    }

    public void _testPartyPrimaryEmail( Party party, EmailAddress[] e ) {
        // Can we set it?
        party.setPrimaryEmail( e[1] );
        assertEquals( "TEST:  PrimaryEmail Switch 1 " + party.getPrimaryEmail() +
                      " is not equal to " + e[1],
                      party.getPrimaryEmail(), e[1]);

        // Can we reset it?
        party.setPrimaryEmail( e[2] );
        assertEquals( "TEST:  PrimaryEmail Switch 2 " + party.getPrimaryEmail() +
                      " is not equal to " + e[2],
                      party.getPrimaryEmail(), e[2]);
    }

    private void __testEmailAddressesForUniqueness( Party party ) {

        List emailList = new ArrayList();

        Iterator iter = party.getEmailAddresses();
        while (iter.hasNext()) {
            EmailAddress emailAddress = (EmailAddress) iter.next();
            assertTrue( "Party " + party.getName() +
                        " has duplicate email address" + emailAddress.toString(),
                        !emailList.contains(emailAddress));

            emailList.add(emailAddress);
        }

    }

    private void _testPartyMultipleEmailAddresses( Party party, EmailAddress[] e ) {
        party.setPrimaryEmail( e[0] );

        party.addEmailAddress( e[1] );
        party.addEmailAddress( e[2] );

        s_log.info( "About to test Emails for Uniqueness." );
        __testEmailAddressesForUniqueness( party );

        // Add a duplicate email address
        party.addEmailAddress( e[1] );

        s_log.info( "About to test Emails for Uniqueness again." );
        __testEmailAddressesForUniqueness( party );


        // This is not fixed in the API yet.

        try {
            party.removeEmailAddress( party.getPrimaryEmail() );
            party.save();
            fail( "Successfully removed the primary Email \n" +
                  "Address and saved the party." );
        } catch ( Exception ex ) {}

        party.setPrimaryEmail( e[0] );
    }

    private void _testPartyURI( Party party ) {
        String uri = "http://www.fightclub.org/stream";

        party.setURI( uri );
        assertEquals( party.getURI(), uri );
    }

    public void _testPrimaryEmail( User user ) throws Exception {
        s_log.debug( "Testing Primary Email" );
        EmailAddress e_old = user.getPrimaryEmail();

        s_log.debug( "Succesfully retrieved PrimaryEmail" );

        user.setPrimaryEmail( m_email );
        assertEquals( user.getPrimaryEmail(), m_email );

        // TO DO: make sure you can set primary email to null.
        //user.setPrimaryEmail( null );
        //asser( user.getPrimaryEmail() == null );
    }

    public void _testPersistence( User user ) throws Exception {

        s_log.debug( "Testing Persistence" );
        user.setScreenName( m_screenName );
        user.save();

        // Try to get a new user from the database.
        // Confirm that it matches the one we saved.
        User u2 = new User( user.getID() );

        // at one point there may have been a bug in persistence that
        // caused the equality check to fail on OIDs but pass on IDs, so
        // we test both here.
        assertEquals( u2.getOID(), user.getOID() );
        assertEquals( u2.getID(), user.getID() );

        assertEquals( u2.getScreenName(), m_screenName );
        // assertEquals( u2, user );

        user.delete();
    }

    public void _testPersonName( User user ) throws Exception {
        s_log.debug( "Testing Person Name" );
        String p_old = user.getPersonName().toString();

        PersonName p_new = new PersonName();
        p_new.setGivenName( "Taco" );
        p_new.setFamilyName( "Burrito" );

        user.getPersonName().setGivenName( p_new.getGivenName() );
        user.getPersonName().setFamilyName( p_new.getFamilyName() );

        assertEquals( user.getPersonName().toString(), p_new.toString() );
        assertTrue( user.getPersonName().toString() + " is equal to " + p_old,
                    !user.getPersonName().toString().equals( p_old ) );
    }

    public void _testScreenName( User user ) throws Exception {
        s_log.debug( "Testing Screen Name" );
        String s_old = user.getScreenName();

        user.setScreenName( m_screenName );
        assertEquals( user.getScreenName(), m_screenName );
        user.setScreenName( s_old );
    }

    public static Test suite() {
        //
        // Reflection is used here to add all
        // the testXXX() methods to the suite.
        //
        return new TestSuite(UserTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
