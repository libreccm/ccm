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

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.PersistenceException;

/**
 * This class contains a collection of images.
 *
 * @author <a href="mailto:flattop@arsdigita.com">Jack Chung</a>
 * @version 1.0
 *
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 */
public class ImageAssetCollection extends DomainCollection {

    public static final String versionId = "$Id: ImageAssetCollection.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";

    /**
     * Constructor.
     *
     **/
    public ImageAssetCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Set the order of this Collection. This method needs to be called
     * before <code>next()</code> is called on this collection.
     *
     */
    public void addOrder(String order) {
        m_dataCollection.addOrder(order);
    }

    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     *
     **/
    public DomainObject getDomainObject() {
        return new ImageAsset(m_dataCollection.getDataObject());
    }

    /**
     * Returns a <code>Image</code> for the current position in
     * the collection.
     *
     **/
    public ImageAsset getImage() {
        return (ImageAsset) getDomainObject();
    }

    // Exposed methods
    public Filter addFilter(String conditions) {
        return m_dataCollection.addFilter(conditions);
    }

    public void clearFilter() {
        m_dataCollection.clearFilter();
    }

    public void clearOrder() throws PersistenceException {
        m_dataCollection.clearOrder();
    }



}
