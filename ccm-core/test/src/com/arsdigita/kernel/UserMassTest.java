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
 * Tests that we can create and modify lots of users without running
 * out of memory.
 *
 *
 * @author Michael Bryzek
 * @version 1.0
 **/
public class UserMassTest extends BaseTestCase {

    public static final boolean FAILS = true;

    public static final String versionId = "$Id: UserMassTest.java 744 2005-09-02 10:43:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final int NUMBER_TO_TEST = 25000;
    private static final int NUMBER_TO_REPORT = 500;

    /**
     * Constructs a UserMassTest with the specified name.
     *
     * @param name Test case name.
     **/
    public UserMassTest( String name ) {
        super( name );
    }

    /**
     * Creates a user with a default email address
     **/
    public static User createUser(String givenName,
                                  String familyName) {
        return createUser(givenName, familyName, null);
    }


    /**
     * Creates a user with specified names and email.
     **/
    public static User createUser(String givenName,
                                  String familyName,
                                  String email) {
        if (email == null) {
            try {
                email = "user-mass-test-" +
                    Sequences.getNextValue() +
                    "@arsdigita.com";
            } catch (SQLException e) {
                e.printStackTrace(System.err);
                fail("Could not generate a unique email address");
            }
        }
        User user = new User();
        user.setPrimaryEmail( new EmailAddress(email) );
        user.getPersonName().setGivenName(givenName);
        user.getPersonName().setFamilyName(familyName);
        user.setScreenName("Random screen name for " + givenName);
        return user;
    }


    public void testMassCreateAndUpdate() {
        String givenName = "Michael";
        String familyName = "Bryzek";
        for ( int i = 1; i <= NUMBER_TO_TEST; i++ ) {
            if ( i % NUMBER_TO_REPORT == 0 ) {
                System.err.println("Creating user: " + i);
            }
            createUser(givenName + i, familyName + i).save();
        }

        UserCollection allUsers = User.retrieveAll();
        int ctr = 0;
        while (allUsers.next()) {
            ctr++;
            if ( ctr % NUMBER_TO_REPORT == 0 ) {
                System.err.println("Updating user: " + ctr);
            }
            User user = allUsers.getUser();
            String oldEmail = user.getPrimaryEmail().toString();
            user.setPrimaryEmail( new EmailAddress( oldEmail + "-" + ctr ) );
            user.setScreenName(user.getScreenName() + "-" + ctr);
            user.save();
        }

    }

    public static Test suite() {
        return new TestSuite(UserMassTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
