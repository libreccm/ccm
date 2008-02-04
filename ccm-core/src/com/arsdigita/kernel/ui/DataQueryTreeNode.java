/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.kernel.ui;

import java.math.BigDecimal;
import com.arsdigita.bebop.tree.TreeNode;
import java.util.HashMap;

/**
 * 
 *  A simple tree node database backed trees.
 *
 *
 * @author Daniel Berrange 
 *
 * @version $Id: DataQueryTreeNode.java 287 2005-02-22 00:29:02Z sskracic $
 */


public class DataQueryTreeNode implements TreeNode  {
    private BigDecimal m_id;
    private String m_name;
    private boolean m_hasChild;
    private HashMap m_values;

    public DataQueryTreeNode(BigDecimal id,
                             String name,
                             boolean hasChild) {
        m_id = id;
        m_name = name;
        m_hasChild = hasChild;
        m_values = null;
    }

    public Object getKey() {
        return m_id.toString();
    }

    public Object getElement() {
        return m_name;
    }

    public BigDecimal getID() {
        return m_id;
    }

    public boolean hasChildren() {
        return m_hasChild;
    }

    public void setValue(String key, Object value) {
        if (m_values == null) {
            m_values = new HashMap();
        }
        m_values.put(key, value);
    }

    public Object getValue(String key) {
        if (m_values != null) {
            return m_values.get(key);
        } else {
            return null;
        }
    }
}
