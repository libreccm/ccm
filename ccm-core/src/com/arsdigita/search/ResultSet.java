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

import java.util.Iterator;

/**
 * This interface provides an API for accessing the documents
 * matching a search query in an efficient manner. It allows
 * for retrieval of a 'page' of results &amp; associated metadata
 * such as the total number of results &amp; the search duration.
 * 
 * @see com.arsdigita.search.Document
 */
public interface ResultSet {
    
    /**
     * Gets an iterator for the page of results between
     * <code>offset</code> and <code>offset+count</code>
     *
     * @param offset the first hit, starting from 0
     * @param count the maximum number of results to return
     * @return an iterator of Document objects
     * @throws java.lang.IllegalOperationException if close has been called
     */
    Iterator getDocuments(long offset,
                          long count);
    
    /**
     * Releases any resources associated with this result
     * set. This is a no-op if the result set doesn't have
     * any system resources allocated
     */
    void close();

    /**
     * Returns the total number of results in the set
     * @return the result count
     */
    long getCount();
    
    /**
     * Return the elapsed time for generating this result
     * set from the original query specification.
     * @return the query time in milliseconds
     */
    long getQueryTime();
    
    /**
     * Return the query engine used to generate the 
     * result set
     * @return the query engine
     */
    String getEngine();
}
