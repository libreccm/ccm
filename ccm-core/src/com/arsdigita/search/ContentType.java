/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.search;


/**
 * A class to represent the different types of content
 * that can be indexed
 */
public class ContentType {
    
    private String m_name;

    private ContentType(String name) {
        m_name = name;
    }
    
    public String toString() {
        String base = super.toString();
        return base + " (name: " + m_name + ")";
    }

    /**
     * Constant for Raw content
     */
    public static final ContentType RAW = new ContentType("raw");
    
    /**
     * Constant for XML content
     */
    public static final ContentType XML = new ContentType("xml");

    /**
     * Constant for plain text content
     */
    public static final ContentType TEXT = new ContentType("text");
}
