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
 * Decorate a data query so that its behavior can be changed and additional
 * methods can be added to a stock data query.
 *
 * @author David Lutterkort
 * @version $Id: DataQueryDecorator.java 1045 2005-12-09 13:41:22Z sskracic $
 */
public class DataQueryDecorator implements DataQuery {

    String versionId = "$Id: DataQueryDecorator.java 1045 2005-12-09 13:41:22Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private DataQuery m_dq;

    /**
     * Decorate the data query <code>dq</code>.
     *
     * @param dq the data query to decorate
     */
    public DataQueryDecorator(DataQuery dq) {
        m_dq = dq;
    }

    /**
     * Retrieve the query with name <code>queryName</code> and decorate
     * it.
     *
     * @param queryName the name of the data query to decorate.
     */
    public DataQueryDecorator(String queryName) {
        this(SessionManager.getSession().retrieveQuery(queryName));
    }

    public CompoundType getType() {
        return m_dq.getType();
    }

    public boolean hasProperty(String propertyName) {
        return m_dq.hasProperty(propertyName);
    }

    // RowSequence methods
    public void rewind() {
        m_dq.rewind();
    }

    public Object get(String propertyName) {
        return m_dq.get(propertyName);
    }

    public int getPosition() {
        return m_dq.getPosition();
    }

    public boolean next() {
        return m_dq.next();
    }

    public long size() {
        return m_dq.size();
    }

    // DataQuery methods
    public void reset() {
        m_dq.reset();
    }

    public boolean first() throws PersistenceException {
        return m_dq.first();
    }

    public boolean isEmpty() throws PersistenceException {
        return m_dq.isEmpty();
    }

    public boolean isBeforeFirst() throws PersistenceException {
        return m_dq.isBeforeFirst();
    }

    public boolean isFirst() throws PersistenceException {
        return m_dq.isFirst();
    }

    public boolean isLast() throws PersistenceException {
        return m_dq.isLast();
    }

    public boolean isAfterLast() throws PersistenceException {
        return m_dq.isAfterLast();
    }

    public boolean last() throws PersistenceException {
        return m_dq.last();
    }

    public boolean previous() throws PersistenceException {
        return m_dq.previous();
    }

    public void addPath(String path) {
        m_dq.addPath(path);
    }

    public Filter setFilter(String conditions) {
        return m_dq.setFilter(conditions);
    }

    public Filter addFilter(String conditions) {
        return m_dq.addFilter(conditions);
    }

    public Filter addFilter(Filter filter) {
        return m_dq.addFilter(filter);
    }

    public boolean removeFilter(Filter filter) {
        return m_dq.removeFilter(filter);
    }

    public Filter addInSubqueryFilter(String propertyName,
                                      String subqueryName) {
        return m_dq.addInSubqueryFilter(propertyName, subqueryName);
    }

    public Filter addInSubqueryFilter(String property,
                                      String subQueryProperty,
                                      String subqueryName) {
        return m_dq.addInSubqueryFilter(property, subQueryProperty,
                                        subqueryName);
    }

    public Filter addNotInSubqueryFilter(String propertyName,
                                         String subqueryName) {
        return m_dq.addNotInSubqueryFilter(propertyName, subqueryName);
    }

    public Filter addEqualsFilter(String attribute, Object value) {
        return m_dq.addEqualsFilter(attribute, value);
    }

    public Filter addNotEqualsFilter(String attribute, Object value) {
        return m_dq.addNotEqualsFilter(attribute, value);
    }

    public void clearFilter() {
        m_dq.clearFilter();
    }

    public FilterFactory getFilterFactory() {
        return m_dq.getFilterFactory();
    }

    public void setOrder(String order) throws PersistenceException {
        m_dq.setOrder(order);
    }

    public void addOrder(String order) throws PersistenceException {
        m_dq.addOrder(order);
    }

    public void addOrderWithNull(String orderOne, Object orderTwo, 
                                 boolean isAscending) {
        m_dq.addOrderWithNull( orderOne, orderTwo, isAscending );
    }

    public void clearOrder() {
        m_dq.clearOrder();
    }

    public void setParameter(String parameterName, Object value) {
        m_dq.setParameter(parameterName, value);
    }

    public Object getParameter(String parameterName) {
        return m_dq.getParameter(parameterName);
    }

    public void setRange(Integer beginIndex) {
        m_dq.setRange(beginIndex);
    }

    public void setRange(Integer beginIndex, Integer endIndex) {
        m_dq.setRange(beginIndex, endIndex);
    }

    public Map getPropertyValues() {
        return m_dq.getPropertyValues();
    }

    public void setReturnsUpperBound(int upperBound) {
        m_dq.setReturnsUpperBound(upperBound);
    }

    public void setReturnsLowerBound(int lowerBound) {
        m_dq.setReturnsLowerBound(lowerBound);
    }

    public void alias(String fromPrefix, String toPrefix) {
        m_dq.alias(fromPrefix, toPrefix);
    }

    public void close() {
        m_dq.close();
        // FIXME: Should we null m_dq at this point ?
    }

    public String toString() {
        return m_dq.toString();
    }

    protected DataQuery getDataQuery() {
        return m_dq;
    }

    public void setOption(String name, Object value) {
        m_dq.setOption(name, value);
    }

    public Object getOption(String name) {
        return m_dq.getOption(name);
    }
}
