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


package com.arsdigita.cms.contentsection;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.LoaderConfig;
import com.arsdigita.cms.installer.ContentSectionSetup;
import com.arsdigita.cms.installer.Util;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.cms.workflow.UnfinishedTaskNotifier;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.ConfigError;
// import com.arsdigita.runtime.DataInitEvent;
import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.runtime.ContextCloseEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Timer;

import org.apache.log4j.Logger;


// CURRENT STATUS:
// (Simple) Migration of the Old Initializer code of this package to the new
// initializer system. Current goal is a pure replacement with as less code
// changes as possible.
// In a second step a restructure of the code will be done.

// Has to handle in future:
//  -- configuration of alert tasks
//  -- creation of additional content sections during restart

/**
 * XXX Reformulate according to the code development!
 * <p>Initializes a content section, registering a default workflow, lifecycle &
 * roles and adding the content types.
 *
 * <p>The initialization process takes several configuration
 * parameters. The <code>name</code> is the name of the content
 * section, the <code>types</code> is a list of content types
 * to register
 *
 * @author Daniel Berrange (berrange@redhat.com)
 * @author Michael Pih
 * @author pb
 * @version $Id: $
 */
public class Initializer extends CompoundInitializer {


    /** Creates a s_logging category with name = to the full name of class */
    private static Logger s_log = Logger.getLogger(Initializer.class);

    /** Local configuration object ContentSectionConfig containing parameters
        which may be changed each system startup.  */
    //  private static final LoaderConfig s_conf = LoaderConfig.getConfig();
    private static final LoaderConfig s_conf = new LoaderConfig();

    /** The Timer used to send Unfinished notifications  */
    private static Timer s_unfinishedTimer;


    public Initializer() {
      //final String url = RuntimeConfig.getConfig().getJDBCURL();
      //final int database = DbHelper.getDatabaseFromURL(url);
    }

//  /**
//   * An empty implementation of {@link Initializer#init(DataInitEvent)}.
//   *
//   * @param evt The data init event.
//   */
//  public void init(DataInitEvent evt) {
//  }

    /**
     * Initializes domain-coupling machinery, usually consisting of
     * registering object instantiators and observers.
     *
     */
    public void init(DomainInitEvent evt) {
        s_log.debug("CMS.installer.Initializer.init(DomainInitEvent) invoked");

        // Recursive invokation of init, is it really necessary??
        // On the other hand:
        // An empty implementations prevents this initializer from being executed.
        // A missing implementations causes the super class method to be executed,
        // which invokes the above added LegacyInitializer.
        // If super is not invoked, various other cms sub-initializer may not run.
        super.init(evt);


        /*
         * loadAlertPrefs loads a list of workflow tasks and associated events
         * from configuration file and fills a hashmap. No database operation.
         * Not a loader task!
         */
        // XXX Currently in ContenSectionSetup - has to be migrated !!
        //      setup.loadAlertPrefs((List) s_conf.getTaskAlerts());


        s_log.debug("CMS.installer.Initializer.init(DomainInitEvent) completed");
    }


    /**
     * Implementation of the {@link Initializer#init(ContextInitEvent)}
     * method.
     *
     * Initializes the scheduler thread to fire all the events for the
     * ......   that have just began or ended.
     *
     * A delay value of 0 inhibits start of processing.
     * @param evt The context init event.
     */
    public void init(ContextInitEvent evt) {
        s_log.debug("content section ContextInitEvent started");

        // XXX to be done yet!
        // Currently we have only one timer, but notification is handled
        // on a per section base. We have also only one set of timing parameters.
        // So we have to configure all sections in the same way.
//      s_unfinishedTimer = setup.startNotifierTask
//          (s_conf.getSendOverdueAlerts(),
//           s_conf.getTaskDuration(),
//           s_conf.getOverdueAlertInterval(),
//           s_conf.getMaxAlerts());

    
        s_log.debug("content section ContextInitEvent completed");
    }

    /**
     * Implementation of the {@link Initializer#init(ContextCloseEvent)}
     * method.
     *
     */
    public void close(ContextCloseEvent evt) {
        s_log.debug("content section ContextCloseEvent started");
        if (s_unfinishedTimer != null) {
            s_unfinishedTimer.cancel();
            s_unfinishedTimer = null;
        }
        s_log.debug("content section ContextCloseEvent completed");
    }


    /**
     * @param section  content section for which notifier should be started
     * @param sendOverdue
     * @param duration
     * @param alertInterval
     * @param max
     * @return
     */
    private final Timer startNotifierTask(
                                   ContentSection section,
                                   Boolean sendOverdue, Integer duration,
                                   Integer alertInterval, Integer max) {
        Timer unfinished = null;
        if (sendOverdue.booleanValue()) {
            if (duration == null || alertInterval == null || max == null) {
                s_log.info("Not sending overdue task alerts, " +
                           "required initialization parameters were not specified");
                return null;
            }
            // start the Timer as a daemon, so it doesn't keep the JVM from exiting
            unfinished = new Timer(true);
            UnfinishedTaskNotifier notifier = 
                    new UnfinishedTaskNotifier( section, duration.intValue(),
                                                alertInterval.intValue(),
                                                max.intValue());
            // schedule the Task to start in 5 minutes, at 1 hour intervals
            unfinished.schedule(notifier, 5L * 60 * 1000, 60L * 60 * 1000);
            s_log.info("Sending overdue alerts for tasks greater than " +
                       duration + " hours old");
        } else {
            s_log.info("Not sending overdue task alerts");
        }

        return unfinished;
    }

}
