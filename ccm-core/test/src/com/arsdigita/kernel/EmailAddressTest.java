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
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Group;
import com.arsdigita.tools.junit.framework.BaseTestCase;
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


public class EmailAddressTest extends BaseTestCase {


    private static final String m_baseDataObjectType =
        "com.arsdigita.kernel.EmailAddress";

    private static final String m_badBaseDataObjectType =
        "com.arsdigita.kernel.Group";

    // Creates a s_logging category with name = to the full name of
    // the EmailAddressTest class.
    private static Logger s_log = Logger.getLogger( EmailAddressTest.class.getName() );

    /**
     * Constructs a EmailAddressTest with the specified name.
     *
     * @param name Test case name.
     **/
    public EmailAddressTest( String name ) {
        super( name );
    }

    public void testEmailViaPartyConstructorAndPersistence() {
        s_log.debug( "Testing creation of email via retrieval from party." );
        Group g = new Group();

        EmailAddress e =  new EmailAddress( "taco@lovemonkey.org" );
        g.setName( "Jumpin' G and the Taco Posse" );
        g.setPrimaryEmail( e );

        g.save();

        Group g_verify = null;
        try {
            g_verify = new Group( g.getOID() );
        } catch ( DataObjectNotFoundException err ) {
            fail( "getOID failure on a getOID" );
        }
        assertNotNull("Group retrieved by OID was null", g_verify);
        assertEquals(e, g_verify.getPrimaryEmail());
    }

    public void testGetSetEmailAddress() {
        s_log.debug( "Testing get and set'ers for EmailAddress" );

        String e_old = new String();
        String e_new = new String();
        try {
            e_old = "butternut(" + Sequences.getNextValue() + "@squash.org";
            e_new = "tristan(" + Sequences.getNextValue() +")@arsdigita.com";
        } catch ( Exception e ) {
            fail( "Database error" );
        }

        EmailAddress email = new EmailAddress( e_old );

        assertEquals( "Email address returned does not equal what it was set to.",
                      email.getEmailAddress(), e_old );

        //          email.setEmailAddress( e_new );
        //          assertEquals( email.getEmailAddress(), e_new );

        //          try {
        //              email.setEmailAddress( e_old );
        //              fail( "Successfully setEmailAddress to a null reference." );
        //          } catch ( Exception e ) {}

        //          s_log.debug( "Finished testing get and set'ers for EmailAddress" );
    }

    public void testIsBouncing() {
        s_log.debug( "Testing get and set'ers for bouncing for EmailAddress" );

        EmailAddress email = new EmailAddress( _getJunkAddress() );

        assertTrue( !email.isBouncing() );

        email.setIsBouncing( true );
        assertTrue( email.isBouncing() );

        email.setIsBouncing( false );
        assertTrue( !email.isBouncing() );
    }

    public void testIsVerified() {
        s_log.debug( "Testing get and set'ers for Verified for EmailAddress" );

        EmailAddress email = new EmailAddress( _getJunkAddress() );

        if ( true ) { return; }

        // should default to false (I think)
        assertTrue( !email.isVerified() );

        email.setIsVerified( false );
        assertTrue( !email.isVerified() );

        email.setIsVerified( true );
        assertTrue( email.isVerified() );
    }

    public void testToString() {
        s_log.debug( "Testing toString for EmailAddress" );

        EmailAddress email = new EmailAddress( _getJunkAddress() );
        // WARNING: Lame test.
        assertEquals( email.toString(), email.getEmailAddress() );
    }

    public String _getJunkAddress() {
        String email = new String();

        try {
            email = "squash(" + Sequences.getNextValue() + ")@it.org";
        } catch ( Exception e ) {
            fail( "Database Error" );
        }

        return email;

    }



    //      /**
    //       * Tests database retrieval
    //       **/
    //      public void testDBRetrieval() throws Exception {

    //      User user;
    //      BigDecimal idval=null;

    //          // create a new user
    //          s_log.debug("Creating user");
    //          user = new User();
    //          idval = Sequences.getNextValue();
    //          user.getDataObject().set("id", idval);
    //          user.setScreenName("screen name " + idval.toString());
    //          user.getPersonName().setGivenName("Oumi");
    //          user.getPersonName().setFamilyName("Mehrotra");
    //          user.setPrimaryEmail(new EmailAddress());
    //          user.getPrimaryEmail().setId(Sequences.getNextValue());
    //          user.getPrimaryEmail().setEmailAddress("oumi(" +
    //                                                 idval +
    //                                                 ")@arsdigita.com");
    //          user.save();
    //          try {
    //      } catch (Exception e) {
    //          fail(e.getMessage());
    //      }

    //      // retrieve the user
    //      log.debug("Retrieving user");
    //      OID oid = new OID("com.arsdigita.kernel.User", idval);
    //      user = new User(oid);

    //      String screenName = user.getScreenName();
    //      log.debug("The screen name is: " + screenName);

    //      }

    public static Test suite() {
        //
        // Reflection is used here to add all
        // the testXXX() methods to the suite.
        //
        return new TestSuite(EmailAddressTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }
}
