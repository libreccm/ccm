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

package com.arsdigita.london.rss;

import com.arsdigita.util.Assert;


/**
 * Highly experimental. This API will definitely
 * change, possibly beyond all recognition.
 */
public class SimpleRSSItemCollection implements RSSItemCollection {
    
    private RSSItem[] m_items;
    private int m_position;
    
    public SimpleRSSItemCollection(RSSItem[] items) {
        m_items = items;
        m_position = -1;
    }
    
    public boolean next() {
        return (++m_position < m_items.length);
    }

    public RSSItem getItem() {
        Assert.isTrue(m_position >= 0 && m_position < m_items.length,
                     "position out of bounds");
        
        return m_items[m_position];
    }
}
