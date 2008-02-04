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
package com.redhat.persistence.metadata;

import java.util.AbstractList;
import java.util.List;

class UnionList extends AbstractList {
    private List m_left;
    private List m_right;

    UnionList(List left, List right) {
        m_left = left;
        m_right = right;
    }

    public Object get(int index) {
        // 0 1 2 (size 3), 3 4 5
        int leftsize = m_left.size();
        if (index >= leftsize) {
            return m_right.get(index - leftsize);
        } else {
            return m_left.get(index);
        }
    }

    public int size() {
        return m_left.size() + m_right.size();
    }
}
