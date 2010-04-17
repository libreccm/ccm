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
package com.arsdigita.domain;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.PersistenceException;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.metadata.CompoundType;
import java.util.Map;

/**
 * This is the base class that all other domain query classes
 * would extend. It provides a facade on to a contained <code>DataQuery</code>.
 *
 * @see com.arsdigita.persistence.DataQuery
 *
 * @author Oumi Mehrotra
 * @author Matthew Booth
 * @version $Id: DomainQuery.java 1045 2005-12-09 13:41:22Z sskracic $
 */
public abstract class DomainQuery implements DataQuery {

    protected final DataQuery m_dataQuery;

    /**
     * Constructor.
     *
     * @see com.arsdigita.persistence.DataQuery
     **/
    public DomainQuery(DataQuery dataQuery) {
        m_dataQuery = dataQuery;
    }

    
    /**
     * Creates a new domain query based on the named
     * data query.
     * @param queryName the fully qualified query name
     */
    public DomainQuery(String queryName) {
        this(SessionManager.getSession().retrieveQuery(queryName));
    }

    /**
     * Returns true if this query fetches the given property.
     *
     * @param propertyName A property name.
     * @return True if this query fetches the given property.
     **/
    public boolean hasProperty(String propertyName) {
        return m_dataQuery.hasProperty( propertyName );
    }



    /**
     * Moves the cursor to the first object in the collection.
     *
     * @return true if the cursor is on an object; false if there are
     * no objects in the collection
     *
     * @see com.arsdigita.persistence.DataQuery#first()
     **/
    public boolean first() {
        return m_dataQuery.first();
    }

    /**
     * Moves to the next object in the collection.
     *
     * @return true if the new current object is valid; false
     * if there are no more objects
     *
     * @see com.arsdigita.persistence.DataQuery#next()
     **/
    public boolean next() {
        return m_dataQuery.next();
    }

    /**
     * Moves to the previous object in the collection.
     *
     * @return true if the new current object is valid; false otherwise
     *
     * @see com.arsdigita.persistence.DataQuery#previous()
     **/
    public boolean previous() {
        return m_dataQuery.previous();
    }

    /**
     * Moves the cursor to the last object in the collection.
     *
     * @return true if the cursor is on a valid object; false if there
     * are no objects in the collection
     *
     * @see com.arsdigita.persistence.DataQuery#last()
     **/
    public boolean last() {
        return m_dataQuery.last();
    }

    /**
     * Inidicates whether the cursor is on the first object of the
     * collection.
     *
     * @return true if the cursor is on the first object; false
     * otherwise
     *
     * @see com.arsdigita.persistence.DataQuery#isFirst()
     **/
    public boolean isFirst() {
        return m_dataQuery.isFirst();
    }

    /**
     * Indicates whether the cursor is on the last object of the
     * collection.
     *
     * @return True if the cursor is on the last object, false
     * otherwise.
     *
     * @see com.arsdigita.persistence.DataQuery#isLast()
     **/
    public boolean isLast() {
        return m_dataQuery.isLast();
    }

    /**
     * Returns the currect position with the collection. The first
     * position is 1.
     *
     * @return The current position, 0 if there is none.
     *
     * @see com.arsdigita.persistence.DataQuery#getPosition()
     **/
    public int getPosition() {
        return m_dataQuery.getPosition();
    }

    /**
     * Returns true if the collection has no rows.
     *
     * @return true if the colleciton has no rows; false otherwise
     *
     * @see com.arsdigita.persistence.DataQuery#isEmpty()
     **/
    public boolean isEmpty() {
        return m_dataQuery.isEmpty();
    }

    /**
     * Indicates whether the cursor is before the first row of the query.
     *
     * @return true if the cursor is before the first row; false if
     *  the cursor is at any other position or the result set contains
     *  rows.
     **/
    public boolean isBeforeFirst() throws PersistenceException {
        return m_dataQuery.isBeforeFirst();
    }

    /**
     * Indicates whether the cursor is after the last row of the query.
     *
     * @return True if the cursor is after the last row, false if the
     *         cursor is at any other position or the result set contains
     *         no rows.
     **/
    public boolean isAfterLast() throws PersistenceException {
        return m_dataQuery.isAfterLast();
    }

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

    public void addPath(String path) {
        m_dataQuery.addPath(path);
    }

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
    public Filter setFilter(String conditions) {
        return m_dataQuery.setFilter( conditions );
    }

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
     *                                  and priority < :upperBound");
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
     *        unless the WRAP_QUERIES option for the DataQuery is set to
     *        "false".
     *        If this is the case, the Filter is simply appended to the end of
     *        the query as follows:
     *        <pre><code>
     *        &lt;data query here&gt;)
     *        [where | or] &lt;conditions here&gt;
     *        </code></pre>
     *        It should normally take the form of
     *        <pre><code>
     *        &lt;attribute_name&gt; &lt;condition&gt; &lt;attribute bind
     *        variable&gt;
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

    public Filter addFilter(String conditions) {
        return m_dataQuery.addFilter( conditions );
    }

    /**
     * Removes the passed in filter from this query if it was directly
     * added to the query.  To remove a filter that was added to a
     * CompoundFilter, you must call CompoundFilter.removeFilter().
     *
     */
    public boolean removeFilter(Filter filter) {
        return m_dataQuery.removeFilter(filter);
    }


    /**
     *  This adds the passed in filter to this query and ANDs it with
     *  an existing filters.  It returns the filter for this query.
     */
    public Filter addFilter(Filter filter) {
        return m_dataQuery.addFilter( filter );
    }

    /**
     * Highly experimental, for use by permissions service only.
     */
    public Filter addInSubqueryFilter(String propertyName,
                                      String subqueryName) {
        return m_dataQuery.addInSubqueryFilter( propertyName, subqueryName );
    }


    /**
     * Add an 'in' subquery to a query. This version can be used with
     * subqueries which return more than 1 column as it wraps the subquery.
     * <code>subQueryProperty</code> is the column pulled out of the subquery.
     *
     * @param propertyName The column to be filtered on.
     * @param subQueryProperty The column in the subquery to be used.
     * @param queryName The fully name of a query defined in a PDL file.
     * @return The Filter object associated with this filter.
     **/
    public Filter addInSubqueryFilter( String propertyName,
                                       String subQueryProperty,
                                       String queryName ) {
        return m_dataQuery.addInSubqueryFilter( propertyName,
                                                subQueryProperty,
                                                queryName );
    }

    /**
     *
     */
    public Filter addNotInSubqueryFilter(String propertyName,
                                         String subqueryName) {
        return m_dataQuery.addNotInSubqueryFilter( propertyName, subqueryName );
    }

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
    public Filter addEqualsFilter(String attribute, Object value) {
        return m_dataQuery.addEqualsFilter( attribute, value );
    }


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
    public Filter addNotEqualsFilter(String attribute, Object value) {
        return m_dataQuery.addNotEqualsFilter( attribute, value );
    }

    /**
     * Clears the current filter for the data query.
     **/
    public void clearFilter() {
        m_dataQuery.clearFilter();
    }


    /**
     *  This retrieves the factory that is used to create the filters
     *  for this DataQuery
     */
    public FilterFactory getFilterFactory() {
        return m_dataQuery.getFilterFactory();
    }


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
    public void setOrder(String order) throws PersistenceException {
        m_dataQuery.setOrder( order );
    }


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
    public void addOrderWithNull(String orderOne, Object orderTwo,
                                 boolean isAscending) {
        m_dataQuery.addOrderWithNull( orderOne, orderTwo, isAscending );
    }


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
    public void addOrder(String order) throws PersistenceException {
        m_dataQuery.addOrder( order );
    }

    /**
     * Clears the current order clause for the data query.
     **/
    public void clearOrder() {
        m_dataQuery.clearOrder();
    }


    /**
     * Allows a user to bind a parameter within a named query.
     *
     * @param parameterName The name of the parameter to bind
     * @param value The value to assign to the parameter
     */
    public void setParameter(String parameterName, Object value) {
        m_dataQuery.setParameter( parameterName, value );
    }


    /**
     * Allows a caller to get a parameter value for a parameter that
     * has already been set
     *
     * @param parameterName The name of the parameter to retrieve
     * @return This returns the object representing the value of the
     * parameter specified by the name or "null" if the parameter value
     * has not yet been set.
     */
    public Object getParameter(String parameterName) {
        return m_dataQuery.getParameter( parameterName );
    }


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
    public void setRange(Integer beginIndex) {
        m_dataQuery.setRange( beginIndex );
    }

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
    public void setRange(Integer beginIndex, Integer endIndex) {
        m_dataQuery.setRange( beginIndex, endIndex );
    }

    /**
     *  This method returns a map of all property/value pairs.  This
     *  essentially allows a single "row" of the query to be passed around.
     */
    public Map getPropertyValues() {
        return m_dataQuery.getPropertyValues();
    }


    /**
     *  This sets the upper bound on the number of rows that can be
     *  returned by this query
     */
    public void setReturnsUpperBound(int upperBound) {
        m_dataQuery.setReturnsUpperBound( upperBound );
    }


    /**
     *  This sets the lower bound on the number of rows that can be
     *  returned by this query
     */
    public void setReturnsLowerBound(int lowerBound) {
        m_dataQuery.setReturnsLowerBound( lowerBound );
    }

    /**
     * Returns the number of rows in this collection.
     *
     * @see com.arsdigita.persistence.DataQueryImpl#size()
     **/
    public long size() {
        return m_dataQuery.size();
    }

    /**
     * Rewinds this collection to the beginning, i.e. it's as if
     * next() was never called.
     *
     * @see com.arsdigita.persistence.DataQueryImpl#rewind()
     **/
    public void rewind() {
        m_dataQuery.rewind();
    }

    /**
     * Returns this collection to its initial state by rewinding it
     * and clearing any filters or ordering.
     *
     * @see com.arsdigita.persistence.DataQueryImpl#reset()
     **/
    public void reset() {
        m_dataQuery.reset();
    }

    /**
     * Explicitly closes this domain collection.
     * Collection should automatically be closed when next
     * returns false, but this method should be
     * explicitly called in the case where all of the data in a query
     * is not needed (e.g. a "while (next())" loop is exited early or
     * only one value is retrieved with if (next()) {...}).
     */
    public void close() {
        m_dataQuery.close();
    }

    /**
     * Returns the value of the <i>propertyName</i> property associated with
     * the current position in the sequence.
     *
     * @param propertyName the name of the property
     *
     * @return the value of the property
     **/
    public Object get(String propertyName) {
        return m_dataQuery.get( propertyName );
    }

    /**
     * Returns the type of this data query.
     **/
    public CompoundType getType() {
        return m_dataQuery.getType();
    }

    /**
     *  Alias a compound property name to a different value. Use the
     *  empty string ("") to add a prefix to all compound property
     *  names that don't match any other aliases.
     *
     *  @param fromPrefix the prefix that you're mapping from i.e.,
     *  the prefix in the PDL file.
     *  @param toPrefix the prefix that you're mapping to i.e.,
     *  the prefix that the programmer is going to use.  */
    public void alias(String fromPrefix, String toPrefix) {
        m_dataQuery.alias(fromPrefix, toPrefix);
    }


    public void setOption(String name, Object value) {
        m_dataQuery.setOption(name, value);
    }

    public Object getOption(String name) {
        return m_dataQuery.getOption(name);
    }
}
