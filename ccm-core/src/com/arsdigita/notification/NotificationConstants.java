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

/**
 * Common constants used the various notification classes.
 *
 * @author Ron Henderson 
 * @version $Id: NotificationConstants.java 287 2005-02-22 00:29:02Z sskracic $
 */

interface NotificationConstants {

    // keys for persistent data

    public static final String DIGEST_ID      = "digestID";
    public static final String REQUEST_ID     = "requestID";
    public static final String MESSAGE_ID     = "messageID";
    public static final String PARTY_TO       = "partyTo";
    public static final String PARTY_TO_ADDR  = "partyToAddr";
    public static final String PARTY_FROM     = "partyFrom";
    public static final String RETRY_COUNT    = "retryCount";
    public static final String SUCCESS        = "success";
    public static final String EXPAND_GROUP   = "expandGroup";
    public static final String REQUEST_DATE   = "requestDate";
    public static final String FULFILL_DATE   = "fulfillDate";
    public static final String STATUS         = "status";
    public static final String MAX_RETRIES    = "maxRetries";
    public static final String EXPUNGE_P      = "expunge";
    public static final String EXPUNGE_MSG_P  = "expungeMessage";
    public static final String HEADER         = "header";
    public static final String SIGNATURE      = "signature";
    public static final String SUBJECT        = "subject";
    public static final String SEPARATOR      = "separator";
    public static final String FREQUENCY      = "frequency";
    public static final String NEXT_RUN       = "nextRun";

    // various states that a notification can be in

    public static final String PENDING        = "pending";
    public static final String QUEUED         = "queued";
    public static final String SENT           = "sent";
    public static final String FAILED         = "failed";
    public static final String FAILED_PARTIAL = "failed_partial";
    public static final String CANCELLED      = "cancelled";

    // special queries

    public static final String GET_SIMPLE_QUEUED_NOTIFICATIONS =
        "com.arsdigita.notification.GetSimpleQueuedNotifications";
    public static final String GET_DIGEST_QUEUED_NOTIFICATIONS =
        "com.arsdigita.notification.GetDigestQueuedNotifications";
}
