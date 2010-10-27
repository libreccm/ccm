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
package com.arsdigita.cms.workflow;

import com.arsdigita.cms.ContentSection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.TransactionContext;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Date;
import java.util.TimerTask;

/**
 * A TimerTask that will send notifications to people
 * if a Workflow Task has not been finished in a timely manner.
 **/
public class UnfinishedTaskNotifier extends TimerTask {
    private static final Logger s_log = Logger.getLogger(UnfinishedTaskNotifier.class);

    // the ID of the ContentSection we're sending notifications for
    private BigDecimal m_sectionID;
    // how long a Task must remain unfinished before a notification is
    // sent out, in milliseconds
    private long m_unfinishedInterval;
    // the length of time between each notification of an unfinished task,
    // in milliseconds
    private long m_notificationInterval;
    // the maximum number of notifications sent for a single unfinished task
    private int m_maxNotifications;

    /**
     * Create a new UnfinishedTaskNotifier
     *
     * @param section The section to send notifications for
     * @param unfinishedInterval How long a Task is idle before we
     * send notifications (in hours)
     * @param notificationInterval How long between sending
     * notifications about the same task (in hours)
     * @param maxNotifications max number of notifications to send about one task
     **/
    public UnfinishedTaskNotifier(ContentSection section,
                                  int unfinishedInterval,
                                  int notificationInterval,
                                  int maxNotifications) {
        super();
        Assert.exists(section, "the ContentSection to send notifications for");
        m_sectionID = section.getID();
        m_unfinishedInterval = (long) unfinishedInterval * 60 * 60 * 1000;
        m_notificationInterval = (long) notificationInterval * 60 * 60 * 1000;
        m_maxNotifications = maxNotifications;
        s_log.debug("Created UnfinishedTaskNotifier for section " + section.getName());
        s_log.debug("Sending notifications for tasks over " + m_unfinishedInterval +
                    " milliseconds old, with repeat notifications every " + 
                    m_notificationInterval + " milliseconds, with a maxmimum of " +
                    m_maxNotifications + " notifications going out for every task.");

    }

    /**
     * The action to be performed by this timer Task.
     */
    public void run() {
        Thread.currentThread().setName("unfinished-notifier");
        try {
            new KernelExcursion() {
                protected final void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());

                    internalRun();
                }
            }.run();
        } catch (Throwable t) {
            s_log.error("Unexpeced error occured in UnfinishedTaskNotifier." +
                        "Task has been disabled", t);
            throw new UncheckedWrapperException(t);
        }
    }

    private void internalRun() {
        s_log.debug("Running UnfinishedTaskNotifier");

        boolean committedTxn = false;
        TransactionContext txn =
            SessionManager.getSession().getTransactionContext();
        long startTime = System.currentTimeMillis();

        try {
            txn.beginTxn();

            DataQuery unfinishedTasks =
                SessionManager.getSession().
                retrieveQuery("com.arsdigita.cms.workflow.getUnfinishedTasks");
            unfinishedTasks.setParameter("sectionID", m_sectionID);
            // we want to do the Date arithmetic in Java, since we set
            // the Date from Java initially
            unfinishedTasks.setParameter("overdueDate",
                   new Date(System.currentTimeMillis() - m_unfinishedInterval));

            while (unfinishedTasks.next()) {
                CMSTask task = (CMSTask) DomainObjectFactory
                    .newInstance((DataObject) unfinishedTasks.get("task"));
                UnfinishedNotification notification = UnfinishedNotification.
                    retrieveForTask(task);
                Date lastSentDate = notification.getLastNotificationDate();
                int numSent = notification.getNumNotifications();
                if ((lastSentDate == null ||
                     lastSentDate.before(new Date(System.currentTimeMillis() -
                                                  m_notificationInterval)))
                    && m_maxNotifications > numSent) {
                    s_log.debug("Sending unfinished notification for " + task +
                                ", started on " + task.getStartDate());
                    // make sure we record that we've sent this notification first
                    notification.incrNumNotifications();
                    notification.save();
                    task.sendUnfinishedNotification();
                }
            }

            txn.commitTxn();
            committedTxn = true;
            s_log.debug("UnfinishedTaskNotifier completed in " +
                        (System.currentTimeMillis() - startTime) + "ms");
        } finally {
            if (!committedTxn) {
                txn.abortTxn();
            }
        }
    }

}

class UnfinishedNotification extends DomainObject {
    static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.workflow.UnfinishedNotification";
    private static final String ID = "id";
    private static final String LAST_SENT_DATE = "lastSentDate";
    private static final String NUM_SENT = "numSent";

    // retrieve an existing UnfinishedNotification
    private UnfinishedNotification(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    // create a new UnfinishedNotification for the given Task
    private UnfinishedNotification(CMSTask task) {
        super(BASE_DATA_OBJECT_TYPE);
        set(ID, task.getID());
    }

    public static UnfinishedNotification retrieveForTask(CMSTask task) {
        try {
            // retrieve the UnfinishedNotification for this CMSTask
            return new UnfinishedNotification(new OID(BASE_DATA_OBJECT_TYPE, task.getID()));
        } catch (DataObjectNotFoundException de) {
            // couldn't find one; create a new one
            return new UnfinishedNotification(task);
        }
    }

    public Date getLastNotificationDate() {
        return (Date) get(LAST_SENT_DATE);
    }

    public int getNumNotifications() {
        Integer num = (Integer) get(NUM_SENT);
        if (num != null) {
            return num.intValue();
        } else {
            return 0;
        }
    }

    public void incrNumNotifications() {
        int num = getNumNotifications();
        num++;
        set(NUM_SENT, new Integer(num));
        set(LAST_SENT_DATE, new Date());
    }
}
