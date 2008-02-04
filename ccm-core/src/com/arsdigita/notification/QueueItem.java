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
import com.arsdigita.domain.ObservableDomainObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.mail.Mail;
import com.arsdigita.messaging.Message;
import com.arsdigita.messaging.MessagePart;
import com.arsdigita.util.MessageType;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.HtmlToText;
import com.arsdigita.util.StringUtils;
import com.arsdigita.web.Web;

import java.math.BigDecimal;
import java.util.Iterator;
import javax.mail.MessagingException;
import org.apache.log4j.Logger;


/**
 * Represents a notification that has been transferred to the outbound
 * message queue.  During processing, this class is used to retrieve
 * information necessary to convert the notification into an outbound
 * email message.
 *
 * @author David Dao
 * @version $Id: QueueItem.java 1513 2007-03-22 09:09:03Z chrisgilbert23 $
 */

class QueueItem extends ObservableDomainObject
    implements NotificationConstants
{
    // Base DataObject type

    public static final String BASE_DATA_OBJECT_TYPE =
        QueueItem.class.getName();

    private static final Logger s_log =
        Logger.getLogger(QueueItem.class);

    private static HtmlToText s_htmlToText =
        new HtmlToText();

    /**
     * The message object contained by this notification.  Retrieved
     * once from the database and cached.
     */

    private Message m_message;

    /**
     * Creates a QueueItem from an OID.
     */

    QueueItem(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Creates a QueueItem from a Notification and recipient.
     */

    QueueItem(Notification n, Party to) {
        super(BASE_DATA_OBJECT_TYPE);
        set(REQUEST_ID, n.getID());
        set(PARTY_TO, to.getID());
    }

    /**
     * Set the success flag for this notification.
     */

    void setSuccess(Boolean b) {
        set(SUCCESS, b);
    }

    /**
     * Gets the message object contained by this notification.
     */

    Message getMessage()
        throws DataObjectNotFoundException
    {
        if (m_message == null) {
            m_message = new Message((BigDecimal) get(MESSAGE_ID));
        }
        return m_message;
    }

    /**
     * Returns the email address of the recipient as a String.
     */

    String getTo() {
        return (String) get(PARTY_TO_ADDR);
    }

    /**
     * Returns the email address of the sender as a String.
     */

    String getFrom()
        throws DataObjectNotFoundException
    {
        return getMessage().getFrom().getPrimaryEmail().toString();
    }

    /**
     * Returns the subject of the notification.
     */

    String getSubject()
        throws DataObjectNotFoundException
    {
        return getMessage().getSubject();
    }

    /**
     * Gets the header for this notification.
     */

    private String getHeader() {
        return StringUtils.nullToEmptyString((String) get(HEADER));
    }

    /**
     * Gets the signature for this notification.
     */

    private String getSignature() {
        return StringUtils.nullToEmptyString((String) get(SIGNATURE));
    }

    /**
     * Returns the body of the message. This method constructs the
     * body of the email by combining the body of the underlying
     * message object with the specified header and signature for the
     * notification.  It then converts the entire message to plain
     * text before sending using {@link HtmlToText}.
     *
     * @return a plain text message to send as email to a user
     */

    String getBody() throws DataObjectNotFoundException {

        Message msg = getMessage();

        String body;
        if (msg.isMimeType(MessageType.TEXT_HTML)) {
            body = s_htmlToText.convert(msg.getBody());
        } else {
            body = StringUtils.wrap(msg.getBody());
        }

        StringBuffer sb = new StringBuffer();
        sb.append(StringUtils.addNewline(getHeader()));
        sb.append(StringUtils.addNewline(body));
        sb.append(StringUtils.addNewline(getSignature()));
        return sb.toString();
    }

    void setBody(Mail mail) {
    	Message msg = getMessage();
    	String altBody = getBody();
	if (msg.isMimeType(MessageType.TEXT_HTML) && Mail.getConfig().sendHTMLMessageAsHTMLEmail()) {
	    StringBuffer htmlBody = new StringBuffer();
            htmlBody.append(StringUtils.addNewline(getHeader()));
            htmlBody.append(StringUtils.addNewline(msg.getBody()));
            htmlBody.append(StringUtils.addNewline(getSignature()));
            
	    if (htmlBody.indexOf("<img") != -1) {
		htmlBody = qualifyImageTags(htmlBody);
	    }
	    mail.setHeaders("Content-Type:" + Mail.TEXT_HTML);
	    mail.setBody(htmlBody.toString(), altBody );
			
	} else {
	    mail.setBody(altBody);
		
	}
    	
    }

    /**
     * Transfer any attachments from the message contained by this
     * notification to a given Mail message.
     *
     * @param mail the Mail to add attachments to
     */

    void addAttachments(Mail mail)
        throws MessagingException
    {
        Message msg;
        try {
            msg = getMessage();
        } catch (DataObjectNotFoundException ex) {
            return;
        }

        if (msg.getAttachmentCount() > 0) {
            Iterator iter = msg.getAttachments();
            while (iter.hasNext()) {
                MessagePart part = (MessagePart) iter.next();
                mail.attach(part.getDataHandler(),
                            part.getDescription(),
                            part.getDisposition());

            }
        }
    }

    BigDecimal getRetryCount() {
        return (BigDecimal) get(RETRY_COUNT);
    }

    void setRetryCount(BigDecimal i) {
        set(RETRY_COUNT, i);
    }

    void incrRetryCount() {
        set(RETRY_COUNT, getRetryCount().add(new BigDecimal(1)));
    }

    private StringBuffer qualifyImageTags (StringBuffer body) {
    	s_log.debug("qualify image tags");
    	boolean inTagTest = false;
    	boolean inImageTag = false;
    	StringBuffer tagType = new StringBuffer();
    	StringBuffer converted = new StringBuffer();
    	char[] chars = body.toString().toCharArray();
    	for (int i = 0; i < chars.length; i++) {
    	    char nextChar = chars[i];
    	    converted.append(nextChar);
    	    s_log.debug("Character: " + nextChar);
    	    if (inImageTag) {
    		if ('"' == nextChar || '\'' == nextChar) {
    		    s_log.debug("Image tag - start or end of attribute");
    		    // start of an image attribute value  (or end)
    		    if (converted.toString().endsWith("src=" + nextChar)) {
    			s_log.debug("In fact, the start of the src attribute");
    			converted.append("http://" + Web.getConfig().getServer().toString());
    			s_log.debug("inserting server name " + Web.getConfig().getServer().toString());
    			inImageTag = false;
    			inTagTest = false;
    		    }
    		}
    		
    	    } else if (inTagTest) {
    		if (' ' == nextChar || '/' == nextChar || '>' == nextChar) {
		    // end of tag type
    		    s_log.debug("This is a " + tagType + " tag");
    		    if ("img".equals(tagType.toString().toLowerCase())) {
    		    	inImageTag = true;
    		    } else {
    		    	// we may still be in a tag, or in a close tag 
		    	// but we're not interested either way, so move on
    		    	s_log.debug("Not interested - keep scanning for the more tags");
    			
    		    }
  		    inTagTest = false;
    		    tagType = new StringBuffer();
    	    	} else {
    		    tagType.append(nextChar);
    	    	}
    	    } else if ('<' == nextChar) {
    	    	s_log.debug("start of tag");
    	    	inTagTest = true;
    	    }
    	}
    	return converted;
    }

}
