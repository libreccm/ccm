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

import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigDecimal;
import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * Tests basic functionality of kernel classes
 *
 *
 * @author Phong Nguyen
 * @version 1.0
 * @see com.arsdigita.kernel
 */


public class PartiesBasicTest extends BaseTestCase {

    public static final String versionId = "$Id: PartiesBasicTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Session m_ssn;

    /**
     * Constructs a PartiesBasicTest with the specified name.
     *
     * @param name Test case name.
     **/
    public PartiesBasicTest( String name ) {
        super( name );
    }

    public void setUp()  {
        m_ssn = SessionManager.getSession();
    }

    public void tearDown() {
    }


    /**
     * Tests database retrieval
     **/
    public void testDBRetrieval() throws Exception {

        // create a new user
        User user = new User();
        BigDecimal idval=user.getID();
        String screenName;

        // Set user properties.
        screenName = "screen name " + idval.toString();
        user.setScreenName(screenName);
        user.getPersonName().setGivenName("Billy");
        user.getPersonName().setFamilyName("Kid");
        String email = "billy(" + idval + ")@arsdigita.com";
        user.setPrimaryEmail(new EmailAddress(email));
        user.save();

        // retrieve the user
        OID oid = new OID(User.BASE_DATA_OBJECT_TYPE, idval);
        user = new User(oid);
        assertEquals("The screenName in the User object is not correct.",
                     screenName, user.getScreenName());
        assertEquals("The primary email in the User object is not correct.",
                     email, user.getPrimaryEmail().getEmailAddress());
    }

    /**
     * Tests database retrieval
     **/
    public void testUserEmail() throws Exception {

        User user = new User();
        BigDecimal idval=user.getID();

        // Set user properties.
        user.setScreenName("screen name " + idval.toString());
        user.getPersonName().setGivenName("Billy");
        user.getPersonName().setFamilyName("Kid");

        user.setPrimaryEmail(new EmailAddress("TESTbilly@arsdigita.com" + idval.toString()));
        user.save();

        try {
            user.setPrimaryEmail(null);
            fail("user's primary email was set to null");
        } catch (Exception e) {
            // exception should happen. Continue.
        }

        Group group = new Group();
        group.setName("test");
        group.setPrimaryEmail(new EmailAddress("TESTgroup@arsdigita.com"));
        group.save();
        group.setPrimaryEmail(null);
        assertTrue(group.getPrimaryEmail()==null);
        group.save();
        assertTrue(group.getPrimaryEmail()==null);

        // re-retrieve the group and test primary email again.
        group = new Group(group.getID());
        assertNull("primary email should be null, but was " + group.getPrimaryEmail(), group.getPrimaryEmail());

        User user2 = new User();
        idval = user2.getID();
        user2.setScreenName("screen name " + idval.toString());
        user2.getPersonName().setGivenName("Billy");
        user2.getPersonName().setFamilyName("Kid");
        user2.setPrimaryEmail(new EmailAddress(user.getPrimaryEmail().getEmailAddress().toUpperCase()));

        try {
            user2.save();
            fail("non-unique email created for user");
        } catch (Exception e) {
            // exception should happen.  Continue.
        }

        // try same as above (user2), but without setting id explicitly.
        // Tests SDM 161014
        User user4 = new User();
        user4.getPersonName().setGivenName("Billy2");
        user4.getPersonName().setFamilyName("Kid2");
        user4.setPrimaryEmail(new EmailAddress(user.getPrimaryEmail().getEmailAddress().toUpperCase()));

        try {
            user4.save();
            fail("non-unique email created for user");
        } catch (Exception e) {
            // exception should happen.  Continue.
        }

        User user3 = new User();
        idval=user3.getID();

        // Set user properties.
        user3.setScreenName("screen name " + idval.toString());
        user3.getPersonName().setGivenName("Billy");
        user3.getPersonName().setFamilyName("Kid");
        try {
            user3.save();
            fail("User.save() succeeded with null email");
        } catch (Exception e) {
            // exception should happen because email wasn't set.
        }


    }

    public void testFactory() throws Exception {
        User user = new User();
        BigDecimal idval=user.getID();

        // Set user properties.
        String screenName = "screen name " + idval.toString();
        user.setScreenName(screenName);
        user.getPersonName().setGivenName("Billy");
        user.getPersonName().setFamilyName("Kid");

        user.setPrimaryEmail(new EmailAddress("TESTbilly@arsdigita.com" + idval.toString()));
        user.save();

        // retrieve the user as a party
        OID oid = new OID(Party.BASE_DATA_OBJECT_TYPE, idval);
        DataObject partyData = m_ssn.retrieve(oid);

        Party party = (Party) DomainObjectFactory.newInstance(partyData);

        assertTrue("Wrong class instantiated",
                   party instanceof User);

        assertEquals("Party data object was not specialized to User",
                     partyData.getObjectType().getQualifiedName(),
                     User.BASE_DATA_OBJECT_TYPE);

        assertEquals("Wrong screen name",
                     screenName, ((User)party).getScreenName());

        party = (Party) DomainObjectFactory.newInstance(oid);
        assertTrue("Wrong class instantiated",
                   party instanceof User);

        assertEquals("Party data object was not specialized to User",
                     partyData.getObjectType().getQualifiedName(),
                     User.BASE_DATA_OBJECT_TYPE);

    }

    public static Test suite() {
        //
        // Reflection is used here to add all
        // the testXXX() methods to the suite.
        //
        return new TestSuite(PartiesBasicTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
