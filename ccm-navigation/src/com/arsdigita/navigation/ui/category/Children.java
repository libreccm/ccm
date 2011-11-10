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

package com.arsdigita.navigation.ui.category;

import com.arsdigita.categorization.Category;
import com.arsdigita.navigation.NavigationModel;
import com.arsdigita.navigation.Navigation;

import com.arsdigita.web.Web;
import com.arsdigita.web.URL;

/**
 * CategoryChildren component displays a list of the immediate children of the 
 * current category
 */
public class Children extends AbstractList {
    
    public Children() {
        super("categoryChildren");
    }

    protected Category getCategory(NavigationModel model) {
        return model.getCategory();
    }

    protected String locateCategory(Category cat) {
        if (!(Web.getContext().getApplication() instanceof Navigation)) {
            return super.locateCategory(cat);
        }

        Category[] path = getModel().getCategoryPath();
        StringBuffer buf = new StringBuffer("/");
        boolean found = false;
        for (int i = 1 ; i < path.length ; i++) {
            if (path[i].getURL() == null) {
                return super.locateCategory(cat);
            }
            buf.append(path[i].getURL() + "/");
        }
        
        if (cat.getURL() == null) {
            return super.locateCategory(cat);
        } else {
            return URL.here(Web.getRequest(),
                            buf + cat.getURL() + "/").toString();
        }
    }
}
