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
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.PersonName;
import com.arsdigita.kernel.User;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import com.arsdigita.util.UncheckedWrapperException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.log4j.Logger;

/**
 * Tests that the permissioning service works correctly regardless of the
 * ordering of particular actions (such as granting to a group, adding a
 * subgroup/member to a group, setting context, etc).
 *
 *
 * @author Patrick McNeill
 * @version 1.0
 **/
public class OrderingTest extends BaseTestCase {

    public final static String versionId = "$Id: OrderingTest.java 744 2005-09-02 10:43:19Z sskracic $";

    private static Logger s_cat =
        Logger.getLogger(OrderingTest.class.getName());


    /**
     * Constructs a OrderingTest with the specified name.
     *
     * @param name Test case name.
     **/
    public OrderingTest( String name ) {
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
    /*
     * Support method for the order generator
     */
    private void swap(int[] ordering, int a, int b) {
        int tmp = ordering[a];
        ordering[a] = ordering[b];
        ordering[b] = tmp;
    }

    /*
     * Support method for the order generator.  Does the actual work by
     * recursively creating all orderings of an array.
     */
    private void permute(int[] ordering, int pos, List result) {
        if (pos == ordering.length) {
            result.add(ordering.clone());
        }

        for (int i = pos; i < ordering.length; i++) {
            swap(ordering, pos, i);
            permute(ordering, pos+1, result);
            swap(ordering, i, pos);
        }
    }

    /**
     * Generates a List of arrays of each possible ordering of "ordering".
     *
     * @param ordering the array containing the elements to permute
     * @return a List of arrays of each possible ordering of the input array
     */
    public List generateOrdering(int[] ordering) {
        ArrayList result = new ArrayList();

        permute(ordering, 0, result);

        return result;
    }

    /*
     * Test all possible combinations of each of:
     *    setContextStep, grantPermissionStep, addMemberStep
     *    setContextStep, grantPermissionStep, addSubgroupStep
     */

    /*
     * Make obj1 inherit permissions from obj2
     */
    private void setContextStep(ACSObject obj1, ACSObject obj2) {
        PermissionService.setContext(obj1, obj2);
    }

    private void grantPermissionStep(ACSObject obj,
                                     Party grantee,
                                     PrivilegeDescriptor pd) {
        PermissionService.grantPermission(
                                          new PermissionDescriptor(pd, obj, grantee));
    }

    private void revokePermissionStep(ACSObject obj,
                                      Party grantee,
                                      PrivilegeDescriptor pd) {
        PermissionService.revokePermission(
                                           new PermissionDescriptor(pd, obj, grantee));
    }

    private void addMemberStep(Group group, User user) {
        group.addMember(user);
        group.save();
    }

    private void addSubgroupStep(Group group, Group subgroup) {
        group.addSubgroup(subgroup);
        group.save();
    }

    /**
     * Try orderings of setContextStep, grantPermissionStep, and addMemberStep.
     */
    public void testNoSubgroupsGranting() {
        int[] ordering = { 1, 0, 2 };
        List orders = generateOrdering(ordering);

        OrderingTestRunner runner = new OrderingTestRunner() {
            void executeTest(int[] ordering) {
                doNoSubgroupGrantTest(ordering);
            }
        };
        Iterator it = orders.iterator();

        while (it.hasNext()) {
            ordering = (int[])it.next();
            runner.run(ordering);
        }
    }

    private void doNoSubgroupGrantTest(int[] ordering) {
        StringBuffer sb = new StringBuffer();


        PrivilegeDescriptor pd = PrivilegeDescriptor.get("read");

        Group group = new Group();
        group.setName("Bob's Group");
        group.save();

        Group obj1 = new Group();
        obj1.setName("Test Object 1");
        obj1.save();

        Group obj2 = new Group();
        obj2.setName("Test Object 2");
        obj2.save();

        User user = new User();
        user.setPrimaryEmail(new EmailAddress("bob@bobs.com"));
        PersonName name = user.getPersonName();
        name.setGivenName("Bob");
        name.setFamilyName("Roberts");
        user.save();

        for (int i = 0; i<ordering.length; i++) {
            switch (ordering[i]) {
            case 0:
                sb.append("Setting context => ");
                setContextStep(obj1, obj2);
                break;
            case 1:
                sb.append("Adding member => ");
                addMemberStep(group, user);
                break;
            case 2:
                sb.append("Granting permission => ");
                grantPermissionStep(obj2, group, pd);
                break;
            }
        }

        sb.append("Checks => ");

        if (!PermissionService.checkPermission(
                                               new PermissionDescriptor(pd, obj1, group))) {
            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (!PermissionService.checkPermission(
                                               new PermissionDescriptor(pd, obj2, group))) {
            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (!PermissionService.checkPermission(
                                               new PermissionDescriptor(pd, obj1, user))) {
            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (!PermissionService.checkPermission(
                                               new PermissionDescriptor(pd, obj2, user))) {
            fail("Inherited permissions check failed\n" + sb.toString());
        }

        sb.append("Done");
    }

    /**
     * Try orderings of setContextStep, grantPermissionStep, addMemberStep,
     * and revokePermissionStep.
     */
    public void testNoSubgroupsRevoking() {
        int[] ordering = { 0, 1, 2, 3 };
        List orders = generateOrdering(ordering);

        Iterator it = orders.iterator();

        OrderingTestRunner runner = new OrderingTestRunner() {
            void executeTest(int[] ordering) {
                doNoSubgroupRevokeTest(ordering);
            }
        };

        outer:
        while (it.hasNext()) {
            runner.run((int[]) it.next());
            continue outer;
        }
    }

    private void doNoSubgroupRevokeTest(int[] ordering) {
        StringBuffer sb = new StringBuffer();
        boolean granted = false;

        PrivilegeDescriptor pd = PrivilegeDescriptor.get("read");

        Group group = new Group();
        group.setName("Bob's Group");
        group.save();

        Group obj1 = new Group();
        obj1.setName("Test Object 1");
        obj1.save();

        Group obj2 = new Group();
        obj2.setName("Test Object 2");
        obj2.save();

        User user = new User();
        user.setPrimaryEmail(new EmailAddress("bob@bobs.com"));
        PersonName name = user.getPersonName();
        name.setGivenName("Bob");
        name.setFamilyName("Roberts");
        user.save();

        for (int i = 0; i<ordering.length; i++) {
            switch (ordering[i]) {
            case 0:
                sb.append("Setting context => ");
                setContextStep(obj1, obj2);
                break;
            case 1:
                sb.append("Adding member => ");
                addMemberStep(group, user);
                break;
            case 2:
                sb.append("Granting permission => ");
                grantPermissionStep(obj2, group, pd);
                granted = true;
                break;
            case 3:
                sb.append("Revoking permission => ");
                if (!granted) {
                    s_cat.info ("Ignoring ordering:\n" + sb.toString());
                    return;
                }
                revokePermissionStep(obj2, group, pd);
                break;
            }
        }

        sb.append("Checks => ");

        if (PermissionService.checkPermission(
                                              new PermissionDescriptor(pd, obj1, group))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (PermissionService.checkPermission(
                                              new PermissionDescriptor(pd, obj2, group))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (PermissionService.checkPermission(
                                              new PermissionDescriptor(pd, obj1, user))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (PermissionService.checkPermission(
                                              new PermissionDescriptor(pd, obj2, user))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        sb.append("Done");
    }

    /**
     * Try orderings of setContextStep, grantPermissionStep, addMemberStep,
     * and addSubgroupStep.
     */
    public void testSubgroupsGranting() {
        int[] ordering = { 0, 1, 2, 3 };
        List orders = generateOrdering(ordering);

        OrderingTestRunner runner = new OrderingTestRunner() {
            void executeTest(int[] ordering) {
                doSubgroupGrantingTest(ordering);
            }
        };

        Iterator it = orders.iterator();

        while (it.hasNext()) {

            ordering = (int[])it.next();
            runner.run(ordering);
        }
    }

    private void doSubgroupGrantingTest(int[] ordering) {
        StringBuffer sb = new StringBuffer();

        PrivilegeDescriptor pd = PrivilegeDescriptor.get("read");

        Group group = new Group();
        group.setName("Bob's Group");
        group.save();

        Group subgroup = new Group();
        subgroup.setName("Bob's Subgroup");
        subgroup.save();

        Group obj1 = new Group();
        obj1.setName("Test Object 1");
        obj1.save();

        Group obj2 = new Group();
        obj2.setName("Test Object 2");
        obj2.save();

        User user = new User();
        user.setPrimaryEmail(new EmailAddress("bob@bobs.com"));
        PersonName name = user.getPersonName();
        name.setGivenName("Bob");
        name.setFamilyName("Roberts");
        user.save();

        for (int i = 0; i<ordering.length; i++) {
            switch (ordering[i]) {
            case 0:
                sb.append("Setting context => ");
                setContextStep(obj1, obj2);
                break;
            case 1:
                sb.append("Adding subgroup => ");
                addSubgroupStep(group, subgroup);
                break;
            case 2:
                sb.append("Granting permission => ");
                grantPermissionStep(obj2, group, pd);
                break;
            case 3:
                sb.append("Adding member => ");
                addMemberStep(subgroup, user);
                break;
            }
        }

        sb.append("Checks => ");

        if (!PermissionService.checkPermission(
                                               new PermissionDescriptor(pd, obj1, group))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (!PermissionService.checkPermission(
                                               new PermissionDescriptor(pd, obj2, group))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (!PermissionService.checkPermission(
                                               new PermissionDescriptor(pd, obj1, subgroup))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (!PermissionService.checkPermission(
                                               new PermissionDescriptor(pd, obj2, subgroup))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (!PermissionService.checkPermission(
                                               new PermissionDescriptor(pd, obj1, user))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (!PermissionService.checkPermission(
                                               new PermissionDescriptor(pd, obj2, user))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        sb.append("Done");
    }

    /**
     * Try orderings of setContextStep, grantPermissionStep, addMemberStep,
     * addSubgroupStep, and revokePermissionStep.
     */
    public void testSubgroupsRevoking() {
        int[] ordering = { 0, 1, 2, 3, 4 };
        List orders = generateOrdering(ordering);

        OrderingTestRunner runner = new OrderingTestRunner() {
            void executeTest(int[] ordering) {
                doSubgroupRevokingTest(ordering);
            }
        };

        Iterator it = orders.iterator();

        outer:
        while (it.hasNext()) {

            ordering = (int[])it.next();

            runner.run(ordering);
            continue outer;


        }
    }

    private void doSubgroupRevokingTest(int[] ordering) {


        PrivilegeDescriptor pd = PrivilegeDescriptor.get("read");

        Group group = new Group();
        group.setName("Bob's Group");
        group.save();

        Group subgroup = new Group();
        subgroup.setName("Bob's Subgroup");
        subgroup.save();

        Group obj1 = new Group();
        obj1.setName("Test Object 1");
        obj1.save();

        Group obj2 = new Group();
        obj2.setName("Test Object 2");
        obj2.save();

        User user = new User();
        user.setPrimaryEmail(new EmailAddress("bob@bobs.com"));
        PersonName name = user.getPersonName();
        name.setGivenName("Bob");
        name.setFamilyName("Roberts");
        user.save();

        boolean granted = false;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i<ordering.length; i++) {
            switch (ordering[i]) {
            case 0:
                sb.append("Setting context => ");
                setContextStep(obj1, obj2);
                break;
            case 1:
                sb.append("Adding member => ");
                addMemberStep(subgroup, user);
                break;
            case 2:
                sb.append("Granting permission => ");
                grantPermissionStep(obj2, group, pd);
                granted = true;
                break;
            case 3:
                sb.append("Revoking permission => ");
                if (!granted) {
                    s_cat.info ("Ignoring ordering:\n" + sb.toString());

                    return;
                }
                revokePermissionStep(obj2, group, pd);
                break;
            case 4:
                sb.append("Adding subgroup => ");
                addSubgroupStep(group, subgroup);
                break;
            }
        }

        sb.append("Checks => ");

        if (PermissionService.checkPermission(
                                              new PermissionDescriptor(pd, obj1, group))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (PermissionService.checkPermission(
                                              new PermissionDescriptor(pd, obj2, group))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (PermissionService.checkPermission(
                                              new PermissionDescriptor(pd, obj1, subgroup))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (PermissionService.checkPermission(
                                              new PermissionDescriptor(pd, obj2, subgroup))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (PermissionService.checkPermission(
                                              new PermissionDescriptor(pd, obj1, user))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        if (PermissionService.checkPermission(
                                              new PermissionDescriptor(pd, obj2, user))) {

            fail("Inherited permissions check failed\n" + sb.toString());
        }

        sb.append("Done");
    }

    public static Test suite() {
        return new TestSuite(OrderingTest.class);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    abstract class OrderingTestRunner {
        abstract void executeTest(final int[] ordering);

        void run(final int[] ordering) {
            Session sess = SessionManager.getSession();
            if (!sess.getTransactionContext().inTxn()) {
                sess.getTransactionContext().beginTxn();
            }

            try {
                KernelExcursion ke = new KernelExcursion() {
                    protected void excurse() {
                        setEffectiveParty(Kernel.getSystemParty());
                        executeTest(ordering);
                    }
                };

                ke.run();
            } finally {
                if (sess.getTransactionContext().inTxn()) {
                    sess.getTransactionContext().abortTxn();
                }
            }

        }
    }
}
