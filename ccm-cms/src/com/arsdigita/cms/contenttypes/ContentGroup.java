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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemCollection;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.DataQueryDataCollectionAdapter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;


/**
 * This is an identifiable group of content that is used by the index page.  
 *
 * @author Randy Graebner (randyg@redhat.com)
 *
 * @version $Id: ContentGroup.java 1967 2009-08-29 21:05:51Z pboy $
 */
public class ContentGroup extends ContentItem {

    public static final String BASE_DATA_OBJECT_TYPE = 
        "com.arsdigita.cms.contenttypes.ContentGroup";

    public static final String ITEM_ASSOCIATIONS = "itemAssociations";

    public ContentGroup() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public ContentGroup(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and <code>getBaseDataObjectType()</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public ContentGroup(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public ContentGroup(DataObject obj) {
        super(obj);
    }

    public ContentGroup(String type) {
        super(type);
    }

    /**
     * @return the base PDL object type for this item. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     *
     *  This needs to be changed to deal with ContentGroupAssociation 
     *  And it needs convenience methods to sorting, etc
     *
     */

    /**
     *  This adds an item to the group.  This actually saves the item.
     *  If the item has already been added then this is a no-op
     */
    public void addContentItem(ContentItem item) {
        ContentGroupAssociation assoc = getItemAssociation(item);
        if (assoc == null) {
            assoc = new ContentGroupAssociation(item, this);
            assoc.setName(item.getName() + getID());
            assoc.setSortKey(new Integer(getMaxSortKey() + 1));
	    assoc.setMaster(getMaster());
            assoc.save();
            add(ITEM_ASSOCIATIONS, assoc);
        }
    }

    /**
     *  This removes an item from the group.  If the item was not
     *  associated with this group then this is a no-op.  This actually
     *  deletes the item so there is no need to call save.
     */
    public void removeContentItem(ContentItem item) {
        ContentGroupAssociation assoc = getItemAssociation(item);
        if (assoc != null) {
             // Don't remove the ContentGroupAssociation from 
             // ITEM_ASSOCIATIONS.  This tries to update
             // cf_content_group_item_map.group_id to null, which
             // violates the not null constraint on that column.
             // Just delete the association.
             // remove(ITEM_ASSOCIATIONS, assoc);
            assoc.delete();
        }
    }

    /**
     *  This removes all items from the group.  This actually deletes
     *  objects so you do not need to save after calling this
     */
    public void clearContentItems() {
        ItemCollection collection = getContentGroupAssociations();
        while (collection.next()) {
            collection.getContentItem().delete();
        }
    }

    /**
     * Get the DataCollection containing all of the 
     * ContentGroupAssociations i this ContentGroup.  Used
     * in the methods below.
     *
     * @param addOrder If true, order the collection by the sortKey
     **/
    private DataCollection getAssociationDataCollection(boolean addOrder) {
        DataCollection collection = SessionManager.getSession()
            .retrieve(ContentGroupAssociation.BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter(ContentGroupAssociation.CONTENT_GROUP + 
                                   "." + ContentGroupAssociation.ID, getID());
         // We add an explicit alias for the isDeleted column, so that it doesn't
         // get re-aliased if we wrap this in a DataQueryDataCollectionAdapter
         collection.alias(ContentGroupAssociation.IS_DELETED,
                          ContentGroupAssociation.IS_DELETED);
        // this filters to make sure the association is not deleted
        collection.addEqualsFilter(ContentGroupAssociation.IS_DELETED, 
                                   Boolean.FALSE);
        if (addOrder) {
            collection.addOrder(ContentGroupAssociation.SORT_KEY);
        }
        return collection;
    }

    /**
     *  this returns the ContentGroupAssociation if one exists
     *  or null if none currently exists (or it has been deleted)
     */
    // This could probably be made more efficient with some sort of
    // caching
    private ContentGroupAssociation getItemAssociation(ContentItem item) {
        DataCollection collection = getAssociationDataCollection(false);
        collection.addEqualsFilter(ContentGroupAssociation.CONTENT_ITEM +
                                   "." + ContentItem.ID, item.getID());
        if (collection.next()) {
            DataObject object = collection.getDataObject();
            collection.close();
            return new ContentGroupAssociation(object);
        }
        return null;
    }


    /**
     *  This returns the related items, in order.
     */
    public ItemCollection getContentItems() {
        DataCollection collection = getAssociationDataCollection(true);
        // this filters to make sure the item itself is not deleted
        collection.addEqualsFilter(ContentGroupAssociation.CONTENT_ITEM + 
                                   "." + ContentGroupAssociation.IS_DELETED, 
                                   Boolean.FALSE);
        DataCollection query = new DataQueryDataCollectionAdapter
            (collection, ContentGroupAssociation.CONTENT_ITEM);

        return new ItemCollection(query);
    }

    /**
     *  This returns the related items, in order.
     */
    public ItemCollection getContentGroupAssociations() {
        DataCollection collection = getAssociationDataCollection(true);

        return new ItemCollection(collection);
    }


    /**
     *  This swaps the sort key with the next item in the list or does
     *  nothing if no such item exists. For instance, if the association is
     *  5th before the call, it will be 6th and the
     *  6th item will become the 5th item, if there is a 6th item.
     *  If the content item is not associated
     *  with this group then this is a no-op.
     */
    public void swapWithNext(ContentItem item) {
        ContentGroupAssociation assoc = getItemAssociation(item);
        if (assoc != null) {
            assoc.swapWithNext();
        }
    }

    /**
     *  This swaps the sort key with the next item in the list or does
     *  nothing if no such item exists. For instance, if the association is
     *  6th before the call, it will be 5th and the
     *  5th item will become the 6th item.  If this item is first in the
     *  list then this is a no-op.  If the content item is not associated
     *  with this group then this is a no-op.
     */
    public void swapWithPrevious(ContentItem item) {
        ContentGroupAssociation assoc = getItemAssociation(item);
        if (assoc != null) {
            assoc.swapWithPrevious();
        }
    }

    /**
     * Get the highest sortKey for associations in this ContentGroup.
     * If there are no associations, returns -1.
     **/
    protected int getMaxSortKey() {
        Integer sortKey = null;
        DataQuery query = SessionManager.getSession().
         // We need to include the deleted ContentGroupAssociations when querying
         // for the max sortKey, or else we risk creating duplicate sortKeys, which
         // would screw up reordering
             retrieveQuery("com.arsdigita.cms.contenttypes.maxItemSortKeyInGroupWithDeleted");
        query.setParameter("groupID", getID());
        try {
            if (query.next()) {
                sortKey = (Integer) query.get("sortKey");
            }
        } finally {
            query.close();
        }
        if (sortKey != null) {
            return sortKey.intValue();
        } else {
            return -1;
        }
    }

    //  hack to fix auto-publishing problems, removed for now (until we deal with the issue for Camden, etc.)
    //public boolean publishOnAssociatedObjectPublish() {
    //return false;
    //}

}
