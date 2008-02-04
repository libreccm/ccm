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
package com.arsdigita.developersupport;

/**
 * A light-weight alternative to java.lang.Integer.
 *
 * <p>Note that equality is currently equivalent to identity.</p>
 *
 * @author  Vadim Nasardinov (vadimn@redhat.com)
 * @since   2004-02-10
 * @version $DateTime: 2004/08/16 18:10:38 $ $Revision: #4 $
 **/
final class MutableInteger {
    private int m_counter;

    /**
     * Initializes to 0.
     **/
    public MutableInteger() {}

    public void increment() {
        m_counter++;
    }

    public int intValue() {
        return m_counter;
    }

    public String toString() {
        return String.valueOf(m_counter);
    }
}
