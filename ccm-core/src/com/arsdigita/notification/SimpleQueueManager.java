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

import com.arsdigita.mail.Mail;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;

import java.math.BigDecimal;
import java.util.TimerTask;
import org.apache.log4j.Logger;


/**
 * Timer thread that processes all non-digest messages in the outbound
 * message queue.
 *
 * @author David Dao 
 * @author Ron Henderson 
 * @version $Id: SimpleQueueManager.java 1513 2007-03-22 09:09:03Z chrisgilbert23 $
 */

class SimpleQueueManager extends TimerTask
    implements NotificationConstants
{

    public static final String versionId = "$Id: SimpleQueueManager.java 1513 2007-03-22 09:09:03Z chrisgilbert23 $ by $Author: chrisgilbert23 $,  $Date: 2004/08/16 $";

    private static final Logger s_log =
        Logger.getLogger(SimpleQueueManager.class.getName());

    /**
     * Performs one sweep of the SimpleQueueManager.
     */

    public void run() {

        s_log.info("processing current requests");
        long time = System.currentTimeMillis();

        boolean committedTxn = false;
        Session session = SessionManager.getSession();
        session.getTransactionContext().beginTxn();
        DataQuery query = null;
        try {

            query = session.retrieveQuery(GET_SIMPLE_QUEUED_NOTIFICATIONS);

            while (query.next()) {

                BigDecimal requestID = (BigDecimal) query.get(REQUEST_ID);
                BigDecimal partyTo   = (BigDecimal) query.get(PARTY_TO);

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Process request " + requestID + 
                                " party " + partyTo);
                }

                QueueItem notice;
                try {
                    OID oid = new OID(QueueItem.BASE_DATA_OBJECT_TYPE);
                    oid.set(REQUEST_ID, requestID);
                    oid.set(PARTY_TO, partyTo);
                    notice = new QueueItem(oid);
                } catch (DataObjectNotFoundException ex) {
                    // Could not find this notification in the queue.
                    s_log.warn("unable to retrieve notification", ex);
                    continue;
                }

                // Construct an email for this notification and try to
                // send it.

                Boolean success;

                if (s_log.isDebugEnabled()) {
                    s_log.debug("To: " +  notice.getTo() + 
                                " From: " + notice.getFrom());
                }

                try {

                    Mail mail = new Mail();
                    mail.setTo(notice.getTo());
                    mail.setFrom(notice.getFrom());
                    mail.setSubject(notice.getSubject());
                    notice.setBody(mail);

                    String messageID = notice.getMessage().getRFCMessageID();
                    if (messageID != null) {
                        mail.setMessageID(messageID);
                    }
                    String replyToAddr = notice.getMessage().getReplyTo();
                    if (replyToAddr != null) {
                        mail.setReplyTo(replyToAddr);
                    }
                    // Handle attachments

                    try {
                        notice.addAttachments(mail);
                    } catch (javax.mail.MessagingException ex) {
                        s_log.warn("error handling attachments", ex);
                    }

                    mail.send();

                    success = Boolean.TRUE;

                } catch (DataObjectNotFoundException ex) {
                    s_log.warn("notification failed: ", ex);
                    success = Boolean.FALSE;
                } catch (javax.mail.MessagingException ex) {
                    s_log.warn("notification failed: ", ex);
                    success = Boolean.FALSE;
                }

                // If the notification failed for some reason,
                // note it in the log file and increment the retry
                // count.  Either way we need to update its status
                // in the queue.

                if (!success.booleanValue()) {
                    notice.incrRetryCount();
                }

                notice.setSuccess(success);
                notice.save();
            }

            session.getTransactionContext().commitTxn();
            committedTxn = true;

        } catch(Throwable t) {
            s_log.error("Unexpected error occured. SimpleQueueManager has been disabled.", t);
            throw new UncheckedWrapperException(t);
        } finally {
            if (!committedTxn) {
                session.getTransactionContext().abortTxn();
            }
            if(query != null) {
                query.close();
            }
        }

        time = System.currentTimeMillis() - time;
        s_log.info("SimpleQueueManager executed in " + time + " ms.");
    }
}
