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
 * This interface is the API through which a domain
 * object provides its searchable content. Content
 * can be provided in one of (currently) 3 formats,
 * plain text, XML, and RAW binary. Implementors
 * should query the SearchConfig class to determine
 * which formats are supported by the current indexer
 *
 * @see com.arsdigita.search.MetadataProvider
 * @see com.arsdigita.search.SearchConfig
 */
public interface ContentProvider {
    
    /**
     * Returns a 'context' tag for this block of
     * content. This may be interpreted by the 
     * search engine implementations
     */
    String getContext();
    
    /**
     * Returns the type of this contnet, one of
     * the constants defined in the Search class
     *
     * @param dobj the domain object
     */
    ContentType getType();

    // XXX force all types to use byte ?
    // or use subclassing instead ?
    /**
     * Returns the actual content for the object
     *
     * @param dobj the domain object
     * @return byte array representing the text,
     * xml or raw contnet
     */
    byte[] getBytes();

}
