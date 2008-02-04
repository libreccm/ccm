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
package com.arsdigita.search.filters;

import com.arsdigita.search.FilterSpecification;
import com.arsdigita.categorization.Category;


/**
 * A filter specification for supplying a list of categories
 * to the category membership filter type
 */
public class CategoryFilterSpecification extends FilterSpecification {

    public static final String CATEGORIES = "categories";
    public static final String DESCENDING = "descending";

    /**
     * Creates a new category filter spec
     * @param cats the categories to check membership of
     * @param boolean whether category children trees will be traversed as well
     */
    public CategoryFilterSpecification(Category[] cats, boolean descending) {
        super(new Object[] { CATEGORIES, cats, DESCENDING, new Boolean(descending) },
              new CategoryFilterType());
    }

    /**
     * Creates a new category filter spec that doesn't check subcategories
     * @param cats the categories to check membership of
     */
    public CategoryFilterSpecification(Category[] cats) {
        this(cats, false);
    }

    /**
     * Returns the list of categories to check
     * @return the list of categories
     */
    public Category[] getCategories() {
        return (Category[])get(CATEGORIES);
    }

    /**
     * Checks whether the filter should traverse subcategory tree(s).
     */
    public boolean isDescending() {
        return Boolean.TRUE.equals(get(DESCENDING));
    }
}
