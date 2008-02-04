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


import com.arsdigita.toolbox.util.GlobalizationUtil ; 

import com.arsdigita.bebop.PageState;

import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.GenericDataQuery;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.persistence.Session;

/**
 * This class is used to create an implementation of
 * {@link DataQueryBuilder} that relies on a static
 * DataQuery.
 *
 * Example:
 * <pre>
 * Page p = new Page("Data Table Page");
 * // Build a data query to retrieve all users.
 * String sql = "select users.user_id," +
 *              "person_names.given_name, " +
 *              "person_names.family_name, " +
 *              "parties.primary_email " +
 *         "from users, person_names, parties " +
 *        "where users.name_id = person_names.name_id " +
 *          "and users.user_id = parties.party_id";
 * String cols[] = {"user_id", "given_name", "family_name", "primary_email"};
 * DataQueryBuilder dqb = new StaticDataQueryBuilder(sql, cols);
 * DataTable dt = new DataTable(dqb);
 *
 * dt.setEmptyView(new Label(GlobalizationUtil.globalize("toolbox.ui.no_results")));
 * dt.addColumn("User ID", "user_id", true, new DefaultTableCellRenderer());
 * dt.addColumn("First Name", "given_name", true, new DefaultTableCellRenderer());
 * dt.addColumn("Last Name", "family_name", true, new DefaultTableCellRenderer());
 * dt.addColumn("Email", "primary_email", true, new DefaultTableCellRenderer());
 *
 * // Put the table on the page and serve.
 * p.add(dt);
 * p.lock();
 * pm.servePage(p, request, response);
 * </pre>
 *
 * @author Bryan Quinn
 */
public class StaticDataQueryBuilder implements DataQueryBuilder {

    public static final String versionId = "$Id: StaticDataQueryBuilder.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private DataQuery m_dataQuery;
    private String m_keyCol;
    private boolean m_locked;

    /**
     * Creates a DataQueryBuilder that will always return
     * the specified DataQuery and keyColumn.
     *
     * @param dq The DataQuery to be returned byt this
     * DataQueryBuilder.
     * @param keyColumn The primary key column specified in
     * the DataQuery provided.
     */
    public StaticDataQueryBuilder(DataQuery dq, String keyColumn) {
        m_dataQuery = dq;
        m_keyCol = keyColumn;
        m_locked = false;
    }

    /**
     * Create a StaticDataQueryBuilder that consists of the specified
     * SQL query.  The column array specifies what property names
     * to map to the columns retrieved by the query in numeric order.
     * The first entry in the column array is presumed to be primary key
     * column.
     *
     * @param sql A SQL query.
     * @param columns An array of column names corresponding to the
     * the columns in the SQL query.
     */
    public StaticDataQueryBuilder(String sql, String[] columns) {
        Session s = SessionManager.getSession();
        GenericDataQuery dq = new GenericDataQuery(s, sql, columns);
        m_dataQuery = dq;
        m_keyCol = columns[0];
        m_locked = false;
    }

    /**
     * Perform all neccessary database operations and return
     * a {@link DataQuery} for the {@link DataTable} to use
     *
     * @param t the parent DataTable
     * @param s the page state
     */
    public DataQuery makeDataQuery(DataTable t, PageState s) {
        return m_dataQuery;
    }

    /**
     * @return the name of the column in the query that serves
     *   as the primary key for the items
     */
    public String getKeyColumn() {
        return m_keyCol;
    }

    /**
     * Update the DataQuery returned by {@link #makeDataQuery} to
     * the specified DataQuery.
     */
    public void setDataQuery(DataQuery dq) {
        if(!m_locked) {
            m_dataQuery = dq;
        }
    }

    /**
     * Update the key column returned by {@link #getKeyColumn} to the
     * the specified key column.
     */
    public void setKeyColumn(String keyColumn) {
        if(!m_locked) {
            m_keyCol = keyColumn;
        }
    }

    /**
     * A convenience method to update the data query and
     * key column with one call.
     */
    public void update(DataQuery dq, String keyColumn) {
        if (!m_locked) {
            m_dataQuery = dq;
            m_keyCol = keyColumn;
        }
    }

    public void lock() {
        m_locked = true;
    }

    public boolean isLocked() {
        return m_locked;
    }
}
