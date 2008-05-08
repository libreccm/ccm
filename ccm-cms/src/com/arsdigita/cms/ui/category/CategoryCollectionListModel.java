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
     * Liest den Namen der Kategorie aus. Angepaßt, damit hier immer
     * der Wert aus Category gelesen wird und nicht die lokalisierte
     * Version. Ist hier sinnvoll, da es Teil der Adminoberfläche für
     * Kategorien ist. Eine lokalisierte Anzeige würde hier nur zu
     * Verwirrung führen.
     * 
     * Quasimodo
     */
    public Object getElement() {
        return getCategory().getName("");
    }

    public String getKey() {
        return getCategory().getID().toString();
    }
}
