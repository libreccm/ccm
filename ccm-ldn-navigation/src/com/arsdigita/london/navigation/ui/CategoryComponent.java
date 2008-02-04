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

package com.arsdigita.london.navigation.ui;

import com.arsdigita.london.navigation.Navigation;

import com.arsdigita.categorization.Category;

import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;

/**
 * Abstract class for rendering categories
 */
public abstract class CategoryComponent extends AbstractComponent {

    protected Element generateCategoryXML(HttpServletRequest request,
                                          HttpServletResponse response,
                                          Category category) {
        return generateCategoryXML(request,
                                   response,
                                   category.getID(),
                                   category.getName(),
                                   category.getDescription(),
                                   locateCategory(category));
    }

    protected Element generateCategoryXML(HttpServletRequest request,
                                          HttpServletResponse response,
                                          BigDecimal id,
                                          String title,
                                          String description,
                                          String url) {
        Element content = Navigation.newElement("category");
        content.addAttribute("id", XML.format(id));
        content.addAttribute("title", title);
        content.addAttribute("description", description);
        content.addAttribute("url", url);
        return content;
    }
    
    protected String locateCategory(Category cat) {
        return Navigation.redirectURL(cat.getOID());
    }
}
