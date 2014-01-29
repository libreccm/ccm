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
package com.arsdigita.persistence.metadata;

import java.sql.Types;
import java.util.List;
import java.util.Iterator;

/**
 * General static utility methods for the metadata classes. These
 * methods are not intended to be used outside of the metadata
 * package.
 *
 * @since 2001-04-02
 * @version 1.0
 * @author <a href="mailto:randyg@alum.mit.edu">Randy Graebner</a>
 */
public class Utilities  {

    public final static String LINE_BREAK =
        System.getProperty("line.separator", "\n\r");

    /**
     * It makes neo sense to instantiate this Utilities class. Hide
     * the constructor.
     **/
    private Utilities() {}

    /**
     *  This takes a string buffer and returns the same thing that was
     *  passed in if the value is not null or creates a StringBuffer to
     *  return if the value is null.
     *
     *  @param sb The StringBuffer to examine
     *  @return A non-null StringBuffer
     */
    public static final StringBuffer getSB(StringBuffer sb) {
        if (sb == null) {
            return new StringBuffer();
        } else {
            return sb;
        }
    }

    /**
     *  This function returns true if the value <code>type</code>
     *  maps to a valid JDBC Type. Mainly intended for use in
     *  precondition statements.
     *
     *  @param type The value to check
     *  @return true if type is one of the constants in <code>java.sql.Types</code>
     *
     */
    public static boolean isJDBCType(final int type) {
        // Kinda ugly to have one big case, but it's actually the
        // fastest lookup.
        switch (type) {
        case Types.ARRAY:
        case Types.BIGINT:
        case Types.BINARY:
        case Types.BIT:
        case Types.BLOB:
        case Types.CHAR:
        case Types.CLOB:
        case Types.DATE:
        case Types.DECIMAL:
        case Types.DISTINCT:
        case Types.DOUBLE:
        case Types.FLOAT:
        case Types.INTEGER:
        case Types.JAVA_OBJECT:
        case Types.LONGVARBINARY:
        case Types.LONGVARCHAR:
        case Types.NULL:
        case Types.NUMERIC:
        case Types.OTHER:
        case Types.REAL:
        case Types.REF:
        case Types.SMALLINT:
        case Types.STRUCT:
        case Types.TIME:
        case Types.TIMESTAMP:
        case Types.TINYINT:
        case Types.VARBINARY:
        case Types.VARCHAR:
            return true;
        default:
            return false;
        }

    }

    /**
     * "join" a List of Strings into a single string, with each string
     * separated by a defined separator string.
     * @deprecated use {@link com.arsdigita.util.StringUtils}
     *
     * @param elements the strings to join together
     * @param sep the separator string
     * @return the strings joined together
     */
    public static String join(List elements, String sep) {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        Iterator iter = elements.iterator();

        while (iter.hasNext()) {
            String element = (String)iter.next();

            if (!first) {
                sb.append(sep);
            } else {
                first = false;
            }

            sb.append(element);
        }

        return sb.toString();
    }
}
