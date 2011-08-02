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
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;


/**
 * This is an object that places an object in a group.  This has to
 * be an object and not an association with link attribute because of
 * the way that CMS versioning handles associations
 *
 * This class should be removed once
 * versioning and persistence support versioned link attributes (sortKey)
 *
 * @author Randy Graebner (randyg@redhat.com)
 *
 * @version $Id: ContentGroupAssociation.java 2090 2010-04-17 08:04:14Z pboy $
 */
public class ContentGroupAssociation extends ContentItem {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.contenttypes.ContentGroupAssociation";

    public static final String CONTENT_ITEM = "contentItem";
    public static final String CONTENT_GROUP = "contentGroup";
    public static final String SORT_KEY = "sortKey";

    public ContentGroupAssociation() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public ContentGroupAssociation(String type) {
        super(type);
    }

    /**
     *  This creates a new association and sets the item and group to the
     *  passed in values.  Both values must be non-null.
     */
    public ContentGroupAssociation(ContentItem item, ContentGroup group) {
        this(BASE_DATA_OBJECT_TYPE);
        setContentItem(item);
        setContentGroup(group);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public ContentGroupAssociation(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public ContentGroupAssociation(DataObject obj) {
        super(obj);
    }


    /**
     * @return the base PDL object type for this item. Child classes should
     *  override this method to return the correct value
     */
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }


    /**
     *  Sets the content item for this association
     */
    public void setContentItem(ContentItem item) {
        //Assert.isTrue(item != null, "The ConentItem must not be null");
        set(CONTENT_ITEM, item);
    }

    /**
     *  This Sets the ContentGroup for this association.
     *  @pre group != null
     */
    protected void setContentGroup(ContentGroup group) {
        Assert.isTrue(group != null, "The ContentGroup must not be null");
        set(CONTENT_GROUP, group);
    }


    /**
     *  This returns the content item for this association
     */
    protected ContentItem getContentItem() {
        return (ContentItem)DomainObjectFactory.newInstance
            ((DataObject)get(CONTENT_ITEM));
    }


    /**
     *  This returns the content group for this association
     */
    protected ContentGroup getContentGroup() {
        return (ContentGroup) DomainObjectFactory.newInstance
            ((DataObject) get(CONTENT_GROUP));
    }

    /**
     * Get the sortKey for this association
     **/
    protected Integer getSortKey() {
        return (Integer) get(SORT_KEY);
    }

    /**
     * Set the sortKey for this association
     **/
    protected void setSortKey(Integer key) {
        set(SORT_KEY, key);
    }

    /**
     *  This swaps the sort key with the next item in the list or does
     *  nothing if no such item exists. For instance, if the association is
     *  5th before the call, it will be 6th and the
     *  6th item will become the 5th item, if there is a 6th item.
     */
    protected void swapWithNext() {
        swapKeys(true, "com.arsdigita.cms.contenttypes.minItemSortKeyInGroup");
    }


    /**
     *  This swaps the sort key with the next item in the list or does
     *  nothing if no such item exists. For instance, if the association is
     *  6th before the call, it will be 5th and the
     *  5th item will become the 6th item.  If this item is first in the
     *  list then this is a no-op
     */
    protected void swapWithPrevious() {
        swapKeys(false, "com.arsdigita.cms.contenttypes.maxItemSortKeyInGroup");
    }

    /**
     *  This swaps the keys.
     *  @param swapNext This indicates if we are swapping with the next
     *                  or the previous
     *  @param queryName This is used to find the key with which to swap
     */
    private void swapKeys(boolean swapNext, String queryName) {

        String methodName = null;
        if (swapNext) {
            methodName = "swapWithNext";
        } else {
            methodName = "swapWithPrevious";
        }

        Assert.isTrue(!isNew(), methodName + " cannot be called on an " +
                          "object that is new");

        Integer currentKey = (Integer)get(SORT_KEY);
        Assert.isTrue(currentKey != null, methodName + " cannot be " +
                          "called on an object that is not currently in the " +
                          "list");

        int key = currentKey.intValue();

        DataQuery query = SessionManager.getSession().retrieveQuery
            (queryName);
        query.setParameter("groupID", getContentGroup().getID());

        int otherKey = key;

        if (swapNext) {
            otherKey = key + 1;
            query.addFilter(query.getFilterFactory()
                            .greaterThan("sortKey", currentKey, true));
        } else {
            otherKey = key - 1;
            query.addFilter(query.getFilterFactory()
                            .lessThan("sortKey", currentKey, true));
        }

        if (query.next()) {
            otherKey = ((Integer)query.get("sortKey")).intValue();
            query.close();
        }

        DataOperation operation = SessionManager.getSession()
            .retrieveDataOperation
            ("com.arsdigita.cms.contenttypes.swapItemWithNextInGroup");
        operation.setParameter("sortKey", new Integer(key));
        operation.setParameter("nextSortKey", new Integer(otherKey));
        operation.setParameter("groupID", getContentGroup().getID());
        operation.execute();

    }

    /**
     * Make sure the item has a sortKey.
     **/
    protected void beforeSave() {
        super.beforeSave();

        if (getSortKey() == null) {
            setSortKey(new Integer(0));
        }
    }

    //  hack to fix auto-publishing problems, removed for now (until we deal with the issue for Camden, etc.)
    //public boolean publishOnAssociatedObjectPublish() {
    //return false;
    //}

}
