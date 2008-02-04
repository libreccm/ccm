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
package com.arsdigita.search.ui.filters;

import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.bebop.PageState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * A simple category filter which displays all
 * subcategories from a specified root
 */
public class SimpleCategoryFilterWidget extends CategoryFilterWidget {

    private static final Logger s_log =
        Logger.getLogger(SimpleCategoryFilterWidget.class);

    private Category[] m_roots;

    /**
     * Creates a new category filter
     */
    protected SimpleCategoryFilterWidget() {
        m_roots = new Category[0];
    }

    /**
     * Creates a new category filter
     * @param root the root category
     */
    public SimpleCategoryFilterWidget(Category root) {
        this(new Category[] { root });
    }

    /**
     * Creates a new category filter
     * @param root the list of root categories
     */
    public SimpleCategoryFilterWidget(Category[] roots) {
        m_roots = roots;
    }


    /**
     *  Sets root categories (hence the category widget content)
     * on per-request basis.  Override if category widget contents
     * must change across requests.
     */
    protected Category[] getRoots(PageState state) {
        return m_roots;
    }


    public Category[] getCategories(PageState state) {
        if (s_log.isDebugEnabled())
            s_log.debug("getCategories", new Throwable());

        Category[] roots = getRoots(state);
        Collection cats = new ArrayList();
        Set seen = new HashSet();
        for (int i = 0 ; i < roots.length ; i++) {
            if (seen.contains(roots[i])) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug("skipping category " + roots[i].getOID());
                }
                continue;
            }

            if (s_log.isDebugEnabled()) {
                s_log.debug("processing category " + roots[i].getOID());
            }

            CategoryCollection scions = roots[i].getDescendants();
            while ( scions.next() ) {
                cats.add(scions.getCategory());
            }
            scions.close();
            seen.add(roots[i]);
        }
        return (Category[])cats.toArray(new Category[cats.size()]);
    }

}
