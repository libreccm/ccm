/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;

/**
 * This class contains contains a collection of ArticleImageAssociations, each
 * of which points to an image, and each of which has a caption. Ideally it
 * should be constructed with a DataCollection of ArticleImageAssociations
 * which has been filtered on isDeleted=0, or there will be deleted associations
 * in the collection. It extends ImageAssetCollection because we need to pass
 * it off as an ImageAssetCollection at various places in the UI code.
 *
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 *
 *
 * @author Hugh Brock .
 * @version $Id: ArticleImageAssnCollection.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ArticleImageAssnCollection extends ImageAssetCollection {

    /**
     * Constructor. Should only be called from Article.getImages()
     *
     **/
    protected ArticleImageAssnCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Returns a <code>DomainObject</code> (the
     * ArticleImageAssociation for the current position in the
     * collection.
     *
     **/
    public DomainObject getDomainObject() {
	return new ArticleImageAssociation
	    (m_dataCollection.getDataObject());
    }

    /**
     * Returns a <code>Image</code> for the current position in
     * the collection.
     *
     **/
    public ImageAsset getImage() {
        return ((ArticleImageAssociation)getDomainObject()).getImage();
    }

    public String getCaption() {
	return ((ArticleImageAssociation)getDomainObject()).getCaption();
    }

    // Exposed methods
    public Filter addEqualsFilter(String attribute, Object value) {
	return m_dataCollection.addEqualsFilter(attribute, value);
    }


}
