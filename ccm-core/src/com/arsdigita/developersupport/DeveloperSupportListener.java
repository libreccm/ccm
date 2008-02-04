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

import java.util.HashMap;

/**
 *
 * @author Joseph A. Bank (jbank@alum.mit.edu)
 * @version 1.0
 **/
public abstract class DeveloperSupportListener {
    public static final String versionId = "$Id: DeveloperSupportListener.java 1460 2007-03-02 14:36:38Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * requestStart
     * Callback indicating a new request has started.
     * Request is an opaque pointer for now for linkage purposes (don't want
     * to have dependencies on the dispatcher here) and for making this
     * infrastructure more general.
     */
    public void requestStart(Object request) {
    }

    /**
     * requestAddProperty
     * Add a new property about this request.
     */
    public void requestAddProperty(Object request, String property, Object value) {
    }
    /**
     * requestEnd
     * Callback indicating the request ended
     */
    public void requestEnd(Object request) {
    }

    /**
     * logQuery
     * Callback logging a database query
     */
    public void logQuery(String connection_id,
                         String type,
                         String query,
                         HashMap bindvars,
                         long time,
                         java.sql.SQLException sqle) {
    }

    /**
     * logQuery
     * Callback logging a database query
     */
    public void logQueryCompletion(String connection_id,
                         String type,
                         String query,
                         HashMap bindvars,
                         long time,
                         long totaltime,
                         java.sql.SQLException sqle) {
    }

    /**
     * logComment
     * Log a generic comment
     */
    public void logComment(String comment) {
    }

    /**
     * startStage
     * Callback indicating a new stage has started.
     * Stages can be used to log help mark the time
     * taken to perform various parts of requests.
     */
    public void startStage(String stagename) {
    }

    /**
     * endStage
     * Callback indicating a stage has ended.
     * Stages can be used to log help mark the time
     * taken to perform various parts of requests.
     */
    public void endStage(String stagename) {
    }
}
