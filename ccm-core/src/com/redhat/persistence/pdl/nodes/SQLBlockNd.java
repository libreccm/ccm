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
package com.redhat.persistence.pdl.nodes;

import java.util.Collection;

/**
 * SQLBlockNd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/

public class SQLBlockNd extends Node {

    public final static String versionId = "$Id: SQLBlockNd.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public static final Field MAPPINGS =
        new Field(SQLBlockNd.class, "mappings", MappingNd.class, 0);
    public static final Field BINDINGS =
        new Field(SQLBlockNd.class, "bindings", BindingNd.class, 0);

    private String m_sql;

    public SQLBlockNd(String sql) {
        m_sql = sql;
    }

    public String getSQL() {
        return m_sql;
    }

    public Collection getMappings() {
        return (Collection) get(MAPPINGS);
    }

    public Collection getBindings() {
        return (Collection) get(BINDINGS);
    }

    public void dispatch(Switch sw) {
        super.dispatch(sw);
        sw.onSQLBlock(this);
    }

}
