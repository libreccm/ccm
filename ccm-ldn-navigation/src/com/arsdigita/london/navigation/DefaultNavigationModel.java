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

package com.arsdigita.london.navigation;

import com.arsdigita.categorization.Category;
import com.arsdigita.kernel.ACSObject;
import com.arsdigita.web.Web;
import com.arsdigita.util.Assert;



public class DefaultNavigationModel extends AbstractNavigationModel {

    protected ACSObject loadObject() {
        Category category = getCategory();
        if (category == null) {
            return null;
        } 
        return category.getIndexObject();
    }

    protected Category loadCategory() {
        Category[] path = getCategoryPath();
        if (path == null ||
            path.length == 0) {
            return null;
        }
        return path[path.length-1];
    }

    protected Category[] loadCategoryPath() {
        Category[] path = NavigationFileResolver.getCategoryPath(Web.getRequest());
        Assert.exists(path, Category[].class);
        return path;
    }

    protected Category loadRootCategory() {
        Category[] path = getCategoryPath();
        if (path == null ||
            path.length == 0) {
            return null;
        }
        return path[0];
    }
}
