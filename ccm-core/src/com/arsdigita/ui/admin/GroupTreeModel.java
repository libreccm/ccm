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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeNode;

import com.arsdigita.domain.DataObjectNotFoundException;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.kernel.ACSObjectCollection;
import com.arsdigita.kernel.Group;
import com.arsdigita.kernel.GroupCollection;

import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;

import java.util.Iterator;

/**
 *
 *
 * @author David Dao
 *
 */
public class GroupTreeModel implements TreeModel {

    private class GroupIterator implements Iterator {

        private ACSObjectCollection m_coll;

        public GroupIterator(ACSObjectCollection coll) {
            m_coll = coll;
        }

        public boolean hasNext() {
            return m_coll.next();
        }

        public Object next() {
            return new GroupTreeNode(m_coll.getACSObject());
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Obtain the root folder of the tree
     */
    public TreeNode getRoot(PageState data) {
        return new RootTreeNode();

    }

    /**
     * Check whether a given node has children
     */
    public boolean hasChildren(TreeNode n, PageState ps) {
        if (n instanceof RootTreeNode) {
            return true;
        } else {

            Session session = SessionManager.getSession();
            DataQuery query = session.retrieveQuery("com.arsdigita.ui.admin.SubGroupCount");
            query.setParameter("groupID", new BigDecimal((String) n.getKey()));

            int count = 0;
            if (query.next()) {
                count = ((BigDecimal) query.get("count")).intValue();
            }

            query.close();

            return (count > 0);
        }

    }

    /**
     * Get direct children in this node.
     */
    public Iterator getChildren(TreeNode n, PageState ps) {
        if (n instanceof RootTreeNode) {
            /**
             * This is a work around until there is an API that
             * will return all first level groups.
             */
            Session ssn = SessionManager.getSession();
            DataCollection coll = ssn.retrieve("com.arsdigita.kernel.Group");

            coll.addInSubqueryFilter("id", "com.arsdigita.ui.admin.AllNoParentGroups");
			
    	    coll.addOrder("lower("+ Group.DISPLAY_NAME + ") asc");
			
            return new GroupIterator(new ACSObjectCollection(coll));
        } else {
            Group group = null;

            try {
                group = new Group(new BigDecimal((String)n.getKey()));
            } catch (DataObjectNotFoundException ed) {
                // Group is not found just return  null.
                return null;
            }

            GroupCollection coll = group.getSubgroups();
            coll.addOrder("lower("+ Group.DISPLAY_NAME + ") asc");

            return new GroupIterator(coll);


        }
    }

}

class RootTreeNode implements TreeNode {

    public Object getKey() {
        return "-1";
    }

    public Object getElement() {
        return "/";
    }
}

class GroupTreeNode implements TreeNode {

    private String m_key;
    private String m_name;

    public GroupTreeNode(ACSObject o) {
        m_key = o.getID().toString();
        m_name = o.getDisplayName();
    }

    public Object getKey() {
        return m_key;
    }

    public Object getElement() {
        return m_name;
    }
}
