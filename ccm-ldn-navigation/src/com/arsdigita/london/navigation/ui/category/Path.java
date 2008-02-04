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

package com.arsdigita.london.navigation.ui.category;

import com.arsdigita.london.navigation.ui.CategoryComponent;
import com.arsdigita.categorization.Category;
import com.arsdigita.xml.Element;
import com.arsdigita.web.Web;
import com.arsdigita.web.URL;

import com.arsdigita.london.navigation.Navigation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * CategoryPath component displays the current location in the category
 * hierarchy as a 'trail'.  
 */
public class Path extends CategoryComponent {  

    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        // obtain a list of the category ancestors
        // the collection is conveniently sorted, starting with the root 
        Category[] path = getModel().getCategoryPath();
        
        if (path == null) {
            return null;
        }

        Element content = Navigation.newElement("categoryPath");
        exportAttributes(content);
        
        for (int i = 0 ; i < path.length ; i++) {            
            content.addContent(generateCategoryXML(request,
                                                   response,
                                                   path[i]));
        }

        return content;
    }
    
    protected String locateCategory(Category cat) {
        if (!(Web.getContext().getApplication() instanceof Navigation)) {
            return super.locateCategory(cat);
        }

        Category[] path = getModel().getCategoryPath();
        StringBuffer buf = new StringBuffer("/");
        boolean found = false;
        if (cat.equals(getModel().getRootCategory())) {
            return URL.here(Web.getRequest(), "/").toString();
        }
        for (int i = 1 ; i < path.length ; i++) {
            if (path[i].getURL() == null) {
                break;
            }
            buf.append(path[i].getURL() + "/");
            if (path[i].equals(cat)) {
                found = true;
                break;
            }
        }
        if (found) {
            return URL.here(Web.getRequest(),
                            buf.toString()).toString();
        }
        return super.locateCategory(cat);
    }
        
}
