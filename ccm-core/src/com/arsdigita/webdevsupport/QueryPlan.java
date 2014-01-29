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
package com.arsdigita.webdevsupport;

import com.arsdigita.db.ConnectionManager;
import com.arsdigita.db.Sequences;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

/**
 *
 * @author Aram Kananov (akananov@redhat.com)
 * @version 1.0
 */

public class QueryPlan {

    private static final Logger s_log = Logger.getLogger(QueryPlan.class);

    public static boolean planTableExists() {
        Session session = SessionManager.getSession();
        DataQuery query =
            session.retrieveQuery(
                                  "com.arsdigita.webdevsupport.planTableExists"
                                  );
        return query.size() == 1;
    }


    public static String getQueryPlanID(String sqlText) {
        String planID = getNewQueryPlanID();
        generateQueryPlan (sqlText, planID);
        return planID;
    }


    public static void deleteQueryPlan(String planID) {
        DataOperation operation =
            SessionManager.getSession().retrieveDataOperation(
                                                              "com.arsdigita.webdevsupport.deleteQueryPlan"
                                                              );
        operation.setParameter("planID", planID);
        operation.execute();
    }

    private static  void generateQueryPlan(String sqlText, String planID) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = ConnectionManager.getCurrentThreadConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate
                ("explain plan set statement_id = '" + planID +
                 "' for " + sqlText);
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        } finally {
            try {
                if (stmt != null) { stmt.close(); }
            } catch (SQLException e2) {
                throw new UncheckedWrapperException(e2);
            }
        }
    }

    private static String getNewQueryPlanID () {
        try {
            return "ACS-query-"+ Sequences.getNextValue();
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

}
