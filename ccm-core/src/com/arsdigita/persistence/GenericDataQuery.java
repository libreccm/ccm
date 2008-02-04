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
package com.arsdigita.persistence;

import com.redhat.persistence.DataSet;
import com.redhat.persistence.Signature;
import com.redhat.persistence.common.ParseException;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.common.SQLParser;
import com.redhat.persistence.metadata.Model;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Role;
import com.redhat.persistence.oql.Static;

import java.io.StringReader;
import java.util.Collections;

/**
 * GenericDataQuery
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #16 $ $Date: 2004/08/16 $
 */

public class GenericDataQuery extends DataQueryImpl {

    public final static String versionId = "$Id: GenericDataQuery.java 738 2005-09-01 12:36:52Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    public GenericDataQuery(Session s, String sql, String[] columns) {
        super(s, ds(s, sql, columns));
    }

    private static DataSet ds(Session s, String sql, String[] columns) {
	ObjectType propType = s.getRoot().getObjectType("global.Object");
        final ObjectType type =
            new ObjectType(Model.getInstance("gdq"), sql, null);
	Signature sig = new Signature(type);
	for (int i = 0; i < columns.length; i++) {
	    type.addProperty
                (new Role(columns[i], propType, false, false, true));
	    sig.addPath(Path.get(columns[i]));
	}

	SQLParser p = new SQLParser(new StringReader(sql));
	try {
	    p.sql();
	} catch (ParseException e) {
	    throw new PersistenceException(e);
	}

        Static st = new Static
            (p.getSQL(), columns, false, Collections.EMPTY_MAP) {
            protected boolean hasType() { return true; }
            protected ObjectType getType() { return type; }
        };

        return new DataSet(s.getProtoSession(), sig, st);
    }

}
