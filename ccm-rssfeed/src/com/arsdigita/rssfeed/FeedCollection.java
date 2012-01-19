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

package com.arsdigita.rssfeed;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

/**
 *
 * @see Feed
 * @version $Id: FeedCollection.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class FeedCollection extends DomainCollection {

    /**
     * Constructor
     * 
     * @param dataCollection 
     */
    protected FeedCollection(DataCollection dataCollection) {
        super(dataCollection);
    }
    
    /**
     * Get the ID for the portal for the current row.
     *
     * @return the id of this portal.
     * @post return != null 
     */
    public BigDecimal getID() {
        BigDecimal id = (BigDecimal)m_dataCollection.get("id");
 
        Assert.exists(id, BigDecimal.class);
       
        return id;
    }

    /**
     * Get the current item as a domain object.
     * 
     * @return the domain object for the current row.
     * @post return != null
     */
    @Override
    public DomainObject getDomainObject() {
        DomainObject domainObject = getFeed();

        Assert.exists(domainObject, DomainObject.class);

        return domainObject;
    }

    /**
     * Get the current item as a Feed domain object.
     *
     * @return a Feed domain object.
     * @post return != null
     */
    public Feed getFeed() {
        DataObject dataObject = m_dataCollection.getDataObject();
 
        Feed portal = new Feed(dataObject);

        Assert.exists(portal, Feed.class);

        return portal;
    }

    /**
     * 
     * @param filter 
     */
    public void filterACSJFeeds(boolean filter) {
        m_dataCollection.addEqualsFilter(Feed.IS_PROVIDER, new Boolean(filter));
    }

    /**
     * Get the title for the portal for the current row.
     *
     * @return the title of this portal.
     * @post return != null 
     */
    public String getTitle() {
        String title = (String)m_dataCollection.get("title");

        Assert.exists(title, String.class);

        return title;
    }
}
