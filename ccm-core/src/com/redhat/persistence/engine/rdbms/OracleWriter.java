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
package com.redhat.persistence.engine.rdbms;

import java.sql.Types;

/**
 * OracleWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: OracleWriter.java 738 2005-09-01 12:36:52Z sskracic $
 */
public class OracleWriter extends ANSIWriter {

    public void write(Select select) {
        write(select.getQuery().generate
              (getEngine().getSession().getRoot(), true));
    }

    void writeBind(Object o, int jdbcType) {
        if (o == null) {
            super.writeBind(o, jdbcType);
        } else {
            switch (jdbcType) {
            case Types.BLOB:
                write("empty_blob()");
                break;
            case Types.CLOB:
                write("empty_clob()");
                break;
            default:
                super.writeBind(o, jdbcType);
                break;
            }
        }
    }
}
