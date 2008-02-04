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

package com.arsdigita.london.navigation;

import com.arsdigita.domain.DomainQuery;
import java.math.BigDecimal;


public class RelatedItemsQuery extends DomainQuery {

    public static final String ITEM_ID = "itemID";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String OBJECT_TYPE = "objectType";
    public static final String WORKING_ID = "workingID";
    
    public RelatedItemsQuery(String queryName) {
        super(queryName);
    }
    
    public BigDecimal getItemID() {
        return (BigDecimal)get(ITEM_ID);
    }

    public String getTitle() {
        return (String)get(TITLE);
    }

    public String getTypeName() {
        return (String)get(TYPE);
    }

    public String getObjectType() {
        return (String)get(OBJECT_TYPE);
    }

    public BigDecimal getWorkingID() {
        return (BigDecimal)get(WORKING_ID);
    }
}
