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

import com.arsdigita.dispatcher.Dispatcher;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import org.apache.log4j.Logger;

/**
 * Test cases for the PackageType class.
 *
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */

public class PackageTypeTest extends BaseTestCase {

    private PackageType m_ptype;
    private PackageType m_ptype2;

    private static Logger s_cat =
        Logger.getLogger(PackageTypeTest.class.getName());

    private static final String m_testKey = "__ACS-test";
    private static final String m_testName = "__ACS Test Package";
    private static final String m_testPlural = "__ACS Test Packages";
    private static final String m_testURI = "http://www.junit.org/";
    private static final String m_testKey2 = "__ACS-tes2t";
    private static final String m_testName2 = "__ACS Test Package2";
    private static final String m_testPlural2 = "__ACS Test Packages2";
    private static final String m_testURI2 = "http://www.junit.org/2";

    private static final String sampleListener =
        "com.arsdigita.kernel.SampleListener";
    private static final String sampleListener2 =
        "com.arsdigita.kernel.SampleListener2";


    public PackageTypeTest (String name) {
        super(name);
    }

    public void setUp() {
        m_ptype = new PackageType();
        m_ptype.setKey(m_testKey);
        m_ptype.setPrettyName(m_testName);
        m_ptype.setPrettyPlural(m_testPlural);
        m_ptype.setURI(m_testURI);
        m_ptype.addListener(sampleListener);
        m_ptype.save();

        m_ptype2 = PackageType.create
            (m_testKey2, m_testName2, m_testPlural2, m_testURI2);
        m_ptype2.addListener(sampleListener2);
        m_ptype2.save();

    }

    private PackageType retrieveTestType(String key) {
        PackageType testPt = null;
        try {
            testPt = PackageType.findByKey(key);
        } catch (DataObjectNotFoundException e) {
            fail("Test package not found.");
        }
        return testPt;
    }

    public void testQuery() {
        PackageType testPt = retrieveTestType(m_testKey);
        assertEquals(m_testName, testPt.getPrettyName());
        assertEquals(m_testPlural, testPt.getPrettyPlural());
        assertEquals(m_testURI, testPt.getURI());
    }

    public void testListenerAssociation() {
        PackageType testPt = retrieveTestType(m_testKey);
        PackageEventListener[] listeners = testPt.getListeners();
        assertTrue(listeners.length == 1);
        // Remove it.
        testPt.removeListener(sampleListener);
        testPt.save();
        // Query for it.
        testPt = retrieveTestType(m_testKey);
        listeners = testPt.getListeners();
        assertTrue("There should be 0 listeners, but instead there are " +
                   listeners.length + " listeners.", listeners.length == 0);
        testPt.addListener(sampleListener);
        testPt.save();

        testPt = retrieveTestType(m_testKey);
        assertTrue(testPt.getListeners().length == 1);
        testPt.addListener(sampleListener2);
        testPt.save();

        testPt = retrieveTestType(m_testKey);
        assertTrue(testPt.getListeners().length == 2);

        testPt = retrieveTestType(m_testKey2);
        assertTrue(testPt.getListeners().length == 1);
        testPt.removeListener(sampleListener2);
        testPt.save();

        testPt = retrieveTestType(m_testKey2);
        assertTrue(testPt.getListeners().length == 0);

        testPt = retrieveTestType(m_testKey);
        testPt.removeListener(sampleListener);
        testPt.removeListener(sampleListener2);
        testPt.save();

        testPt = retrieveTestType(m_testKey);
        assertTrue(testPt.getListeners().length == 0);
        testPt.addListener(sampleListener2);
        testPt.save();
        testPt = retrieveTestType(m_testKey);
        assertTrue(testPt.getListeners().length == 1);
    }

    public void testListenerBehavior() {
        PackageType testPt = retrieveTestType(m_testKey);
        SiteNode siteNode = SiteNode.createSiteNode("__ACS-test");
        SiteNode siteNode2 = SiteNode.createSiteNode("__ACS-test2");
        PackageInstance pkg = testPt.createInstance("test1");
        pkg.save();
        siteNode.mountPackage(pkg);
        siteNode.save();
        siteNode2.mountPackage(pkg);
        siteNode2.save();
        siteNode2.unMountPackage();
        siteNode2.save();
        pkg.delete();
    }

    /**
     * Tests getDispatcher to make sure it returns the right
     * dispatcher.
     *
     * @author Richard Li */
    public void testGetDispatcher() {
        PackageType testPt = retrieveTestType(m_testKey);
        Dispatcher testDispatcher = null;
        try {
            testDispatcher = (Dispatcher) testPt.getDispatcher();
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // We should have the JSPApplicationDispatcher class here,
        // so we use Reflection to verify this.

        Class dispatcherClass = testDispatcher.getClass();
        String dispatcherName = dispatcherClass.getName();

        assertEquals("com.arsdigita.dispatcher.JSPApplicationDispatcher",dispatcherName);
    }

    /**
     * Tests getDispatcherClass() in the default case, where the
     * constructor returns JSPApplicationDispatcher.
     *
     * @author Richard Li
     */
    public void testGetDispatcherClass() {
        PackageType testPt = retrieveTestType(m_testKey);
        String dispatcherName = testPt.getDispatcherClass();
        assertEquals("com.arsdigita.dispatcher.JSPApplicationDispatcher",dispatcherName);
    }

    public void testSetDispatcherClass() {
        PackageType testPt = retrieveTestType(m_testKey);
        testPt.setDispatcherClass("com.arsdigita.bebop.BebopMapDispatcher");
        String dispatcherName = testPt.getDispatcherClass();
        assertEquals("com.arsdigita.bebop.BebopMapDispatcher",dispatcherName);

        // cleanup: restore to default
        testPt.setDispatcherClass("com.arsdigita.dispatcher.JSPApplicationDispatcher");
    }

}
