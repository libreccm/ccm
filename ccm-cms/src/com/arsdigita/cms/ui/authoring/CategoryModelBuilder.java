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
package com.arsdigita.cms.ui.authoring;


import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.persistence.DataQuery;
import com.arsdigita.persistence.SessionManager;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class CategoryModelBuilder {
    private final static Logger s_log =
        Logger.getLogger(CategoryModelBuilder.class);

    private Category m_root;
    private ItemSelectionModel m_item;
    private RequestLocal m_assigned;
    private RequestLocal m_unassigned;
    private RequestLocal m_loaded;

    public CategoryModelBuilder(Category root, ItemSelectionModel item) {
        m_item = item;
        m_root = root;
        m_root.disconnect();
        m_assigned = new RequestLocal();
        m_unassigned = new RequestLocal();
        m_loaded = new RequestLocal();
    }

    public Map getUnassignedCategories(PageState state) {
        if (!Boolean.TRUE.equals(m_loaded.get(state))) {
            loadCategories(state);
        }

        return (Map)m_unassigned.get(state);
    }

    public Map getAssignedCategories(PageState state) {
        if (!Boolean.TRUE.equals(m_loaded.get(state))) {
            loadCategories(state);
        }

        return (Map)m_assigned.get(state);
    }

    private void loadCategories(PageState state) {
        TreeMap unassigned = new TreeMap(new CategoryCollator());
        TreeMap assigned = new TreeMap(new CategoryCollator());

        HashSet initial = new HashSet();

        ContentItem item = (ContentItem)m_item.getSelectedObject(state);

        CategoryCollection cursor = item.getCategoryCollection();

        while ( cursor.next() ) {
            Category c = cursor.getCategory();
            initial.add(c.getID());
        }
        cursor.close();

        // This may be hokey code, but it pulls out the entire
        // cat hierarchy & generates full path for each one
        // in a single query. Hurrah!
        // XXX cat purpose
        DataQuery cats = SessionManager.getSession()
            .retrieveQuery("com.arsdigita.categorization.getAllCategoryChildren");
        cats.setParameter("id", m_root.getID());

        HashMap childrenMap = new HashMap();

        while (cats.next()) {
            Object[] cat = new Object[] {
                cats.get("id"),
                cats.get("name")
            };

            ArrayList children = (ArrayList)childrenMap.get(cats.get("parentID"));
            if (children == null) {
                children = new ArrayList();
                childrenMap.put(cats.get("parentID"), children);
            }
            children.add(cat);
        }

        LinkedList queue = new LinkedList();
        LinkedList nameQueue = new LinkedList();
        queue.addLast(m_root.getID());
        if (m_root.isAbstract()) {
            nameQueue.addLast("");
        } else {
            nameQueue.addLast(m_root.getName());
        }

        while (!queue.isEmpty()) {
            BigDecimal id = (BigDecimal)queue.removeFirst();
            String name = (String)nameQueue.removeFirst();

            // Process the node unless:
            // The category is assigned
            // The category's name is empty (meaning that it's the root)
            if (name != null && !"".equals(name)) {
                if (initial.contains(id)) {
                    assigned.put(new CategoryKey(id, name), name);
                } else {
                    unassigned.put(new CategoryKey(id, name), name);
                }
            }

            ArrayList children = (ArrayList)childrenMap.get(id);
            if (children != null) {
                // Append children
                for (Iterator i = children.iterator(); i.hasNext(); ) {
                    Object[] child = (Object[])i.next();
                    BigDecimal childID = (BigDecimal)child[0];
                    String childName = (String)child[1];
                    queue.addLast(childID);
                    StringBuffer nameBuf = new StringBuffer(name);
                    if (name.length() > 0) {
                        nameBuf.append(" > ");
                    }
                    nameBuf.append(childName);
                    nameQueue.addLast(nameBuf.toString());
                }
            }
        }

        m_unassigned.set(state, unassigned);
        m_assigned.set(state, assigned);

        m_loaded.set(state, Boolean.TRUE);
    }

    public class CategoryKey {
        private BigDecimal m_id;
        private String m_name;

        public CategoryKey(BigDecimal id,
                           String name) {
            m_id = id;
            m_name = name;
        }

        public String getName() {
            return m_name;
        }
        public BigDecimal getID() {
            return m_id;
        }

        public boolean equals(Object o) {
            try {
                CategoryKey key = (CategoryKey)o;

                return key.m_id.equals(m_id);
            } catch (ClassCastException e) {
                return false;
            }
        }

        public int hashCode() {
            return m_id.hashCode();
        }
    }

    private class CategoryCollator implements Comparator {

        private Collator m_collator = Collator.getInstance();

        public int compare(Object o1,
                           Object o2) {
            CategoryKey m1 = (CategoryKey)o1;
            CategoryKey m2 = (CategoryKey)o2;

            return m_collator.compare(m1.getName(), m2.getName());
        }
    }
}
