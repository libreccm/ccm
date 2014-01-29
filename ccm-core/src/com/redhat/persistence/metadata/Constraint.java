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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import com.arsdigita.util.UncheckedWrapperException;

/**
 * Constraint
 *
 * @author <a href="mailto:rhs@mit.edu">rhs@mit.edu</a>
 * @version $Revision: #8 $ $Date: 2004/08/16 $
 **/

public abstract class Constraint {

    

    private Table m_table;
    private String m_name;
    private Column[] m_columns;

    // sourceBytes is used for generating the constraint name
    private static final char[] sourceBytes = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_'};


    Constraint(Table table, String name, Column[] columns) {
        m_table = table;
        m_name = name;
        m_columns = columns;

        if (m_table.getConstraint(getClass(), m_columns) != null) {
            throw new IllegalArgumentException
                ("Table already has constraint: " + m_table.getName());
        }

        m_table.addConstraint(this);

        Set cols = new HashSet();
        for (int i = 0; i < m_columns.length; i++) {
            m_columns[i].addConstraint(this);
            cols.add(m_columns[i]);
            if (!m_columns[i].getTable().equals(table)) {
                throw new IllegalArgumentException
                    ("All column constraints must be from the same table.");
            }
        }

        if (cols.size() != m_columns.length) {
            throw new IllegalArgumentException("Duplicate columns");
        }

        if (m_name == null) {
            generateName();
        }
    }


    private String generateName() {
        return generateName(m_table, getSortedColumns(), getSuffix());
    }

    static String generateName(Table table, List cols, String suffix) {
        StringBuffer buf = new StringBuffer();

        buf.append(table.getName());

        for (Iterator it = cols.iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            buf.append("_");
            buf.append(col.getName());
        }

        String name = buf.toString();
        buf = new StringBuffer(abbreviate(name, 22));

        buf.append(suffix);
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest((name + suffix).getBytes());
            char[] hash = new char[5];
            for (int i = 0; i < 5; i++) {
                int intValue = (new Byte(bytes[i])).intValue();
                if (intValue < 0) {
                    intValue = intValue * (-1);
                }
                intValue = intValue % sourceBytes.length;
                hash[i] = sourceBytes[intValue];
            }
            buf.append("_" + new String(hash));
        } catch (GeneralSecurityException e) {
            throw new UncheckedWrapperException(e);
        }
        String result = buf.toString();
        return result;
    }

    /**
     *  The length is the desired length for the abbreviated name
     */
    private static final String abbreviate(String name, int length) {
        if (name == null || name.length() <= length) {
            return name;
        }

        // we set a minSize to guarantee that no string between each _
        // is shorter than that size. This allows for some chance for
        // the user to actually be able to read the constraint to tell
        // what it is
        int minSize = 3;

        // allMinSizeOrLess is used to avoid a boundary condition
        boolean allMinSizeOrLess = true;

        StringBuffer result = new StringBuffer();
        int charsRemoved = 0;
        int previousIndex = 0;
        int currentIndex = name.indexOf("_");
        while (previousIndex > -1 && currentIndex > -1) {
            if (currentIndex - previousIndex > minSize + 1) {
                // here we just remove the character before the "_"
                result.append(name.substring(previousIndex, currentIndex-1));
                charsRemoved++;
                allMinSizeOrLess = allMinSizeOrLess &&
                    (currentIndex - previousIndex <= minSize);
            } else {
                // this means the string is too short so we just leave it
                result.append(name.substring(previousIndex, currentIndex));
            }
            if (name.length() - charsRemoved <= length) {
                // after the change above, the string is now short enough
                // so we go ahead and break out of the loop
                allMinSizeOrLess = false;
                previousIndex = currentIndex;
                break;
            }
            previousIndex = currentIndex;
            currentIndex = name.indexOf("_", currentIndex + 1);
        }
        result.append(name.substring(previousIndex));

        String finalResult = result.toString();
        if (allMinSizeOrLess) {
            // all of our segments are short so we just truncate the end
            // of the string to make it the correct length
            result.setLength(length);
            finalResult = result.toString();
        } else if (finalResult.length() > length) {
            // this means that there are still some segments greater than
            // minSize that can be trimmed.
            finalResult = abbreviate(result.toString(), length);
        }
        return finalResult;
    }


    public Table getTable() {
        return m_table;
    }

    public String getName() {
        if (m_name == null) {
            m_name = generateName();
        }
        return m_name;
    }

    public Column[] getColumns() {
        return m_columns;
    }

    public abstract boolean isDeferred();

    abstract String getSuffix();

    abstract String getColumnSQL();

    public abstract String getSQL();

    String getColumnList() {
        return getColumnList(false);
    }

    String getColumnList(boolean sort) {
        List cols;

        if (sort) {
            cols = getSortedColumns();
        } else {
            cols = new ArrayList(Arrays.asList(m_columns));
        }

        StringBuffer result = new StringBuffer("(");

        for (Iterator it = cols.iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            result.append(col.getName());
            if (it.hasNext()) {
                result.append(", ");
            }
        }

        result.append(")");

        return result.toString();
    }

    List getSortedColumns() {
        List cols = new ArrayList(Arrays.asList(m_columns));

        Collections.sort(cols, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Column c1 = (Column) o1;
                    Column c2 = (Column) o2;
                    return c1.getName().compareTo(c2.getName());
                }
            });

        return cols;
    }

    public String toString() {
        return getSQL();
    }

}
