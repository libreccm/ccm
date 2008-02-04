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

import com.arsdigita.initializer.Configuration;
import com.arsdigita.initializer.InitializationException;
import org.apache.log4j.Logger;

/**
 * Initializes the scheduler thread to fire all the events for the
 * lifecycles or phases that have just began or ended.
 *
 * Where the initializer is registered, you need to include the
 * delay and frequency in seconds
 *
 * @author Jack Chung
 * @version $Revision: #9 $ $DateTime: 2004/08/17 23:15:09 $
 */

public class Initializer
    implements com.arsdigita.initializer.Initializer {

    public static final String versionId = "$Id: Initializer.java 1292 2006-08-25 17:55:03Z apevec $ by $Author: apevec $, $DateTime: 2004/08/17 23:15:09 $";

    private static Logger s_log =
        Logger.getLogger(Initializer.class);

    private Configuration m_conf = new Configuration();

    private Scheduler scheduler;

    public Initializer() throws InitializationException {
        // XXX move to ccm registry
        m_conf.initParameter("delay",
                             "The delay of the scheduler in seconds, 0 to disable LC background thread",
                             Integer.class);
        m_conf.initParameter("frequency",
                             "The frequency of the scheduler in seconds",
                             Integer.class);
    }

    /**
     * Returns the configuration object used by this initializer.
     **/
    public Configuration getConfiguration() {
        return m_conf;
    }


    /**
     * Called on startup.
     **/
    public void startup() {

        long delay = ((Integer) m_conf.getParameter("delay")).longValue();
        long frequency = ((Integer) m_conf.getParameter("frequency")).longValue();

        if (delay > 0) {
            Scheduler.s_timerDelay = delay * (long) 1000;
            Scheduler.s_timerFrequency = frequency * (long) 1000;
            Scheduler.startTimer();
        }
    }

    /**
     * Called on shutdown. It's probably not a good idea to depend on this
     * being called.
     **/
    public void shutdown() {
        Scheduler.stopTimer();
    }

}
