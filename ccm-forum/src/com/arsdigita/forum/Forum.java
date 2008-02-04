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

import com.arsdigita.categorization.Category;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.EmailAddress;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.Kernel;
import com.arsdigita.kernel.KernelExcursion;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.permissions.PermissionDescriptor;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.messaging.MessageThread;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * The Forum class represents a discussion forum.
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 *
 * @version $Revision: #25 $ $Author: sskracic $ $DateTime: 2004/08/17 23:26:27 $
 */

public class Forum extends Application {
    public static final String versionId =
        "$Id: Forum.java 755 2005-09-02 13:42:47Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:26:27 $";

    private static ForumConfig s_config = new ForumConfig();

    static {
        s_config.load();
    }

    public static ForumConfig getConfig() {
        return s_config;
    }


    private static final Logger s_log = Logger.getLogger(Forum.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.forum.Forum";

    public static final String PACKAGE_TYPE = "forum";

    public static final String FORUM_MODERATION_PRIVILEGE
        = "forum_moderation";

    private static final String POSTS = "posts";
    private static final String SUBSCRIPTIONS = "subscriptions";
    private static final String MODERATION = "isModerated";
    private static final String NOTICEBOARD = "isNoticeboard";
    private static final String MODERATION_GROUP = "moderationGroup";
    private static final String CATEGORY = "category";
    private static final String EXPIRE_AFTER = "expireAfter";
    private static final String LIFECYCLE_DEFINITION = "lifecycleDefinition";

    public Forum(DataObject data) {
        super(data);
    }

    public Forum(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Forum(BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public static Forum create(String urlName, String title,
                               Application parent) {
        return create(urlName, title, parent, false);
    }

    /**
     * This method should be used to create a new Forum object everywhere
     * except in the constructor of a subclass of Forum.
     * This will by default create a new Category which will be the root
     * category for the Forum in the event that the Forum should be
     * categorized.
     * This also sets up instant and daily subscriptions on the Forum.
     * The default for moderation is false
     */

    public static Forum create(String urlName, String title,
                               Application parent, boolean moderated) {
        s_log.debug("creating forum " + title);
        Forum forum = (Forum) Application.createApplication
            (BASE_DATA_OBJECT_TYPE, urlName, title, parent);

        forum.setModerated(moderated);

        return forum;
    }

    /**
     * Sets Root Category for forum
     */
    private void setRootCategory(Category category) {
        Assert.exists(category, Category.class);
        setAssociation(CATEGORY, category);
    }

    /**
     * @return the Root Category for this forum, or creates a new one
     * does not have a root category, and returns it.
     */

    public Category getRootCategory() {
        DataObject category = (DataObject) get(CATEGORY);
        if (category == null) {
            return createRootCategory();
        } else {
            return (Category)DomainObjectFactory
                .newInstance(category);
        }
    }

    /**
     * creates a Root Category for the forum.
     */
    private Category createRootCategory() {
        Category category = new Category(getTitle(),
                                         "Root category for forum "
                                         + getTitle());
        category.save();
        setRootCategory(category);
        return category;
    }

    private void createModerationGroup() {
        Group moderators = new Group();
        moderators.setName(getTitle() + " Moderators");
        setAssociation( MODERATION_GROUP, moderators );
        // This is bit of a hack.  For moderator messages to be sent out properly,
        // moderator group has to have proper primary email address.  Which means
        // it has to be unique among all primary email addresses.  Failing to the
        // that, we block *all* emails going out from CCM (whether they're generated
        // by Forum app doesn't matter), since SimpleQueueManager will encounter
        // NPE when trying to retrieve sender's email address, thus stopping any
        // further message processing.
        //   Actually, the only hack involved is making the email address unique.
        String email = "forum-moderator-" + getID() + "-" + moderators.getID() + "@" + s_config.getReplyHostName();
        moderators.setPrimaryEmail(new EmailAddress(email));
    }

    public void initialize() {
        super.initialize();

        if (isNew()) {
            setModerated(false);
            setNoticeboard(false);
            createRootCategory();
            createModerationGroup();
        }
    }

    private boolean m_wasNew;

    protected void beforeSave() {
        m_wasNew = isNew();
        super.beforeSave();
    }

    protected void afterSave() {
        if (m_wasNew) {
            PermissionService.setContext(getRootCategory(), this);

            if (getModerationGroup() != null ) {
                PermissionService.grantPermission(
                    new PermissionDescriptor(
                        PrivilegeDescriptor.get(FORUM_MODERATION_PRIVILEGE),
                        this,
                        getModerationGroup())
                );
            }
            KernelExcursion excursion = new KernelExcursion() {
                    protected void excurse() {
                        setParty(Kernel.getSystemParty());
                        // FIXME
                        // Workspace parentWorkspace =
                        //     Workspace.getWorkspaceForApplication
                        //         (Forum.this);
                        //
                        // if (parentWorkspace != null) {
                        //     PermissionService.grantPermission
                        //         (new PermissionDescriptor
                        //          (PrivilegeDescriptor.WRITE,
                        //           Forum.this,
                        //           parentWorkspace.getMemberRole()));
                        // }

                    }
                };
            excursion.run();
        }

        DataCollection subs = null;
        try {
            subs = getSubscriptions();
            if (subs.isEmpty()) {
                createSubscriptions();
            }

        } finally {
            if ( null != subs ) {
                subs.close();
            }
        }


        super.afterSave();
    }

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    /**
     * Gets all the Subscriptions associated with this Forum.
     */
    public DataCollection getAllSubscriptions() {
        DataAssociationCursor dac = ((DataAssociation) get(SUBSCRIPTIONS)).cursor();
        return dac;
    }

    public DataCollection getSubscriptions() {
        DataCollection subs = getAllSubscriptions();
        subs.addEqualsFilter("isModerationAlert", Boolean.FALSE);

        return subs;
    }

    public DataCollection getModerationAlerts() {
        DataCollection subs = getAllSubscriptions();
        subs.addEqualsFilter("isModerationAlert", Boolean.TRUE);

        return subs;
    }

    /**
     * <font color="red">Experimental</font>
     * Gets all the messages which have been posted to this forum.
     * We never actually use this method and it may disappear in the
     * near future.  (This method is not actually as useful as it might
     * appear because you don't get important information like the depth
     * of the message in the thread.  OTOH, maybe it should be modified
     * to use a custom DataQuery which does this.)
     */
    public DataAssociation getPosts() {
        return (DataAssociation)get(POSTS);
    }

    /**
     * Gets a ThreadCollection of the threads in this forum.  I.e. the
     * top-level posts which are not replies to any other post.
     */
    public ThreadCollection getThreads() {
        return getThreads((BigDecimal)null);
    }

    /**
     * Gets a ThreadCollection of the threads in this forum.  I.e. the
     * top-level posts which are not replies to any other post. It
     * is filtered to only show approved messages, or those posted
     * by the user
     */
    public ThreadCollection getThreads(Party party) {
        ThreadCollection threads = getThreads();

        if (isModerated() &&
            !canModerate(party)) {
            s_log.debug( "Only showing approved threads" );
            threads.filterUnapproved(party);
        }

        return threads;
    }


    /**
     * Gets a ThreadCollection of the threads in a specific Category.
     */
    public ThreadCollection getThreads(BigDecimal categoryID) {

         DataCollection threadsData
             = SessionManager.getSession().retrieve(
                MessageThread.BASE_DATA_OBJECT_TYPE);


         threadsData.addEqualsFilter("root.objectID", getID());

         if (categoryID != null) {
             // XXX bad dep on ui package
             if (categoryID.equals(com.arsdigita.forum.ui.Constants.TOPIC_NONE)) {
                 Filter f = threadsData.addNotInSubqueryFilter
                     ("root.id", "com.arsdigita.forum.uncategoryObject");
             } else {
                 Filter f = threadsData.addInSubqueryFilter
                     ("root.id", "com.arsdigita.forum.categoryObject");
                 f.set("categoryID", categoryID);
             }
         }

         threadsData.addOrder("lastUpdate desc");

         return new ThreadCollection(threadsData);
    }

    /**
     * Gets a ThreadCollection of the threads in a specific Category.
     * It is filtered to only show approved messages, or those posted
     * by the user
     */
    public ThreadCollection getThreads(BigDecimal categoryID,
                                       Party party) {
        ThreadCollection threads = getThreads(categoryID);

        if (isModerated() &&
            !canModerate(party)) {
            s_log.debug( "Only showing approved threads" );
            threads.filterUnapproved(party);
        }

        return threads;
    }



    /**
     * Sets up instant and daily subscriptions for the forum.  Daily
     * digests will appear to come from the specified user.  The
     * subscriptions are save()d by this method.
     *
     * @param creationUser the User whom daily digests will come from.
     */
    protected void createSubscriptions() {
        s_log.debug("Creating subscriptions!");

        new KernelExcursion() {
            protected void excurse() {
                setParty(Kernel.getSystemParty());

                final Party party = getConfig().getDigestUser();
                Assert.exists(party, Party.class);

                new ForumSubscription(Forum.this).save();
                new DailySubscription(Forum.this, party).save();

                Group moderators = getModerationGroup();
                if ( null != moderators ) {

                    s_log.debug("creatiing moderation subscription "
                                + moderators.getName());

                    new ModerationAlert(Forum.this, moderators).save();
                }
            }
        }.run();
    }

    /**
     * Gets categories and number of posts for the forum.
     *
     * @return DataQuery with category_id, name, number of threads,
     * and last post date
     */
    public DataQuery getCategories() {
        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.forum.getCategorizationSummary");
        query.setParameter("forumID", getID());
        return query;
    }

    /**
     * Gets empty categories for the forum.
     *
     * @return DataQuery with category_id and name
     */
    public DataQuery getEmptyCategories() {
        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.forum.getUnusedCategories");
        query.setParameter("forumID", getID());
        return query;
    }

    /**
     * Gets Uncategory and number of posts for the forum.
     *
     * @return DataQuery with number of threads
     */
    public DataQuery getUnCategory() {
        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery
            ("com.arsdigita.forum.getUncategorizedSummary");
        query.setParameter("forumID", getID());
        return query;
    }

    public DataAssociationCursor getFilledCategories() {
        Category root = getRootCategory();
        DataAssociationCursor cursor =
            root.getRelatedCategories(Category.CHILD);

        Filter f = cursor.addInSubqueryFilter
            ("id", "com.arsdigita.forum.filledCategories");
        return cursor;
    }

    /**
     * Receives category and returns boolean of whether forum has
     * posts in that category.
     *
     * @return boolean
     */
    public boolean hasCategorizedPosts(Category cat) {
        ThreadCollection children = getThreads(cat.getID());
        if (children.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * checks if the user can edit posts in this forum
     */
    public boolean canEdit(Party party) {
        return (getConfig().canAdminEditPosts() &&
                PermissionService.checkPermission(
                    new PermissionDescriptor(PrivilegeDescriptor.EDIT,
                                             this,
                                             party)));
    }

    public boolean canAdminister(Party party) {
        return PermissionService.checkPermission(
            new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                     this,
                                     party));
    }

    public boolean canModerate(Party party) {
        return PermissionService.checkPermission(
            new PermissionDescriptor(
                PrivilegeDescriptor.get(FORUM_MODERATION_PRIVILEGE),
                this,
                party));
    }

    /**
     * Enables / disables moderation on the forum. When
     * disabling moderation, all pending posts will be
     * automatically marked as approved.
     */
    public void setModerated(boolean moderate) {
        Boolean old = (Boolean)get(MODERATION);
        if (Boolean.TRUE.equals(old) &&
            !moderate) {

            DataAssociationCursor posts = getPosts().cursor();
            posts.addEqualsFilter(Post.STATUS, Post.PENDING);
            while (posts.next()) {
                Post post
                    = (Post)DomainObjectFactory.newInstance(
                        posts.getDataObject());
                post.setStatus(Post.APPROVED);
                post.save();
            }

        }
        set(MODERATION, new Boolean(moderate));
    }

    public boolean isModerated() {
        Boolean isModerated = (Boolean)get(MODERATION);
        Assert.exists(isModerated);
        return isModerated.booleanValue();
    }

    /**
     *  Enables/disables the noticeboard functionality.
     * If enabled, no replies will be allowed.
     */
    public void setNoticeboard(boolean noticeboard) {
        set(NOTICEBOARD, new Boolean(noticeboard));
    }

    public boolean isNoticeboard() {
        return Boolean.TRUE.equals(get(NOTICEBOARD));
    }

    /** Returns the moderator group. Null if it doesn't exist */
    public Group getModerationGroup() {
        DataObject dObj = (DataObject) get( MODERATION_GROUP );
        Assert.exists(dObj, DataObject.class);
        return (Group)DomainObjectFactory.newInstance(dObj);
    }

    public String getContextPath() {
        return "/ccm-forum";
    }

    public String getServletPath() {
        return "/main";
    }

    public void setExpireAfter(int value) {
        set(EXPIRE_AFTER, new BigDecimal(value));
        // remove any previous lifecycle definition
        LifecycleDefinition previousLife = getLifecycleDefinition();
        if (previousLife != null) {
            setLifecycleDefinition(null);
            previousLife.delete();
        }
        if (value == 0) {
            return;
        }
        LifecycleDefinition newLife = new LifecycleDefinition();
        newLife.setLabel("Delete expired noticeboard postings");
        newLife.addPhaseDefinition("Noticeboard posting lifespan",
                                   null,
                                   new Integer(0),
                                   new Integer(1440 * value),  // in minutes
                                   null);
        setLifecycleDefinition(newLife);
        //   We must make sure that all existing postings in this forum
        // have the same expiration policy.
        DataAssociationCursor posts = getPosts().cursor();
        while (posts.next()) {
            Post post
                = (Post)DomainObjectFactory.newInstance(
                    posts.getDataObject());
            s_log.debug("Resetting expiration lifecycle for " + post.getOID());
            post.setLifecycle(newLife);
        }
    }

    public int getExpireAfter() {
        BigDecimal expire = (BigDecimal) get(EXPIRE_AFTER);
        if (expire == null) {
            return 0;
        }
        return expire.intValue();
    }

    public LifecycleDefinition getLifecycleDefinition() {
        DataObject life = (DataObject) get(LIFECYCLE_DEFINITION);
        if (life == null) {
            return null;
        }
        return new LifecycleDefinition(life);
    }

    // Not publicly accessible, since setExpireAfter() is frontend
    private void setLifecycleDefinition(LifecycleDefinition life) {
        set(LIFECYCLE_DEFINITION, life);
    }


}
