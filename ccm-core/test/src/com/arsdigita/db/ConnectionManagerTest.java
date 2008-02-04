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

import com.arsdigita.persistence.SessionManager;
import java.sql.SQLException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConnectionManagerTest extends TestCase {

    public static final String versionId = "$Id: ConnectionManagerTest.java 743 2005-09-02 10:37:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private String ORACLE_DATE_QUERY = "select sysdate from dual";
    private String POSTGRES_DATE_QUERY = "select 'now'::timestamp";

    private static java.sql.Connection conn = null;

    public ConnectionManagerTest(String name) {
        super(name);
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(ConnectionManagerTest.class);
    }

    protected void setUp() {

    }

    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ConnectionManagerTest("testGetConnection"));
        return suite;
    }

    public void testGetConnection() throws SQLException {
        conn = ConnectionManager.getConnection();

        try {
            assertNotNull(conn);

            String dateQuery = null;
            if (SessionManager.getSession().getDatabase() == DbHelper.DB_POSTGRES) {
                dateQuery = POSTGRES_DATE_QUERY;
            } else {
                dateQuery = ORACLE_DATE_QUERY;
            }

            java.sql.PreparedStatement stmt =
                conn.prepareStatement(dateQuery);
            try {
                java.sql.ResultSet rs = stmt.executeQuery();
                try {
                    if (rs.next()) {
                        String date = rs.getString(1);
                        assertNotNull(date);
                    } else {
                        fail("Empty result set from sysdate query");
                    }
                } finally {
                    rs.close();
                }
            } finally {
                stmt.close();
            }
        } finally {
            ConnectionManager.returnConnection(conn);            
        }
    }
}
