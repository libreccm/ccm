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


import com.arsdigita.bebop.PageState;

import java.util.Iterator;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;
import java.math.BigDecimal;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.bebop.tree.TreeNode;
import org.apache.log4j.Logger;


/**
 * 
 * A generic tree model for representing data base backed trees
 * in an efficient manner. The number of queries is proportional
 * to the number of branches in the tree, rather than the total
 * number of nodes.
 *
 * All that is required to use this class are two
 * custom data queries, the names of which will be
 * passed into the constructor. The first one is used
 * to pull out the name, id and number of children for the
 * root node. The following example shows an example
 * implementation for category tree, which should be customized
 * to suit your data model.
 *
 * <pre>
 * query getRootCategory {
 *     do {
 *        select g.name,
 *               g.category_id,
 *               count(sd.category_id) as sub_count
 *        from cat_categories g,
 *             cat_category_category_map sd
 *        where g.category_id = :objectID
 *          and sd.category_id(+) = g.category_id
 *        group by g.name, g.category_id
 *    } map {
 *        id = g.category_id;
 *        name = g.name;
 *        nchild = sub_count;
 *    }
 * }
 * </pre>
 *
 * The second data query does a similar task, but for
 * *all* children of a particular node. Again the following
 * example for categories can be customized by changing the
 * table names:
 *
 * <pre>
 * query getSubCategories {
 *     do {
 *         select g.name,
 *                g.category_id,
 *                count(sd2.category_id) as sub_count
 *         from cat_categories g,
 *              cat_category_category_map sd1,
 *              cat_category_category_map sd2
 *         where sd1.category_id = :objectID
 *           and g.category_id = sd1.related_category_id
 *           and sd2.category_id(+) = sd1.related_category_id
 *         group by g.name, g.category_id
 *     } map {
 *         id = g.category_id;
 *         name = g.name;
 *         nchild = sub_count;
 *     }
 * }
 * </pre>
 *
 *
 * @author Daniel Berrange 
 *
 * @version $Id: DataQueryTreeModel.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class DataQueryTreeModel implements TreeModel {

    private static final Logger s_log =
        Logger.getLogger(DataQueryTreeModel.class);

    private BigDecimal m_root;
    private String m_getRootCategory;
    private String m_getSubCategories;

    public DataQueryTreeModel (String getRootCategory,
                               String getSubCategories) {
        this(null, getRootCategory, getSubCategories);
    }

    /**
     *  Constructor, which takes in the root Category.
     *
     * @param root the object id of the root category
     * @param getRootCategory the data query name for root category
     * @param getSubCategories the data query name for sub categories
     */
    public DataQueryTreeModel (BigDecimal root,
                               String getRootCategory,
                               String getSubCategories) {
        m_root = root;
        m_getRootCategory = getRootCategory;
        m_getSubCategories = getSubCategories;
    }


    /**
     * Obtains all the children of the node as an iterator,
     * returning CategoryTreeNodeLites.
     *
     *  @param n the TreeNode that is used to look for the children
     *  @param data the PageState to use for permissioning purposes
     *  @return an iterator of child TreeNodes.
     */
    public Iterator getChildren(TreeNode node, PageState data) {
        return getDataQueryTreeIterator
            ((DataQueryTreeNode)node, m_getSubCategories);
    }

    /**
     * Indicates whether the specified tree node has children.
     *
     *  @param n the TreeNode that is used to look for the children
     *  @param data the PageState to use for permissioning purposes
     *  @return an iterator of child TreeNodes.
     */
    public boolean hasChildren(TreeNode node, PageState state) {
        DataQueryTreeNode n = (DataQueryTreeNode)node;
        return n.hasChildren();
    }

    public TreeNode getRoot(PageState state) {
        Session session = SessionManager.getSession();
        DataQuery query = session.retrieveQuery(m_getRootCategory);

        if (m_root != null)
            query.setParameter("objectID", m_root);

        if (query.next()) {
            BigDecimal id = (BigDecimal)query.get("id");
            String name = (String)query.get("name");
            BigDecimal count = (BigDecimal)query.get("nchild");

            query.close();

            return new DataQueryTreeNode(id, name, count.intValue() > 0);
        }
        throw new UncheckedWrapperException("cannot find root node");
    }

    protected DataQueryTreeIterator getDataQueryTreeIterator
        (DataQueryTreeNode node, String getSubCategories) {
        return new DataQueryTreeIterator(node, getSubCategories);
    }
}
