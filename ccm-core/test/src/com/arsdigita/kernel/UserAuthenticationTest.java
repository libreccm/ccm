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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigDecimal;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests basic functionality of the UserAuthentication class
 *
 *
 * @author Phong Nguyen
 * @version 1.0
 * @see com.arsdigita.kernel.UserAuthentication
 */
public class UserAuthenticationTest extends BaseTestCase {

    

    private Session m_ssn;

    /**
     * Constructs a UserAuthenticationTest with the specified name.
     *
     * @param name Test case name.
     **/
    public UserAuthenticationTest( String name ) {
        super( name );
    }

    public void setUp()  {
        try {
            m_ssn = SessionManager.getSession();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            fail(e.getMessage());
        }
    }

    public void tearDown() {
    }


    /**
     * Tests database retrieval
     **/
    public void testUserAuthentication() throws Exception {

        User user = new User();
        BigDecimal idval = user.getID();
        String screenName = "phongn76_" + idval.toString();
        String givenName = "Phong";
        String familyName = "Nguyen";
        String emailAddress = "phong" + idval + "@arsdigita.com";
        String password = "acsrules";
        String passwordQuestion = "What is your lucky number?";
        String passwordAnswer = "7";

        // create a new user
        user.setScreenName(screenName);
        user.getPersonName().setGivenName(givenName);
        user.getPersonName().setFamilyName(familyName);
        user.setPrimaryEmail(new EmailAddress(emailAddress));
        user.save();

        // retrieve the user
        OID oid = new OID(User.BASE_DATA_OBJECT_TYPE, idval);
        user = new User(oid);
        assertEquals("The given name for the user is not correct.",
                     givenName, user.getPersonName().getGivenName());
        assertEquals("The family name for the user is not correct.",
                     familyName, user.getPersonName().getFamilyName());
        assertEquals("The screen name for the user is not correct.",
                     screenName, user.getScreenName());
        assertEquals("The primary email for the user is not correct.",
                     emailAddress, user.getPrimaryEmail().getEmailAddress());

        // set authentication data for the user
        UserAuthentication userAuth = UserAuthentication.createForUser(user);
        userAuth.setPassword(password);
        userAuth.setPasswordQuestion(passwordQuestion);
        userAuth.setPasswordAnswer(passwordAnswer);
        user.getPersonName().setGivenName(givenName);
        user.getPersonName().setFamilyName(familyName);
        userAuth.save();

        // retrieving authentication data
        userAuth = UserAuthentication.retrieveForUser(oid);
        assertEquals("The password question is not correct.",
                     passwordQuestion, userAuth.getPasswordQuestion());
        assertTrue("The password \"" + password + "\" should have been the valid " +
                   "password.", userAuth.isValidPassword(password));
        assertTrue("The answer \"" + passwordAnswer + "\" should have been the " +
                   "valid password.", userAuth.isValidAnswer(passwordAnswer));

        // retrieving authentication and user by login name
        userAuth = UserAuthentication.retrieveForLoginName(emailAddress);
        user = userAuth.getUser();
        assertEquals("The given name for the user is not correct.",
                     givenName, user.getPersonName().getGivenName());
        assertEquals("The family name for the user is not correct.",
                     familyName, user.getPersonName().getFamilyName());
        assertEquals("The screen name for the user is not correct.",
                     screenName, user.getScreenName());
        assertEquals("The primary email for the user is not correct.",
                     emailAddress, user.getPrimaryEmail().getEmailAddress());
        assertEquals("The password question is not correct.",
                     passwordQuestion, userAuth.getPasswordQuestion());
        assertTrue("The password \"" + password + "\" should have been the valid " +
                   "password.", userAuth.isValidPassword(password));
        assertTrue("The answer \"" + passwordAnswer + "\" should have been the " +
                   "valid password.", userAuth.isValidAnswer(passwordAnswer));

        // Make sure exception thrown with invalid login name
        try {
            UserAuthentication.retrieveForLoginName("This is a bogus login name");
            fail("No exception thrown when retrieving a user " +
                 "authentication record for a bogus email address");
        } catch (DataObjectNotFoundException e) {
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
