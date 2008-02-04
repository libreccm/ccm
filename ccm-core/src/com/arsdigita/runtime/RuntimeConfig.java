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
package com.arsdigita.runtime;

import com.arsdigita.util.jdbc.JDBCURLParameter;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;
import org.apache.log4j.Logger;

/**
 * A configuration record for configuration of the runtime
 * environment.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: RuntimeConfig.java 1393 2006-11-28 09:12:32Z sskracic $
 */
public final class RuntimeConfig extends AbstractConfig {
    public final static String versionId =
        "$Id: RuntimeConfig.java 1393 2006-11-28 09:12:32Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/16 18:10:38 $";

    private static final Logger s_log = Logger.getLogger(RuntimeConfig.class);

    private static RuntimeConfig s_config;

    /**
     * Returns the singleton configuration record for the runtime
     * environment.
     *
     * @return The <code>RuntimeConfig</code> record; it cannot be null
     */
    public static final synchronized RuntimeConfig getConfig() {
        if (s_config == null) {
            s_config = new RuntimeConfig();
            s_config.require("ccm-core/runtime.properties");
        }

        return s_config;
    }

    private final Parameter m_url;
    private final Parameter m_poolSize;
    private final Parameter m_pingInterval;
    private final Parameter m_queryCacheSize;
    private final Parameter m_threadTagging;
    private final Parameter m_resultSetWindowSize;
    private final Parameter m_runBackgroundTasks;

    /**
     * Constructs an empty RuntimeConfig object.
     **/

    public RuntimeConfig() {
        m_url = new JDBCURLParameter("waf.runtime.jdbc_url");
        m_poolSize = new IntegerParameter
            ("waf.runtime.jdbc_pool_size", Parameter.OPTIONAL,
             new Integer(10));
        m_pingInterval = new IntegerParameter
            ("waf.runtime.jdbc_ping_interval", Parameter.OPTIONAL,
             new Integer(30000));
        m_queryCacheSize = new IntegerParameter
            ("waf.runtime.query_cache_size", Parameter.OPTIONAL,
             new Integer(2000));
        m_threadTagging = new BooleanParameter
            ("waf.runtime.thread_tagging",
             Parameter.REQUIRED,
             Boolean.TRUE);
        m_resultSetWindowSize = new IntegerParameter
            ("waf.runtime.jdbc_resultset_windowsize", Parameter.REQUIRED,
             new Integer(1));
		m_runBackgroundTasks = new BooleanParameter
					("waf.runtime.run_background_tasks",
					 Parameter.REQUIRED,
					 Boolean.TRUE);

        register(m_url);
        register(m_poolSize);
        register(m_pingInterval);
        register(m_queryCacheSize);
        register(m_threadTagging);
        register(m_resultSetWindowSize);
        register(m_runBackgroundTasks);

        loadInfo();
    }

    /**
     * Returns the default JDBC URL for the current runtime.
     *
     * @return A <code>String</code> JDBC URL; it cannot be null
     */
    public final String getJDBCURL() {
        return (String) get(m_url);
    }

    /**
     * Returns the maximum size to be used for the connection pool.
     *
     * @return An integer limit on the number of JDBC connections
     * allowed open at once.
     */

    public final int getJDBCPoolSize() {
        return ((Integer) get(m_poolSize)).intValue();
    }

    public final long getJDBCPingInterval() {
        return ((Integer) get(m_pingInterval)).longValue();
    }

    /**
     *  Returns the size of in-memory window of a fetched result set.
     *
     * @return 0 if all fetched rows are kept in memory (beware!)
     */
    public final int getResultSetWindowSize() {
        return ((Integer) get(m_resultSetWindowSize)).intValue();
    }

    public final int getQueryCacheSize() {
        return ((Integer) get(m_queryCacheSize)).intValue();
    }

    public final boolean isThreadTaggingEnabled() {
        return ((Boolean)get(m_threadTagging)).booleanValue();
    }
	public final boolean runBackGroundTasks() {
		return ((Boolean)get(m_runBackgroundTasks)).booleanValue();
	}
}
