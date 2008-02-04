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
package com.arsdigita.db;


import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

public class DbHelper {

    // Only static methods in this class
    private DbHelper() {}

    private static Logger s_log = Logger.getLogger(DbHelper.class);

    public static final int DB_DEFAULT = 0;
    public static final int DB_ORACLE = 1;
    public static final int DB_POSTGRES = 2;

    public static final int DB_MAX = DB_POSTGRES;

    private static int s_database = DB_DEFAULT;

    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Sets the database type. The parameter should be one of the
     * constants specified in this file.
     */
    public static void setDatabase(int database) {
        Assert.assertTrue((database >= DB_DEFAULT) &&
                          (database <= DB_MAX));

        s_database = database;

        s_log.info("Setting database to " + getDatabaseName(database));
    }

    /**
     *  This will return the type of database that is being used by
     *  the system.  It will return DB_DEFAULT if no database has been
     *  specified.  Otherwise, it will return an int corresponding to
     *  one of the database constants defined in this file.
     */
    public static int getDatabase() {
        return s_database;
    }

    public static int getDatabase(DatabaseMetaData md) {
        try {
            return getDatabaseFromURL(md.getURL());
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    public static int getDatabase(Connection conn) {
        try {
            return getDatabase(conn.getMetaData());
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    public static int getDatabase(Statement stmt) {
        try {
            return getDatabase(stmt.getConnection());
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    public static int getDatabase(ResultSet rs) {
        try {
            return getDatabase(rs.getStatement());
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    /**
     * Gets the directory name for the current database
     */
    public static String getDatabaseDirectory() {
        return getDatabaseDirectory(s_database);
    }

    /**
     * Gets the directory name to be used for database specific
     * files
     */
    public static String getDatabaseDirectory(int database) {
        Assert.assertTrue((database >= DB_DEFAULT) &&
                          (database <= DB_MAX));

        switch (database) {
        case DB_ORACLE:
            s_log.info("ORACLE");
            return "oracle-se";
        case DB_POSTGRES:
            s_log.info("POSTGRES");
            return "postgres";
        default:
            return "default";
        }
    }

    private static final String[] DB_SUFFIXES = { "ora", "pg" };

    /**
     * Gets the filename suffix used to distinguish between resource files
     * based on database.
     **/

    public static String getDatabaseSuffix() {
        return getDatabaseSuffix(s_database);
    }

    /**
     * Gets the filename suffix used to distinguish between resource files
     * based on database.
     **/

    public static String getDatabaseSuffix(int database) {
        switch (database) {
        case DB_ORACLE:
            return DB_SUFFIXES[0];
        case DB_POSTGRES:
            return DB_SUFFIXES[1];
        default:
            return null;
        }
    }

    public static String[] getDatabaseSuffixes() {
        return DB_SUFFIXES;
    }


    /**
     * Parses the JDBC url to determine the database
     * type, will return DB_DEFAULT if no supported
     * database is determined.
     */
    public static int getDatabaseFromURL(String url) {
        if (!url.startsWith("jdbc:")) {
            throw new IllegalArgumentException("JDBC URL " +
                                       url + " doesn't start with jdbc:");
        }

        int pos = url.indexOf(":", 5);

        if (pos == -1) {
            throw new IllegalArgumentException("JDBC URL " + url +
                                       " is not of the form jdbc:[dbname]:xyz");
        }

        String driver = url.substring(5, pos);
        s_log.debug("Got driver name " + driver, new  Throwable());

        if ("oracle".equals(driver)) {
            return DB_ORACLE;
        } else if ("postgresql".equals(driver)) {
            return DB_POSTGRES;
        } else {
            return DB_DEFAULT;
        }
    }

    /**
     * Gets the pretty name for a given database integer
     * identifier.
     */
    public static String getDatabaseName(int database) {
        Assert.assertTrue((database >= DB_DEFAULT) &&
                          (database <= DB_MAX));

        switch (database) {
        case DB_ORACLE:
            return "Oracle SE";
        case DB_POSTGRES:
            return "PostgreSQL";
        default:
            return "Default";
        }
    }


    /**
     * Convenience method for throwing a DbUnsupportedException
     * filling in the message for the current database type.
     */
    public static void unsupportedDatabaseError(String operation) {
        throw new DbUnsupportedException("Database " +
                                         DbHelper.getDatabaseName(s_database) +
                                         " does not support " + operation);
    }

    /**
     * Returns the width of the VARCHAR column required to store
     * <code>str</code> in the database.
     *
     * <p>This abstracts the differences in the interpretation of, say,
     * VARCHAR(100) in Oracle and Postgres. In Oracle, this means 100
     * bytes. Therefore, a 100-character long string may not fit in a
     * VARCHAR(100) column in Oracle, depending on the particular encoding used.
     * In Postgres, VARCHAR(100) means 100 characters.</p>
     *
     * @return 1 if <code>str</code> is <code>null</code>; otherwise a
     * db-specific positive value.
     **/
    public static int varcharLength(String str) {
        if ( str == null || "".equals(str) ) return 1;

        /**
         * See change 30544.  See also
         * http://post-office.corp.redhat.com/archives/ccm-engineering-list/2003-May/msg00016.html
         * (that is Message-ID: <20030502111749.GB1867@tuborg>, in case the URL
         * changes)
         * See also Dan's followup at
         * http://post-office.corp.redhat.com/archives/ccm-engineering-list/2003-May/msg00017.html
         **/
        int result = 0;
        switch (getDatabase()) {
        case DB_POSTGRES:
            result = str.length();
            break;
        case DB_ORACLE:
            try {
                result = str.getBytes(DEFAULT_ENCODING).length;
            } catch (java.io.UnsupportedEncodingException ex) {
                throw new UncheckedWrapperException
                    (DEFAULT_ENCODING + " not supported by JRE", ex);
            }
            break;
        default:
            DbHelper.unsupportedDatabaseError("varcharLength");
        }

        if ( result == 0 ) return 1;

        return result;
    }

    /**
     * Truncate a string to a specified length, respecting character
     * boundaries.
     *
     * @param s The string to be truncated.
     * @param maxLength The maximum length of the string, in units that
     * are database-dependent (for PG, characters; for Oracle, bytes).
     *
     * @see #varcharLength(String)
     */
    public static String truncateString(String s, int maxLength) {
        String result = null;

        switch (getDatabase()) {
        case DB_POSTGRES:
            result = s.substring(0, maxLength-1);
            break;
        case DB_ORACLE:
            byte sBytes[] = s.getBytes();
            byte sTruncateBytes[] = new byte[maxLength];

            // Truncate based on bytes, and construct a new string
            System.arraycopy(sBytes, 0, sTruncateBytes, 0, maxLength);
            String truncateString = new String(sTruncateBytes);

            // New string might have partially truncated a multi-byte
            // character, so we drop the last character. Note that this is
            // conservative, and in some cases the last character is a
            // legitimate multi-byte character and is dropped
            // anyway. However, implementing a completely correct solution
            // would require the use of BreakIterator.following and
            // therefore be an O(N) solution (I think).
            result = truncateString.substring(0,truncateString.length()-1);
            break;
        default:
            DbHelper.unsupportedDatabaseError("varcharLength");
        }

        return result;

    }

}
