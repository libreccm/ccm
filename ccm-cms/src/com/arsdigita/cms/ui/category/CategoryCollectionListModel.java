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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.categorization.Category;
import com.arsdigita.categorization.CategoryCollection;

import java.math.BigDecimal;

/**
 * A {@link ListModel} that iterates over categories via a cursor.
 */
public final class CategoryCollectionListModel implements ListModel {

    private Category m_cat;
    private BigDecimal m_excludedID;
    private CategoryCollection m_categories;


    /**
     * Constructs a new <code>CategoryCollectionListModel</code>
     */
    public CategoryCollectionListModel(CategoryCollection coll) {
        this(coll, null);
    }

    /**
     * Constructs a new <code>CategoryCollectionListModel</code>
     */
    public CategoryCollectionListModel(CategoryCollection coll,
                                       BigDecimal excludedID) {

        m_categories = coll;
        m_excludedID = excludedID;
        m_cat = null;
    }

    public boolean next() {
        if ( m_categories.next() ) {
            final Category category = m_categories.getCategory();
            if ( category.getID().equals(m_excludedID) ) {
                return next();
            } else {
                m_cat = category;
                return true;
            }
        } else {
            return false;
        }
    }

    private Category getCategory() {
        if ( m_cat == null ) {
            throw new IllegalStateException("call next() first");
        }
        return m_cat;
    }

    /**
     * Reads the name of the category.
     *
     * Quasimodo:
     * Modified to ensure that the value is read from Category (and not the
     * localized version). This is necessary because we are in the admin GUI,
     * a localized version would be confusing.
     */
    public Object getElement() {
        return getCategory().getName("");
    }

    public String getKey() {
        return getCategory().getID().toString();
    }
}
