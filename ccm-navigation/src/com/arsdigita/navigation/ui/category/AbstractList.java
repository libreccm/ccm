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
import com.arsdigita.categorization.CategoryCollection;
import com.arsdigita.xml.Element;

import com.arsdigita.navigation.Navigation;
import com.arsdigita.navigation.NavigationModel;
import com.arsdigita.navigation.ui.CategoryComponent;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


/**
 * Abstract class for displaying a list of categories
 */

public abstract class AbstractList extends CategoryComponent {
    private static final Logger s_log = Logger.getLogger( AbstractList.class );

    private String m_name;

    protected AbstractList(String name) {
        m_name = name;
    }

    protected abstract Category getCategory(NavigationModel model);

    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        Category cat = getCategory(getModel());

        if( s_log.isDebugEnabled() ) {
            String catStr = (null == cat) ? "null" : cat.getOID().toString();
            s_log.debug( getClass().getName() + " got category: " + catStr );
                         
        }
        
        if (cat == null) {
            return null;
        }

        Element content = Navigation.newElement(m_name);
        exportAttributes(content);

        // obtain a list of siblings of the current category
        CategoryCollection children = cat.getChildren();
        children.addOrder("link." + Category.SORT_KEY);
        
        while (children.next()) {
            Category subcat = (Category)children.getACSObject();
            if (!subcat.isEnabled()) {
                continue;
            }
            content.addContent(generateCategoryXML(
                                   request,
                                   response,
                                   subcat));
        }
        
        return content;
    }

    protected Element generateCategoryXML(HttpServletRequest request,
                                          HttpServletResponse response,
                                          BigDecimal id,
                                          String title,
                                          String description,
                                          String url) {
        Element e = super.generateCategoryXML( request, response, id, title,
                                               description, url );

        Category cat = getModel().getCategory();
        if( null != cat && cat.getID().equals( id ) ) {
            e.addAttribute( "isSelected", "true" );
        }

        return e;
    }
}
