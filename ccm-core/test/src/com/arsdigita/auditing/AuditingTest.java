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
package com.arsdigita.auditing;

import com.arsdigita.db.Sequences;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests basic functionality of auditing DomainObject
 * Note that this test uses it in a fairly trivial way,
 * by simply having an Auditing object use an external
 * User object.  This is unlikely to be the typical
 * usage.
 *
 *
 * @author Phong Nguyen
 * @version 1.0
 * @see com.arsdigita.kernel
 */


public class AuditingTest extends BaseTestCase {
    public static final String versionId = "$Id $";

    private TransactionContext m_txn;

    private User m_test_user1;
    private User m_test_user2;
    //This represents the "current_user" that a session would get
    private User m_current_user;

    private Date m_current_date = new Date();
    private String m_current_ip = "127.0.0.1";

    /**
     * Constructs a AuditingTest with the specified name.
     *
     * @param name Test case name.
     **/
    public AuditingTest( String name ) {
        super( name );
    }

    public void setUp() {
        try {
            setUpUsers();
            AuditingSaveFactory.setPrototype(new TestAuditingSaveInfo());
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    private void setUpUsers() throws SQLException {
        m_test_user1 = newUser();
        m_test_user2 = newUser();
        m_current_user = m_test_user1;
    }

    private void tearDownUsers() throws SQLException {
        //          m_test_user1.delete();
        //          m_test_user2.delete();
    }


    private User newUser() throws SQLException {
        User user = new User();
        BigDecimal idval = Sequences.getNextValue();
        user.setScreenName("screen name " + idval);
        user.getPersonName().setGivenName("Joseph");
        user.getPersonName().setFamilyName("Bank");
        user.setPrimaryEmail(new EmailAddress("jbank(" + idval + ")@arsdigita.com"));
        user.save();
        return user;
    }

    private void checkNotNull(Audited audited) {
        //The audit info shouldn't be null
        assertNotNull("CreationDate null", audited.getCreationDate());
        assertNotNull("LastModifiedDate null", audited.getLastModifiedDate());
        assertNotNull("CreationUser null", audited.getCreationUser());
        assertNotNull("LastModifiedUser null", audited.getLastModifiedUser());
        assertNotNull("CreationIP null", audited.getCreationIP());
        assertNotNull("LastModifiedIP null", audited.getLastModifiedIP());
    }

    /* Checks for creation info of an audited object */
    private void checkCreateEqualsModified(Audited audited) {
        User creation_user = audited.getCreationUser();
        User mod_user = audited.getLastModifiedUser();
        assertEquals(audited.getCreationDate().toString(),
                     audited.getLastModifiedDate().toString());
        assertEquals(audited.getCreationIP(), audited.getLastModifiedIP());

        assertEquals(creation_user == null ? null : creation_user.getID(),
                     mod_user == null ? null : mod_user.getID());
    }

    private void checkCreateInfo(Audited audited) {
        //The audit info shouldn't be null
        checkNotNull(audited);
        //Creation info should be modification info
        checkCreateEqualsModified(audited);
    }


    private void assertNotEquals(Object o1, Object o2) {
        assertTrue(o1 + " not equals " + o2, !(o1.equals(o2)));
    }

    private void checkModifiedInfo(Audited audited) {
        //The audit info shouldn't be null
        checkNotNull(audited);
        //Creation info should be modification info
        assertNotEquals(audited.getCreationDate().toString(),
                        audited.getLastModifiedDate().toString());
        assertNotEquals(audited.getCreationIP(), audited.getLastModifiedIP());
        assertNotEquals(audited.getCreationUser().getID(),
                        audited.getLastModifiedUser().getID());
    }


    /**
     * Tests creation
     **/
    public void testCreate() throws Exception {
        User user = m_current_user;
        BigDecimal idval=user.getID();

        BasicAuditTrail auditing = new BasicAuditTrail();
        AuditingSaveInfo sinfo = AuditingSaveFactory.newInstance();
        auditing.setID(idval);
        auditing.setCreationInfo(sinfo);
        auditing.save();
        try {
            checkCreateInfo(auditing);
        } finally {
            auditing.delete();
        }
    }


    /**
     * Tests retrieval
     **/
    public void testRetrieve() throws Exception {
        User user = m_current_user;
        BigDecimal idval=user.getID();

        BasicAuditTrail auditing = new BasicAuditTrail();
        AuditingSaveInfo sinfo = AuditingSaveFactory.newInstance();
        auditing.setID(idval);
        auditing.setCreationInfo(sinfo);
        auditing.save();

        //The audit info shouldn't be null
        BasicAuditTrail audit2 = BasicAuditTrail.retrieveForACSObject(user);
        try {
            assertEquals(auditing.getOID().toString(), audit2.getOID().toString());
            checkCreateInfo(audit2);
        } finally {
            auditing.delete();
        }
    }

    private void setModifiedInfo() {
        //This changes the save info
        m_current_user = m_test_user2;
        m_current_date = new Date();
        m_current_date.setYear(m_current_date.getYear()+1);
        m_current_ip = "127.0.0.2";
    }

    public void testModified() throws Exception {
        User user = m_current_user;
        BigDecimal idval=user.getID();

        BasicAuditTrail auditing = new BasicAuditTrail();
        AuditingSaveInfo sinfo = AuditingSaveFactory.newInstance();
        auditing.setID(idval);
        auditing.setCreationInfo(sinfo);
        auditing.save();
        try {
            checkCreateInfo(auditing);

            setModifiedInfo();
            sinfo = AuditingSaveFactory.newInstance();

            auditing.setLastModifiedInfo(sinfo);
            auditing.save();
            checkModifiedInfo(auditing);
        } finally {
            auditing.delete();
        }
    }

    //same as above, but retrieve back the saved info
    //and check
    public void testModifiedRetrieve() throws Exception {
        User user = m_current_user;
        BigDecimal idval=user.getID();

        BasicAuditTrail auditing = new BasicAuditTrail();
        AuditingSaveInfo sinfo = AuditingSaveFactory.newInstance();
        auditing.setID(idval);
        auditing.setCreationInfo(sinfo);
        auditing.save();
        try {
            checkCreateInfo(auditing);

            setModifiedInfo();
            sinfo = AuditingSaveFactory.newInstance();

            auditing.setLastModifiedInfo(sinfo);
            auditing.save();
            checkModifiedInfo(auditing);

            BasicAuditTrail auditing2 = BasicAuditTrail.retrieveForACSObject(user);
            checkModifiedInfo(auditing2);

        } finally {
            auditing.delete();
        }
    }


    /**
     * Tests AuditedACSObject subclasses
     **/
    public void testAuditedACSObjectCreate() throws Exception {
        AuditedACSObject obj = new DummyAuditedACSObject();
        obj.save();
        try {
            checkCreateInfo(obj);
        } finally {
            obj.delete();
        }
    }

    /**
     * Tests AuditedACSObject subclasses retrieve
     **/
    public void testAuditedACSObjectRetrieve() throws Exception {
        AuditedACSObject obj = new DummyAuditedACSObject();
        obj.save();

        AuditedACSObject obj2 = new DummyAuditedACSObject(obj.getOID());

        try {
            checkCreateInfo(obj2);
        } finally {
            obj.delete();
        }
    }

    /**
     * Tests AuditedACSObject subclasses retrieve
     **/
    public void testAuditedACSObjectModify() throws Exception {
        AuditedACSObject obj = new DummyAuditedACSObject();
        obj.save();
        setModifiedInfo();
        obj.save();

        try {
            checkModifiedInfo(obj);
        } finally {
            obj.delete();
        }
    }

    //same as above, but retrieve back the saved info
    //and check
    public void testAuditedACSObjectModifyRetrieve() throws Exception {
        AuditedACSObject obj = new DummyAuditedACSObject();
        obj.save();
        setModifiedInfo();
        obj.save();

        try {
            checkModifiedInfo(obj);
            AuditedACSObject obj2 = new DummyAuditedACSObject(obj.getOID());
            checkModifiedInfo(obj2);
        } finally {
            obj.delete();
        }
    }


    /**
     * Tests whether null works for user info, etc
     **/
    public void testAuditedACSObjectNull() throws Exception {
        m_current_user = null;
        m_current_ip = null;
        AuditedACSObject obj = new DummyAuditedACSObject();
        obj.save();
        try {
            checkCreateEqualsModified(obj);
        } finally {
            obj.delete();
        }
    }


    public static Test suite() {
        //
        // Reflection is used here to add all
        // the testBLAH() methods to the suite.
        //
        return new TestSuite(AuditingTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }


    //Dummy auditing info for test since we can't grab audit info
    //from the Web session
    private class TestAuditingSaveInfo implements AuditingSaveInfo {
        public User getSaveUser() { return m_current_user; }
        public Date getSaveDate() { return m_current_date; }
        public String getSaveIP() { return m_current_ip; }
        public AuditingSaveInfo newInstance() {
            return new TestAuditingSaveInfo();
        }
    }


    //Dummy AuditedObject, just for testing
    private class DummyAuditedACSObject extends AuditedACSObject {
        public DummyAuditedACSObject() throws DataObjectNotFoundException {
            super("com.arsdigita.kernel.ACSObject");
        }

        public DummyAuditedACSObject(OID oid) throws DataObjectNotFoundException{
            super(oid);
        }
    }
}
