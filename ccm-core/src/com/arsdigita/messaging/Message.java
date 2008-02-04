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
package com.arsdigita.messaging;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 * A plain text message with optional attachments.  The message body
 * should have a MIME type of text/plain or text/html.  Each
 * attachment to the message can have an arbitrary MIME type and
 * format.  Messages can also refer to another object on the system,
 * and in this way be attached to ContentItems, bboard forums, etc.
 *
 * @author Ron Henderson 
 * @author David Dao 
 * @version $Id: Message.java 1503 2007-03-20 12:31:29Z chrisgilbert23 $
 */

public class Message extends ACSObject implements MessageType
{

    /**
     * Attachments.
     */

    private ArrayList m_attachments;

    /**
     * Base DataObject type
     */

    public static final String BASE_DATA_OBJECT_TYPE = Message.class.getName();

    /**
     * Keys for persistent data.
     */

    public static final String SENDER    = "sender";
    public static final String REPLY_TO  = "replyTo";
    public static final String SUBJECT   = "subject";
    public static final String BODY      = "body";
    public static final String TYPE      = "type";
    public static final String SENT      = "sent";
    public static final String INREPLYTO = "inReplyTo";
    public static final String OBJECT_ID = "objectID";
    public static final String MESSAGE_ID = "messageID";

    public static final String MESSAGE_PART = "messagePart";

    private Party m_sender = null;

    /**
     * Used for logging.
     */

    private static final Logger s_log =
        Logger.getLogger(Message.class);

    /**
     * Creates a new message with the sentDate initialized to the
     * current time, but leaves all other parameters null.
     */

    public Message() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Creates a new message of the specified data object type.
     * Initializes the sentDate to the current time, but leaves all
     * other parameters null.
     *
     * @param type the DataObject type.
     */

    public Message(String type) {
        super(type);
        setSentDate(new Date());
    }

    /**
     * Creates a message from a party with a given subject.
     *
     * @param f the party sending the message
     * @param s the subject of the message
     */

    public Message(Party f, String s) {
        this(f,s,null);
    }

    /**
     * Creates a message from a party with a given subject and body.
     *
     * @param f the party sending the message
     * @param s the subject of the message
     * @param b the plain-text body of the message
     */

    public Message(Party f, String s, String b) {
        this();

        setFrom(f);
        setSubject(s);
        setText(b);
    }

    /**
     * Creates a message from its underlying data object.
     *
     * @param dataObject the DataObject representing this message.
     */

    public Message(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Creates a message by retrieving it from the database using its
     * id.
     *
     * @param id the id of the message
     */

    public Message(BigDecimal id)
        throws DataObjectNotFoundException
    {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates a message by retrieving it from the database using its
     * OID.
     *
     * @param oid the OID of the message
     */

    public Message(OID oid)
        throws DataObjectNotFoundException
    {
        super(oid);
    }

    /**
     * Copies the necessary reply information from a parent message to
     * this one.  This includes:
     *
     * <ul>
     *   <li>Setting the replyTo property
     *   <li>Setting the subject to "Re:" plus the original subject
     *       (unless it already begins with "Re:")
     *   <li>Setting the refersTo property
     * </ul>
     *
     * @param msg the message to generate reply information from
     */

    protected void getReplyInfo(Message msg) {

        // Set inReplyTo

        set(INREPLYTO, msg.getID());

        // Set the refersTo property (possibly null)

        setRefersTo(msg.getRefersTo());

        // Add a "Re: " to the subject of the message unless there is
        // already one such string at the beginning.

        String prefix  = "Re:";
        String subject = msg.getSubject();

        if (subject.startsWith(prefix)) {
            setSubject(subject);
        } else {
            setSubject(prefix + " " + subject);
        }
    }

    /**
     *  Returns the name of the message
     */
    public String getDisplayName() {
        return getSubject();
    }


    /**
     * Gets a new message that is suitable for a reply to this
     * message.
     */

    public Message reply() throws MessagingException {
        Message reply = new Message();
        reply.getReplyInfo(this);
        return reply;
    }

    /**
     * Gets the subject of the message.
     * @return the subject of the message.
     */

    public String getSubject() {
        return (String) get(SUBJECT);
    }

    /**
     * Sets the subject of the message.
     * @param s the subject
     */

    public void setSubject(String s) {
        set(SUBJECT, s);
    }

    /**
     * Sets the sender of the message.
     * @param f the party sending the message
     */

    public void setFrom(Party f) {
        m_sender = f;
        setAssociation(SENDER, f);
    }

    /**
     * Gets the sender of the message.
     * @return the sender.
     */

    public Party getFrom() {
        if (m_sender == null) {
            DataObject senderData = (DataObject) get(SENDER);
            if (senderData != null) {
                m_sender = (Party) DomainObjectFactory.newInstance
                    (senderData);
            }
        }
        return m_sender;
    }

    /**
     * Sets the return email address of the message. Sets this
     * field if you want return address differs than sender.
     * @param addr reply address.
     */
    public void setReplyTo(String addr) {
        set(REPLY_TO, addr);
    }

    /**
     * Gets the return email address of the message.
     * @return the return email address.
     */
    public String getReplyTo() {
        return (String) get(REPLY_TO);
    }

    /**
     * Returns the content of the message as a String.  Message bodies
     * can always be represented as text.  If you need to check on the
     * MIME type of the body, use the getBodyType method and process
     * the body accordingly.
     *
     * @return the content of the message.
     */

    public String getBody() {
        return (String) get(BODY);
    }

    /**
     *  This returns the body in a form that can be displayed directly
     *  as HTML.  Basically, this conditionally modifies the
     *  body depending on the result of getBodyType();
     */
    public String getBodyAsHTML() {
        return generateHTMLText(getBody(), getBodyType());
    }


    /**
     * Sets the body of the message to the given string with the
     * specified MIME type.  Use one of the standard types defined in
     * the {@link MessageType} interface:
     *
     * <ul>
     *   <li>MessageType.TEXT_PLAIN</li>
     *   <li>MessageType.TEXT_HTML</li>
     *   <li>MessageType.TEXT_PREFORMATTED</li>
     * </ul>
     *
     * @param body the body of the message
     * @param type the MIME type
     */

    public void setBody(String body, String type) {
        set(BODY, body);
        set(TYPE, type);
    }


    /**
     * A convenience method that sets the body of the message to a
     * string with a MIME type of "text/plain".
     */

    public void setText(String text) {
        setBody(text, TEXT_PLAIN);
    }

    /**
     * Returns the MIME type of the message body.  This should
     * normally have a primary type of text and secondary type of
     * plain or html.
     *
     * @return the MIME type of the message body.
     */

    public String getBodyType() {
        return (String) get(TYPE);
    }

    /**
     * Returns <code>true</code> if this message is of the specified
     * MIME type.  Only compares the primary type and subtype, and
     * ignore any additional qualifiers.
     *
     * @return <code>true</code> if this message is of the specified
     * MIME type; <code>false</code> otherwise.
     */

    public boolean isMimeType(String mimeType) {
        String primary = getPrimaryType(mimeType);
        return primary.regionMatches
            (true, 0, getPrimaryType(getBodyType()), 0, primary.length());
    }

    /**
     * Returns the primary MIME type in a String
     */

    private static String getPrimaryType(String type) {
        StringTokenizer st = new StringTokenizer(type, " ;");
        return st.nextToken();
    }

    /**
     * Returns the date this message was sent.
     * @return the date this message was sent.
     */

    public Date getSentDate() {
        return (Date) get(SENT);
    }

    /**
     * Sets the sent date of a message.
     *
     * @param sentDate the date the message was sent
     */

    public void setSentDate(Date sentDate) {
        set(SENT, sentDate);
    }

    /**
     * Returns the ID of an ACSObject which this message refers to.
     * Applications should implement their own methods for retrieving
     * the underlying object from the database.
     *
     * @return the ID of an ACSObject which this message refers to.
     */

    public BigDecimal getRefersTo() {
        return (BigDecimal) get(OBJECT_ID);
    }

    /**
     * Sets the ID of an ACSObject which this message refers to.
     *
     * @param id the ID of the ACSObject this message refers to.
     */

    public void setRefersTo(BigDecimal id) {
        set(OBJECT_ID, id);
    }

    /**
     * Sets the ACSObject which this message refers to.
     *
     * @param obj the ACSObject this message refers to.
     */

    public void setRefersTo(ACSObject obj) {
        setRefersTo(obj.getID());
    }

    /**
     * Return the RFC 822 Message-ID header for this message.
     *
     * @return RFC 822 Message-ID
     */
    public String getRFCMessageID() {
        return (String) get(MESSAGE_ID);
    }

    /**
     * Set RFC 822 Message-ID header for this message. Message-ID
     * header needs to be in internet address format (e.g. xxx@hostname)
     */
    public void setRFCMessageID(String messageID) {
        set(MESSAGE_ID, messageID);
    }

    /**
     * Adds a text/plain attachment to a message with a given name and
     * a disposition of MessagePart.ATTACHMENT.
     *
     * @param text the content of the attachment
     * @param name the name of the attachment
     */

    public void attach(String text,
                       String name) {
        attach(text,name,null,MessagePart.ATTACHMENT);
    }

    /**
     * Adds a text/plain attachment with the given name and
     * description to a message, with a disposition of
     * MessagePart.ATTACHMENT.
     *
     * @param text the content of the attachment
     * @param name the name of the attachment
     * @param description a description of the attachment
     */

    public void attach(String text,
                       String name,
                       String description) {
        attach(text,name,description,MessagePart.ATTACHMENT);
    }

    /**
     * Adds a text/plain attachment with the given name, description
     * and disposition to a message.
     *
     * @param text the content of the attachment
     * @param name the name of the attachment
     * @param description a description of the attachment
     */

    public void attach(String text,
                       String name,
                       String description,
                       String disposition) {
        MessagePart part = new MessagePart();
        attach(part);

        part.setName(name);
        part.setDescription(description);
        part.setDisposition(disposition);
        part.setText(text);
    }

    /**
     * Attaches a MessagePart to this message.  This method is used by
     * all of the other attach methods.
     *
     * @param part the MessagePart to attach
     */

    public void attach(MessagePart part) {
        addPart(part);
    }

    /**
     * Returns the number of items attached to this message.
     * @return the number of items attached to this message.
     */

    public int getAttachmentCount() {
        return getParts().size();
    }

    /**
     * Returns an iterator over the attachments for this message.
     * @return an iterator over the attachments for this message.
     */

    public ListIterator getAttachments() {
        return getParts().listIterator();
    }

    /**
     * Adds a new MessagePart to this message.  Used internally to add
     * parts and correctly maintain the association between the
     * message body and its attachments.
     */

    private void addPart(MessagePart part) {
        getParts().add(part);
        part.addToAssociation(getPartAssociation());
    }

    /**
     * Get the DataAssociation between this message and its
     * attachments.
     */

    private DataAssociation getPartAssociation() {
        return (DataAssociation) get(MESSAGE_PART);
    }

    /**
     * Returns the list of attachments.  If the list of attachments
     * has not been initialized, this will take care of initializing
     * it and loading any parts from the database.
     */

    private ArrayList getParts() {

        if (m_attachments == null) {
            m_attachments = new ArrayList();

            if (!isNew()) {
                DataAssociationCursor cursor =
                    getPartAssociation().cursor();
                try {
                    while (cursor.next()) {
                        m_attachments.add
                            (new MessagePart(cursor.getDataObject()));
                    }
                } catch (DataObjectNotFoundException ex) {
                    s_log.warn("error initializing message attachments");
                } finally {
                    cursor.close();
                }
            }
        }

        return m_attachments;
    }

    /**
     * Returns the MessageID of this instance as a BigDecimal.
     * @return the MessageID of this instance.
     *
     * @deprecated Use getID in place of getMessageID
     */

    public BigDecimal getMessageID() {
        return getID();
    }

    /**
     * Returns the Container for this Message.  This is implemented by
     * instantiating the object the message refers to, which may or
     * may not be null.
     *
     * @return the ACSObject that contains this message.
     */
    public ACSObject getContainer() {
        BigDecimal id = getRefersTo();
        if ( id != null ) {
            OID oid = new OID(ACSObject.BASE_DATA_OBJECT_TYPE,id);
            try {
                return (ACSObject) DomainObjectFactory.newInstance(oid);
            } catch (DataObjectNotFoundException ex) {
                // fall through to the failure case at the bottom
            }
        }
        return null;
    }
    /**
    * return the parent Message, or null if there is no parent
    */ 
    public Message getParent() {
	Message parent = null;
	BigDecimal id = (BigDecimal) get(INREPLYTO);
	if (id != null) {
	    parent = new Message(id);
	}
	return parent;
    }

    /**
     * Allow writes if user has read on the parent (forum).
     */
    public void doWriteCheck() {
        getContainer().assertPrivilege(PrivilegeDescriptor.READ);
    }

    /**
     * @return true if the container for this Message has changed.
     */

    public boolean isContainerModified() {
        return isPropertyModified(OBJECT_ID);
    }

    /**
     * @deprecated Use com.arsdigita.util.HtmlToText.generateHTMLText.
     */
    public static String generateHTMLText(String text, String formatType) {
        return com.arsdigita.util.HtmlToText.generateHTMLText(text,  formatType);
    }

    protected void afterSave() {
        super.afterSave();

        PermissionService.setContext(this, getContainer());
    }

}
