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
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.messaging.Message;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>The <code>Notification</code> class is used to create and send
 * messages via email to ACS users and groups.  It acts as a wrapper
 * for a {@link Message} which contains the subject, sender, body and
 * any attachments for the email.  The recipient can be a {@link
 * com.arsdigita.kernel.User} or a {@link com.arsdigita.kernel.Group}.
 * In the case of Group, the message can be sent to the group's email
 * address or expanded into a separate message for each member of the
 * group.
 *
 * <h4>Email Alerts</h4>
 *
 * <p>When using notifications for email alerts, applications often
 * need to wrap a special header and signature around the contained
 * Message object.  This can be useful for including introductory
 * remarks and action links in the email body.  The
 * <code>setHeader</code> and <code>setSignature</code> methods allow
 * you to do this without the need to create a separate Message for
 * the modified email.
 *
 * <p>For example, a bboard application might want to include a link
 * so users can unsubscribe from the bboard.  The alert processing
 * code for the bboard can include this information in the alert as
 * part of the signature:</p>
 *
 * <pre>
 * notice.setMessage(msg);
 * notice.setHeader
 *     ("Posted by: " + userName + "\n" +
 *      "Topic    : " + topic + "\n" +
 *      "Subject  : " + msg.getSubject() + "\n");
 * notice.setSignature
 *     ("To post a response, come back to the forum:\n" +
 *       forumURL +
 *      "Use the following URL to disable the alert that sent you this message:\n"
 *       forumUnsubscribeURL);
 * notice.save();
 * </pre>
 *
 * <h4>Digests</h4>
 *
 * <p>Finally, notifications can be sent in "instant processing mode"
 * or as part of a {@link Digest}.  When sent as part of a digest all
 * notifications to the same recipient are collected into a single
 * email and sent at regular internal.  For example, an hourly digest
 * might send a user all of their workflow task updates that have
 * changed in the past hour, rather a much larger number of individual
 * messages everytime an tasks changed.
 *
 * @author Ron Henderson 
 * @author David Dao 
 * @version $Id: Notification.java 287 2005-02-22 00:29:02Z sskracic $ */

public class Notification extends ACSObject implements NotificationConstants {
    // Base DataObject type

    public static final String BASE_DATA_OBJECT_TYPE =
        Notification.class.getName();

    /**
     * Creates an empty <code>Notification</code>.  This constructor
     * is invoked by all others to initialize the following default
     * parameters:
     *
     * <ol>
     * <li>REQUEST_DATE to the current time
     * <li>MAX_RETRIES to three (3)
     * <li>STATUS to "pending"
     * <li>EXPUNGE_P to FALSE (do not delete after processing)
     * <li>EXPAND_GROUP to TRUE (send to group members)
     * </ol>
     *
     * Any of these defaults can be overridden in a specialized
     * constructor or by calling the appropriate methods to change
     * these parameter settings.
     */

    public Notification() {
        super(BASE_DATA_OBJECT_TYPE);

        set(REQUEST_DATE, new Date());
        set(MAX_RETRIES, new Integer(3));
        setStatus(PENDING);
        setIsPermanent(Boolean.TRUE);
        setExpandGroup(Boolean.TRUE);
    }

    /**
     * Creates a notification by supplying the digest, sender,
     * receiver, subject, and body of the message.
     *
     * @param digest the digest this notification is part of
     * @param to the party receiving the message
     * @param from the party sending the message
     * @param subject the subject of the message
     * @param body the body of the message
     */

    public Notification(Digest digest, Party to, Party from,
                        String subject, String body) {
        this(to,from,subject,body);
        setDigest(digest);
    }

    /**
     * Creates a notification by supplying the digest, the receiver, and an existing
     * message to send.
     *
     * @param digest notification belongs to this digest
     * @param to the party recieving the message
     * @param msg the message to send
     */

    public Notification(Digest digest, Party to, Message msg) {
        this(to,msg);
        setDigest(digest);
    }

    /**
     * Creates a notification by supplying the sender, receiver,
     * subject, and body of the message.  Creates an internal {@link Message}
     * object to store this information and sets the MessageDelete
     * flag to <code>true</code> so the internal object is deleted when
     * the notification is deleted.  Also sets the isPermanent flag to
     * <code>false</code> so the notification will be deleted after
     * processing.
     *
     * @param to the party receiving the message
     * @param from the party sending the message
     * @param subject the subject of the message
     * @param body the body of the message
     */

    public Notification(Party to, Party from, String subject, String body) {
        this();

        Message msg = new Message(from, subject, body);

        setTo(to);
        setMessage(msg);
        setMessageDelete(Boolean.TRUE);
        setIsPermanent(Boolean.FALSE);
    }

    /**
     * Creates a notification by supplying the receiver and an existing
     * message to send. All other information (subject, body, and
     * sender) is encapsulated by the message.
     *
     * @param to the party recieving the message
     * @param msg the message to send
     */

    public Notification(Party to, Message msg) {
        this();

        setTo(to);
        setMessage(msg);
    }


    /**
     * Retrieves an existing notification from the database using its
     * OID.
     *
     * @param oid the OID of the notification
     */

    public Notification(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Retrieves an existing notification from the database using its
     * BigDecimal id.
     *
     * @param id the BigDecimal ID of the notification
     */

    public Notification(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE,id));
    }

    /**
     * Sets the sender of the notification.
     * @param to the sender of the notification
     */

    public void setTo(Party to) {
        set(PARTY_TO, to.getID());
    }

    /**
     * Gets the message object contained by this notification, which
     * may be null.
     */

    private Message getMessage() {
        BigDecimal id = (BigDecimal) get(MESSAGE_ID);
        Message msg = null;

        if (id != null) {
            try {
                msg = new Message(id);
            } catch (DataObjectNotFoundException ex) {
                msg = null;
            }
        }

        return msg;
    }

    /**
     * Sets the message containing the sender, subject, and body of
     * this notification.
     *
     * Note: should we disable this for anything not isNew()?
     * @param msg the message of this notification
     */

    public void setMessage(Message msg) {

        if (msg.isNew()) {
            msg.save();
        }

        set(MESSAGE_ID, msg.getID());
    }

    /**
     * Get the status of this notification.
     */

    public String getStatus() {
        return (String) get(STATUS);
    }

    /**
     * setStatus has package access level.
     */
    void setStatus(String status) {
        set(STATUS, status);
    }

    /**
     * Gets the flag for whether this notification remains in the
     * database after processing.
     *
     * @return <code>true</code> if this notification remains in the
     * database after processing; <code>false</code> otherwise.
     */

    public Boolean getIsPermanent() {
        return new Boolean(!((Boolean) get(EXPUNGE_P)).booleanValue());
    }

    /**
     * Sets the flag for whether this notification remains in the
     * database after processing. If permanent is set, then this
     * notification will remain in the database.
     *
     * @param permanent <code>true</code> if this notification should
     * remain in the database after processing
     */

    public void setIsPermanent(Boolean permanent) {
        set(EXPUNGE_P, new Boolean(!permanent.booleanValue()));
    }

    /**
     * Gets the value of the MessageDelete flag if set.
     * @return <code>true</code> if the MessageDelete flag is set;
     * <code>false</code> otherwise.
     */

    public Boolean getMessageDelete() {
        return (Boolean) get(EXPUNGE_MSG_P);
    }

    /**
     * Sets the flag for whether the underlying message should be
     * deleted if and when this request is deleted.
     * @param value <code>true</code> if the underlying message should be
     * deleted when this request is deleted
     */

    public void setMessageDelete(Boolean value) {
        set(EXPUNGE_MSG_P, value);
    }

    /**
     * Gets the group expansion flag for this notification.
     * @return the group expansion flag for this notification.
     */
    public Boolean getExpandGroup() {
        return (Boolean) get(EXPAND_GROUP);
    }

    /**
     * Sets the group expansion flag for this notification.
     */
    public void setExpandGroup(Boolean expandGroup) {
        set(EXPAND_GROUP, expandGroup);
    }

    /**
     * Gets the digest associated with this notification. Returns null
     * if there is no digest.
     *
     * @return the digest of this notification, or null if
     * there is no digest.
     */

    public Digest getDigest()
        throws DataObjectNotFoundException
    {
        BigDecimal digestID = (BigDecimal) get(DIGEST_ID);

        if (digestID != null) {
            return new Digest(digestID);
        } else {
            return null;
        }
    }

    /**
     * Sets the digest parameter for a notification.
     *
     * @param digest the digest this message belongs to
     */

    public void setDigest(Digest digest) {
        set(DIGEST_ID, digest.getID());
    }

    /**
     * Gets the request date. The API does not allow for setting the
     * request date, since this handled automatically when the request
     * is saved.
     *
     * @return the date of the request
     */
    public Date getRequestDate() {
        return (Date) get(REQUEST_DATE);
    }

    /**
     * Gets the fulfill date.
     * @return the date the request was fulfilled
     */
    public Date getFulfillDate() {
        return (Date) get(FULFILL_DATE);
    }

    /**
     * Sets the fulfill date. This method is only available to the
     * queue management classes inside the notification package.
     */
    void setFulfillDate(Date d) {
        set(FULFILL_DATE, d);
    }

    /**
     * Sets the header of the alert.  The header is prepended to the
     * body of the email message (before the content of the contained
     * Message body).
     *
     * @param header the header of the alert
     * @since 4.8.1
     */

    public void setHeader(String header) {
        set(HEADER, header);
    }

    /**
     * Sets the signature of the notification. The signature is
     * appended to the body of the email message (after the content of
     * the contained Message body).
     *
     * @param signature the signature of the alert
     * @since 4.8.1
     */

    public void setSignature(String signature) {
        set(SIGNATURE, signature);
    }

    /**
     * Returns <code>true</code> if the primary MIME type of the
     * contained message matches the specified MIME type. Always
     * returns false if a message has not been specified.
     *
     * @return true if the contained Message is of the specified type.
     */

    private boolean isMimeType(String type) {
        Message msg = getMessage();
        if (msg == null) {
            return false;
        } else {
            return msg.isMimeType(type);
        }
    }

    protected void beforeSave() {

        // Check to see if we need to save the underlying
        // message. This could be the case if we generated an internal
        // message.

        Message msg = getMessage();
        if (msg != null) {
            if (msg.isNew()) {
                msg.save();
            }

            set(MESSAGE_ID, msg.getID());
        }

        super.beforeSave(); // Save the request
    }

    /**
     * Deletes the notification.  This also checks to see if the
     * corresponding message should be deleted at the same time.
     */

    public void delete() {

        boolean msgDelete = getMessageDelete().booleanValue();
        Message msg;

        // If the MessageDelete flag is turned on, then go ahead and
        // delete the message from the database.  In case we get
        // can't retrieve it just turn the delete flag back off.

        if (msgDelete) {
            msg = getMessage();
            if (msg == null) {
                msgDelete = false;
            }
            // find if there are other referring notifications. If so msgdelete = false
            // so only the last notification tries to delete the message
            DataCollection notifications = SessionManager.getSession().retrieve(Notification.BASE_DATA_OBJECT_TYPE);
            notifications.addEqualsFilter(MESSAGE_ID, msg.getID());
            notifications.addNotEqualsFilter(ID, this.getID());
            if (notifications.size() > 0) {
                // other notifications that still refer to the message. Let
                // the last one out delete the message else foreign key breach
                // brings down the whole request queue process
                msgDelete = false;
            }
        } else {
            msg = null;
        }

        // Delete the request

        super.delete();

        // Delete the message if necessary.

        if (msgDelete) {
            msg.delete();
        }
    }
}
