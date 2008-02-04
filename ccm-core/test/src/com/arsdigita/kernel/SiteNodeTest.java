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
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.tools.junit.framework.BaseTestCase;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Test cases for the Site Node class.
 *
 * @author Bryan Quinn
 * @version $Revision: #12 $ $Date: 2004/08/16 $
 * @since ACS 5.0
 */

public class SiteNodeTest extends BaseTestCase {
    public static final String versionId = "$Id: SiteNodeTest.java 750 2005-09-02 12:38:44Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private SiteNode m_siteNode;
    private Session m_ssn;
    private static Logger s_log =
        Logger.getLogger(SiteNodeTest.class.getName());


    public SiteNodeTest(String name) {
        super(name);
    }

    public void setUp() {
        try {
            m_ssn = SessionManager.getSession();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public void tearDown() {
    }

    private SiteNode createNode(String name) {
        return createNode(name, null);
    }

    private SiteNode createNode(String name, SiteNode parent) {
        SiteNode siteNode = new SiteNode();
        siteNode.setName(name);
        siteNode.setParent(parent);
        siteNode.save();
        return siteNode;
    }

    public void testCreate() {
        final String testName = "__ACS-test";
        SiteNode newNode = null;

        SiteNode siteNode = createNode(testName);
        // Test setting a persistent property.
        siteNode.setName(testName);
        BigDecimal nodeID = siteNode.getID();

        // Test retrieval of a persistent property.
        try {
            newNode = new SiteNode(nodeID);
        } catch (DataObjectNotFoundException e) {
            fail("The SiteNode was not created properly or could not be " +
                 "retrieved.");
        }
        assertEquals("Value of property does not match what was set",
                     newNode.getName(),
                     testName);

        // Test retrieveal via a query.
        DataCollection allSiteNodes =
            m_ssn.retrieve("com.arsdigita.kernel.SiteNode");
        allSiteNodes.addFilter("name = '" + testName + "'");
        assertTrue(allSiteNodes.next());
        assertEquals("The SiteNode was not retrieved correctly.",
                     testName,
                     allSiteNodes.get("name"));
        allSiteNodes.close();
    }

    public void testGetSiteNodeByURL() {
        SiteNode node = null;
        final String testParent = "__ACS-test2";
        final String testChild = "__child";
        final String testPath = "/" + testParent + "/" + testChild;
        final String testPathWithSlash = "/" + testParent + "/" + testChild + "/";
        SiteNode parent = createNode(testParent, SiteNode.getRootSiteNode());
        SiteNode child = createNode(testChild, parent);
        try {
            node = SiteNode.getSiteNode(testPath);
        } catch (DataObjectNotFoundException e) {
            fail("The SiteNode could not be retrieved.");
        }
        assertTrue("Querying for the node by URL failed.", node != null);
        assertEquals("The nodes are not equal.", child, node);
        assertEquals(testPathWithSlash, node.getURL());

        //try getting the url with a slash at the end
        try {
            node = SiteNode.getSiteNode(testPathWithSlash);
        } catch (DataObjectNotFoundException e) {
            fail("The SiteNode could not be retrieved.");
        }
        assertTrue("Querying for the node by URL failed.", node != null);
        assertEquals("The nodes are not equal.", child, node);
        assertEquals(testPathWithSlash, node.getURL());


        //make sure that if we add additional parts to the url
        //we still get back the same node
        try {
            node = SiteNode.getSiteNode(testPath + "/index");
        } catch (DataObjectNotFoundException e) {
            fail("The SiteNode could not be retrieved.");
        }
        assertTrue("Querying for the node by URL failed.", node != null);
        assertEquals("The nodes are not equal.", child, node);
    }

    public void testAssociations() {
        SiteNodeCollection snc = null;
        SiteNode parentNode = createNode("__ACS-test-parent", null);
        BigDecimal parentID = parentNode.getID();
        createNode("child1", parentNode);
        createNode("child2", parentNode);
        createNode("child3", parentNode);
        createNode("child4", parentNode);

        try {
            snc = new SiteNode(parentID).getChildren();
        } catch (DataObjectNotFoundException e) {
            fail("Unable to retrieve the SiteNode.");
        }
        int i = 0;
        while (snc.next()) {
            i++;
            snc.getSiteNode().delete();
        }
        assertEquals("There should be four children of the parent.",
                     new Integer(4), new Integer(i));
    }

    public void testStyle() {
        final String path = "__ACS-test-style";
        SiteNode node = createNode(path, null);
        Stylesheet sheet = Stylesheet.createStylesheet("/some/path");
        node.addStylesheet(sheet);
        node.save();
        Stylesheet sheets[] = node.getStylesheets(null, null);
        if (sheets.length == 0) {
            fail("No stylesheets were found. We expected to find one");
        }
        assertEquals("Stylesheet returned was not the one we expected",
                     sheet.getID(), sheets[0].getID());
    }

    // This test fails because the kernel initializer no longer mounts the
    // bebop-demo pages.
    public void FAILStestMount() {
        PackageType pType = null;
        SiteNode testNode = null;

        try {
            pType = PackageType.findByKey("bebop-demo");
        } catch (DataObjectNotFoundException e) {
            fail("Unable to retrieve the bebop-demo package.");
        }
        PackageInstance pkg = pType.createInstance("__ACS-test-bebop");
        SiteNode siteNode = createNode("__ACS-test-3", SiteNode.getRootSiteNode());
        siteNode.mountPackage(pkg);
        siteNode.save();


        try {
            testNode = new SiteNode(siteNode.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("Unable to retrieve the siteNode.");
        }
        PackageInstance testPkg = testNode.getPackageInstance();
        assertTrue("The package was not mounted correctly.",
                   pkg.getOID().equals(testPkg.getOID()));

        testNode.unMountPackage();
        testNode.save();
        assertTrue("The package was not unmounted correctly.",
                   testNode.getPackageInstance() == null);

        try {
            testNode = new SiteNode(siteNode.getOID());
        } catch (DataObjectNotFoundException e) {
            fail("Unable to retrieve the siteNode.");
        }
        testNode.mountPackage(pkg);
        testNode.save();
        // testing to see if the sitenode parent is still correct...
        assertEquals(siteNode.getParent(), testNode.getParent());
    }

    public void testSetParent() throws DataObjectNotFoundException {
        SiteNode root = createNode("root", null);
        SiteNode a = createNode("A", root);
        SiteNode b = createNode("B", a);
        SiteNode x = createNode("X", root);
        assertTrue("URL for site node B before setParent",
                   b.getURL().equals("root/A/B/"));
        a.setParent(x);
        a.save();

        // re-retrieve B because B has old properties.
        // this line shouldn't be necessary.
        b = new SiteNode(new com.arsdigita.persistence.OID
                         ("com.arsdigita.kernel.SiteNode",
                          b.getNodeId()));
        assertTrue("URL for site node B after setParent is " + b.getURL(),
                   b.getURL().equals("root/X/A/B/"));
    }
}
