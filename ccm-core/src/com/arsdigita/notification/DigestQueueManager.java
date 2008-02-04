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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.kernel.User;
import com.arsdigita.mail.Mail;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimerTask;

/**
 * Processes the digest messages in the queue.
 *
 * @author David Dao 
 * @author Ron Henderson 
 * @version $Id: DigestQueueManager.java 287 2005-02-22 00:29:02Z sskracic $
 */

class DigestQueueManager extends TimerTask
    implements NotificationConstants
{
    public static final String versionId = "$Id: DigestQueueManager.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $,  $Date: 2004/08/16 $";

    private static final Logger log =
        Logger.getLogger(DigestQueueManager.class);

    // String catalog for digest message.  Should be globalized.

    private static final String FROM    = "From: ";
    private static final String SUBJECT = "Subject: ";

    /**
     * Processes one sweep of the DigestQueueManager.
     */

    public void run() {
        log.info("processing current requests");

        long time = System.currentTimeMillis();

        HashMap digestMap = new HashMap();

        boolean committedTxn = false;
        Session session = SessionManager.getSession();
        session.getTransactionContext().beginTxn();

        try {

            /**
             * Retrieve all digests to be processed during this run.
             * Store all notifications in double hash map using
             * digestID and partyTo as key.
             */

            DataQuery query = session.retrieveQuery(GET_DIGEST_QUEUED_NOTIFICATIONS);

            while (query.next()) {

                BigDecimal digestID  = (BigDecimal) query.get(DIGEST_ID);
                BigDecimal partyTo   = (BigDecimal) query.get(PARTY_TO);
                BigDecimal requestID = (BigDecimal) query.get(REQUEST_ID);

                HashMap partyMap = (HashMap) digestMap.get(digestID);

                if (!digestMap.containsKey(digestID)) {
                    digestMap.put(digestID, new HashMap());
                }

                partyMap = (HashMap) digestMap.get(digestID);

                if (!partyMap.containsKey(partyTo)) {
                    partyMap.put(partyTo, new ArrayList());
                }

                ((ArrayList) partyMap.get(partyTo)).add(requestID);
            }

            // Now we have a list of all digests that need to be
            // processed, and for each digest a list of parties with
            // messages.  We execute the following loop to generate
            // the outbound emails:
            //
            //     foreach digest {
            //         foreach party {
            //            mail = new Mail()
            //            foreach message {
            //                add to mail
            //            }
            //            mail.send()
            //         }
            //     }
            //

            Iterator digestIDIter = digestMap.keySet().iterator();

            while (digestIDIter.hasNext()) {

                BigDecimal digestID = (BigDecimal) digestIDIter.next();

                Digest digest;
                try {
                    digest = new Digest(digestID);
                } catch (DataObjectNotFoundException e) {
                    log.warn("failed to retrieved digest " + digestID);
                    continue;
                }

                // These are common to all emails

                String from      = digest.getFromEmail();
                String subject   = digest.getSubject();
                String header    = StringUtils.addNewline(digest.getHeader());
                String signature = StringUtils.addNewline(digest.getSignature());
                String separator = StringUtils.addNewline(digest.getSeparator());

                HashMap  partyMap    = (HashMap) digestMap.get(digestID);
                Iterator partyIDIter = partyMap.keySet().iterator();

                while (partyIDIter.hasNext()) {

                    BigDecimal partyID = (BigDecimal) partyIDIter.next();

                    String to;
                    try {
                        to = User.retrieve(partyID).getPrimaryEmail().toString();
                    } catch (DataObjectNotFoundException e) {
                        log.warn("failed to retrieve user " + partyID);
                        continue;
                    }

                    // Retrieve all messages and compose into one:
                    //
                    // << header >>
                    //
                    // << separator >>
                    //
                    // From: << msg.getFrom() >>
                    // Subject: << msg.getSubject() >>
                    //
                    // << msg.getBody() >>
                    //
                    // << separator >>
                    //
                    //    ...
                    //
                    // << separator >>
                    //
                    // << signature >>

                    ArrayList requestIDList = (ArrayList) partyMap.get(partyID);
                    ArrayList noticeList = new ArrayList();

                    String nl = System.getProperty("line.separator");

                    Mail mail = new Mail(to, from, subject);

                    StringBuffer body = new StringBuffer();
                    body.append(header);
                    body.append(separator);

                    for (int i = 0; i < requestIDList.size(); i++) {

                        BigDecimal requestID = (BigDecimal) requestIDList.get(i);

                        try {

                            OID oid = new OID(QueueItem.BASE_DATA_OBJECT_TYPE);
                            oid.set(REQUEST_ID, requestID);
                            oid.set(PARTY_TO, partyID);

                            QueueItem notice = new QueueItem(oid);
                            noticeList.add(notice);

                            // Add the content of this notification to
                            // the body of the email going out.

                            body.append(FROM);
                            body.append(notice.getFrom());
                            body.append(nl);

                            body.append(SUBJECT);
                            body.append(notice.getSubject());
                            body.append(nl).append(nl);

                            body.append(StringUtils.addNewline(notice.getBody()));
                            body.append(separator);

                            // Handle attachments

                            try {
                                notice.addAttachments(mail);
                            } catch (javax.mail.MessagingException ex) {
                                log.warn("error handling attachments", ex);
                            }

                        } catch (DataObjectNotFoundException ex) {
                            log.warn("error handling notification", ex);
                            continue; // skip this request
                        }
                    }

                    body.append(signature);

                    try {

                        mail.setBody(body.toString());
                        mail.send();

                        // Update notifications in queue to successful
                        // status.

                        for (int i = 0; i < noticeList.size(); i++) {
                            QueueItem n = (QueueItem) noticeList.get(i);
                            n.setSuccess(Boolean.TRUE);
                            n.save();
                        }

                    } catch (MessagingException e) {

                        log.warn("Failed to send digest notification",e);

                        // Increase retry count for all queued
                        // notifications belonging to this digest. Failed
                        // notification in a digest will rerun the
                        // next time the digest is processed.

                        for (int i = 0; i < noticeList.size(); i++) {
                            QueueItem n = (QueueItem) noticeList.get(i);
                            n.setSuccess(Boolean.FALSE);
                            n.incrRetryCount();
                            n.save();
                        }
                    }
                }

                digest.updateNextRun();
                digest.save();
            }

            session.getTransactionContext().commitTxn();
            committedTxn = true;

        } catch(Throwable t) {
            log.error("Unexpected error occured. DigestQueueManager disabled", t);
            throw new UncheckedWrapperException(t);
        } finally {
            if (!committedTxn) {
                session.getTransactionContext().abortTxn();
            }
        }

        time = System.currentTimeMillis() - time;
        log.info("DigestQueueManager completed in " + time + " ms.");
    }

}
