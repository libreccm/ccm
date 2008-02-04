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

import com.arsdigita.categorization.CategorizedObject;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleService;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.notification.Notification;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * A Post represents a single posting to a discussion forum.
 *
 *<ul>
 *  <li>pending - Message only visible to moderators</li>
 *  <li>approved - Message visible to all</li>
 *  <li>rejected - Message only visible to moderators</li>
 *  <li>reapprove - Message visible, but content hidden</li>
 *  <li>suppressed  - Message visible, but ontent hidden</li>
 *
 *</ul>
 *</p>
 *
 *<p>
 *<ul>
 *  <li>
 *      <b>New message</b>
 *      <ol>
 *        <li> Submitter posts (notify admin)</li>
 *
 *        <li> -> Admin 'approve' / 'reject'</li>
 *        <li> -> APPROVED Message goes live</li>
 *        <li> OR -> Message stays as draft and submitter is notified
 *        go back to 3)</li>
 *    </ol>
 *      </li>
 *  <li>
 *      <b>Edit message</b>
 *      <ol>
 *        <li> Submitter edits already approved message (notify admin)
 *        status changes to 'reapprove'</li>
 *
 *        <li> -> Admin moderates, change state to 'approve',
 *'supressed', or 'rejected' depending on whether they want to hide
 *the entire thread or just the content of the message.</li>
 *
 *        <li> -> APPROVED new version of message goes live</li>
 *
 *        <li> OR -> REJECTED old version of message stays around?</li>
 *    </ol>
 *  </li>
 *
 *  <li><b>user deletes message</b>
 *  <ol>
 *    <li>-> Status change to 'supressed'</li>
 *
 *    </ol>
 *    </li>
 *  <li>
 *      <b>Moderator changes decision</b>
 *      <ol>
 *        <li>Change status to 'supressed' or 'rejected', depending on
 *        wheather they want to remove the entire thread or just the
 *        content of the message. </li>
 *    </ol>
 *  </li>
 *
 *</ul>
 *</p>

 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 * @author Nobuko Asakai (nasakai@redhat.com)
 */

public class Post extends ThreadedMessage {
   private static final Logger s_log = Logger.getLogger(Post.class);

    /** PDL property for marking the approval state of a message, one
     * of 'approved', 'rejected', 'reapprove', 'supressed' */
    public static final String STATUS = "status";

    /** ID of the administrator who last changed the status of a
     * message */
    public static final String MODERATOR = "moderator";

    /** The status strings */
    public static final String PENDING = "pending";
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";
    public static final String REAPPROVE = "reapprove";
    public static final String SUPPRESSED = "suppressed";


    public static final String POST_STATUS_SUBQUERY =
        "com.arsdigita.forum.threadModerationStatus";

    private Party m_moderator;


    /*
     * The base DomainObject is Post which extends ThreadedMessage. In
     * other words, all bboard messages are ThreadedMessages.
     */

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.forum.Post";

    private Post() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    protected Post(String typeName) {
        super(typeName);
    }

    public Post(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Post(DataObject obj) {
        super(obj);
    }

    /**
     * Creates a new Posting in a form. If the forum
     * is moderated, then the post's status will be
     * set to PENDING, otherwise it will be set to
     * APPROVED
     * @param forum the owner forum
     */
    public static Post create(Forum forum) {
        return create(forum,
                      forum.isModerated() ? PENDING : APPROVED);
    }

    /**
     * Creates a new Posting in a form. The approval
     * status will be set as specified.
     * @param forum the owner forum
     * @param status the approval status
     */
    public static Post create(Forum forum,
                              String status) {
        Post post = new Post();
        post.setup(forum, status);
        return post;
    }

    protected void setup(Forum forum,
                         String status) {
        setForum(forum);
        setStatus(status);
    }

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * @deprecated use replyTo() instead
     */
    public Post replyToPost() {
        return (Post)replyTo();
    }

    public ThreadedMessage newInstance() {
        return create(getForum());
    }

    /**
     * Sets the Message-ID header to &lt;id&gt;.bboard@somehost.com
     * before saving.
     */
    protected void beforeSave() {
        Forum forum = getForum();
        Assert.exists(forum, Forum.class);

        BigDecimal id = getID();
        // XXX this isn't really the host we want
        setRFCMessageID(id + ".bboard@" +
                        Forum.getConfig().getReplyHostName());
        setReplyTo(getRefersTo() + ".bboard@" +
                   Forum.getConfig().getReplyHostName());

        super.beforeSave();

        if (isNew()) {
            if (forum.isNoticeboard()  &&  forum.getExpireAfter() > 0) {
                s_log.info("Creating expiration lifecycle for " + getOID());
                setLifecycle(forum.getLifecycleDefinition());
            }
        }
    }

    protected void afterSave() {
        super.afterSave();
        Forum forum = getForum();
        MessageThread root = getThread();
        Assert.exists(root);

        s_log.info("Setting context for " + getOID() + " to " + root.getOID());
        PermissionService.setContext(this, root);
        s_log.info("Setting context for " + root.getOID() + " to " +
                   forum.getOID());
        PermissionService.setContext(root, forum);

    }

    /**
     * Sends out the notifications for any subscriptions to the forum
     * or thread to which this message belongs. Only sends
     * notificatios if the post is approved.
     */
    public void sendNotifications() {
        KernelExcursion ex = new KernelExcursion() {
                protected void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());
                    doSendNotifications();
                }
            };
        ex.run();
    }

    /**
     * Send alerts to moderators. No-op if moderation is not turned
     * on for the forum.
     */
    public void sendModeratorAlerts() {
        KernelExcursion ex = new KernelExcursion() {
                protected void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());
                    doSendModeratorAlerts();
                }
            };
        ex.run();
    }

    private void doSendNotifications() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("sending user notifications");
        }
        Forum forum = getForum();
        if (getStatus().equals(APPROVED)) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Sending forum level subsriptions");
            }
            DataCollection subscriptions = forum.getSubscriptions();

            while (subscriptions.next()) {
                ForumSubscription subscription = (ForumSubscription)
                    DomainObjectFactory.newInstance(
                        subscriptions.getDataObject());
                s_log.debug("notification to  " + subscription.getOID());

                subscription.sendNotification(Post.this);
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug("Sending thread level subsriptions");
            }
            ThreadSubscription sub =
                ThreadSubscription.getThreadSubscription(getThread());

            if (sub != null ) {
                sub.sendNotification(this);
            } else {
                s_log.error("Got a null ThreadSubscription from " +
                            "Post # " + getID());
            }
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Not sending notifications because the " +
                            "message is not approved");
            }
        }
    }

    private void doSendModeratorAlerts() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("sending moderator notice");
        }
        Forum forum = getForum();
        if (forum.isModerated()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Processing moderation alerts");
            }
            DataCollection alerts = forum.getModerationAlerts();

            while (alerts.next()) {
                ModerationAlert alert
                    = (ModerationAlert)
                    DomainObjectFactory.newInstance(alerts.getDataObject());
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Processing moderation alert " + alert.getOID());
                }
                alert.sendNotification(this);
            }
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Not sending moderator alerts because the " +
                            "forum is not moderated");
            }
        }
    }

    /**
     * Set the Forum that contains this post.  Just a wrapper for the
     * setRefersTo method of ThreadedMessage to make the notation a
     * bit nicer.
     *
     * @param forum the Forum that contains this post.
     */

    public void setForum(Forum forum) {
        setRefersTo(forum);
    }

    public Forum getForum() {
        BigDecimal id = getRefersTo();
        Assert.exists(id, BigDecimal.class);
        return new Forum(id);
    }

    /**
     * Map this post to a category.  Creates a mapping
     * saves it immediately.  Throws a PersistenceException if called
     * on a Post object that has not been saved.
     *
     * @param category the Category for this post.
     */

    public void mapCategory(Category category)
        throws PersistenceException {
        if (isNew()) {
            throw new PersistenceException
                ("Post must be persistent to map categories");
        }
        category.addChild(this);
        category.save();
    }

    /**
     * Clears categories for this post. Used when editing a post
     */

    public void clearCategories() {
        DataOperation clearCategories = SessionManager.getSession()
            .retrieveDataOperation("com.arsdigita.forum.clearCategories");
        clearCategories.setParameter("postID", this.getID());
        clearCategories.execute();
        return;
    }

    /**
     * Gets the categories to which this post is assigned
     */
    public CategoryCollection getCategories() {
        return new CategorizedObject(this).getParents();
    }

    /**
     * creates a ThreadSubscription, but only if this is a root
     * Note, you must save() the Post before calling this method.
     */
    public void createThreadSubscription() {
        if (getRoot() == null) {
            ThreadSubscription sub = new ThreadSubscription();
            sub.setThread(getThread());
            sub.save();
        }
    }

    /**
     * Determines if the User has permission to edit this Post.
     * Note that you probably don't want to use this over and
     * over for a list of messages because the permission check
     * on the forum is not cached.
     */
    public boolean canEdit(Party party) {
        Party author = getFrom();
        return (Forum.getConfig().canAuthorEditPosts()
                && author.equals(party))
            || getForum().canEdit(party);
    }

    public void setStatus(String status) {
        Assert.truth(
            (status.equals(APPROVED)
             || status.equals(REJECTED)
             || status.equals(REAPPROVE)
             || status.equals(SUPPRESSED)
             || status.equals(PENDING)
            ),
            "The status must be one of " + APPROVED
            + ", " + REJECTED
            + ", " + REAPPROVE
            + ", "+ SUPPRESSED
            + ", the input was " + status
        );

        set(STATUS, status);
    }

    public String getStatus() {
        return (String)get(STATUS);
    }

    public void setModerator(Party moderator) {
        setAssociation(MODERATOR, moderator);
    }

    public Party getModerator() {
        if (m_moderator == null) {
            DataObject moderatorData = (DataObject) get(MODERATOR);
            if (moderatorData != null) {
                m_moderator = (Party) DomainObjectFactory.newInstance
                    (moderatorData);
            }
        }
        return m_moderator;
    }

    protected void beforeDelete() {
        // First delete associated entries in nt_requests this entry and
        // all of its replies (in case of a root message) have
        List replies = new ArrayList();
        List msgIdList = new ArrayList();
        msgIdList.add(getID());
        if (getRoot() == null) {
            DataCollection msgs = SessionManager.getSession()
                .retrieve(BASE_DATA_OBJECT_TYPE);
            msgs.addEqualsFilter("root", getID());
            while (msgs.next()) {
                replies.add(msgs.getDataObject());
                msgIdList.add( msgs.getDataObject().getOID().get("id"));
            }
        }
        DataCollection requests = SessionManager.getSession()
            .retrieve(Notification.BASE_DATA_OBJECT_TYPE);
        requests.addFilter("messageID in :msgIdList").set("msgIdList", msgIdList);
        while (requests.next()) {
            Notification no = new Notification(requests.getDataObject().getOID());
            no.setMessageDelete(Boolean.FALSE);
            no.delete();
        }

        if (getRoot() == null) {
            // This posting is the root of the thread.  Make sure all postings
            // in this thread are deleted before this very one.  Also
            // take care of thread subscription.
            ThreadSubscription sub = ThreadSubscription.getThreadSubscription(this.getThread());
            sub.delete();
            MessageThread thread = MessageThread.getFromRootMessage(this);
            thread.delete();
            for (Iterator it = replies.iterator(); it.hasNext(); ) {
                Post reply = new Post( (DataObject) it.next());
                reply.delete();
            }
        }
        super.beforeDelete();
    }


    // package access only
    void setLifecycle(LifecycleDefinition life) {
        Lifecycle cycle = life.createFullLifecycle(getSentDate(), ExpirationListener.class.getName());
        LifecycleService.setLifecycle(this, cycle);
        cycle.start();
        cycle.save();
    }


}
