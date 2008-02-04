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

import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TestTransaction;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Testlet
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 **/

public abstract class Testlet {

    public final static String versionId = "$Id: Testlet.java 745 2005-09-02 10:50:34Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger LOG = Logger.getLogger(Testlet.class);

    protected static final int KEY = 0x1;
    protected static final int ATTRIBUTE = 0x2;
    protected static final int ROLE = 0x4;
    protected static final int OPTIONAL = 0x8;
    protected static final int REQUIRED = 0x10;
    protected static final int COLLECTION = 0x20;
    protected static final int COMPONENT = 0x40;
    protected static final int COMPOSITE = 0x80;

    protected static final boolean test(Property prop, final int flags) {
        if ((KEY & flags) > 0 && prop.isKeyProperty()) {
            return true;
        } else if ((ATTRIBUTE & flags) > 0 &&
                   prop.isAttribute()) {
            return true;
        } else if ((ROLE & flags) > 0 &&
                   prop.isRole()) {
            return true;
        } else if ((OPTIONAL & flags) > 0 &&
                   prop.isNullable()) {
            return true;
        } else if ((REQUIRED & flags) > 0 &&
                   prop.isRequired()) {
            return true;
        } else if ((COLLECTION & flags) > 0 &&
                   prop.isCollection()) {
            return true;
        } else if ((COMPONENT & flags) > 0 &&
                   prop.isComponent()) {
            return true;
        } else if ((COMPOSITE & flags) > 0 &&
                   prop.isComposite()) {
            return true;
        }

        return false;
    }

    protected static final ObjectTree makeTree(ObjectType type, int include,
                                         int exclude, int depth) {
        ObjectTree result = new ObjectTree(type);
        addPaths(result, include, exclude, depth);
        return result;
    }

    protected static final void addPaths(ObjectTree tree, int include,
                                         int exclude, int depth) {
        ObjectType type = tree.getObjectType();
        for (Iterator it = type.getProperties(); it.hasNext(); ) {
            Property prop = (Property) it.next();
            if (test(prop, include) && !test(prop, exclude)) {
                tree.addPath(prop.getName());
                if (prop.isRole()) {
                    ObjectTree subtree = tree.getSubtree(prop.getName());
                    if (depth > 0) {
                        addPaths(subtree, include, exclude, depth - 1);
                    } else {
                        addPaths(subtree, REQUIRED, exclude, 0);
                    }
                }
            }
        }
    }

    private static final boolean isCircular(ObjectTree tree, Property prop) {
        if (tree.getObjectType().equals(prop.getType())) {
            return true;
        } else {
            ObjectTree parent = tree.getParent();
            if (parent == null) {
                return false;
            } else {
                return isCircular(parent, prop);
            }
        }
    }

    protected static final void verify(DataObject data, ObjectTree tree,
                                       DataSource ds) {
        TestTransaction.testCommitTxn
            (SessionManager.getSession().getTransactionContext());
        verifyRecursive(data, tree, ds);
    }

    private static final void verifyRecursive(DataObject data, ObjectTree tree,
                                              DataSource ds) {
        if (data == null) {
            Assert.fail("Null data object for " + tree.getAbsolutePath() +
                        ", expected: " + ds.getOID(tree) +
                        ", tree: " + tree.getRoot());
        }

        LOG.warn("Comparing " + data.getOID() + " with tree:\n" + tree +
                 " against the following datasource: " + ds.getKey());
        for (Iterator it = tree.getAttributes().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();

            Assert.assertEquals(ds.getTestData(tree, prop.getName()),
                                data.get(prop.getName()),
                                tree.getAbsolutePath(prop.getName()),
                                tree.getAbsolutePath(prop.getName()));
        }

        for (Iterator it = tree.getSubtrees().iterator(); it.hasNext(); ) {
            ObjectTree subtree = (ObjectTree) it.next();

            verifyRecursive
                ((DataObject) data.get(subtree.getName()), subtree, ds);
        }
    }

    protected static final DataObject create(ObjectTree tree, DataSource ds) {
        OID oid = ds.getOID(tree);

        Session ssn = SessionManager.getSession();
        LOG.warn("Creating " + oid + " for " + tree.getAbsolutePath());
        DataObject result = ssn.create(oid);

        for (Iterator it = tree.getAttributes().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();

            if (!prop.isKeyProperty()) {
                Object td = ds.getTestData(tree, prop.getName());
                LOG.warn("Setting " +
                         tree.getAbsolutePath(prop.getName()) + " to " + td);
                result.set(prop.getName(), td);
            }
        }

        for (Iterator it = tree.getSubtrees().iterator(); it.hasNext(); ) {
            ObjectTree subtree = (ObjectTree) it.next();
            Property prop = tree.getProperty(subtree.getName());

            DataObject data = create(subtree, ds);
            data.save();

            if (prop.isCollection()) {
                LOG.warn("Adding " + data.getOID() +
                         " to " + tree.getAbsolutePath(prop.getName()));
                DataAssociation da =
                    (DataAssociation) result.get(prop.getName());
                da.add(data);
            } else {
                LOG.warn("Setting " +
                         tree.getAbsolutePath(prop.getName()) + " to " +
                         data.getOID());
                result.set(prop.getName(), data);
            }
        }

        return result;
    }

    protected static final void update(DataObject data, ObjectTree tree,
                                       DataSource ds) {
        for (Iterator it = tree.getAttributes().iterator(); it.hasNext(); ) {
            Property prop = (Property) it.next();

            if (!prop.isKeyProperty()) {
                data.set(prop.getName(),
                         ds.getTestData(tree, prop.getName()));
            }
        }

        for (Iterator it = tree.getSubtrees().iterator(); it.hasNext(); ) {
            ObjectTree subtree = (ObjectTree) it.next();
            Property prop = tree.getProperty(subtree.getName());

            DataObject child = (DataObject) data.get(prop.getName());

            for (Iterator attrs = subtree.getAttributes().iterator();
                 attrs.hasNext(); ) {
                Property key = (Property) attrs.next();
                // assuming noncompound key
                if (key.isKeyProperty()) {
                    Object id = ds.getTestData(subtree, key.getName());
                    child = data.getSession().retrieve
                        (new OID(child.getOID().getObjectType(), id));
                    if (child == null) {
                        child = create(subtree, ds);
                        child.save();
                    }

                    data.set(prop.getName(), child);
                    data.save();
                    break;
                }
            }

            update(child, subtree, ds);
            child.save();
        }
    }

    public abstract void run();

}
