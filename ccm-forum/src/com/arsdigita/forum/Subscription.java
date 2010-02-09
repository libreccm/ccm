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
package com.arsdigita.forum;

import org.apache.log4j.Logger;

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.mail.Mail;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.notification.BaseSubscription;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;

/**
 * The abstract Subscription class provides the ability for Users
 * to sign up for email notifications.  Subclasses will specify
 * the object to which the notifications apply.
 *
 * The default implementation provides instant notifications.  Subclasses
 * should override sendNotification() to alter this behavior.
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 * @author chrisg23
 * @version $Id: Subscription.java 1628 2007-09-17 08:10:40Z chrisg23 $
 */
public abstract class Subscription extends BaseSubscription {

    private static final Logger s_log = Logger.getLogger(Subscription.class);

    protected static final String HTML_SEPARATOR = "<br><br>\n<hr>\n<br><br>\n";
    public Subscription(String objectType) {
        super(objectType);
    }

    public Subscription(DataObject dataObj) {
        super(dataObj);
    }

    public Subscription(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Returns a header for forum alerts with the following standard
     * information:
     *
     * <pre>
     * Forum    : Name
     * Subject  : Subject
     * com.arsdigita.messaging.ThreadedMessageed by: User
     * </pre>
     *
     * @return a header to insert at the top of the alert.
     */
    public String getHeader(ThreadedMessage msg) {
        Assert.truth(msg instanceof Post,
                     "Parameter msg should be a Post");

        Post post = (Post)msg;

        String author = post.getFrom().getName();
        if (author == null) {
            author = "Unknown";
        }
        StringBuffer sb = new StringBuffer();
        if (Mail.getConfig().sendHTMLMessageAsHTMLEmail()) {
	        sb.append("<font size='medium' face='Verdana, Arial, Helvetica, sans-serif'>");
	        sb.append("Forum    : ");
	        sb.append(post.getForum().getDisplayName()).append("\n<br>");
	        sb.append("Subject  : ");
	        sb.append(post.getSubject()).append("\n<br>");
	        sb.append("Posted by: ");
	        sb.append(author).append("\n<br><br>");
        } else { 
       
        sb.append("Forum    : ");
        sb.append(post.getForum().getDisplayName()).append("\n");
        sb.append("Subject  : ");
        sb.append(post.getSubject()).append("\n");
        sb.append("Posted by: ");
        sb.append(author).append("\n\n");

        }


        return sb.toString();
    }

    /**
     * Returns the signature to be appended to the alert.  The default
     * implementation returns a separator and a generic messages.
     */
    public String getSignature(ThreadedMessage post) {
    	if (Mail.getConfig().sendHTMLMessageAsHTMLEmail()) {
    	    return HTML_SEPARATOR + ALERT_BLURB + "</font>";
    	} else {
        return SEPARATOR + ALERT_BLURB;
    }
    }

    /**
     * @return an appropriate message to direct people back to the
     * forum, for inclusion in the signature of an alert, or an empty
     * string if the URL cannot be determined.
     */
    protected static String getReturnURLMessage(Post post) {
        final ParameterMap params = new ParameterMap();
        params.setParameter("thread", post.getThread().getID());

        final URL url = URL.there(post.getForum(), "/thread.jsp", params);

        StringBuffer sb = new StringBuffer();
        sb.append("To reply to this message, go to:\n");

        if (Mail.getConfig().sendHTMLMessageAsHTMLEmail()) {
        	sb.append("<br><a href='" + url.getURL() + "'>" + url.getURL() + "</a>");
        } else {
        sb.append(url.getURL());
        }
        return sb.toString();
    }

    
}
