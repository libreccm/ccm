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
package com.arsdigita.web.monitoring;

import com.arsdigita.db.ConnectionManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * This page checks to make sure that the database is still working properly.
 * If it is, the page contains the word "success".  If it is not then
 * it returned the word "failed".
 *
 * To use this, simply map it to a URL in one of your dispatchers.
 * You can then point the correct keepalive script to point at the page
 * and look at the output
 *
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @version $Id: DBTestServlet.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DBTestServlet extends HttpServlet {

    private static final Logger s_log =
        Logger.getLogger(DBTestServlet.class);

    private final Date m_date = new Date();

    /**
     *
     * @param sreq
     * @param sresp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public final void service(final HttpServletRequest sreq,
                              final HttpServletResponse sresp)
        throws ServletException, IOException {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Servicing request " + sreq.getRequestURI() + " [" +
                        sreq.getContextPath() + "," +
                        sreq.getServletPath() + "," +
                        sreq.getPathInfo() + "," +
                        sreq.getQueryString() + "]");
        }

        PrintWriter out = sresp.getWriter();
        m_date.setTime(System.currentTimeMillis());

        Connection conn = null;
        try {
            conn = ConnectionManager.getConnection();

            if ( conn != null ) {
                String query = "select 1 from dual";
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(query);

                if ( rset.next() ) {
                    if (s_log.isDebugEnabled()) {
                        s_log.debug("Database test successful");
                    }
                    out.println("success - " + m_date);                    
                    return;
                }                
            } else {
                s_log.warn("Null connection returned during database test");
            }
        } catch (SQLException e) {
            s_log.warn("SQLException during database test", e);
        } finally {
            if (conn != null) {
                try {
                    ConnectionManager.returnConnection(conn);
                } catch (SQLException ex) {
                    s_log.warn("Unable to return connection", ex);
                }
            }
        }

        out.println("failure - " + m_date);
    }
}
