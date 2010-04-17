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
package com.redhat.persistence.common;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: SQL.java 287 2005-02-22 00:29:02Z sskracic $
 **/

public class SQL {

    private SQLToken m_first;
    private SQLToken m_last;

    public SQL() {
        m_first = null;
        m_last = null;
    }

    public void append(SQLToken sql) {
        if (m_first == null) {
            m_first = sql;
            m_last = sql;
        } else {
            m_last.m_next = sql;
            sql.m_previous = m_last;
            m_last = sql;
        }
    }

    public SQLToken getFirst() {
        return m_first;
    }

    public SQLToken getLast() {
        return m_last;
    }

    public List getBindings() {
        return getBindings(m_first, null);
    }

    public static final List getBindings(SQLToken start, SQLToken end) {
        ArrayList result = new ArrayList();
        for (SQLToken t = start; t != end; t = t.getNext()) {
            if (t.isBind()) {
                result.add(Path.get(t.getImage()));
            }
        }
        return result;
    }

    public String toString() {
        return toString(m_first, null);
    }

    public static final String toString(SQLToken start, SQLToken end) {
        StringBuffer result = new StringBuffer();
        for (SQLToken t = start; t != end; t = t.getNext() ) {
            result.append(t.getImage());
        }
        return result.toString();
    }

}
