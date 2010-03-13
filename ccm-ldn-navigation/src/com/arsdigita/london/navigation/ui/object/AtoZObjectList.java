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


package com.arsdigita.london.navigation.ui.object;

import com.arsdigita.london.navigation.Navigation;
import com.arsdigita.london.navigation.ui.AbstractObjectList;

import com.arsdigita.persistence.FilterFactory;
import com.arsdigita.persistence.CompoundFilter;
import com.arsdigita.persistence.Filter;
import com.arsdigita.persistence.DataCollection;

import com.arsdigita.categorization.Category;

import com.arsdigita.kernel.ACSObject;

import com.arsdigita.xml.Element;
import com.arsdigita.xml.XML;

import com.arsdigita.util.Assert;

import com.arsdigita.web.URL;
import com.arsdigita.web.ParameterMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * A object list paginated by A-Z letters
 */
public class AtoZObjectList extends AbstractObjectList {
    
    private String m_titleProperty = ACSObject.DISPLAY_NAME;

    public void setTitleProperty(String property) {
        Assert.isLocked(this);
        m_titleProperty = property;
    }

    protected DataCollection getObjects(HttpServletRequest request,
                                        HttpServletResponse response) {
        DataCollection objects = super.getObjects(request, response);
        
        String letter = getLetter(request);
        
        if (letter != null) {
            FilterFactory fact = objects.getFilterFactory();
            CompoundFilter or  = fact.or();
            
            Filter lower = fact.startsWith(m_titleProperty,
                                           letter.toLowerCase(), false);
            Filter upper = fact.startsWith(m_titleProperty,
                                           letter.toUpperCase(), false);
            or.addFilter(lower);
            or.addFilter(upper);
            
            objects.addFilter(or);
        }

        return objects;
    }

    protected String getLetter(HttpServletRequest request) {
        String letter = request.getParameter("letter");
        
        return letter;
    }

    public Element generateXML(HttpServletRequest request,
                               HttpServletResponse response) {
        Element content = Navigation.newElement("atozObjectList");

        Category cat = getCategory();

        String currentLetter = getLetter(request);
        Element lettersEl = Navigation.newElement("letters");
        
        char l = 'A';
        while ( l <= 'Z' ) {
            final String letter = String.valueOf(l++);
            ParameterMap map = new ParameterMap();
            map.setParameter("categoryID", cat.getID().toString());
            map.setParameter("letter", letter);
            
            Element letterEl = Navigation.newElement("letter");
            letterEl.addAttribute("letter", letter);
            letterEl.addAttribute("url", 
                                  XML.format(URL.request(request, map)));
            letterEl.addAttribute("selected", 
                                  (letter.equals(currentLetter) ? "1" : "0"));
            lettersEl.addContent(letterEl);
        }
        content.addContent(lettersEl);
        
        // Generate 'any' letter
        ParameterMap map = new ParameterMap();
        map.setParameter("categoryID", cat.getID().toString());
        Element letterEl = Navigation.newElement("letter");
        letterEl.addAttribute("letter", "any");
        letterEl.addAttribute("url", 
                              XML.format(URL.request(request, map)));
        letterEl.addAttribute("selected", (currentLetter == null ? "1" : "0"));
        lettersEl.addContent(letterEl);

        content.addContent(generateObjectListXML(request,
                                                 response));

        return content;
    }
}
