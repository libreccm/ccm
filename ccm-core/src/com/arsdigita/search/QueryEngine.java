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
 * This interface is the bridge between the generic 
 * application programmers API and the backend (search engine
 * specific) implementations. Instances of this this
 * interface are registered against various combinations
 * of filter type and search engine to perform the actual
 * search operation. Application programmers do not need
 * to access instances of this class directly, rather they
 * should use the <code>process</code> method in the <code>Search</code>
 * class.
 * <p>
 * The provided implementation for intermedia currently
 * allows any combination of the following filters:
 *<pre>
 * 'permissions' - filter by READ privilege
 * 'categories' - filter by category membership
 * 'types' - filter by object type
 *</pre>
 * The provided implementation for lucene currently
 * only supports the 'types' filter.
 *
 * @see com.arsdigita.search.QueryEngineRegistry
 * @see com.arsdigita.search.Search
 */
public interface QueryEngine {
    
    /**
     * Processes a query specification generating a document
     * result set
     * @param spec the query specification
     * @return the document result set
     */
    public ResultSet process(QuerySpecification spec);
}


