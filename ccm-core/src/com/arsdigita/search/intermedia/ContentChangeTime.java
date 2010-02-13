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
package com.arsdigita.search.intermedia;

import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.SessionManager;
import org.apache.log4j.Logger;

/**
 * The ContentChangeTime class is used to store the time in which
 * content changes.
 *
 * @author Archit Shah
 * @version $Id: ContentChangeTime.java 287 2005-02-22 00:29:02Z sskracic $  
 **/
class ContentChangeTime {

    // Creates a s_logging category with name = to the full name of class
    private static final Logger s_log =
        Logger.getLogger(ContentChangeTime.class.getName());

    /**
     * no need to construct
     **/
    private ContentChangeTime() { }

    /**
     * getContentChangeTime - Uses the pdl retrieve code to retrieve the
     * earliest time in the content_change_time table.
     **/
    public static long getContentChangeTime() {
        DataQuery dq = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.search.intermedia.getContentChangeTimes");

        Long result = null;
        if (dq.next()) {
            result = (Long) dq.get("timeLastChange");
        }
        dq.close();

        if (result == null) { return 0L; }
        return result.longValue();
    }

    /**
     * getEarliestTime - Returns the earliest time later than the
     * time indexing was started.
     **/
    public static long getEarliestTime(long timeIndexingStarted) {
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.search.intermedia.getTimeEarliestNonSyncedChange");
        Filter f = query.addFilter("timeEarliestChange > :time");
        f.set("time", new Long(timeIndexingStarted));

        try {
            if (query.next()) {
                long earliestTime =
                    ((Long) query.get("timeEarliestChange")).longValue();
                return earliestTime;
            } else {
                String errMsg =
                    "ContentChangeTime.getEarliestTime did not return a row";
                s_log.warn( errMsg );
                return timeIndexingStarted + 1;
            }
        } finally {
            query.close();
        }
    }

    public static void flagChange() {
        DataOperation op = SessionManager.getSession().retrieveDataOperation
            ("com.arsdigita.search.intermedia.InsertContentChangeTime");
        Long now = new Long(System.currentTimeMillis());
        op.setParameter("timeLastChange", now);
        op.execute();
    }
}
