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
package com.arsdigita.kernel.permissions;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupTest;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.PartyCollection;
import com.arsdigita.kernel.RoleTest;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests basic functionality of the PermissionService class
 *
 *
 * @author Phong Nguyen
 * @version 1.0
 **/
public class PermissionTest extends BaseTestCase {

    public final static String versionId =
        "$Id: PermissionTest.java 744 2005-09-02 10:43:19Z sskracic $";

    private Session m_ssn;

    private ACSObject m_target;
    private User m_grantedUser;

    // a number grabbed from Sequences at the beginning of the test and
    // converted to string.  Used to ensure unique user names in case
    // someone runs this test repeatedly, committing the transaction
    // for each run (as I often do to debug test failures).
    private String m_uniqueNum;

    /**
     * Constructs a PermissionTest with the specified name.
     *
     * @param name Test case name.
     **/
    public PermissionTest( String name ) {
        super( name );
    }

    protected void runTest() throws Throwable {
        KernelExcursion ke = new KernelExcursion() {
            protected void excurse() {
                setEffectiveParty(Kernel.getSystemParty());
                try {
                    superRun();
                } catch(Throwable t) {
                    throw new UncheckedWrapperException(t);
                }
            }
        };

        ke.run();

    }

    protected void superRun() throws Throwable {
        super.runTest();
    }

    private static class GenericACSObject extends ACSObject {
        GenericACSObject() { super(); }
    }

    static ACSObject createObject() {
        ACSObject obj = new GenericACSObject();
        obj.save();
        return obj;
    }

    protected void setUp() {
        m_ssn = SessionManager.getSession();
        m_target = createObject();
        m_grantedUser = RoleTest.createUser("permissiontest");
    }


    public void testPrivilegeDescriptor() {
        assertTrue("PrivielgeDescriptor init error: missing read privilege",
               PrivilegeDescriptor.get(PrivilegeDescriptor.READ.getName())!=null);

        assertTrue("PrivielgeDescriptor init error: missing read privilege",
               PrivilegeDescriptor.get(PrivilegeDescriptor.WRITE.getName())!=null);

        assertTrue("PrivielgeDescriptor init error: missing read privilege",
               PrivilegeDescriptor.get(PrivilegeDescriptor.ADMIN.getName())!=null);

        assertTrue("PrivielgeDescriptor init error: missing read privilege",
               PrivilegeDescriptor.get(PrivilegeDescriptor.CREATE.getName())!=null);

        PrivilegeDescriptor.createPrivilege("somesillyprivilege");
        assertTrue (PrivilegeDescriptor.get("somesillyprivilege")!=null);

        DataObject dobj = m_ssn.retrieve(new OID ("com.arsdigita.kernel.permissions.Privilege", "somesillyprivilege"));
        assertTrue(dobj!=null);

        PrivilegeDescriptor.get("somesillyprivilege").deletePrivilege();
        assertTrue (PrivilegeDescriptor.get("somesillyprivilge")==null);

        dobj = m_ssn.retrieve(new OID ("com.arsdigita.kernel.permissions.Privilege", "somesillyprivilege"));
        assertTrue(dobj==null);
    }

    /**
     * Tests database retrieval
     **/
    public void testPermission() {

        PermissionDescriptor objPerm;

        PrivilegeDescriptor priv = PrivilegeDescriptor.READ;

        // test out permission services
        objPerm = new PermissionDescriptor(priv, m_target, m_grantedUser);

        assertTrue("The user \"" + m_grantedUser.getName() + "\" should not have " +
               "the \"" + priv.getName() + "\" on the user \"" +
               m_target.getDisplayName() + "\"",
               !PermissionService.checkPermission(objPerm));

        PermissionService.grantPermission(objPerm);

        assertTrue("The user \"" + m_grantedUser.getName() + "\" should have " +
               "the \"" + priv.getName() + "\" on the user \"" +
               m_target.getDisplayName() + "\"",
               PermissionService.checkPermission(objPerm));

        PermissionService.revokePermission(objPerm);

        assertTrue("The user \"" + m_grantedUser.getName() + "\" should no longer " +
               "have the \"" + priv.getName() + "\" on the user \"" +
               m_target.getDisplayName() + "\" since it was just revoked.",
               !PermissionService.checkPermission(objPerm));

    }

    public void testUniversalPermission() {
        // Grant universal write.
        PermissionDescriptor objPerm =
            new UniversalPermissionDescriptor(PrivilegeDescriptor.WRITE,
                                              m_grantedUser);
        PermissionService.grantPermission(objPerm);

        // Verify that granted user has universal write.
        objPerm =
            new UniversalPermissionDescriptor(PrivilegeDescriptor.WRITE,
                                              m_grantedUser.getOID());

        assertTrue("UniversalPermissionDescriptor not successfully granted or " +
               "checked ",
               PermissionService.checkPermission(objPerm));

        // Verify that the permission applies on an arbitrary
        // object.
        objPerm = new PermissionDescriptor(PrivilegeDescriptor.WRITE,
                                           m_target, m_grantedUser);
        assertTrue("Universal permission did not apply universally",
               PermissionService.checkPermission(objPerm));

        // Revoke the universal WRITE permission
        objPerm =
            new UniversalPermissionDescriptor(PrivilegeDescriptor.WRITE,
                                              m_grantedUser);
        PermissionService.revokePermission(objPerm);
        assertTrue("Universal write permission not successfully revoked",
               !PermissionService.checkDirectPermission(objPerm));

        // Now test admin privilege (see SDM #158010)
        objPerm =
            new UniversalPermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                              m_grantedUser);
        PermissionService.grantPermission(objPerm);

        // Verify read on arbitrary object, should be implied from previous
        // grant.
        objPerm = new PermissionDescriptor(PrivilegeDescriptor.READ,
                                           m_target, m_grantedUser);
        assertTrue("Universal permission did not apply universally",
               PermissionService.checkPermission(objPerm));

        // because user still has "admin", they should still have
        // "write" by implication
        assertTrue("Granted user should still have " +
               "implied universal write permission",
               PermissionService.checkPermission(objPerm));
        objPerm = new PermissionDescriptor(PrivilegeDescriptor.WRITE,
                                           m_target, m_grantedUser);
        assertTrue("Granted user should still have " +
               "implied write permission on target user",
               PermissionService.checkPermission(objPerm));

        // Second revoke the universal ADMIN permission
        objPerm =
            new UniversalPermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                              m_grantedUser);
        PermissionService.revokePermission(objPerm);
        assertTrue("Universal permission not successfully revoked",
               !PermissionService.checkPermission(objPerm));
        objPerm = new PermissionDescriptor(PrivilegeDescriptor.READ,
                                           m_target, m_grantedUser);
        assertTrue("Universal permission not successfully revoked",
               !PermissionService.checkPermission(objPerm));

    }

    public void testGetGrantedPermissions() {
        ObjectPermissionCollection univGrants = 
            PermissionService.getGrantedUniversalPermissions();
        // initSize is needed since getGrantedPermission takes in account universal 
        // permissions too
        long initSize = univGrants.size();
        
        PermissionDescriptor objPerm1 =
            new PermissionDescriptor(PrivilegeDescriptor.READ,
                                     m_target, m_grantedUser);
        PermissionService.grantPermission(objPerm1);

        PermissionDescriptor objPerm2 =
            new PermissionDescriptor(PrivilegeDescriptor.WRITE,
                                     m_target, m_grantedUser);
        PermissionService.grantPermission(objPerm2);

        ObjectPermissionCollection grants =
            PermissionService.getGrantedPermissions(m_target.getOID());

        assertEquals("Wrong number of grants retreived",
                     initSize + 2, grants.size());

        while(grants.next()) {
            if (grants.getGranteeID().compareTo(m_grantedUser.getID()) != 0) 
                continue;
            assertTrue("Grantee not retrieved as user " , grants.granteeIsUser());
            assertEquals("Incorrect grantee name",
                         grants.getGranteeName(), m_grantedUser.getName());
            assertEquals("Incorrect grantee email",
                         grants.getGranteeEmail(),
                         m_grantedUser.getPrimaryEmail());
            assertEquals("Incorrect grantee OID",
                         grants.getGranteeOID(), m_grantedUser.getOID());
            PrivilegeDescriptor priv = grants.getPrivilege();
            assertTrue("Incorrect privilege",
                       priv.equals(PrivilegeDescriptor.READ) ||
                       priv.equals(PrivilegeDescriptor.WRITE));
            assertTrue(!grants.isInherited());
        }

        PermissionService.revokePermission(objPerm1);
        PermissionService.revokePermission(objPerm2);
        grants=PermissionService.getGrantedPermissions(m_target.getOID());
        assertEquals("getGrantedPermissions() should be empty",
                     grants.size() - initSize, 0);
    }

    public void testFilterObjects() {
        User user1 = RoleTest.createUser("test1");
        User user2 = RoleTest.createUser("test2");
        User user3 = RoleTest.createUser("test3");

        PermissionDescriptor objPerm1 =
            new PermissionDescriptor(PrivilegeDescriptor.READ,
                                     user1, m_grantedUser);
        PermissionService.grantPermission(objPerm1);

        PermissionDescriptor objPerm2 =
            new PermissionDescriptor(PrivilegeDescriptor.READ,
                                     user2, m_grantedUser);
        PermissionService.grantPermission(objPerm2);

        Group g = new Group();
        g.setName("testFilterObjects Group");
        g.addMember(user1);
        g.addMember(user2);
        g.addMember(user3);
        g.save();

        PartyCollection members = g.getMembers();
        PermissionService.filterObjects(members,
                                        PrivilegeDescriptor.READ,
                                        m_grantedUser.getOID());

        assertEquals("Wrong number of objects in filtered collection",
                     members.size(), 2);
        while(members.next()) {
            assertTrue(members.getID().equals(user1.getID()) ||
                       members.getID().equals(user2.getID()));
        }

        // Revoke and retest
        PermissionService.revokePermission(objPerm1);
        PermissionService.revokePermission(objPerm2);

        PartyCollection users = g.getMembers();
        PermissionService.filterObjects(users,
                                        PrivilegeDescriptor.READ,
                                        m_grantedUser.getOID());

        assertEquals("filtered collection should be empty",
                     users.size(), 0);

    }

    /**
     * basic test that PermissionService.getContext/setContext work.
     **/
    public void testContext() throws Exception {

        User user = RoleTest.createUser("permcontext1");
        User contextUser = RoleTest.createUser("permcontext2");

        // test objectContext creation and retrieval
        PermissionService.setContext(user, contextUser);

        DataObject retrievedContext =
            PermissionService.getContext(user);

        assertEquals("The context object was not set correctly using " +
                     "setContext(ACSObject).", contextUser.getID(),
                     retrievedContext.getOID().get("id"));

        PermissionService.setContext(user, null);

        DataObject retrievedContext2 =
            PermissionService.getContext(user);
        assertTrue("The context was not successfully set to null or " +
               "retrieved as null",
               retrievedContext2 == null);

    }

    /**
     * test for cascading privileges through the context hierarchy
     **/
    public void testContextHierarchy() {
        
        // initSize is needed since getGrantedPermission takes in account universal 
        // permissions too
        long initSize = PermissionService.getGrantedUniversalPermissions().size();
        
        ACSObject child1 = createObject();
        ACSObject child2 = createObject();
        ACSObject gchild = createObject();

        PermissionService.setContext(m_target, null);
        PermissionService.setContext(child1, m_target);
        PermissionService.setContext(child2, m_target);
        PermissionService.setContext(gchild, child1);
        ObjectPermissionCollection grants;

        PermissionDescriptor objPerm1 = new PermissionDescriptor(
            PrivilegeDescriptor.READ, m_target, m_grantedUser);
        PermissionService.grantPermission(objPerm1);

        PermissionDescriptor objPerm2 = new PermissionDescriptor(
            PrivilegeDescriptor.WRITE, m_target, m_grantedUser);
        PermissionService.grantPermission(objPerm2);


        grants = PermissionService.getGrantedPermissions(m_target.getOID());
        assertEquals("Wrong number of grants", initSize + 2, grants.size());

        grants = PermissionService.getGrantedPermissions(child1.getOID());
        assertEquals("Wrong number of grants", initSize + 2, grants.size());

        grants = PermissionService.getGrantedPermissions(child2.getOID());
        assertEquals("Wrong number of grants", initSize + 2, grants.size());

        grants = PermissionService.getGrantedPermissions(gchild.getOID());
        assertEquals("Wrong number of grants", initSize + 2, grants.size());

        assertTrue("Should have privilege", PermissionService.checkPermission(
                       new PermissionDescriptor(
                           PrivilegeDescriptor.READ, child1, m_grantedUser)));

        PermissionService.setContext(child1, null);

        assertFalse("Shouldn't have privilege",
                    PermissionService.checkPermission(
                        new PermissionDescriptor(
                            PrivilegeDescriptor.READ, child1, m_grantedUser)));

        grants = PermissionService.getGrantedPermissions(child1.getOID());
        assertEquals("Wrong number of grants", initSize, grants.size());

        grants = PermissionService.getGrantedPermissions(gchild.getOID());
        assertEquals("Wrong number of grants", initSize, grants.size());

        PermissionDescriptor objPerm3 =
            new PermissionDescriptor(PrivilegeDescriptor.CREATE,
                                     child1, m_grantedUser);
        PermissionService.grantPermission(objPerm3);

        grants = PermissionService.getGrantedPermissions(child1.getOID());
        assertEquals("Wrong number of grants", initSize + 1, grants.size());

        grants = PermissionService.getGrantedPermissions(gchild.getOID());
        assertEquals("Wrong number of grants", initSize + 1, grants.size());

        PermissionService.setContext(child1, m_target);

        grants = PermissionService.getGrantedPermissions(child2.getOID());
        assertEquals("Wrong number of grants", initSize + 2, grants.size());

        grants = PermissionService.getGrantedPermissions(gchild.getOID());
        assertEquals("Wrong number of grants", initSize + 3, grants.size());

        PermissionService.revokePermission(objPerm1);
        PermissionService.revokePermission(objPerm2);
        PermissionService.revokePermission(objPerm3);
        grants = PermissionService.getGrantedPermissions(child1.getOID());
        assertEquals("Wrong number of grants", initSize + 0, grants.size());
        grants = PermissionService.getGrantedPermissions(child2.getOID());
        assertEquals("Wrong number of grants", initSize + 0, grants.size());
        grants = PermissionService.getGrantedPermissions(gchild.getOID());
        assertEquals("Wrong number of grants", initSize + 0, grants.size());
    }

    /**
     * basic test for PermissionService.{get,getDirect,getImplied}Privileges
     *
     * Marking this test as FAILS.  When run in the suite,
     * ParameterizedPrivilegeTest run first.  ParameterizedPrivilegeTest creates
     * junk privileges.  It rolls back the transaction so that the privileges
     * are not written to the database.  However, the cache it uses is not
     * flushed, causing testGetPrivileges() to fail.
     *
     **/
    public void FAILStestGetPrivileges() throws Exception {
        PrivilegeDescriptor fooPriv =
            PrivilegeDescriptor.createPrivilege("foo");
        PrivilegeDescriptor barPriv =
            PrivilegeDescriptor.createPrivilege("bar");

        User object = RoleTest.createUser("permprivileges1");
        User user = RoleTest.createUser("permprivileges2");
        User objectContext = RoleTest.createUser("permprivileges3");

        PermissionService.setContext(object, objectContext);

        Group g1 = new Group();
        g1.setName("testGetPrivileges Group");
        g1.addMember(user);
        g1.save();

        Group g2 = new Group();
        g2.setName("testGetPrivileges Group");
        g2.addMember(user);
        g2.save();

        PermissionService.grantPermission(
                                          new PermissionDescriptor(PrivilegeDescriptor.READ, object, user));

        PermissionService.grantPermission(
                                          new PermissionDescriptor(PrivilegeDescriptor.CREATE,
                                                                   object, user));

        PermissionService.grantPermission(
                                          new PermissionDescriptor(PrivilegeDescriptor.WRITE, object, g1));

        PermissionService.grantPermission(
                                          new PermissionDescriptor(fooPriv, objectContext, user));

        PermissionService.grantPermission(
                                          new PermissionDescriptor(barPriv, objectContext, g2));

        Iterator privs = PermissionService.getPrivileges(object.getOID(),
                                                         user.getOID());
        validateCollection(privs,
                           new PrivilegeDescriptor[]
            {
                PrivilegeDescriptor.READ,
                PrivilegeDescriptor.WRITE,
                PrivilegeDescriptor.CREATE,
                fooPriv,
                barPriv
            });

        privs = PermissionService.getDirectPrivileges(object.getOID(),
                                                      user.getOID());

        validateCollection(privs,
                           new PrivilegeDescriptor[]
            {
                PrivilegeDescriptor.READ,
                PrivilegeDescriptor.CREATE
            });

        privs = PermissionService.getImpliedPrivileges(object.getOID(),
                                                       user.getOID());
        validateCollection(privs,
                           new PrivilegeDescriptor[]
            {
                PrivilegeDescriptor.READ,
                PrivilegeDescriptor.WRITE,
                PrivilegeDescriptor.CREATE,
                fooPriv,
                barPriv
            });

        PermissionService.grantPermission(new
                                          PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                                               object,
                                                               g2));

        privs = PermissionService.getPrivileges(object.getOID(),
                                                user.getOID());
        validateCollection(privs,
                           new PrivilegeDescriptor[]
            {
                PrivilegeDescriptor.READ,
                PrivilegeDescriptor.WRITE,
                PrivilegeDescriptor.CREATE,
                fooPriv,
                barPriv,
                PrivilegeDescriptor.ADMIN
            });

        privs = PermissionService.getDirectPrivileges(object.getOID(),
                                                      user.getOID());

        validateCollection(privs,
                           new PrivilegeDescriptor[]
            {
                PrivilegeDescriptor.READ,
                PrivilegeDescriptor.CREATE
            });

        privs = PermissionService.getImpliedPrivileges(object.getOID(),
                                                       user.getOID());
        validateCollection(privs,
                           PrivilegeDescriptor.getAll().toArray());
    }

    /**
     * test permission inheritence based on context hierarchy.  This
     * tests a few different orders of operations -- even though som
     * assertions look redundant, they aren't.  Also tests the
     * effect of object deletion on permission inheritence.
     *
     * At changelist 15195 an 'on delete cascade' clause was added to the
     * context_id column of object_context_map.  There is an unresolved debate
     * as to whether that clause should be there or not.  In the meantime, we
     * are marking this test as FAILS.
     *
     **/
    public void FAILStestPermissionInheritence() throws Exception {

        User user1 = RoleTest.createUser("perminherit1");
        User user2 = RoleTest.createUser("perminherit2");
        User user3 = RoleTest.createUser("perminherit3");
        // test objectContext creation and retrieval
        PermissionService.setContext(user1, user2);
        PermissionService.setContext(user2, user3);

        PermissionService.grantPermission(new
                                          PermissionDescriptor(PrivilegeDescriptor.READ,
                                                               user3,
                                                               m_grantedUser));

        assertTrue("Permission on user3 not inherited by user1",
               PermissionService.checkPermission(new
                                                 PermissionDescriptor(PrivilegeDescriptor.READ,
                                                                      user1,
                                                                      m_grantedUser)));

        PermissionService.revokePermission(new
                                           PermissionDescriptor(PrivilegeDescriptor.READ,
                                                                user3,
                                                                m_grantedUser));

        assertTrue("Inherited permission was revoked but check still succeeds",
               !PermissionService.checkPermission(new
                                                  PermissionDescriptor(PrivilegeDescriptor.READ,
                                                                       user1,
                                                                       m_grantedUser)));
        PermissionService.grantPermission(new PermissionDescriptor(PrivilegeDescriptor.READ,
                                                                   user3,
                                                                   m_grantedUser));

        assertTrue("Permission on user3 not inherited by user1",
               PermissionService.checkPermission(new
                                                 PermissionDescriptor(PrivilegeDescriptor.READ,
                                                                      user1,
                                                                      m_grantedUser)));

        PermissionService.setContext(user2, null);

        assertTrue("Permission still inherited from old implied context",
               !PermissionService.checkPermission(new
                                                  PermissionDescriptor(PrivilegeDescriptor.READ,
                                                                       user1,
                                                                       m_grantedUser)));

        PermissionService.grantPermission(new
                                          PermissionDescriptor(PrivilegeDescriptor.READ,
                                                               user2,
                                                               m_grantedUser));

        assertTrue("Permission on user2 not inherited by user1",
               PermissionService.checkPermission(new
                                                 PermissionDescriptor(PrivilegeDescriptor.READ,
                                                                      user1,
                                                                      m_grantedUser)));


        PermissionService.setContext(user2, user3);

        assertTrue("Permission on users 2&3 not inherited by user1",
               PermissionService.checkPermission(new
                                                 PermissionDescriptor(PrivilegeDescriptor.READ,
                                                                      user1,
                                                                      m_grantedUser)));

        PermissionService.revokePermission(new
                                           PermissionDescriptor(PrivilegeDescriptor.READ,
                                                                user2,
                                                                m_grantedUser));

        assertTrue("Permission on user3 not inherited by user1",
               PermissionService.checkPermission(new
                                                 PermissionDescriptor(PrivilegeDescriptor.READ,
                                                                      user1,
                                                                      m_grantedUser)));

        try {
            user2.delete();
            fail("user2 was deleted even though it serves as user1's context");
        } catch (PersistenceException e) {
            // intentionally empty
        }

        try {
            user3.delete();
            fail("user3 was deleted even though it serves as user2's context");
        } catch (PersistenceException e) {
            // intentionally empty
        }

        user1.delete();

        assertTrue("Permission on user3 not inherited by user2",
               PermissionService.checkPermission(new
                                                 PermissionDescriptor(PrivilegeDescriptor.READ,
                                                                      user2,
                                                                      m_grantedUser)));
        try {
            user3.delete();
            fail("user3 was deleted even though it serves as user2's context");
        } catch (PersistenceException e) {
            // intentionally empty
        }

        user2.delete();
        user3.delete();

    }

    /**
     * Tests group membership denormalization. m_grantedUser becomes a member
     * of a group through two different subgroups. When he is removed from
     * both subgroups, the permissions of the parent group should no longer
     * apply to him. See bug 106160.
     */
    public void testInheritGroupDenorm() {
        Group sub1 = GroupTest._createGroup();
        Group sub2 = GroupTest._createGroup();
        Group group = GroupTest._createGroup();
        group.addSubgroup(sub1);
        group.addSubgroup(sub2);
        group.save();
        sub1.addMember(m_grantedUser);
        sub1.save();
        sub2.addMember(m_grantedUser);
        sub2.save();

        PrivilegeDescriptor priv = PrivilegeDescriptor.READ;
        PermissionDescriptor objPerm =
            new PermissionDescriptor(priv, m_target, group);

        PermissionService.grantPermission(objPerm);

        assertTrue(PermissionService.checkPermission
                   (new PermissionDescriptor(priv, m_target, m_grantedUser)));

        sub1.removeMember(m_grantedUser);
        sub1.save();
        sub2.removeMember(m_grantedUser);
        sub2.save();

        assertTrue(!PermissionService.checkPermission
                   (new PermissionDescriptor(priv, m_target, m_grantedUser)));
    }

    private void validateCollection(Iterator objects,
                                    Object[] targetObjects)
        throws Exception
    {

        Collection set = arrayToSet(targetObjects);

        int ctr=0;
        while (objects.hasNext()) {
            ctr++;
            Object o = objects.next();
            if (!set.remove(o)) {
                fail("Collection should not contain " + o);
            }
        }
        if (ctr != targetObjects.length) {
            fail("Collection contains wrong number of objects.  " +
                 "Expected " + targetObjects.length + " objects." +
                 "Got " + ctr + " objects.");
        }

    }

    private Collection arrayToSet(Object[] objects) {
        HashSet set = new HashSet();
        for (int i=0; i<objects.length; i++) {
            set.add(objects[i]);
        }
        return set;
    }

    public static Test suite() {
        return new TestSuite(PermissionTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

}
