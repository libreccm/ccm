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
import com.arsdigita.util.Assert;

/**
 * The global navigation model returns the
 * default root category.
 */
public class GlobalNavigationModel extends AbstractNavigationModel {

    protected ACSObject loadObject() {
        return null;
    }
    
    protected Category loadCategory() {
        Category[] cats = getCategoryPath();
        return cats[cats.length-1];
    }
    
    protected Category[] loadCategoryPath() {
        return new Category[]{ getRootCategory() };
    }
    
    protected Category loadRootCategory() {
        Category cat = Navigation.getConfig().getDefaultCategoryRoot();
        Assert.exists(cat, Category.class);

        return cat;
    }
}
