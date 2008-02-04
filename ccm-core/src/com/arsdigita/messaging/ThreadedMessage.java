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

// ACS Core classes
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Party;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;

// Java Core classes
import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * Extends Message in a way that allows messages to be organized into
 * discussion threads with a tree structure.  A typical discussion
 * might be organized as follows:
 *
 * <pre>
 *     msg-0
 *         msg-0.0
 *         msg-0.1
 *             msg-0.1.0
 *             msg-0.1.1
 *         msg-0.2
 *     msg-1
 *         msg-1.0
 *     msg-2
 * </pre>
 *
 * <p>where msg-0.0 and msg-0.1 are replies to msg-0, msg-0.1.0 is a
 * reply to msg-0.1, and so forth.  Messages at the first level
 * (msg-0, msg-1, and msg-2) are referred to as "root" message, and
 * higher-level messages contain a pointer to their common root.  If a
 * root message is deleted, all of its children are deleted.
 *
 * <p>A structure like the one shown above is created using the
 * reply() method, which returns a new ThreadedMessage correctly
 * initialized to represent a response to its parent.  For example,
 * you might generate a similar structure using:
 *
 * <pre>
 *     msg0 = new Message();       // root message
 *
 *     msg00  = msg0.reply();      // level 1 replies
 *     msg01  = msg0.reply();
 *
 *     msg010 = msg01.reply();     // level 2 replies (to msg01)
 *     msg011 = msg01.reply();
 * </pre>
 *
 * <p>Replying to a message always generates a new message one level
 * deeper in the tree.  Successive replies to the same message
 * generate the appropriate "next child" for that message.
 *
 * @author Ron Henderson
 * @version $Id:
 * //core-platform/dev/services/messaging/src/ThreadedMessage.java#3 $
 */

public class ThreadedMessage extends Message {

    /**
     * Base data object type.
     */

    public static final String BASE_DATA_OBJECT_TYPE =
        ThreadedMessage.class.getName();

    /**
     * Keys for persistent data.
     */

    private static final String ROOT_ID   = "root";
    public static final String ROOT      = "root";
    private static final String SORT_KEY  = "sortKey";

    private boolean m_wasNew = false;

    private MessageThread m_thread = null;
    private ThreadedMessage m_root = null;

    private static final Logger s_log =
        Logger.getLogger(ThreadedMessage.class);

    /**
     * Creates a new message with the sentDate initialized to the
     * current time, but leaves all other parameters null.
     */

    public ThreadedMessage() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Creates a threaded message from a party with a given subject.
     *
     * @param f the party sending the message
     * @param s the subject of the message
     */

    public ThreadedMessage(Party f, String s) {
        this(f,s,null);
    }

    /**
     * Creates a threaded message from a party with a given subject and body.
     *
     * @param from the party sending the message
     * @param subject the subject of the message
     * @param body the plain-text body of the message
     */

    public ThreadedMessage(Party from, String subject, String body) {
        // Call the default constructor to initialize
        this();

        setFrom(from);
        setSubject(subject);
        setText(body);
    }

    /**
     * Creates a threaded message from its underlying data type.
     *
     * @param type the DataObject type.
     */

    public ThreadedMessage(String type) {
        super(type);
    }

    /**
     * Creates a threaded message from its underlying data object.
     *
     * @param dataObject the DataObject representing this message.
     */

    public ThreadedMessage(DataObject dataObject) {
        super(dataObject);
    }

    /**
     * Creates a threaded message by retrieving it from the database
     * using its id;
     *
     * @param key the id of the message.
     */

    public ThreadedMessage(BigDecimal id) {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Creates a threaded message by retrieving it from the database
     * using its OID.
     *
     * @param oid the OID of the message
     */

    public ThreadedMessage(OID oid) {
        super(oid);
    }

    public ThreadedMessage newInstance() {
        return new ThreadedMessage();
    }

    /**
     * Gets a new message suitable for a reply to this message, with
     * the given sender and message body.
     *
     * @param from the Party sending the reply
     * @param body the text/plain body of the reply
     */

    public ThreadedMessage replyTo(Party from, String body) {
        ThreadedMessage reply = replyTo();
        reply.setFrom(from);
        reply.setText(body);

        return reply;
    }

    /**
     * Gets a new message that is suitable for a reply to this
     * message. The message object returned will have its root and
     * sort key properties initialized so that it is a valid child of
     * this message in the message tree.
     *
     * <p>For example, if root = 14 and sortKey = 04a, the new message
     * will have root = 14 and sortKey = 04a000.
     *
     * <p>If this message already has many responses, the new sort key
     * will be computed based on the maximum value of the current
     * responses. Absolute uniqueness of sort keys is not guaranteed,
     * but conflicts are highly unlikely.
     *
     * @return a new child response to this message.
     */

    public ThreadedMessage replyTo() {
        return replyTo(newInstance());
    }

    public ThreadedMessage replyTo(ThreadedMessage reply) {        
        SortKey nextKey = getNextChild();        

        // Initialize the basic properties

        reply.getReplyInfo(this);

        // Initialize the root message
        if (getRoot() == null) {
            reply.setRootID(getID());
        } else {
            reply.setRootID(getRoot());
        }

        // Initialize the sort key
        reply.setSortKey(nextKey);
        reply.setRefersTo(getRefersTo());
        //don't want to add to the reply count until a reply is confirmed
        //getThread().updateForNewMessage(reply);
        //getThread().save();
        return reply;
    }

    /**
     * Gets the ID of the root message associated with this family of
     * messages.
     * @return the ID of the root message
     */

    public BigDecimal getRoot() {
        return (BigDecimal) get(ROOT_ID);
    }

    public ThreadedMessage getRootMsg() {
        if (m_root == null) {
            DataObject rootData = (DataObject) get(ROOT);
            if (rootData != null) {
                m_root = new ThreadedMessage(rootData);
            }
        }
        return m_root;
    }

    /**
     * @deprecated Use the replyTo() method instead of this method
     * @throws UnsupportedOperationException
     */

    public void setRoot(BigDecimal root) {
        throw new UnsupportedOperationException
            ("ThreadedMessage.setRoot() is no longer supported. " +
             "Use ThreadedMessage.replyTo() instead.");
    }

    protected void setRootID(BigDecimal root) {
        set(ROOT_ID, root);
    }

    /**
     * Gets the value of the sort key (possibly null).
     * @return the sort key
     */

    public SortKey getSortKey() {
        String key = (String) get(SORT_KEY);
        return key != null ? new SortKey(key) : null;
    }

    /**
     * Sets the value of the sort key.
     * @param key is the sort key for this message
     */

    public void setSortKey(SortKey key) {
        set(SORT_KEY, key.toString());
    }

    /**
     * Gets the depth of the message within a tree of messages.
     */

    public int getDepth() {
        SortKey key = getSortKey();
        return  key == null ? 0 : key.getDepth();
    }

    /**
     * Gets the number of replies in this thread.  Note -- this is not
     * the number of replies below this message, but the total number
     * in the entire thread to which this message belongs.
     *
     * @deprecated use getThread().getNumReplies();
     */
    public long getNumReplies() {
        return getThread().getNumReplies();
    }

    private void setThread(MessageThread mt) {
        m_thread = mt;
    }

    /**
     * gets the MessageThread that this message belongs to
     */
    public MessageThread getThread() {
        if (m_thread == null) {
            BigDecimal rootID = getRoot();
            ThreadedMessage root = null;
            if (rootID == null) {
                root = this;
            } else {
                root = new ThreadedMessage(rootID);
            }
            m_thread = MessageThread.getFromRootMessage(root);
        }
        return m_thread;
    }

    /**
     * Saves the message after verifying that the root and sort key
     * are valid.  Also creates a new MessageThread if this is a root message.
     */

    protected void beforeSave() {
        if (getRoot() == null && getSortKey() != null) {
            throw new PersistenceException
                ("ThreadedMessage: root message must have a null sort key");
        }

        if (getRoot() != null && getSortKey() == null) {
            throw new PersistenceException
                ("ThreadedMessage: non-root message must have a sort key");
        }

        m_wasNew = isNew();

        super.beforeSave();
    }

    protected void afterSave() {
        super.afterSave();

        if (getRoot() == null && m_wasNew) {
            setThread(new MessageThread(this));
            getThread().save();
            m_wasNew = false;
        } else {
        	getThread().updateThreadStatistics(this);
        }
    }

    /**
     * Gets the sort key corresponding to the next child of this
     * message.
     *
     * @return the sort key of the next child.
     */        
    private SortKey getNextChild() {
            //      Prepare a query for the next valid sort key

             Session session = SessionManager.getSession();
             DataQuery query = session.retrieveQuery
                 ("com.arsdigita.messaging.maxSortKey");

             // Limit the query to other messages in the appropriate
             // subtree. If this message has no root, then it's at level
             // zero and we search for other messages that have this one as
             // root.  Otherwise we search for messages with the same root
             // and sort keys that are children of this one.

             Filter f;

             SortKey parent = getSortKey();
             SortKey child;

             if (getRoot() == null) {
                 f = query.addFilter("root = :root");
                 f.set("root", getID());

                 f = query.addEqualsFilter("sortSize", "3");
             } else {
                 f = query.addFilter("root = :root");
                 f.set("root", getRoot());

                 f = query.addFilter("sortKey like :parent");
                 f.set("parent", parent + "%");
                             
                 f = query.addFilter("inReplyTo = :reply");
                 f.set("reply", getID());
            
                 f = query.addFilter("sortSize > :parentSize");
                 f.set("parentSize", new Integer(parent.length()));
             }

             if (query.next()) {
                 child = new SortKey((String)query.get("sortKey"));                 
                 child.next();
                 query.close();
             } else {
                 child = (parent == null) ? new SortKey() : parent.getChild();
             }

             return child;        
    }

    protected void beforeDelete() {
        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Deleting children of " + getOID() );
        }

        DataCollection children = SessionManager.getSession().retrieve
            ( BASE_DATA_OBJECT_TYPE );
        children.addEqualsFilter( Message.INREPLYTO, getID() );

        while( children.next() ) {
        	
            DomainObjectFactory.newInstance( children.getDataObject() ).delete();
        }
		// ensure thread is stored now, as we can't get it after delete as this object won't exist for
		// us to retrieve it's root and hence thread
		//
		// only store for non root messages as the thread will be deleted
		// before the message if this is a root we are deleting
		//
		// it would make no sense to update thread statistics when deleting the root post anyway

		if (getRoot() != null) {
			m_thread = getThread();
		} else {
			m_thread = null;
		}
        if( s_log.isDebugEnabled() ) {
            s_log.debug( "Deleting " + getOID() );
        }
    }
    
	protected void afterDelete() {
		/*update number of replies based on the data in the database hence it needs to
		be calculated after the delete has happened*/
		//getThread().removeMessage( this );	
		
		if (m_thread != null) {
			try {
				m_thread.getID();
				m_thread.updateThreadStatistics(this);
			} catch (Exception e) {
				// thread no longer exists in database - happens if we delete the root
				// message, and we get to the afterDelete on one of the children of that root
			}
			
		}
		
	}
	
	protected void addReplyFilter (DataCollection replies) {
		// no filtering for ThreadedMessage, but subclasses may wish to override this
	}
	
}
