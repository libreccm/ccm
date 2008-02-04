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

import com.arsdigita.dispatcher.RequestEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

/**
 * Information kept about each request
 *
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @version 1.0
 **/
public class RequestInfo {
    public static final String versionId = "$Id: RequestInfo.java 1460 2007-03-02 14:36:38Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    static private int s_counter = 1;

    private int  m_id = nextID();
    private Date m_start = new Date();
    private Date m_end = null;
    private HashMap m_headers = new HashMap();
    private HashMap m_parameters = new HashMap();

    private static synchronized int nextID() {
        return s_counter++;
    }

    public RequestInfo(RequestEvent re) {
        //get the url, and IP
        HttpServletRequest req = re.getRequest();

        addProperty("METHOD", req.getMethod());
        addProperty("QUERY", req.getQueryString());
        addProperty("IP", req.getRemoteAddr());
        addProperty("URL", req.getRequestURI());
        for (Enumeration e = req.getHeaderNames();
             e.hasMoreElements(); ) {
            String header = (String)e.nextElement();
            m_headers.put(header, req.getHeader(header));
        }
        for (Enumeration e = req.getParameterNames();
             e.hasMoreElements(); ) {
            String param = (String)e.nextElement();
            m_parameters.put(param, req.getParameter(param));
        }
    }

    public Iterator getParameterNames() {
        return m_parameters.keySet().iterator();
    }
    public String getParameter(String name) {
        return (String)m_parameters.get(name);
    }

    public void finish() {
        m_end = new Date();
        
        // Sometimes stages aren't ended which means
        // that we end up with huge negative values 
        // for time. So lets fix  them up now...
        for (int i = 0 ; i < m_stages.size() ; i++) {
            StageInfo si = (StageInfo)m_stages.get(i);
            if (si.endTime() == 0) {
                si.end(numQueries());
            }
        }
    }

    public String getTime() {
        return m_start.toString();
    }

    public String getEndTime() {
        if (m_end != null) {
            return m_end.toString();
        }
        return "<none>";
    }

    public String getDuration() {
        if (m_end == null) {
            return "";
        }
        return (m_end.getTime() - m_start.getTime())+"";
    }

    public String getIP() {
        return (String)getProperty("IP");
    }

    public String getURL() {
        return (String)getProperty("URL");
    }

    public String getRequest() {
        String query = getQuery();
        if (query == null) {
            return getMethod() + " " + getURL();
        } else {
            return getMethod() + " " + getURL() + "?" + getQuery();
        }
    }

    public String getMethod() {
        return (String)getProperty("METHOD");
    }

    public String getQuery() {
        return (String)getProperty("QUERY");
    }

    public int getID() {
        return m_id;
    }

    public Iterator headerKeys() {
        return m_headers.keySet().iterator();
    }

    public String getHeader(String key) {
        return (String)m_headers.get(key);
    }


    private HashMap m_properties = new HashMap();

    public void addProperty(String property,
                            Object value) {
        m_properties.put(property, value);
    }

    public Set propertyKeys() {
        return m_properties.keySet();
    }

    public Object getProperty(String key) {
        return m_properties.get(key);
    }

    private List m_comments = new ArrayList();

    public void logComment(String comment) {
        m_comments.add(comment);
    }
    public ListIterator getComments() {
        return m_comments.listIterator();
    }

    public int numComments() {
        return m_comments.size();
    }

    //Query handling
    private List m_queries = new ArrayList();

    public void logQuery(QueryInfo qi) {
        m_queries.add(qi);
    }

    public ListIterator getQueries() {
        return m_queries.listIterator();
    }

    public QueryInfo getQuery(int id) {
        Iterator iter = getQueries();
        while (iter.hasNext()) {
            QueryInfo qi = (QueryInfo)iter.next();
            if (qi.getID() == id) {
                return qi;
            }
        }
        return null;
    }

    QueryInfo findQuery(String connection_id, String type, String query, HashMap bindvars, long time) {
        // the query we're searching is most likely the last one, so search backwards
        QueryInfo qi = null;
        for (int i=getNumQueries()-1; i>=0; i--) {
            QueryInfo curr = (QueryInfo) m_queries.get(i);
            if (curr.getConnectionID().equals(connection_id)
                    &&  curr.getType().equals(type)
                    &&  curr.getQuery().equals(query)
                    &&  curr.getBindvars().equals(bindvars)
                    &&  curr.getTime() == time) {
                qi = curr;
                break;
            }
        }
        return qi;
    }

    /**
     * @deprecated Use {@link #getNumQueries()} instead, which
     * enables this object to be treated as a bean.
     */
    public int numQueries() {
        return m_queries.size();
    }

    public int getNumQueries() {
        return numQueries();
    }

    public boolean isDevSupportRequest() {
        return getProperty("IS_DS") != null;
    }

    private int m_stage_depth = 0;
    private List m_stages = new ArrayList();
    private StageInfo m_last_stage = null;
    public void startStage(String stagename) {
        if (m_last_stage != null) {
            m_last_stage.setLeaf(false);
        }
        m_last_stage = new StageInfo(stagename, m_stage_depth++, numQueries());
        m_stages.add(m_last_stage);
    }

    public void endStage(String stagename) {
        for (int i=m_stages.size()-1; i>=0; i--) {
            StageInfo si = (StageInfo)m_stages.get(i);
            if (si.getName().equals(stagename)) {
                si.end(numQueries());
            }
        }
        m_last_stage = null;
        m_stage_depth--;
    }

    public int numStages() {
        return m_stages.size();
    }
    public ListIterator getStages() {
        return m_stages.listIterator();
    }
}
