/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the Open Software License v2.1
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://rhea.redhat.com/licenses/osl2.1.html.
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.mimetypes.MimeType;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

import org.apache.log4j.Logger;

/**
 * This content type represents a Link content type for linking
 * ContentItems and external links.
 *
 * @version $Revision: #4 $ $Date: 2004/03/30 $
 * @author Scott Seago (sseago@redhat.com)
 */
public class RelatedLink extends Link  {

    private static final Logger s_log = Logger.getLogger(RelatedLink.class);

    /** PDL properties */
    public static final String LINK_LIST_NAME = "linkListName";
    public static final String RESOURCE_SIZE = "resourceSize";
    public static final String RESOURCE_TYPE = "resourceType";
    public static final String LINK_OWNER = "linkOwner";
    public static final String RELATED_LINKS  = "links";

    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE
        = "com.arsdigita.cms.contentassets.RelatedLink";

    /**
     * Default constructor. This creates a new RelatedLink.
     */
    public RelatedLink() {
        this( BASE_DATA_OBJECT_TYPE );
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <code>id</code> and <code>RelatedLink.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>
     */
    public RelatedLink( BigDecimal id )
        throws DataObjectNotFoundException {
        this( new OID( BASE_DATA_OBJECT_TYPE, id ) );
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <code>oid</code>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>
     */
    public RelatedLink( OID oid )
        throws DataObjectNotFoundException {
        super( oid );
    }

    /**
     * Constructor.  Retrieves or creates a content item using the
     * <code>DataObject</code> argument.
     *
     * @param obj The <code>DataObject</code> with which to create or
     * load a content item
     */
    public RelatedLink( DataObject obj ) {
        super( obj );
    }

    /**
     * Constructor.  Creates a new <code>RelatedLink</code> using the given data
     * object type.
     *
     * @param type The <code>String</code> data object type of the
     * item to create
     */
    public RelatedLink( String type ) {
        super( type );
    }

    /** get the name of the named link list. */
    public String getLinkListName(){
      return (String) get(LINK_LIST_NAME);
    }

    /** Set the name of the named link list. */
    public void setLinkListName(String name){
      set(LINK_LIST_NAME , name);
    }

    /**
     * Get the MimeType of the target resource to which this link points.
     * 
     * @return <code>MimeType</code> of target resource.
     */
    public MimeType getResourceType(){
      DataObject obj = (DataObject) get ( RESOURCE_TYPE );
      if(obj != null){
        return new MimeType(obj);
      }
      return null;
    }

    /**
     * Set the MimeType of the target resource to which this link points.
     * 
     * @param type , <code>MimeType</code> of target resource.
     */
    public void setResourceType(MimeType type){
      setAssociation(RESOURCE_TYPE , type);
    }

    /** get the size of the target resource. */
    public String getResourceSize(){
      return (String) get(RESOURCE_SIZE);
    }

    /** Set the size of the target resource. */
    public void setResourceSize(String size){
      set(RESOURCE_SIZE , size);
    }

    /** 
     * Sets the ContentItem that this link belongs to
     * 
     * @param item The link Owner
     */
    public void setLinkOwner(ContentItem item) {
        Assert.exists(item, ContentItem.class);
        s_log.debug("Setting linkOwner to" + item.getName());
        setAssociation(LINK_OWNER, item);
    }

    /** 
     * Gets the ContentItem that this link belongs to
     * 
     * @return The link Owner
     */
    public ContentItem getLinkOwner() {
        DataObject dobj = (DataObject) get(LINK_OWNER);
        if (dobj == null) {
            return null;
        } else {
            return (ContentItem)DomainObjectFactory.newInstance(dobj);
        }

    }

    /**
     * Retrieves related links for a given content item
     *
     * @param item The item to return links for
     */
    public static DataCollection getRelatedLinks(ContentItem item, String name) {
        s_log.debug("Getting related links for a content item");
        Session session = SessionManager.getSession();
        DataCollection links = session.retrieve(BASE_DATA_OBJECT_TYPE);
        links.addEqualsFilter(LINK_OWNER + ".id", item.getID());
        links.addEqualsFilter(LINK_LIST_NAME, name);
        links.addOrder(ORDER);
        return links;
    }

    /** 
     * Returns a DataCollection of related links which refer to the
     * given item. 
     *
     * @param item The target Item to return links for
     *
     * @return DataCollection of referring RelatedLinks
     */
    public static DataCollection getReferringRelatedLinks(ContentItem item) {
        Session session = SessionManager.getSession();
        DataCollection links = session.retrieve(BASE_DATA_OBJECT_TYPE);
        Filter filter = links.addInSubqueryFilter("id", "com.arsdigita.cms.contentassets.getReferringRelatedLinks");
        filter.set("itemID", item.getID());
        
        return links;
    }


    /**
     * Swaps this <code>RelatedLink</code> with the next one,
     * according to the linkOrder
     */
    public void swapWithNext() {
        swapWithNext("com.arsdigita.cms.contentassets.allRelatedLinkOrderForItem",
                     "com.arsdigita.cms.contentassets.swapRelatedLinkWithNextInGroup");
    }

    /**
     * Swaps this <code>RelatedLink</code> with the previous one,
     * according to the linkOrder
     */
    public void swapWithPrevious() {
        swapWithPrevious("com.arsdigita.cms.contentassets.allRelatedLinkOrderForItem",
                         "com.arsdigita.cms.contentassets.swapRelatedLinkWithNextInGroup");
    }

    /**
     * Given a dataquery name, returns the (possibly filtered)
     * DataQuery for use in swapKeys. This implementation filters
     * on the <code>linkOwner</code> property, so that only
     * RelatedLinks which belong to the same <code>ContentItem</code>
     * will be swapped.
     *
     * @param queryName name of the DataQuery to use
     * @return the DataQuery
     */
    protected DataQuery getSwapQuery(String queryName) {
        DataQuery query = super.getSwapQuery(queryName);
        query.setParameter("ownerID", getLinkOwner().getID());
        return query;
    }

    /**
     * Given a data operation name, returns the 
     * DataOperation for use in swapKeys. This implementation sets the
     * "ownerID" parameter, in addition to what is set by
     * super.getSwapOperation
     *
     * @param operationName the Name of the DataOperation to use
     *
     * @return the DataOperation used to swap the sort keys.
     */
    protected DataOperation getSwapOperation(String operationName) {
        DataOperation operation = super.getSwapOperation(operationName);
        operation.setParameter("ownerID", getLinkOwner().getID());
        return operation;
    }



    /**
     * This method is only used for setting initial sort keys for
     * links which exist without them. This is called by swapKeys
     * instead of attempting to swap if the key found is
     * null. This implementation sorts all RelatedLinks owned by this
     * RelatedLink's "linkOwner" by title.
     */
    protected void alphabetize() {
        Session session = SessionManager.getSession();
        DataCollection links = session.retrieve(BASE_DATA_OBJECT_TYPE);
        links.addEqualsFilter(LINK_OWNER + ".id", getLinkOwner().getID());
        links.addEqualsFilter(LINK_LIST_NAME, getLinkListName());
        links.addOrder(TITLE);
        int sortKey = 0;
        while (links.next()) {
            sortKey++;
            Link link = new RelatedLink(links.getDataObject());
            link.setOrder(sortKey);
            link.save();
        }
        
    }

    /**
     * Returns the max sort key value for all RelatedLinks with the
     * same link owner as this link.
     *
     * @return the max sort key value
     */
    public int maxOrder() {
        ContentItem linkOwner = getLinkOwner();
        if (linkOwner == null) {
            return 0;
        }
        int returnOrder = 0;
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.cms.contentassets.allRelatedLinkOrderForItem");
        query.setParameter("ownerID", getLinkOwner().getID());
        query.setParameter("linkListName", getLinkListName());
        query.addOrder("linkOrder DESC");
        if (query.next()) {
            Integer linkOrder = ((Integer)query.get("linkOrder"));
            query.close();
            if (linkOrder != null) {
                returnOrder = linkOrder.intValue();
            }
        }
        return returnOrder;
        
    }

    public void beforeSave() {
        super.beforeSave();
        if (getOrder() == null) {
            setOrder(maxOrder()+1);
        }
    }
}
