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
package com.arsdigita.runtime;

import com.arsdigita.db.DbHelper;
import com.arsdigita.installer.SQLLoader;
import com.arsdigita.util.StringUtils;
import java.sql.Connection;
import org.apache.log4j.Logger;

/**
 * Subject to change.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: SchemaLoader.java 736 2005-09-01 10:46:05Z sskracic $
 */
public final class SchemaLoader {
    private static final Logger s_log = Logger.getLogger(SchemaLoader.class);

    private final String m_script;

    public SchemaLoader(final String script) {
        if (script == null) throw new IllegalArgumentException();

        m_script = script;
    }

    private String interpolatedScriptName() {
        final String url = RuntimeConfig.getConfig().getJDBCURL();
        final int db = DbHelper.getDatabaseFromURL(url);

        return StringUtils.interpolate
            (m_script, "database", DbHelper.getDatabaseDirectory(db));
    }

    public void run(final Connection conn) {
        if (conn == null) throw new IllegalArgumentException();

        SQLLoader.load(conn, interpolatedScriptName());
    }

    public String toString() {
        return interpolatedScriptName();
    }
}
