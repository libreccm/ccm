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

import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQL;
import com.redhat.persistence.common.SQLToken;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * SQLBlock
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class SQLBlock {

    

    private SQL m_sql;
    private ArrayList m_assigns = new ArrayList();

    private HashMap m_mappings = new HashMap();
    private HashMap m_types = new HashMap();

    public static class Assign {

        private SQLToken m_begin;
        private SQLToken m_end;

        private Assign(SQLToken begin, SQLToken end) {
            m_begin = begin;
            m_end = end;
        }

        public SQLToken getBegin() {
            return m_begin;
        }

        public SQLToken getEnd() {
            return m_end;
        }

        public String toString() {
            return SQL.toString(m_begin, m_end);
        }

    }


    public SQLBlock(SQL sql) {
        m_sql = sql;
    }

    public SQL getSQL() {
        return m_sql;
    }

    public void addAssign(SQLToken begin, SQLToken end) {
        m_assigns.add(new Assign(begin, end));
    }

    public Collection getAssigns() {
        return m_assigns;
    }

    public boolean hasMapping(Path path) {
        return m_mappings.containsKey(path);
    }

    public void addMapping(Path path, Path column) {
        if (hasMapping(path)) {
            throw new IllegalArgumentException
                ("already have mapping: " + path);
        }
        m_mappings.put(path, column);
    }

    public void removeMapping(Path path) {
        m_mappings.remove(path);
    }

    public Path getMapping(Path path) {
        return (Path) m_mappings.get(path);
    }

    public Collection getPaths() {
        return m_mappings.keySet();
    }

    public boolean hasType(Path path) {
        return m_types.containsKey(path);
    }

    public void addType(Path path, int type) {
        if (hasType(path)) {
            throw new IllegalArgumentException
                ("already have mapping: " + path);
        }
        m_types.put(path, new Integer(type));
    }

    public int getType(Path path) {
        return ((Integer) m_types.get(path)).intValue();
    }

    public String toString() {
        return m_sql.toString() + "\n assigns = " + m_assigns;
    }

}
