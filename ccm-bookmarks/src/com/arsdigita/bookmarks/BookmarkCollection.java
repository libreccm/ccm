/*
 * Copyright (C) 2001, 2002 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of
 * the License at http://www.redhat.com/licenses/ccmpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package com.arsdigita.bookmarks;

import com.arsdigita.domain.DomainCollection;
import com.arsdigita.domain.DomainObject;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.util.Assert;
import java.math.BigDecimal;

public class BookmarkCollection extends DomainCollection {

    protected BookmarkCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Get the ID for the Bookmark for the current row.
     *
     * @return the id of this Bookmark.
     * @post return != null
     */
    public BigDecimal getID() {
        BigDecimal id = (BigDecimal)m_dataCollection.get("id");

        Assert.assertNotNull(id);

        return id;
    }

    /**
     * Get the current item as a domain object.
     *
     * @return the domain object for the current row.
     * @post return != null
     */
    public DomainObject getDomainObject() {
        DomainObject domainObject = getBookmark();

        Assert.assertNotNull(domainObject);

        return domainObject;
    }

    /**
     * Get the current item as a Bookmark domain object.
     *
     * @return a Bookmark domain object.
     * @post return != null
     */
    public Bookmark getBookmark() {
        DataObject dataObject = m_dataCollection.getDataObject();

        Bookmark bookmark = Bookmark.retrieveBookmark(dataObject);

        Assert.assertNotNull(bookmark);

        return bookmark;
    }

    /**
     * Get the name for the Bookmark for the current row.
     *
     * @return the name of this bookmark.
     * @post return != null
     */
    public String getName() {
        String name = (String)m_dataCollection.get("bookmark_name");

        Assert.assertNotNull(name);

        return name;
    }

    /**
     * Get the url for the bookmark for the current row.
     *
     * @return the url of this bookmark.
     * @post return != null
     */
    public String getURL() {
        String url = (String)m_dataCollection.get("bookmark_url");

        Assert.assertNotNull(url);

        return url;
    }

    public boolean getNewWindow() {
        return "1".equals(m_dataCollection.get("new_window"));
    }
}
