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

/**
 * This class contains a collection of {@link
 * com.arsdigita.cms.ContentType content types}.
 *
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 *
 * @author Jack Chung (flattop@arsdigita.com)
 * @version $Id: ContentTypeCollection.java 2263 2012-01-08 17:43:22Z pboy $ 
 */
public class ContentTypeCollection extends DomainCollection {

    /**
     * Constructor.
     *
     * @param dataCollection
     */
    public ContentTypeCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Set the order of this Collection. This method needs to be called
     * before <code>next()</code> is called on this collection.
     *
     * @param order: name of the pdl property to use for sorting the collection
     * (e.g. "primaryURL" in order to sort by section name
     * see c.ad.london.util.cmd.SiteMapList as an example ).
     */
    @Override
    public void addOrder(String order) {
        m_dataCollection.addOrder(order);
    }


    @Override
    public Filter addFilter(String conditions) {
        return m_dataCollection.addFilter(conditions);
    }


    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     *
     * @return 
     */
    @Override
    public DomainObject getDomainObject() {
        return new ContentType(m_dataCollection.getDataObject());
    }

    /**
     * Returns a <code>LifecycleDefinition</code> for the current position in
     * the collection.
     *
     * @return 
     */
    public ContentType getContentType() {
        return (ContentType) getDomainObject();
    }

    /**
     * Reset this collection
     */
    @Override
    public void reset() {
        m_dataCollection.reset();
    }


}
