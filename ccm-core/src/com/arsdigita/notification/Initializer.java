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
package com.arsdigita.notification;

import com.arsdigita.runtime.ContextCloseEvent;
import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.runtime.GenericInitializer;

import java.util.Timer;

import org.apache.log4j.Logger;

/**
 * Initializes the Notification package.
 *
 * Initializes three timer tasks to maintain the notification service. These
 * tasks handle the following:
 * <UL>
 * <LI><b>RequestManager</b> schedules new requests for a notification
 * in the outbound mail queue, and updates the status of items in the
 * request table that have already been processed.
 *
 * <LI><b>SimpleQueueManager</b> processes messages in the outbound
 * mail queue that are not part of a digest.
 *
 * <LI><b>DigestQueueManager</b> processes messages in the outbound
 * mail queue that are part of a digest.
 * </ul>
 *
 * @author David Dao
 * @author Peter Boy (pboy@barkhof.uni-bremen.de)
 * @version $Id: $
 */
public class Initializer extends GenericInitializer {

    // Creates a s_logging category with name = to the full name of class
    public static final Logger s_log = Logger.getLogger(Initializer.class);

    // Timer threads.  Each one is started as a daemon.
    /**
     * Schedules new requests for a notification in the outbound mail queue
     * and updates the status of items in the request table that have already
     * been processed..
     */
    private static Timer NotificationRequestManagerTimer = new Timer(true);

    /**
     * Processes messages in the outbound mail queue that are part of a digest.
     */
    private static Timer NotificationDigestQueueTimer = new Timer(true);
    
    /**
     * processes messages in the outbound mail queue that are not part of a digest.
     */
    private static Timer NotificationSimpleQueueTimer = new Timer(true);


    /**
     * Default (empty) Constructor
     */
    public Initializer() {
//      s_log.debug("notification initializer instantiated.");
    }

    /**
     * Implementation of the {@link Initializer#init(ContextInitEvent)}.
     *
     * Start various background threads for notification service which are
     * needed for a proper servlet container context operation.
     *
     * @param evt The context init event.
     **/
    public void init(ContextInitEvent evt) {
        s_log.debug("notification background startup begin.");

        NotificationConfig conf = NotificationConfig.getConfig();
        s_log.debug("Notification configuration loaded.");

        NotificationRequestManagerTimer.scheduleAtFixedRate(
                                   new RequestManager(),
                                   conf.getRequestManagerDelay(),
                                   conf.getRequestManagerPeriod()
                                  );

        NotificationDigestQueueTimer.scheduleAtFixedRate(
                                new DigestQueueManager(),
                                conf.getDigestQueueDelay(),
                                conf.getDigestQueuePeriod()
                               );

        NotificationSimpleQueueTimer.scheduleAtFixedRate(
                                new SimpleQueueManager(),
                                conf.getSimpleQueueDelay(),
                                conf.getSimpleQueuePeriod()
                               );

        s_log.debug("notification background processing started");
    }

    /**
     * Implementation of the {@link Initializer#close(ContextCloseEvent)}.
     *
     * Stops background threads started during initialization so the servlet
     * container can terminate the applications main thread.
     */
    public void close(ContextCloseEvent evt) {

        NotificationSimpleQueueTimer.cancel();
        NotificationDigestQueueTimer.cancel();
        NotificationRequestManagerTimer.cancel();

        s_log.debug("Notification background processing stopped");
    }

}
