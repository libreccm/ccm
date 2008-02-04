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

import com.arsdigita.persistence.metadata.CompoundType;
import java.util.Map;

/**
 * An instance of the DataQuery class may be used to access the results of a
 * named query. It is typically used in the following manner:
 *
 * <blockquote><pre>
 * DataQuery query = session.retrieveQuery("MyQuery");
 *
 * Filter f = query.addEqualsFilter("myProperty", value);
 *
 * query.addOrder("creationDate desc");
 *
 * int numLines = query.size();
 * System.out.println("Lines: " + numLines);
 *
 * while (query.next()) {
 *   Object prop = query.get("myProperty");
 *   System.out.println("MyProperty: " + prop);
 * }
 * </pre></blockquote>
 *
 * Named queries are defined in a PDL file using the following syntax:
 * <pre><blockquote>
 * query MyQuery {
 *     do {
 *         select *
 *         from my_table;
 *     } map {
 *         myProperty = my_table.my_column;
 *         creationDate = my_table.creation_date;
 *     }
 * }
 * </blockquote>
 * </pre>
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #14 $ $Date: 2004/08/16 $
 */

public interface DataQuery {

    String versionId = "$Id: DataQuery.java 1045 2005-12-09 13:41:22Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Returns the type of this data query.
     **/
    CompoundType getType();


    /**
     * Returns true if this query fetches the given property.
     *
     * @param propertyName A property name.
     * @return True if this query fetches the given property.
     **/
    boolean hasProperty(String propertyName);


    /**
     * Returns the data query to its initial state by rewinding it and
     * clearing any filters or ordering.
     **/
    void reset();


    /**
     * Moves the cursor to the first row in the query.
     * <font color=red>Not implemented yet.</font>
     *
     * @return true if the cursor is on a valid row; false if there are no
     *         rows in the query.
     *
     * @exception PersistenceException Always thrown!
     **/
    boolean first() throws PersistenceException;


    /**
     * Returns true if the query has no rows.
     *
     * @return true if the query has no rows; false otherwise
     **/
    boolean isEmpty() throws PersistenceException;


    /**
     * Indicates whether the cursor is before the first row of the query.
     *
     * @return true if the cursor is before the first row; false if
     *  the cursor is at any other position or the result set contains
     *  rows.
     **/
    boolean isBeforeFirst() throws PersistenceException;

    /**
     * Indicates whether the cursor is on the first row of the query.
     *
     * @return true if the cursor is on the first row; false otherwise
     **/
    boolean isFirst() throws PersistenceException;


    /**
     * Indicates whether the cursor is on the last row of the query.
     * Note: Calling the method isLast may be expensive because the
     * JDBC driver might need to fetch ahead one row in order to
     * determine whether the current row is the last row in the result
     * set.
     * <p>
     * If the query has not yet been executed, it executes the query.
     * <p>
     * <font color=red>Not implemented yet.</font>
     *
     * @return True if the cursor is on the last row, false otherwise.
     **/
    boolean isLast() throws PersistenceException;


    /**
     * Indicates whether the cursor is after the last row of the query.
     *
     * @return True if the cursor is after the last row, false if the
     *         cursor is at any other position or the result set contains
     *         no rows.
     **/
    boolean isAfterLast() throws PersistenceException;


    /**
     * Moves the cursor to the last row in the query.
     * <font color=red>Not implemented yet.</font>
     * <p>
     * <font color=red>Not implemented yet.</font>
     *
     * @return true if the new current row is valid; false if there are no
     *         rows in the query
     * @exception PersistenceException Always thrown!
     **/
    boolean last() throws PersistenceException;


    /**
     * Moves to the previous row in the query.
     * <font color=red>Not implemented yet.</font>
     *
     * @return true if the new current row is valid; false otherwise
     * @exception PersistenceException Always thrown!
     **/
    boolean previous() throws PersistenceException;


    /**
     * Adds to the set of paths fetched by this DataQuery. The path is
     * specified by a series of identifiers seperated by dots ('.').
     * ID properties are automatically added as necessary.
     *
     * <blockquote><pre>
     * DataQuery query = ssn.retrieve("exampleQuery");
     * query.addPath("foo.bar.name");
     * query.addPath("foo.bar.desc");
     * while (query.next()) {
     *     BigInteger id = query.get("foo.bar.id");
     *     String name = query.get("foo.bar.name");
     *     String desc = query.get("foo.bar.desc");
     * }
     * </pre></blockquote>
     *
     * @param path the additional path to fetch
     **/

    void addPath(String path);


    /**
     * Sets a filter for this query. The filter consists of a set of SQL
     * condition specified in terms of the properties of this query. The
     * conditions may be combined with "and" and "or". Bind variables may be
     * used in the body of the filter. The values are set by using the set
     * method on the Filter object that is returned.
     *
     * <blockquote><pre>
     * Filter f = query.setFilter("id < :maxId and id > :minId");
     * f.set("maxId", 10);
     * f.set("minId", 1);
     * </pre></blockquote>
     *
     * @param conditions the conditions for the filter
     * @deprecated see {@link #addFilter(java.lang.String)}
     *
     * @return the newly created filter for this query
     **/
    Filter setFilter(String conditions);


    /**
     * Adds the conditions to the filter that will be used on this
     * query.  If a filter already exists, this alters the filter
     * object and returns the altered object.  If one does not
     * already exist, it creates a new filter.  When adding filters
     * the user should be aware that their query is wrapped and the
     * filter is appended to the wrapped query.  That is, your query
     * will look like the following:
     *        <pre><code>
     *        select * from (&lt;data query here&gt;) results
     *        where &lt;conditions here&gt;
     *        </code></pre>
     *<p>
     *
     * When adding
     * filters, the user should not use the same parameter name
     * in multiple filters.  That is, the following will not work
     *
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority > :bound");
     * filter.set("bound", new Integer(3));
     * filter = query.addFilter("priority < :bound");
     * filter.set("bound", new Integer(8));
     * </code>
     * </pre>
     * The above actually evaluates to
     * <code>"priority < 8 and priority > 8"</code>
     * which is clearly not what the developer wants.
     * <p>
     * The following will work.
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority > :lowerBound");
     * filter.set("lowerBound", new Integer(3));
     * filter = query.addFilter("priority < :upperBound");
     * filter.set("upperBound", new Integer(8));
     * </code>
     * </pre>
     * It is actually the same as
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority > :lowerBound
     *                                  and priority < :uperBound");
     * filter.set("upperBound", new Integer(8));
     * filter.set("lowerBound", new Integer(3));
     * </code>
     * </pre>
     *
     * @param conditions The conditions for the filter.  This is a string
     *        that should be used to filter the DataQuery.  Specifically,
     *        if this is the first filter added, it appends the information
     *        on to a view-on-the-fly.  e.g.
     *        <pre><code>
     *        select * from (&lt;data query here&gt;) results
     *        where &lt;conditions here&gt;
     *        </code></pre>
     *        unless the WRAP_QUERIES option for the DataQuery is set to "false".
     *        If this is the case, the Filter is simply appended to the end of
     *        the query as follows:
     *        <pre><code>
     *        &lt;data query here&gt;)
     *        [where | or] &lt;conditions here&gt;
     *        </code></pre>
     *        It should normally take the form of
     *        <pre><code>
     *        &lt;attribute_name&gt; &lt;condition&gt; &lt;attribute bind variable&gt;
     *        </code></pre>
     *        where the "condition" is something like "=", "&lt;", "&gt;", or
     *        "!=".  The "bind variable" should be a colon followed by
     *        some attribute name that will later be set with a call to
     *        {@link com.arsdigita.persistence.Filter#set(java.lang.String,
     *               java.lang.Object)}
     *        <p>
     *        It is possible to set multiple conditions with a single
     *        addFilter statement by combining the conditions with an "and"
     *        or an "or".  Conditions may be grouped by using parentheses.
     *        Consecutive calls to addFilter append the filters using
     *        "and".
     *        <p>
     *        If there is already a filter that exists for this query
     *        then the passed in conditions are added to the current
     *        conditions with an AND like <code>(&lt;current conditions&gt;)
     *        and (&lt; passed in conditions&gt;)</code>
     *
     * @return The filter that has just been added to the query
     **/

    Filter addFilter(String conditions);


    /**
     *  This adds the passed in filter to this query and ANDs it with
     *  an existing filters.  It returns the filter for this query.
     */
    Filter addFilter(Filter filter);

    /**
     * Removes the passed in filter from this query if it was directly
     * added to the query.  To remove a filter that was added to a
     * CompoundFilter, you must call CompoundFilter.removeFilter().
     *
     */
    boolean removeFilter(Filter filter);

    /**
     * Add an 'in' subquery to a query.
     *
     * @param propertyName The column to be filtered on.
     * @param subqueryName The full name of a query defined in a PDL file.
     */
    Filter addInSubqueryFilter(String propertyName, String subqueryName);


    /**
     * Highly experimental; use with caution.. Add an 'in' subquery to
     * a query. This version can be used with subqueries which return
     * more than 1 column as it wraps the subquery.
     * <code>subQueryProperty</code> is the column pulled out of the
     * subquery.
     *
     * @param propertyName The column to be filtered on.
     * @param subQueryProperty The column in the subquery to be used.
     * @param queryName The full name of a query defined in a PDL file.
     * @return The Filter object associated with this filter.
     **/
    Filter addInSubqueryFilter( String propertyName,
                                String subQueryProperty,
                                String queryName );

    /**
     *
     */
    Filter addNotInSubqueryFilter(String propertyName, String subqueryName);

    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  = 'value.toString()'</code>" unless the value is an integer
     *  (in which case it creates "</code>attribute =
     *  value.toString()</code>") or the developer is using oracle and
     *  the value is null.  In this case, it would create
     *  "<code>attribute is null</code>".
     *
     *  <p>
     *
     *  This is simply a convenience method for
     *  <code>
     *  addFilter(getFilterFactory().equals(attribute, value));
     *  </code>
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     */
    Filter addEqualsFilter(String attribute, Object value);


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  = 'value.toString()'</code>" unless the value is an integer
     *  (in which case it creates "</code>attribute !=
     *  value.toString()</code>") or the developer is using oracle and
     *  the value is null.  In this case, it would create
     *  "<code>attribute is not null</code>".
     *
     *  <p>
     *
     *  This is simply a convenience method for
     *  <code>
     *  addFilter(getFilterFactory().notEquals(attribute, value));
     *  </code>
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     */
    Filter addNotEqualsFilter(String attribute, Object value);


    /**
     * Clears the current filter for the data query.
     **/
    void clearFilter();


    /**
     *  This retrieves the factory that is used to create the filters
     *  for this DataQuery
     */
    FilterFactory getFilterFactory();


    /**
     * Set the order in which the result of this query will be returned. The
     * string passed is a standard SQL order by clause specified in terms of
     * the properties. For example:
     *
     * <blockquote><pre>
     * query.setOrder("creationDate desc, id");
     * </pre></blockquote>
     * @deprecated see {@link #addOrder(java.lang.String)}
     **/
    void setOrder(String order) throws PersistenceException;


    /**
     * Set the order in which the result of this query will be returned. The
     * string passed is a standard SQL order by clause specified in terms of
     * the properties. For example:
     *
     * <blockquote><pre>
     * query.addOrder("creationDate desc, id");
     * </pre></blockquote>
     *
     * @param order This String parameter specifies the ordering of the
     *              output.  This should be a comma seperated list
     *              of Attribute names (not the database column names)
     *              in the order of precedence.
     *              Separating attributes by commas is the same as
     *              calling addOrder multiple times, each with the
     *              next attribute.  For instance, this
     *              <pre><code>
     *              addOrder("creationDate");
     *              addOrder("creationUser");
     *              </code></pre>
     *              is the same as
     *              <pre><code>
     *              addOrder("creationDate, creationUser");
     *              </code></pre>
     *
     *              <p>
     *              If the items should be ordered in ascending order,
     *              the attribute name should be followed by the word "asc"
     *              If the items should be ordered in descending order,
     *              the attribute should be followed by the word "desc"
     *              For instance, or order by ascending date and descending
     *              user (for users created with the same date), you would
     *              use the following:
     *              <pre><code>
     *              addOrder("creationDate asc, creationUser desc");
     *              </code></pre>
     *
     **/
    void addOrder(String order) throws PersistenceException;


    /**
     *  This adds order on the first value if it is not null or
     *  the second value if the first value is null.  This is
     *  similar to doing an addOrder(nvl(columnOne, columnTwo))
     *
     *  @param orderOne This is typically the column that will
     *                  be used for the ordering.  If this column
     *                  is null then the value of orderTwo is used for
     *                  the ordering
     *  @param orderTwo This is typically an actual value (such as -1)
     *                  but can also be a column name the value used
     *                  for the ordering
     *  @param isAscending If this is true then the items are ordered
     *                     in ascending order.  Otherwise, they are
     *                     ordering in descending order
     *  @exception PersistenceException is thrown if the query has
     *             already been executed.
     */
    void addOrderWithNull(String orderOne, Object orderTwo,
                          boolean isAscending)
        throws PersistenceException;


    /**
     * Clears the current order clause for the data query.
     **/
    void clearOrder();


    /**
     * Allows a user to bind a parameter within a named query.
     *
     * @param parameterName The name of the parameter to bind
     * @param value The value to assign to the parameter
     */
    void setParameter(String parameterName, Object value);


    /**
     * Allows a user to pass arbitrary options to the underlying
     * persistence engine.  Currently, only one option is implemented
     * "jdbc_resultset_windowsize" which, if set, overrides the
     * default window size for the curreny query.  This option must be
     * set before a call to next() has been made.
     *
     * @param optionName The name of the option parameter
     * @param value The value to assign to option parameter
     */
    void setOption(String optionName, Object value);

    /**
     * Examine the query configuration options.
     *
     * @param optionName The name of the query option parameter
     * @return the object representing the value of the query
     *         option parameter or "null" if not set
     */
    Object getOption(String optionName);


    /**
     * Allows a caller to get a parameter value for a parameter that
     * has already been set
     *
     * @param parameterName The name of the parameter to retrieve
     * @return This returns the object representing the value of the
     * parameter specified by the name or "null" if the parameter value
     * has not yet been set.
     */
    Object getParameter(String parameterName);


    /**
     *  This method allows the developer to set the range of
     *  rows desired.  Thus, the DataQuery will only return the
     *  rows between beginIndex and endIndex.  The range begins
     *  at the specified beginIndex and returns all rows after that.
     *  Thus, if a query returns 30 rows and the beginIndex is set
     *  to 6, the last 25 rows of the query will be returned.
     *
     *  @param beginIndex This is the number of the first row that
     *                    should be returned by this query.  Setting
     *                    beginIndex to 1 returns all rows.  This is
     *                    inclusive.
     */
    void setRange(Integer beginIndex);

    /**
     *  This method allows the developer to set the range of
     *  rows desired.  Thus, the DataQuery will only return the
     *  rows between beginIndex and endIndex.  The range begins
     *  at the specified beginIndex and extends to the row at index
     *  endIndex - 1. Thus the number of rows returned is
     *  endIndex-beginIndex.
     *
     *  @param beginIndex This is the number of the first row that
     *                    should be returned by this query.  Setting
     *                    beginIndex to 1 returns the rows from the
     *                    beginning.  This is inclusive.
     *  @param endIndex This is the number of the row after the last
     *                  row that should be returned.  That is, this
     *                  is exclusive (specifying beginIndex = 1 and
     *                  endIndex = 10 returns 9 rows);
     *  @exception A PersistenceException is thrown if
     *             endIndex <= beginIndex
     */
    void setRange(Integer beginIndex, Integer endIndex);


    /**
     *  This method returns a map of all property/value pairs.  This
     *  essentially allows a single "row" of the query to be passed around.
     */
    public Map getPropertyValues();


    /**
     *  This sets the upper bound on the number of rows that can be
     *  returned by this query
     */
    public void setReturnsUpperBound(int upperBound);


    /**
     *  This sets the lower bound on the number of rows that can be
     *  returned by this query
     */
    public void setReturnsLowerBound(int lowerBound);

    /**
     *  Alias a compound property name to a different value. Use the
     *  empty string ("") to add a prefix to all compound property
     *  names that don't match any other aliases.
     *
     *  @param fromPrefix the prefix that you're mapping from i.e.,
     *  the prefix in the PDL file.
     *  @param toPrefix the prefix that you're mapping to i.e.,
     *  the prefix that the programmer is going to use.  */
    public void alias(String fromPrefix, String toPrefix);

    /**
     * Explicitly closes this DataQuery.
     * Query should automatically be closed when next
     * returns false, but this method should be
     * explicitly called in the case where all of the data in a query
     * is not needed (e.g. a "while (next())" loop is exited early or
     * only one value is retrieved with if (next()) {...}).
     */
    void close();

    /**
     * Rewinds the row sequence to the beginning.  It's as if next() was
     * never called.
     **/
    void rewind();


    /**
     * Returns the value of the <i>propertyName</i> property associated with
     * the current position in the sequence.
     *
     * @param propertyName the name of the property
     *
     * @return the value of the property
     **/
    Object get(String propertyName);


    /**
     * Returns the current position within the sequence. The first
     * position is 1.
     *
     * @return the current position; 0 if there is no current position
     **/
    int getPosition();

    /**
     * Moves the cursor to the next row in the sequence.
     *
     * @return true if the new current row is valid; false if there are no
     *         more rows.
     **/
    boolean next();

    /**
     * Returns the size of this query.
     * @return the number of rows.
     **/
    long size();

}
