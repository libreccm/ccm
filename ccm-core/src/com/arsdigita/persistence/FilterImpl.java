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

import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.oql.Expression;

import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Filter is used to restrict the results of a query.  Filters can
 * be combined and manipulated to create complex queries.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Id: FilterImpl.java 738 2005-09-01 12:36:52Z sskracic $
 */
abstract class FilterImpl implements Filter {

    private static final Logger m_log =
        Logger.getLogger(Filter.class.getName());

    private Map m_bindings = new HashMap();

    protected FilterImpl() {}

    /**
     * For subclasses of FilterImpl, this method is not used in generating the
     * actual filter. It seems reasonable for subclasses of FilterImpl to
     * implement this method anyways, if only for toString or other debugging
     * purposes.
     */
    public abstract String getConditions();

    /**
     * For instances of FilterImpl, DataQueryImpl and CompoundFilterImpl use
     * this method to generate oql filter conditions.
     */
    protected abstract Expression makeExpression(DataQueryImpl query,
                                                 Map bindings);

    /**
     * Returns a name that is safe to use for binding the passed in property
     * name.
     **/
    static final String bindName(String propertyName) {
        StringBuffer result = new StringBuffer(propertyName.length());
        for (int i = 0; i < propertyName.length(); i++) {
            char c = propertyName.charAt(i);
            switch (c) {
            case '.':
                result.append('_');
                break;
            case ' ':
            case '\t':
            case '\n':
            case '\r':
            case '(':
            case ')':
                break;
            default:
                result.append(c);
                break;
            }
        }

        return result.toString();
    }

    static String createNullString(String comparator, String variableName) {
        if (comparator.indexOf("!") > -1 || comparator.indexOf("<>") > -1) {
            return variableName + " is not null";
        } else {
            return variableName + " is null";
        }
    }

    /**
     *  Creates a new filter with the given conditions
     *
     *  @param conditions The SQL conditions that make up the heart of the
     *                    filter.  Conditions must not be null or the
     *                    empty string
     */
    public static Filter simple(String conditions) {
        if (conditions == null || conditions.equals("")) {
            throw  new PersistenceException("The filter conditions must not " +
                                            "be null or the empty string");
        }

        return new SimpleFilter(conditions);
    }


    /**
     *  This takes an attribute and returns a filter that is guranteed to
     *  return false
     */
    private static Filter filterForNullValue(String attribute,
                                             boolean trueForAllIfValueIsNull) {
        // we do not want to return null so we return something
        // that is either always true or always false
        if (trueForAllIfValueIsNull) {
            return new SimpleFilter(null);
        } else {
            // We are setting it to both null and not null because we know
            // that it is not possible to have both.
            return simple(createNullString("!=", attribute) +
                          " and " + createNullString("=", attribute));
        }
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
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     */
    protected static Filter equals(String attribute, Object value) {
        return EqualsFilter.eq(attribute, value);
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  != 'value.toString()'</code>" unless the value is an integer
     *  (in which case it creates "</code>attribute !=
     *  value.toString()</code>") or the developer is using oracle and
     *  the value is null.  In this case, it would create
     *  "<code>attribute is not null</code>".
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     */
    protected static Filter notEquals(String attribute, Object value) {
        return EqualsFilter.notEq(attribute, value);
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
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
    protected static Filter lessThan(String attribute, Object value,
                                     boolean trueForAllIfValueIsNull) {
        return createComparisonFilter(attribute, value,
                                      trueForAllIfValueIsNull, "<");
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
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
    protected static Filter lessThanEquals(String attribute, Object value,
                                           boolean trueForAllIfValueIsNull) {
        return createComparisonFilter(attribute, value,
                                      trueForAllIfValueIsNull, "<=");
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  > value</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of a NO-OP (true)
     *         or 1==2 (false)
     */
    protected static Filter greaterThan(String attribute, Object value,
                                        boolean trueForAllIfValueIsNull) {
        return createComparisonFilter(attribute, value,
                                      trueForAllIfValueIsNull, ">");
    }


    /**
     *  This creates the appropriate SQL for the given attribute and
     *  passed in value.  It creates a filter for "<code>attribute
     *  >= value</code>" unless the developer is using oracle and
     *  the value is null.  In this case, it uses the parameter
     *  <code>trueForAllIfValueIsNull</code> to determine how to change
     *  the query to work.
     *
     *  @param attribute The name of the attribute to bind with the value
     *  @param value The value for the specified attribute
     *  @param trueForAllIfValueIsNull This specifies whether a value
     *         of null should be the equivalent of a NO-OP (true)
     *         or 1==2 (false)
     */
    protected static Filter greaterThanEquals(String attribute, Object value,
                                              boolean trueForAllIfValueIsNull) {
        return createComparisonFilter(attribute, value,
                                      trueForAllIfValueIsNull, ">=");
    }


    /**
     *  This actually creates the filter for lessThan, lessThanEquals,
     *  greaterThan, and greaterThanEquals
     */
    private static Filter createComparisonFilter
        (String attribute, Object value, boolean trueForAllIfValueIsNull,
         String comparator) {
        if (value == null) {
            return filterForNullValue(attribute, trueForAllIfValueIsNull);
        } else {
            String bind = bindName(attribute);
            Filter filter = simple(attribute + " " + comparator + " :" + bind);
            filter.set(bind, value);
            return filter;
        }

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
    protected static Filter startsWith(String attribute, String value,
                                       boolean trueForAllIfValueIsNull) {
        if (value == null) {
            return filterForNullValue(attribute, trueForAllIfValueIsNull);
        } else {
            String bind = bindName(attribute);
            Filter filter = simple(attribute + " like :" + bind + " || '%'");
            filter.set(bind, value);
            return filter;
        }
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
    protected static Filter endsWith(String attribute, String value,
                                     boolean trueForAllIfValueIsNull) {
        if (value == null) {
            return filterForNullValue(attribute, trueForAllIfValueIsNull);
        } else {
            String bind = bindName(attribute);
            Filter filter = simple(attribute + " like '%' || :" + bind);
            filter.set(bind, value);
            return filter;
        }
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
    protected static Filter contains(String attribute, String value,
                                     boolean trueForAllIfValueIsNull) {
        if (value == null) {
            return filterForNullValue(attribute, trueForAllIfValueIsNull);
        } else {
            String bind = bindName(attribute);
            Filter filter = simple(attribute + " like '%' || :" + bind +
                                   " || '%'");
            filter.set(bind, value);
            return filter;
        }
    }


    /**
     * This creates a filter that constructs an "in" style subquery with the
     * given property and subquery. The subquery must be a fully qualified
     * query name of a query defined in a PDL file somewhere.
     **/

    protected static Filter in(Root root, String propertyName,
                               String queryName) {
        if (propertyName == null || propertyName.equals("") ||
            queryName == null || queryName.equals("")) {
            throw new IllegalArgumentException
		("The propertyName and queryName must be non empty.");
        }

        return new InFilter(root, propertyName, null, queryName);
    }

    /**
     * This creates a filter that constructs an "in" style subquery with the
     * given property to be filtered on and subquery. subQueryProperty is the
     * property in the subquery which relates to the property being filtered
     * on. The subquery must be a fully qualified query name of a query defined
     * in a PDL file somewhere.
     **/

    protected static Filter in(Root root, String property,
                               String subQueryProperty, String queryName) {
        if (property == null || property.equals("") ||
            subQueryProperty == null || subQueryProperty.equals("") ||
            queryName == null || queryName.equals("") ) {
            throw new IllegalArgumentException
                ("The property, subQueryProperty and queryName must be " +
                 "non empty.");
        }

        return new InFilter(root, property, subQueryProperty, queryName);
    }

    /**
     * This creates a filter that constructs a "not in" style subquery with the
     * given property and subquery. The subquery must be a fully qualified
     * query name of a query defined in a PDL file somewhere.
     **/

    protected static Filter notIn(Root root, String propertyName,
                                  String queryName) {
        if (propertyName == null || propertyName.equals("") ||
            queryName == null || queryName.equals("")) {
            throw new IllegalArgumentException
		("The propertyName and queryName must be non empty.");
        }

	final InFilter in = new InFilter(root, propertyName, null, queryName);
        return new SimpleFilter("not " + in.getConditions());
    }


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
    public Filter set(String parameterName, Object value) {
        if (m_bindings.containsKey(parameterName)) {
	    if (m_log.isEnabledFor(Level.WARN)) {
		m_log.warn("The existing filter already contains a parameter " +
			   "named \"" + parameterName + "\".  Overwriting the" +
			   " the old value " + m_bindings.get(parameterName) +
			   " with " + value);
	    }
        } 
        m_bindings.put(parameterName, value);
        return this;
    }


    /**
     *  This returns the bindings for this Filter.  That is, it returns
     *  a map of key (variable name) - value (variable value) pairs
     *
     *  @return a map of key (variable name) - value (variable value) pairs
     */
    public Map getBindings() {
        return m_bindings;
    }


    /**
     *  This adds an item to the bindings.  <b>This should ONLY be used
     *  by classes extending FilterImpl</b>
     *
     *  @param key The key (attribute name) for the new binding
     *  @param value the value for the new binding
     */
    protected void addBinding(String key, Object value) {
        m_bindings.put(key, value);
    }


    /**
     *  This a Map to the bindings.  <b>This should ONLY be used
     *  by classes extending FilterImpl</b>
     *  @param bindings A map of attribute/value pairs to add to the
     *  bindings of the filter
     */
    protected void addBindings(Map bindings) {
        m_bindings.putAll(bindings);
    }

}
