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
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.sql.SQLException;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests basic functionality of the UserAuthentication class
 *
 *
 * @author Tristan Cohen
 * @version 1.0
 * @see com.arsdigita.kernel.UserAuthentication
 */
public class UserAuthenticationTestNew extends BaseTestCase {


    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.UserAuthentication";

    public static final String BAD_BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.kernel.Group";

    private User user;
    private Group bad_user;

    /**
     * Constructs a UserAuthenticationTest with the specified name.
     *
     * @param name Test case name.
     **/
    public UserAuthenticationTestNew( String name ) {
        super( name );
    }

    public void setUp()  {
        try {
            user = _createUser();
            bad_user = _createBadUser();
        } catch (SQLException se ) {
            se.printStackTrace( System.err );
            fail( se.getMessage() );
        }

        user.save();
        bad_user.save();
    }

    public void tearDown() {
        user.delete();
        bad_user.delete();
    }

    private User _createUser() throws SQLException {
        User new_user = new User();
        new_user.setScreenName( "Priopus" );
        new_user.getPersonName().setGivenName( "Dinky" );
        new_user.getPersonName().setFamilyName( "McDorkBurger" );
        new_user.setPrimaryEmail( new EmailAddress( "tristan(" + Sequences.getNextValue() + ")@arsdigita.com" ) );

        return new_user;
    }


    private Group _createBadUser() throws SQLException {
        Group new_bad_user = new Group();
        new_bad_user.setPrimaryEmail( new EmailAddress( "bad_user(" + Sequences.getNextValue() + ")@arsdigita.com" ) );
        return new_bad_user;
    }

    /**
     *  Test Constructors
     *
     *  To Do: Fix the instantiators to try to instantiate with bad ID's.
     **/

    public UserAuthentication _testInstantiationByCreateForUserByUserOB() throws Exception {
        // Object Signatures should prevent bad objects from being passed in here.
        UserAuthentication u_auth = UserAuthentication.createForUser( user );
        return u_auth;
    }

    public UserAuthentication _testInstantiationByCreateForUserByUserOID() throws Exception {
        UserAuthentication u_auth;
        try {
            u_auth = UserAuthentication.createForUser( bad_user.getOID() );
            fail( "Successfully set up a UserAuthentication for the OID of a non-user object" );
        } catch ( Exception e ) {}

        u_auth = UserAuthentication.createForUser( user.getOID() );
        return u_auth;
    }

    public UserAuthentication _testInstantiationByCreateForUserByBigDecimal() throws Exception {
        UserAuthentication u_auth;
        try {
            u_auth = UserAuthentication.createForUser( bad_user.getID() );
            fail( "Successfully set up a UserAuthentication for the ID of a non-user object" );
        } catch ( Exception e ) {}

        u_auth = UserAuthentication.createForUser( user.getID() );
        return u_auth;
    }

    public UserAuthentication _testInstantiationByRetrieveForUserByUserOB() throws Exception {
        // Object Signatures should prevent bad objects from being passed in here.
        UserAuthentication u_auth = UserAuthentication.retrieveForUser( user );
        return u_auth;
    }

    public UserAuthentication _testInstantiationByRetrieveForUserByUserOID() throws Exception {
        UserAuthentication u_auth;
        try {
            u_auth = UserAuthentication.retrieveForUser( bad_user.getOID() );
            fail( "Successfully retrieved a UserAuthentication for the OID of a non-user object" );
        } catch ( Exception e ) {}

        u_auth = UserAuthentication.retrieveForUser( user.getOID() );
        return u_auth;
    }

    public UserAuthentication _testInstantiationByRetrieveForUserByBigDecimal() throws Exception {
        UserAuthentication u_auth;
        try {
            u_auth = UserAuthentication.retrieveForUser( bad_user.getID() );
            fail( "Successfully retrieved a UserAuthentication for the ID of a non-user object" );
        } catch ( Exception e ) {}

        u_auth = UserAuthentication.retrieveForUser( user.getID() );
        return u_auth;
    }

    public UserAuthentication _testInstantiationByRetrieveForLoginName() throws Exception {
        UserAuthentication u_auth;
        try {
            u_auth = UserAuthentication.retrieveForLoginName( "gypsyMcgypsy" );
            fail( "Successfully retrieved a UserAuthentication for bad LoginName" );
        } catch ( Exception e ) {}

        u_auth =  UserAuthentication.retrieveForLoginName( user.getPrimaryEmail().toString() );
        return u_auth;
    }

    public UserAuthentication _testInstantiationByBlankConstructor() throws Exception {
        // there is no blank constructor.
        if ( true ) { return null; }

        UserAuthentication u_auth = null;
        //u_auth = new UserAuthentication();
        return u_auth;
    }

    public void testUserAuthentication() {

        // You shouldn't be able to retrieve a UserAuth Object without having
        // saved it at one point.
        try {
            UserAuthentication u_first = _testInstantiationByRetrieveForUserByUserOB();
        } catch ( Exception e ) {}


        UserAuthentication u_auth_c, u_auth_r, u_auth_canon;

        try {
            u_auth_r = _testInstantiationByRetrieveForUserByUserOB();
            fail( "Successfully retrieved a UserAuthentication Object when it had yet to be saved." );
        } catch ( Exception e ) {}

        try {
            u_auth_c = _testInstantiationByCreateForUserByUserOB();
            // This is the "official version" of the Authentication Object
            u_auth_canon = u_auth_c;
            u_auth_canon.save();

            // This should work now.
            u_auth_r = _testInstantiationByRetrieveForUserByUserOB();
            assertEquals( u_auth_c, u_auth_r );
            _runStandardTests( u_auth_c );
            _runStandardTests( u_auth_r );

            u_auth_c = _testInstantiationByCreateForUserByUserOID();
            u_auth_r = _testInstantiationByRetrieveForUserByUserOID();
            assertEquals( u_auth_c, u_auth_r );
            assertEquals( u_auth_c, u_auth_canon );
            _runStandardTests( u_auth_c );
            _runStandardTests( u_auth_r );

            u_auth_c = _testInstantiationByCreateForUserByBigDecimal();
            u_auth_r = _testInstantiationByRetrieveForUserByBigDecimal();
            assertEquals( u_auth_c, u_auth_r );
            assertEquals( u_auth_c, u_auth_canon );
            _runStandardTests( u_auth_c );
            _runStandardTests( u_auth_r );

            u_auth_r = _testInstantiationByRetrieveForLoginName();
            assertEquals( u_auth_c, u_auth_canon );
            _runStandardTests( u_auth_r );

            u_auth_r = _testInstantiationByBlankConstructor();
            _runStandardTests( u_auth_r );

            // Now delete the canonical UserAuthentication ob.
            u_auth_canon.delete();
        } catch ( Exception e ) {
            fail( "Some error" );
        }
    }

    public void _runStandardTests( UserAuthentication u_auth ) {
        _passwordTests( u_auth );
        _passwordQuestionTests( u_auth );
    }

    public void _passwordTests( UserAuthentication u_auth ) {
        String passwd = "Giant Pants   ";

        // This would work .. if we had set the password yet.
        assertEquals( u_auth.isValidPassword( passwd.trim() ), false );
        u_auth.setPassword( passwd );
        assertTrue( !u_auth.isValidPassword( passwd ) );
        assertTrue( u_auth.isValidPassword( passwd.trim() ) );

        u_auth.setPassword( null );
    }

    public void _passwordQuestionTests( UserAuthentication u_auth ) {
        String pwd_question = "What did the rake say to the hoe?";
        String pwd_answer   = "Hi ho";
        String bad_pwd_answer = "hi ho";

        u_auth.setPasswordQuestion( pwd_question );
        u_auth.setPasswordAnswer( pwd_answer );

        assertEquals( u_auth.getPasswordQuestion(), pwd_question );
        assertTrue( !u_auth.isValidAnswer( bad_pwd_answer ) );
        assertTrue( u_auth.isValidAnswer( pwd_answer ) );
    }

    public void _publicFieldsTest( UserAuthentication u_auth ) {
        assertEquals( u_auth.getBaseDataObjectType(), u_auth.BASE_DATA_OBJECT_TYPE );
    }

    public void testPersistence() {
        try {

            User user_1 = null;
            UserAuthentication u_auth_1, u_auth_test;
            String u_passwd_1, u_passwd_2;

            try {
                user_1 = _createUser();
                user_1.save();
            } catch ( Exception e ) {
                fail( "Difficulty creating testcase users" );

            }

            u_auth_1 = UserAuthentication.createForUser( user_1 );

            u_passwd_1 = "el taco grande";
            u_passwd_2 = "el poco burrito";

            u_auth_1.setPassword( u_passwd_1 );
            u_auth_1.save();

            u_auth_test = UserAuthentication.retrieveForUser( user_1 );

            assertTrue( u_auth_1.isValidPassword( u_passwd_1 ) );
            assertTrue( !u_auth_1.isValidPassword( u_passwd_2 ) );
            assertEquals( u_auth_1, u_auth_test );
            u_auth_1.delete();
            user_1.delete();
        } catch ( Exception e ) {
            fail( "Some sort of error" );
        }
    }

    public static Test suite() {
        //
        // Reflection is used here to add all
        // the testXXX() methods to the suite.
        //
        return new TestSuite(UserAuthenticationTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
