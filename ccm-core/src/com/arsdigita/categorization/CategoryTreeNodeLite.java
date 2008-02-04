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
package com.arsdigita.categorization;



import java.math.BigDecimal;
import com.arsdigita.bebop.tree.TreeNode;


/**
 *
 * <p>A light-weight category node. See {@link
 * com.arsdigita.categorization.CategoryTreeModelLite}. </p>
 *
 * @see com.arsdigita.categorization.CategoryTreeModelLite
 * @author Daniel Berrange
 * @version $Revision: #11 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class CategoryTreeNodeLite implements TreeNode  {
    private BigDecimal m_id;
    private String m_name;
    private boolean m_hasChild;

    public CategoryTreeNodeLite(BigDecimal id,
                                String name,
                                boolean hasChild) {
        m_id = id;
        m_name = name;
        m_hasChild = hasChild;
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
}
