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
package com.arsdigita.cms;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.PersistenceException;

/**
 * This class contains a collection of files.
 *
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 *
 * @author <a href="mailto:scott@arsdigita.com">Scott Seago</a>
 * @version 1.0
 * @version $Id: FileAssetCollection.java 287 2005-02-22 00:29:02Z sskracic $ 
 */
public class FileAssetCollection extends DomainCollection {

    /**
     * Constructor.
     *
     **/
    public FileAssetCollection(DataCollection dataCollection) {
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
        return new FileAsset(m_dataCollection.getDataObject());
    }

    /**
     * Returns a <code>File</code> for the current position in
     * the collection.
     *
     **/
    public FileAsset getFile() {
        return (FileAsset) getDomainObject();
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
