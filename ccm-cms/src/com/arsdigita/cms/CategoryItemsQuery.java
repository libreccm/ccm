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

import com.arsdigita.categorization.Category;
import com.arsdigita.domain.DomainQuery;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;

/**
 * This class represents a query which returns the ContentItems in a
 * given category. The query may return either LIVE or DRAFT
 * items. For each ContentItem returned, the fields represented in the
 * query are ItemID, Name, DisplayName, ObjectType and DefaultDomainClass.
 *
 * @see com.arsdigita.domain.DomainQuery
 **/
public class CategoryItemsQuery extends DomainQuery {
    
    private static final String ITEMS_QUERY = 
        "com.arsdigita.cms.itemsInCategory";

    public static final String CATEGORY_ID = "categoryID";
    public static final String CONTEXT = "context";
    public static final String ITEM_ID = "itemID";
    public static final String NAME = "name";
    public static final String DISPLAY_NAME = "displayName";
    public static final String OBJECT_TYPE= "objectType";
    public static final String DEFAULT_DOMAIN_CLASS= "defaultDomainClass";

    /**
     * Constructor.
     *
     * @see com.arsdigita.domain.DomainQuery
     **/
    private CategoryItemsQuery(DataQuery items) {
        super(items);
    }
    
    /**
     * Returns a CategoryItemsQuery for the given Category and default
     * LIVE context
     *
     * @param cat Category within which to retrieve items
     *
     **/
    public static CategoryItemsQuery retrieve(Category cat) {
	return retrieve(cat, ContentItem.LIVE);
    }

    /**
     * Returns a CategoryItemsQuery for the given Category and item
     * context
     *
     * @param cat Category within which to retrieve items
     * @param context Context (DRAFT or LIVE) for the items.
     *
     **/
    public static CategoryItemsQuery retrieve(Category cat, String context) {
        Session s = SessionManager.getSession();
        DataQuery dc = 
            s.retrieveQuery(ITEMS_QUERY);
        
        dc.setParameter(CATEGORY_ID, cat.getID());
	dc.setParameter(CONTEXT, (ContentItem.LIVE == context) ? 
			ContentItem.LIVE : ContentItem.DRAFT);
        
        return new CategoryItemsQuery(dc);
    }

    /**
     * Returns the value of the <i>itemID</i> property associated with
     *
     * @return the value of the itemID
     **/
    public BigDecimal getItemID() {
        return (BigDecimal)get(ITEM_ID);
    }

    /**
     * Returns the value of the <i>name</i> property associated with
     *
     * @return the value of the name
     **/
    public String getName() {
        return (String)get(NAME);
    }

    /**
     * Returns the value of the <i>displayName</i> property associated with
     *
     * @return the value of the displayName
     **/
    public String getDisplayName() {
        return (String)get(DISPLAY_NAME);
    }

    /**
     * Returns the value of the <i>objectType</i> property associated with
     *
     * @return the value of the objectType
     **/
    public String getObjectType() {
        return (String)get(OBJECT_TYPE);
    }

    /**
     * Returns the value of the <i>defaultDomainClass</i> property associated with
     *
     * @return the value of the defaultDomainClass
     **/
    public String getDefaultDomainClass() {
        return (String)get(DEFAULT_DOMAIN_CLASS);
    }
}
