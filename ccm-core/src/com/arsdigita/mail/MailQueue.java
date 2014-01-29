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
package com.arsdigita.mail;

// Java Core
import java.util.ArrayList;
import java.util.Iterator;

// JavaMail API
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;

/**
 * Bundles a number of messages and sends them all at once.
 * Saves the overhead of opening a separate connection to the SMTP
 * server for each individual message.
 *
 * @author Ron Henderson 
 * @version $Id: MailQueue.java 287 2005-02-22 00:29:02Z sskracic $
 */

public class MailQueue {

    /**
     * A collection of Mail objects to send.
     */

    private ArrayList m_messages;

    /**
     * Creates an empty mail queue.
     */

    public MailQueue() {
        m_messages = new ArrayList();
    }

    /**
     * Adds a mail message to the queue.
     *
     * @param msg the message to add
     */

    public void addMail(Mail msg) {
        m_messages.add(msg);
    }

    /**
     * Gets the number of messages in the queue.
     * @return the number of messages in the queue
     */

    public int getCount() {
        return m_messages.size();
    }

    /**
     * Sends all messages.
     */

    public void send()
        throws MessagingException,
               SendFailedException
    {
        Iterator iter = m_messages.iterator();

        // Grab an instance of the appropriate Mail session.

        Session session = Mail.getSession();

        // Create a Transport for sending messages, connect to it, and
        // ship all of the message off.

        Transport transport = session.getTransport();
        transport.connect();

        while (iter.hasNext()) {
            ((Mail) iter.next()).send(transport);
        }

        transport.close();

        m_messages.clear();
    }
}
