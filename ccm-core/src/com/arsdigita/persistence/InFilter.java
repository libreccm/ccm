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
package com.arsdigita.persistence;

import com.arsdigita.db.DbHelper;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.MetadataException;
import com.redhat.persistence.metadata.ObjectMap;
import com.redhat.persistence.metadata.ObjectType;
import com.redhat.persistence.metadata.Root;
import com.redhat.persistence.metadata.SQLBlock;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * InFilter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #13 $ $Date: 2004/08/16 $
 **/

class InFilter extends SimpleFilter implements Filter {

    private static Logger s_log = Logger.getLogger(InFilter.class);

    public final static String versionId = "$Id: InFilter.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    InFilter(Root root, String property, String subqueryProperty,
             String query) {
        super(makeConditions(root, property, subqueryProperty, query));
        if (s_log.isDebugEnabled()) {
            s_log.debug("InFilter: " + property + " - " + subqueryProperty +
                        " - " + query);
        }
    }

    private static SQLBlock getBlock(Root root, String query) {
        ObjectType ot = root.getObjectType(query);
        ObjectMap map = root.getObjectMap(ot);
        if (map == null) {
            throw new PersistenceException("no such query: " + query);
        }
        return map.getRetrieveAll();
    }

    private static String makeConditions(Root root, String prop,
                                         String subProperty, String query) {
        SQLBlock block = getBlock(root, query);
        Path subProp;
        if (subProperty == null) {
            Iterator paths = block.getPaths().iterator();
            if (paths.hasNext()) {
                subProp = (Path) paths.next();
            } else {
                return prop + " in (" + query + ")";
            }

            if (paths.hasNext()) {
                throw new PersistenceException
                    ("subquery has more than one mapping");
            }
        } else {
            subProp = Path.get(subProperty);
        }

        Path subcol = block.getMapping(subProp);
        if (subcol == null) {
            throw new MetadataException
                (root, block, "no such path: " + subProp);
        }

        final int currentDB = DbHelper.getDatabase();
        final StringBuffer sb = new StringBuffer();

        if (currentDB == DbHelper.DB_POSTGRES) {
            sb.append("exists ( select RAW[subquery_id] from (select RAW[");
            sb.append(subcol.getPath());
            sb.append("] as RAW[subquery_id] from (");
            sb.append(query);
            sb.append(") RAW[insub1] ) RAW[insub2] where ");
            sb.append("RAW[insub2.subquery_id] = ");
            sb.append(prop).append(")");
        } else if (currentDB == DbHelper.DB_ORACLE) {
            sb.append(prop).append(" in (select RAW[");
            sb.append(subcol.getPath()).append("] from (");
            sb.append(query).append(") RAW[insub])");
        } else {
            throw new IllegalStateException
                ("Unknown database: " + DbHelper.getDatabaseName(currentDB));
        }
        return sb.toString();
    }
}
