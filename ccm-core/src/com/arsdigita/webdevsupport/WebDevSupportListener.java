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

import com.arsdigita.developersupport.DeveloperSupportListener;
import com.arsdigita.dispatcher.RequestEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import org.apache.log4j.Logger;



/**
 * WebDevSupportListener
 *   DeveloperSupportListener for Web Development Support package.
 *  <p>
 *    
 *  </p>
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @version 1.0
 * @version $Id: WebDevSupportListener.java 1460 2007-03-02 14:36:38Z sskracic $
 */
public class WebDevSupportListener extends DeveloperSupportListener {

    private static final Logger s_log =
        Logger.getLogger( WebDevSupportListener.class );

    private static WebDevSupportListener s_instance;
    public static synchronized WebDevSupportListener getInstance() {
        if (s_instance == null) {
            s_instance = new WebDevSupportListener();
        }
        return s_instance;
    }

    private WebDevSupportListener() {
        //empty for now
    }

    private static int s_max_requests = 100;
    public void setMaxRequests(int max_requests) {
        s_max_requests = max_requests;
    }

    public int getMaxRequests() {
        return s_max_requests;
    }

    //We store a HashTable that maps Threads to their most recent
    //request object.  This gets cleaned up when requests end
    private HashMap m_threadRequestMap = new HashMap();
    private ArrayList m_requests = new ArrayList();

    private synchronized void registerNewRequest(RequestInfo ri) {
        m_threadRequestMap.put(Thread.currentThread(), ri);
        m_requests.add(ri);
        if (s_max_requests != -1) {
            int to_remove = m_requests.size() - s_max_requests;
            //this is kindof expensive, but usually just one at a time
            for (int i = 0; i<to_remove; i++) {
                m_requests.remove(0);
            }
        }
    }

    synchronized void clearRequestHistory() {
        m_requests.clear();
    }

    private RequestInfo getCurrentRequest() {
        return (RequestInfo)m_threadRequestMap.get(Thread.currentThread());
    }

    private synchronized void unRegisterRequest() {
        m_threadRequestMap.remove(Thread.currentThread());
    }

    public ListIterator getRequestsReverse() {
        //need to copy the requests to allow the iterator
        //to work in spite of concurrent modifications
        ArrayList lst = (ArrayList)m_requests.clone();
        return lst.listIterator(lst.size());
    }

    public ListIterator getRequests() {
        //need to copy the requests to allow the iterator
        //to work in spite of concurrent modifications
        ArrayList lst = (ArrayList)m_requests.clone();
        return lst.listIterator();
    }

    public RequestInfo getRequest(int id) {
        Iterator iter = m_requests.iterator();
        while (iter.hasNext()) {
            RequestInfo ri = (RequestInfo)iter.next();
            if (ri.getID() == id) {
                return ri;
            }
        }
        return null;
    }


    /**
     * requestStart
     * Callback indicating a new request has started.
     * Request is an opaque pointer for now for linkage purposes (don't want
     * to have dependencies on the dispatcher here) and for making this
     * infrastructure more general.
     */
    @Override
    public void requestStart(Object request) {
        if (request instanceof RequestEvent && getCurrentRequest() == null) {
            registerNewRequest(new RequestInfo((RequestEvent)request));
        }
    }

    /**
     * requestAddProperty
     * Add a new property about this request.
     */
    @Override
    public void requestAddProperty(Object request, String property, Object value) {
        RequestInfo ri = getCurrentRequest();
        if (ri != null) {
            ri.addProperty(property, value);
        }
    }

    /**
     * requestEnd
     * Callback indicating the request ended
     */
    @Override
    public void requestEnd(Object request) {
        RequestInfo ri = getCurrentRequest();
        if (ri != null) {
            ri.finish();
        }
        unRegisterRequest();
    }

    /**
     * logQuery
     * Callback logging a database query
     */
    @Override
    public void logQuery(String connection_id,
                         String type,
                         String query,
                         HashMap bindvars,
                         long time,
                         java.sql.SQLException sqle) {
        RequestInfo ri = getCurrentRequest();
        if (ri != null) {
            ri.logQuery(new QueryInfo(ri.numQueries()+1,
                                      connection_id,
                                      type,
                                      query,
                                      bindvars,
                                      time,
                                      sqle));
        }
    }

    /**
     * logQuery
     * Callback logging a database query
     */
    @Override
    public void logQueryCompletion(String connection_id,
                         String type,
                         String query,
                         HashMap bindvars,
                         long time,
                         long totaltime,
                         java.sql.SQLException sqle) {
        RequestInfo ri = getCurrentRequest();
        if (ri != null) {
            QueryInfo qi = ri.findQuery(connection_id, type, query, bindvars, time);
            if (qi == null) {
                s_log.warn("Could not find query: " + query + "\nBinds: " + bindvars);
            } else {
                qi.setCompletion(totaltime, sqle);
            }
        }
    }

    /**
     * logComment
     * Log a generic comment
     */
    @Override
    public void logComment(String comment) {
        RequestInfo ri = getCurrentRequest();
        if (ri != null) {
            ri.logComment(comment);
        }
    }

    /**
     * startStage
     * Callback indicating a new stage has started.
     * Stages can be used to log help mark the time
     * taken to perform various parts of requests.
     */
    @Override
    public void startStage(String stagename) {
        RequestInfo ri = getCurrentRequest();
        if (ri != null) {
            ri.startStage(stagename);
        }
    }

    /**
     * endStage
     * Callback indicating a stage has ended.
     * Stages can be used to log help mark the time
     * taken to perform various parts of requests.
     */
    @Override
    public void endStage(String stagename) {
        RequestInfo ri = getCurrentRequest();
        if (ri != null) {
            ri.endStage(stagename);
        }
    }

}
