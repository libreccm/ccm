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
package com.arsdigita.cms.lifecycle;

import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.runtime.ContextCloseEvent;

import org.apache.log4j.Logger;

/**
 * Initializes the Lifecycle package.
 *
 * Initializes the scheduler thread to fire all the events for the lifecycles
 * or phases that have just began or ended.
 *
 * A value of 0 for the delay parameter (see below) disables LC background thread.
 * This initializer is a sub-initializer of the cms initializer which adds it
 * to the list of initializers to be executed
 *
 * @author Peter Boy (pboy@barkhof.uni-bremen.de)
 * @version $Id: Initializer.java 2070 2010-01-28 08:47:41Z pboy $
 *
 */
public class Initializer extends com.arsdigita.runtime.GenericInitializer {

    // Creates a s_logging category with name = to the full name of class
    public static final Logger s_log = Logger.getLogger(Initializer.class);


    /**
     * 
     */
    public Initializer() {
    }


    /**
     * Implementation of the {@link Initializer#init(ContextInitEvent)}
     * method.
     *
     * Initializes the scheduler thread to fire all the events for the
     * lifecycles or phases that have just began or ended.
     *
     * A delay value of 0 inhibits start of processing.
     * @param evt The context init event.
     */
    public void init(ContextInitEvent evt) {
        s_log.debug("lifecycle background startup beginn.");

        LifecycleConfig conf = LifecycleConfig.getConfig();
        s_log.debug("lifecycle configuration loaded.");

        Integer delay = conf.getDelay();
            s_log.debug("delay configuration loaded. Value: " + delay  );
        Integer frequency = conf.getFrequency();
            s_log.debug("frequency configuration loaded. Value: " + frequency );

        if (delay > 0) {
            Scheduler.setTimerDelay(delay);
            Scheduler.setTimerFrequency(frequency);
            Scheduler.startTimer();
        }

        s_log.debug("lifecycle background processing started");
    }

    /**
     *
     */
    public void close(ContextCloseEvent evt) {
        Scheduler.stopTimer();
        s_log.debug("lifecycle background processing stopped");
    }

}
