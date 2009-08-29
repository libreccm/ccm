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

import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.arsdigita.db.DbHelper;

/**
 * LobTest - for testing Blob and Clob datatype.
 *
 * @author Jeff Teeters 
 * @version $Id: LobTest.java 745 2005-09-02 10:50:34Z sskracic $
 */

public class LobTest extends PersistenceTestCase {

    private Logger s_cat =
        Logger.getLogger(LobTest.class);

    private static final int HI = 255;
    private static final int LO = 1;
    private static final byte [] sourceBytes = new byte[HI-LO+1];
    static {
        for (int i = 0; i <= HI-LO; i++) {
            sourceBytes[i] = new Long(i+LO).byteValue();
        }
    }

    public LobTest(String name) {
        super(name);
    }

    protected void persistenceSetUp() {
        load("com/arsdigita/persistence/testpdl/mdsql/Datatype.pdl");
        super.persistenceSetUp();
    }

    protected void persistenceTearDown() {
        load("com/arsdigita/persistence/testpdl/mdsql/Datatype.pdl");
        super.persistenceTearDown();
    }

    private int getDatabase() {
        return DbHelper.getDatabase(getSession().getConnection());
    }

    public void testDoNothing() {
        // no-op to avoid 'no tests found' warning message if
        // all the other tests in this class are flagged with FAILS.
    }

    /***
     * Test writing large blobs
     ***/
    public void testLargeBlobs() {
        runLobTest("blob", 5000000);
        runLobTest("blob", 4000);
        runLobTest("blob", 2000);
    }

    /***
     * Test writing large clobs
     ***/
    public void testLargeClobsAscending() {
        // make sure a few different values are tested
        // even if the first try succeeds
        runLobTest("clob", 2000);
        runLobTest("clob", 3000);
        runLobTest("clob", 3999);
        runLobTest("clob", 4000);
        runLobTest("clob", 4001);
        runLobTest("clob", 1000000);
    }


    /***
     * Test writing large clobs
     ***/
    public void testLargeClobsDescending() {
        // make sure a few different values are tested
        // even if the first try succeeds
        runLobTest("clob", 1000000);
        runLobTest("clob", 4001);
        runLobTest("clob", 4000);
        runLobTest("clob", 3999);
        runLobTest("clob", 3000);
        runLobTest("clob", 2000);
    }

    public void testLargeClobsDatabaseSpecificSyntax() {
        String db = null;
        if (getDatabase() == DbHelper.DB_POSTGRES) {
            db = "postgres";
        } else {
            db = "oracle";
        }
        // not bothering with the binary search, it isn't reliable anyway...
        runLobTest(db + "clob", 1000000);
        runLobTest(db + "clob", 4001);
        runLobTest(db + "clob", 4000);
        runLobTest(db + "clob", 3999);
        runLobTest(db + "clob", 3000);
        runLobTest(db + "clob", 2000);
    }

    /***
     * Test Java's handling of *large* strings, to make sure
     * failures in the other two tests aren't due to JVM stuff.
     ***/
    public void testLargeStrings() {
        runLobTest("string (Just Java, no Oracle/Postgres/DB)", 1000000);
    }


    /**
     * Test creating blob or clob.
     * @param lobType either "blob" or "clob"
     */
    public void runLobTest(String lobType, int largeSize) {
        s_cat.info("Starting test of large " + lobType);
        if (sizeWorks(largeSize, lobType)==null) {
            s_cat.info(lobType + " test passed with size=" + largeSize);
            return;   // passes test
        }
        // Fails test with large size.  Use binary search to find
        // size at which it breaks
        int high = largeSize;
        int mid;
        int low  = 1;
        int count = 0;
        String errMsg;
        while (high - low > 1 && count < 30) {
            mid = (high + low) / 2;
            s_cat.info("Testing size " + mid + "...");
            if((errMsg=sizeWorks(mid, lobType)) == null) {
                s_cat.info(" passed.");
                low = mid;
            } else {
                s_cat.info(" failed: "+ errMsg);
                high = mid;
            }
            count++;
        }
        s_cat.error(count + " steps.  Failed storing large " + lobType +
                    ".  Low (size worked) = " + low +
                    ".  High (size failed) = " + high + ".");
        fail(count + " steps.  Failed storing large " + lobType +
             ".  Low (size worked) = " + low +
             ".  High (size failed) = " + high + ".");
    }


    // Support routine for above test.  Returns errMsg or null if ok.
    private String sizeWorks(int size, String lobType) {
        if (lobType.equals("blob")){
            return runBlobTest(size);
        } else if (lobType.equals("clob")) {
            return runClobTest(size);
        } else if (lobType.equals("oracleclob") ||
                   lobType.equals("postgresclob")) {
            return runClobDatabaseSpecificTest(size);
        } else {
            return runStringTest(size);
        }
    }


    // Test saving blob.  Return null if works, error message if not
    private String runBlobTest(int size) {
        try {
            // Create a string of random bytes
            byte [] testBytes = new byte[size];
            int i;
            for (i = 0; i < size; i++) {
                // no longer using random so that the data can be checked
                // against a known pattern.
                testBytes[i] = sourceBytes[i % (HI-LO+1)];
            }

            DataObject dt = getSession().create("examples.Datatype");
            dt.set("id", BigInteger.ZERO);
            dt.set("blob", testBytes);
            dt.save();

            dt = getSession().retrieve(new OID("examples.Datatype", BigInteger.ZERO));
            byte[] foundBytes = (byte[]) dt.get("blob");
            dt.delete();

            if (foundBytes.length != testBytes.length) {
                return "Length mismatch.  Found length=" + foundBytes.length;
            } else if (foundBytes.length == 0) {
                return "Length matches, but is zero.";
            }
            for (i = 0; i < foundBytes.length; i++)
                if (foundBytes[i] != testBytes[i])
                    return "Mismatch at byte " + i;
            return null;
        } catch (Exception e) {
            String msg = e.toString();
            // with large binds, this error msg can be huge
            if (msg.length() > 500) {
                msg = msg.substring(0,500) + "...";
            }
            s_cat.error("Database error when testing with size =" + size, e);
            return "Database error when testing with size =" + size +
                ". :" + msg;
        }
    }


    // Test saving clob.  Return null if works, error message if not
    private String runClobTest(int size) {
        try {
            // Create a string of random bytes
            byte [] testBytes = new byte[size];
            int i;
            for (i = 0; i < size; i++) {
                // no longer using random so that the data can be checked
                // against a known pattern.
                testBytes[i] = sourceBytes[i % (HI-LO+1)];
            }
            String testString = new String(testBytes);

            DataObject dt = getSession().create("examples.Datatype");
            dt.set("id", BigInteger.ZERO);
            dt.set("clob", testString);
            dt.save();

            dt = getSession().retrieve(new OID("examples.Datatype", BigInteger.ZERO));
            String foundString = (String) dt.get("clob");
            dt.delete();

            if (foundString.length() != testString.length()) {
                return "Length mismatch.  Found length=" + foundString.length();
            }
            for (i = 0; i < foundString.length() - 1; i++) {
                if (foundString.charAt(i) != testString.charAt(i)) {
                    return "Mismatch at character " + i;
                }
            }
            return null;
        } catch (Exception e) {
            String msg = e.toString();
            // with large binds, this error msg can be huge
            if (msg.length() > 500) {
                msg = msg.substring(0,500) + "...";
            }
            s_cat.error("Database error when testing with size =" + size, e);
            return "Database error when testing with size =" + size +
                ". :" + msg;
        }
    }

    // Test manipulating a large string; doesn't actually hit the DB with it.
    private String runStringTest(int size) {
        // Create a string of random chars
        char [] testChars = new char[size];
        int i;
        for (i = 0; i < size; i++)
            testChars[i] =  // Should be an easier way to get random chars
                (char)
                ((new Long(Math.round(Math.floor(Math.random() * 256.0))))
                 .byteValue());

        String testString = new String(testChars);

        if (testString.length() != size) {
            return "Length mismatch.  Found length=" + testString.length();
        }

        for (i = 0; i < testString.length(); i++) {
            if (testString.charAt(i) != testChars[i]) {
                return "Mismatch at character " + i + " (" +
                    testChars[i] + " vs. " + testString.charAt(i) + ")";
            }
        }

        return null;
    }


    private String runClobDatabaseSpecificTest(int size) {
        try {
            // Create a string of random bytes
            byte [] testBytes = new byte[size];
            int i;
            for (i = 0; i < size; i++) {
                // no longer using random so that the data can be checked
                // against a known pattern.
                testBytes[i] = sourceBytes[i % (HI-LO+1)];
            }
            String testString = new String(testBytes);

            Connection conn = getSession().getConnection();

            if (getDatabase() == DbHelper.DB_POSTGRES) {
                executePostgresUpdate(conn, testString, size);
            } else {
                executeOracleUpdate(conn, testString, size);
            }

            // using ids that aren't all 0 because explicit use of Connection
            // object writes behind persistence cache [ashah]
            DataObject dt = getSession().retrieve
                (new OID("examples.Datatype", BigInteger.valueOf(size)));

            String foundString = (String) dt.get("clob");
            dt.delete();

            if (foundString.length() != testString.length()) {
                return "Length mismatch.  Found length=" + foundString.length();
            }
            for (i = 0; i < foundString.length(); i++) {
                if (foundString.charAt(i) != testString.charAt(i)) {
                    return "Mismatch at character " + i;
                }
            }
            return null;

        } catch (Exception e) {
            String msg = e.toString();
            // with large binds, this error msg can be huge
            if (msg.length() > 500) {
                msg = msg.substring(0,500) + "...";
            }
            String dbName = null;
            if (getDatabase() == DbHelper.DB_POSTGRES) {
                dbName = "Postgres";
            } else {
                dbName = "Oracle";
            }
            s_cat.error(dbName + " error when testing with size =" + size, e);
            return dbName + " error when testing with size =" + size +
                ". :" + msg;
        }
    }


    private void executeOracleUpdate(Connection conn, String testString,
                                     int id)
        throws java.sql.SQLException, java.io.IOException, IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        PreparedStatement ps =
            conn.prepareStatement("insert into t_datatypes  (id, j_clob) " +
                                  "values  (?, EMPTY_CLOB())");
        try {
            ps.setBigDecimal(1, new BigDecimal(BigInteger.valueOf(id)));
            ps.executeUpdate();
        } finally {
            ps.close();
        }

        ps = conn.prepareStatement("select j_clob from t_datatypes " +
                                   "where id = ? for update");

        try {
            ps.setBigDecimal(1, new BigDecimal(BigInteger.valueOf(id)));
            ResultSet rs = ps.executeQuery();
            rs.next();
            Clob clob = rs.getClob(1);
            Writer char_stream = (Writer)clob.getClass().getMethod("getCharacterOutputStream", new Class[0]).invoke(clob);
            char_stream.write(testString);
            char_stream.flush();
            char_stream.close();
            rs.close();
        } finally {
            ps.close();
        }
    }


    private void executePostgresUpdate(Connection conn, String testString,
                                       int id)
        throws java.sql.SQLException, java.io.IOException {
        PreparedStatement ps =
            conn.prepareStatement("insert into t_datatypes " +
                                  "(id, j_clob  ) values (? , ?)");
        try {
            ps.setBigDecimal(1, new BigDecimal(BigInteger.valueOf(id)));
            ps.setString(2, testString);
            ps.executeUpdate();
        } finally {
            ps.close();
        }
    }
}
