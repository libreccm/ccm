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

import com.arsdigita.kernel.ui.DataQueryTreeModel;
import com.arsdigita.kernel.ui.DataQueryTreeNode;
import com.arsdigita.kernel.ui.DataQueryTreeIterator;
import org.apache.log4j.Logger;

/**
 * Implements the {@link com.arsdigita.bebop.tree.TreeModel} interface for
 * categories.
 *
 * @author Daniel Berrange
 * @version $Revision: #17 $ $DateTime: 2004/08/16 18:10:38 $
 */
public class CategoryTreeModelLite extends DataQueryTreeModel {

    private static final Logger s_log =
                                Logger.getLogger(CategoryTreeModelLite.class);
    String m_order = null;

    /**
     * Initializes with the passed in the root Category.
     *
     * @param root the root category for this TreeModel
     */
    public CategoryTreeModelLite(Category root) {
        this(root, null);
    }

    /**
     * Initializes with the passed in the root Category.
     *
     * @param root the root category for this TreeModel
     * @param order the field to order by
     */
    public CategoryTreeModelLite(Category root, String order) {
        super(root.getID(),
              "com.arsdigita.categorization.getRootCategory",
              "com.arsdigita.categorization.getSubCategories");
        m_order = order;
    }

    @Override
    protected DataQueryTreeIterator getDataQueryTreeIterator(DataQueryTreeNode node,
                                                             String getSubCategories) {
        return new CategoryTreeIterator(node, getSubCategories, m_order);
    }

    private static class CategoryTreeIterator extends DataQueryTreeIterator {

        public CategoryTreeIterator(DataQueryTreeNode node, String getSubCategories, String order) {
            super(node, getSubCategories);
            if (order != null) {
                addOrder(order);
            }
        }

        @Override
        public Object next() {
            DataQueryTreeNode node = (DataQueryTreeNode) super.next();

            //                    m_nodes.getLink
            node.setValue(Category.IS_ABSTRACT,
                          (Boolean) m_nodes.get(Category.IS_ABSTRACT));
            return node;
        }

    }
}
