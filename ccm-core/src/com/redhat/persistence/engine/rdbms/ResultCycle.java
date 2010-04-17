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
package com.redhat.persistence.engine.rdbms;

import com.arsdigita.util.WrappedError;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * ResultCycle
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: ResultCycle.java 287 2005-02-22 00:29:02Z sskracic $
 **/

class ResultCycle {

    private static final Logger LOG = Logger.getLogger(ResultCycle.class);

    final private RDBMSEngine m_engine;
    private ResultSet m_rs;
    final private StatementLifecycle m_cycle;
    final private Throwable m_trace;

    ResultCycle(RDBMSEngine engine, ResultSet rs, StatementLifecycle cycle) {
        if (rs == null) {
            throw new IllegalArgumentException("null result set");
        }

        m_engine = engine;
        m_rs = rs;
        m_cycle = cycle;
        if (LOG.isInfoEnabled()) {
            m_trace = new Throwable();
        } else {
            m_trace = null;
        }
    }

    protected void finalize() {
        if (m_rs != null) {
            LOG.warn("ResultSet  was not closed.  " +
                     "Turn on INFO logging for " + this.getClass() +
                     " to see the stack trace for this ResultSet.");

            if (m_trace != null) {
                LOG.info("The ResultSet was created at: ", m_trace);
            }

            m_rs = null;
        }
    }

    public ResultSet getResultSet() {
        return m_rs;
    }

    public StatementLifecycle getLifecycle() {
        return m_cycle;
    }

    public boolean next() {
        if (m_rs == null) {
            throw new IllegalStateException("result set closed");
        }
        try {
            if (m_cycle != null) { m_cycle.beginNext(); }
            boolean result = m_rs.next();
            if (m_cycle != null) { m_cycle.endNext(result); }
            if (!result) { close(); }
            return result;
        } catch (SQLException e) {
            if (m_cycle != null) { m_cycle.endNext(e); }
            throw new WrappedError(e);
        }
    }

    public void close() {
        if (m_rs == null) { return; }
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Closing Statement because resultset was closed.");
            }
            if (m_cycle != null) { m_cycle.beginClose(); }
            m_rs.getStatement().close();
            m_rs.close();
            if (m_cycle != null) { m_cycle.endClose(); }
            m_rs = null;
            m_engine.release();
        } catch (SQLException e) {
            if (m_cycle != null) { m_cycle.endClose(e); }
            throw new WrappedError(e);
        }
    }

}
