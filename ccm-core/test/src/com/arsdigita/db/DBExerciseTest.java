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
package com.arsdigita.db;

import com.arsdigita.persistence.DedicatedConnectionSource;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.runtime.RuntimeConfig;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This test exists to exercise the database and the sundry JDBC
 * methods.  It should ultimately be extended to include most or
 * all such methods.
 *
 * @author Kevin Scaldeferri
 */


public class DBExerciseTest extends TestCase {

    public static final String versionId = "$Id: DBExerciseTest.java 748 2005-09-02 11:57:31Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static java.sql.Connection conn;

    private static final String dirRoot =
        System.getProperty("test.base.dir") +
        "/com/arsdigita/db/";
    private static final String blobFileName = "adlogo.gif";

    public DBExerciseTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(DBExerciseTest.class);
    }

    static void setupSession() {
        final String key = "default";
        String url = RuntimeConfig.getConfig().getJDBCURL();
        final MetadataRoot root = MetadataRoot.getMetadataRoot();
        SessionManager.configure(key, root, new DedicatedConnectionSource(url));
    }

    public static Test suite() throws SQLException {
        TestSuite suite = new TestSuite();
        suite.addTest(new DBExerciseTest("testBlob"));

        TestSetup wrapper = new TestSetup(suite) {
                public void setUp() throws SQLException {
                    //setupSession();
                    conn = SessionManager.getSession().getConnection();
                    java.sql.PreparedStatement tableStmt = null;

                    if (SessionManager.getSession().getDatabase() == DbHelper.DB_POSTGRES) {
                        tableStmt = conn.prepareStatement
                            ("create table db_test (\n" +
                             "    theId          integer primary key,\n" +
                             "    aBlob          bytea\n" +
                             ")");
                    } else {
                        tableStmt = conn.prepareStatement
                            ("create table db_test (\n" +
                             "    theId          integer primary key,\n" +
                             "    aBlob          blob\n" +
                             ")");
                    }
                    tableStmt.executeUpdate();
                    tableStmt.close();
                }

                public void tearDown() throws SQLException {
                    java.sql.PreparedStatement stmt;
                    stmt = conn.prepareStatement("drop table db_test");
                    stmt.executeUpdate();
                    stmt.close();
                }

            };

        return wrapper;
    }

    public static Test makeWrapper() throws SQLException
    {
        return suite();
    }
    public void testBlob() {
        try {
            java.sql.PreparedStatement blobInsertStmt =
                conn.prepareStatement("insert into db_test\n" +
                                      "(theId, aBlob)\n" +
                                      "values\n" +
                                      "(?,?)");

            // might not be the right location
            File blobFile = new File(dirRoot, blobFileName);
            long fileSize = blobFile.length();
            byte[] blobBytes = new byte[(int) fileSize];

            DataInputStream in = new DataInputStream(new FileInputStream(blobFile));
            in.readFully(blobBytes);
            in.close();

            blobInsertStmt.setInt(1, 1);
            blobInsertStmt.setBytes(2,blobBytes);
            blobInsertStmt.executeUpdate();
            blobInsertStmt.close();

            java.sql.PreparedStatement blobRetrieveStmt =
                conn.prepareStatement("select aBlob\n" +
                                      "from db_test\n" +
                                      "where theId = 1");

            java.sql.ResultSet rs = blobRetrieveStmt.executeQuery();

            if (rs.next()) {
                long size = 0;
                if (DbHelper.getDatabase() == DbHelper.DB_POSTGRES) {
                    size = (new Integer(rs.getBytes(1).length)).longValue();
                } else {
                    java.sql.Blob blob = rs.getBlob(1);
                    size = blob.length();
                }
                assertEquals(fileSize, size);
            } else {
                fail("Didn't find row we just inserted");
            }
            rs.close();
            blobRetrieveStmt.close();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
