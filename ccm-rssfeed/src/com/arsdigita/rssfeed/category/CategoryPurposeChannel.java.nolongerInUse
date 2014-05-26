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

package com.arsdigita.rssfeed.category;

import com.arsdigita.rssfeed.RSSItem;
import com.arsdigita.rssfeed.RSSChannel;
import com.arsdigita.rssfeed.RSSItemCollection;
import com.arsdigita.rssfeed.RSSImage;
import com.arsdigita.kernel.URLService;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryPurpose;
import java.util.Iterator;
import java.util.Collection;
import com.arsdigita.util.Assert;

    
public class CategoryPurposeChannel implements RSSChannel {
    
    private RSSImage m_image;
    private CategoryPurpose m_purpose;

    public CategoryPurposeChannel(CategoryPurpose purpose,
                                  RSSImage image) {
        m_image = image;
        m_purpose = purpose;
    }

    public String getTitle() {
        return m_purpose.getName();
    }

    public String getLink() {
        return URLService.locate(m_purpose.getOID());
    }

    public String getDescription() {
        return m_purpose.getDescription();
    }

    public RSSImage getImage() {
        return m_image;
    }

    public RSSItemCollection getItems() {
        return new CategoryPurposeItemCollection(m_purpose.getCategories());
    }

    
    private class CategoryPurposeItemCollection implements RSSItemCollection {
        
        private Iterator m_categories;
        private Category m_category;

        public CategoryPurposeItemCollection(Collection categories) {
            m_categories = categories.iterator();
            m_category = null;
        }
        
        public boolean next() {
            if (m_categories.hasNext()) {
                m_category = (Category)m_categories.next();
                return true;
            } else {
                m_category = null;
                return false;
            }
        }

        public RSSItem getItem() {
            Assert.exists(m_category, Category.class);
            return new CategoryPurposeItem(m_category);
        }
    }
    
    private class CategoryPurposeItem implements RSSItem {
        private Category m_category;
        private String m_categoryLink;
        
        public CategoryPurposeItem(Category category) {
            m_category = category;
        }
        
        public String getTitle() {
            return m_category.getName();
        }
        
        public String getLink() {
            return URLService.locate(m_category.getOID());
        }
        
        public String getDescription() {
            return m_category.getDescription();
        }
    }
}
