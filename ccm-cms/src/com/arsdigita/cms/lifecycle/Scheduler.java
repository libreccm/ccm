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
package com.arsdigita.cms.lifecycle;


import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.UncheckedWrapperException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import org.apache.log4j.Logger;

/**
 * Scheduler thread to fire all the events for the
 * lifecycles or phases that have just began or ended.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Revision: #20 $ $DateTime: 2004/08/17 23:15:09 $
 */
public class Scheduler {

    public static final String versionId = "$Id: Scheduler.java 1583 2007-05-25 15:32:13Z chrisgilbert23 $ by $Author: chrisgilbert23 $, $DateTime: 2004/08/17 23:15:09 $";

    private static final String CYCLE_ID = "cycleId";
    private static final String PHASE_ID = "phaseId";

    // Time in milliseconds
    protected static long s_timerDelay;
    protected static long s_timerFrequency;

    // For storing timer which runs a method periodically to fire
    // begin and end events
    private static Timer s_Timer;
    private static boolean s_running = false;

    // Creates a s_logging category with name = to the full name of class
    private static Logger s_log =
        Logger.getLogger( Scheduler.class.getName() );

    // A noop listener used when the listener in the DB is null. This ensures
    // that lifecycles and phases with no listener are still properly marked
    // as started/ended in the DB and not just ignored.
    private static final LifecycleListener s_emptyListener
        = new LifecycleListener() {
            public void begin(LifecycleEvent e) { }
            public void end(LifecycleEvent e) { }
        };

    /**
     * startTimer - starts the timer
     */
    public static synchronized void startTimer() {
        if ( s_Timer != null ) {
            return;        // Timer already exists
        }

        // Timer triggers after s_timerDelay then periodically
        // every s_timerFrequence seconds.
        // run timer as a daemon
        s_Timer = new Timer(true);
        s_log.debug( "Starting timer with timerDelay=" + s_timerDelay);
        s_Timer.schedule(new SchedulerTask(), s_timerDelay, s_timerFrequency);
    }

    /**
     * stopTimer - stops the timer.  Should only be called when the
     *             server is shutdown.
     **/
    public static synchronized void stopTimer() {
        if ( s_Timer == null ) {
            return;        // No timer.  Nothing to stop.
        }
        // Stop the timer
        s_Timer.cancel();
        s_Timer = null;
    }


    /**
     * Check which begin/end events to fire and then fire them.
     */
    protected static synchronized void checkAndFire() {
        TransactionContext txn = null;
        try {
            Iterator i;
            s_running = true;

            txn = SessionManager.getSession().getTransactionContext();

            // Notes: loop through the query and add the objects to the LinkedList
            //  then for each object in the LinkedList:
            //    begin the transaction,
            //    perform any operations from the listener,
            //    update the hasBegun or hasEnded column, and
            //    close the transaction (or abort if there are any errors)

            // Lifecycle Start Events
            txn.beginTxn();
            i = getPendingEvents(getLifecycleStart(),
                                 LifecycleEvent.LIFECYCLE,
                                 CYCLE_ID);
            txn.commitTxn();
            fireCycleEvents(i, true);

            // Phase Start Events
            txn.beginTxn();
            i = getPendingEvents(getPhaseStart(),
                                 LifecycleEvent.PHASE,
                                 PHASE_ID);
            txn.commitTxn();
            firePhaseEvents(i, true);

            // Phase End Events
            txn.beginTxn();
            i = getPendingEvents(getPhaseEnd(),
                                 LifecycleEvent.PHASE,
                                 PHASE_ID);
            txn.commitTxn();
            firePhaseEvents(i, false);

            // Lifecycle End Events
            txn.beginTxn();
            i = getPendingEvents(getLifecycleEnd(),
                                 LifecycleEvent.LIFECYCLE,
                                 CYCLE_ID);
            txn.commitTxn();
            fireCycleEvents(i, false);

        } catch (Throwable t) {
            try {
                if (txn != null) {
                    txn.abortTxn();
                }
            } catch (Throwable t2) {
                s_log.warn("Transaction cleanup failed: ", t2);
            }
        } finally {
            s_running = false;
        }
    }


    private static synchronized LifecycleEvent getEvent(int eventType,
                                                        DataQuery query) {

        BigDecimal objectId = (BigDecimal) query.get("objectId");
        String objectType = (String) query.get("objectType");
        Date startTime = (Date) query.get("startDateTime");
        Date endTime = (Date) query.get("endDateTime");

        return new LifecycleEvent(eventType, startTime, endTime,
                                  objectType, objectId);
    }


    private static synchronized LifecycleListener getListener(DataQuery query) {
        String listener = (String) query.get("listener");

        if (listener == null) {
            return s_emptyListener;
        } else {
            try {
                Class listenerClass = Class.forName(listener);
                return (LifecycleListener) listenerClass.newInstance();
            } catch (Exception e) {
                s_log.error( "Error in LifecycleEvent - getting listener: " +
                             e.getMessage(), e);
                return null;
            }
        }
    }


    /**
     * run - Run the task
     */
    public static synchronized void run() {
        Thread.currentThread().setName("cycle");
        Session ssn = SessionManager.getSession();
        if ( !s_running ) {
            new KernelExcursion() {
                protected final void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());
                    s_log.debug("Firing off lifecycle schedular job");
                    checkAndFire();
                }
            }.run();
        }
    }


    /**
     * test run - Called in a test file and run only once
     */
    public static synchronized void runTest() {
        new KernelExcursion() {
            protected final void excurse() {
                setEffectiveParty(Kernel.getSystemParty());

                checkAndFire();
            }
        }.run();
    }


    protected static synchronized DataQuery getLifecycleStart() {
        Session session = SessionManager.getSession();
        DataQuery query =
            session.retrieveQuery("com.arsdigita.cms.getLifecycleStart");
        query.setParameter("startDateTime", new Date());
        return query;
    }

    protected static synchronized DataQuery getLifecycleEnd() {
        Session session = SessionManager.getSession();
        DataQuery query =
            session.retrieveQuery("com.arsdigita.cms.getLifecycleEnd");
        query.setParameter("endDateTime", new Date());
        return query;
    }

    protected static synchronized DataQuery getPhaseStart() {
        Session session = SessionManager.getSession();
        DataQuery query =
            session.retrieveQuery("com.arsdigita.cms.getPhaseStart");
        query.setParameter("startDateTime", new Date());
        return query;
    }

    protected static synchronized DataQuery getPhaseEnd() {
        Session session = SessionManager.getSession();
        DataQuery query =
            session.retrieveQuery("com.arsdigita.cms.getPhaseEnd");
        query.setParameter("endDateTime", new Date());
        return query;
    }


    ////////////////////////////////////////////////////
    //
    // These functions are only used in checkAndFire.
    //

    /**
     * Returns an iterator of pending events.
     */
    private static synchronized Iterator getPendingEvents(
                                                          DataQuery query, int eventType, String idColumn
                                                          ) {
        try {
            LinkedList rowList = new LinkedList();

            while ( query.next() ) {
                LifecycleEvent event = getEvent(eventType, query);
                LifecycleListener listener = getListener(query);

                if ( listener != null ) {
                    BigDecimal id = (BigDecimal) query.get(idColumn);
                    rowList.add(new EventRow(event, listener, id));
                }
            }

            return rowList.iterator();

        } finally {
            query.close();
        }

    }

    /**
     * Prepares the lifecycle events for firing.
     *
     * @param events An iterator of pending lifecycle events
     * @param begin true if this is a "begin" event, false otherwise
     */
    private static synchronized void fireCycleEvents(Iterator events,
                                                     boolean begin) {
        TransactionContext txn =
            SessionManager.getSession().getTransactionContext();

        while ( events.hasNext() ) {
            EventRow row = (EventRow) events.next();

            LifecycleEvent event = row.getEvent();
            LifecycleListener listener = row.getListener();

            BigDecimal id = row.getObjectID();
            Lifecycle cycle = null;
            txn.beginTxn();
            try {
                cycle = new Lifecycle(id);
            } catch (DataObjectNotFoundException e) {
                txn.abortTxn();
                throw new UncheckedWrapperException( (String) GlobalizationUtil.globalize("cms.lifecycle.could_not_fetch_lifecycle").localize() + id, e);
            }

            try {
                if ( begin ) {
                    // Start the lifecycle.
                    listener.begin(event);
                    cycle.setHasBegun(true);
                } else {
                    // Stop the lifecycle.
                    listener.end(event);
                    cycle.stop();
                    if (ContentSection.getConfig().deleteFinishedLifecycles()) {
                    	// this seems to be the best place to do this - after any other 
                    	// activities associated with the end of the cycle have been 
                    	// completed
                    	s_log.debug("Lifecycle " + cycle.getID() + " has ended - now deleting it");
                    	cycle.delete();
                    }
                }
                //
                // cg not required with new persistence & would cause exception
                // if cycle deleted above
                //cycle.save();

                txn.commitTxn();
            } catch (Exception e) {
                txn.abortTxn();
                // TODO: Shouldn't this re-throw? The transaction is aborted, so some other error
                // will occur...
                s_log.error( "Error in LifecycleEvent: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Prepares the phase events for firing.
     *
     * @param events An iterator of pending phase events
     * @param begin true if this is a "begin" event, false otherwise
     */
    private static synchronized void firePhaseEvents(Iterator events,
                                                     boolean begin) {

        TransactionContext txn =
            SessionManager.getSession().getTransactionContext();

        while ( events.hasNext() ) {
            EventRow row = (EventRow) events.next();
            LifecycleEvent event = row.getEvent();
            LifecycleListener listener = row.getListener();

            BigDecimal phaseID = row.getObjectID();
            Phase phase = null;
            txn.beginTxn();
            try {
                phase = new Phase(phaseID);
            } catch (DataObjectNotFoundException e) {
                txn.abortTxn();
                throw new UncheckedWrapperException( (String) GlobalizationUtil.globalize("cms.lifecycle.could_not_fetch_phase").localize() + phaseID, e);
            }

            try {
                if ( begin ) {
                    listener.begin(event);
                    phase.setHasBegun(true);
                } else {
                    listener.end(event);
                    phase.stop();
                }
                phase.save();
                txn.commitTxn();
            } catch (Exception e) {
                txn.abortTxn();
                // TODO: Shouldn't this re-throw? The transaction is aborted, so some other error
                // will occur...
                s_log.error("Error in LifecycleEvent: " + e.getMessage(), e);
            }
        }
    }


    /**
     * Wrapper for lifecycle events.
     */
    private static class EventRow {

        private LifecycleEvent m_event;
        private LifecycleListener m_listener;
        private BigDecimal m_id;

        public EventRow(LifecycleEvent event,
                        LifecycleListener listener,
                        BigDecimal id) {
            m_event = event;
            m_listener = listener;
            m_id = id;
        }

        public LifecycleEvent getEvent() { return m_event; }
        public LifecycleListener getListener() { return m_listener; }
        public BigDecimal getObjectID() { return m_id; }
    }

}
