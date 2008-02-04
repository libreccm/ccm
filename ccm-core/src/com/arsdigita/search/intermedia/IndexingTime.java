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

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * The IndexingTime class is used to retrieve times
 * related to the most recent indexing operation.
 * Indexing operations are run as an oracle job.
 *   The times are retrieved from table search_indexing_jobs.
 * The (PL/SQL) procedures that store the times are defined
 * in file search-indexing-procs-create.sql.
 *
 * @author Jeff Teeters
 **/
class IndexingTime {
    public static final String versionId = "$Id: IndexingTime.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // Creates a s_logging category with name = to the full name of class
    private static final Logger s_log =
        Logger.getLogger( IndexingTime.class.getName() );

    private String m_jobStatus;
    private Long m_jobNum;

    private Date m_timeQueued_date;
    private Date m_timeStarted_date;
    private Date m_timeFinished_date;
    private Date m_timeFailed_date;
    private Date m_oracleSysdate;

    private long m_timeQueued;
    private long m_timeStarted;
    private long m_timeFinished;
    private long m_timeFailed;

    /**
     * no need for constructor
     **/
    private IndexingTime(String jobStatus, Long jobNum,
                         Date timeQueued_date,
                         Date timeStarted_date,
                         Date timeFinished_date,
                         Date timeFailed_date,
                         Date oracleSysdate) {
        m_jobStatus = jobStatus;
        m_jobNum = jobNum;
        m_timeQueued_date = timeQueued_date;
        m_timeStarted_date = timeStarted_date;
        m_timeFinished_date = timeFinished_date;
        m_timeFailed_date = timeFailed_date;
        m_oracleSysdate = oracleSysdate;

        convertDates();
    }

    /**
     * convertDates - Convert Oracle dates to Java times, taking into account
     * any possible difference in the clocks.
     **/

    private void convertDates() {
        final long curTime_ms = (new Date()).getTime();
        final long sysdate_ms = getOracleSysdate().getTime();
        //  Get any difference between times.  Will be positive if Oracle is behind
        final long diff = curTime_ms - sysdate_ms;

        // Convert Oracle dates to corresponding Java times
        m_timeQueued = getTimeQueued_date().getTime() + diff;

        // When converting other times, if not set, save as zero
        if (getTimeStarted_date() != null) {
            m_timeStarted = getTimeStarted_date().getTime() + diff;
        } else {
            m_timeStarted = 0;
        }
        if (getTimeFinished_date() != null) {
            m_timeFinished = getTimeFinished_date().getTime() + diff;
        } else {
            m_timeFinished = 0;
        }
        if (getTimeFailed_date() != null) {
            m_timeFailed = getTimeFailed_date().getTime() + diff;
        } else {
            m_timeFailed = 0;
        }
    }


    /**
     * getIndexingTime - Uses the pdl retrieve code to retrieve the
     * information about the most recently running job.  This class
     * is never used to write anything into the database, only read.
     **/
    public static IndexingTime getIndexingTime() {
        DataQuery job = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.search.intermedia.getJobInfo");

        Long jobNum;
        String jobStatus;
        if (job.next()) {
            jobNum = (Long) job.get("jobNum");
            jobStatus = (String) job.get("jobStatus");
        } else {
            String error_message = "Unable to run getIndexingTime";
            throw new IllegalStateException(error_message);
        }
        job.close();

        DataQuery indexInfo = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.search.intermedia.getIndexingInfo");
        indexInfo.setParameter("jobNum", jobNum);

        if (indexInfo.next()) {
            IndexingTime result =
                new IndexingTime(jobStatus, jobNum,
                                 (Date) indexInfo.get("timeQueued_date"),
                                 (Date) indexInfo.get("timeStarted_date"),
                                 (Date) indexInfo.get("timeFinished_date"),
                                 (Date) indexInfo.get("timeFailed_date"),
                                 (Date) indexInfo.get("oracleSysdate"));
            indexInfo.close();
            return result;
        } else {
            throw new IllegalStateException
                ("No index info found for job: " + jobNum);
        }
    }


    //
    // Accessors for persistence layer
    //

    private Date getTimeQueued_date() {
        return m_timeQueued_date;
    }

    private Date getTimeStarted_date() {
        return m_timeStarted_date;
    }

    private Date getTimeFinished_date() {
        return m_timeFinished_date;
    }

    private Date getTimeFailed_date() {
        return m_timeFailed_date;
    }

    private Date getOracleSysdate() {
        return m_oracleSysdate;
    }


    //
    // Accessors for external classes
    //

    public Long getJobNum() {
        return m_jobNum;
    }

    public String getJobStatus() {
        return m_jobStatus;
    }

    public long getTimeQueued() {
        return m_timeQueued;
    }

    public long getTimeStarted() {
        return m_timeStarted;
    }

    public long getTimeFinished() {
        return m_timeFinished;
    }

    public long getTimeFailed() {
        return m_timeFailed;
    }
}
