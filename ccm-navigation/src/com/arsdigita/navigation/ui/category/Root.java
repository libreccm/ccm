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

import com.arsdigita.navigation.ui.CategoryComponent;

import com.arsdigita.categorization.Category;
import com.arsdigita.xml.Element;
import com.arsdigita.web.Web;
import com.arsdigita.web.URL;

import com.arsdigita.navigation.Navigation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Outputs the root navigation category
 */
public class Root extends CategoryComponent {

    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        Category root = getModel().getRootCategory();
        if (root == null) {
            return null;
        }
        
        Element content = Navigation.newElement("categoryRoot");
        exportAttributes(content);
        
        content.addContent(generateCategoryXML(request,
                                               response,
                                               root.getID(),
                                               root.getName(),
                                               root.getDescription(),
                                               URL.here(Web.getRequest(),
                                                        "/").toString()));
        return content;
    }
}
