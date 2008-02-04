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

import com.arsdigita.persistence.metadata.ObjectType;

/**
 * This is used to represent the relationship between two objects.
 *
 * <p>
 *
 * It is important to note that when the deprecated methods in this class
 * are removed, <font color="red"><b>this class will no longer extend
 * DataCollection</b></font>.  If you want something that extends
 * DataCollection, use DataAssociationCursor instead.
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @author <a href="mailto:randyg@alum.mit.edu">randyg@alum.mit.edu</a>
 * @version $Revision: #12 $ $Date: 2004/08/16 $ */

public interface DataAssociation extends DataCollection {
    // when this no longer extends DataCollection, make sure it
    // still has get/set Query and get/set Source

    String versionId = "$Id: DataAssociation.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    /**
     * Adds <i>object</i> to the persistent association.
     * Note: The object does NOT truly become part of the association until
     * save() is called on the association's parent object. This means, for
     * example, that cursor() will return the same cursor that it did before
     * any objects are added.
     *
     * @param object The object to add.
     **/
    DataObject add(DataObject object);


    /**
     * Removes all the objects in the persistent association.
     **/
    void clear();


    /**
     * Returns a data collection containing all the objects in this
     * association.
     **/
    DataCollection getDataCollection();


    /**
     * Returns a data association iterator that allows users to iterate
     * through all of the data associations
     **/
    DataAssociationCursor getDataAssociationCursor();


    /**
     * Returns a data association iterator that allows users to iterate
     * through all of the data associations
     *
     * This is a convenience method for getDataAssociationCursor()
     */
    DataAssociationCursor cursor();


    /**
     * Calls get("link." + name).
     *
     * @param name The name of the link property.
     *
     * @deprecated Use {@link #cursor()}.getLinkProperty
     * @return The property value.
     */
    Object getLinkProperty(String name);


    /**
     * Removes the object associated with the current position in the
     * collection.
     * @deprecated Use {@link #remove(DataObject object)} or
     * {@link #cursor()}, loop through the object
     * and then call remove()
     */
    void remove();


    /**
     * Removes <i>object</i> from the collection.
     *
     * Note: The object does NOT truly get removed from the association until
     * save() is called on the association's parent object. This means, for
     * example, that cursor() will return the same cursor that it did before
     * any objects are added.
     * @param object The object to remove.
     */
    void remove(DataObject object);


    /**
     * Removes <i>object</i> from the collection.
     *
     * Note: The object does NOT truly get removed from the association until
     * save() is called on the association's parent object. This means, for
     * example, that cursor() will return the same cursor that it did before
     * any objects are added.
     * @param oid The OID of the object to remove.
     */
    void remove(OID oid);


    /**
     * Returns true if the collection has been modified.
     *
     * @return True if modified, false otherwise.
     */
    boolean isModified();


    /*
     *  Deprecated methods from DataQuery
     */


    /**
     * Rewinds the data query to the beginning, i.e. it's as if next() was
     * never called.
     * @deprecated use {@link #cursor()}.rewind()
     **/
    void rewind();


    /**
     * Returns the data query to its initial state by rewinding it and
     * clearing any filters or ordering.
     * @deprecated use {@link #cursor()}.reset()
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
     * @deprecated use {@link #cursor()}.first()
     **/
    boolean first() throws PersistenceException;


    /**
     * Returns the value of the <i>propertyName</i> property associated with
     * the current position in the query.
     *
     * @param propertyName the name of the property
     *
     * @return the value of the property
     * @deprecated use {@link #cursor()}.get()
     **/
    Object get(String propertyName) throws PersistenceException;


    /**
     * Returns the current position within the query. The first position is 1.
     *
     * @return the current position; 0 if there is no current position
     * @deprecated use {@link #cursor()}.getPosition()
     **/
    int getPosition() throws PersistenceException;


    /**
     * Returns true if the query has no rows.
     *
     * @return true if the query has no rows; false otherwise
     * @deprecated use {@link #cursor()}.isEmpty()
     **/
    boolean isEmpty() throws PersistenceException;


    /**
     * Indicates whether the cursor is on the first row of the query.
     *
     * @return true if the cursor is on the first row; false otherwise
     * @deprecated use {@link #cursor()}.isFirst()
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
     *
     * @deprecated use {@link #cursor()}.isLast()
     *
     * @return True if the cursor is on the last row, false otherwise.
     **/
    boolean isLast() throws PersistenceException;


    /**
     * Moves the cursor to the last row in the query.
     * <font color=red>Not implemented yet.</font>
     *
     * @return true if the new current row is valid; false if there are no
     *         rows in the query
     * @exception PersistenceException Always thrown!
     * @deprecated use {@link #cursor()}.last()
     **/
    boolean last() throws PersistenceException;


    /**
     * Moves the cursor to the next row in the query.
     *
     * @return true if the new current row is valid; false if there are no
     *         more rows
     * @deprecated use {@link #cursor()}.next()
     **/
    boolean next() throws PersistenceException;


    /**
     * Moves to the previous row in the query.
     * <font color=red>Not implemented yet.</font>
     *
     * @return true if the new current row is valid; false otherwise
     * @exception PersistenceException Always thrown!
     * @deprecated use {@link #cursor()}.previous()
     **/
    boolean previous() throws PersistenceException;


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
     * @deprecated see #addFilter
     *
     * @return the newly created filter for this query
     * @deprecated use {@link #cursor()}.setFilter(String conditions)
     **/
    Filter setFilter(String conditions);


    /**
     * Adds the conditions to the filter that will be used on this
     * query.  If a filter already exists, this alters the filter
     * object and returns the altered object.  If one does not
     * already exist, it creates a new filter.  When adding
     * filters, the user should not use the same parameter name
     * in multiple filters.  That is, the following will not work
     *
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority < :bound");
     * filter.set("bound", new Integer(3));
     * filter = query.addFilter("priority < :bound");
     * filter.set("bound", new Integer(8));
     * </code>
     * </pre>
     * The above actually evaluates to
     * <code>"priority < 3 and priority > 3"</code>
     * which is clearly now what the developer wants.
     * <p>
     * The following will work.
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority < :lowerBound");
     * filter.set("lowerBound", new Integer(3));
     * filter = query.addFilter("priority < :upperBound");
     * filter.set("upperBound", new Integer(8));
     * </code>
     * </pre>
     * It is actually the same as
     * <pre>
     * <code>
     * Filter filter = query.addFilter("priority < :lowerBound
     *                                  and priority > :uperBound");
     * filter.set("upperBound", new Integer(8));
     * filter.set("lowerBound", new Integer(3));
     * </code>
     * </pre>
     *
     * @param conditions The conditions for the filter.  This is a string
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
     *        <p>
     *        If there is already a filter that exists for this query
     *        then the passed in conditions are added to the current
     *        conditions with an AND like <code>(&lt;current conditions&gt;)
     *        and (&lt; passed in conditions&gt;)</code>
     *
     * @return The filter that has just been added to the query
     * @deprecated use {@link #cursor()}.addFilter(String conditions)
     *
     */
    Filter addFilter(String conditions);


    /**
     * Clears the current filter for the data query.
     * @deprecated use {@link #cursor()}.clearFilter()
     **/
    void clearFilter();


    /**
     * Set the order in which the result of this query will be returned. The
     * string passed is a standard SQL order by clause specified in terms of
     * the properties. For example:
     *
     * <blockquote><pre>
     * query.setOrder("creationDate desc, id");
     * </pre></blockquote>
     * @deprecated see #addOrder
     * @deprecated use {@link #cursor()}.setOrder(String order)
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
     * @deprecated use {@link #cursor()}.addOrder(String order)
     **/
    void addOrder(String order) throws PersistenceException;


    /**
     * Clears the current order clause for the data query.
     * @deprecated use {@link #cursor()}.clearOrder()
     **/
    void clearOrder();


    /**
     * Returns the size of this query (i.e. the number of rows that
     * are returned).
     * @deprecated use {@link #cursor()}.size()
     **/
    long size() throws PersistenceException;


    /**
     * Allows a user to bind a parameter within a named query.
     *
     * @param parameterName The name of the parameter to bind
     * @param value The value to assign to the parameter
     *
     * @deprecated use {@link #cursor()}.setParameter(Strin
     * parameterName, Object value)}
     */
    void setParameter(String parameterName, Object value);


    /**
     * Allows a caller to get a parameter value for a parameter that
     * has already been set
     *
     * @param parameterName The name of the parameter to retrieve
     * @return This returns the object representing the value of the
     * parameter specified by the name or "null" if the parameter value
     * has not yet been set.
     * @deprecated use {@link #cursor()}.getParameter(String parameterName)
     */
    Object getParameter(String parameterName);


    /**
     * Explicitly closes this DataQuery.
     * Query should automatically be closed when next
     * returns false, but this method should be
     * explicitly called in the case where all of the data in a query
     * is not needed (e.g. a "while (next())" loop is exited early or
     * only one value is retrieved with if (next()) {...}).
     * @deprecated use {@link #cursor()}.close()
     */
    void close();


    /**
     *
     *  Deprecated methods from DataCollection
     *
     */


    /**
     * Returns a data object for the current position in the collection.
     *
     * @return A DataObject.
     * @deprecated use {@link #cursor()}.getDataObject()
     **/

    DataObject getDataObject();

    /**
     * Returns the object type of the data collection.
     *
     * @return The object type of the data collection.
     * @deprecated use {@link #cursor()}.getObjectType()
     **/

    ObjectType getObjectType();

}
