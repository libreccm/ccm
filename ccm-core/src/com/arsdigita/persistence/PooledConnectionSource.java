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
package com.arsdigita.persistence;

import com.arsdigita.caching.CacheTable;
import com.arsdigita.db.DbHelper;
import com.arsdigita.runtime.RuntimeConfig;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.util.jdbc.Connections;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * PooledConnectionSource
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: PooledConnectionSource.java 885 2005-09-20 15:54:06Z sskracic $
 **/
public class PooledConnectionSource implements ConnectionSource {

    private static final Logger s_log =
                                Logger.getLogger(PooledConnectionSource.class);
    private static CacheTable s_connectionTags =
                              new CacheTable("jdbcConnectionTags",
                                             RuntimeConfig.getConfig().
            getJDBCPoolSize() + 10,
                                             CacheTable.MAX_CACHE_AGE,
                                             false);
    private String m_url;
    private int m_size;
    private long m_interval;
    private Set m_connections = new HashSet();
    private List m_available = new ArrayList();
    private List m_untested = new ArrayList();
    private static boolean s_taggingEnabled =
                           RuntimeConfig.getConfig().isThreadTaggingEnabled();

    public PooledConnectionSource(String url, int size, long interval) {
        m_url = url;
        m_size = size;
        m_interval = interval;

        Tester tester = new Tester();
        tester.setDaemon(true);
        tester.start();

        if (m_interval > 0) {
            Poller poller = new Poller();
            poller.setDaemon(true);
            poller.start();
        }
    }

    /**
     *  Tries to acquire preferred JDBC connection, if
     * it's available.  If not, grab the least recently used
     * connection.
     */
    public synchronized Connection acquire(Connection pref) {
        if (pref == null) {
            return acquire();
        }
        int ndx = m_available.indexOf(pref);
        if (ndx > -1) {
            return (Connection) m_available.remove(ndx);
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Reacquisition failed: " + pref
                            + ", tag: " + s_connectionTags.get(pref.toString()));
            }
            return acquire();
        }
    }

    public synchronized Connection acquire() {
        while (true) {
            if (!m_available.isEmpty()) {
                Connection conn = (Connection) m_available.remove(0);
                renameThread(conn);
                /**
                 * jensp 2011-06-18: Change to prevent connections from being
                 * in "idle in transaction" state. Such connections seam to 
                 * cause problems (memory etc.) with PostgreSQL.
                 */
                try {
                    conn.setAutoCommit(false);
                } catch (SQLException ex) {
                    s_log.warn("Failed to set autocommit to false");
                }
                /**
                 * jensp end
                 */
                return conn;
            } else if (m_connections.size() < m_size) {
                Connection result = (Connection) Connections.acquire(m_url);
                s_connectionTags.put(result.toString(), tag(result));
                m_connections.add(result);
                renameThread(result);
                /**
                 * jensp 2011-06-18: Change to prevent connections from being
                 * in "idle in transaction" state. Such connections seam to 
                 * cause problems (memory etc.) with PostgreSQL.
                 */
                try {
                    result.setAutoCommit(false);
                } catch (SQLException ex) {
                    s_log.warn("Failed to set autocommit to false");
                }
                return result;
            } else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new UncheckedWrapperException(e);
                }
            }
        }
    }

    static void renameThread(Connection conn) {
        if (s_taggingEnabled) {
            Thread curr = Thread.currentThread();
            String tname = curr.getName();
            String ctag = (String) s_connectionTags.get(conn.toString());
            if (ctag == null) {
                s_log.warn("Could not obtain conn tag for: " + conn);
                return;
            }
            String newName = tname.replaceAll("(-db[0-9]*)*$", "") + "-db"
                             + ctag;
            if (!tname.equals(newName)) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("setting the thread name to: " + newName);
                }
                curr.setName(newName);
            }
        }
    }

    public static final String getConnectionTag(Connection conn) {
        return (String) s_connectionTags.get(conn.toString());
    }

    private static final String tag(final Connection conn) {
        final int database = DbHelper.getDatabase(conn);
        try {
            String sql = "";
            String tag = "";
            switch (database) {
                case DbHelper.DB_POSTGRES:
                    sql = "select pg_backend_pid() as tag";
                    break;
                case DbHelper.DB_ORACLE:
                    sql = "select userenv('SESSIONID') as tag from dual";
                    break;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tag = rs.getString(1).trim();
                rs.close();
            }
            stmt.close();
            s_log.info("Tagging JDBC connection: " + conn + " with tag: " + tag);
            return tag;
        } catch (SQLException e) {
            throw new UncheckedWrapperException(e);
        }
    }

    public synchronized void release(Connection conn) {
        if (!m_connections.contains(conn)) {
            throw new IllegalArgumentException("connection did come from this source: "
                                               + conn);
        }

        boolean remove;
        try {
            remove = conn.isClosed();
        } catch (SQLException e) {
            s_log.warn("error calling Connection.isClosed()", e);
            remove = true;
        }

        if (remove) {
            remove(conn);
        } else {
            /**
             * jensp 2011-06-18: Change to prevent connections from being
             * in "idle in transaction" state. Such connections seam to 
             * cause problems (memory etc.) with PostgreSQL.
             */
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                s_log.warn("Failed to set auto commit to true.");
            }
            /**
             * jensp end
             */
            m_available.add(conn);
        }

        notify();
    }

    private synchronized void remove(Connection conn) {
        m_connections.remove(conn);
        m_available.remove(conn);
        s_log.info("removed: " + conn + ", tag: " + s_connectionTags.get(conn.
                toString()));
        s_connectionTags.remove(conn.toString());
    }

    synchronized void testAvailable() {
        synchronized (m_untested) {
            m_untested.addAll(m_available);
            m_available.clear();
            m_untested.notify();
        }
    }

    private class Poller extends Thread {

        public void run() {
            while (true) {
                //s_log.error("PollerThread run(): " + m_interval);
                try {
                    Thread.sleep(m_interval);
                } catch (InterruptedException e) {
                    throw new UncheckedWrapperException(e);
                }
                testAvailable();
            }
        }
    }

    // This Thread is running only if ther Poller Thread sends a notify() to
    // the synchronized m_untested variable.
    private class Tester extends Thread {

        public void run() {
            List untested = new ArrayList();
            while (true) {
                untested.clear();
                synchronized (m_untested) {
                    if (m_untested.isEmpty()) {
                        try {
                            m_untested.wait();
                        } catch (InterruptedException e) {
                            throw new UncheckedWrapperException(e);
                        }
                    }
                    untested.addAll(m_untested);
                    m_untested.clear();
                }

                for (Iterator it = untested.iterator(); it.hasNext();) {
                    Connection conn = (Connection) it.next();
                    SQLException e = test(conn);
                    if (e != null) {
                        s_log.warn("connection " + conn
                                   + ", tag: " + s_connectionTags.get(conn.
                                toString())
                                   + " failed test", e);
                        try {
                            conn.close();
                        } catch (SQLException ex) {
                            s_log.warn("error while closing bad connection", ex);
                        }
                    }
                    release(conn);
                }
            }
        }
    }
    private static final String[] TYPES = new String[]{"TABLE"};

    private static SQLException test(Connection conn) {
        try {
            // This should guarantee a db roundtrip on any normal JDBC
            // implementation.
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, "dummy", TYPES);
            rs.close();
            return null;
        } catch (SQLException e) {
            return e;
        }
    }
}
