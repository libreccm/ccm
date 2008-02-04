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
package com.arsdigita.persistence;

import java.util.Map;

/**
 * Filter is used to restrict the results of a query.  Filters can
 * be combined and manipulated to create complex queries.
 *
 *    <p>
 *    It is important to realize that Filters are just that; they
 *    filter the resulting data from the query.  For instance, if you
 *    have:
 *    </p>
 *    <pre><code>
 *
 *query myDataQuery {
 *  do {
 *     select max(article_id) from articles
 *  } map {
 *     articleID = articles.article_id;
 *  }
 *}</code></pre>
 *
 *      <p>
 *      and then add a the filter "lower(title) like 'b%'"
 *      the new query will be
 *      </p>
 *      <pre><code>
 * select *
 * from (select max(article_id) from articles) results
 * where lower(title) like 'b%'
 * </code></pre>
 *        <p>and not</p>
 *        <pre><code>
 * select max(article_id) from articles where lower(title) like 'b%'
 * </code></pre>
 *
 *        <p>This can clearly lead to different results.</p>
 *  <p>However, it is possible to get the query you want by setting
 *  the WRAP_QUERIES option to false.  You can declare this value within
 *  the "options" block of the data query.  For example,</p>
 *    <pre><code>
 *
 *query myDataQuery {
 *  options {
 *           WRAP_QUERIES = false;
 *  }
 *  do {
 *     select max(article_id), title from articles
 *  } map {
 *     articleID = articles.article_id;
 *  }
 *}</code></pre>
 *
 * <p>
 * It is also important to note that any attribute used within a filter
 * MUST appear within the "map" section of the query definition.  This
 * is because the filter must be able to map the attribute name to the
 * correct column and assuming that an attribute name is the same as
 * the column name is not sufficient.  So, filtering the above query
 * by the "title" column will error.
 *
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #9 $ $Date: 2004/08/16 $
 */

public interface Filter {

    String versionId = "$Id: Filter.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    // this string is used as the namespace for the bind variables
    String FILTER = "__FILTERPARAMS__";


    /**
     *  Sets the values of the bind variables in the Filter.
     *
     *  @param parameterName The name of the bind variable
     *  @param value The value to substitute in for the bind variable.
     *
     *  @pre value == null || value instanceof Number || value instanceof String
     *  @return returns <code>this</code>
     *
     *  @throws PersistenceException if value type unsupported.
     */
    Filter set(String parameterName, Object value);


    /**
     *  This returns the bindings for this Filter.  That is, it returns
     *  a map of key (variable name) - value (variable value) pairs
     *
     *  @return a map of key (variable name) - value (variable value) pairs
     */
    Map getBindings();


    /**
     *  This returns the string representation of this Filter before
     *  any bindings are applied
     */
    String getConditions();

}
