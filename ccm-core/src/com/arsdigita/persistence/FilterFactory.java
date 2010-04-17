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


/**
 * FilterFactory is the interface that dictates the methods needed by
 * factories to hand out filters
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Id: FilterFactory.java 287 2005-02-22 00:29:02Z sskracic $
 */

public interface FilterFactory {

    // These are variables indicating what to use in the comparrison
    public final static int EQUALS = 1;
    public final static int NOT_EQUALS = 2;
    public final static int GREATER_THAN = 3;
    public final static int LESS_THAN = 4;
    public final static int GREATER_THAN_EQUALS = 5;
    public final static int LESS_THAN_EQUALS = 6;
    public final static int STARTS_WITH = 7;
    public final static int ENDS_WITH = 8;
    public final static int CONTAINS = 9;


    /**
     *
     *  @param sql The conditions for the filter.  This is a string
     *        that should represent part of a SQL "where" clause.  Specifically,
     *        it should normally take the form of
     *        <pre><code>
     *        &lt;column_name&gt; &lt;condition&gt; &lt;attribute bind variable&gt;
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
     */
    Filter simple(String sql);


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  = 'value.toString()'</code>" unless the value is an integer
     *  (in which case it creates "</code>attributeName =
     *  value.toString()</code>") or the developer is using oracle and
     *  the value is null.  In this case, it would create
     *  "<code>attributeName is null</code>".
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute */
    Filter equals(String attribute, Object value);


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  != 'value.toString()'</code>" unless the value is an integer
     *  (in which case it creates "</code>attributeName !=
     *  value.toString()</code>") or the developer is using oracle and
     *  the value is null.  In this case, it would create
     *  "<code>attributeName is not null</code>".
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     */
    Filter notEquals(String attribute, Object value);


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  < value</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    Filter lessThan(String attribute, Object value,
                    boolean trueForAllIfValueIsNull);


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  <= value</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    Filter lessThanEquals(String attribute, Object value,
                          boolean trueForAllIfValueIsNull);


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  > value</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    Filter greaterThan(String attribute, Object value,
                       boolean trueForAllIfValueIsNull);


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  >= value</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    Filter greaterThanEquals(String attribute, Object value,
                             boolean trueForAllIfValueIsNull);


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  like 'value%'</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    Filter startsWith(String attribute, String value,
                      boolean trueForAllIfValueIsNull);


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  like '%value'</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    Filter endsWith(String attribute, String value,
                    boolean trueForAllIfValueIsNull);


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attributeName
     *  like '%value%'</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of 1==1 (true)
     *         or 1==2 (false)
     */
    Filter contains(String attribute, String value,
                    boolean trueForAllIfValueIsNull);


    /**
     *  This method is used to compare two expressions to each other.
     *  This is necessary instead of just passing in straight SQL so that
     *  Oracle's null problem may be handled correctly.  Specifically,
     *  this creates the expression
     *  <code><pre>
     *  ((&lt;expressionOne&gt; &lt;comparator&gt; &lt;expressionTwo&gt;)
     *    or
     *   (&lt;expressionOne&gt; is [not] null
     *     and &lt;expressionTwo&gt; is [not] null))
     *  </pre></code>
     *  <p>
     *  This method is useful when code needs to use PL/SQL functions
     *  within the filter (e.g. <code>nvl</code> or <code>upper</code> or
     *  <code>lower</code>
     *
     *  @param expressionOne This is the first expression for the comparrison.
     *                       One typical use for it is to pass in something
     *                       like "upper(&lt;attribute name&gt;)"
     *  @param comparator This is the "int" that is used to represent
     *                    how to compare the two expressions.  The int
     *                    should be one of the "int"s specified by the
     *                    constants within FilterFactory
     *  @param expressionTwo This is the second expression.  This could
     *                       be something as simple as a bind variable or
     *                       as complex as a call to a PL/SQL function.
     */
    Filter compare(String expressionOne, int comparator, String expressionTwo);


    /**
     * This creates a filter that constructs an "in" style subquery with the
     * given property and subquery. The subquery must be a fully qualified
     * query name of a query defined in a PDL file somewhere.
     **/

    Filter in(String propertyName, String queryName);


    /**
     * This creates a filter that constructs an "in" style subquery with the
     * given property to be filtered on and subquery. subQueryProperty is the
     * property in the subquery which relates to the property being filtered
     * on. The subquery must be a fully qualified query name of a query defined
     * in a PDL file somewhere.
     **/

    Filter in( String property, String subQueryProperty, String queryName );


    /**
     * This creates a filter that constructs a "not in" style subquery with the
     * given property and subquery. The subquery must be a fully qualified
     * query name of a query defined in a PDL file somewhere.
     **/

    Filter notIn(String propertyName, String queryName);


    /**
     *  This creates and returns a filter that can be used to AND
     *  existing filters together.  Whenever addFilter is called
     *  on the filters that is returned, it ANDs the passed in
     *  Filters with the existing filter
     */
    CompoundFilter and();


    /**
     *  This creates and returns a filter that can be used to OR
     *  existing filters together.  Whenever addFilter is called
     *  on the filters that is returned, it ORs the passed in
     *  Filters with the existing filter
     */
    CompoundFilter or();

}
