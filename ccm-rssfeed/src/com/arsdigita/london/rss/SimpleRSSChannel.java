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

/**
 * Highly experimental. This API will definitely
 * change, possibly beyond all recognition.
 */
public class SimpleRSSChannel implements RSSChannel {
    
    private String m_title;
    private String m_link;
    private String m_description;
    private RSSImage m_image;
    private RSSItemCollection m_items;
    
    public SimpleRSSChannel(String title,
                            String link,
                            String description,
                            RSSImage image,
                            RSSItemCollection items) {
        m_title = title;
        m_link = link;
        m_description = description;
        m_image = image;
        m_items = items;
    }

    public String getTitle() {
        return m_title;
    }

    public String getLink() {
        return m_link;
    }

    public String getDescription() {
        return m_description;
    }

    public RSSImage getImage() {
        return m_image;
    }

    public RSSItemCollection getItems() {
        return m_items;
    }
}
