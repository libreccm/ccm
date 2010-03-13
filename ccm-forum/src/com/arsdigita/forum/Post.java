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

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.arsdigita.bebop.PageState;
import com.arsdigita.categorization.CategorizedObject;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleService;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.forum.ui.PostForm;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.messaging.ThreadedMessage;
import com.arsdigita.notification.Notification;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;

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
 *
 *
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

	/**
	 * 0..n association with PostImageAttachments
	 */
	public static final String IMAGE_ATTACHMENTS = "images";

	/**
	 * 0..n association with PostFileAttachments
	 */
	public static final String FILE_ATTACHMENTS = "files";

    /** The status strings */
    public static final String PENDING = "pending";
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";
    public static final String REAPPROVE = "reapprove";
    public static final String SUPPRESSED = "suppressed";


    public static final String POST_STATUS_SUBQUERY =
        "com.arsdigita.forum.threadModerationStatus";

    private Party m_moderator;

	// referred to afterSave method
	private boolean m_wasNew;

    /*
     * The base DomainObject is Post which extends ThreadedMessage. In
     * other words, all bboard messages are ThreadedMessages.
     */

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.forum.Post";

    private Post() {
        this(BASE_DATA_OBJECT_TYPE);
    }

	public Post(String typeName) {
        super(typeName);
    }

    public Post(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

	public Post(BigDecimal id) {
		this(new OID(BASE_DATA_OBJECT_TYPE, id));
	}

    public Post(DataObject obj) {
        super(obj);
    }

    /**
	 * Creates a new Posting in a forum. The post is
	 * not yet in a fit state to be saved as it needs
	 * it's status to be set, and the subject and message
	 * 
     * @param forum the owner forum
     */
    public static Post create(Forum forum) {
        Post post = new Post();
		post.setForum(forum);
        return post;
    }

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
	 * overridden version of method in ThreadedMessage
	 * used to create a reply to an existing post
     */
    public ThreadedMessage newInstance() {
        return create(getForum());
    }

    /**
     * Sets the Message-ID header to &lt;id&gt;.bboard@somehost.com
     * before saving.
     */
    protected void beforeSave() {
		m_wasNew = isNew();
        Forum forum = getForum();
        Assert.exists(forum, Forum.class);

        BigDecimal id = getID();
        // XXX this isn't really the host we want
        setRFCMessageID(id + ".bboard@" +
                        Forum.getConfig().getReplyHostName());
        setReplyTo(getRefersTo() + ".bboard@" +
                   Forum.getConfig().getReplyHostName());

        super.beforeSave();

    }

	/**
	 * set permission contexts for this post to the root post, and for the root
	 * post to the forum. Additionally create a lifecycle if required for a new
	 * root post
	 */

    protected void afterSave() {
        super.afterSave();
        Forum forum = getForum();
        MessageThread root = getThread();
        Assert.exists(root);

        s_log.info("Setting context for " + getOID() + " to " + root.getOID());
        PermissionService.setContext(this, root);
		s_log.info(
			"Setting context for " + root.getOID() + " to " + 
                   forum.getOID());
        PermissionService.setContext(root, forum);
		// originally this was created in beforeSave, but this was when only noticeboard
		// (reply disabled) forums could have a lifecycle. Now that all forums may 
		// have a lifecycle on root posts, the method needs to be here in order 
		// for persistence to work when users are replying to posts chris.gilbert@westsussex.gov.uk

		if (m_wasNew) {
			if (getRoot() == null && forum.getExpireAfter() > 0) {
				s_log.info("Creating expiration lifecycle for " + getOID());
				setLifecycle(forum.getLifecycleDefinition());
			}
		}
		m_wasNew = false;

		DataAssociationCursor files = getFiles();

		// allow attached files to be returned in search results
		// by setting their status as live
		while (files.next()) {
			PostFileAttachment file =
				(PostFileAttachment) DomainObjectFactory.newInstance(
					files.getDataObject());
			if (getStatus().equals(APPROVED)) {
				file.setLive();
			} else {
				file.setDraft();
			}

		}

    }

    /**
     * Sends out the notifications for any subscriptions to the forum
     * or thread to which this message belongs. Only sends
	 * notifications if the post is approved.
     */
	public void sendNotifications(final String context) {
        KernelExcursion ex = new KernelExcursion() {
                protected void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());
				doSendNotifications(context);
                }
            };
        ex.run();
    }

    /**
     * Send alerts to moderators. No-op if moderation is not turned
     * on for the forum.
     */
    public void sendModeratorAlerts() {

		if (!getStatus().equals(APPROVED)) {
			// don't send if pre-approved (ie posted by a moderator)
        KernelExcursion ex = new KernelExcursion() {
                protected void excurse() {
                    setEffectiveParty(Kernel.getSystemParty());
                    doSendModeratorAlerts();
                }
            };
        ex.run();
		} else {
			s_log.debug("not sending moderator alerts because the post " +
				"was pre-approved (created by an approver)");
		}
    }

	private void doSendNotifications(String context) {
            s_log.debug("sending user notifications");
        Forum forum = getForum();
        if (getStatus().equals(APPROVED)) {
                s_log.debug("Sending forum level subsriptions");
            DataCollection subscriptions = forum.getSubscriptions();

            while (subscriptions.next()) {
                ForumSubscription subscription = (ForumSubscription)
                    DomainObjectFactory.newInstance(
                        subscriptions.getDataObject());
                s_log.debug("notification to  " + subscription.getOID());

				subscription.sendNotification(Post.this, Forum.getConfig().deleteNotifications());
            }

                s_log.debug("Sending thread level subsriptions");
			if (context == null || !context.equals(PostForm.NEW_CONTEXT)) {

            ThreadSubscription sub =
                ThreadSubscription.getThreadSubscription(getThread());
				if (sub == null) {
					s_log.error(
						"Got a null ThreadSubscription from "
							+ "Post # "
							+ getID());
            } else {
					sub.sendNotification(this, Forum.getConfig().deleteNotifications());
				}

            }

        } else {
                s_log.debug("Not sending notifications because the " +
                            "message is not approved");
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
					DomainObjectFactory.newInstance(
					alerts.getDataObject());
                    s_log.debug("Processing moderation alert " + alert.getOID());
				alert.sendNotification(this, Forum.getConfig().deleteNotifications());
            }
        } else {
                s_log.debug("Not sending moderator alerts because the " +
                            "forum is not moderated");
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
		DataCollection categories =
			SessionManager.getSession().retrieve(
				Category.BASE_DATA_OBJECT_TYPE);
		categories.addEqualsFilter(
			Category.CHILD_OBJECTS + "." + ACSObject.ID,
			getID());
		while (categories.next()) {
			Category cat =
				(Category) DomainObjectFactory.newInstance(
					categories.getDataObject());
			cat.removeChild(this);
		}

		// above is slower than data operation implementation below,
		// but data op caused problems in persistence. If edited post
		// had topic unchanged, then attempt was made to assign topic
		// category before data op had cleared existing. Hence exception
		// - attempt to map object to same cat twice

		/*
		DataOperation clearCategories =
			SessionManager.getSession().retrieveDataOperation(
				"com.arsdigita.forum.clearCategories");
        clearCategories.setParameter("postID", this.getID());
        clearCategories.execute();
		return;*/
    }

    /**
     * Gets the categories to which this post is assigned
     */
    public CategoryCollection getCategories() {
        return new CategorizedObject(this).getParents();
    }

    /**
	 * creates a ThreadSubscription, and returns it but only if this is a root,
	 * else return null
     * Note, you must save() the Post before calling this method.
     */
	public ThreadSubscription createThreadSubscription() {
		ThreadSubscription sub = null;
        if (getRoot() == null) {
			sub = new ThreadSubscription();
            sub.setThread(getThread());
            sub.save();
        }
		return sub;
	}

	public ThreadSubscription getSubscription() {
		MessageThread thread;
		if (getRoot() != null) {
			thread = getRootMsg().getThread();
		} else {
			thread = getThread();
		}
		DataCollection subscriptions =
			SessionManager.getSession().retrieve(
				ThreadSubscription.BASE_DATA_OBJECT_TYPE);
		subscriptions.addEqualsFilter(
			ThreadSubscription.THREAD,
			thread.getID());
		ThreadSubscription subscription = null;
		while (subscriptions.next()) {
			subscription =
				(ThreadSubscription) DomainObjectFactory.newInstance(
					subscriptions.getDataObject());

		}
		return subscription;
    }

    /**
     * Determines if the User has permission to edit this Post.
     * Note that you probably don't want to use this over and
     * over for a list of messages because the permission check
     * on the forum is not cached.
     */
    public boolean canEdit(Party party) {
        Party author = getFrom();
		// cg added - for anonymous posts, don't allow editing, else everyone could edit everyone else's posts
		return (
			!author.equals(Kernel.getPublicUser())
				&& Forum.getConfig().canAuthorEditPosts()
                && author.equals(party))
            || getForum().canEdit(party);
    }

    public void setStatus(String status) {
        Assert.isTrue(
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

	/**
	 * set the status of a new post according to the priviliges of
	 * the current user - used by UI when creating new post or reply
	 * @param state
	 */
	public void setStatus(PageState state) {
		setStatus(state, null);
	}

	/**
	 * set the status of an edited post according to the privileges
	 * of the current user and the status of the post that is being
	 * edited - used by the edit post UI
	 * @param state
	 * @param previousStatus
	 */
	public void setStatus(PageState state, String previousStatus) {
		ForumContext ctx = ForumContext.getContext(state);
		Forum forum = ctx.getForum();
		// set status of edited post
		if (forum.isModerated() && !ctx.canModerate()) {
			if (Post.APPROVED.equals(previousStatus)) {
				setStatus(Post.REAPPROVE);
			} else {
				setStatus(Post.PENDING);
			}
		} else {
			setStatus(Post.APPROVED);
		}

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

	// note that the replies to this post are deleted in beforeDelete() of 
	// ThreadedMessage (and hence beforeDelete is called recursively on their replies)

    protected void beforeDelete() {
		s_log.debug("Post - before delete " + getID());

		//		threaded message recursively deletes children 
		super.beforeDelete();
		// remove any nt_requests
		DataCollection requests =
			SessionManager.getSession().retrieve(
				Notification.BASE_DATA_OBJECT_TYPE);
		requests.addEqualsFilter(Notification.MESSAGE_ID, this.getID());
        while (requests.next()) {
            Notification no = new Notification(requests.getDataObject().getOID());
            no.setMessageDelete(Boolean.FALSE);
            no.delete();
        }

        if (getRoot() == null) {
			s_log.debug(
				"Root post - get rid of thread subscription and thread");
			// This posting is the root of the thread.  Remove the thread subscription and thread
			MessageThread thread = getThread();
			ThreadSubscription sub =
				ThreadSubscription.getThreadSubscription(thread);
			if (sub != null) {
				// if unconfirmed post, then threadsubscription has not been created
            sub.delete();

            }
			if (thread != null) {
				thread.delete();
        }
    }

	}

    // package access only
    void setLifecycle(LifecycleDefinition life) {
        Lifecycle cycle = life.createFullLifecycle(getSentDate(), ExpirationListener.class.getName());
        LifecycleService.setLifecycle(this, cycle);
        cycle.start();
        cycle.save();
    }

	public void addImage(PostImageAttachment image) {
		DataAssociation images = (DataAssociation) get(Post.IMAGE_ATTACHMENTS);
		image.addToAssociation(images);
		long currentImageCount = images.getDataAssociationCursor().size();
		image.setImageOrder((int) currentImageCount);
	}

	public void removeImage(PostImageAttachment image) {
		DataAssociation images = (DataAssociation) get(Post.IMAGE_ATTACHMENTS);
		image.removeFromAssociation(images);
		renumberImages();
	}

	// image order for a new image is based on the count of existing
	// images, hence necessary to fill in any gaps when images are deleted
	private void renumberImages() {
		int count = 1;
		DataAssociationCursor images = getImages();
		while (images.next()) {
			PostImageAttachment image =
				(PostImageAttachment) DomainObjectFactory.newInstance(
					images.getDataObject());
			image.setImageOrder(count);
			count++;
		}
	}

	public DataAssociationCursor getImages() {
		DataAssociationCursor images =
			((DataAssociation) get(Post.IMAGE_ATTACHMENTS))
				.getDataAssociationCursor();
		images.addOrder(PostImageAttachment.IMAGE_ORDER);
		return images;
	}

	public void addFile(PostFileAttachment file) {
		DataAssociation files = (DataAssociation) get(Post.FILE_ATTACHMENTS);
		file.addToAssociation(files);
		PermissionService.setContext(file, this);
		long currentFileCount = files.getDataAssociationCursor().size();
		file.setFileOrder((int) currentFileCount);
	}

	public void removeFile(PostFileAttachment file) {
		DataAssociation files = (DataAssociation) get(Post.FILE_ATTACHMENTS);
		file.removeFromAssociation(files);
		renumberFiles();

	}

	//	file order for a new file is based on the count of existing
	// files, hence necessary to fill in any gaps when images are deleted

	private void renumberFiles() {
		int count = 1;
		DataAssociationCursor files = getFiles();
		while (files.next()) {
			PostFileAttachment file =
				(PostFileAttachment) DomainObjectFactory.newInstance(
					files.getDataObject());
			file.setFileOrder(count);
			count++;
		}

	}

	public DataAssociationCursor getFiles() {
		DataAssociationCursor files =
			((DataAssociation) get(Post.FILE_ATTACHMENTS))
				.getDataAssociationCursor();
		files.addOrder(PostFileAttachment.FILE_ORDER);
		return files;

	}


	/**
	 * used by thread to prevent counting unapproved posts in the 
	 * reply count.
	 * 
	 */
	// should really be static - revisit this - refer to MessageThread for use
	protected void addReplyFilter(DataCollection replies) {
		replies.addEqualsFilter(STATUS, APPROVED);

	}

}
