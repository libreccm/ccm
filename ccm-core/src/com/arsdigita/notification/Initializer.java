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

import java.util.Timer;
import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import org.apache.log4j.Logger;

/**
 * Initializes three timer tasks to maintain the notification
 * service. These tasks handle the following:
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
 * @version $Id: Initializer.java 287 2005-02-22 00:29:02Z sskracic $
 * @since
 */

public class Initializer
    implements NotificationParameters,
               com.arsdigita.initializer.Initializer
{

    public static final String versionId = "$Id: Initializer.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";
    /**
     * For logging.
     */

    private static final Logger s_log =
        Logger.getLogger(Initializer.class);

    /**
     * Configuration.
     */

    private Configuration m_conf = new Configuration();

    /**
     * Timer threads.  Each one is started as a daemon.
     */

    private static Timer timer0 = new Timer(true);
    private static Timer timer1 = new Timer(true);
    private static Timer timer2 = new Timer(true);

    public Initializer() throws InitializationException {
        m_conf.initParameter(REQUEST_MANAGER_DELAY,
                             REQUEST_MANAGER_DELAY_DESCRIPTION,
                             Integer.class,
                             new Integer(0));

        m_conf.initParameter(REQUEST_MANAGER_PERIOD,
                             REQUEST_MANAGER_PERIOD_DESCRIPTION,
                             Integer.class,
                             new Integer(900));

        m_conf.initParameter(SIMPLE_QUEUE_DELAY,
                             SIMPLE_QUEUE_DELAY_DESCRIPTION,
                             Integer.class,
                             new Integer(0));

        m_conf.initParameter(SIMPLE_QUEUE_PERIOD,
                             SIMPLE_QUEUE_PERIOD_DESCRIPTION,
                             Integer.class,
                             new Integer(900));


        m_conf.initParameter(DIGEST_QUEUE_DELAY,
                             DIGEST_QUEUE_DELAY_DESCRIPTION,
                             Integer.class,
                             new Integer(0));

        m_conf.initParameter(DIGEST_QUEUE_PERIOD,
                             DIGEST_QUEUE_PERIOD_DESCRIPTION,
                             Integer.class,
                             new Integer(900));
    }

    /**
     * Returns the configuration object used by this initializer.
     */

    public final Configuration getConfiguration() {
        return m_conf;
    }

    /**
     * Starts up the notification service.
     */

    public void startup() {

        timer0.scheduleAtFixedRate(new DigestQueueManager(),
                                   getTimeMsec(DIGEST_QUEUE_DELAY),
                                   getTimeMsec(DIGEST_QUEUE_PERIOD));

        timer1.scheduleAtFixedRate(new SimpleQueueManager(),
                                    getTimeMsec(SIMPLE_QUEUE_DELAY),
                                    getTimeMsec(SIMPLE_QUEUE_PERIOD));

        timer2.scheduleAtFixedRate(new RequestManager(),
                                    getTimeMsec(REQUEST_MANAGER_DELAY),
                                    getTimeMsec(REQUEST_MANAGER_PERIOD));
    }

    /**
     * Helper method to lookup a timing key and convert its value to
     * milliseconds.
     *
     * @param key is the configuration parameter key
     */

    private long getTimeMsec(String key) {
        return ((Integer) m_conf.getParameter(key)).longValue() * 1000L;
    }

    /**
     * Shuts down the notification service.
     */

    public void shutdown() {
    }
}

/**
 * Private interface for storing constants
 */

interface NotificationParameters {

    public final static String REQUEST_MANAGER_DELAY =
        "RequestManagerDelay";
    public final static String REQUEST_MANAGER_DELAY_DESCRIPTION =
        "Request manager's delay in seconds.";
    public final static String REQUEST_MANAGER_PERIOD =
        "RequestManagerPeriod";
    public final static String REQUEST_MANAGER_PERIOD_DESCRIPTION =
        "Request manager's period in seconds";
    public final static String SIMPLE_QUEUE_DELAY =
        "SimpleQueueDelay";
    public final static String SIMPLE_QUEUE_DELAY_DESCRIPTION =
        "Simple queue's delay in seconds.";
    public final static String SIMPLE_QUEUE_PERIOD =
        "SimpleQueuePeriod";
    public final static String SIMPLE_QUEUE_PERIOD_DESCRIPTION =
        "Simple queue's period in seconds.";
    public final static String DIGEST_QUEUE_DELAY =
        "DigestQueueDelay";
    public final static String DIGEST_QUEUE_DELAY_DESCRIPTION =
        "Digest queue's delay in seconds.";
    public final static String DIGEST_QUEUE_PERIOD =
        "DigestQueuePeriod";
    public final static String DIGEST_QUEUE_PERIOD_DESCRIPTION =
        "Digest queue's period in seconds.";

}
