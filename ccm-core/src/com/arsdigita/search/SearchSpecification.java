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
 *
 */
package com.arsdigita.search;


/**
 * @deprecated moved to com.arsdigita.search.intermedia package
 * @see com.arsdigita.search.intermedia.SearchSpecification
 **/

public class SearchSpecification 
    extends com.arsdigita.search.intermedia.SearchSpecification{


    /**
     * Create a SearchSpecification object.
     * @param sql Sql select statement to perform the search.  See
     *    example above.
     * @param columns The names of columns returned by the
     *    search.
     * @param maxRows The maximum number of result rows retrieved
     *    by the search.
     * @param rowsPerPage The maximum number of rows per result
     * page returned by method getPage.
     **/
    public SearchSpecification(String sql, String[] columns,
                               int maxRows, int rowsPerPage) {

        super(sql, columns, maxRows, rowsPerPage);
    }

    /**
     * Create a SearchSpecification object, using the default values
     * for maxRows and rowsPerPage.
     * @param sql Sql select statement to perform the search.  See
     *    example above.
     * @param columns The names of columns returned by the
     *    search.
     **/
    public SearchSpecification(String sql, String[] columns) {
        super(sql, columns);
    }

}
