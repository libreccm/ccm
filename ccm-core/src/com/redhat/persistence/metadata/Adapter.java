/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.metadata;

import com.redhat.persistence.PropertyMap;
import com.redhat.persistence.Session;
import com.redhat.persistence.common.CompoundKey;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Adapter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public abstract class Adapter {

    public final static String versionId = "$Id: Adapter.java 735 2005-09-01 06:42:59Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Root m_root;

    void setRoot(Root root) {
        m_root = root;
    }

    public Root getRoot() {
        return m_root;
    }

    public Object getSessionKey(Object obj) {
        return getSessionKey(getObjectType(obj), getProperties(obj));
    }

    public Object getSessionKey(ObjectType type, PropertyMap props) {
        Collection keys = type.getKeyProperties();
        Object key = null;

        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            Object value = props.get((Property) it.next());
            if (key == null) {
                key = value;
            } else {
                key = new CompoundKey(key, value);
            }
        }

        return new CompoundKey(type.getBasetype(), key);
    }

    // This needs work. It's odd to have an adapter interface here in the
    // session layer that knows about prepared statements. Also I don't like
    // having things that throw unsupported operation exception.

    public Object fetch(ResultSet rs, String column) throws SQLException {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
        throws SQLException {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    public int defaultJDBCType() {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    public boolean isMutation(Object value, int jdbcType) {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    /**
     *  Determines whether the values can be provided via JDBC
     * bind variables.  It's normally set to 'true', unless we
     * have some non-tolerant JDBC drivers (for example Postgres 8)
     * which don't tolerate our completely broken and inconsistent
     * management of Boolean values.  If 'false', then the Adapter
     * is responsible for providing the properly escaped String
     * which will be a part of SQL statement text.
     *
     * @see getLiteralCode(Object)
     */
    public boolean isBindable() {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    /**
     *  Inserts literal value of passed object in SQL statement text.
     * This method must be overridden if isBindable() is false.
     *
     * @see isBindable()
     */
    public String getLiteralCode(Object o) {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    public void mutate(ResultSet rs, String column, Object obj, int type)
        throws SQLException {
        throw new UnsupportedOperationException
            ("not a bindable adapter: " + getClass().getName());
    }

    public void setSession(Object obj, Session ssn) { return; }

    public Object getObject(ObjectType basetype, PropertyMap props) {
        throw new UnsupportedOperationException
            ("not a compound adapter: " + getClass().getName());
    }

    public abstract PropertyMap getProperties(Object obj);

    public abstract ObjectType getObjectType(Object obj);

}
