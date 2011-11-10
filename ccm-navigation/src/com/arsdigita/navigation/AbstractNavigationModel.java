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
 */

package com.arsdigita.navigation;

import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.london.util.TransactionLocal;

public abstract class AbstractNavigationModel implements NavigationModel {

    private TransactionLocal m_object = new TransactionLocal() {
            public Object initialValue() {
                return loadObject();
            }
        };
    private TransactionLocal m_category = new TransactionLocal() {
            public Object initialValue() {
                return loadCategory();
            }
        };
    private TransactionLocal m_categoryPath = new TransactionLocal() {
            public Object initialValue() {
                return loadCategoryPath();
            }
        };
    private TransactionLocal m_rootCategory = new TransactionLocal() {
            public Object initialValue() {
                return loadRootCategory();
            }
        };
    
    protected abstract ACSObject loadObject();
    protected abstract Category loadCategory();
    protected abstract Category[] loadCategoryPath();
    protected abstract Category loadRootCategory();


    /**
     * Returns the currently selected display object
     */
    public final ACSObject getObject() {
        return (ACSObject)m_object.get();
    }

    /**
     * Returns the currently selected category 
     */
    public final Category getCategory() {
        return (Category)m_category.get();
    }
    
    /**
     * Returns the ancestors for the currently selected
     * category.
     */
    public final Category[] getCategoryPath() {
        return (Category[])m_categoryPath.get();
    }
    
    /**
     * Returns the root category
     */
    public final Category getRootCategory() {
        return (Category)m_rootCategory.get();
    }
}
