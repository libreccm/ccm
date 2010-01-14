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

import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.Date;
import java.util.Timer;


/**
 * The BuildIndex class provides methods to keep track of when the index should
 * be rebuilt and to automatically rebuild the index.
 *
 * All the methods in the class are called either directly or indirectly from
 * method checkState, which is called periodically by a timer.
 * The object and timer are initialized at server startup by the Initializer
 * (see Initializer.java)
 *
 * A summary of how this class controls the index rebuilding is given below:
 *
 * When content that is searchable is changed or added, the
 * SearchableObserver object for the content creates and saves
 * an object of type ContentChangeTime.  This saves the time
 * of the change into table content_change_time.  Times in
 * that table are retrieved by this class.
 *
 * The timer periodically calls method checkState.  This
 * method examines the state from ContentChangeTime and
 * the last build state from IndexingTime  and
 * possibily starts a new index sync operation, or gives up
 * on a sync operation that already started but didn't finish
 * in the expected time (timed out).
 *
 * If checkState determines that indexing must be done, it calls
 * startIndexing.  startIndexing creates a new thread to do the
 * indexing in the background so that checkState will always
 * return quickly.
 *
 * @author Jeff Teeters
 * @author Joseph Bank
 * @version 1.0
 * @version $Id: BuildIndex.java 560 2005-06-10 17:01:14Z apevec $
 */
class BuildIndex {

    // Creates a s_logging category with name = to the full name of class
    private static final Logger s_log =
        Logger.getLogger( BuildIndex.class.getName() );

    // for storing timer which runs a method periodically to maintain
    // the index
    private static Timer s_Timer;
    
    // Prameters specifying how long after content changes that the
    // index should be re-synced.

    // timerDelay - Time (in milliseconds) between the periodic calls to
    //              check the status of content or index rebuilding.  The
    //              calls are controlled by a timer.
    //              If this value is 0, the content is never searched.
    // syncDelay - the time (in milliseconds) after which if a content change
    //             is made the index should be resynced if there are not other
    //              changes during that time.
    // maxSyncDelay - time (in milliseconds) after when a change is made, the
    //             index will be resynced, regardless of whether or not any
    //             changes have subsequently been made.
    // maxIndexingTime - time (in milliseconds) after which an indexing
    //             operation that was started is considered to have failed.
    // indexingRetryDelay - time (in milliseconds) after which an indexing
    //             operation that failed will be retried.
    //
    // NOTE: Following stored in MILLISECONDS, default values are specified in
    //       IN SECONDS.  This keeps the units the same as that used in the
    //       state information.

    private static long s_timerDelay;
    private static long s_syncDelay;
    private static long s_maxSyncDelay;
    private static long s_maxIndexingTime;
    private static long s_indexingRetryDelay;

    /**
     * setParametersValues - initialize delay and time values.
     * Values must be specified in milliseconds. This called by the Initializer
     * and also by tests (which use values much shorter than the defaults).
     */
    static void setParameterValues(int timerDelay,
                                   int syncDelay,
                                   int maxSyncDelay,
                                   int maxIndexingTime,
                                   int indexingRetryDelay) {
        s_log.debug("Setting search parameters");
        s_timerDelay = timerDelay;
        s_syncDelay = syncDelay;
        s_maxSyncDelay = maxSyncDelay;
        s_maxIndexingTime = maxIndexingTime;
        s_indexingRetryDelay = indexingRetryDelay;

        s_log.debug("timerDelay: " + timerDelay);
        s_log.debug("syncDelay: " + syncDelay);
        s_log.debug("maxSyncDelay: " + maxSyncDelay);
        s_log.debug("maxIndexingTime: " + maxIndexingTime);
        s_log.debug("indexingRetryDelay: " + indexingRetryDelay);
    }
    
    /**
     * startTimer - starts the timer that maintains the search index
     **/
    static synchronized void startTimer() {
        if ( s_timerDelay == 0 ) {
            s_log.debug("Turning of search since delay is 0");
            stopTimer();
            return;
        }
        if (s_Timer != null ) {
            s_log.debug("Timer exists");
            return;        // Timer already exists
        }
        // Create a new timer to check for indexing to be finished
        // Timer triggers immediately (after 1ms) then periodically
        // every timerDelay seconds.
        s_Timer = new Timer(true);
        s_log.debug( "Starting search timer with delay=" + s_timerDelay);
        s_Timer.schedule(new TimerFinished(), (long) 1, s_timerDelay);
    }


    /**
     * stopTimer - stops the timer.  Should only be called when the
     *             server is shutdown.
     **/
    static synchronized void stopTimer() {
        if (s_Timer == null) {
            s_log.debug("No timer to stop.");
            return;        // No timer.  Nothing to stop.
        }
        // Stop the timer
        s_Timer.cancel();
        s_Timer = null;
    }

    /**
     * restartTimer - restarts the timer that maintains the search index,
     * with a new set of parameters
     **/
    static synchronized void restartTimer(int timerDelay,
                                          int syncDelay,
                                          int maxSyncDelay,
                                          int maxIndexingTime,
                                          int indexingRetryDelay) {
        setParameterValues(timerDelay,
                           syncDelay,
                           maxSyncDelay,
                           maxIndexingTime,
                           indexingRetryDelay);
        stopTimer();
        startTimer();
    }


    /**
     * startIndexing - start an index sync or build operation.
     * task should be either "sync" or "build".  Actual indexing
     * is done by an oracle job which is queued, allowing this method
     * to return quickly.
     */
    private static void startIndexing(String task) {
        try {
            s_log.info( "Seach BuildIndex " + task + " task starting.");
            Session ssn = SessionManager.getSession();
            java.sql.Connection con = ssn.getConnection();
            CallableStatement funCall =
                con.prepareCall("{ ? = call search_indexing.queue_task('" +
                                task + "')}");
            funCall.registerOutParameter(1, Types.INTEGER);
            funCall.execute();
            int return_value = funCall.getInt(1);
            funCall.close();
            // Make sure a non-zero value was returned.  If not, a job
            // was already active
            if (return_value == 0) {
                s_log.error("Search BuildIndex " + task + " could not start."+
                            "  Previous job still active.");
            }
        } catch (java.sql.SQLException e) {
            // flag current BuildIndex failed. Will later try new
            // attempt at sync
            s_log.error("Search BuildIndex.startIndexing failed", e);
            return;
        }
    }

    /**
     * Go ahead and sync, without waiting for timer's criteria to elapse,
     * provided the sync isn't currently running.
     */
    public static synchronized void forceSyncNow() {
        IndexingTime it = IndexingTime.getIndexingTime();
        if (! it.getJobStatus().equals("running")) {
            s_log.debug( "search timer: forcing sync now");
            startIndexing("sync");
        } else {
            s_log.debug( "search timer: not forcing sync now " +
                         "because sync is currently running");
        }
    }

    /**
     * checkState - Check the current state and take appropriate action.
     * This method is called periodically by the timer (TimerFinished.java)
     */
    static synchronized void checkState() {
        IndexingTime it = IndexingTime.getIndexingTime();
        // debugging info
        s_log.info( "Search timer executed at " +
                    new Date() + " State = " + it.getJobStatus());

        if (it.getJobStatus().equals("finished")) {
            if (shouldSyncAgain(it)) {
                startIndexing("sync");
            } else {
                s_log.debug("Search timer: index resync needed, but must wait.");
            }
        } else if (it.getJobStatus().equals("failed")) {
            // Indexing has been detected as having failed.
            // See if indexingRetryDelay has passed
            long timeSinceFailure = System.currentTimeMillis() - it.getTimeFailed();
            final boolean pastRetryDelay = timeSinceFailure >= s_indexingRetryDelay;
            if (pastRetryDelay) {
                startIndexing("sync");
            }
        } else if (it.getJobStatus().equals("running")) {
            // Has indexing operation timed out? (i.e. running longer than expected)
            final long timeSinceStart = System.currentTimeMillis() - it.getTimeStarted();
            final boolean indexingTimedOut = timeSinceStart > s_maxIndexingTime;
            if (indexingTimedOut) {
                // yes, indexing operation timed out
                s_log.error("Index sync timed out.  The index rebuild " +
                            "took longer than " + s_maxIndexingTime / 1000 +
                            " seconds.");
                // TODO: Should abort oracle job
            }
        }

        // If it.getJobStatus() is "queued", just wait.
    }

    private static boolean shouldSyncAgain( IndexingTime it) {
        Assert.truth(it.getJobStatus().equals("finished"));

        final long timeLastChange = ContentChangeTime.getContentChangeTime();

        if(s_log.isDebugEnabled()) {
            s_log.debug("Search timer: timeLastChange = " + new Date(timeLastChange) +
                            " startTime = " + new Date(it.getTimeStarted()));
        }
        if (timeLastChange <= it.getTimeStarted()) {
            s_log.debug("Search timer: No need to resync index.");
            return false;
        }


        final long currentTime = System.currentTimeMillis();

        final long timeSinceLastChange = currentTime - timeLastChange;
        final long earliestUnsynchedChange =
            ContentChangeTime.getEarliestTime(it.getTimeStarted());
        // See if need to start a new indexing
        s_log.debug( "Time since last change:" + timeSinceLastChange);
        final long timeSinceFirstUnsynchedChange =
            currentTime - earliestUnsynchedChange;
        // See if Sync should be done now
        final boolean shouldSync = timeSinceLastChange >= s_syncDelay ||
                        timeSinceFirstUnsynchedChange >= s_maxSyncDelay;
        return shouldSync;
    }
}
