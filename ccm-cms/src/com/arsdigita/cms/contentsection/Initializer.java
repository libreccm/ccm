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
import com.arsdigita.cms.ContentSectionCollection;
import com.arsdigita.cms.workflow.UnfinishedTaskNotifier;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.kernel.ACSObjectInstantiator;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.runtime.CompoundInitializer;
import com.arsdigita.runtime.ContextInitEvent;
import com.arsdigita.runtime.ContextCloseEvent;
// import com.arsdigita.runtime.DataInitEvent;
import com.arsdigita.runtime.DomainInitEvent;
import com.arsdigita.web.Application;
import com.arsdigita.cms.workflow.CMSTask;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.Vector;

import org.apache.log4j.Logger;


/**
 * Initializes the content section sub-package of the CMS package (module).
 *
 * XXX Reformulate according to the code development!
 * This initializer performs:
 * - check whether to create an additional content sections during restart
 * - initializes alert preferences for each content section
 * - initializes overdue alerts for each content section
 *
 * In the (hopefully) near future:
 * Content section specific tasks of cms.Initializer will be moved into this
 * Initializer.
 *
 *
 * @author Daniel Berrange (berrange@redhat.com)
 * @author Michael Pih
 * @author pboy (pb@zes.uni-bremen.de)
 * @version $Id: Initializer.java $
 */
public class Initializer extends CompoundInitializer {


    /** Creates a s_logging category with name = to the full name of class    */
    private static Logger s_log = Logger.getLogger(Initializer.class);

    /** Configuration object ContentSectionConfig containing parameters
        which may be changed each system startup.                             */
    private static final ContentSectionConfig s_conf = ContentSectionConfig
                                                       .getInstance();

    /** The Timer used to send Unfinished notifications  */
    private static Vector s_unfinishedTimers = new Vector();


    public Initializer() {
      //final String url = RuntimeConfig.getConfig().getJDBCURL();
      //final int database = DbHelper.getDatabaseFromURL(url);
    }

//  Currently nothing to do here. May change during the ongoing migration process
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
     * Here additionally checks whether to create a new content section.
     */
    @Override
    public void init(DomainInitEvent evt) {
        s_log.debug("contentsection.Initializer.init(DomainInitEvent) invoked");

        // Recursive invokation of init!
        // An empty implementations prevents this initializer from being executed.
        super.init(evt);

        /* Register object instantiator for ContentSection                   */
        evt.getFactory().registerInstantiator
            (ContentSection.BASE_DATA_OBJECT_TYPE,
             new ACSObjectInstantiator() {
                 @Override
                 public DomainObject doNewInstance(DataObject dobj) {
                     return new ContentSection(dobj);
                 }
             } );

             // whether we have to create an additional (new) content section
             // specified in config file.
             checkForNewContentSection();

        s_log.debug("contentsection.Initializer.init(DomainInitEvent) completed");
    }


    /**
     * Implementation of the {@link Initializer#init(ContextInitEvent)}
     * method.
     *
     * Steps through all installed content sections and for each section
     * - initializes the alert preferences
     * - initializes the scheduler background thread to fire all alert events.
     *
     * A delay value of 0 inhibits start of processing.
     * @param evt The context init event.
     */
    @Override
    public void init(ContextInitEvent evt) {
        s_log.debug("content section ContextInitEvent started");

        super.init(evt);

        // Currently we have only one set of both alert preference configuration
        // and timer configuration. Notification is handled on a per section
        // base, so we have to configure all sections in the same way.
        // TODO: Store alerts prefs as well as timer configuration for each
        // content section and make it configurable in the UI.
        // For now we step through all sections and configure them the same way.
        ContentSectionCollection sections=ContentSection.getAllSections();
        while( sections.next() ) {
            ContentSection section = sections.getContentSection();

            // Initialize workflow tasks and associated events from configuration
            // file filling a hashmap.
            initializeTaskAlerts(section, s_conf.getTaskAlerts() );

            // create a standard java util timer object
            Timer unfinishedTimer = startNotifierTask(
                                                section,
                                                s_conf.getSendOverdueAlerts(),
                                                s_conf.getTaskDuration(),
                                                s_conf.getAlertInterval(),
                                                s_conf.getMaxAlerts()
                                                     );
            if ( unfinishedTimer != null) {
                s_unfinishedTimers.addElement(unfinishedTimer);
            }
        }
        sections.close();
    
        s_log.debug("content section ContextInitEvent completed");
    }

    /**
     * Implementation of the {@link Initializer#init(ContextCloseEvent)}
     * method.
     *
     * Stops various background threads started during startup process.
     */
    @Override
    public void close(ContextCloseEvent evt) {
        s_log.debug("content section ContextCloseEvent started");

        Timer unfinishedTimer = null;
        if (s_unfinishedTimers.size() > 0) {
            for (Enumeration el=s_unfinishedTimers.elements();
                             el.hasMoreElements(); ) {
                unfinishedTimer = (Timer) el.nextElement();
                if(unfinishedTimer != null) unfinishedTimer.cancel();
                unfinishedTimer = null;
            // s_unfinishedTimer = null;
            }

        }
        s_log.debug("content section ContextCloseEvent completed");
    }

    private void checkForNewContentSection() {

        // Check here weather a new content section has to be created.
        String newSectionName = s_conf.getNewContentSectionName();
        if (newSectionName != null && !newSectionName.isEmpty() ) {
            ContentSectionCollection sections=ContentSection.getAllSections();
            sections.addEqualsFilter( Application.PRIMARY_URL,
                                      "/" + newSectionName + "/" );
            ContentSection section;
            if( sections.next() ) {
                // Section with the configured name already exists
                s_log.warn( "Content section " + newSectionName +
                            " already exists, skipping creation task.\n" +
                            "You may delete the entry from configuration file.");
                section = sections.getContentSection();
                sections.close();
            } else {
                s_log.info( "Content section " + newSectionName + " in " +
                            " doesn't exist, creating it." );
                TransactionContext txn = SessionManager.getSession()
                                                       .getTransactionContext();
                txn.beginTxn();
                ContentSectionSetup.setupContentSectionAppInstance
                                    (newSectionName,
                                     s_conf.getStuffGroup(),
                                     s_conf.isPubliclyViewable(),
                                     s_conf.getItemResolverClass(),
                                     s_conf.getTemplateResolverClass(),
                                     s_conf.getContentSectionsContentTypes(),
                                     s_conf.getUseSectionCategories(),
                                     s_conf.getCategoryFileList()
                                    );
                txn.commitTxn();
            }
        }

    }

    /**
     * Steps through a string array of tasks and associated alert events
     * creating section specific CMStasks from configuration file.
     *
     * Note: Tasks are created on a per section base, but we have currently no
     * way to store different values for each section. So all sections are
     * configured equal.
     *
     * @param section  A section object
     * @param taskAlerts An array of tasks and associated events
     */
    public void initializeTaskAlerts(ContentSection section,
                                     String[] taskAlerts) {

        if (taskAlerts != null) {
            for (int i=0,n=taskAlerts.length; i<n; i++) {
                StringTokenizer tok = new StringTokenizer(taskAlerts[i],":");
                try {
                    String taskName = tok.nextToken();
                    while (tok.hasMoreTokens()) {
                        String operation = tok.nextToken();
                        CMSTask.addAlert(section, taskName, operation);
                    }
                } catch (NoSuchElementException nsee) {
                    s_log.warn("Invalid task alerts definition");
                }
            }
        }

    }


    /**
     * @param section  content section for which notifier should be started
     * @param sendOverdue
     * @param duration
     * @param alertInterval
     * @param max
     * @return
     */
    private Timer startNotifierTask( ContentSection section,
                                     Boolean sendOverdue,
                                     Integer duration,
                                     Integer alertInterval,
                                     Integer max
                                   ) {
        Timer unfinished = null;
        if (sendOverdue.booleanValue()) {
            if (duration == null || alertInterval == null || max == null) {
                s_log.info("Not sending overdue task alerts, " +
                           "required initialization parameters were not specified");
                return null;
            }
            // start the Timer as a daemon, so it doesn't keep the JVM from exiting
            unfinished = new Timer(true);
            UnfinishedTaskNotifier notifier = new UnfinishedTaskNotifier(
                                                      section,
                                                      duration.intValue(),
                                                      alertInterval.intValue(),
                                                      max.intValue() );
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
