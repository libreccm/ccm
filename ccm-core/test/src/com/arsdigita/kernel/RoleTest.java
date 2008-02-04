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
import com.arsdigita.persistence.Filter;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigDecimal;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Basic unit tests on Roles
 *
 *
 * @see com.arsdigita.kernel.Role
 *
 * @author Michael Bryzek
 * @version 1.0
 **/

public class RoleTest extends BaseTestCase {

    public static final String versionId = "$Id: RoleTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Group m_group;

    /**
     * Constructs a RoleTest with the specified name.
     *
     * @param name Test case name.
     **/
    public RoleTest( String name ) {
        super( name );
    }

    public void setUp() {
        m_group = GroupTest._createGroup("RoleTest Group");
        m_group.save();
    }

    public void tearDown() {
        m_group = null;
    }

    /**
     * Tests the (Group, String) constructor
     **/
    public void testConstructor() {
        Role role = createRole("testConstructor");
        role.save();
    }


    /**
     * Tests the (OID) constructor
     **/
    public void testConstructor_1() {
        String roleName = "testConstructor_1";
        Role role = createRole(roleName);
        role.save();
        try {
            role = new Role(role.getOID());
        } catch (DataObjectNotFoundException e) {
            e.printStackTrace();
            fail("Could not retrieve role for oid: " + role.getOID());
        }
        assertEquals("Role not retrieved by oid properly",
                     roleName, role.getName());
    }

    /**
     * Tests the (String) constructor
     **/
    public void testConstructor_2() {
        String roleName = "testConstructor_2";
        Role role = createRole(roleName);
        role.save();
        try {
            role = new Role(role.getID());
        } catch (DataObjectNotFoundException e) {
            e.printStackTrace();
            fail("Could not retrieve role for id: " + role.getID());
        }

        assertEquals("Role not retrieved by id properly",
                     roleName, role.getName());
    }


    /**
     * Makes sure we can add and remove members from roles.
     **/
    public void testAddingRemovingMembers() {
        User[] authors = new User[] { createUser("Author1"),
                                      createUser("Author2") };
        User[] editors = new User[] { createUser("Editor1"),
                                      createUser("Editor2") };

        Role author = createRole("Author");
        author.addMember(authors[0]);
        author.addMember(authors[1]);
        author.save();

        Role editor = createRole("Editor");
        editor.addMember(editors[0]);
        editor.addMember(editors[1]);
        editor.save();

        try {
            author = new Role(author.getOID());
            editor = new Role(editor.getOID());
        } catch (DataObjectNotFoundException e) {
            e.printStackTrace();
            fail("Could not retrieve author or editor role");
        }

        GroupTest._validateCollection(author.getMemberUsers(), authors);
        GroupTest._validateCollection(editor.getMemberUsers(), editors);

        author.removeMember(authors[0]);
        author.save();
        try {
            author = new Role(author.getOID());
        } catch (DataObjectNotFoundException e) {
            e.printStackTrace();
            fail("Could not retrieve author role");
        }
        GroupTest._validateCollection(author.getMemberUsers(),
                                      new User[] { authors[1] });

    }

    /**
     * There was a bug in setting name where roleGroup was not
     * initialized.
     **/
    public void testSettingNameOnly() {
        Role r = createRole("testSettingNameOnly");
        r.save();

        try {
            r = new Role(r.getID());
        } catch (DataObjectNotFoundException e) {
            fail("Could not retrieve role: " + r.getID());
        }
        try {
            r.setName("testSettingNameOnly - 2");
        } catch (Exception e) {
            fail("Could not set the role's name");
        }
        r.save();
    }

    /**
     * Tests deleting a role. Ensures members are all deleted as well.
     **/
    public void testDeletingRole() {
        Role r = createRole("testDeletingRole");
        r.addMember(createUser("testDeletingRole-user1"));
        r.save();

        BigDecimal implicitId = r.getGroup().getID();
        r.delete();

        GroupCollection all = Group.retrieveAll();
        Filter f = all.addFilter("id = :id");
        f.set("id", implicitId);
        assertEquals(0, all.size());
    }

    public static Test suite() {
        return new TestSuite(RoleTest.class);
    }


    /**
     * Creates the specified role for the specified group. Description
     * is automatically generated.
     *
     * @see #createRole(Group, String, String)
     **/
    public static Role createRole(Group group, String roleName) {
        return createRole(group, roleName, roleName + " description");
    }

    /**
     * Creates the specified role for the specified group
     **/
    public static Role createRole(Group group, String roleName, String description) {
        Role role = new Role(group, roleName);
        role.setDescription(description);
        return role;
    }


    /**
     * Creates the specified role for this instances group
     **/
    private Role createRole(String roleName) {
        return createRole(m_group, roleName);
    }

    /**
     * Creates a new user with a generated email address and family
     * name.  The specified name should have no spaces (because it
     * is used to generate the email address).
     **/
    public static User createUser(String name) {
        return createUser(name, name, "Generic family name");
    }

    /**
     * Creates a new user with the specified email and names.
     * Note that a unique identifier and "@arsdigita.com" are automatically
     * appended to the specified email.
     **/
    public static User createUser(final String email, final String givenName,
                                  final String familyName) {

        final User user = new User();
        KernelExcursion ke = new KernelExcursion() {
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                BigDecimal id;


                id = user.setID();

                user.setPrimaryEmail(new EmailAddress(email +
                                                      "(" + id + ")@arsdigita.com"));
                user.getPersonName().setGivenName( givenName );
                user.getPersonName().setFamilyName( familyName );
                user.save();

            }
        };

        ke.run();
        return user;
    }
}
