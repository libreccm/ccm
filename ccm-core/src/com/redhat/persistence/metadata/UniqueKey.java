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

import java.util.HashSet;
import java.util.Set;

/**
 * UniqueKey
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class UniqueKey extends Constraint {

    public final static String versionId = "$Id: UniqueKey.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Set m_foreignKeys = new HashSet();

    public UniqueKey(Table table, String name, Column[] columns) {
        super(checkDuplicates(table, columns), name, columns);
    }

    private static final Table checkDuplicates(Table table,
                                               Column[] columns) {
        if (table.getUniqueKey(columns) != null) {
            throw new IllegalArgumentException
                ("Table already has a unique key: " + table.getName());
        }
        return table;
    }

    public UniqueKey(String name, Column column) {
        this(column.getTable(), null, new Column[] {column});
    }

    public boolean isPrimaryKey() {
        return this.equals(getTable().getPrimaryKey());
    }

    public Set getForeignKeys() {
        return m_foreignKeys;
    }

    void addForeignKey(ForeignKey fk) {
        m_foreignKeys.add(fk);
    }

    public boolean isDeferred() {
        return false;
    }

    String getSuffix() {
        if (isPrimaryKey()) {
            return "_p";
        } else {
            return "_u";
        }
    }

    String getColumnSQL() {
        String keyword = isPrimaryKey() ? "primary key" : "unique";
        if (getName() == null) {
            return "        " + keyword;
        } else {
            return "        constraint " + getName() + "\n          " +
                keyword;
        }
    }

    public String getSQL() {
        String keyword = isPrimaryKey() ? "primary key" : "unique";
        if (getName() == null) {
            return "    " + keyword + getColumnList();
        } else {
            return "    constraint " + getName() + "\n      " + keyword +
                getColumnList();
        }
    }

}
