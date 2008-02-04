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

package com.arsdigita.london.rss.category;

import com.arsdigita.london.rss.RSSItem;
import com.arsdigita.london.rss.RSSChannel;
import com.arsdigita.london.rss.RSSItemCollection;
import com.arsdigita.london.rss.RSSImage;
import com.arsdigita.kernel.URLService;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.search.Searchable;
import com.arsdigita.categorization.Category;
import com.arsdigita.util.Assert;

    
public class CategoryChannel implements RSSChannel {
    
    private RSSImage m_image;
    private Category m_category;

    public CategoryChannel(Category category,
                           RSSImage image) {
        m_image = image;
        m_category = category;
    }

    public String getTitle() {
        return m_category.getDisplayName();
    }

    public String getLink() {
        return URLService.locate(m_category.getOID());
    }

    public String getDescription() {
        return m_category.getDescription();
    }

    public RSSImage getImage() {
        return m_image;
    }

    public RSSItemCollection getItems() {
        return new CategoryItemCollection(m_category.getObjects(ACSObject.BASE_DATA_OBJECT_TYPE));
    }

    
    private class CategoryItemCollection implements RSSItemCollection {
        
        private ACSObjectCollection m_objects;
        private ACSObject m_object;

        public CategoryItemCollection(ACSObjectCollection objects) {
            m_objects = objects;
            m_object = null;
        }
        
        public boolean next() {
            if (m_objects.next()) {
                m_object = m_objects.getACSObject();
                return true;
            } else {
                m_object = null;
                return false;
            }
        }

        public RSSItem getItem() {
            Assert.exists(m_object, ACSObject.class);
            return new CategoryItem(m_object);
        }
    }
    
    private class CategoryItem implements RSSItem {
        private ACSObject m_object;
        
        public CategoryItem(ACSObject object) {
            m_object = object;
        }
        
        public String getTitle() {
            return m_object.getDisplayName();
        }
        
        public String getLink() {
            return URLService.locate(m_object.getOID());
        }
        
        public String getDescription() {
            if (m_object instanceof Searchable) {
                return ((Searchable)m_object).getSearchSummary();
            } else {
                return null;
            }
        }
    }
}
