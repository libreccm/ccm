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
package com.arsdigita.developersupport;

import java.util.Vector;
import java.util.HashMap;

/**
 * DeveloperSupport
 *
 * This class provides interfaces called by the request handling
 * and database code for collecting developer support information.
 * By registering DeveloperSupportListener's, you can add handlers
 * for this information.
 *
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @version $Id: DeveloperSupport.java 1460 2007-03-02 14:36:38Z sskracic $
 **/
public class DeveloperSupport {

    private DeveloperSupport() { } //you can't create me!

    //note that we intentionally are using a thread safe
    //collection because this will be accessed by multiple
    //threads and we don't want to introduce synchronization
    //overhead to the callbacks themselves because we can
    //optimize for the case where no listeners are registered.
    private static Vector s_listeners = new Vector();

    /**
     * addListener
     * Add a new listener.
     */
    static public synchronized void addListener(DeveloperSupportListener l) {        
        if (!s_listeners.contains(l)) {            
            s_listeners.add(l);
        }
    }

    //Currently only used for testing.
    static synchronized void clearListeners() {
        s_listeners.clear();
    }

    static public synchronized void removeListener(DeveloperSupportListener l) {
        s_listeners.remove(l);
    }

    static public synchronized boolean containsListener(DeveloperSupportListener l) {
        return s_listeners.contains(l);
    }

    static public int getListenerCount() {
        return s_listeners.size();
    }

    /**
     * requeststart
     * Callback indicating a new request has started.
     * Request is an opaque pointer for now for linkage purposes (don't want
     * to have dependencies on the dispatcher here) and for making this
     * infrastructure more general.
     */
    static public void requestStart(Object request) {
        for (int i = 0; i<s_listeners.size(); i++) {
            DeveloperSupportListener l =
                    (DeveloperSupportListener)s_listeners.elementAt(i);
            l.requestStart(request);
        }
    }

    /**
     * requestAddProperty
     * Add a new property about this request.
     */
    static public synchronized void requestAddProperty(Object request,
                                                       String property,
                                                       Object value) {
        for (int i = 0; i<s_listeners.size(); i++) {
            DeveloperSupportListener l =
                    (DeveloperSupportListener)s_listeners.elementAt(i);
            l.requestAddProperty(request, property, value);
        }
    }

    /**
     * requestEnd
     * Callback indicating the request ended
     */
    static public void requestEnd(Object request) {
        for (int i = 0; i<s_listeners.size(); i++) {
            DeveloperSupportListener l =
                    (DeveloperSupportListener)s_listeners.elementAt(i);
            l.requestEnd(request);
        }
    }

    /**
     * logQuery
     * Callback logging a database query.
     * sqle should be null if no exception was thrown, otherwise
     *      it should be the exception thrown
     */
    static public void logQuery(String connection_id,
                                String type,
                                String query,
                                HashMap bindvars,
                                long time,
                                java.sql.SQLException sqle) {
        for (int i = 0; i<s_listeners.size(); i++) {
            DeveloperSupportListener l =
                    (DeveloperSupportListener)s_listeners.elementAt(i);
            l.logQuery(connection_id, type, query, bindvars, time, sqle);
        }
    }

    /**
     * logQuery
     * Callback logging a database query.
     * sqle should be null if no exception was thrown, otherwise
     *      it should be the exception thrown
     */
    static public void logQueryCompletion(String connection_id,
                                String type,
                                String query,
                                HashMap bindvars,
                                long time,
                                long totaltime,
                                java.sql.SQLException sqle) {
        for (int i = 0; i<s_listeners.size(); i++) {
            DeveloperSupportListener l =
                    (DeveloperSupportListener)s_listeners.elementAt(i);
            l.logQueryCompletion(connection_id, type, query, bindvars, time, totaltime, sqle);
        }
    }

    /**
     * logComment
     * Log a generic comment
     */
    static public void logComment(String comment) {
        for (int i = 0; i<s_listeners.size(); i++) {
            DeveloperSupportListener l =
                    (DeveloperSupportListener)s_listeners.elementAt(i);
            l.logComment(comment);
        }

    }

    /**
     * startStage
     * Callback indicating a new stage has started.
     * Stages can be used to log help mark the time
     * taken to perform various parts of requests.
     */
    static public void startStage(String stagename) {
        for (int i = 0; i<s_listeners.size(); i++) {
            DeveloperSupportListener l =
                    (DeveloperSupportListener)s_listeners.elementAt(i);
            l.startStage(stagename);
        }
    }

    /**
     * endStage
     * Callback indicating a stage has ended.
     * Stages can be used to log help mark the time
     * taken to perform various parts of requests.
     */
    static public void endStage(String stagename) {
        for (int i = 0; i<s_listeners.size(); i++) {
            DeveloperSupportListener l =
                    (DeveloperSupportListener)s_listeners.elementAt(i);
            l.endStage(stagename);
        }
    }

}
