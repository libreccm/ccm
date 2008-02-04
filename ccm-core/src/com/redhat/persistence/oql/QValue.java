/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.redhat.persistence.oql;

import com.redhat.persistence.metadata.Column;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.Table;

/**
 * QValue
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #4 $ $Date: 2004/08/16 $
 **/

class QValue {

    public final static String versionId = "$Id: QValue.java 737 2005-09-01 12:27:29Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private QFrame m_frame;
    private String m_column = null;
    private Code m_sql = null;

    QValue(QFrame frame, String column) {
        m_frame = frame;
        m_column = column;
    }

    QValue(QFrame frame, Code sql) {
        m_frame = frame;
        m_sql = sql;
    }

    QFrame getFrame() {
        return m_frame;
    }

    String getTable() {
        return m_frame.getTable();
    }

    String getColumn() {
        return m_column;
    }

    boolean isNullable() {
        QFrame frame = m_frame.getDuplicate();
        if (frame.isOuter()) { return true; }
        if (frame.getTable() == null) { return true; }
        Root root = frame.getGenerator().getRoot();
        Table t = root.getTable(frame.getTable());
        if (t == null) { return true; }
        Column col = t.getColumn(m_column);
        return col.isNullable();
    }

    Code emit() {
        if (m_sql != null) {
            return m_sql;
        } else {
            return new Code(m_frame.alias() + "." + m_column);
        }
    }

    public String toString() {
        if (m_sql != null) {
            return m_sql.toString();
        } else {
            return m_frame.alias() + "." + m_column;
        }
    }

}
