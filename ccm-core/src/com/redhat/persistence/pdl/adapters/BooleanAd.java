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
package com.redhat.persistence.pdl.adapters;

import com.arsdigita.db.DbHelper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * BooleanAd
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 **/

public class BooleanAd extends SimpleAdapter {

    

    public BooleanAd() {
	super("global.Boolean", Types.BIT);
    }

    public void bind(PreparedStatement ps, int index, Object obj, int type)
	throws SQLException {
        // Because postgres has a boolean type and we are currently
        // using char(1) and the jdbc driver uses "false" and "true"
        // we have to do this converstion.  Long term, we want
        // to remove this
        if (Boolean.TRUE.equals(obj)) {
            ps.setString(index, "1");
        } else {
            ps.setString(index, "0");
        }
    }

    public boolean isBindable() {
        return DbHelper.getDatabase() != DbHelper.DB_POSTGRES;
    }

    public String getLiteralCode(Object o) {
        return Boolean.TRUE.equals(o) ? "'1'" : "'0'";
    }

    public Object fetch(ResultSet rs, String column) throws SQLException {
	boolean bool = rs.getBoolean(column);
	if (rs.wasNull()) {
	    return null;
	} else if (bool) {
	    return Boolean.TRUE;
	} else {
	    return Boolean.FALSE;
	}
    }
}
