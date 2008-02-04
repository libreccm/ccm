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
package com.arsdigita.persistence;

import com.arsdigita.persistence.metadata.ObjectType;
import java.math.BigDecimal;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * OIDTest
 * <p> This class performs unit tests on com.arsdigita.persistence.OID </p>
 *
 *
 * @author Michael Bryzek
 * @date $Date: 2004/08/16 $
 * @version $Revision: #13 $
 *
 * @see com.arsdigita.persistence.OID
 **/

public class OIDTest extends TestCase {

    public final static String versionId = "$Id: OIDTest.java 745 2005-09-02 10:50:34Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static Logger s_log =
        Logger.getLogger(OIDTest.class.getName());

    private OID oid;
    private static final String TYPE = "mdsql.Node";
    private static final int ID = 42;

    /**
     * Constructor (needed for JTest)
     * @param name    Name of Object
     **/
    public OIDTest(String name) {
        super(name);
    }

    /**
     * Used by JUnit (called before each test method)
     **/
    protected void setUp() {
        oid = new OID(TYPE, ID);
    }

    /**
     * Used by JUnit (called after each test method)
     **/
    protected void tearDown() {
        oid = null;
    }


    public void testCreate() {
        // try string id
        OID oid = new OID(TYPE, ID);
        // try a bad id string
        try {
            oid = new OID("foobar!", ID);
            fail("Should not create an OID with invalid type name!");
        }
        catch(PersistenceException e) {

        }
        // Use an object type.
        ObjectType type =
            SessionManager.getMetadataRoot().getObjectType(TYPE);
        oid = new OID( type, ID );

        oid = new OID(type);

        oid = new OID(TYPE);
        try {
            oid = new OID("com.arsdigita.notification.QueueItem", ID);
            fail("Shouldn't be able to use a compound key!");
        }
        catch(PersistenceException e) {

        }


    }
    /**
     * This test makes sure we can serialize and deserialize OID's
     **/
    public void testSerialization() throws Exception {
        String serial = oid.toString();
        s_log.info("OID Serial: " + serial);
        OID parsed = OID.valueOf(serial);
        assertEquals("Parsing failed", oid.toString(), parsed.toString());
        assertEquals("Parsing or equals() failed", oid, parsed);

        parseInvalidOID("comlete nonsense");

        // now we test with multiple integer keys
        OID multiKey = new OID
            ("com.arsdigita.notification.QueueItem");
        multiKey.set("requestID", new BigDecimal(3));
        multiKey.set("partyTo", new BigDecimal(8));
        parsed = OID.valueOf(multiKey.toString());

        assertTrue("Parsing of OID with multiple big decimal keys failed",
               multiKey.equals(parsed));

        // now we test with multiple keys of multiple types
        OID multiKeyMulti = new OID("com.arsdigita.kernel.permissions.Permission");
        multiKeyMulti.set("objectId", new BigDecimal(4));
        multiKeyMulti.set("partyId", new BigDecimal(4));
        multiKeyMulti.set("privilege", "my privilege");
        parsed = OID.valueOf(multiKeyMulti.toString());
        assertTrue("Parsing of OID with multiple keys failed",
               multiKeyMulti.equals(parsed));
    }


    public void testArePropertiesNull() {
        assertTrue(oid.arePropertiesNull() == false);
        OID oid2 = new OID(TYPE);
        assertTrue(oid2.arePropertiesNull());
        oid2.set("id", new BigDecimal(12));
        assertTrue(oid2.arePropertiesNull() == false);
    }

    public void testSetTypeValidation() {
        oid = new OID(TYPE);
        try {
            oid.set("id", new Integer(12));
            fail("Initializing BigDecimal field w/ " +
                 "Integer should have thrown error");
        } catch (PersistenceException e) {
            // ignore
        }

        try {
            oid.set("id", "12");
            fail("Initializing BigDecimal field w/ " +
                 "String should have thrown error");
        } catch (PersistenceException e) {
            // ignore
        }
    }

    public void testSetCompoundTypeValidation() {
        oid = new OID("mdsql.linkTest.ArticleImageLink");

        try {
            oid.set("article", new Integer(12));
            fail("Initializing Article field w/ " +
                 "Integer should have thrown error");
        } catch (PersistenceException e) {
            // ignore
        }

        try {
            oid.set("article", "12");
            fail("Initializing Article field w/ " +
                 "String should have thrown error");
        } catch (PersistenceException e) {
            // ignore
        }

        DataObject article = SessionManager.getSession().create
            ("mdsql.linkTest.Article");
        try {
            oid.set("image", article);
            fail("Initializing Image field w/ " +
                 "Article should have thrown error");
        } catch (PersistenceException e) {
            // ignore
        }

        // should work
        oid.set("article", article);
    }


    /* Utility to contain tests for parsing invalid OID
     *
     *
     */
    private void parseInvalidOID(String oidValue) {
        try  {
            // missing :
            OID.valueOf(oidValue);
            fail("Parsed : " + oidValue);
        }
        catch(IllegalArgumentException e) {

        }

    }


    /**
     * Test OID.equals()
     **/
    public void testEquals() {
        OID oid2 = new OID(TYPE, ID);
        assertEquals("Equality test failed", oid, oid2);
        assertEquals("Hash equality failed!", oid.hashCode(), oid2.hashCode());

        oid2 = new OID(TYPE, 100);
        assertTrue("equals() returned true", !oid.equals(oid2));
        assertTrue("Hash inequality failed!", oid.hashCode() != oid2.hashCode());
    }


    /**
     * Main method needed to make a self runnable class
     *
     * @param args This is required for main method
     **/
    public static void main(String[] args) {

        junit.textui.TestRunner.run( new TestSuite(OIDTest.class) );
    }
}
