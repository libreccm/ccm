/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.persistence.tests.data;

import com.arsdigita.tools.junit.framework.BaseTestCase;

/**
 * The MappingsTest class contains JUnit test cases for the various ways that
 * persistences supports mapping logical object hierarchy to a physical data
 * model.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 **/

public class MappingsTest extends BaseTestCase {

    

    public MappingsTest(String name) {
        super(name);
    }

    private static final void doTest(String type) {
        String qualified = "com.arsdigita.persistence.tests.data.mappings." +
            type;
        CRUDTestlet testlet = new CRUDTestlet(qualified);
        testlet.run();
    }

    private static final void doIsolationTest(String type, String[] path) {
        String qualified = "com.arsdigita.persistence.tests.data.mappings." +
            type;
        new PropertyIsolationTestlet(qualified, path).run();
    }

    private static final void doDoubleTest(String type, String[] path) {
        String qualified = "com.arsdigita.persistence.tests.data.mappings." +
            type;
        new DoubleUpdateTestlet(qualified, path).run();
    }

    public void testBase() {
        doTest("Base");
    }

    public void testTarget() {
        doTest("Target");
    }

    public void testParasiteOne() {
        doTest("ParasiteOne");
    }

    public void testParasiteTwo() {
        doTest("ParasiteTwo");
    }

    public void testSymbioteOne() {
        doTest("SymbioteOne");
    }

    public void testSymbioteTwo() {
        doTest("SymbioteOne");
    }

    public void testNormalized() {
        doTest("Normalized");
    }

    public void testReferenceIsolation() {
        doIsolationTest
            ("ReferenceTo", new String[] { "target", "id" });
    }

    public void testReferenceDouble() {
        doDoubleTest
            ("ReferenceTo", new String[] { "target", "id" });
    }

    public void testReferenceFromDouble() {
        doDoubleTest
            ("ReferenceFrom", new String[] { "target", "id" });
    }

    public void testReferenceMappingTableIsolation() {
        doIsolationTest
            ("ReferenceMappingTable", new String[] { "target", "id" });
    }

    public void testReferenceMappingTableDouble() {
        doDoubleTest
            ("ReferenceMappingTable", new String[] { "target", "id" });
    }

    public void testRequiredTwoWayReference() {
        new DoubleUpdateTestlet
            ("test.Component", new String[] { "test", "id" }).run();
    }

    public void testNakedJoin() {
        doTest("NakedJoin");
    }
}
