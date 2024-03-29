/*
 * Copyright (C) 2005 Runtime Collective Ltd. All Rights Reserved.
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

package com.arsdigita.atoz;

import com.arsdigita.cms.ContentItem;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.OID;
import com.arsdigita.util.Assert;

import org.apache.log4j.Logger;


public class ItemAlias extends ACSObject {

    private static final Logger s_log = Logger.getLogger(ItemAlias.class);
    
    public static final String BASE_DATA_OBJECT_TYPE = ItemAlias.class.getName();

    public static final String TITLE = "title";
    public static final String LETTER = "letter";
    public static final String ITEM_PROVIDER = "itemProvider";
    public static final String CONTENT_ITEM = "contentItem";

    /**
     * Constructor
     */
    public ItemAlias() {
        this(BASE_DATA_OBJECT_TYPE);
    }
    
    /**
     * Constructor
     */
    public ItemAlias(String type) {
        super(type);
    }
    
    /**
     * Constructor
     */
    public ItemAlias(DataObject obj) {
        super(obj);
    }

    /**
     * Constructor
     * @param oid 
     */
    public ItemAlias(OID oid) {
        super(oid);
    }
    
    /**
     * 
     * @param title
     * @param letter
     * @param itemProvider
     * @param contentItem 
     */
    protected void setup(String title,
                         String letter,
                         ItemProvider itemProvider,
                         ContentItem contentItem) {
        setTitle(title);
        setLetter(letter);
        setItemProvider(itemProvider);
        setContentItem(contentItem);
    }

    public String getTitle() {
        return (String) get(TITLE);
    }
    
    public void setTitle(String title) {
        Assert.exists(title, String.class);
        set(TITLE, title);
    }

    public String getLetter() {
        return (String) get(LETTER);
    }
    
    public void setLetter(String letter) {
        set(LETTER, letter);
    }

    public void setItemProvider(ItemProvider itemProvider) {
        Assert.exists(itemProvider, ItemProvider.class);
        set(ITEM_PROVIDER, itemProvider );
    }

    public ItemProvider getItemProvider() {
        if (get(ITEM_PROVIDER) == null) {
            return null;
        } else {
            return new ItemProvider((DataObject) get(ITEM_PROVIDER));
        }
    }

    public void setContentItem(ContentItem contentItem) {
        Assert.exists(contentItem, ContentItem.class);
        set(CONTENT_ITEM, contentItem);
    }

    public ContentItem getContentItem() {
        if (get(CONTENT_ITEM) == null) {
            return null;
        } else {
            return new ContentItem((DataObject) get(CONTENT_ITEM));
        }
    }
}
