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
package com.arsdigita.core;

import com.arsdigita.db.DbHelper;
import com.arsdigita.packaging.ConfigRegistry;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.runtime.ScriptContext;
import com.arsdigita.util.jdbc.Connections;
import java.sql.Connection;

/**
 * DBCheck
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/

public class DBCheck extends BaseCheck {

    public final static String versionId = "$Id: DBCheck.java 736 2005-09-01 10:46:05Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private boolean checkOracleJDBC() {
        final String classname = "oracle.jdbc.driver.OracleDriver";

        if (isClassFound(classname)) {
            m_out.println(message("oracle_jdbc_found"));

            checkDuplicates(classname);
            return true;
        } else {
            m_out.println(message("oracle_jdbc_not_found"));
            m_out.println();
            m_out.println(message("classpath"));
            return false;
        }
    }

    private boolean checkDatabaseConnection() {
        if (m_verbose) {
            m_out.println("Checking that RuntimeConfig is configured");
        }

        ConfigRegistry reg = new ConfigRegistry();
        if (reg.isConfigured(RuntimeConfig.class)) {
            m_out.println(message("runtime_configured"));
        } else {
            m_out.println(message("runtime_unconfigured"));
            return false;
        }

        if (m_verbose) {
            m_out.println("Checking that JDBC URL is set");
        }

        final String url = RuntimeConfig.getConfig().getJDBCURL();
        if (url == null) {
            m_out.println(message("jdbc_unset"));
            return false;
        } else {
            m_out.println(message("jdbc_set"));
        }
        Connection conn = null;
        String error = null;

        if (m_verbose) {
            m_out.println("Connecting to JDBC URL " + url);
        }

        try {
            conn = Connections.acquire(url);
        } catch (Exception e) {
            error = e.getMessage();
        }

        if (conn == null) {
            if (m_verbose) {
                m_out.println("Cannot connect");
            }

            m_out.println(message("db_down"));

            m_out.println("ERROR: " + error);
            return false;
        } else {
            if (m_verbose) {
                m_out.println("Connection succesful");
            }

            m_out.println(message("db_up"));
            return true;
        }
    }

    private boolean checkSupportedDatabase(int db) {
        if ( db == DbHelper.DB_DEFAULT ) {
            m_out.println(message("unsupported_database"));
            if (m_verbose) {
                m_out.println(RuntimeConfig.getConfig().getJDBCURL());
            }
            return false;
        }
        m_out.println(message("supported_database"));
        return true;
    }

    public void run(ScriptContext ctx) {
        int db = DbHelper.getDatabaseFromURL(RuntimeConfig.
                                             getConfig().getJDBCURL());

        if ( ! checkSupportedDatabase(db) ) {
            status(FAIL);
        } else {
            if ( db == DbHelper.DB_POSTGRES ) {
                if ( checkDatabaseConnection() ) {
                    status(PASS);
                } else {
                    status(FAIL);
                }
            } else {
                if (checkOracleJDBC() && checkDatabaseConnection()) {
                    status(PASS);
                } else {
                    status(FAIL);
                }
            }
        }
    }

}
