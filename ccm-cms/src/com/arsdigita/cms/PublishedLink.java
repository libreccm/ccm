/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.cms.publishToFile.QueueManager;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.domain.DomainServiceInterfaceExposer;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.ObjectType;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class represents an association between a pending or live
 * ContentItem (or one of its components) and a separate top-level
 * Contenttem.
 *
 */
class PublishedLink extends DomainObject {

    private static final Logger s_log = Logger.getLogger(PublishedLink.class);

    static final String SOURCE_MASTER_ITEM = "pending";

    // replace below later with:
    //public static final String PENDING_OID = "pendingOID"
    static final String PENDING_SOURCE = "pendingSource";
    static final String PROPERTY_NAME = "propertyName";
    static final String DRAFT_TARGET = "draftTarget";

    static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.PublishedLink";

    protected String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Default constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * <code>ObjectType</code> of "PublishedLink".
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     */
    protected PublishedLink() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Creates a PublishedLink object with the specified data object.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(DataObject)
     */
    protected PublishedLink(DataObject data) {
        super(data);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     *
     * @see com.arsdigita.domain.DomainObject#DomainObject(OID)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.OID
     */
    protected PublishedLink(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Creates new PublishedLink unless one already exists for the
     * specified source, target, and property
     *
     * @param sourceMasterItem the top-level pending or live <code>ContentItem</code> which
     * this <code>PublishedLink</code> is a component of.
     * @param linkSource the immediate source of this
     * <code>PublishedLink</code>, a component of the sourceMasterItem
     * (or the item itself)
     * @param propertyName the Property name for this <code>PublishedLink</code>
     * @param linkTarget  the top-level draft <code>ContentItem</code> which
     * is the target of this <code>PublishedLink</code> .
     *
     * @return the newly-created PublishedLink, or the existing one if
     * one already exists for these items.
     */
    static PublishedLink create(ContentItem sourceMasterItem,
                                DomainObject linkSource,
                                String propertyName,
                                ContentItem linkTarget) {
        OID oid = new OID(BASE_DATA_OBJECT_TYPE);
        oid.set(SOURCE_MASTER_ITEM, DomainServiceInterfaceExposer.getDataObject(sourceMasterItem));
        oid.set(PROPERTY_NAME, propertyName);
        oid.set(DRAFT_TARGET, DomainServiceInterfaceExposer.getDataObject(linkTarget));

        // this will need to be  refactored if we switch to OID link sourcess
        if (linkSource instanceof ACSObject) {
            oid.set(PENDING_SOURCE, DomainServiceInterfaceExposer.getDataObject(linkSource));
        } else {
            Assert.fail("Cannot set PublishedLink source " + linkSource + "; it is not an " +
                        "ACSObject");
        }

        PublishedLink link = null;
        try {
            link  = new PublishedLink(oid);
        } catch (DataObjectNotFoundException e) {
            link = new PublishedLink(SessionManager.getSession().create(oid));
        }

        return link;
    }

    /**
     * Returns the top-level pending or live <code>ContentItem</code> which
     * this <code>PublishedLink</code> is a component of.
     *
     * @return the top-level pending or live <code>ContentItem</code> which
     * this <code>PublishedLink</code> is a component of.
     *
     */
    ContentItem getSourceMasterItem() {
        final DataObject item = (DataObject) get(SOURCE_MASTER_ITEM);

        return item == null ? null :
            (ContentItem) DomainObjectFactory.newInstance
            ((DataObject) item);
    }

    /**
     * Returns the immediate source of this <code>PublishedLink</code>
     *
     * @return the immediate source of this <code>PublishedLink</code>
     *
     */
    DomainObject getLinkSource() {
        // this will need to be  refactored if we switch to OIDs
        final DataObject item = (DataObject) get(PENDING_SOURCE);

        return item == null ? null :
            DomainObjectFactory.newInstance
            ((DataObject) item);
    }

    /**
     * Returns the Property name for this <code>PublishedLink</code>
     *
     * @return the Property name for this <code>PublishedLink</code>
     *
     */
    String getPropertyName() {
        return  (String) get(PROPERTY_NAME);
    }

    /**
     * Returns the top-level draft <code>ContentItem</code> which
     * is the target of this <code>PublishedLink</code> .
     *
     * @return the top-level draft <code>ContentItem</code> which
     * is the target of this <code>PublishedLink</code> .
     *
     */
    ContentItem getLinkTarget() {
        final DataObject item = (DataObject) get(DRAFT_TARGET);

        return item == null ? null :
            (ContentItem) DomainObjectFactory.newInstance
            ((DataObject) item);
    }

    /**
     * Updates live associations based on PublishedLinks which either
     * point <em>from</em> or <em>to</em> the given
     * <code>ContentItem</code>. If both ends of the link are now
     * live, the live-live association will be updated. In addition,
     * for links <em>to</em> this new live item, the source of the
     * link is refreshed via <code>QueueManager.queueRepublish</code>.
     *
     * @param item The item which was just published
     */
    public static void updateLiveLinks(ContentItem item) {
        Session session = SessionManager.getSession();
        Set itemsToRefresh = new HashSet();

        ContentItem draftItem = item.getDraftVersion();
        DataCollection linksToItem = session.retrieve(BASE_DATA_OBJECT_TYPE);
        linksToItem.addEqualsFilter(DRAFT_TARGET + ".id", draftItem.getID());
        linksToItem.addEqualsFilter(SOURCE_MASTER_ITEM + "." + ContentItem.VERSION, ContentItem.LIVE);
        updateLiveLinksFromCollection(linksToItem, itemsToRefresh);
        linksToItem.close();

        DataCollection linksFromItem = session.retrieve(BASE_DATA_OBJECT_TYPE);
        linksFromItem.addEqualsFilter(SOURCE_MASTER_ITEM + ".id", item.getID());
        updateLiveLinksFromCollection(linksFromItem, null);
        linksFromItem.close();

        Iterator refreshIterator = itemsToRefresh.iterator();
        while (refreshIterator.hasNext()) {
            OID oid = (OID) refreshIterator.next();
            ContentItem refreshItem = (ContentItem) DomainObjectFactory.newInstance(oid);
            if (refreshItem.canPublishToFS()) {
                QueueManager.queueRepublish(refreshItem);
            }
        }
    }

    private static void updateLiveLinksFromCollection(DataCollection coll, Set itemsToRefresh) {
        while (coll.next()) {
            // will change w/ OID references
            DataObject master = (DataObject) coll.get(SOURCE_MASTER_ITEM);
            DataObject src = (DataObject) coll.get(PENDING_SOURCE);
            src.specialize((String)src.get(ACSObject.OBJECT_TYPE));
            String propertyName = (String) coll.get(PROPERTY_NAME);
            Assert.exists(src, DataObject.class);
            Assert.exists(propertyName, String.class);

            DataObject target = null;
            DataObject draftTarget  = (DataObject) coll.get(DRAFT_TARGET);
            DataAssociationCursor targetVersions =
                ((DataAssociation) draftTarget.get(ContentItem.VERSIONS)).cursor();
            targetVersions.addEqualsFilter(ContentItem.VERSION, ContentItem.LIVE);
            if (targetVersions.next()) {
                target = targetVersions.getDataObject();
            }
            targetVersions.close();
            if (target != null) {
                ObjectType ot = src.getObjectType();
                Property prop = ot.getProperty(propertyName);
                Assert.exists(prop, propertyName + " for type " + ot.getQualifiedName() + ", ID: " + src.get("id"));
                if (prop.isCollection()) {
                    DataAssociation da = (DataAssociation) src.get(propertyName);
                    da.add(target);
                } else {
                    src.set(propertyName,target);
                }
                if (itemsToRefresh != null && master != null) {
                    itemsToRefresh.add(master.getOID());
                }
            }
        }

    }

    /**
     * At unpublish time, for links <em>to</em> this unpublished item,
     * the source of the link is refreshed via
     * <code>QueueManager.queueRepublish</code>.
     *
     * @param item The item which was just published
     */
    public static void refreshOnUnpublish(ContentItem item) {
        Session session = SessionManager.getSession();
        Set itemsToRefresh = new HashSet();

        DataCollection linksToItem = session.retrieve(BASE_DATA_OBJECT_TYPE);
        linksToItem.addEqualsFilter(DRAFT_TARGET + ".id", item.getID());
        linksToItem.addEqualsFilter(SOURCE_MASTER_ITEM + "." + ContentItem.VERSION, ContentItem.LIVE);
        while (linksToItem.next()) {
            // will change w/ OID references
            DataObject master = (DataObject) linksToItem.get(SOURCE_MASTER_ITEM);
            if (master != null) {
                itemsToRefresh.add(master.getOID());
            }
        }
        Iterator refreshIterator = itemsToRefresh.iterator();
        while (refreshIterator.hasNext()) {
            OID oid = (OID) refreshIterator.next();
            ContentItem refreshItem = (ContentItem) DomainObjectFactory.newInstance(oid);
            if (refreshItem.canPublishToFS()) {
                QueueManager.queueRepublish(refreshItem);
            }
        }
    }

}
