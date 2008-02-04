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
package com.redhat.persistence.common;

/**
 * CompoundKey
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #5 $ $Date: 2004/08/16 $
 **/

public final class CompoundKey {

    public final static String versionId = "$Id: CompoundKey.java 287 2005-02-22 00:29:02Z sskracic $ by $Author: sskracic $, $DateTime: 2004/08/16 18:10:38 $";

    private Object m_one;
    private Object m_two;

    public CompoundKey(Object one, Object two) {
        m_one = one;
        m_two = two;
    }

    public int hashCode() {
        return (m_one == null ? 0 : m_one.hashCode()) ^
            (m_two == null ? 0 : m_two.hashCode());
    }

    private static final boolean compare(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }

    public boolean equals(Object o) {
        if (o instanceof CompoundKey) {
            CompoundKey key = (CompoundKey) o;
            return compare(m_one, key.m_one) && compare(m_two, key.m_two);
        } else {
            return false;
        }
    }

    public String toString() {
        return "key(" + m_one + ", " + m_two + ")";
    }

}
