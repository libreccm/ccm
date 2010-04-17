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
package com.arsdigita.developersupport;

import com.arsdigita.persistence.PooledConnectionSource;
import com.redhat.persistence.engine.rdbms.RDBMSProfiler;
import com.redhat.persistence.engine.rdbms.RDBMSStatement;
import com.redhat.persistence.engine.rdbms.StatementLifecycle;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * DeveloperSupportProfiler
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: DeveloperSupportProfiler.java 1498 2007-03-19 16:22:15Z apevec $
 **/

public class DeveloperSupportProfiler implements RDBMSProfiler {


    public DeveloperSupportProfiler() {}

    public StatementLifecycle getLifecycle(Connection conn, RDBMSStatement stmt) {
        if (DeveloperSupport.getListenerCount() > 0) {
            return new Lifecycle(conn, stmt);
        } else {
            return null;
        }
    }

    private class Lifecycle implements StatementLifecycle {

        private RDBMSStatement m_stmt;
        private Connection m_conn;
        private long m_start;
        private long m_execute;
        private long m_total;
        private long m_begin_next;
        private long m_begin_get;
        private long m_begin_close;
        private boolean m_closed = false;
        private HashMap m_bindings = new HashMap();


        public Lifecycle(Connection conn, RDBMSStatement stmt) {
            m_conn = conn;
            m_stmt = stmt;
            m_start = System.currentTimeMillis();
        }

        public void beginPrepare() {}
        public void endPrepare() {}
        public void endPrepare(SQLException e) {}

        public void beginSet(int pos, int type, Object obj) {
            m_bindings.put(new Integer(pos), obj);
        }

        public void endSet() {}
        public void endSet(SQLException e) {}

        public void beginExecute() {
            m_start = System.currentTimeMillis();
        }

        public void endExecute(int updateCount) {
            endExecute(null);
        }

        public void endExecute(SQLException e) {
            m_execute = System.currentTimeMillis() - m_start;
            m_total = m_execute;
            DeveloperSupport.logQuery(
                PooledConnectionSource.getConnectionTag(m_conn),
                (m_stmt.getSignature() == null
                  ? "executeUpdate" : "executeQuery"),
                 m_stmt.getText(), m_bindings, m_execute, e);
        }

        public void beginNext() {
            m_begin_next = System.currentTimeMillis();
        }
        public void endNext(boolean more) {
            m_total += (System.currentTimeMillis() - m_begin_next);
        }
        public void endNext(SQLException e) {
            endNext(false);
        }

        public void beginGet(String column) {
            m_begin_get = System.currentTimeMillis();
        }
        public void endGet(Object result) {
            endGet();
        }
        public void endGet(SQLException e) {
            endGet();
        }
        private void endGet() {
            m_total += (System.currentTimeMillis() - m_begin_get);
        }

        public void beginClose() {
            m_begin_close = System.currentTimeMillis();
        }
        public void endClose() {
            endClose(null);
        }
        public void endClose(SQLException e) {
            m_total += (System.currentTimeMillis() - m_begin_close);
            DeveloperSupport.logQueryCompletion(
                PooledConnectionSource.getConnectionTag(m_conn),
                (m_stmt.getSignature() == null
                  ? "executeUpdate" : "executeQuery"),
                 m_stmt.getText(), m_bindings, m_execute, m_total, e);
        }

    }

}
