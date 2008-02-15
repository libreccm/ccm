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

import com.arsdigita.auditing.BasicAuditTrail;
import com.arsdigita.cms.lifecycle.Lifecycle;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainCollectionIterator;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.kernel.permissions.PrivilegeDescriptor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

/**
 * This class represents folders for which to organize items in a tree
 * hierarchy.
 *
 * Folders will only ever exist as draft or live versions. There
 * should never be any folders that are pending. The pending versions
 * of ordinary content items are stored in the live version of
 * folders.
 *
 * Folders cannot have their own lifecycles. The methods to get or set
 * lifecycles are no-ops.
 *
 * You should never call {@link #publish} or {@link #unpublish} on a
 * folder; at present, these methods only log a warning when they are
 * called. In the future, these warnings may be turned into actual
 * errors.
 *
 * @author Jack Chung
 * @author Michael Pih
 * @author David Lutterkort
 * @version $Id: Folder.java 1317 2006-09-07 08:47:15Z sskracic $
 */
public class Folder extends ContentItem {
    public static final String versionId =
        "$Id: Folder.java 1317 2006-09-07 08:47:15Z sskracic $" +
        "$Author: sskracic $" +
        "$DateTime: 2004/08/17 23:15:09 $";

    private static final Logger s_log = Logger.getLogger(Folder.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.Folder";

    public static final String INDEX = "index";
    public static final String HOME_FOLDER = "homeFolder";
    public static final String HOME_SECTION = "homeSection";

    private static final String ITEMS_QUERY = "com.arsdigita.cms.ItemsInFolder";
    private static final String PRIMARY_INSTANCES_QUERY =
        "com.arsdigita.cms.PrimaryInstancesInFolder";
    private static final String ITEM_QUERY = "com.arsdigita.cms.ItemInFolder";
    private static final String FOLDER_QUERY = "com.arsdigita.cms.FolderInFolder";
    private static final String LABEL = "label";
    private static final String NAME = "name";
    private final static String ITEM = "item";
    private boolean m_wasNew;

    protected static final String ITEMS = "items";

    /**
     * Default constructor. This creates a new folder.
     */
    public Folder() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <cod>oid</code>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     */
    public Folder(final OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <code>id</code> and
     * <code>Folder.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>
     */
    public Folder(final BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Folder(final DataObject obj) {
        super(obj);
    }

    public Folder(final String type) {
        super(type);
    }

    protected ContentItem makeCopy() {
        final Folder newItem = (Folder) super.makeCopy();
        DomainCollectionIterator items = new DomainCollectionIterator(getItems());
        newItem.copyItemsToFolder(items);
        return newItem;
    }

    public void copyItemsToFolder(final Iterator items) {
        while (items.hasNext()) {
            ContentItem item = (ContentItem) items.next();
            item.copy(this, true);
        }
    }
    /**
     * @return the base PDL object type for this item. Child classes
     * should override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Deletes the folder.
     *
     * @throws IllegalStateException if the folder is not empty.
     */
    public void delete() throws IllegalStateException {
        s_log.debug("Deleting folder");

        if (!isEmpty()) {
            throw new IllegalStateException
                ("Attempt to delete non-empty folder " + getOID() + "; " +
                 "only empty folders can be deleted");
        }

        super.delete();
    }

    protected void beforeDelete() {
        DataCollection maps = SessionManager.getSession().retrieve
            (UserHomeFolderMap.BASE_DATA_OBJECT_TYPE);
        maps.addEqualsFilter(HOME_FOLDER + "." + ID,getID());
        while (maps.next()) {
            maps.getDataObject().delete();
        }

        super.beforeDelete();
    }

    protected void beforeSave() {
        m_wasNew = isNew();

        super.beforeSave();
    }

    protected void afterSave() {
        super.afterSave();

        if (m_wasNew) {
            // If the parent of this folder is null, and this folder
            // is the root folder of its content section, then the
            // content section is set as the context of this folder,
            // so that permissions cascade correctly.

            // NB: Because folder is saved before the creation of the
            // content in the CMS Initializer, content section is null
            // and this isn't run!
            final ACSObject parent = getParent();

            if (parent == null) {
                // Verify that the content section is not null and
                // this is its root folder.

                final ContentSection section = getContentSection();

                if (section != null &&
                    (this.equals(section.getRootFolder()) ||
                     this.equals(section.getTemplatesFolder()))) {
                    PermissionService.setContext(this, section);
                }
            }
        }

        // All folder versions should inherit their permissions from the
        // working version
        final ContentItem workingVersion = getWorkingVersion();

        if (!this.equals(workingVersion)) {
            PermissionService.setContext(this, workingVersion);
        }
    }

    /**
     * Fetches the child items of this folder. The returned collection
     * provides methods to filter by various criteria, for example by
     * name or by whether items are folders or not.
     *
     * @param bSort whether to sort the collection by isFolder and ID
     * @return child items of this folder
     */
    public ItemCollection getItems(boolean bSort) {

        DataQueryDataCollectionAdapter adapter = new DataQueryDataCollectionAdapter(ITEMS_QUERY, ITEM);
        adapter.setParameter(PARENT, getID());
        Assert.unequal(PENDING, getVersion());
        adapter.setParameter(VERSION, getVersion());

        return new ItemCollection(adapter, bSort);
    }

    /**
     * Fetches the child items of this folder. The returned collection
     * provides methods to filter by various criteria, for example by
     * name or by whether items are folders or not.  The items returned
     * by this method are sorted by isFolder and ID
     *
     * @return child items of this folder, sorted by isFolder and ID
     */
    public ItemCollection getItems() {
        return getItems(true);
    }


    /**
     * Returns collection of primary language instances for bundles in
     * this folder.
     */
    public ItemCollection getPrimaryInstances() {
        final DataQuery query = SessionManager.getSession().retrieveQuery
            (PRIMARY_INSTANCES_QUERY);
        query.setParameter(PARENT, getID());

        Assert.unequal(PENDING, getVersion());

        query.setParameter(VERSION, getVersion());

        return new ItemCollection(query);
    }

    /**
     * Returns a child content item in this folder (which could itself
     * be a folder) with the specified name.
     *
     * @param name The name of the item
     * @param isFolder If true, only return a subfolder. Otherwise,
     * return any subitem
     * @return The item with the given name, or null if no such item
     * exists in the folder
     */
    public ContentItem getItem(final String name,
                               final boolean isFolder) {

        DataQuery query;
        if (isFolder) {
            query = SessionManager.getSession().retrieveQuery(FOLDER_QUERY);
        } else {
            query = SessionManager.getSession().retrieveQuery(ITEM_QUERY);
        }

        query.setParameter(PARENT, getID());
        query.setParameter(VERSION, getVersion());
        query.setParameter(NAME, name);

        DataCollection items = new DataQueryDataCollectionAdapter(query, ITEM);

        if (items.next()) {
            DataObject dataObj = items.getDataObject();
            ContentItem result = (ContentItem)DomainObjectFactory
                .newInstance(dataObj);

            if (items.next()) {
                s_log.warn("Item in folder has a duplicate name; one " +
                           "is " + result + " and one is " +
                           (ContentItem)DomainObjectFactory
                           .newInstance(items.getDataObject()));
                throw new IllegalStateException();
            }

            return result;
        } else {
            return null;
        }
    }

    public void addItem(final ContentBundle item) {
        // TODO: saving the item here is a little weird, but
        // the only way we can guarantee that it ever gets saved.
        item.setParent(this);

        item.save();
    }

    public String getDisplayName() {
        final String result = getLabel();

        if (result == null) {
            return super.getDisplayName();
        } else {
            return result;
        }
    }


    /**
     * Fetches the label of the folder.
     */
    public final String getLabel() {
        return (String) get(LABEL);
    }

    /**
     * Set the label of this folder.
     */
    public final void setLabel(final String value) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Setting label to " + value);
        }

        set(LABEL, value);
    }

    /**
     * Set the version of the folder. An attempt to set the version to
     * pending will result in the folder's version being set to live. We will
     * never have any pending versions of folders, only live or draft.
     *
     * Pending versions of items are stored in the live version of a
     * folder.
     */
    protected void setVersion(String version) {
        if (ContentItem.PENDING.equals(version)) {
            version = ContentItem.LIVE;
        }

        super.setVersion(version);
    }

    //
    // Publish/unpublish stuff
    //

    public void unpublish() {
        if (s_log.isInfoEnabled()) {
            s_log.info("Unpublishing folder " + this);
        }

        super.unpublish();
    }

    public ContentItem publish(final LifecycleDefinition cycleDef,
                               final Date startDate) {
        if (s_log.isInfoEnabled()) {
            s_log.info("Publishing folder " + this);
        }

        return super.publish(cycleDef, startDate);
    }

    //
    // Lifecycle stuff
    //

    /**
     * Always returns <code>null</code>, as folders do not have lifecycles.
     *
     * @return a <code>Lifecycle</code> value
     */
    public Lifecycle getLifecycle() {
        return null;
    }

    /**
     * Does not do anything, as folders do not have lifecycles.
     */
    public void setLifecycle(final Lifecycle cycle) {
        return;
    }

    /**
     * Does not do anything, as folders do not have lifecycles.
     */
    public void removeLifecycle() {
        return;
    }

    protected void addPendingVersion(final ContentItem version) {
        if (Assert.isEnabled()) {
            assertDraft();
        }
        version.setVersion(LIVE);
        //version.save();
        setLiveVersion(version);
    }

    public void removePendingVersion(final ContentItem version) {
        Assert.unequal(PENDING, version.getVersion());

        return;
    }

    //
    // Index item
    //

    /**
     * Get the (special) index item for the folder. The index item is
     * what carries all the user-editable attributes of the
     * folder. The index item is what should be published when a index
     * page for a folder is desired.
     *
     * The index item is an ordinary item in every respect, i.e., it
     * is part of the collection returned by <code>getItems()</code>,
     * you cannot delete a folder if it still has an index item etc.
     */
    public ContentBundle getIndexItem() {
        // BECAUSE INDEX ITEM MIGHT NOT BE UPDATED FOR PUBLISHED
        // FOLDERS, CHECK IF DRAFT VERSIONS OF LIVE FOLDERS HAVE INDEX
        // ITEM.

        if (getVersion().compareTo(ContentItem.LIVE) == 0) {
            final ContentItem indexItem =
                ((Folder) getWorkingVersion()).getIndexItem();

            if (indexItem == null) {
                return null;
            }

            return (ContentBundle) indexItem.getLiveVersion();
        }

        final DataObject index = (DataObject) get(INDEX);

        if (index == null) {
            return null;
        }

        try {
            return (ContentBundle) DomainObjectFactory.newInstance(index);
        } catch (PersistenceException pe) {
            throw new UncheckedWrapperException(pe);
        }
    }

    /**
     * Sets the index item. This also adds the item to the folder.
     *
     * @param item The index item with the folder's user-editable
     * attributes
     */
    public final void setIndexItem(final ContentBundle item) {
        setAssociation(INDEX, item);

        addItem(item); // XXX Why is this needed?
    }

    /**
     * Removes the index item.
     */
    public final void removeIndexItem() {
        setAssociation(INDEX, null);
    }

    /**
     * Returns <code>true</code> if the folder is empty.
     *
     * @return <code>true</code> if the folder is empty
     */
    public boolean isEmpty() {
        final Session session = SessionManager.getSession();

        final DataQuery query = session.retrieveQuery
            ("com.arsdigita.cms.folderNotEmpty");
        query.setParameter("id", getID());

        final boolean result = !query.next();

        query.close();

        return result;
    }

    /**
     * Returns <code>true</code> if the folder contains at least one
     * folder, <code>false</code> if the folder does not contain any
     * folders, but is either empty or contains only ordinary items.
     *
     * @return <code>true</code> if the folder contains other folders.
     */
    public boolean containsFolders() {
        final Session session = SessionManager.getSession();

        final DataQuery query = session.retrieveQuery
            ("com.arsdigita.cms.folderHasNoSubFolders");
        query.setParameter("id", getID());

        final boolean result = !query.next();

        query.close();

        return result;
    }

    /**
     * Copy the specified property (attribute or association) from the specified
     * source folder. This method almost completely overrides the
     * metadata-driven methods in <code>ObjectCopier</code>. If the property in
     * question is an association to <code>ContentItem</code>(s), this method
     * should <em>only</em> call <code>FooContentItem newChild =
     * copier.copyItem(originalChild)</code>.  An attempt to call any other
     * method in order to copy the child will most likely have disastrous
     * consequences.
     *
     * If a child class overrides this method, it should return
     * <code>super.copyProperty</code> in order to indicate that it is
     * not interested in handling the property in any special way.
     *
     * @param srcItem the source item
     * @param property the property to copy
     * @param copier the ItemCopier
     * @return true if the property was copied, false to indicate that
     * regular metadata-driven methods should be used to copy the
     * property
     */
    public boolean copyProperty(final CustomCopy srcItem,
                                final Property property,
                                final ItemCopier copier) {
        // Ignore the items association.
        String attrName = property.getName();
        if (ITEMS.equals(attrName) || INDEX.equals(attrName)) {
            return true;
        }

        return super.copyProperty(srcItem, property, copier);
    }

    /**
     * Folders aren't explicitly p2fs'd
     */
    protected boolean canPublishToFS() {
        return false;
    }

    /**
     * A collection of items that can be filtered to return only folders or
     * only nonfolders.
     */
    public static class ItemCollection
        extends com.arsdigita.cms.ItemCollection {
        private final static String IS_FOLDER = "isFolder";
        private final static String HAS_CHILDREN = "hasChildren";
        private final static String ITEM = "item";
        private final static String HAS_LIVE_VERSION = "hasLiveVersion";
        private final static String TYPE_LABEL = "type.label";
        private final static String AUDIT_TRAIL="item.auditing";

        private DataQuery m_query;


        /**
         * Constructor
         * @param adapter an adapter constructed using the query name rather than a
         * DataQuery object. This constructor must be used if there is any
         * intention to permission filter the results as only a DataQueryDataCollectionAdapter
         * constructed using query name has the bug fix to allow permission filtering
         *
         * @param bSort whether to sort the collection by isFolder and ID
         */
        public ItemCollection (DataQueryDataCollectionAdapter adapter, boolean bSort) {
            super(adapter);
            doAlias(adapter);
            init(adapter, bSort);
        }

        public ItemCollection (DataQueryDataCollectionAdapter adapter) {
            this(adapter, true);
        }

        /**
         * Constructor
         * @param query the Data Query to use to retrieve the collection
         * @param bSort whether to sort the collection by isFolder and ID
         */
        //ideally, we wouldn't sort the collection by default and only provide
        //one constructor.  But, that would break the existing API
        public ItemCollection(DataQuery query, boolean bSort) {
            super(new DataQueryDataCollectionAdapter(doAlias(query), ITEM));

            init(query, bSort);
        }

        /**
         * Convenience Constructor that always sorts the collection
         * by isFolder and ID
         * @param query the Data Query to use to retrieve the collection
         */
        public ItemCollection(DataQuery query) {
            super(new DataQueryDataCollectionAdapter(doAlias(query), ITEM));

            init(query, true);
        }

        private void init(DataQuery query, boolean bSort) {
            m_query = query;

            if (bSort) {
                m_query.addOrder("isFolder desc");

                addOrder("id desc");
            }
        }

        private static DataQuery doAlias(final DataQuery query) {
            query.alias("isFolder", "isFolder");

            return query;
        }

        /**
         * Sets the range of the dataquery. This is used by the
         * paginator.
         *
         * @param beginIndex The start index
         * @param endIndex The end index
         */
        public void setRange(final Integer beginIndex,
                             final Integer endIndex) {
            m_dataQuery.setRange(beginIndex, endIndex);
        }

        public String getDisplayName() {
            return (String) get(DISPLAY_NAME);
        }

        /**
         * For performance reaons, override superclass methods and
         * try to get the audit info without instantiating a content item.
         * We know this can help because the getPrimaryInstances
         * query retrieves the audit info directly
         */
        public Date getCreationDate() {
            DataObject dobj = (DataObject) get(AUDIT_TRAIL);
            if (dobj != null){
                BasicAuditTrail audit = new BasicAuditTrail(dobj);
                return audit.getCreationDate();
            } else {
                return super.getCreationDate();
            }
        }

        public Date getLastModifiedDate() {
            DataObject dobj = (DataObject) get(AUDIT_TRAIL);
            if (dobj != null){
                BasicAuditTrail audit = new BasicAuditTrail(dobj);
                return audit.getLastModifiedDate();
            } else {
                return super.getLastModifiedDate();
            }
        }

        /**
         * Return the pretty name of the content type of the current item. If
         * the current item is a folder, the string <tt>Folder</tt> is
         * returned, otherwise the label of the item's content type.
         *
         * @return the pretty name of the content type of the current item.
         */
        public String getTypeLabel() {
            if (isFolder()) {
                return "Folder";
            } else {
                return  (String) get(TYPE_LABEL);
            }
        }

        /**
         * Filter the collection by whether items are folders or not.
         *
         * @param v <code>true</code> if the data query should only list folders,
         * <code>false</code> if the data query should only list non-folder
         * items.
         *
         */
        public void addFolderFilter(final boolean v) {
            m_query.addEqualsFilter(IS_FOLDER, v ? "1" : "0");
        }

        /**
         * Return <code>true</code> if the current item in the collection is a
         * folder.
         *
         * @return <code>true</code> if the current item in the collection is a
         * folder.
         */
        public boolean isFolder() {
            Boolean result = (Boolean) m_query.get(IS_FOLDER);
            return result.booleanValue();
        }

        public boolean hasChildren() {
            Boolean result = (Boolean) m_query.get(HAS_CHILDREN);
            return result.booleanValue();
        }

        public boolean isLive() {
            String version = (String) get(ContentItem.VERSION);
            if (ContentItem.LIVE.equals(version) ) {
                return true;
            }
            Boolean hasLive = (Boolean) m_query.get(HAS_LIVE_VERSION);
            return hasLive.booleanValue();
        }

        /**
         * Only used on CollectionS returned by getPrimaryInstance()
         */
        public BigDecimal getBundleID() {
            if (isFolder()) {
                return null;
            } else {
                return (BigDecimal) m_query.get("bundleID");
            }
        }
    }

    /**
     * Called by <code>VersionCopier</code> to determine whether to
     * publish associated items when an item goes live. This will only
     * have an effect for non-component associations where the item is
     * not yet published. Override default for <code>Folder</code>s
     * since they don't have their own lifecycles and a folder must be
     * published when an item in it goes live.
     *
     * @return whether to publish this item
     */
    public boolean autoPublishIfAssociated() {
        return true;
    }

    public static void setUserHomeFolder(User user,Folder folder) {
        UserHomeFolderMap map = UserHomeFolderMap.findOrCreateUserHomeFolderMap(user,folder.getContentSection());
        map.setHomeFolder(folder);
        map.save();
    }

    public static Folder getUserHomeFolder(User user,ContentSection section) {
        Folder folder = null;
        UserHomeFolderMap map = UserHomeFolderMap.findUserHomeFolderMap(user,section);
        if ( map != null ) {
            folder = map.getHomeFolder();
            if ( folder != null ) {
                CMSContext context = CMS.getContext();
                SecurityManager sm;
                if (context.hasSecurityManager()) {
                    sm = CMS.getContext().getSecurityManager();
                } else {
                    sm = new SecurityManager(section);
                }
                if ( !sm.canAccess(user,SecurityConstants.PREVIEW_PAGES,folder) ) {
                    folder = null;
                }
            }
        }
        return folder;
    }
}
