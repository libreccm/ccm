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
package com.arsdigita.webdevsupport;

import com.arsdigita.util.StringUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @version 1.0
 **/
public class QueryInfo {
    public static final String versionId = "$Id: QueryInfo.java 1460 2007-03-02 14:36:38Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    private int m_id;
    private String m_connection_id;
    private String m_type;
    private String m_query;
    private TreeMap m_bindvars;
    private long m_time;
    private long m_total;
    private boolean m_closed;
    private java.sql.SQLException m_sqle;
    private Throwable m_stack_trace;

    public QueryInfo(int id,
                     String connection_id,
                     String type,
                     String query,
                     Map bindvars,
                     long time,
                     java.sql.SQLException sqle) {
        m_id = id;
        m_connection_id = connection_id;
        m_type = type;
        m_query = query;
        m_bindvars = new TreeMap(bindvars);
        m_time = time;
        m_total = time;
        m_sqle = sqle;
        m_stack_trace = new Throwable();
        m_closed = false;
    }

    public int getID() {
        return m_id;
    }
    public String getConnectionID() {
        return m_connection_id;
    }
    public String getType() {
        return m_type;
    }
    public String getQuery() {
        return m_query;
    }
    public Map getBindvars() {
        return m_bindvars;
    }
    public long getTime() {
        return m_time;
    }
    public long getTotalTime() {
        return m_closed ? m_total : m_time;
    }
    public java.sql.SQLException getSQLE() {
        return m_sqle;
    };
    public boolean isClosed() {
        return m_closed;
    };

    void setCompletion(long total, java.sql.SQLException sqle) {
        m_total = total;
        m_closed = true;
        if (m_sqle == null) {
            m_sqle = sqle;
        }
    }

    public String getStackTrace() {
        String result = StringUtils.getStackTrace(m_stack_trace);
        //look for the last "DeveloperSupport.logQuery" command
        int i;
        int j;
        if (((i = result.indexOf("DeveloperSupport.logQuery")) != -1) &&
            ((j = result.indexOf("\n", i)) != -1)) {
            result = result.substring(j);
        }
        return result;
    }

    public boolean equals(Object o) {
        if (o instanceof QueryInfo) {
            QueryInfo qi = (QueryInfo)o;
            boolean bindvars_equals;
            if (qi.getBindvars() == null) {
                bindvars_equals = (getBindvars() == null);
            } else {
                bindvars_equals = qi.getBindvars().equals(getBindvars());
            }
            return bindvars_equals &&
                qi.getQuery().equals(getQuery());
        }
        return false;
    }
    public int hashCode() {
        return getQuery().hashCode();
    }

}
