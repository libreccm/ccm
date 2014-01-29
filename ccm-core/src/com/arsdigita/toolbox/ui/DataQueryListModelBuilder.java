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
package com.arsdigita.toolbox.ui;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.PageState;
import com.arsdigita.util.LockableImpl;

import java.util.NoSuchElementException;

/**
 * Builds a {@link ListModel} from a {@link DataQuery}.  By specifying the name
 * of the key and value columns, a {@link DataQuery} object can be transformed
 * into a {@link ListModel}.
 *
 * To use this class, override the {@link #getDataQuery(PageState ps)} method. This
 * method prepares a query, applying any additional filters, so that a call
 * to <code>next()</code> will return the first row of the result set.
 *
 * @author Michael Pih 
 * @author Stanislav Freidin 
 *
 * @version $Revision: #11 $ $Date: 2004/08/16 $
 */
public abstract class DataQueryListModelBuilder extends LockableImpl
    implements ListModelBuilder {

    private String m_keyName;
    private String m_valueName;

    /**
     * Construct a new <code>DataQueryListModelBuilder</code>
     *
     * @param keyName the name of the attribute in the query that contains
     *   the primary key of a row
     * @param valueName the name of the attribute in the query that contains
     *   the value. The value will be passed directly to the
     *   {@link com.arsdigita.bebop.list.ListCellRenderer} of the {@link List}. Usually,
     *   the value will be a primitive (such as a <code>String</code> or
     *   a <code>Date</code>); the default list cell renderer will call
     *   <code>.toString()</code> on the value and display it as a
     *   link.  If null, the ListModel will return a Map of all the data in the
     *   row.
     */
    public DataQueryListModelBuilder(String keyName, String valueName) {
        m_keyName = keyName;
        m_valueName = valueName;
    }

    /**
     * Construct a new <code>DataQueryListModelBuilder</code> that returns
     * a Map containing all the data in each row (rather than a single column).
     *
     * @param keyName the name of the Property that stores the row's primary
     *                key.
     */
    public DataQueryListModelBuilder(String keyName) {
        this(keyName, null);
    }

    /**
     * Returns the data query for the current request. Child classes should
     * override this method to construct the appropriate data query (or
     * obtain a named query), and then apply the right filters and sorts
     * to it.
     *
     * @param ps represents the current request
     * @return the {@link DataQuery} for the request
     */
    protected abstract DataQuery getDataQuery(PageState ps);

    public ListModel makeModel(List l, PageState ps) {
        DataQuery dq = getDataQuery(ps);
        return new DataQueryListModel(dq, m_keyName, m_valueName);
    }

    /**
     * Encapsulates a {@link DataQuery} as a {@link ListModel}.
     * Uses the key and value attributes as specified in the
     * <code>DataQueryListModelBuilder</code> constructor.
     */
    public class DataQueryListModel implements ListModel {

        private DataQuery m_dataQuery;
        private String m_keyName;
        private String m_valueName;


        /**
         * Construct a new <code>DataQueryListModel</code>
         *
         * @param dq the {@link DataQuery} that will be used to supply the list with data
         * @param keyName the name of the attribute which contains the primary key for
         *   each row of the query
         * @param valueName the name of the attribute which contains the value for
         *   each row of the query
         */
        public DataQueryListModel(DataQuery dq, String keyName, String valueName) {
            m_dataQuery = dq;
            m_keyName   = keyName;
            m_valueName = valueName;
        }

        /**
         * Advance to the next row
         *
         * @return false if the query was exhausted, true otherwise
         */
        public boolean next() throws NoSuchElementException {
            return m_dataQuery.next();
        }

        /**
         * Return the value of the attribute specified in the <code>valueName</code>
         * constructor parameter. This value will most likely be used by the
         * {@link com.arsdigita.bebop.list.ListCellRenderer} to display a label.
         *
         * @return the value of the current row
         * @see com.arsdigita.bebop.list.ListCellRenderer
         */
        public Object getElement() {
            if (m_valueName == null) {
                return m_dataQuery.getPropertyValues();
            } else {
                return m_dataQuery.get(m_valueName);
            }
        }

        /**
         * Returns the string representation of the primary key of the current row.
         * The primary key is the value of the attribute specified in the
         * <code>keyName</code> constructor parameter. This value will be used to
         * uniquely identify the current row.
         *
         * @return the primary key as a string
         * @see List#getSelectedKey(PageState)
         */
        public String getKey() {
            Object obj = m_dataQuery.get(m_keyName);
            if ( obj != null ) {
                return obj.toString();
            } else {
                return null;
            }
        }

        /**
         * Return the number of rows in the query
         */
        public long size() {
            return m_dataQuery.size();
        }

    }


}
