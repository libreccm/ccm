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
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Application;

/**
 * Main domain class of a forum application representing a discussion forum.
 *
 * It manages creation of new forum instances and provides getters and setters
 * for instance specific configuration options.
 *
 * XXX: Forum knows about <i>threads</i> which groups a set of posts to the same
 * subject, and <i>topics</i> which group a set of threads about the same general
 * theme. Currently Forum uses <i>catgegory</i> as synonym for topic, which may be
 * misleading in some contexts, because there is <i>forum-categorized</i> which
 * uses category in the usual CMS way, esp. navigation categories. Should be
 * cleaned up in the future.
 *
 * @author Kevin Scaldeferri (kevin@arsdigita.com)
 * @author chrisg23
 * @author Jens Pelzetter (jensp)
 * @version $Revision:  1.7 $ 
 * @version $Id: Forum.java 2261 2012-01-08 17:17:10Z pboy $
 */
public class Forum extends Application {

    /** Private logger instance for debugging purpose  */
    private static final Logger s_log = Logger.getLogger(Forum.class);
    public static final String BASE_DATA_OBJECT_TYPE =
                               "com.arsdigita.forum.Forum";
    public static final String PACKAGE_TYPE = "forum";
    public static final String THREAD_SUBSCRIPTION_GROUPS_NAME =
                               "Thread Subscription Groups";
    private static final ForumConfig s_config = new ForumConfig();

    static {
        s_log.debug("Static initalizer starting...");
        s_config.load();
        s_log.debug("Static initalizer finished.");
    }

    public static ForumConfig getConfig() {
        return s_config;
    }
    //////
    //Forum specific privileges
    /////
    public static final String FORUM_MODERATION_PRIVILEGE = "forum_moderation";
    public static final String CREATE_THREAD_PRIVILEGE = "forum_create_thread";
    public static final String RESPOND_TO_THREAD_PRIVILEGE = "forum_respond";
    // separate read privilege required because all public users 
    // have READ on homepage, which is parent of forum, hence
    // everyone inherits READ cg
    //
    // note in hindsight, I have stopped homepage being set as
    // permission context for forum, because site search checks 
    // for READ privilege anyway, and so search results were being
    // returned for non public posts. This means there is no longer 
    // any need for a separate forum_read privilege, though it 
    // does no harm. Now removed
    //
    // pb: Reactivated READ privilege in order to provide forums for different
    // groups of users for their internal use and to provide private forums
    // for logged in users only (no public read access).
    public static final String FORUM_READ_PRIVILEGE = "forum_read";
    ///////
    // pdl forum attribute/association names
    ///////
    private static final String POSTS = "posts";
    private static final String SUBSCRIPTIONS = "subscriptions";
    private static final String MODERATION = "isModerated";
    private static final String PUBLIC = "isPublic";
    private static final String NOTICEBOARD = "isNoticeboard";
    private static final String ADMIN_GROUP = "adminGroup";
    private static final String MODERATION_GROUP = "moderationGroup";
    private static final String THREAD_CREATE_GROUP = "threadCreateGroup";
    private static final String THREAD_RESPONDER_GROUP = "threadRespondGroup";
    private static final String READ_GROUP = "readGroup";
    private static final String CATEGORY = "category";
    private static final String EXPIRE_AFTER = "expireAfter";
    private static final String LIFECYCLE_DEFINITION = "lifecycleDefinition";
    // additional attributes added chris.gilbert@westsussex.gov.uk
    private static final String ALLOW_FILE_ATTACHMENTS =
                                "fileAttachmentsAllowed";
    private static final String ALLOW_IMAGE_UPLOADS = "imageUploadsAllowed";
    private static final String AUTOSUBSCRIBE_THREAD_STARTER =
                                "autoSubscribeThreadStarter";
    private static final String INTRODUCTION = "introduction";
    private static final String NO_CATEGORY_POSTS = "noCategoryPostsAllowed";
    private static final String ANONYMOUS_POSTS = "anonymousPostsAllowed";

    /**
     * Creates a new Forum instance encapsulating the given data object.
     * @see com.arsdigita.persistence.Session#retrieve(String)
     *
     * @param data The data object to encapsulate in the Forum instance
     *             (new domain object).
     */
    public Forum(DataObject data) {
        super(data);
    }

    /**
     * The contained <code>DataObject</code> is retrieved from the
     * persistent storage mechanism with an OID specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     *
     * @exception DataObjectNotFoundException Thrown if we cannot
     *            retrieve a data object for the specified OID
     */
    public Forum(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Retrieves a Forum instance (its contained DataObject) based on its
     * internal id which is used to search for the OID.
     *  
     * @param id
     * @throws DataObjectNotFoundException
     */
    public Forum(BigDecimal id) throws DataObjectNotFoundException {
        super(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Convenient class for creation of a standard forum. Property "Moderated"
     * is set to false. 
     * 
     * @param urlName of the forum to  be created
     * @param title of forum to be created
     * @param parent object of forum to be created
     * @return Forum instance
     */
    public static Forum create(String urlName, String title,
                               Application parent) {
        return create(urlName, title, parent, false);
    }

    /**
     * This method should be used to create a new Forum object everywhere
     * except in the constructor of a subclass of Forum.
     * This will by default create a new Category which will be the root
     * category for the Forum in the event that the Forum should be categorized.
     *
     * This also sets up instant and daily subscriptions on the Forum.
     * The default for moderation is false.
     *
     * Also sets default values for other forum settings. These can be
     * amended under the setup tab in the ui
     */
    public static Forum create(String urlName, String title,
                               Application parent, boolean moderated) {
        s_log.debug("creating forum " + title);

        /* Create an aplication instance including a container group in the
         * user administration (5. parameter true) named according to the 
         * forum title.                                                      */
        Forum forum = (Forum) Application.createApplication(
                                          BASE_DATA_OBJECT_TYPE, urlName,
                                          title, parent, true);

        forum.setModerated(moderated);
        forum.setPublic(true);
        // default settings ensure legacy forum users do not
        // see any change  chris.gilbert@westsussex.gov.uk
        forum.setAllowFileAttachments(false);
        forum.setAllowImageUploads(false);
        forum.setAutoSubscribeThreadCreator(false);
        forum.setNoCategoryPostsAllowed(true);
        forum.setAnonymousPostsAllowed(false);

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
            return (Category) DomainObjectFactory.newInstance(category);
        }
    }

    /**
     * Set introduction
     */
    public void setIntroduction(String introduction) {
        set(INTRODUCTION, introduction);
    }

    /**
     * @return introduction
     */
    public String getIntroduction() {
        return (String) get(INTRODUCTION);
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

    /**
     * 
     */
    private void createGroups() {

        Group administrators = new Group();
        administrators.setName(getTitle() + " Administrators");
        setAssociation(ADMIN_GROUP, administrators);

        Group moderators = new Group();
        moderators.setName(getTitle() + " Moderators");
        setAssociation(MODERATION_GROUP, moderators);
        // This is bit of a hack.  For moderator messages to be sent out properly,
        // moderator group has to have proper primary email address.  Which means
        // it has to be unique among all primary email addresses.  Failing to the
        // that, we block *all* emails going out from CCM (whether they're generated
        // by Forum app doesn't matter), since SimpleQueueManager will encounter
        // NPE when trying to retrieve sender's email address, thus stopping any
        // further message processing.
        //   Actually, the only hack involved is making the email address unique.
        String email = "forum-moderator-" + getID() + "-" + moderators.getID()
                       + "@" + s_config.getReplyHostName();
        moderators.setPrimaryEmail(new EmailAddress(email));

        // chris.gilbert@westsussex.gov.uk create additional groups for privilege
        // assignment - could have assigned privileges directly without having
        // associated groups, but this reduces rows in the (already enormous)
        // dnm_permissions table
        Group threadCreators = new Group();
        threadCreators.setName(getTitle() + " Thread Creators");
        setAssociation(THREAD_CREATE_GROUP, threadCreators);

        Group threadResponders = new Group();
        threadResponders.setName(getTitle() + " Thread Responders");
        setAssociation(THREAD_RESPONDER_GROUP, threadResponders);

        Group forumReaders = new Group();
        forumReaders.setName(getTitle() + " Readers");
        setAssociation(READ_GROUP, forumReaders);

        Group container = getGroup();  // Application.getGroup(): get group
        // associated with this application

        container.addSubgroup(administrators);
        container.addSubgroup(moderators);
        container.addSubgroup(threadCreators);
        container.addSubgroup(threadResponders);
        container.addSubgroup(forumReaders);
        Group threadSubscriptions = new Group();
        threadSubscriptions.setName(THREAD_SUBSCRIPTION_GROUPS_NAME);
        container.addSubgroup(threadSubscriptions);
        container.save();

    }

    @Override
    public void initialize() {
        super.initialize();

        if (isNew()) {
            setModerated(false);
            setNoticeboard(false);
            setPublic(true);
            setAllowFileAttachments(false);
            setAllowImageUploads(false);
            setAutoSubscribeThreadCreator(false);
            setNoCategoryPostsAllowed(true);
            setAnonymousPostsAllowed(false);
            createRootCategory();

        }
    }
    private boolean m_wasNew;

    @Override
    protected void beforeSave() {
        m_wasNew = isNew();
        super.beforeSave();
    }

    /** 
     * 
     */
    @Override
    protected void afterSave() {

        if (m_wasNew) {
            PermissionService.setContext(getRootCategory(), this);
            createGroups();
            if (getAdminGroup() != null) {
                PermissionService.grantPermission(new PermissionDescriptor(
                        PrivilegeDescriptor.ADMIN,
                        this,
                        getAdminGroup()));
                s_log.debug("Current user : "
                            + Kernel.getContext().getParty().getPrimaryEmail()
                            + " class is "
                            + Kernel.getContext().getParty().getClass());

                //
                // chris.gilbert@westsussex.gov.uk Original plan was that
                // creator of forum is administrator by default,
                // but party from Kernel at this point in code is
                // acs-system-party - creation must happen in a KernelExcersion
                // somewhere though I can't immediately see where.
                // As a consequence, code below justs causes a classcast exception,
                //
                // revisit, but in meantime, only site admin can administer new forum
                // until forum admin permissions set in UI
                //
                // User creator = (User) Kernel.getContext().getParty();
                // can't be null but let's be supercautious
                // if (creator != null) {
                //     getAdminGroup().addMember(creator);
                // }
                //
            }

            if (getModerationGroup() != null) {
                PermissionService.grantPermission(
                        new PermissionDescriptor(
                        PrivilegeDescriptor.get(
                        FORUM_MODERATION_PRIVILEGE),
                        this,
                        getModerationGroup()));
            }

            if (getThreadCreateGroup() != null) {
                PermissionService.grantPermission(
                        new PermissionDescriptor(
                        PrivilegeDescriptor.get(
                        CREATE_THREAD_PRIVILEGE),
                        this,
                        getThreadCreateGroup()));
                // chris.gilbert@westsussex.gov.uk
                // wouldn't do this normally, but this enables legacy implementations
                // to use new version without any side effects
                // public can view forum by default and see create thread link - existing
                // code forces login if link is selected
                getThreadCreateGroup().addMember(Kernel.getPublicUser());
            }

            if (getThreadResponderGroup() != null) {
                PermissionService.grantPermission(
                        new PermissionDescriptor(
                        PrivilegeDescriptor.get(
                        RESPOND_TO_THREAD_PRIVILEGE),
                        this,
                        getThreadResponderGroup()));
            }

            if (getReadGroup() != null) {
                PermissionService.grantPermission(
                        new PermissionDescriptor(
                        PrivilegeDescriptor.READ,
                        this,
                        getReadGroup()));
                PermissionService.grantPermission(new PermissionDescriptor(
                        PrivilegeDescriptor.get(FORUM_READ_PRIVILEGE),
                        this,
                        getReadGroup()));
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
        }  //if mWasNew

        DataCollection subs = null;
        try {
            subs = getSubscriptions();
            if (subs.isEmpty()) {
                createSubscriptions();
            }

        } finally {
            if (null != subs) {
                subs.close();
            }
        }


        // chris.gilbert@westsussex.gov.uk line removed.
        // afterSave in Application sets permission
        // context of forum to parent app (portal homepage)
        // don't want to inherit permissions of portal,
        // as public users have 'READ' privilege on this
        // and so get shown postings in search results.
        // super.afterSave();

    }  //Method afterSave()

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Gets all the Subscriptions associated with this Forum.
     */
    public DataCollection getAllSubscriptions() {
        DataAssociationCursor dac = ((DataAssociation) get(SUBSCRIPTIONS)).
                cursor();
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
        return (DataAssociation) get(POSTS);
    }

    /**
     * gets all pending messages and messages for reapproval - allows
     * moderators to see which messages require their attention
     * @return
     */
    public DataAssociation getPendingPosts() {
        // doesn't use getPosts in view of the warning that it
        // may disappear
        DataAssociation posts = (DataAssociation) get(POSTS);
        FilterFactory factory = posts.getFilterFactory();
        Filter pending = factory.equals(Post.STATUS, Post.PENDING);
        Filter reapprove = factory.equals(Post.STATUS, Post.REAPPROVE);

        posts.addFilter(factory.or().addFilter(pending).addFilter(reapprove));

        return posts;
    }

    /**
     * gets all suppressed messages - allows moderators to see which messages
     * heve been rejected / require their attention
     * @return
     */
    public DataAssociation getSuppressedPosts() {
        // doesn't use getPosts in view of the warning that it 
        // may disappear
        DataAssociation posts = (DataAssociation) get(POSTS);
        posts.addEqualsFilter(Post.STATUS, Post.SUPPRESSED);
        return posts;
    }

    /**
     * Gets a ThreadCollection of the threads in this forum.  I.e. the
     * top-level posts which are not replies to any other post.
     */
    public ThreadCollection getThreads() {
        return getThreads((BigDecimal) null);
    }

    /**
     * Gets a ThreadCollection of the threads in this forum.  I.e. the
     * top-level posts which are not replies to any other post. It
     * is filtered to only show approved messages, or those posted
     * by the user
     */
    public ThreadCollection getThreads(Party party) {
        ThreadCollection threads = getThreads();

        if (isModerated() && !canModerate(party)) {
            s_log.debug("Only showing approved threads");
            threads.filterUnapproved(party);
        }

        return threads;
    }

    /**
     * Gets a ThreadCollection of the threads in a specific Category.
     */
    public ThreadCollection getThreads(BigDecimal categoryID) {

        DataCollection threadsData = SessionManager.getSession().retrieve(
                MessageThread.BASE_DATA_OBJECT_TYPE);


        threadsData.addEqualsFilter("root.objectID", getID());

        if (categoryID != null) {
            // XXX bad dep on ui package
            if (categoryID.equals(com.arsdigita.forum.ui.Constants.TOPIC_NONE)) {
                Filter f =
                       threadsData.addNotInSubqueryFilter("root.id",
                                                          "com.arsdigita.forum.uncategoryObject");
            } else {
                Filter f =
                       threadsData.addInSubqueryFilter("root.id",
                                                       "com.arsdigita.forum.categoryObject");
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

        if (isModerated() && !canModerate(party)) {
            s_log.debug("Only showing approved threads");
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
                if (null != moderators) {

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
        DataQuery query = session.retrieveQuery(
                "com.arsdigita.forum.getCategorizationSummary");
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
        DataQuery query = session.retrieveQuery(
                "com.arsdigita.forum.getUnusedCategories");
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
        DataQuery query = session.retrieveQuery(
                "com.arsdigita.forum.getUncategorizedSummary");
        query.setParameter("forumID", getID());
        return query;
    }

    public DataAssociationCursor getFilledCategories() {
        Category root = getRootCategory();
        DataAssociationCursor cursor =
                              root.getRelatedCategories(Category.CHILD);

        Filter f =
               cursor.addInSubqueryFilter("id",
                                          "com.arsdigita.forum.filledCategories");
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
        return (getConfig().canAdminEditPosts() && PermissionService.
                checkPermission(
                new PermissionDescriptor(PrivilegeDescriptor.EDIT,
                                         this,
                                         party)));
    }

    /**
     * checks if the user can delete posts in this forum
     */
    public boolean canDelete(Party party) {
        return ((getConfig().canAdminEditPosts() 
                || getConfig().canAuthorDeletePosts())
                && PermissionService.checkPermission(
                new PermissionDescriptor(PrivilegeDescriptor.DELETE,
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
        Boolean old = (Boolean) get(MODERATION);
        if (Boolean.TRUE.equals(old) && !moderate) {

            DataAssociationCursor posts = getPosts().cursor();
            posts.addEqualsFilter(Post.STATUS, Post.PENDING);
            while (posts.next()) {
                Post post = (Post) DomainObjectFactory.newInstance(
                        posts.getDataObject());
                post.setStatus(Post.APPROVED);
                post.save();
            }

        }
        set(MODERATION, new Boolean(moderate));
    }

    public boolean isModerated() {
        Boolean isModerated = (Boolean) get(MODERATION);
        Assert.exists(isModerated);
        return isModerated.booleanValue();
    }

    public boolean isPublic() {
        Boolean isPublic = (Boolean) get(PUBLIC);
        Assert.exists(isPublic);
        return isPublic.booleanValue();
    }

    public void setPublic(boolean isPublic) {
        set(PUBLIC, new Boolean(isPublic));
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

    /** Returns the administrator group. Null if it doesn't exist */
    public Group getAdminGroup() {
        DataObject dObj = (DataObject) get(ADMIN_GROUP);
        Assert.exists(dObj, DataObject.class);
        return (Group) DomainObjectFactory.newInstance(dObj);
    }

    /** Returns the moderator group. Null if it doesn't exist */
    public Group getModerationGroup() {
        DataObject dObj = (DataObject) get(MODERATION_GROUP);
        Assert.exists(dObj, DataObject.class);
        return (Group) DomainObjectFactory.newInstance(dObj);
    }

    /** Returns the thread create group. Null if it doesn't exist */
    public Group getThreadCreateGroup() {
        DataObject dObj = (DataObject) get(THREAD_CREATE_GROUP);
        Assert.exists(dObj, DataObject.class);
        return (Group) DomainObjectFactory.newInstance(dObj);
    }

    /** Returns the thread reply group. Null if it doesn't exist */
    public Group getThreadResponderGroup() {
        DataObject dObj = (DataObject) get(THREAD_RESPONDER_GROUP);
        Assert.exists(dObj, DataObject.class);
        return (Group) DomainObjectFactory.newInstance(dObj);
    }

    /** Returns the read group. Null if it doesn't exist */
    public Group getReadGroup() {
        DataObject dObj = (DataObject) get(READ_GROUP);
        Assert.exists(dObj, DataObject.class);
        return (Group) DomainObjectFactory.newInstance(dObj);
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
        newLife.addPhaseDefinition("Forum posting lifespan",
                                   null,
                                   new Integer(0),
                                   new Integer(1440 * value), // in minutes
                                   null);
        setLifecycleDefinition(newLife);
        //   We must make sure that all existing postings in this forum
        // have the same expiration policy.
        DataAssociationCursor posts = getPosts().cursor();
        while (posts.next()) {
            Post post = (Post) DomainObjectFactory.newInstance(posts.
                    getDataObject());
            if (post.getThread().getRootMessage().getID().equals(post.getID())) {
                s_log.debug("Resetting expiration lifecycle for "
                            + post.getOID());
                post.setLifecycle(newLife);
            }
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

    /**
     * method required for upgrade - normally groups are set during
     * forum creation and so there is no need to invoke a setter
     * @author cgyg9330
     *
     */
    public void setAdminGroup(Group group) {
        setAssociation(ADMIN_GROUP, group);
        PermissionService.grantPermission(
                new PermissionDescriptor(PrivilegeDescriptor.ADMIN, this, group));
    }

    /**
     * method required for upgrade - normally groups are set during
     * forum creation and so there is no need to invoke a setter
     * @author cgyg9330
     *
     */
    public void setThreadCreatorGroup(Group group) {
        setAssociation(THREAD_CREATE_GROUP, group);
        PermissionService.grantPermission(
                new PermissionDescriptor(
                PrivilegeDescriptor.get(CREATE_THREAD_PRIVILEGE),
                this,
                group));
    }

    /**
     * method required for upgrade - normally groups are set during forum
     * creation and so there is no need to invoke a setter
     * @author cgyg9330
     *
     */
    public void setThreadResponderGroup(Group group) {
        setAssociation(THREAD_RESPONDER_GROUP, group);
        PermissionService.grantPermission(
                new PermissionDescriptor(
                PrivilegeDescriptor.get(RESPOND_TO_THREAD_PRIVILEGE),
                this,
                group));
    }

    /**
     * method required for upgrade - normally groups are set during forum
     * creation and so creation and so there is no need to invoke a setter
     * @author cgyg9330
     *
     */
    public void setReaderGroup(Group group) {
        setAssociation(READ_GROUP, group);
        PermissionService.grantPermission(
                new PermissionDescriptor(
                PrivilegeDescriptor.READ, this, group));
    }

    /**
     * @return
     */
    public boolean allowFileAttachments() {
        return ((Boolean) get(ALLOW_FILE_ATTACHMENTS)).booleanValue();
    }

    public boolean allowImageUploads() {
        return ((Boolean) get(ALLOW_IMAGE_UPLOADS)).booleanValue();
    }

    public boolean autoSubscribeThreadStarter() {
        return ((Boolean) get(AUTOSUBSCRIBE_THREAD_STARTER)).booleanValue();
    }

    public boolean noCategoryPostsAllowed() {
        return ((Boolean) get(NO_CATEGORY_POSTS)).booleanValue();
    }

    public boolean anonymousPostsAllowed() {
        return ((Boolean) get(ANONYMOUS_POSTS)).booleanValue();
    }

    public void setAllowFileAttachments(boolean allow) {
        set(ALLOW_FILE_ATTACHMENTS, new Boolean(allow));
    }

    public void setAllowImageUploads(boolean allow) {
        set(ALLOW_IMAGE_UPLOADS, new Boolean(allow));
    }

    public void setAutoSubscribeThreadCreator(boolean subscribe) {
        set(AUTOSUBSCRIBE_THREAD_STARTER, new Boolean(subscribe));
    }

    public void setNoCategoryPostsAllowed(boolean allow) {
        set(NO_CATEGORY_POSTS, new Boolean(allow));
    }

    public void setAnonymousPostsAllowed(boolean allow) {
        set(ANONYMOUS_POSTS, new Boolean(allow));
    }

    public void setTitle(String title) {
        String oldTitle = getTitle();
        super.setTitle(title);
        if (!oldTitle.equals(title)) {
            // 1. rename permission groups
            getAdminGroup().setName(title + " Administrators");
            getModerationGroup().setName(title + " Moderators");
            getThreadCreateGroup().setName(title + " Thread Creators");
            getThreadResponderGroup().setName(title + " Thread Responders");
            getReadGroup().setName(title + " Readers");

            DataCollection subscriptions = getSubscriptions();
            while (subscriptions.next()) {
                ForumSubscription subscription =
                                  (ForumSubscription) DomainObjectFactory.
                        newInstance(subscriptions.getDataObject());
                subscription.getGroup().setName(subscription.getGroupName(this));
            }

            ThreadCollection threads = getThreads();
            while (threads.next()) {
                ThreadSubscription threadSub = ThreadSubscription.
                        getThreadSubscription(
                        threads.getMessageThread());
                threadSub.getGroup().setName(threadSub.getSubscriptionGroupName(
                        this));

            }

        }

    }

//  /*
//   * Application specific method only required if installed in its own
//   * web application context
//   */
//  public String getContextPath() {
//      return "/ccm-forum";
//  }
    /**
     * Returns the path name of the location of the applications servlet/JSP.
     *
     * The method overwrites the super class to provide an application specific
     * location for servlets/JSP. This is necessary if you whish to install the
     * module (application) along with others in one context. If you install the
     * module into its own context (no longer recommended for versions newer
     * than 1.0.4) you may use a standard location.
     *
     * Usually it is a symbolic name/path, which will be mapped in the web.xml
     * to the real location in the file system. Example:
     * <servlet>
     *   <servlet-name>forum-main</servlet-name>
     *   <servlet-class>com.arsdigita.forum.ForumServlet</servlet-class>
     * </servlet>
     *
     * <servlet-mapping>
     *   <servlet-name>forum-main</servlet-name>
     *   <url-pattern>/ccm-forum/main/*</url-pattern>
     * </servlet-mapping>
     *
     * @return path name to the applications servlet/JSP
     */
    @Override
    public String getServletPath() {
        // sufficient if installed into its own web appl. context (ccm-forum)
        // return "/main";
        return "/forum-main/main";
    }
}
