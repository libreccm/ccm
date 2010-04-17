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

import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.Property;
import com.arsdigita.util.Assert;
import com.arsdigita.versioning.VersionedACSObject;

import java.math.BigDecimal;

/**
 * This class associates an Article and an Image with a particular
 * caption.
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #17 $ $Date: 2004/08/17 $
 * @version $Id: ArticleImageAssociation.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ArticleImageAssociation extends ContentItem {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.ArticleImageAssociation";

    protected static final String ARTICLE = "captionArticle";
    protected static final String IMAGE = "imageAsset";
    protected static final String CAPTION = "caption";
    protected static final String ARTICLE_ID = "articleId";
    protected static final String IMAGE_ID = "imageId";


    /**
     * Default constructor.
     **/
    public ArticleImageAssociation() {
        super(BASE_DATA_OBJECT_TYPE);
    }

    /**
     * Constructor retrieves the contained <code>DataObject</code> from the
     * persistent storage mechanism with an <code>OID</code> specified by
     * <i>oid</i>.
     *
     * @param oid The <code>OID</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public ArticleImageAssociation(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }
    public ArticleImageAssociation(DataObject obj) {
        super(obj);
    }


    public ArticleImageAssociation(String type) {
        super(type);
    }

    /**
     *  This returns the association object that is specified by
     *  the passed in IDs or it returns null if no such association exists
     */
    public static ArticleImageAssociation retrieveAssociation
        (BigDecimal articleID, BigDecimal imageID) {
        DataCollection collection = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter(ARTICLE_ID, articleID);
        collection.addEqualsFilter(IMAGE_ID, imageID);
        // no deleted associations, please
        collection.addEqualsFilter(VersionedACSObject.IS_DELETED, new BigDecimal(0));
        if (collection.next()) {
            ArticleImageAssociation association =
                new ArticleImageAssociation(collection.getDataObject());
            collection.close();
            return association;
        }
        return null;
    }

    /**
     *  This returns true if the image is associated with at least one article.
     */
    public static boolean imageHasDirectAssociation
        (BigDecimal imageID) {
        DataCollection collection = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter(IMAGE_ID, imageID);
        // no deleted associations, please
        collection.addEqualsFilter(VersionedACSObject.IS_DELETED, new BigDecimal(0));
        if (collection.next()) {
            collection.close();
            return true;
        }
        return false;
    }

    /**
     *  This returns true if the image is associated with at least one article.
     */

    public static boolean imageHasAssociation
        (BigDecimal imageID) {
        try {
            ImageAsset asset = (ImageAsset) DomainObjectFactory.newInstance
                (new OID(ImageAsset.BASE_DATA_OBJECT_TYPE, imageID));
            return asset == null ? false : imageHasAssociation(asset);
        } catch (DataObjectNotFoundException e) {
            // can't find asset, return false
            return false;
        }
    }
    /**
     *  This returns true if the image is associated with at least one article, checking both liveand draft versions
     */
    public static boolean imageHasAssociation
        (ImageAsset image) {
        Assert.exists(image);
        boolean returnValue = imageHasDirectAssociation(image.getID());
        if (!returnValue) {
            if (!image.getVersion().equals(ContentItem.DRAFT)) {
                ContentItem item = image.getWorkingVersion();
                if (item != null)
                    returnValue = imageHasDirectAssociation(item.getID());
            }
        }
        if (!returnValue) {
            if (!image.getVersion().equals(ContentItem.PENDING)) {
                ItemCollection pendingVersions = image.getPendingVersions();
                while(pendingVersions.next()) {
                    ContentItem item = pendingVersions.getContentItem();
                    returnValue = returnValue || imageHasDirectAssociation(item.getID());
                }
            }
        }
        if (!returnValue) {
            if (!image.getVersion().equals(ContentItem.LIVE)) {
                ContentItem item = image.getLiveVersion();
                if (item != null)
                    returnValue = imageHasDirectAssociation(item.getID());
            }
        }

        DataCollection collection = SessionManager.getSession().retrieve
            (BASE_DATA_OBJECT_TYPE);
        collection.addEqualsFilter(IMAGE_ID, image.getID());
        // no deleted associations, please
        collection.addEqualsFilter(VersionedACSObject.IS_DELETED, new BigDecimal(0));
        if (collection.next()) {
            collection.close();
            return true;
        }
        return false;
    }

    public BigDecimal getArticleID() {
        return (BigDecimal) get(ARTICLE_ID);
    }

    public Article getArticle() {
        DataCollection col = SessionManager.getSession().retrieve(ARTICLE);
        if (col.next()) {
            Article art = new Article(col.getDataObject());
            col.close();
            return art;
        }
        return null;
    }

    public void setArticle(Article article) {
        setAssociation(ARTICLE, article);
    }

    public BigDecimal getImageID() {
        return (BigDecimal) get(IMAGE_ID);
    }

    public ImageAsset getImage() {
        return (ImageAsset) DomainObjectFactory.
            newInstance((DataObject) get(IMAGE));
    }

    public void setImage(ImageAsset image) {
        setAssociation(IMAGE, image);
    }

    public String getCaption() {
        return (String) get(CAPTION);
    }

    public void setCaption(String caption) {
        set(CAPTION, caption);
    }

    /**
     * Auto-publish the associated ReusableImageAssociation if it is not yet live
     *
     * @param source the source CustomCopy item
     * @param property the property to copy
     * @param copier a temporary class that is able to copy a child item
     *   correctly.
     * @return true if the property was copied; false to indicate
     *   that regular metadata-driven methods should be used
     *   to copy the property.
     */
    public boolean copyProperty(final CustomCopy source,
				final Property property,
				final ItemCopier copier) {
	String attribute = property.getName();
	// don't copy these attributes. they're controlled by explicit associations
	if (IMAGE_ID.equals(attribute) || ARTICLE_ID.equals(attribute)) {
	    return true;
	}

        if (copier.getCopyType() == ItemCopier.VERSION_COPY
                && IMAGE.equals(attribute)) {
	    ImageAsset image = ((ArticleImageAssociation)source).getImage();
	    if (image != null) {
		ImageAsset liveImage = (ImageAsset) image.getLiveVersion();
		if (liveImage == null) {
		    liveImage = (ImageAsset) image.createLiveVersion();
		}
	    }
	    // This method only makes sure that the ReusableImageAsset
            // is auto-published. It still returns  false so that the
            // copier can generate PublishedLink objects appropriately.
	    return false;
        }
	return super.copyProperty(source, property, copier);
    }

}
