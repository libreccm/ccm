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

import com.arsdigita.tools.junit.extensions.BaseTestSetup;
import com.arsdigita.tools.junit.extensions.CoreTestSetup;
import java.math.BigInteger;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * PartyTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #14 $ $Date: 2004/08/16 $
 */

abstract public class PartyTest extends PersistenceTestCase {

    public final static String versionId = "$Id: PartyTest.java 741 2005-09-02 10:21:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static Logger s_log =
        Logger.getLogger(PartyTest.class.getName());

    public PartyTest(String name) {
        super(name);
    }

    abstract String getModelName();

    public void testUserCRUD() {
        DataObject user = getSession().create(getModelName() + ".User");
        user.set("id", BigInteger.ZERO);
        user.set("email", "rhs@mit.edu");
        user.set("firstName", "Rafael");
        user.set("lastNames", "H. Schloming");
        user.save();

        user = getSession().retrieve(new OID(getModelName() + ".User", BigInteger.ZERO));

        if (user == null)
            fail("User was not saved or retrieved properly.");

        assertEquals("User id was not saved or retrieved properly.",
                     BigInteger.ZERO,
                     user.get("id"));
        assertEquals("User email was not saved or retrieved properly.",
                     "rhs@mit.edu",
                     user.get("email"));
        assertEquals("User firstName was not saved or retrieved properly.",
                     "Rafael",
                     user.get("firstName"));
        assertEquals("User lastNames was not saved or retrieved properly.",
                     "H. Schloming",
                     user.get("lastNames"));

        user.set("email", "rhs@arsdigita.com");
        user.save();
        s_log.info(user.toString());
        user = getSession().retrieve(new OID(getModelName() + ".User", BigInteger.ZERO));

        if (user == null)
            fail("User was not retrieved properly after update.");

        assertEquals("User email was not updated properly.",
                     "rhs@arsdigita.com",
                     user.get("email"));

        user.delete();

        user = getSession().retrieve(new OID(getModelName() + ".User", BigInteger.ZERO));
        if (user != null)
            fail("User was not deleted properly.");
    }

    public void testGroupCRUD() {
        DataObject group = getSession().create(getModelName() + ".Group");
        group.set("id", BigInteger.ZERO);
        group.set("email", "sipb@mit.edu");
        group.set("name", "SIPB");
        group.save();

        group = getSession().retrieve(new OID(getModelName() + ".Group", BigInteger.ZERO));

        if (group == null)
            fail("Group was not saved or retrieved properly.");

        assertEquals("Group id was not saved or retrieved properly.",
                     BigInteger.ZERO,
                     group.get("id"));
        assertEquals("Group email was not saved or retrieved properly.",
                     "sipb@mit.edu",
                     group.get("email"));
        assertEquals("Group name was not saved or retrieved properly.",
                     "SIPB",
                     group.get("name"));

        group.set("email", "lusers@mit.edu");
        group.save();

        group = getSession().retrieve(new OID(getModelName() + ".Group", BigInteger.ZERO));

        if (group == null)
            fail("Group was not retrieved properly after update.");

        assertEquals("Group email was not updated properly.",
                     "lusers@mit.edu",
                     group.get("email"));

        group.delete();

        group = getSession().retrieve(new OID(getModelName() + ".Group", BigInteger.ZERO));
        if (group != null)
            fail("Group was not deleted properly.");
    }

    public void testMembershipAssociation() {
        DataObject user = getSession().create(getModelName() + ".User");
        user.set("id", BigInteger.ZERO);
        user.set("email", "rhs@mit.edu");
        user.set("firstName", "Rafael");
        user.set("lastNames", "H. Schloming");
        user.save();

        DataObject group = getSession().create(getModelName() + ".Group");
        group.set("id", BigInteger.ONE);
        group.set("email", "test@asdf.com");
        group.set("name", "Test Group");

        DataAssociation members =
            (DataAssociation) group.get("members");
        members.add(user);
        assertTrue("Members was just modified!", members.isModified() );
        group.save();

        group = getSession().retrieve(new OID(getModelName() + ".Group", BigInteger.ONE));
        members = (DataAssociation) group.get("members");
        assertTrue("Members was not modified!", members.isModified() == false );

        if (!members.next())
            fail("Data association should contain at least one row.");

        assertEquals("User email incorrect in data association.",
                     "rhs@mit.edu",
                     members.get("email"));
        assertEquals("User firstName incorrect in data association.",
                     "Rafael",
                     members.get("firstName"));
        assertEquals("User lastNames incorrect in data association.",
                     "H. Schloming",
                     members.get("lastNames"));


        if (members.next())
            fail("Data association should contain at most one row.");


        DataAssociation groups = (DataAssociation) user.get("groups");

        if (!groups.next())
            fail("Data association should contain at least one row.");

        assertEquals("Group email incorrect in data association.",
                     "test@asdf.com",
                     groups.get("email"));
        assertEquals("Group name incorrect in data association.",
                     "Test Group",
                     groups.get("name"));

        if (groups.next())
            fail("Data association should contain at most one row.");

        BigInteger userId2 = new BigInteger("2");
        DataObject user2 = makeBasicUser( userId2,
                                          "jorris@arsdigita.com",
                                          "Jon",
                                          "orris");
        members.add(user2);

        DataObject user3 = makeBasicUser( new BigInteger("3"),
                                          "foo@bar.com",
                                          "Foo",
                                          "Bar");
        members.add(user3);
        group.save();
        members = (DataAssociation) group.get("members");

        members.remove(user2);
        group.save();
        while(members.next())
            {
                assertTrue("User2 should have been removed!", userId2.equals(members.get("id")) == false );
            }

    }

    public void testLazyAttributes() {
        DataObject user = getSession().create(getModelName() + ".User");
        user.set("id", BigInteger.ZERO);
        user.set("email", "rhs@mit.edu");
        user.set("firstName", "Rafael");
        user.set("lastNames", "H. Schloming");
        user.set("bio", "This is a test.");
        user.save();

        user = getSession().retrieve(new OID(getModelName() + ".User", BigInteger.ZERO));
        assertEquals("On demand retrieve failed.",
                     "This is a test.",
                     user.get("bio"));
    }

    public void testNotFound() {
        DataObject user = makeBasicUser(new BigInteger("0"), "asdf@asfd.com",
                                        "Asdf", "Fdsa");
        OID oid = new OID(getModelName() + ".Group", new BigInteger("0"));
        DataObject group = getSession().retrieve(oid);
        assertEquals("Group was retrieved even though it doesn't exist.",
                     null, group);
    }

    private static final int NUM_MEMBERS = 10;

    public void testCascadedDeletes() {
        DataObject group = getSession().create(getModelName() + ".Group");
        group.set("id", new BigInteger("1"));
        group.set("email", "test@group.com");
        group.set("name", "Test Group");

        DataAssociation members = (DataAssociation) group.get("members");

        for (int i = 2; i < NUM_MEMBERS + 2; i++) {
            DataObject user = getSession().create(getModelName() + ".User");
            user.set("id", new BigInteger(Integer.toString(i)));
            user.set("email", "user" + i + "@foo.com");
            user.set("firstName", "Joe");
            user.set("lastNames", "User " + i);
            user.save();
            members.add(user);
        }

        group.save();

        assertEquals("members not saved properly", 10, members.size());

        OID oid = new OID(getModelName() + ".Group", new BigInteger("1"));

        /*
        if (com.arsdigita.db.DbHelper.getDatabase
            (getSession().getConnection()) !=
            com.arsdigita.db.DbHelper.DB_POSTGRES && 
            !"mdsql".equals(getModelName())) {
            group = getSession().retrieve(oid);
            try {
                group.delete();
                fail("group was successfully deleted when it should " +
                     "have errored out with constraint failures");
            } catch (PersistenceException e) {
                // Do nothing.
            }
        }
        */
        group = getSession().retrieve(oid);
        members = (DataAssociation) group.get("members");
        DataAssociationCursor cursor = members.cursor();
        cursor = members.cursor();

        while (cursor.next()) {
            cursor.remove();
        }

        group.delete();

        assertEquals("group was not properly deleted",
                     null,
                     getSession().retrieve(oid));
    }

    public void testSaveComposites() {
        Session ssn = getSession();
        DataObject user = ssn.create(getModelName() + ".User");
        user.set("id", new BigInteger("1"));
        user.set("email", "rhs@mit.edu");
        user.set("firstName", "Rafael");
        user.set("lastNames", " H. Schloming");

        DataObject color = ssn.create(getModelName() + ".Color");
        color.set("id", new BigInteger("1"));
        color.set("name", "red");

        user.set("favorateColor", color);
        user.save();

        OID oid = new OID(getModelName() + ".User", new BigInteger("1"));
        user = ssn.retrieve(oid);
        color = (DataObject) user.get("favorateColor");
        assertEquals("Color not saved properly", "red", color.get("name"));

        color.set("name", "green");
        user.save();

        user = ssn.retrieve(oid);
        color = (DataObject) user.get("favorateColor");
        assertEquals("Save not cascaded properly",
                     "green",
                     color.get("name"));
    }

    private DataObject makeBasicUser(BigInteger id,
                                     String email,
                                     String firstName,
                                     String lastNames)
    {
        DataObject user = getSession().create(getModelName() + ".User");
        user.set("id", id);
        user.set("email", email);
        user.set("firstName", firstName);
        user.set("lastNames", lastNames);
        user.save();

        return user;

    }

    public static void main(String args[]) {
        TestSuite suite = new TestSuite(PartyTest.class);
        BaseTestSetup wrapper = new CoreTestSetup(suite);
        junit.textui.TestRunner.run( wrapper );

    }

}
