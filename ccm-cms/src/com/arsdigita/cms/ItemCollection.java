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
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataCollection;
import java.util.Date;
import java.math.BigDecimal;

/**
 * Represents a set of {@link com.arsdigita.cms.ContentItem content
 * items}.
 *
 * @author <a href="mailto:pihman@arsdigita.com">Michael Pih</a>
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @version $Revision: #14 $ $Date: 2004/08/17 $
 *
 * @see com.arsdigita.domain.DomainCollection
 * @see com.arsdigita.persistence.DataCollection
 */
public class ItemCollection extends DomainCollection {

    /**
     * Constructor.
     */
    public ItemCollection(DataCollection dataCollection) {
        super(dataCollection);
    }

    /**
     * Returns a <code>DomainObject</code> for the current position in
     * the collection.
     */
    public DomainObject getDomainObject() {
        return DomainObjectFactory.newInstance(m_dataCollection.getDataObject());
    }


    /**
     * Returns a <code>ContentItem</code> for the current position in
     * the collection.
     */
    public ContentItem getContentItem() {
        return (ContentItem) getDomainObject();
    }

    /**
     * Return the object ID for the content item at the current position in
     * the collection.
     *
     * @return the object ID for the content item at the current position in
     * the collection.
     */
    public BigDecimal getID() {
        return (BigDecimal) get(ACSObject.ID);
    }

    /**
     * Return the name of the content item at the current position in the
     * collection.
     *
     * @return the name the content item at the current position in the
     * collection.
     */
    public String getName() {
        return (String) get(ContentItem.NAME);
    }

    /**
     * Return the display name for the current object. If the data collection
     * has an attribute with name <code>ACSObject.DISPLAY_NAME</code> that is
     * used. Otherwise, the underlying content item is instantiated, and its
     * display name is returned.
     *
     * @return the display name of the current item
     */
    public String getDisplayName() {
        String result = (String) get(ACSObject.DISPLAY_NAME);
        if ( result == null ) {
            return getContentItem().getDisplayName();
        }
        return result;
    }

    /**
     * Return the version of the content item at the current position in the
     * collection.
     *
     * @return the version the content item at the current position in the
     * collection.
     */
    public String getVersion() {
        return (String) get(ContentItem.VERSION);
    }

    /**
     * Return the language of the content item at the current position
     * in the collection.
     *
     * @return the language the content item at the current position
     * in the collection.
     */
    public String getLanguage() {
        return (String) get(ContentItem.LANGUAGE);
    }

    /**
     * Filter items by name and leave only those in the collection whose name
     * equals the given value.
     *
     * @param name the name for which items should be filtered.
     */
    public void addNameFilter(String name) {
        m_dataCollection.addEqualsFilter(ContentItem.NAME, name);
    }

    /**
     * Filter items by version and leave only those in the collection that
     * are live (if <code>true</code> is passed in) or that are draft items
     * (if <code>false</code>) is passed in.
     *
     * @param live <code>true</code> if only live items should remain in the
     * collection, <code>false</code> if only draft items should remain.
     */
    public void addVersionFilter(boolean live) {
        m_dataCollection.addEqualsFilter(ContentItem.VERSION,
                                         live ? ContentItem.LIVE : ContentItem.DRAFT);
    }

    public Date getLastModifiedDate() {
        return getContentItem().getLastModifiedDate();
    }

    public Date getCreationDate() {
        return getContentItem().getCreationDate();
    }
}
