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

import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * NodeTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #10 $ $Date: 2004/08/16 $
 */

public abstract class NodeTest extends PersistenceTestCase {

    public final static String versionId = "$Id: NodeTest.java 741 2005-09-02 10:21:19Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static Logger s_log =
        Logger.getLogger(NodeTest.class.getName());

    public NodeTest(String name) {
        super(name);
    }

    abstract String getModelName();

    public void testCRUD() {
        DataObject node = getSession().create(getModelName() + ".Node");
        node.set("id", new BigDecimal(0));
        node.set("name", "Root");
        assertTrue(node.isNew());
        node.save();
        assertTrue(node.isNew() == false);

        node = getSession().retrieve(new OID(getModelName() + ".Node", 0));
        assertEquals("Node was not retrieved properly.",
                     "Root",
                     node.get("name"));

        node.set("name", "root");
        assertTrue( node.isModified());
        assertTrue( node.isPropertyModified("name"));
        node.save();
        assertTrue( node.isModified() == false );


        node = getSession().retrieve(new OID(getModelName() + ".Node", 0));
        assertEquals("Node was not updated properly.",
                     "root",
                     node.get("name"));

        node.delete();
        assertTrue( node.isDeleted() );
        node = getSession().retrieve(new OID(getModelName() + ".Node", 0));
        assertEquals("Node was not deleted properly.",
                     null,
                     node);

        try {
            node = createNode(0, "Root");
            node.set("parent", "grizzle!");
            node.save();
            fail("Set parent to an invalid type!");

        } catch (PersistenceException e) {
            node.delete();
        }

        try {
            node = createNode(0, "Root");
            node.set("foo", "bar");
            node.save();
            fail("Set an invalid attribute!");

        } catch (PersistenceException e) {
            node.delete();
        }


        // XXX: I don't think this should be illegal
        /*try {
          node = createNode(0, "Root");
          node.set("parent", node);
          node.save();
          fail("Set parent to itself!");
          }
          catch (PersistenceException e) {
          }*/

        try {
            node = createNode(0, "Root");
            node.save();
            node = createNode(0, "Root");
            node.save();
            fail("Created two nodes with same id!");

        } catch (PersistenceException e) {
            node.delete();
        }

        try {
            node = createNode(0, "Root");
            node.get("foobar");
            fail("Got nonexistent property!");

        } catch (PersistenceException e) {
        }

    }

    public void testLazyUpdates() {
        DataObject parent = getSession().create(getModelName() + ".Node");
        parent.set("id", new BigDecimal(0));
        parent.set("name", "parent");

        DataObject child = getSession().create(getModelName() + ".Node");
        child.set("id", new BigDecimal(1));
        child.set("name", "child");
        child.set("parent", parent);

        parent.save();
        child.save();

        child = getSession().retrieve(new OID(getModelName() + ".Node", 1));
        child.set("name", "other child");
        child.save();

        child = getSession().retrieve(new OID(getModelName() + ".Node", 1));
        assertEquals("Update failed.",
                     "other child",
                     child.get("name"));
        assertTrue(child.get("parent") != null);
    }

    public void testParentAssociation() {
        DataObject parent = getSession().create(getModelName() + ".Node");
        parent.set("id", new BigDecimal(0));
        parent.set("name", "parent");

        DataObject child = getSession().create(getModelName() + ".Node");
        child.set("id", new BigDecimal(1));
        child.set("name", "child");
        child.set("parent", parent);

        parent.save();
        child.save();

        child = getSession().retrieve(new OID(getModelName() + ".Node", 1));
        try {
            parent = (DataObject) child.get("parent");
        } catch (ClassCastException e) {
            fail("Parent was not retrieved properly.");
        }
        assertEquals("Incorrect type was retrieved " +
                     "from parent association.",
                     parent.getObjectType().getQualifiedName(),
                     getModelName() + ".Node");
        assertEquals("Parent id was not successfully retrieved " +
                     "from the parent association.",
                     new BigDecimal(0),
                     parent.get("id"));
        assertEquals("Parent name was not successfully retrieved " +
                     "from the parent association.",
                     "parent",
                     parent.get("name"));

        parent = getSession().retrieve(new OID(getModelName() + ".Node", 0));
        DataAssociation children = null;
        try {
            children = (DataAssociation) parent.get("children");
        } catch (ClassCastException e) {
            fail("Children were not retrieved properly.");
        }

        if (children.next()) {
            child = children.getDataObject();
            assertEquals("Incorrect type was retrieved " +
                         "from children association.",
                         getModelName() + ".Node",
                         child.getObjectType().getQualifiedName());
            assertEquals("Child id was not correctly retrieved from " +
                         "the children association.",
                         new BigDecimal(1),
                         child.get("id"));
            assertEquals("Child name was not correctly retrieved from " +
                         "the children association.",
                         "child",
                         child.get("name"));
        } else {
            fail("Children association is empty.");
        }

        if (children.next())
            fail("Children association has too many rows.");
    }

    public void testChildrenAssociation() {
        DataObject root = getSession().create(getModelName() + ".Node");
        root.set("id", new BigDecimal(0));
        root.set("name", null);

        DataObject bin = getSession().create(getModelName() + ".Node");
        bin.set("id", new BigDecimal(1));
        bin.set("name", "bin");
        bin.set("parent", root);

        DataObject etc = getSession().create(getModelName() + ".Node");
        etc.set("id", new BigDecimal(2));
        etc.set("name", "etc");
        etc.set("parent", root);

        DataObject usr = getSession().create(getModelName() + ".Node");
        usr.set("id", new BigDecimal(3));
        usr.set("name", "usr");
        usr.set("parent", root);

        root.save();
        bin.save();
        etc.save();
        usr.save();

        root = getSession().retrieve(new OID(getModelName() + ".Node", 0));
        DataAssociation children = null;
        try {
            children = (DataAssociation) root.get("children");
        } catch (ClassCastException e) {
            fail("Children association was not retrieved properly.");
        }

        children.addOrder("id");
        DataObject child;

        if (children.next()) {
            child = children.getDataObject();
            assertEquals("Id was not retrieved correctly " +
                         "from children association.",
                         new BigDecimal(1),
                         child.get("id")
                         );
            assertEquals("Name was not retrieved correctly " +
                         "from children association.",
                         "bin",
                         child.get("name"));
        } else {
            fail("Not enouch objects in the children association.");
        }

        if (children.next()) {
            child = children.getDataObject();
            assertEquals("Id was not retrieved correctly " +
                         "from children association.",
                         new BigDecimal(2),
                         child.get("id")
                         );
            assertEquals("Name was not retrieved correctly " +
                         "from children association.",
                         "etc",
                         child.get("name"));
        } else {
            fail("Not enouch objects in the children association.");
        }

        if (children.next()) {
            child = children.getDataObject();
            assertEquals("Id was not retrieved correctly " +
                         "from children association.",
                         new BigDecimal(3),
                         child.get("id")
                         );
            assertEquals("Name was not retrieved correctly " +
                         "from children association.",
                         "usr",
                         child.get("name"));
        } else {
            fail("Not enouch objects in the children association.");
        }
        children.close();
    }

    public void testIsNew() {
        DataObject child = getSession().create(getModelName() + ".Node");

        assertTrue("isNew() failed.", child.isNew());

        child.set("id", new BigDecimal(1));
        child.set("name", "child");
        child.get("parent");

        assertTrue("isNew() failed.", child.isNew());
    }

    private DataObject createNode(BigDecimal id, String name) {
        DataObject node = getSession().create(getModelName() + ".Node");
        node.set("id", id);
        node.set("name", name);
        return node;
    }

    private DataObject createNode(BigDecimal id, String name, DataObject parent) {
        DataObject node = createNode(id, name);
        node.set("parent", parent);
        return node;

    }

    private DataObject createNode(int id, String name) {
        return createNode(new BigDecimal(id), name);
    }

    private DataObject createNode(int id, String name, DataObject parent) {
        DataObject node = createNode(id, name);
        node.set("parent", parent);
        return node;

    }

}
