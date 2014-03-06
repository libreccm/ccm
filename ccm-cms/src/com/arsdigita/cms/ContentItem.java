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
package com.arsdigita.cms;

import com.arsdigita.auditing.AuditingObserver;
import com.arsdigita.auditing.BasicAuditTrail;
import com.arsdigita.categorization.CategorizedObject;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.contenttypes.ContentGroupAssociation;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.lifecycle.LifecycleService;
import com.arsdigita.cms.lifecycle.PublishLifecycleListener;
import com.arsdigita.cms.publishToFile.QueueManager;
import com.arsdigita.domain.AbstractDomainObjectObserver;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainObjectObserver;
import com.arsdigita.globalization.GlobalizationException;
import com.arsdigita.globalization.Locale;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Reporter;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.versioning.VersionedACSObject;
import com.arsdigita.versioning.Versions;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class represents a content item.
 *
 * <h4>Publishing Items</h4>
 *
 * The {@link #publish(LifecycleDefinition,java.util.Date)} method can be used
 * to schedule the item for publication. The publication of an item proceeds in
 * two steps:
 *
 * 1. Pending Version
 *
 * A pending version is immediately created for the item, and each subitem of
 * the item. When the internal
 * <code>createPendingVersion</code> method is called, the content item will
 * attempt to clone itself in order to create the pending version.
 *
 * First, the item will clone itself and all of its scalar attributes.Then, the
 * item will clone all of its <strong>composite</strong> relations. After that,
 * the item will copy all of its non-composite associations "by reference". If a
 * target of any association is a
 * <code>ContentItem</code>, the cloned item will reference the live or pending
 * version of the target item.
 *
 * For example, consider
 * <code>Articles</code> A and B, both of which reference an
 * <code>ImageAsset</code> I:
 *
 * <blockquote><pre>
 * A ---&gt; I &lt;--- B
 * </pre></blockquote>
 *
 * When A is published, creating a pending version A', I will be published as
 * well:
 *
 * <blockquote><pre>
 * A ---&gt; I &lt;--- B
 * A'---&gt; I'
 * </pre></blockquote>
 *
 * When B is later published as B', B' will reference I':
 *
 * <blockquote><pre>
 * A ---&gt; I &lt;--- B
 * A'---&gt; I'&lt;--- B'
 * </pre></blockquote>
 *
 * In order to work correctly with the automatic publishing code, every subclass
 * of
 * <code>ContentItem</code> (such as "FooItem extends ContentItem") <b>must</b>
 * adhere to the following guidelines:
 *
 * <ul> <li>The subclass must have a constructor of the form <blockquote><pre>
 * public FooItem(DataObject obj) {
 *     super(obj);
 * }
 * </pre></blockquote> </li>
 *
 * <li>The subclass must have a constructor of the form <blockquote><pre>
 * public FooItem(String type) {
 *     super(type);
 *
 *     // Do more stuff here.
 * }
 * </pre></blockquote> </li>
 *
 * <li>If the PDL file for the subclass contains any link attributes, read-only
 * associations, or in general any associations that are not standard, the
 * subclass must implement the {@link #copyProperty(ContentItem, String, ItemCopier)}
 * method. For examples on how to implement it, see the methods's javadoc and
 * the sample implementation in the {@link Article} class.</li> </ul>
 *
 * After the pending version is created, the version copier will assign it a new
 * lifecycle, based on the values passed in to {@link
 * #publish(LifecycleDefinition, java.util.Date)}, but <em>only</em> if the new
 * pending version is a regular aggregation (not a composition). In theory, it
 * should make no difference whether the new pending version is a composition or
 * not; however, some bugs within the publishing code currently prevent this
 * from working correctly. For this reason, it is <em>critically important</em>
 * to pass the right parameter to {@link ItemCopier#copy} the {@link #copyProperty(ContentItem, String,
 * ItemCopier)} method.
 *
 * 2. Live Version
 *
 * When the lifecycle finally rolls around to the start date specified in the
 * <code>publish</code> method, the pending versions for the item and all the
 * subitems will be promoted to live, and the item will appear on the live site.
 * Another publishing bug currently makes it a <em>requirement</em> to reload
 * the original item from the database after it has been successfully published;
 * I am working on fixing this.
 *
 * 3. Unpublishing
 *
 * When the lifecycle for an item expires, its live version is deleted and
 * removed from the live site, along with all its subitems.
 *
 * 4. Future work
 *
 * The new data model makes it possible to have multiple pending versions for a
 * content item; it should also be theoretically possible to archive expired
 * live versions, as opposed to deletin g them. There are no Java APIs for this
 * functionality as of yet, however.
 *
 * <h4>Copying Items</h4>
 *
 * The {@link ItemCopier#copy} method may be used to create a nearly identical
 * copy of the item, according to the rules described above. The new item will
 * be a full-fledged, standalone item. Note that the services (such as
 * categories) will not be automatically transferred to the new copy of the
 * item; the {@link
 * #copyServicesFrom(ContentItem)} method must be called on the new item to
 * transfer the services. Calling this method is not a requirement, however.
 *
 * @author Uday Mathur
 * @author Jack Chung
 * @author Michael Pih
 * @author Stanislav Freidin &lt;sfreidin@redhat.com&gt;
 * @author Jens Pelzetter
 *
 * @version $Id: ContentItem.java 2305 2012-05-01 12:26:33Z pboy $
 */
public class ContentItem extends VersionedACSObject implements CustomCopy {

    private static final Logger s_log = Logger.getLogger(ContentItem.class);
    private static final Logger s_logDenorm =
                                Logger.getLogger(ContentItem.class.getName()
                                                 + ".Denorm");
    private static final String MODEL = "com.arsdigita.cms";
    private static final String QUERY_PENDING_ITEMS =
                                MODEL + ".getPendingSortedByLifecycle";
    public static final String BASE_DATA_OBJECT_TYPE = MODEL + ".ContentItem";
    /**
     * A state marking the draft or master item corresponding to a live or
     * pending version of that item.
     */
    public static final String DRAFT = "draft";
    /**
     * A state marking the live version, a copy of the draft item.
     */
    public static final String LIVE = "live";
    /**
     * A state marking the live version, a copy of the draft item.
     */
    public static final String PENDING = "pending";
    // Metadata attribute constants
    public static final String ANCESTORS = "ancestors";
    public static final String PARENT = "parent";
    public static final String CHILDREN = "contentChildren";
    public static final String CONTENT_TYPE = "type";
    public static final String VERSION = "version";
    public static final String NAME = "name";
    public static final String ADDITIONAL_INFO = "additionalInfo";
    public static final String LANGUAGE = "language";
    public static final String AUDITING = "auditing";
    public static final String DRAFT_VERSION = "masterVersion";
    public static final String VERSIONS = "slaveVersions";
    public static final String CONTENT_SECTION = "section";
    private static final String PUBLISH_LISTENER_CLASS =
                                PublishLifecycleListener.class.getName();
    private VersionCache m_pending;
    private VersionCache m_live;
    private boolean m_wasNew;
    private Reporter m_reporter;
    private BasicAuditTrail m_audit_trail;

    /**
     * Default constructor. This creates a new content item.
     */
    public ContentItem() {
        this(BASE_DATA_OBJECT_TYPE);

        s_log.debug("Undergoing creation");
    }

    /**
     * Constructor. The contained
     * <code>DataObject</code> is retrieved from the persistent storage
     * mechanism with an
     * <code>OID</code> specified by
     * <code>oid</code>.
     *
     * @param oid The
     * <code>OID</code> for the retrieved
     * <code>DataObject</code>
     */
    public ContentItem(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained
     * <code>DataObject</code> is retrieved from the persistent storage
     * mechanism with an
     * <code>OID</code> specified by
     * <code>id</code> and
     * <code>ContentItem.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The
     * <code>id</code> for the retrieved
     * <code>DataObject</code>
     */
    public ContentItem(final BigDecimal id)
            throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. Retrieves or creates a content item using the
     * <code>DataObject</code> argument.
     *
     * @param obj The
     * <code>DataObject</code> with which to create or load a content item
     */
    public ContentItem(final DataObject obj) {
        super(obj);
    }

    /**
     * Constructor. Creates a new content item using the given data object type.
     * Such items are created as draft versions.
     *
     * @param type The
     * <code>String</code> data object type of the item to create
     */
    public ContentItem(final String type) {
        super(type);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Content item " + this + " created with type " + type);
        }
    }
    private static DomainObjectObserver s_parentObs =
                                        new AbstractDomainObjectObserver() {

        public void set(DomainObject dobj, String name,
                        Object old, Object newVal) {
            if (PARENT.equals(name)) {
                ContentItem ci = (ContentItem) dobj;

                if (newVal != null) {
                    PermissionService.setContext(ci.getOID(),
                                                 ((DataObject) newVal).getOID());
                }
            }
        }
    };

    /**
     * Called from the base class (
     * <code>DomainObject</code>) constructors.
     */
    protected void initialize() {
        super.initialize();
        addObserver(s_parentObs);

        DataObject dataObj = (DataObject) get(AUDITING);
        if (dataObj != null) {
            m_audit_trail = new BasicAuditTrail(dataObj);
        } else {
            // creates a new one when one doesn't already exist
            m_audit_trail = BasicAuditTrail.retrieveForACSObject(this);
        }

        addObserver(new AuditingObserver(m_audit_trail));

        m_pending = new VersionCache();
        m_live = new VersionCache();

        m_reporter = new Reporter(s_log, this, ContentItem.class);

        if (isNew()) {
            s_log.debug(this + " is being newly created; "
                        + "marking it as a draft version");

            m_wasNew = true;

            set(VERSION, DRAFT);

            setMaster(this);

            try {
                final ContentType type =
                                  ContentType.findByAssociatedObjectType(
                        getSpecificObjectType());

                if (s_log.isDebugEnabled()) {
                    s_log.debug("Set content type for " + this + " to " + type);
                }
                setContentType(type);
            } catch (DataObjectNotFoundException donfe) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("No content type found for " + this);
                }
                // Do nothing.  There's no associated content
                // type.
            }
        } else {
            if (Assert.isEnabled()) {
                Assert.exists(getVersion(), String.class);
            }
        }
    }

    /**
     * @return the base PDL object type for this item. Child classes should
     *         override this method to return the correct value
     */
    @Override
    public String getBaseDataObjectType() {
        return this.BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Publicized getter method for use by metadata forms.
     *
     * @param key
     *
     * @return
     */
    @Override
    public Object get(final String key) {
        return super.get(key);
    }

    /**
     * Public setter method for use by metadata forms.
     *
     * @param key param value
     */
    @Override
    public void set(final String key, final Object value) {
        super.set(key, value);
    }

    /**
     * Public add for use by metadata forms.
     *
     * @param propertyName param dobj
     *
     * @return
     */
    @Override
    public DataObject add(String propertyName, DomainObject dobj) {
        return super.add(propertyName, dobj);
    }

    /**
     * Public remove for use by metadata forms
     *
     * @param propertyName param dobj
     */
    @Override
    public void remove(String propertyName, DomainObject dobj) {
        super.remove(propertyName, dobj);
    }

    /**
     * For new content items, sets the associated content type if it has not
     * been already set.
     */
    @Override
    protected void beforeSave() {
        m_wasNew = isNew();

        super.beforeSave();

        if (m_wasNew) {
            // Set the default content section.
            if (getContentSection() == null) {
                setDefaultContentSection();
            }
        }
    }

    /*
     * *
     * removed cg - object observer sets context based on parent whenever parent
     * is updated
     *
     * protected void afterSave() { super.afterSave(); s_log.info("******After
     * Save of object " + getOID()); // Set the object's context to its parent
     * object for // permissioning. if (m_wasNew) { final ACSObject parent =
     * getParent(); if (parent == null) { s_log.info("parent is null - set
     * context to content section"); PermissionService.setContext(this,
     * getContentSection()); } else { s_log.info("parent is " +
     * parent.getOID()); PermissionService.setContext(this, parent); } } }
     */
    private void setDefaultContentSection() {
        s_log.debug("Setting the default content section");

        final String version = getVersion();

        if (version != null && version.equals(ContentItem.DRAFT)) {
            // If the parent is not a folder, the content section of
            // the child (this item) should not be set.

            final ACSObject parent = getParent();

            if (parent != null && parent instanceof Folder) {
                setContentSection(((ContentItem) parent).getContentSection());
            } else {
                s_log.debug("The item's parent is not a folder; I am "
                            + "not setting the default content section");
            }
        } else {
            s_log.debug("The item's version is null or it is not draft; "
                        + "doing nothing");
        }
    }

    /**
     * Fetch the display name of the content item. The display name for a {@link com.arsdigita.cms.ContentItem}
     * is the name property.
     *
     * @return The name of the content item
     */
    public String getDisplayName() {
        return getName();
    }

    /**
     * Fetches the name of the content item.
     *
     * @return The name of the content item
     */
    public String getName() {
        return (String) get(NAME);
    }

    /**
     * Sets the name of the content item.
     *
     * @param value The name of the content item
     */
    public void setName(final String value) {
        Assert.exists(value, String.class);

        set(NAME, value);

        m_reporter.mutated("name");
    }

    public String getAdditionalInfo() {
        return (String) get(ADDITIONAL_INFO);
    }
    
    public void setAdditionalInfo(final String additionalInfo) {
        set(ADDITIONAL_INFO, additionalInfo);
    }
    
    /**
     * Get the parent object.
     */
    public ACSObject getParent() {
        return (ACSObject) DomainObjectFactory.newInstance((DataObject) get(
                PARENT));
    }

    /**
     * Set the parent object.
     *
     * @param object The
     * <code>ACSObject</code> parent
     */
    public final void setParent(final ACSObject object) {
        setAssociation(PARENT, object);
        m_reporter.mutated("parent");
    }

    /**
     * Fetches all the child items of this item.
     *
     * @return an
     * <code>ItemCollection</code> of children
     */
    public final ItemCollection getChildren() {
        final DataAssociationCursor cursor =
                                    ((DataAssociation) super.get(CHILDREN)).
                cursor();

        return new ItemCollection(cursor);
    }

    /**
     * Gets the content type of this content item.
     */
    public ContentType getContentType() {
        DataObject type = (DataObject) get(CONTENT_TYPE);

        if (type == null) {
            return null;
        } else {
            return new ContentType(type);
        }
    }

    /**
     * Sets the content type of this content item.
     *
     * @param type The content type
     */
    public void setContentType(ContentType type) {
        setAssociation(CONTENT_TYPE, type);

        m_reporter.mutated("contentType");
    }

    public boolean isContentType(ContentType type) {

        try {
            // Try to cast this contentItem to the desired content type
            // This will succeed if this ci is of the type or a subclass
            Class.forName(type.getClassName()).cast(this);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Returns the content section to which this item belongs. Fetches the
     * denormalized content section of an item. If one is not found, this method
     * returns null.
     *
     * Since
     * <code>cms_items.section_id</code> is a denormalization, this method may
     * return null even if the item "belongs" to a content section. For example,
     * calling
     * <code>getContentSection()</code> on an Article's
     * <code>ImageAsset</code> will return null even though the image asset
     * should belong to the same section as the article.
     *
     * @return The content section to which this item belongs
     */
    public ContentSection getContentSection() {
        return (ContentSection) DomainObjectFactory.newInstance((DataObject) get(
                CONTENT_SECTION));
    }

    /**
     * Set the content section of an item.
     *
     * @param section The content section
     */
    public final void setContentSection(final ContentSection section) {
        setAssociation(CONTENT_SECTION, section);

        m_reporter.mutated("contentSection");
    }

    /**
     * Return the path to the item starting at its root. The path is absolute,
     * of the form <tt>/x/y/z</tt> where <tt>x</tt> and <tt>y</tt> are the names
     * of the item's grandparent and parent respectively, and <tt>z</tt> is the
     * name of the item itself.
     *
     * The item's root is the ancestor reachable through repeated
     * <code>getParent()</code> calls whose parent is
     * <code>null</code>. This is usually a folder, but may be any {
     *
     * @see com.arsdigita.kernel.ACSObject}.
     *
     * Note that the name of the root folder of the content section where the
     * item resides is not included in the path.
     *
     * @see #getPathInfo(boolean)
     * @return the path from the item's root to the item
     */
    public String getPath() {
        return getPathNoJsp();
    }

    /**
     *
     * @see #getPathInfo(boolean)
     *
     * @return the path from the item's root to the item
     */
    public String getPathNoJsp() {
        StringBuffer result = new StringBuffer(400);
        ItemCollection coll = getPathInfo(true);
        coll.next();
        s_log.debug("Get item path not jsp");
        boolean first = true;
        while (coll.next()) {
            if (!first) {
                result.append('/');
            } else {
                first = false;
            }
            s_log.debug("Add " + coll.getName());
            result.append(coll.getName());
            s_log.debug("Now " + result);
        }

        return result.toString();
    }

    /**
     * Return a collection of ancestors starting from the item's root to the
     * item's parent item. For items contained in folders this is similar to a
     * directory path to the item. The collection starts with the root item and
     * ends with the item's direct parent.
     *
     * <p> The item's root is the ancestor reachable through repeated
     * <code>getParent()</code> calls whose parent is
     * <code>null</code>. This is usually a folder, but may be any {
     *
     * @see com.arsdigita.kernel.ACSObject}.
     *
     * @see #getPathInfo(boolean)
     *
     * @return the collection of the item's ancestors.
     */
    public ItemCollection getPathInfo() {
        return getPathInfo(false);
    }

    /**
     * Return a collection of ancestors starting from the item's root to the
     * item's parent item (if
     * <code>includeSelf</code> is
     * <code>false</code>) or to the item itself otherwise. For items contained
     * in folders this is similar to a directory path to the item. The
     * collection starts with the root item and ends with the item's direct
     * parent.
     *
     * <p> The item's root is the ancestor reachable through repeated
     * <code>getParent()</code> calls whose parent is
     * <code>null</code>. This is usually a folder, but may be any {
     *
     * @see com.arsdigita.kernel.ACSObject}.
     *
     * @param includeSelf a
     * <code>boolean</code> value.
     *
     * @return the items on the path to the root folder.
     */
    public ItemCollection getPathInfo(boolean includeSelf) {
        DataCollection collection = SessionManager.getSession().retrieve(
                BASE_DATA_OBJECT_TYPE);

        String ids = (String) get(ANCESTORS);
        if (ids == null) {
            // this should not happen
            if (includeSelf) {
                // there are no ancestors so we only return this item
                collection.addEqualsFilter(ID, getID());
                return new ItemCollection(collection);
            } else {
                // there are no ancestors and we want want to return this
                // it so we want an empty collection...but, this should
                // never happen
                collection.addFilter("1=2");
                return new ItemCollection(collection);
            }
        }

        //add list of ancestors split by "/" character
        ArrayList ancestors = new ArrayList();
        int iIndex = 0;
        for (int i = ids.indexOf("/", 0); i != -1; i = ids.indexOf("/", iIndex)) {
            ancestors.add(ids.substring(0, i + 1));
            iIndex = i + 1;
        }

        Filter filter = collection.addFilter(ANCESTORS + " in :ancestors");
        filter.set("ancestors", ancestors);

        collection.addOrder(ANCESTORS);
        if (!includeSelf) {
            collection.addNotEqualsFilter(ID, getID());
        }

        return new ItemCollection(collection);
    }

    //
    //  Methods for accessing and linking content item versions
    //
    /**
     * Gets the version tag.
     */
    public String getVersion() {
        return (String) get(VERSION);
    }

    /**
     * Sets the version tag.
     *
     * @param version A version tag, {@link #LIVE} or {@link #DRAFT} or {@link #PENDING}
     */
    protected void setVersion(final String version) {
        set(VERSION, version);

        m_reporter.mutated("version");
    }

    /**
     * Returns
     * <code>true</code> if this item is a
     * <code>DRAFT</code> version.
     *
     * @return < code>true</code> if this item is a
     * <code>DRAFT</code> version
     */
    public boolean isDraftVersion() {
        return DRAFT.equals(getVersion());
    }

    /**
     * Returns the
     * <code>DRAFT</code> version of this content item.
     *
     * @return the draft version
     */
    public ContentItem getDraftVersion() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting the draft version of " + this);
        }

        final DataObject draft = (DataObject) get(DRAFT_VERSION);

        if (draft == null) {
            // XXX I would like to put this here, but publishing
            // prevents us.

            //assertDraft();

            return this;
        } else {
            return (ContentItem) DomainObjectFactory.newInstance(draft);
        }
    }

    /**
     * Fetches the draft (aka, "master" or "working") version of this content
     * item.
     *
     * @return the working version representation of the
     * <code>ContentItem</code>, possibly this item
     *
     * @deprecated use {@link #getDraftVersion()} instead
     */
    public ContentItem getWorkingVersion() {
        return getDraftVersion();
    }

    /**
     * Returns
     * <code>true</code> if this item is a
     * <code>PENDING</code> version.
     *
     * @return < code>true</code> if
     * <code>this</code> is one of the pending versions
     */
    public boolean isPendingVersion() {
        return PENDING.equals(getVersion());
    }

    /**
     * Returns one
     * <code>PENDING</code> version of this content item.
     *
     * @return one of the pending versions
     */
    ContentItem getPendingVersion() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("getPendingVersion: " + getOID());
        }

        if (m_pending.isCached()) {
            return m_pending.get();
        }

        return m_pending.set(getUncachedPendingVersion());
    }

    private ContentItem getUncachedPendingVersion() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("getUncachedPendingVersion: " + getOID());
        }

        ItemCollection versions = getPendingVersions();
        try {
            if (versions.next()) {
                return versions.getContentItem();
            }
            return null;
        } finally {
            versions.close();
        }
    }

    /**
     * <p>Fetches the pending versions, if any, of this content item. The
     * versions are returned in chronological order, sorted by their respective
     * lifecycle's start date.</p>
     *
     * @return the collection of pending versions for this item
     */
    public ItemCollection getPendingVersions() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("getPendingVersions: " + getOID());
        }
        DataQuery versions = getSession().retrieveQuery(QUERY_PENDING_ITEMS);
        versions.setParameter("itemID", getDraftVersion().getID());

        return new ItemCollection(new DataQueryDataCollectionAdapter(versions,
                                                                     "item"));
    }

    /**
     * Adds a pending version to the item.
     */
    protected void addPendingVersion(final ContentItem version) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding pending version " + version);
        }

        if (Assert.isEnabled()) {
            Assert.exists(version, ContentItem.class);
            assertDraft();
            version.assertPending();
        }

        add(VERSIONS, version);
        m_pending.clear();
    }

    /**
     * Removes a pending version from the item.
     *
     * @param version the version to remove
     */
    public void removePendingVersion(final ContentItem version) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing pending version " + version);
        }

        if (Assert.isEnabled()) {
            Assert.exists(version, ContentItem.class);
            assertDraft();
            version.assertPending();
        }

        remove(VERSIONS, version);
        m_pending.clear();
        version.delete();
    }

    /**
     * Returns
     * <code>true</code> if this item is a
     * <code>LIVE</code> version.
     *
     * @return < code>true</code> if
     * <code>this</code> is the live version
     */
    public boolean isLiveVersion() {
        return LIVE.equals(getVersion());
    }

    /**
     * Fetches the live version of this content item. Returns null if there is
     * none.
     *
     * @return a
     * <code>ContentItem</code> representing the live version
     */
    public ContentItem getLiveVersion() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting the live version of " + this);
        }

        if (LIVE.equals(getVersion())) {
            return this;
        }

        if (m_live.isCached()) {
            return m_live.get();
        }

        s_log.debug("m_live miss");

        final DataAssociationCursor versions =
                                    ((DataAssociation) get(VERSIONS)).cursor();

        versions.addEqualsFilter(VERSION, LIVE);

        try {
            if (versions.next()) {
                ContentItem item =
                            (ContentItem) DomainObjectFactory.newInstance(versions.
                        getDataObject());
                return m_live.set(item);
            }
            return m_live.set(null);
        } finally {
            versions.close();
        }
    }

    /**
     * Sets the live version.
     *
     * @param version The
     * <code>ContentItem</code> to set live
     */
    protected void setLiveVersion(final ContentItem version) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting live version to " + version);
        }

        if (Assert.isEnabled()) {
            assertDraft();
        }

        final ContentItem live = getLiveVersion();

        if (live != null) {
            remove(VERSIONS, live);
        }

        if (version == null) {
            m_live.set(null);
        } else {
            add(VERSIONS, version);
            m_live.set(version);
        }
    }

    /**
     * Get the live version for the item. If no live version exists, return the
     * latest pending version, if any.
     *
     * @return the public version for this item, or null if none
     */
    public ContentItem getPublicVersion() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("getPublicVersion: " + getOID());
        }

        final ContentItem live = getLiveVersion();

        if (live == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("getPublicVersion: no live version " + getOID());
            }
            return getPendingVersion();
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("getPublicVersion: returning live version " + getOID());
        }

        return live;
    }

    //
    // Publishing methods
    //
    /**
     * Method to determine whether this ContentItem should be automatically
     * published to the file system.
     */
    protected boolean canPublishToFS() {
        return true;
    }

    /**
     * Publish this item to the filesystem; can only be called on a live
     * version.
     */
    protected void publishToFS() {
        if (!canPublishToFS()) {
            return;
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Publishing item " + this + " to the file system");
        }

        assertLive();

        QueueManager.queuePublish(this);
    }

    protected void unpublishFromFS() {
        if (!canPublishToFS()) {
            return;
        }

        if (s_log.isDebugEnabled()) {
            s_log.debug("Unpublishing item " + this + " to the file system");
        }

        assertLive();

        QueueManager.queueUnpublish(this);
    }

    /**
     * Returns true if this language version of this item has a publicly viewable
     * version. This item is not necessarily the live version nor is this
     * method to be confused with isPublished.
     *
     * @return < code>true<code> if this content item has a live
     * version, or if it <em>is</em> the live version
     */
    public boolean isLive() {
        return getLiveVersion() != null;
    }

    // Added by: Quasimodo
    /**
     * Returns true if this item has a publicly viewable version in any language.
     * This item is not necessarily the live version nor is this method
     * to be confused with isPublished.
     *
     * @return < code>true<code> if this content bundle item has a live
     * version
     */
    public boolean hasLiveInstance() {
        return getBundle().getLiveVersion() != null;
    }

    /**
     * Makes an item live or not live.
     *
     * @param version the version which should become live, null to make the
     *                item non-live
     */
    public void setLive(final ContentItem version) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting item " + this + " live with version "
                        + version);
        }

        if (Assert.isEnabled()) {
            Assert.isTrue(version == null || LIVE.equals(version.getVersion()),
                          "Item version " + version + " must be null or "
                          + "the live version");
        }

        if (isLive()) {
            s_log.debug("The item is already live; getting the current "
                        + "live version");

            final ContentItem oldVersion = getLiveVersion();

            if (s_log.isDebugEnabled()) {
                s_log.debug("The current live version is " + oldVersion);
            }

            ACSObject parent = null;

            if (version == null) {
                // Find all live items with the same parent as this
                // item, other than this item.

                // XXX We don't need to use a custom query here
                // anymore.
                final DataQuery items =
                                SessionManager.getSession().retrieveQuery(
                        "com.arsdigita.cms.getLiveItemsWithSameParent");
                items.addNotEqualsFilter("id", oldVersion.getID());
                items.setParameter("itemId", oldVersion.getID());

                // If there aren't any, unpublish the parent.  Don't
                // get the parent of the live version, because it all
                // breaks.

                if (!items.next()) {
                    parent = getParent();

                    if (s_log.isDebugEnabled()) {
                        s_log.debug(oldVersion + " is the last child of "
                                    + parent);
                    }
                }

                items.close();
            }

            // Queue task to delete any files written for this item.
            if (oldVersion.canPublishToFS()) {
                oldVersion.unpublishFromFS();
            }

            if (version == null || !version.equals(oldVersion)) {
                s_log.debug("Deleting old live version");

                oldVersion.delete();
                PublishedLink.refreshOnUnpublish(this);
            }

            if (parent instanceof ContentBundle || parent instanceof Folder) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("Parent of " + oldVersion + " is " + parent
                                + "; unpublishing the parent");
                }

                ((ContentItem) parent).setLive(null);
            }

            s_log.debug("Setting the live version association to null and "
                        + "saving");

            setLiveVersion(null);

            save();
        }

        if (version != null) {
            s_log.debug("The new version is not null; setting the live "
                        + "version association");

            setLiveVersion(version);

            save();

            PublishedLink.updateLiveLinks(version);
            save();

            // publish item (as template or html pages) to the file
            // system if appropriate
            if (version.canPublishToFS()) {
                version.publishToFS();
            }
        }
    }

    /**
     * Schedules an item for publication.
     *
     * @param cycleDef  The lifecycle definition
     * @param startDate The time to schedule the start of the lifecycle. If
     *                  null, use the current time as the start date.
     *
     * @return the new pending version
     */
    public ContentItem publish(final LifecycleDefinition cycleDef,
                               final Date startDate) {

        applyTag("Published");
        Versions.suspendVersioning();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Publishing item " + this + " with lifecycle "
                        + "definition " + cycleDef + " and start date "
                        + startDate);
        }
        /*
         * amended Chris Gilbert
         *
         * Some content types may have their own lifecycles with their own
         * default listeners. Previous implementation just enforced the listener
         * retrieved from getPublisherClassName. This amendment looks for a
         * default listener in the cycle definition first
         *
         */
        String listener = cycleDef.getDefaultListener();
        if (listener == null) {
            listener = getPublishListenerClassName();
        }
        final Lifecycle cycle =
                        cycleDef.createFullLifecycle(startDate, listener);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Instantiated lifecycle " + cycle);
        }

        // Create the pending version for the item
        final ContentItem pending = createPendingVersion(cycle);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Created pending content item " + pending);
        }

        if (Assert.isEnabled()) {
            Assert.exists(pending, ContentItem.class);
            Assert.isTrue(PENDING.equals(pending.getVersion())
                          || LIVE.equals(pending.getVersion()),
                          "The new pending item must be pending or live; "
                          + "instead it is " + pending.getVersion());
        }
        return pending;
    }

    public String getPublishListenerClassName() {
        String className = ContentSection.getConfig().
                getPublishLifecycleListenerClass();
        if (className != null && !"".equals(className)) {
            return className;
        } else {
            return PUBLISH_LISTENER_CLASS;
        }
    }

    /**
     * Unpublishes an item. This method removes the item's lifecycle and removes
     * all pending versions. It is intended for use in UI code, and it should
     * not be used for making items go "unlive". Instead, use
     * <code>setLive(null)</code>.
     */
    public void unpublish() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Unpublishing item " + this);
        }
        Versions.suspendVersioning();

        if (isLive()) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("The item is currently live; removing the "
                            + "lifecycle of the public version, "
                            + getPublicVersion());
            }

            removeLifecycle(getPublicVersion());

            setLive(null);
        } else {
            s_log.debug("The item is not live; removing its lifecycle");

            removeLifecycle(this);
        }

        s_log.debug("Removing all pending versions");

        final ItemCollection pending = getPendingVersions();

        while (pending.next()) {
            final ContentItem item = pending.getContentItem();

            removePendingVersion(item);
        }

        save();
    }

    /**
     * Republish the item using its existing lifecycle
     */
    public void republish() {
        republish(false);
    }

    /**
     * Republish the item @parameter reset - if true create a new lifecycle, if
     * false use existing Called from ui.lifecycle.ItemLifecycleItemPane.java
     */
    public void republish(boolean reset) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Republishing item " + getOID().toString());
        }

        applyTag("Republished");
        Versions.suspendVersioning();

        Assert.isTrue(isLive(), "Attempt to republish non live item " + getOID());

        //ToDo Remove item from cache
        if (CMSConfig.getInstanceOf().getEnableXmlCache()) {
            XMLDeliveryCache.getInstance().removeFromCache(getOID());
        }
        
        Lifecycle cycle = getLifecycle();
        Assert.exists(cycle, Lifecycle.class);
        //resets lifecycle if opted
        if (reset) {
            cycle.reset();
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Reusing lifecycle " + cycle.getOID());
        }

        ContentItem newLive = createPendingVersion(cycle);
        setLive(null);
        promotePendingVersion(newLive);
    }

    /**
     * Fetches the publication lifecycle.
     *
     * @return The associated lifecycle, null if there is none
     */
    public Lifecycle getLifecycle() {
        s_log.debug("Resolving the item's lifecycle");

        final Lifecycle lifecycle = LifecycleService.getLifecycle(this);

        if (lifecycle == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("The item has no lifecycle; checking if the "
                            + "public version has a lifecycle");
            }

            final ContentItem pub = getPublicVersion();

            if (pub == null) {
                s_log.debug("There is no public version; returning null");

                return null;
            } else {
                final Lifecycle cyclelife = LifecycleService.getLifecycle(pub);

                if (s_log.isDebugEnabled()) {
                    s_log.debug("The public version has a lifecycle; "
                                + "returning " + cyclelife);
                }

                return cyclelife;
            }
        } else {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Found " + lifecycle);
            }

            return lifecycle;
        }
    }

    /**
     * Return true if this item has been published.
     *
     * @return true if this item has a lifecycle, false otherwise
     */
    public boolean isPublished() {
        return getLifecycle() != null;
    }

    /**
     * Apply a lifecycle to this content item.
     *
     * @param lifecycle The lifecycle
     */
    public void setLifecycle(final Lifecycle lifecycle) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting lifecycle to " + lifecycle + " on " + this);
        }

        Assert.exists(lifecycle, Lifecycle.class);

        LifecycleService.setLifecycle(this, lifecycle);
    }

    // XXX domlay What is the relation of setLifecycle(Lifecycle) and
    // publish(LifecycleDefinition ...)?  It doesn't seem coherent.
    /**
     * Remove the associated lifecycle.
     */
    public void removeLifecycle(final ContentItem itemToRemove) {
        // XXX Should this method be static?  Why does it take an
        // item?

        if (s_log.isDebugEnabled()) {
            s_log.debug("Removing lifecycle instance from item "
                        + itemToRemove);
        }

        LifecycleService.removeLifecycle(itemToRemove);
    }

    private ContentBundle getBundle() {
        final ACSObject parent = getParent();

        if (parent instanceof ContentBundle) {
            return (ContentBundle) parent;
        } else {
            return null;
        }
    }

    private void setBundle(final ContentBundle bundle) {
        setParent(bundle);
    }

    //
    //  Category stuff
    //
    /**
     * @return all categories to which this item belongs
     */
    public CategoryCollection getCategoryCollection() {
        final ContentBundle bundle = getBundle();

        if (bundle != null) {
            return bundle.getCategoryCollection();
        }

        return new CategorizedObject(this).getParents();
    }

    /**
     * Returns an iterator over the categories associated with this content item
     * which is associated with the given use context
     *
     * @param useContext the category use context
     *
     * @return An iterator over all Categories to which this item belongs
     */
    public Iterator getCategories(String useContext) {
        final ContentBundle bundle = getBundle();

        if (bundle != null) {
            return bundle.getCategories(useContext);
        }


        Category root = Category.getRootForObject(getContentSection(),
                                                  useContext);
        if (null == root) {
            s_log.warn("No root category for "
                       + getContentSection().getOID().toString()
                       + " with context " + useContext);
            return Collections.EMPTY_LIST.iterator();
        }

        CategoryCollection cats = root.getDescendants();
        cats.addEqualsFilter("childObjects.id", getID());

        Collection categories = new LinkedList();
        while (cats.next()) {
            categories.add(cats.getCategory());
        }
        return categories.iterator();
    }

    /**
     * Sets a category as the default/primary category for this item. Actual
     * default assignment is performed on the bundle if one exists.
     *
     * If this category is not already assigned to this item, then this method
     * also adds the category to the item.
     *
     * @param category The category to set as the default.
     *
     */
    public void setDefaultCategory(Category category) {
        ContentBundle bundle = getBundle();
        if (bundle != null) {
            bundle.setDefaultCategory(category);
            return;
        }
        CategorizedObject cObj = new CategorizedObject(this);
        cObj.setDefaultParentCategory(category);
        category.save();
        return;
    }

    /**
     * Adds a category to this content item (or its bundle if one exists)
     *
     * @param category The category to add this item to
     *
     */
    public void addCategory(Category category) {
        ContentBundle bundle = getBundle();
        if (bundle != null) {
            bundle.addCategory(category);
            return;
        }
        category.addChild(this);
        category.save();
        return;
    }

    /**
     * Removes a category from this content item (or its bundle if one exists)
     *
     * @param category The category to remove this item from
     *
     */
    public void removeCategory(Category category) {
        ContentBundle bundle = getBundle();
        if (bundle != null) {
            bundle.removeCategory(category);
            return;
        }
        category.removeChild(this);
        category.save();
        return;
    }

    //
    //  Versioning stuff
    //
    /**
     * Recursively copy this item, creating a clone. Reassign composite
     * associations from the copy to point to the copies of original items. This
     * method will not automatically transfer services (such as categories) to
     * the copy; the {@link #copyServicesFrom(ContentItem)} method should be
     * called to accomplish this. <p> NOTE: This method will also save the item
     * and all of its unpublished subitems.
     *
     * NOTE: This method should be final with the addition of makeCopy, but is
     * not just in case there are extensions in some PS code. The
     * 'non-finalness' of this method should be considered deprecated.
     *
     * @return the live version for this item
     *
     * @see #copyServicesFrom(ContentItem)
     */
    public ContentItem copy() {
        return copy(null, false);
    }

    public ContentItem copy(String lang) {
        return copy(null, false, lang);
    }

    /**
     * Recursively copy this item, creating a clone. Reassign composite
     * associations from the copy to point to the copies of original items. <p>
     * NOTE: This method will save the item and all of its unpublished subitems.
     *
     * @param newParent    The new parent item for this item
     * @param copyServices Copy services if true
     *
     * @return the new copy of the item
     *
     * @see #copyServicesFrom(ContentItem)
     */
    final public ContentItem copy(final ContentItem newParent,
                                  final boolean copyServices) {
        ContentItem newItem = makeCopy();
        if (newParent != null) {
            newItem.setParent(newParent);
        }
        if (copyServices) {
            newItem.copyServicesFrom(this);
        }
        return newItem;
    }

    final public ContentItem copy(final ContentItem newParent,
                                  final boolean copyServices,
                                  final String lang) {
        ContentItem newItem = makeCopy(lang);
        if (newParent != null) {
            newItem.setParent(newParent);
        }
        if (copyServices) {
            newItem.copyServicesFrom(this);
        }
        return newItem;
    }

    /**
     * Performs the actual mechanics of copying a content item. Non-final so
     * that subtypes can extend copying behavior.
     *
     * @return A new copy of the item
     */
    protected ContentItem makeCopy() {

        if (s_log.isDebugEnabled()) {
            s_log.debug("Copy taking place", new Throwable("trace"));
        }

        final ContentItem newItem = new ObjectCopier().copyItem(this);
        // Doesn't seem like I should have to do this, but what the hell
        newItem.setContentSection(getContentSection());
        newItem.save();

        return newItem;
    }

    /**
     * Variant of {@link ACSObject#makeCopy} which allows to pass the (further)
     * language of the copy.
     *
     * @param language
     *
     * @return
     */
    protected ContentItem makeCopy(String language) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Copy taking place", new Throwable("trace"));
        }

        LanguageAwareObjectCopier copier = new LanguageAwareObjectCopier(
                language);

        final ContentItem newItem = copier.copyItem(this);
        // Doesn't seem like I should have to do this, but what the hell
        newItem.setContentSection(getContentSection());
        newItem.save();

        return newItem;
    }

    /**
     * Transfer services, such as categories, from the passed-in item to this
     * item. This method should be called immediately after {@link ItemCopier#copy},
     * as follows: <blockquote><pre><code> Article newArticle = (Article)oldArticle.copyItem();
     * newArticle.copyServicesFrom(oldArticle);</code></pre></blockquote> <p>
     * WARNING: This method will most likely crash if you call it twice in a
     * row.
     *
     * @param source the
     * <code>ContentItem</code> whose services will be copied
     *
     * @see #copy()
     */
    public void copyServicesFrom(final ContentItem source) {
        ObjectCopier.copyServices(this, source);
    }

    /**
     * Recursively copy this item, creating a pending version. Reassign
     * composite associations from the pending version to point to the
     * pending/live versions of other items.
     *
     * NOTE: This method will also save the item and all of its unpublished
     * subitems.
     *
     * @param cycle the lifecycle to use. A null cycle implies that a live
     *              version should be created.
     *
     * @return the new pending version for this item
     */
    protected ContentItem createPendingVersion(final Lifecycle cycle) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Creating a pending version with lifecycle " + cycle);
        }

        return new VersionCopier(cycle).copyItem(this);
    }

    /**
     * Promote the specified pending version to live. Delete the old live
     * version, if any.
     *
     * @param pending The pending item to promote
     */
    public void promotePendingVersion(final ContentItem pending) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Promoting pending version " + pending + " to live");
        }

        assertDraft();

        final boolean isNewVersionLive = pending.isLiveVersion();

        final ContentItem live = getLiveVersion();

        if (live != null) {
            if (live.equals(pending) && isNewVersionLive) {
                return;
            }

            // Remove the previous live version, if any
            setLive(null);
        }

        if (!isNewVersionLive) {
            // Recursively set the pending version's tag to "live"
            // and update the master's live_version_id

            pending.setVersionRecursively(LIVE);
        }

        setLive(pending);

        ContentBundle draftBundle = getBundle();
        ContentBundle liveBundle = pending.getBundle();
        if ((draftBundle != null)
            && (liveBundle != null)
            && (!liveBundle.isLiveVersion())) {
            draftBundle.promotePendingVersion(liveBundle);
        }

        save();
    }

    /**
     * Recursively update the version attribute of the current content item to
     * the new value. Used by the lifecycle listener to promote a pending
     * version to live.
     *
     * @param version The new Version to set
     */
    protected void setVersionRecursively(final String version) {
        s_log.debug("Recursively updating the version attribute of the "
                    + "item");

        new VersionUpdater(version).updateItemVersion(this);
    }

    /**
     * Recursively copy this item, creating a live version. Reassign component
     * associations from the live version to point to the live versions of other
     * items.
     *
     * @return the live version for this item
     */
    public ContentItem createLiveVersion() {
        s_log.debug("Creating live version");

        ContentItem pending = createPendingVersion(null);

        promotePendingVersion(pending);

        return pending;
    }

    /**
     * Copy the specified property (attribute or association) from the specified
     * source item. This method almost completely overrides the metadata-driven
     * methods in
     * <code>ObjectCopier</code>. ...
     *
     * ObjectCopier will no longer call it, so existing implementations need to
     * update to the new signature
     *
     * @deprecated use {@link #copyProperty(CustomCopy, Property, ItemCopier)}
     * instead
     */
    protected final boolean copyProperty(final ContentItem source,
                                         final String attribute,
                                         final ItemCopier copier) {
        throw new UnsupportedOperationException(
                "use copyProperty(CustomCopy, Property, ItemCopier) for copying");
    }

    /**
     * Copy the specified property (attribute or association) from the specified
     * source item. This method almost completely overrides the metadata-driven
     * methods in
     * <code>ObjectCopier</code>. If the property in question is an association
     * to
     * <code>ContentItem</code>(s), this method should <b>only</b> call
     * <code>FooContentItem newChild = copier.copy(srcItem, this,
     * riginalChild, property);</code> An attempt to call any other method in
     * order to copy the child will most likely have disastrous consequences. In
     * fact, this copier method should generally be called for any DomainObject
     * copies, later making custom changes, unless the copying behavior itself
     * is different from the default (or the item should not be copied at all at
     * this point).
     *
     *
     * If a subclass of a class which implements CustomCopy overrides this
     * method, it should return
     * <code>super.copyProperty</code> for properties which do not need custom
     * behavior in order to indicate that it is not interested in handling the
     * property in any special way.
     *
     * As a hypothetical example (no longer reflected in Article itself), the {@link Article}
     * class extends
     * <code>ContentItem</code>. It defines an association to 0..n
     * {@link ImageAsset}. Unfortunately, the association has "order_n" and
     * "caption" link attributes, which cannot be copied automatically, since
     * the persistence system doesn't know enough about them. The following
     * sample code from the {@link Article} class ensures that images are copied
     * correctly:
     *
     * <blockquote><pre><code>
     * public boolean copyProperty(CustomCopy srcItem, Property property, ItemCopier copier) {
     *
     *  String attrName = property.getname()
     *   // We only care about copying images; all other properties should
     *   // be handled in a default manner
     *   if (!attrName.equals(IMAGES))
     *     return super.copyProperty(srcItem, property, copier);
     *
     *   // The source item is guaranteed to be of the correct type
     *   Article src = (Article)srcItem;
     *
     *   // Retrieve images from the source
     *   ImageAssetCollection srcImages = src.getImages();
     *
     *   // Copy each image using the passed-in copier
     *   while(srcImages.next()) {
     *     ImageAsset srcImage = srcImages.getImage();
     *
     *     // Images may be shared between items, and so they are not
     *     // composite. Thus, we are going to pass false to the object
     *     // copier in the second parameter
     *     ImageAsset newImage = (ImageAsset)copier.copy(srcItem, this, srcImage, property);
     *
     *     // Add the new image to the new item
     *     addImage(newImage, src.getCaption(srcImage));
     *   }
     *
     *   // Tell the automated copying service that we have handled this
     *   // property
     *   return true;
     * }
     * </code></pre></blockquote>
     *
     * Note that for top-level item associations,
     * <code>VersionCopier</code> will return
     * <code>null</code> since the actual associatons are only created at "go
     * live" time, so the ability to override behavior for top-level item
     * associations is somewhat limited. A common case for needing to override
     * copyProperty to handle these associations would be to auto-publish the
     * target of the association but still handle the association updating
     * normally. In this case, copyProperty would call publish() separately on
     * the associated object, and then return
     * <code>false</code> to indicate that the copier should continue to handle
     * the association normally.
     *
     * @param source   the source CustomCopy item
     * @param property the property to copy
     * @param copier   a temporary class that is able to copy a child item
     *                 correctly.
     *
     * @return true if the property was copied; false to indicate that regular
     *         metadata-driven methods should be used to copy the property.
     */
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        String attribute = property.getName();
        if (CHILDREN.equals(attribute)) {
            return true;
        }

        // Ignore live and pending versions.
        if (VERSIONS.equals(attribute)) {
            return true;
        }

        // Don't copy path denormalization.
        if (ANCESTORS.equals(attribute)) {
            return true;
        }

        //don't copy BasicAuditingTrail
        if (AUDITING.equals(attribute)) {
            return true;
        }

        if ("categories".equals(attribute)) {
            return true;
        }

        // If live Bundle already exists, recategorize.
        // jensp 2012: Behavior changed. The ContentBundle will also be republished.
        if (PARENT.equals(attribute)) {
            ACSObject parent = ((ContentItem) source).getParent();
            if (parent != null && copier.getCopyType()
                                  == ItemCopier.VERSION_COPY) {
                if (parent instanceof ContentBundle) {

                    final ContentBundle bundle = (ContentBundle) parent;
                    final ContentBundle oldLiveBundle =
                                        (ContentBundle) bundle.getPublicVersion();
                    //jensp 2012-03-07 Changes to the ContentBundle were not
                    //published because the ContentBundle was not republished.
                    //Moved the next lines out of the if below to enable 
                    //republishing of the ContentBundle
                    final ContentBundle liveBundle = (ContentBundle) bundle.createPendingVersion(null);
                    /*
                     * if (liveBundle == null) { } else { Set liveCatSet = new
                     * HashSet(); Set draftCatSet = new HashSet();
                     *
                     * CategoryCollection liveCategories =
                     * liveBundle.getCategoryCollection(); while
                     * (liveCategories.next()) {
                     * liveCatSet.add(liveCategories.getCategory()); }
                     * liveCategories.close();
                     *
                     * CategoryCollection draftCategories =
                     * bundle.getCategoryCollection(); while
                     * (draftCategories.next()) {
                     * draftCatSet.add(draftCategories.getCategory()); }
                     * draftCategories.close();
                     *
                     * Set catsToRemove = new HashSet(liveCatSet);
                     * catsToRemove.removeAll(draftCatSet); Set catsToAdd = new
                     * HashSet(draftCatSet); catsToAdd.removeAll(liveCatSet);
                     *
                     * Iterator removeIter = catsToRemove.iterator(); while
                     * (removeIter.hasNext()) { liveBundle.removeCategory(
                     * (Category) removeIter.next()); } Iterator addIter =
                     * catsToAdd.iterator(); while (addIter.hasNext()) {
                     * liveBundle.addCategory((Category) addIter.next()); }
                     *
                     * }
                     */
                    if (oldLiveBundle != null) {
                        final ItemCollection instances = oldLiveBundle.
                                getInstances();
                        while (instances.next()) {
                            liveBundle.addInstance(
                                    instances.getContentItem());
                        }
                    }

                    setBundle(liveBundle);
                    return true;
                } else if (parent instanceof Folder) {
                    Folder folder = (Folder) parent;
                    Folder liveFolder = (Folder) folder.getLiveVersion();
                    if (liveFolder == null) {
                        liveFolder = (Folder) folder.createLiveVersion();
                    }
                    setParent(liveFolder);
                    return true;
                }
            }
        }

        /*final AssociationCopierLoader assocCopierLoader =
                                      AssociationCopierLoader.getInstanceOf();
        final AssociationCopier assocCopier = assocCopierLoader.
                getAssociationCopierFor(property, source);
        if (assocCopier != null) {
            return assocCopier.copyProperty(source, property, copier);
        }*/
        
        if (source instanceof ContentItem) {
            final ContentItem sourceItem = (ContentItem) source;
            final Object value = sourceItem.get(property.getName());
            if (value instanceof DataCollection) {
                final DataCollection collection = (DataCollection) value;
                while(collection.next()) {                    
                    DomainObject obj = DomainObjectFactory.newInstance(collection.getDataObject());
                    if (obj instanceof ContentItem) {
                        final ContentItem item = (ContentItem) obj;
                        collection.close();
                        return item.copyReverseProperty(source, 
                                                        this, 
                                                        property,                                                       
                                                        copier);
                    }
                }                
            }
        }

        return false;
    }
          
    public boolean copyReverseProperty(final CustomCopy source,
                                       final ContentItem liveItem,
                                       final Property property,
                                       final ItemCopier copier) {
        return false;
    }

    /**
     * Copy services from the source item. This method is the analogue of the {@link #copyProperty}
     * method above. The object copier will call this method whenever an item
     * has been successfully published, in order to transfer services such as
     * categorization or permissions to the live version. <p> This method is
     * requied to return false to signal the object copier to transfer default
     * services from the source item; or true in order to abort further
     * processing of services.
     *
     * @return true to tell the object copier to stop copying services for this
     *         item, false otherwise
     */
    public boolean copyServices(ContentItem srcItem) {
        return false;
    }

    //
    //  Multilingual content
    //
    /**
     * Language of the content item.
     *
     * @return ISO639 2-letter language code
     */
    public String getLanguage() {
        return (String) get(LANGUAGE);
    }

    /**
     * Set the language of the content item.
     *
     * @param language ISO639 2-letter language code
     */
    public void setLanguage(String language) {
        set(LANGUAGE, language);

        m_reporter.mutated("language");
    }

    /**
     * Get the locale for this content item.
     *
     * @return The locale of the item @post return != null
     */
    public com.arsdigita.globalization.Locale getLocale() {
        Locale locale = null;
        // SystemLocaleProvider slp = new SystemLocaleProvider();
        // slp.getLocale()

        try {
            locale = Locale.fromJavaLocale(new java.util.Locale(getLanguage(),
                                                                ""));
        } catch (GlobalizationException e) {
            s_log.warn("GlobalizationException thrown in getLocale()", e);
            throw new UncheckedWrapperException(e.getMessage());
        }

        Assert.exists(locale, Locale.class);

        return locale;
    }

    /**
     * Assert that this item is a draft version
     */
    public final void assertDraft() {
        Assert.isEqual(DRAFT, getVersion());
    }

    /**
     * Assert that this item is a pending version
     */
    public final void assertPending() {
        Assert.isEqual(PENDING, getVersion());
    }

    /**
     * Assert that this item is a live version
     */
    public final void assertLive() {
        Assert.isEqual(LIVE, getVersion());
    }

    //
    // Deprecated methods and classes
    //
    /**
     * Assert that this item is a top-level master object
     *
     * @deprecated with no replacement
     */
    public final void assertMaster() {
        Assert.isTrue(isMaster(), "Item " + getOID() + " is a top-level item");
    }

    //
    // Private utility methods and classes
    //
    /**
     * Caches a version of this item.
     */
    private class VersionCache {

        private ContentItem m_version;
        private boolean m_cached;

        VersionCache() {
            m_version = null;
            m_cached = false;
        }

        boolean isCached() {
            return m_cached;
        }

        ContentItem get() {
            Assert.isTrue(m_cached);

            return m_version;
        }

        ContentItem set(final ContentItem version) {
            m_version = version;
            m_cached = true;

            return m_version;
        }

        void clear() {
            m_version = null;
            m_cached = false;
        }
    }

    /**
     * Remove any Links pointing to this item before deletion. XXX This should
     * go away when one-way association targets can specify the equivalent of on
     * delete set null
     */
    @Override
    protected void beforeDelete() {
        super.beforeDelete();

        // remove Link associations to this
        DataCollection dc = SessionManager.getSession().retrieve(
                Link.BASE_DATA_OBJECT_TYPE);
        dc.addEqualsFilter(Link.TARGET_ITEM + "." + ACSObject.ID,
                           getID());
        while (dc.next()) {
            Link link = (Link) DomainObjectFactory.newInstance(
                    dc.getDataObject());
            link.setTargetItem(null);
        }

        // remove ContentGroup associations to this
        dc = SessionManager.getSession().retrieve(
                ContentGroupAssociation.BASE_DATA_OBJECT_TYPE);
        dc.addEqualsFilter(ContentGroupAssociation.CONTENT_ITEM + "."
                           + ACSObject.ID,
                           getID());
        while (dc.next()) {
            ContentGroupAssociation groupAssoc =
                                    (ContentGroupAssociation) DomainObjectFactory.
                    newInstance(dc.getDataObject());
            groupAssoc.setContentItem(null);
        }
    }

    /**
     * Overriding the Auditing interface in order to use the denormalized
     * information
     */
    /**
     * Gets the user who created the object. May be null.
     *
     * @return the user who created the object.
     */
    @Override
    public User getCreationUser() {
        return m_audit_trail.getCreationUser();
    }

    /**
     * Gets the creation date of the object.
     *
     * @return the creation date.
     */
    @Override
    public Date getCreationDate() {
        return m_audit_trail.getCreationDate();
    }

    /**
     * Gets the IP address associated with creating an object. May be null.
     *
     * @return the creation IP address.
     */
    @Override
    public String getCreationIP() {
        return m_audit_trail.getCreationIP();
    }

    /**
     * Gets the user who last modified the object. May be null.
     *
     * @return the last modifying user.
     */
    @Override
    public User getLastModifiedUser() {
        return m_audit_trail.getLastModifiedUser();
    }

    /**
     * Gets the last modified date.
     *
     * @return the last modified date.
     */
    @Override
    public Date getLastModifiedDate() {
        return m_audit_trail.getLastModifiedDate();
    }

    /**
     * Gets the last modified IP address. May be null.
     *
     * @return the IP address associated with the last modification.
     */
    @Override
    public String getLastModifiedIP() {
        return m_audit_trail.getLastModifiedIP();
    }

    /**
     * <p> Override this to explicit that your content items have extra XML to
     * generate. An overriding implementation should call the super method, and
     * append its generators to the list. Example: </p>
     * <pre>
     * {@code
     * @Override
     * public List<ExtraXMLGenerator> getExtraXMLGenerators() {
     *   final List<ExtraXMLGenerators> generators =
     *     super.getExtraXMLGenerators();
     *
     *   generators.add(new YourExtraXMLGenerator());
     *
     *   return generators;
     * }
     * }
     * </pre>
     *
     * @return A list of all extra XML Generators for this content item.
     */
    public List<ExtraXMLGenerator> getExtraXMLGenerators() {
        return new ArrayList<ExtraXMLGenerator>();
    }

    /**
     * <p> Override this method if your content items have extra XML for list
     * views. You may return the same XML generators as in
     * {@link #getExtraXMLGenerators()}. But beware: The page state passed to
     * generators returned by this method will may be null. </p>
     *
     * @return A list of all extra XML Generators for lists views of this
     *         content item.
     */
    public List<ExtraXMLGenerator> getExtraListXMLGenerators() {
        return new ArrayList<ExtraXMLGenerator>();
    }
}
