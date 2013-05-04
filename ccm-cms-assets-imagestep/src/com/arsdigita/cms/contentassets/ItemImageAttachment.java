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
 */
package com.arsdigita.cms.contentassets;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.CustomCopy;
import com.arsdigita.cms.ItemCopier;
import com.arsdigita.cms.ReusableImageAsset;
import com.arsdigita.cms.contenttypes.Link;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
// import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import org.apache.log4j.Logger;

/**
 * @version $Revision: #3 $ $Date: 2004/04/08 $
 * @version $Id: $
 **/
public class ItemImageAttachment extends ACSObject implements CustomCopy {

    /** PDL property name for contact details */
    public static final String IMAGE = "image";
    public static final String ITEM = "item";
    public static final String USE_CONTEXT = "useContext";
    public static final String CAPTION = "caption";
    public static final String DESCRIPTION = "description";
    public static final String TITLE = "title";
    public static final String IMAGE_ATTACHMENTS = "imageAttachments";
    public static final String ITEM_ATTACHMENTS = "itemAttachments";
    public static final String IMAGE_LINK = "imageLink";
    /** Data object type for this domain object */
    public static final String BASE_DATA_OBJECT_TYPE = "com.arsdigita.cms.contentassets.ItemImageAttachment";
    private static final Logger s_log = Logger.getLogger(ItemImageAttachment.class);

    private static final ItemImageAttachmentConfig 
                         s_config = ItemImageAttachmentConfig.instanceOf();

    private ItemImageAttachment() {
        this(BASE_DATA_OBJECT_TYPE);
    }

    public ItemImageAttachment(DataObject obj) {
        super(obj);
    }

    public ItemImageAttachment(String type) {
        super(type);
    }

    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    public ItemImageAttachment(ContentItem item, ReusableImageAsset image) {
        this();

        set(ITEM, item);
        set(IMAGE, image);
    }

    public static ItemImageAttachment retrieve(OID oid) {
        return (ItemImageAttachment) DomainObjectFactory.newInstance(oid);
    }

    public static ItemImageAttachmentConfig getConfig() {
        return s_config;
    }

    public ReusableImageAsset getImage() {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting image for " + getOID());
        }

        DataObject dobj = (DataObject) get(IMAGE);
        Assert.exists(dobj);

        return (ReusableImageAsset) DomainObjectFactory.newInstance(dobj);
    }

    public void setImage(ReusableImageAsset image) {
        Assert.exists(image, ReusableImageAsset.class);
        set(IMAGE, image);
    }

    public ContentItem getItem() {
        DataObject dobj = (DataObject) get(ITEM);
        Assert.exists(dobj);

        return (ContentItem) DomainObjectFactory.newInstance(dobj);
    }

    public void setItem(ContentItem item) {
        Assert.exists(item, ContentItem.class);
        set(ITEM, item);
    }

    /** Retrieves links for a content item */
    public static DataCollection getImageAttachments(ContentItem item) {
        Assert.exists(item, ContentItem.class);

        if (s_log.isDebugEnabled()) {
            s_log.debug("Getting attachments for " + item.getOID());
        }

        DataCollection attachments = SessionManager.getSession().retrieve(BASE_DATA_OBJECT_TYPE);
        attachments.addEqualsFilter(ITEM + ".id", item.getID());

        return attachments;
    }

    public void setUseContext(String useContext) {
        set(USE_CONTEXT, useContext);
    }

    public String getUseContext() {
        return (String) get(USE_CONTEXT);
    }

    public void setCaption(String caption) {
        set(CAPTION, caption);
    }

    public String getCaption() {
        return (String) get(CAPTION);
    }

    public void setTitle(String title) {
        set(TITLE, title);
    }

    public String getTitle() {
        return (String) get(TITLE);
    }

    public void setDescription(String description) {
        set(DESCRIPTION, description);
    }

    public String getDescription() {
        return (String) get(DESCRIPTION);
    }

    /**
     * Automatically publish an unpublished image
     */
    public boolean copyProperty(final CustomCopy source,
                                final Property property,
                                final ItemCopier copier) {
        String attribute = property.getName();
        if (ItemCopier.VERSION_COPY == copier.getCopyType()
                                       && IMAGE.equals(attribute)) {
            ItemImageAttachment attachment = (ItemImageAttachment) source;
            ReusableImageAsset image = attachment.getImage();

            ReusableImageAsset liveImage =
                               (ReusableImageAsset) image.getLiveVersion();

            if (null == liveImage) {
                liveImage = (ReusableImageAsset) image.createLiveVersion();
            }

            setImage(liveImage);
            return true;
        }

        return false;
    }

    // chris gilbert - optional link
    public Link getLink() {
        Link link = null;
        DataObject dobj = (DataObject) get(IMAGE_LINK);
        if (dobj != null) {

            link = (Link) DomainObjectFactory.newInstance(dobj);
        }
        return link;
    }

    public void setLink(Link link) {
        Assert.exists(link, Link.class);
        set(IMAGE_LINK, link);
    }

    public void removeLink() {
        // when we delete the link, the image still references it in DB
        // can't make it composite because then image is deleted if we delete
        // link. Have to set link to null first (I think)
        DomainObject link = DomainObjectFactory.newInstance((DataObject)get(IMAGE_LINK));
        set(IMAGE_LINK, null);
        save();
        link.delete();

    }
}
