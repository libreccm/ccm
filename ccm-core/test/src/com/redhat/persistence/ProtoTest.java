/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence;

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.DbHelper;
import com.redhat.persistence.engine.rdbms.ConnectionSource;
import com.redhat.persistence.engine.rdbms.OracleWriter;
import com.redhat.persistence.engine.rdbms.PostgresWriter;
import com.redhat.persistence.engine.rdbms.RDBMSEngine;
import com.redhat.persistence.engine.rdbms.SQLWriter;
import com.redhat.persistence.metadata.Adapter;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Property;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.pdl.PDL;
import java.io.FileReader;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import junit.framework.TestCase;

/**
 * ProtoTest
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: ProtoTest.java 740 2005-09-02 10:13:54Z sskracic $
 **/

public class ProtoTest extends TestCase {


    public ProtoTest(String name) {
        super(name);
    }

    private static final String TEST_PDL =
        "test/pdl/com/arsdigita/persistence/Test.pdl";

    public void test() throws Exception {
        Root root = new Root();
        PDL pdl = new PDL();
        pdl.load(new FileReader(TEST_PDL), TEST_PDL);
        pdl.emit(root);

        SQLWriter w;
        switch (DbHelper.getDatabase()) {
        case DbHelper.DB_ORACLE:
            w = new OracleWriter();
            break;
        case DbHelper.DB_POSTGRES:
            w = new PostgresWriter();
            break;
        default:
            DbHelper.unsupportedDatabaseError("proto test");
            w = null;
            break;
        }

        Session ssn = new Session
            (root, new RDBMSEngine
             (new ConnectionSource() {
                public Connection acquire() {
                    try {
                        Connection conn =
                            ConnectionManager.getConnection();
                        conn.setAutoCommit(false);
                        return conn;
                    } catch (SQLException e) {
                        throw new Error(e.getMessage());
                    }
                }
                public void release(Connection conn) {
                    // Do nothing
                }
             }, w), new QuerySource());

        ObjectType TEST = ssn.getRoot().getObjectType("test.Test");
        ObjectType ICLE = ssn.getRoot().getObjectType("test.Icle");
        ObjectType COMPONENT = ssn.getRoot().getObjectType("test.Component");

        Adapter a = new Generic.Adapter();
        ssn.getRoot().addAdapter(Generic.class, a);
	TEST.setJavaClass(Generic.class);
	ICLE.setJavaClass(Generic.class);
	COMPONENT.setJavaClass(Generic.class);

        Generic test = new Generic(TEST, BigInteger.ZERO);
        Property NAME = TEST.getProperty("name");
        Property COLLECTION = TEST.getProperty("collection");
        Property OPT2MANY = TEST.getProperty("opt2many");
        doTest(ssn, test, NAME, COLLECTION);
        doTest(ssn, test, NAME, OPT2MANY);
    }

    private void doTest(Session ssn, Generic obj, Property str, Property col) {
        Property REQUIRED = obj.getType().getProperty("required");
        Generic req = new Generic(REQUIRED.getType(), new BigInteger("10"));
        ssn.create(req);

        ssn.create(obj);
        ssn.set(obj, REQUIRED, req);

        // FIXME: forced this file to compile by commenting out the following line
        //Object obj2 = ssn.retrieve(obj.getType(), obj.getID());
        Object obj2 = null;

//        assertTrue("obj: " + obj + ", obj2: " + obj2, obj == obj2);

        ssn.set(obj, str, "foo");
        assertEquals("foo", ssn.get(obj, str));

        ssn.delete(obj);
        // FIXME: forcing to compile by commenting out
        // assertEquals(null, ssn.retrieve(obj.getType(), obj.getID()));

        ssn.create(obj);
        ssn.set(obj, REQUIRED, req);

        PersistentCollection pc =
            (PersistentCollection) ssn.get(obj, col);

        ObjectType ICLE = ssn.getRoot().getObjectType("test.Icle");
        Object one = new Generic(ICLE, new BigInteger("1"));
        ssn.create(one);
        Object two = new Generic(ICLE, new BigInteger("2"));
        ssn.create(two);
        Object three = new Generic(ICLE, new BigInteger("3"));
        ssn.create(three);

        ssn.add(obj, col, one);
        assertCollection(new Object[] {one}, pc);
        ssn.add(obj, col, two);
        assertCollection(new Object[] {one, two}, pc);
        ssn.add(obj, col, three);
        assertCollection(new Object[] {one, two, three}, pc);
        ssn.remove(obj, col, two);
        assertCollection(new Object[] {one, three}, pc);
        ssn.remove(obj, col, three);
        assertCollection(new Object[] {one}, pc);
        ssn.add(obj, col, three);
        assertCollection(new Object[] {one, three}, pc);

        System.out.println();
        System.out.println("---------------------------------");
        System.out.println();
        ssn.dump();

        ssn.flush();

        System.out.println();
        System.out.println("---------------------------------");
        System.out.println();
        ssn.dump();

        ssn.delete(obj);
        ssn.flush();
        System.out.println();
        System.out.println("---------------------------------");
        System.out.println();
        ssn.dump();

        ssn.rollback();
    }

    private static void assertCollection(Object[] expected,
                                         PersistentCollection actual) {
        HashSet exp = new HashSet();
        for (int i = 0; i < expected.length; i++) {
            exp.add(expected[i]);
        }

        HashSet act = new HashSet();
        DataSet ds = actual.getDataSet();
        Cursor c = ds.getCursor();
        while (c.next()) {
            act.add(c.get());
        }

        assertEquals(exp, act);
    }

}
