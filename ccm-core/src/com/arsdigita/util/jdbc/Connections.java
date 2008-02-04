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
package com.arsdigita.util.jdbc;

import com.arsdigita.db.DbHelper;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Classes;
import com.arsdigita.util.UncheckedWrapperException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * A collection of static utility methods for dealing with JDBC
 * connections.
 *
 * @author Justin Ross
 */
public final class Connections {
    public static final String versionId =
        "$Id: Connections.java 287 2005-02-22 00:29:02Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(Connections.class);

    /**
     * Acquires a single connection using <code>url</code>.  This
     * method takes care of loading the appropriate driver and turning
     * auto-commit off.
     *
     * @param url A <code>String</code> JDBC URL
     */
    public static final Connection acquire(final String url) {
        Assert.exists(url);

        final int database = DbHelper.getDatabaseFromURL(url);

        try {
            switch (database) {
            case DbHelper.DB_POSTGRES:
                Classes.loadClass("org.postgresql.Driver");
                break;
            case DbHelper.DB_ORACLE:
                Classes.loadClass("oracle.jdbc.driver.OracleDriver");
                break;
            }

            final Connection conn = DriverManager.getConnection(url);

            Assert.exists(conn, Connection.class);

            conn.setAutoCommit(false);

            // This is a workaround for a bug in certain versions of
            // oracle that cause oracle to erroneously report parse
            // errors or 0600 errors when a UNION ALL is used in a
            // subquery.
            DatabaseMetaData meta = conn.getMetaData();
            String product = meta.getDatabaseProductName();
            String version = meta.getDatabaseProductVersion();
            if ("Oracle".equals(product) &&
                (version.indexOf("9.0.1") != -1 ||
                 version.indexOf("9.2.0.1.0") != -1)) {
                final Statement stmt = conn.createStatement();
                stmt.execute
                    ("alter session set \"_push_join_union_view\" = false");
                stmt.close();
            }

            return conn;
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }
}
