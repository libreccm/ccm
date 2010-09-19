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
import com.arsdigita.cms.FileAsset;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.permissions.PermissionService;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.DataOperation;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;
import org.apache.log4j.Logger;

/**
 * Represents a FileAsset uploaded as one of N files attached to a content item
 *
 *
 * @author sseago@redhat.com
 * @version $Id: FileAttachment.java 1112 2006-04-18 14:02:10Z apevec $
 */
public class FileAttachment extends FileAsset {

    private static final Logger s_log = Logger.getLogger(FileAttachment.class);

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.contentassets.FileAttachment";

    public static final String FILE_OWNER        = "fileOwner";
    public static final String FILE_ATTACHMENTS  = "fileAttachments";
    public static final String FILE_ORDER        = "fileOrder";

    private static final FileAttachmentConfig s_config = new FileAttachmentConfig();
    
    static {
        s_config.load();
    }


    public FileAttachment() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public FileAttachment(OID oid) {
        super(oid);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and <code>ContentPage.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public FileAttachment(BigDecimal id) {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    /**
     * Constructor. Creates a new <code>DomainObject<code> instance to
     * encapsulate a given data object.
     *
     * @param dataObject The data object to encapsulate in the new domain
     * object.
     * @see com.arsdigita.persistence.Session#retrieve(String)
     **/
    public FileAttachment(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is
     * initialized with a new <code>DataObject</code> with an
     * ObjectType specified by the string typeName.
     *
     * @param typeName The name of the ObjectType of the
     * new instance.
     *
     * @see com.arsdigita.persistence.Session#create(String)
     * @see com.arsdigita.persistence.DataObject
     * @see com.arsdigita.persistence.metadata.ObjectType
     **/
    public FileAttachment(String type) {
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
     * @return the owning ContentItem
     **/
    public ContentItem getFileOwner() {
        return (ContentItem) DomainObjectFactory.newInstance
            ((DataObject) get(FILE_OWNER));
    }

    public void setFileOwner(ContentItem fileOwner) {
        Assert.exists(fileOwner);
        this.setMaster(fileOwner);
        setAssociation(FILE_OWNER, fileOwner);

        setLanguage( fileOwner.getLanguage() );
    }

    public void setFileOrder(Integer order) {
        set(FILE_ORDER, order);
    }

    public void setFileOrder(int order) {
        set(FILE_ORDER, new Integer(order));
    }

    public Integer getFileOrder() {
        return (Integer) get(FILE_ORDER);
    }

    /** Retrieves attachments for a content item */
    public static DataCollection getAttachments(ContentItem item) {
        s_log.debug("Getting attachments for a content item: " + item);
        Session session = SessionManager.getSession();
        DataCollection files = session.retrieve(BASE_DATA_OBJECT_TYPE);
        files.addEqualsFilter(FILE_OWNER + ".id", item.getID());
        files.addEqualsFilter(IS_DELETED, "0");
        files.addOrder(FILE_ORDER);
        return files;
    }

    /**
     * This method is only used for setting initial sort keys for attachments
     * which exist without them. This is called by swapKeys instead of
     * attempting to swap if the key found is null. This implementation sorts all
     * FileAttachments owned by this FileAttachment's "fileOwner" by file name.
     */
    public void alphabetize() {
        int sortKey = maxOrder();
        DataCollection attachments = getAttachments(getFileOwner());
        attachments.addOrder(NAME);
        while (attachments.next()) {
            sortKey++;
            FileAttachment fa = new FileAttachment(attachments.getDataObject());
            fa.setFileOrder(sortKey);
            fa.save();
        }
    }

    /**
     * Returns the max sort key value for all FileAttachments with the
     * same file owner as this file.
     *
     * @return the max sort key value
     */
    public int maxOrder() {
        ContentItem fileOwner = getFileOwner();
        if (fileOwner == null) {
            return 0;
        }
        int returnOrder = 0;
        DataQuery query = SessionManager.getSession().retrieveQuery
            ("com.arsdigita.cms.contentassets.maxFileAttachmentOrderForItem");
        query.setParameter("ownerID", fileOwner.getID());
        if (query.next()) {
            Integer fileOrder = ((Integer)query.get("fileOrder"));
            query.close();
            if (fileOrder != null) {
                returnOrder = fileOrder.intValue();
            }
        }
        return returnOrder;
    }


    /**
     * Swaps this <code>FileAttachment</code> with the next one,
     * according to the fileOrder.
     */
    public void swapWithNext() {
        swapKeys(true);
    }

    /**
     * Swaps this <code>FileAttachment</code> with the previous one,
     * according to the fileOrder.
     */
    public void swapWithPrevious() {
        swapKeys(false);
    }

    /**
     *  This swaps the sort keys.
     *  @param swapNext This indicates if we are swapping with the next
     *                  or the previous
     */
    public void swapKeys(boolean swapNext) {

        Assert.isTrue(!isNew(), "swapKeys() cannot be called on an " +
                          "object that is new");

        ContentItem fileOwner = getFileOwner();
        Assert.exists(fileOwner, "fileOwner must be set for swapKeys() to work");

        Integer currentKey = getFileOrder();
        // if the current item is not already ordered, alphabetize
        // instead the first time. This is instead of having to deal
        // with an upgrade script.
        if (currentKey == null) {
            alphabetize();
            return;
        }

        // find out the other key which we're about to swap with
        // if (next) otherkey = min(file_order) where file_order > currentKey;
        // if (previous) otherkey = max(file_order) where file_order < currentKey;

        DataQuery query = SessionManager.getSession()
            .retrieveQuery("com.arsdigita.cms.contentassets.getAdjacentSortKey");
        query.setParameter("ownerID", fileOwner.getID());
        query.setParameter("fileOrder", currentKey);
        query.setParameter("param", swapNext ? "next" : "prev");

        Integer otherKey = null;

        if (query.next()) {
            otherKey = (Integer) query.get("otherKey");
            query.close();
        } else {
            // the other key not found, something went wrong.
            return;
        }

        DataOperation operation = SessionManager.getSession()
            .retrieveDataOperation(
             "com.arsdigita.cms.contentassets.swapFileAttachmentOrder");
        operation.setParameter("ownerID", fileOwner.getID());
        operation.setParameter("fileOrder", currentKey);
        operation.setParameter("nextFileOrder", otherKey);
        operation.execute();

    }


    protected void beforeSave() {
        super.beforeSave();
        if (getFileOrder() == null) {
            setFileOrder(maxOrder()+1);
        }
    }


    protected void afterSave() {
        super.afterSave();

        ContentItem fileOwner = getFileOwner();

        if( null != fileOwner )
            PermissionService.setContext( this, fileOwner );
    }

    public static FileAttachmentConfig getConfig() {
        return s_config;
    }

}
