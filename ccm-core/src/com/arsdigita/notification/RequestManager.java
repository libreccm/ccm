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
package com.arsdigita.notification;

import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.UserCollection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.TimerTask;
import org.apache.log4j.Logger;

/**
 * The RequestManager is a child of TimerTask. It is scheduled for periodic
 * execution in the Initializer. RequestManager sweeps through the list of notification
 * requests and queues them for sending. Once a request is in the queue, its
 * status is updated each time together and any necessary clean-up work is performed.
 *
 * @author David Dao 
 * @version $Id: RequestManager.java 287 2005-02-22 00:29:02Z sskracic $
 */

class RequestManager extends TimerTask {

    public static final String versionId =
        "$Id: RequestManager.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $,  $Date: 2004/08/16 $";

    private static final Logger s_log =
        Logger.getLogger(RequestManager.class.getName());

    // Persistence key attributes
    private static final String REQUEST_ID = "requestID";
    private static final String PARTY_TO   = "partyTo";
    private static final String NOTIFICATION_IN_QUEUE = "queued";

    // PDL Queries
    private static final String UPDATE_NOTIFICATION_SUCCESSFUL =
        "com.arsdigita.notification.UpdateNotificationSuccessful";
    private static final String UPDATE_NOTIFICATION_FAILURE =
        "com.arsdigita.notification.UpdateNotificationFailure";
    private static final String UPDATE_NOTIFICATION_PARTIAL_FAILURE =
        "com.arsdigita.notification.UpdateNotificationPartialFailure";
    private static final String GET_PENDING_NOTIFCATIONS =
        "com.arsdigita.notification.GetPendingNotifications";
    private static final String DELETE_NOTIFICATION_QUEUED =
        "com.arsdigita.notification.DeleteNotificationQueued";
    private static final String GET_COMPLETE_NOTIFICATIONS =
        "com.arsdigita.notification.GetCompleteNotifications";

    /**
     * Implements the run method of TimerTask. This is the execution block each time
     * the RequestManager runs. The following actions are performed:
     * <ul>
     * <li>Updates the status of requests, identifying which succeeded or failed
     * since the method was last run
     * <li>Sweeps processed requests from the queue
     * <li>Queues new pending requests
     * </ul>
     */

    public void run() {
        KernelExcursion rootExcursion = new KernelExcursion() {
                protected void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());
                    s_log.info("processing current requests.");
                    long time = System.currentTimeMillis();

                    /**
                     * Check and update status of requests that have already been queued.
                     */

                    boolean committedTxn = false;
                    Session session = SessionManager.getSession();
                    session.getTransactionContext().beginTxn();

                    try {
                        session.retrieveDataOperation(UPDATE_NOTIFICATION_SUCCESSFUL).execute();
                        session.retrieveDataOperation(UPDATE_NOTIFICATION_FAILURE).execute();

                        /**
                         * NOTE: I need to rewrite this query. Currently this query is working
                         * correctly if it executes after UPDATE_NOTIFICATION_SUCCESSFUL and
                         * UPDATE_NOTIFICATION_FAILURE.
                         */

                        session.retrieveDataOperation(UPDATE_NOTIFICATION_PARTIAL_FAILURE).execute();

                        // delete from nt_queue where status in ('sent', failed, partial-failure)

                        session.retrieveDataOperation(DELETE_NOTIFICATION_QUEUED).execute();

                        // Iterate through a list of notifications and invoke delete on each
                        // object.
                        // Why I am not using one query to do the delete?
                        // Because there is no DELETE CASCADE in the datamodel for nt_requests
                        // table.

                        DataQuery qry = session.retrieveQuery(GET_COMPLETE_NOTIFICATIONS);
                        while (qry.next()) {
                            try {
                                OID oid = new OID(Notification.BASE_DATA_OBJECT_TYPE, qry.get(REQUEST_ID));
                                Notification n = new Notification(oid);
                                n.delete();
                            } catch (DataObjectNotFoundException e) {
                                s_log.error("Retrieve complete notification", e);
                                // skip this notification.
                            }
                        }
                        session.getTransactionContext().commitTxn();
                        committedTxn = true;

                    } finally {
                        if (!committedTxn) {
                            session.getTransactionContext().abortTxn();
                        }
                    }

                    /**
                     * Tranfer pending requests to the outbound queue.
                     */

                    session.getTransactionContext().beginTxn();
                    committedTxn = false;

                    try {
                        DataQuery query = session.retrieveQuery(GET_PENDING_NOTIFCATIONS);

                        while (query.next()) {

                            BigDecimal requestID = (BigDecimal) query.get(REQUEST_ID);
                            BigDecimal partyTo = (BigDecimal) query.get(PARTY_TO);

                            try {

                                OID oid = new OID(Party.BASE_DATA_OBJECT_TYPE, partyTo);
                                Party party = (Party) DomainObjectFactory.newInstance(oid);

                                Notification notification = new Notification(new OID(Notification.BASE_DATA_OBJECT_TYPE, requestID));
                                notification.setStatus(NOTIFICATION_IN_QUEUE);
                                notification.save();

                                // Add notification to queue

                                Boolean isGroupExpand = notification.getExpandGroup();
                                boolean expand = true; // Default value for group expand

                                if (isGroupExpand != null)
                                    expand = isGroupExpand.booleanValue();

                                if ((party instanceof User) || (!expand)) {
                                    QueueItem queued = new QueueItem(notification, party);
                                    queued.save();
                                } else if (party instanceof Group) {
                                    // Expand group.
                                    Group group = (Group) party;
                                    UserCollection userCollection = group.getAllMemberUsers();
                                    while (userCollection.next()) {
                                        User user = userCollection.getUser();
                                        QueueItem queued = new QueueItem(notification, user);
                                        queued.save();
                                    }
                                }
                            } catch (Exception e) {
                                s_log.warn("RequestManager", e);
                                e.printStackTrace();
                            }
                        }

                        session.getTransactionContext().commitTxn();
                        committedTxn = true;

                    } finally {
                        if (!committedTxn) {
                            session.getTransactionContext().abortTxn();
                        }
                    }

                    time = System.currentTimeMillis() - time;
                    s_log.info("RequestManager executed in " + time + " ms.");
                }};

        try {
            rootExcursion.run();
        } catch (Throwable t) {
            s_log.error("Unexpected error occured. RequestManager disabled", t);
            throw new UncheckedWrapperException(t);
        }
    }
}
