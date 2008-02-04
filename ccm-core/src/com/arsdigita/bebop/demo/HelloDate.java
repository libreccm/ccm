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
package com.arsdigita.bebop.demo;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.db.ConnectionManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Logger;

public class HelloDate extends com.arsdigita.bebop.Label {

    public static final String versionId = "$Id: HelloDate.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log =
        Logger.getLogger(HelloDate.class.getName());

    /**
     * Hello-date is a special-case label.
     */
    public HelloDate() {
        // set the label text via a print listener, because we
        // want to query Oracle for the date on each request.
        super(new HelloDatePrintListener());
    }

    private static class HelloDatePrintListener implements PrintListener {
        public void prepare(PrintEvent pevt) {
            Label target = (Label)pevt.getTarget();

            // we're setting the label text dynamically
            // <bebop:label>Hello, tody's date is ...</bebop:label>
            // where the date comes from Oracle's "select
            // sysdate from dual"
            //
            // we're counting on whatever stylesheet we have available
            // knowing what to do with "bebop:label"
            target.setLabel(makeHelloString());
        }

        /**
         * Get the message we want to display from the database
         */
        private String makeHelloString() {
            StringBuffer result = new StringBuffer(100);
            // get sysdate from Oracle
            Connection conn = null;
            try {
                // XXX: you could also use persistence layer
                // here, we're just using JDBC for now.
                // This use of straight JDBC is obsolete, do not
                // use it as a good example.
                conn = ConnectionManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery
                    ("select to_char(sysdate, 'Month dd, yyyy'), "
                     + "to_char(sysdate, 'HH24:MI') from dual");
                // here you would put in "obj.getProperty(...)" instead
                // of directly referencing a result set
                rs.next();
                result.append("Hello!  Today's date is ")
                    .append(rs.getString(1))
                    .append(". It is now ")
                    .append(rs.getString(2))
                    .append(".");
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                s_log.error(e);
                try {ConnectionManager.returnConnection(conn); }
                catch (SQLException e2) {
                    s_log.error(e2);
                }
                result.append("WARNING: Failed to get date from database.");
            }
            return result.toString();
        }
    }
}
