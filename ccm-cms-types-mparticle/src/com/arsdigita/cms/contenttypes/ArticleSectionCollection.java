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
package com.arsdigita.cms.contenttypes;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.PersistenceException;

/**
 * This class contains a collection of ArticleSections.
 *
 * @see DomainCollection
 * @see DataCollection
 *
 * @author <a href="mailto:hbrock@redhat.com">Hugh Brock</a>
 * @version $Id: ArticleSectionCollection.java 2099 2010-04-17 15:35:14Z pboy $
 */
public class ArticleSectionCollection extends DomainCollection {

    /**
     * Constructor.
     *
     **/
    public ArticleSectionCollection(DataCollection dataCollection) {
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
        return new ArticleSection(m_dataCollection.getDataObject());
    }

    /**
     * Returns a <code>Image</code> for the current position in
     * the collection.
     *
     **/
    public ArticleSection getArticleSection() {
        return (ArticleSection) getDomainObject();
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
