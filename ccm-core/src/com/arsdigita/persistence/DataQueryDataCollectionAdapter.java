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
import com.redhat.persistence.metadata.Property;

/**
 *  Adapter to make a data query look
 * like a data collection. If your data query looks something like the
 * following in PDL:
 *
 * <pre> <code>
 * query ItemsInFolder {
 *   ContentItem item;
 *   ContentType type;
 *   do {
 *     select i.item_id, i.name, i.version,
 *            t.type_id, t.label
 *     from cms_items i, content_types t
 *     where i.type_id = t.type_id
 *   } map {
 *     item.id = i.item_id;
 *     item.name = i.name;
 *     ...
 *     type.id = t.type_id;
 *     type.label = t.label;
 *   }
 * </code></pre>
 * and <code>dq</code> is a <code>DataQuery</code> constructed from that
 * PDL description, then you can get a dat collection of items through
 *
 * <pre><code>
 *    new DataQueryDataCollectionAdapter(dq, "item");
 * </code></pre>
 * and a new data collection of content types through
 * <pre><code>
 *    new DataQueryDataCollectionAdapter(dq, "type");
 * </code></pre>
 *
 * <b>Warning:</b> Note that all manipulations of the data collection also
 * change the underlying data query. The constructed object is not a
 * cursor, it just wraps the data query that was passed in.
 *
 * @author David Lutterkort
 * @version $Id: DataQueryDataCollectionAdapter.java 904 2005-09-22 05:57:11Z apevec $
 */
public class DataQueryDataCollectionAdapter extends DataQueryDecorator
    implements DataCollection {

    private ObjectType m_type;
    private String m_dataObjectProperty;

    /**
     * Create a data collection that uses the objects with name
     * <code>dataObjectProperty</code> from the data query as its data
     * objects.
     *
     * @param dq the data query from which data objects are taken
     * @param dataObjectProperty the name of the data objects in the query
     */
    public DataQueryDataCollectionAdapter(DataQuery dq,
                                          String dataObjectProperty) {
        super(dq);
        m_dataObjectProperty = dataObjectProperty;

        if ( !(dataObjectProperty == null || "".equals(dataObjectProperty)) ) {
            alias("*", m_dataObjectProperty + ".*");
        }
    }

    /**
     * Create a data collection that uses the objects with name
     * <code>dataObjectProperty</code> from the data query as its data
     * objects. The data query with name <code>queryName</code> is
     * retrieved and used as the source for data objects.
     *
     * @param queryName the name of the data query from which data objects
     * are taken
     * @param dataObjectProperty the name of the data objects in the query
     */
    public DataQueryDataCollectionAdapter(String queryName,
                                          String dataObjectProperty) {
        super(queryName);
        // SF patch [ 1160550 ] BugFix in DataQueryDomainCollectionAdapter
        // populate object type prior to first call to next()
        // essential if you need to filter DataCollection with PermissionService.filterObjects()
        com.redhat.persistence.metadata.ObjectType type = SessionManager.getSession().getRoot().getObjectType(queryName);
        Property prop = type.getProperty(dataObjectProperty);
        String propertyType = prop.getType().getQualifiedName();
        m_type = SessionManager.getMetadataRoot().getObjectType(propertyType);

        if (dataObjectProperty == null) { dataObjectProperty = ""; }
        m_dataObjectProperty = dataObjectProperty;

        if ( !(dataObjectProperty == null || "".equals(dataObjectProperty)) ) {
            alias("*", m_dataObjectProperty + ".*");
        }
    }

    public DataObject getDataObject() {
        return (DataObject) get(m_dataObjectProperty);
    }

    public ObjectType getObjectType() {
        // FIXME: This is lame and will fail horribly if called before the
        // first call to next(). But I am not master of the metdata system
        // enough to figure out how we could get the object type directly
        // from the data query ...
        if ( m_type == null ) {
            m_type = getDataObject().getObjectType();
        }
        return m_type;
    }

    /**
     * Retrieve an attribute of the underlying query. Typically,
     * this method will be used to retrieve link attributes.
     *
     * @param propertyName the name of the link attribute to retrieve
     * @return the valur of the link attribute (may be null)
     */
    public Object getLinkAttribute(String propertyName) {
        return get("link." + propertyName);
    }

    // Adapt DataQuery methods. All property names need to be replaced by
    // m_dataObjectProperty + "." + name


    public Object get(String propertyName) {
        return super.get(propertyName);
    }

    public Filter setFilter(String conditions) {
        return super.setFilter(conditions);
    }

    public Filter addFilter(String conditions) {
        return super.addFilter(conditions);
    }

    public Filter addFilter(Filter filter) {
        return super.addFilter(filter);
    }

    public Filter addInSubqueryFilter(String propertyName,
                                      String subqueryName) {
        return super.addInSubqueryFilter(propertyName, subqueryName);
    }

    public Filter addEqualsFilter(String attribute, Object value) {
        return super.addEqualsFilter(attribute, value);
    }

    public Filter addNotEqualsFilter(String attribute, Object value) {
        return super.addNotEqualsFilter(attribute, value);
    }

    public void setOrder(String order) throws PersistenceException {
        super.setOrder(order);
    }

    public void addOrder(String order) throws PersistenceException {
        super.addOrder(order);
    }

    public String toString() {
        return "DataQueryDataCollectionAdapter for {" + super.toString() + "}";
    }

    public boolean contains(OID oid) {
        throw new UnsupportedOperationException();
    }

    public boolean contains(DataObject dobj) {
        throw new UnsupportedOperationException();
    }
}
