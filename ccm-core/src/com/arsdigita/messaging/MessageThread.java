/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;
import java.util.Date;

/**
 * This class abstracts the concept of a Thread of messages.  I.e. it
 * frees you from needing to know implementation details of the
 * ThreadedMessage class.
 *
 * @author Kevin Scaldeferri 
 * @since 4.8.11
 * @version $Revision: 1.5 $ $DateTime: 2004/08/16 18:10:38 $
 */

public class MessageThread extends ACSObject {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.messaging.Thread";

    public static final String LAST_UPDATE = "lastUpdate";
    private static final String ROOT = "root";
    private static final String AUTHOR = "author";
    private static final String REPLIES = "numReplies";

    private ThreadedMessage m_root = null;
    private Party m_author = null;

	public MessageThread(String type) {
		super(type);
	}
    // need some contructors here...
    public MessageThread(ThreadedMessage rootMsg) {
        super(BASE_DATA_OBJECT_TYPE);
        setRootMessage(rootMsg);
        setAuthor(rootMsg.getFrom());
        setLatestUpdateDate(rootMsg.getSentDate());
    }

    public MessageThread(DataObject data) {
        super(data);
    }

    public MessageThread(BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }
    public MessageThread(OID id) throws DataObjectNotFoundException {
        super(id);
    }

    protected void initialize() {
        super.initialize();
        if (isNew() && get(REPLIES) == null) {
            set(REPLIES, new BigDecimal(0));
        }
    }

    /**
     * This method retrieves the root message, i.e. the message that
     * is the start of the thread
     */
    public ThreadedMessage getRootMessage() {
        if (m_root == null ) {
            DataObject rootData = (DataObject) get(ROOT);
            if (rootData != null) {
                m_root = (ThreadedMessage)DomainObjectFactory.newInstance(rootData);
            }
        }
        return m_root;
    }

    public ACSObject getContainer() {
        return getRootMessage().getContainer();
    }

    /**
     * Allow writes if user has read on the parent (forum).
     */
    public void doWriteCheck() {
        getContainer().assertPrivilege(PrivilegeDescriptor.READ);
    }

    private void setRootMessage(ThreadedMessage rootMsg) {
        m_root = rootMsg;
        setAssociation(ROOT, rootMsg);
    }

    /**
     * Gets the total number of messages in this thread
     */
    public long getNumReplies() {
        return ((BigDecimal) get(REPLIES)).longValue();
    }

    /**
     * Increments the number of messages in the thread by one.
     * The reason for the continued existance of this is thread safety
     */
    private void incrNumberOfReplies() {
        DataOperation op = SessionManager.getSession().retrieveDataOperation(
                                                                             "com.arsdigita.messaging.incrNumReplies");
        op.setParameter("threadID", getID());
        op.execute();
    }

    /**
     * Decrements the number of messages in the thread by one.
     * The reason for the continued existance of this is thread safety
     */
    private void decrNumberOfReplies() {
        DataOperation op = SessionManager.getSession().retrieveDataOperation(
                                                                             "com.arsdigita.messaging.decrNumReplies");
        op.setParameter("threadID", getID());
        op.execute();
    }

    /**
     * Gets the Date of the most recent message added to this thread
     */
    public Date getLatestUpdateDate() {
        return (Date) get(LAST_UPDATE);
    }

    /**
     * Sets the Date of the most recent update to the thread
     */
    private void setLatestUpdateDate(Date date) {
        set(LAST_UPDATE, date);
    }

    public String getSubject() {
        return getRootMessage().getSubject();
    }

    public String getAuthorName() {
        DataObject author = (DataObject) get(AUTHOR);
        if (author == null) {
            return null;
        } else {
            return (String) author.get(DISPLAY_NAME);
        }
    }

    public Party getAuthor() {
        if (m_author == null) {
            DataObject authorData = (DataObject) get(AUTHOR);
            if (authorData != null) {
                m_author = (Party) DomainObjectFactory.newInstance(authorData);
            }
        }
        return m_author;
    }

    private void setAuthor(Party author) {
        m_author = author;
        setAssociation(AUTHOR, author);
    }

    /**
     * @deprecated ThreadedMessage now updates thread statistics after save
     * and after delete
     * updates the MessageThread as necessary for the new Message being added.
     * This is only meant to be called by the ThreadedMessage class
     *
     * @pre msg.getThread().equals(this)
     */
    void updateForNewMessage(ThreadedMessage msg) {
        Assert.assertTrue(msg.getThread().equals(this));
        setLatestUpdateDate(msg.getSentDate());
        incrNumberOfReplies();
    }


    /**
     * @deprecated ThreadedMessage now updates thread statistics after save
     * and after delete
     * updates the MessageThread as necessary for a Message being removed.
     * This is only meant to be called by the ThreadedMessage class
     *
     * @pre msg.getThread().equals(this)
     */
    void removeMessage(ThreadedMessage msg) {
        Assert.assertTrue(msg.getThread().equals(this));
        decrNumberOfReplies();
    }

    /**
     * retrieves the MessageThread object which has the specified
     * ThreadedMessage as its root
     *
     * @pre msg.getRoot == null
     */
    public static MessageThread getFromRootMessage(ThreadedMessage msg) {
        Assert.assertEquals(null, msg.getRoot());
        DataCollection threads = SessionManager.getSession().retrieve(
                                                                      BASE_DATA_OBJECT_TYPE);
        threads.addEqualsFilter("root.id", msg.getID());

        MessageThread t = null;
        try {
            if (threads.next()) {
                t = new MessageThread(threads.getDataObject());
            } else {
                // we would probably like to do something graceful here
                // in general, but for now we let it return null so
                // we make sure that our data migration script works
            }
        } finally {
            threads.close();
        }
        return t;
    }

    public String toString() {
        return getOID().toString();
    }
    
    /**
     * update reply count and last updated date according to 
     * current database contents. subclasses of ThreadedMessage
     * may inplement addReplyFilter to filter out any messages
     * that shouldn't be 
     * @param message
     */
	public void updateThreadStatistics(ThreadedMessage message) {		
		DataCollection replies = SessionManager.getSession().retrieve(message.getObjectType());
		replies.addEqualsFilter(ThreadedMessage.ROOT, getRootMessage().getID());
		message.addReplyFilter(replies);
		long replyCount = replies.size();
		set(REPLIES, new BigDecimal(replyCount)); 	
    	if (replyCount == 0){
    		setLatestUpdateDate(this.getRootMessage().getSentDate());
    	} else {
    		replies.addOrder(Message.SENT + " desc");
    		if(replies.next()){
    			ThreadedMessage latestReply = (ThreadedMessage) DomainObjectFactory.newInstance(replies.getDataObject());
    			setLatestUpdateDate(latestReply.getSentDate());
    			replies.close();
    		}
    	}
	}
}
