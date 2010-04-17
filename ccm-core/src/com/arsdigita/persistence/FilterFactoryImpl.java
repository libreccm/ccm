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
 * FilterFactoryImpl actually hands out the requested filters
 *
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Id: FilterFactoryImpl.java 287 2005-02-22 00:29:02Z sskracic $
 */

class FilterFactoryImpl implements FilterFactory {

    private Session m_ssn;

    FilterFactoryImpl(Session ssn) {
        m_ssn = ssn;
    }

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
    public Filter simple(String sql) {
        return FilterImpl.simple(sql);
    }


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
    public Filter equals(String attribute, Object value) {
        return FilterImpl.equals(attribute, value);
    }


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
    public Filter notEquals(String attribute, Object value) {
        return FilterImpl.notEquals(attribute, value);
    }


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
    public Filter lessThan(String attribute, Object value,
                           boolean trueForAllIfValueIsNull) {
        return FilterImpl.lessThan(attribute, value, trueForAllIfValueIsNull);
    }


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
    public Filter lessThanEquals(String attribute, Object value,
                                 boolean trueForAllIfValueIsNull) {
        return FilterImpl.lessThanEquals(attribute, value,
                                         trueForAllIfValueIsNull);
    }


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
    public Filter greaterThan(String attribute, Object value,
                              boolean trueForAllIfValueIsNull) {
        return FilterImpl.greaterThan(attribute, value, trueForAllIfValueIsNull);
    }


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
    public Filter greaterThanEquals(String attribute, Object value,
                                    boolean trueForAllIfValueIsNull) {
        return FilterImpl.greaterThanEquals(attribute, value,
                                            trueForAllIfValueIsNull);
    }


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
    public Filter startsWith(String attribute, String value,
                             boolean trueForAllIfValueIsNull) {
        return FilterImpl.startsWith(attribute, value, trueForAllIfValueIsNull);
    }


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
    public Filter endsWith(String attribute, String value,
                           boolean trueForAllIfValueIsNull) {
        return FilterImpl.endsWith(attribute, value, trueForAllIfValueIsNull);
    }


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
    public Filter contains(String attribute, String value,
                           boolean trueForAllIfValueIsNull) {
        return FilterImpl.contains(attribute, value, trueForAllIfValueIsNull);
    }


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
     *  <p>
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
    public Filter compare(String expressionOne, int comparator,
                          String expressionTwo) {
        String stringComparator = null;
        boolean notEqualsValue = false;

        switch (comparator) {
        case EQUALS:
            stringComparator = "=";
            break;
        case NOT_EQUALS:
            stringComparator = "!=";
            notEqualsValue = true;
            break;
        case GREATER_THAN:
            stringComparator = ">";
            notEqualsValue = true;
            break;
        case LESS_THAN:
            stringComparator = "<";
            notEqualsValue = true;
            break;
        case GREATER_THAN_EQUALS:
            stringComparator = ">=";
            break;
        case LESS_THAN_EQUALS:
            stringComparator = "<=";
            break;
        case STARTS_WITH:
            return simple(expressionOne + " like " + expressionTwo + " || '%'");
        case ENDS_WITH:
            return simple(expressionOne + " like '%' || " + expressionTwo);
        case CONTAINS:
            return simple(expressionOne + " like '%' || " +
                          expressionTwo + " || '%'");
        default:
            throw new PersistenceException("The comparator that you have " +
                                           "passed in is not value");
        }

        String expressionOneNotNull =
            FilterImpl.createNullString(stringComparator, expressionOne);
        String expressionTwoNotNull =
            FilterImpl.createNullString(stringComparator, expressionTwo);

        if (notEqualsValue) {
            // in this case, we have to make sure that they are not equal
            return simple("(" + expressionOne + " " + stringComparator + " "
                          + expressionTwo +
                          " or (" + expressionOneNotNull + " and not " +
                          expressionTwoNotNull + ") or (" +
                          expressionTwoNotNull + " and not " +
                          expressionOneNotNull + "))");
        } else {
            return simple("(" + expressionOne + " " + stringComparator + " " +
                          expressionTwo + " or (" +
                          expressionOneNotNull + " and " +
                          expressionTwoNotNull + "))");
        }
    }


    /**
     * This creates a filter that constructs an "in" style subquery with the
     * given property and subquery. The subquery must be a fully qualified
     * query name of a query defined in a PDL file somewhere.
     **/
    public Filter in(String propertyName, String queryName) {
        return FilterImpl.in(m_ssn.getRoot(), propertyName, queryName);
    }


    /**
     * This creates a filter that constructs an "in" style subquery with the
     * given property to be filtered on and subquery. subQueryProperty is the
     * property in the subquery which relates to the property being filtered
     * on. The subquery must be a fully qualified query name of a query defined
     * in a PDL file somewhere.
     **/

    public Filter in( String property,
                      String subQueryProperty,
                      String queryName ) {
        return FilterImpl.in(m_ssn.getRoot(), property, subQueryProperty,
                             queryName);
    }


    /**
     * This creates a filter that constructs a "not in" style subquery with the
     * given property and subquery. The subquery must be a fully qualified
     * query name of a query defined in a PDL file somewhere.
     **/
    public Filter notIn(String propertyName, String queryName) {
        return FilterImpl.notIn(m_ssn.getRoot(), propertyName, queryName);
    }


    /**
     *  This creates and returns a filter that can be used to AND
     *  existing filters together.  Whenever addFilter is called
     *  on the filters that is returned, it ANDs the passed in
     *  Filters with the existing filter
     */
    public CompoundFilter and() {
        return CompoundFilterImpl.and();
    }


    /**
     *  This creates and returns a filter that can be used to OR
     *  existing filters together.  Whenever addFilter is called
     *  on the filters that is returned, it ORs the passed in
     *  Filters with the existing filter
     */
    public CompoundFilter or() {
        return CompoundFilterImpl.or();
    }
}
