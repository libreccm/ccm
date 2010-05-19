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
package com.arsdigita.cms.basetypes;

import com.arsdigita.cms.ImageAsset;
import com.arsdigita.cms.ImageAssetCollection;
import com.arsdigita.cms.TextPage;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.persistence.DataAssociation;
import com.arsdigita.persistence.DataAssociationCursor;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;

import java.math.BigDecimal;

/**
 * A class that represents an Article
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Revision: #28 $ $Date: 2004/08/17 $
 * @version $Id: Article.java 2070 2010-01-28 08:47:41Z pboy $
 */
public class Article extends TextPage {

    public static final String BASE_DATA_OBJECT_TYPE =
        "com.arsdigita.cms.basetypes.Article";

    protected static final String IMAGES = "imageAssets";

    private static final String IMAGE_CAPTIONS = "imageCaptions";

    private static org.apache.log4j.Logger s_log =
        org.apache.log4j.Logger.getLogger(Article.class);

    /**
     * Default constructor. This creates a new article.
     **/
    public Article() {
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
    public Article(OID oid) throws DataObjectNotFoundException {
        super(oid);
    }

    public Article(String type) {
	super(type);
    }

    /**
     * Constructor. The contained <code>DataObject</code> is retrieved
     * from the persistent storage mechanism with an <code>OID</code>
     * specified by <i>id</i> and
     * <code>Article.BASE_DATA_OBJECT_TYPE</code>.
     *
     * @param id The <code>id</code> for the retrieved
     * <code>DataObject</code>.
     **/
    public Article(BigDecimal id) throws DataObjectNotFoundException {
        this(new OID(BASE_DATA_OBJECT_TYPE, id));
    }

    public Article(DataObject obj) {
        super(obj);
    }

    /**
     * @return the base PDL object type for this item. Child classes should
     *  override this method to return the correct value
     */
    @Override
    public String getBaseDataObjectType() {
        return BASE_DATA_OBJECT_TYPE;
    }

    /**
     * Add an image to this article.  If the image is already added
     * to the article, the caption will be updated.
     *
     * @param image the image to add
     * @param caption the caption for the image
     * @return true if image is added and false if caption is updated
     */
    public boolean addImage(ImageAsset image, String caption) {
	ImageAssetCollection col = getImages();
	col.addEqualsFilter(ArticleImageAssociation.IMAGE_ID,image.getID());
	boolean toReturn = false;
	ArticleImageAssociation assn = null;
	if (col.next()) {
	    assn = (ArticleImageAssociation)(col.getDomainObject());
            col.close();
	} else {
	    assn = new ArticleImageAssociation();
            String name = this.getName();
            Assert.exists(name, String.class);
            String imgName = image.getName();
            Assert.exists(imgName, String.class);
	    assn.setName(name + "/" + imgName);
	    assn.setArticle(this);
	    assn.setImage(image);
	    toReturn = true;
	    assn.setMaster(this);
	}
	assn.setCaption(caption);
	assn.save();
	return toReturn;
    }

    /**
     * Get the caption of the image
     * @deprecated Do not use this method, it will always return the
     * first available caption regardless of what image is in use. Use
     * ArticleImageAssnCollection.getCaption() or
     * ArticleImageAssociation.getCaption().
     * @return the caption, or null if the image is not associated to this
     *   article
     */
    public String getCaption(ImageAsset image) {
	DataCollection col = (DataCollection)get(IMAGE_CAPTIONS);
	String caption = null;
	if (col.next()) {
	    caption = (String)col.getDataObject().get("caption");
	}
        col.close();
	return caption;
    }

    /**
     * Remove a image from this article.
     * @return true is the image is removed, false otherwise.
     */
    public boolean removeImage(ImageAsset image) {
        ArticleImageAssociation assn = ArticleImageAssociation
            .retrieveAssociation(getID(), image.getID());
	if (assn != null) {
	    assn.delete();
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Get the images for this article
     */
    public ImageAssetCollection getImages() {
	DataAssociationCursor dac = ((DataAssociation) get(IMAGE_CAPTIONS)).cursor();
	ImageAssetCollection images = new ArticleImageAssnCollection(dac);
	return images;
    }

    /**
     * Unassociate all images from this article
     */
    public void clearImages() {
        ImageAssetCollection images = getImages();
        while(images.next()) {
            images.getDomainObject().delete();
        }
    }

    @Override
    protected void propagateMaster(com.arsdigita.versioning.VersionedACSObject master) {
	super.propagateMaster(master);
	ImageAssetCollection collection = getImages();
	while (collection.next()) {
	    ArticleImageAssociation assn = (ArticleImageAssociation)(collection.getDomainObject());
	    assn.setMaster(master);
	    assn.save();
	}
    }
}
