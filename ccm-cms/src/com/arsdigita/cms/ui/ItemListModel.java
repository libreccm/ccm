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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemCollection;

/**
 *  A {@link ListModel} that iterates over the passed in ItemCollection
 *  This is an easy starting place for lists that need to dispaly items.
 */
public class ItemListModel implements ListModel {
    
    public static final String versionId = "$Id: ItemListModel.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/17 23:15:09 $";
    
    private ItemCollection m_collection;
    private ContentItem m_item;

    /**
     * Construct a new <code>GroupListModel</code>
     */
    public ItemListModel(ItemCollection collection) {
        m_collection = collection;
    }

    public boolean next() {
        if(m_collection.next()) {
            m_item = m_collection.getContentItem();
            return true;
        } else {
            m_item = null;
            return false;
        }
    }

    /**
     *  This returns the Name of the item
     */
    public Object getElement() {
        return m_item.getName();
    }

    /**
     *  This returns the item ID as a String
     */
    public String getKey() {
        return m_item.getID().toString();
    }

    /**
     *  This provides protected access to the current content item 
     *  This can be used by code that needs to override the behavior
     *  of getKey or getElement
     */
    protected ContentItem getCurrentItem() {
        return m_item;
    }
}
